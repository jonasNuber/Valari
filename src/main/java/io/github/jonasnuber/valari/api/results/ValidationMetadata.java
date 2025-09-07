package io.github.jonasnuber.valari.api.results;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable container for metadata that describes the context of a validation.
 *
 * <p>
 * This class separates the <em>descriptive metadata</em> of a validation
 * from its <em>execution state</em> ({@code SUCCESS}, {@code FAILURE}, etc.).
 * Typically, {@code ValidationMetadata} is created first, then passed into
 * a {@link ValidationResult.Builder} to produce a result with a concrete
 * {@link io.github.jonasnuber.valari.api.results.ValidationState}.
 * </p>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * ValidationMetadata metadata = new ValidationMetadata.Builder("must not be null")
 *     .messageKey("validation.notnull")
 *     .labelType(LabelType.FIELD)
 *     .label("username")
 *     .build();
 *
 * ValidationResult result = new ValidationResult.Builder(metadata).fail();
 * }</pre>
 *
 * @author Jonas Nuber
 */
public class ValidationMetadata {
    private final String defaultMessage;
    private final String messageKey;
    private final List<Object> messageArguments;
    private final LabelType labelType;
    private final String label;

    private ValidationMetadata(Builder builder) {
        this.defaultMessage = builder.defaultMessage;
        this.messageKey = builder.messageKey;
        this.messageArguments = builder.messageArguments;
        this.labelType = builder.labelType;
        this.label = builder.label;
    }

    /** @return the default human-readable validation message */
    public String getDefaultMessage() {
        return defaultMessage;
    }

    /** @return the message key for internationalization / resolution */
    public String getMessageKey() {
        return messageKey;
    }

    /** @return the arguments to use when formatting the message */
    public List<Object> getMessageArguments() {
        return messageArguments;
    }

    /** @return the semantic type of the label (e.g., field, parameter, etc.) */
    public LabelType getLabelType() {
        return labelType;
    }

    /** @return the name or identifier of the validated element */
    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return "ValidationMetadata{" +
                "defaultMessage='" + defaultMessage + '\'' +
                ", messageKey='" + messageKey + '\'' +
                ", messageArguments=" + messageArguments +
                ", labelType=" + labelType +
                ", label='" + label + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ValidationMetadata that = (ValidationMetadata) o;
        return Objects.equals(defaultMessage, that.defaultMessage) && Objects.equals(messageKey, that.messageKey) && Objects.equals(messageArguments, that.messageArguments) && Objects.equals(labelType, that.labelType) && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultMessage, messageKey, messageArguments, labelType, label);
    }

    /**
     * Builder for {@link ValidationMetadata}.
     * <p>
     * Enforces that {@code defaultMessage} is always provided.
     * Provides fluent methods for setting optional attributes.
     * </p>
     */
    public static class Builder  {
        private final String defaultMessage;
        private final List<Object> messageArguments = new ArrayList<>();

        private String messageKey = "not.provided";
        private LabelType labelType = LabelType.SUBJECT;
        private String label = "<unknown>";

        /**
         * Creates a new builder with the given default message.
         *
         * @param defaultMessage the default human-readable message,
         *                       must not be {@code null}
         */
        public Builder (String defaultMessage) {
            this.defaultMessage = Objects.requireNonNull(defaultMessage, "DefaultMessage must not be null");
        }

        /**
         * Sets the message key for this validation.
         *
         * @param messageKey the message key, must not be {@code null}
         * @return this builder
         */
        public Builder messageKey(String messageKey){
            this.messageKey = Objects.requireNonNull(messageKey, "MessageKey must not be null");
            return this;
        }

        /**
         * Adds a single message argument.
         *
         * @param messageArgument an argument to insert into the formatted message
         * @return this builder
         */
        public Builder messageArgument(Object messageArgument) {
            Objects.requireNonNull(messageArgument, "MessageArgument must not be null");
            messageArguments.add(messageArgument);
            return this;
        }

        /**
         * Adds multiple message arguments.
         *
         * @param messageArguments the arguments to add
         * @return this builder
         */
        public Builder messageArguments(Object... messageArguments) {
            for(Object messageArgument : messageArguments) {
                messageArgument(messageArgument);
            }

            return this;
        }

        /**
         * Sets the label type describing what is validated.
         *
         * @param labelType the label type, must not be {@code null}
         * @return this builder
         */
        public Builder labelType(LabelType labelType) {
            this.labelType = Objects.requireNonNull(labelType, "LabelType must not be null");

            return this;
        }

        /**
         * Sets the label (e.g., field or parameter name).
         *
         * @param label the label value, must not be {@code null}
         * @return this builder
         */
        public Builder label(String label) {
            this.label = Objects.requireNonNull(label, "Label must not be null");

            return this;
        }

        /**
         * Builds an immutable {@link ValidationMetadata} instance.
         *
         * @return a new {@code ValidationMetadata}
         */
        public ValidationMetadata build() {
            return new ValidationMetadata(this);
        }
    }
}
