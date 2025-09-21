package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import io.github.jonasnuber.valari.spi.MessageResolver;
import io.github.jonasnuber.valari.spi.ThrowingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents the outcome of a single validation operation.
 * <p>
 * A {@code ValidationResult} encapsulates both the logical state of the validation
 * (success, failure, or skipped) and the metadata necessary to produce
 * human-readable or localized validation messages. It acts as the standard
 * result container for all {@link io.github.jonasnuber.valari.spi.Validation}
 * executions within the Valari framework.
 * </p>
 *
 * <h2>Message resolution</h2>
 * Messages can be resolved via the configured {@link MessageResolver} and {@link Locale}.
 * For convenience, the {@link #resolveValidationMessage()} method uses the global defaults
 * from {@link MessageResolutionContext}.
 * <p>
 * The higher-level {@link #getDetailedMessage(MessageResolver, Locale)} method wraps the
 * raw validation message into a standardized result message such as:
 * <ul>
 *   <li>"The field 'username' is valid: must not be null"</li>
 *   <li>"Validation for field 'email' was skipped"</li>
 *   <li>"The attribute 'age' is invalid: must be greater than 0"</li>
 * </ul>
 *
 * <h2>Construction</h2>
 * Instances are created using the nested {@link Builder} class, which ensures
 * consistency of state and metadata.
 *
 * @author Jonas Nuber
 */
public final class ValidationResult implements ThrowingResult {
    private final String defaultMessage;
    private final String messageKey;
    private final List<Object> messageArguments;
    private final LabelType labelType;
    private final String label;
    private final Object value;
    private final ValidationState state;

    protected ValidationResult(Builder builder) {
        this.defaultMessage = builder.defaultMessage;
        this.messageKey = builder.messageKey;
        this.messageArguments = builder.messageArguments;
        this.labelType = builder.labelType;
        this.label = builder.label;
        this.value = builder.value;
        this.state = builder.state;
    }

    /**
     * Returns a copy of this result with an updated label and label type.
     *
     * @param labelType the new label type describing the subject (e.g. FIELD, ATTRIBUTE).
     * @param label the descriptive label (e.g. field name or object name).
     * @return a new {@code ValidationResult} with updated label information.
     */
    public ValidationResult withLabel(LabelType labelType, String label) {
        return new Builder(this)
                .labelType(labelType)
                .label(label)
                .build();
    }

    /**
     * Creates a new result representing a skipped validation.
     * <p>
     * Skipped results indicate that the validation was intentionally not executed
     * (e.g. due to a precondition).
     * </p>
     *
     * @return a {@code ValidationResult} with state {@link ValidationState#SKIPPED}.
     */
    public static ValidationResult skip() {
        return new Builder("Validation was skipped").skip();
    }

    /**
     * Resolves the validation-specific message (not the full result message).
     * <p>
     * This uses the global defaults from {@link MessageResolutionContext}.
     * </p>
     *
     * @return the resolved validation message.
     */
    public String resolveValidationMessage() {
        return resolveValidationMessage(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }

    /**
     * Resolves the validation-specific message (not the full result message).
     *
     * @param resolver the message resolver to use.
     * @param locale the locale for which to resolve the message.
     * @return the resolved validation message.
     */
    public String resolveValidationMessage(MessageResolver resolver, Locale locale) {
        return resolver.resolve(messageKey, messageArguments, defaultMessage, locale);
    }

    /**
     * Resolves a human-readable message describing this result,
     * including the validation state, label, and resolved message.
     * <p>
     * Example outputs:
     * <ul>
     *   <li>"The field 'username' is valid: must not be null"</li>
     *   <li>"Validation for field 'email' was skipped"</li>
     *   <li>"The attribute 'age' is invalid: must be greater than 0"</li>
     * </ul>
     * </p>
     *
     * @param resolver the message resolver to use.
     * @param locale the locale for message resolution.
     * @return a fully formatted result message.
     */
    @Override
    public String getDetailedMessage(MessageResolver resolver, Locale locale) {
        return switch (state) {
            case SUCCESS -> resolver.resolve(
                    "validation.result.success",
                    List.of(labelType, label, resolveValidationMessage(resolver, locale)),
                    "The {0} \"{1}\" is valid: {2}",
                    locale
            );
            case SKIPPED -> resolver.resolve(
                    "validation.result.skipped",
                    List.of(labelType, label),
                    "Validation for {0} \"{1}\" was skipped",
                    locale
            );
            case FAILURE -> resolver.resolve(
                    "validation.result.failure",
                    List.of(labelType, label, resolveValidationMessage(resolver, locale)),
                    "The {0} \"{1}\" is invalid: {2}",
                    locale
            );
        };
    }

    @Override
    public String getMessage(MessageResolver resolver, Locale locale) {
        return resolver.resolve(
                "validation.result.aggregated.field",
                List.of(labelType, label, resolveValidationMessage()),
                "{0} \"{1}\": {2}",
                locale
        );
    }

    /**
     * Throws an {@link InvalidAttributeValueException} if this result indicates failure.
     * <p>
     * This method is a shortcut for applications that prefer exceptions to
     * explicit result handling.
     * </p>
     *
     * @throws InvalidAttributeValueException if the state is {@link ValidationState#FAILURE}.
     */
    @Override
    public void throwIfInvalid() {
        throwIfInvalid(InvalidAttributeValueException::new);
    }

    /** @return the default message to use if no localized message is found. */
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /** @return the message key for localization. */
    public String getMessageKey() {
        return messageKey;
    }

    /** @return the arguments used for message formatting. */
    public List<Object> getMessageArguments(){
        return messageArguments;
    }

    /** @return the type of label describing the validated subject. */
    public LabelType getLabelType() {
        return labelType;
    }

    /** @return the label (e.g. field name or object name). */
    public String getLabel(){
        return label;
    }

    /** @return the value that was validated, or {@code null} if not set. */
    public Object getValue() {
        return value;
    }

    /** @return the current validation state. */
    @Override
    public ValidationState getState() {
        return state;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ValidationResult that = (ValidationResult) o;
        return Objects.equals(defaultMessage, that.defaultMessage) && Objects.equals(messageKey, that.messageKey) && Objects.equals(messageArguments, that.messageArguments) && Objects.equals(labelType, that.labelType) && Objects.equals(label, that.label) && Objects.equals(value, that.value) && state == that.state;
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultMessage, messageKey, messageArguments, labelType, label, value, state);
    }

    @Override
    public String toString() {
        return "ValidationResult{" +
                "defaultMessage='" + defaultMessage + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", messageArguments=" + messageArguments +
                ", labelType=" + labelType +
                ", label='" + label + '\'' +
                ", value=" + value +
                ", state=" + state +
                '}';
    }

    /**
     * Builder for constructing {@link ValidationResult} instances.
     * <p>
     * The builder collects message metadata, labeling information, and
     * an optional value, then finalizes the result into one of the
     * supported states: {@link #ok()}, {@link #fail()}, or {@link #skip()}.
     * </p>
     *
     * <h2>Usage example</h2>
     * <pre>{@code
     * ValidationResult result = new ValidationResult.Builder("must not be null")
     *     .messageKey("validation.object.notNull")
     *     .labelType(LabelType.FIELD)
     *     .label("username")
     *     .value(user.getUsername())
     *     .fail();
     * }</pre>
     */
    public static class Builder {
        private final String defaultMessage;
        private final List<Object> messageArguments = new ArrayList<>();

        private String messageKey = "not.provided";
        private LabelType labelType = LabelType.SUBJECT;
        private String label = "<unknown>";
        private Object value;
        private ValidationState state;

        /**
         * Creates a builder with the given default message.
         *
         * @param defaultMessage the default message to use if no localized message is available.
         */
        public Builder(String defaultMessage) {
            this.defaultMessage = Objects.requireNonNull(defaultMessage, "DefaultMessage must not be null");
        }

        /**
         * Initializes a builder from validation metadata.
         *
         * @param metadata metadata containing message and label information.
         */
        public Builder(ValidationMetadata metadata) {
            defaultMessage = metadata.getDefaultMessage();
            messageKey = metadata.getMessageKey();
            messageArguments.addAll(metadata.getMessageArguments());
            labelType = metadata.getLabelType();
            label = metadata.getLabel();
        }

        private Builder(ValidationResult existing) {
            Objects.requireNonNull(existing, "Validation Result to extend cannot be null");

            defaultMessage = existing.defaultMessage;
            messageArguments.addAll(existing.messageArguments);
            messageKey = existing.messageKey;
            labelType = existing.labelType;
            label = existing.label;
            value = existing.value;
            state = existing.state;
        }

        /**
         * Sets the message key used for localization.
         *
         * @param messageKey the message key.
         * @return this builder for chaining.
         */
        public Builder messageKey(String messageKey){
            this.messageKey = Objects.requireNonNull(messageKey, "MessageKey must not be null");
            return this;
        }

        /**
         * Adds a single argument to be used when formatting the message.
         *
         * @param messageArgument the argument to add.
         * @return this builder for chaining.
         */
        public Builder messageArgument(Object messageArgument) {
            Objects.requireNonNull(messageArgument, "MessageArgument must not be null");
            messageArguments.add(messageArgument);
            return this;
        }

        /**
         * Adds multiple arguments to be used when formatting the message.
         *
         * @param messageArguments the arguments to add.
         * @return this builder for chaining.
         */
        public Builder messageArguments(Object... messageArguments) {
            for(Object messageArgument : messageArguments) {
                messageArgument(messageArgument);
            }

            return this;
        }

        /**
         * Sets the type of label describing the validated subject.
         *
         * @param labelType the label type.
         * @return this builder for chaining.
         */
        public Builder labelType(LabelType labelType) {
            this.labelType = Objects.requireNonNull(labelType, "LabelType must not be null");

            return this;
        }

        /**
         * Sets the descriptive label of the validated subject.
         *
         * @param label the label (e.g. field name).
         * @return this builder for chaining.
         */
        public Builder label(String label) {
            this.label = Objects.requireNonNull(label, "Label must not be null");
            return this;
        }

        /**
         * Sets the value that was validated.
         *
         * @param value the validated value.
         * @return this builder for chaining.
         */
        public Builder value(Object value){
            this.value = value;
            return this;
        }

        /**
         * Finalizes the result as a successful validation.
         *
         * @return a {@code ValidationResult} with state {@link ValidationState#SUCCESS}.
         */
        public ValidationResult ok() {
            state = ValidationState.SUCCESS;

            return new ValidationResult(this);
        }

        /**
         * Finalizes the result as a failed validation.
         *
         * @return a {@code ValidationResult} with state {@link ValidationState#FAILURE}.
         */
        public ValidationResult fail() {
            state = ValidationState.FAILURE;

            return new ValidationResult(this);
        }

        /**
         * Finalizes the result as a skipped validation.
         *
         * @return a {@code ValidationResult} with state {@link ValidationState#SKIPPED}.
         */
        public ValidationResult skip() {
            state = ValidationState.SKIPPED;

            return new ValidationResult(this);
        }

        private ValidationResult build() {
            return new ValidationResult(this);
        }
    }
}
