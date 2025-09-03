package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import io.github.jonasnuber.valari.spi.MessageResolver;
import io.github.jonasnuber.valari.spi.ThrowingResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * The ValidationResult class represents the result of a validation process.
 * It can indicate whether the validation was successful (OK) or failed (FAIL), and can
 * initiate an exception throw in case of a failed validation.
 *
 * @author Jonas Nuber
 */
public class ValidationResult implements ThrowingResult {
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

    public ValidationResult withLabel(LabelType labelType, String label) {
        return new Builder(this)
                .labelType(labelType)
                .label(label)
                .build();
    }

    public static ValidationResult skip() {
        return new Builder("Validation was skipped").skip();
    }

    public String resolveValidationMessage() {
        return resolveValidationMessage(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }

    public String resolveValidationMessage(MessageResolver resolver, Locale locale) {
        return resolver.resolve(messageKey, messageArguments, defaultMessage, locale);
    }

    @Override
    public String getMessage(MessageResolver resolver, Locale locale) {
        return switch (state) {
            case SUCCESS -> resolver.resolve(
                    "validation.result.success",
                    List.of(label, resolveValidationMessage(resolver, locale)),
                    "The field \"{0}\" is valid: {1}",
                    locale
            );
            case SKIPPED -> resolver.resolve(
                    "validation.result.skipped",
                    List.of(label),
                    "Validation for field \"{0}\" was skipped",
                    locale
            );
            case FAILURE -> resolver.resolve(
                    "validation.result.failure",
                    List.of(label, resolveValidationMessage(resolver, locale)),
                    "The field \"{0}\" is invalid: {1}",
                    locale
            );
        };
    }

    @Override
    public void throwIfInvalid() {
        throwIfInvalid(InvalidAttributeValueException::new);
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public List<Object> getMessageArguments(){
        return messageArguments;
    }

    public LabelType getLabelType() {
        return labelType;
    }

    public String getLabel(){
        return label;
    }

    public Object getValue() {
        return value;
    }

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

    public static class Builder {
        private final String defaultMessage;
        private final List<Object> messageArguments = new ArrayList<>();

        private String messageKey = "not.provided";
        private LabelType labelType = LabelType.SUBJECT;
        private String label = "<unknown>";
        private Object value;
        private ValidationState state;

        public Builder(String defaultMessage) {
            this.defaultMessage = Objects.requireNonNull(defaultMessage, "DefaultMessage must not be null");
        }

        private Builder(ValidationResult existing) {
            Objects.requireNonNull(existing, "Validation Result to extend cannot be null");

            this.defaultMessage = existing.defaultMessage;
            this.messageArguments.addAll(existing.messageArguments);
            this.messageKey = existing.messageKey;
            this.labelType = existing.labelType;
            this.label = existing.label;
            this.value = existing.value;
            this.state = existing.state;
        }

        public Builder messageKey(String messageKey){
            this.messageKey = Objects.requireNonNull(messageKey, "MessageKey must not be null");
            return this;
        }

        public Builder messageArgument(Object messageArgument) {
            Objects.requireNonNull(messageArgument, "MessageArgument must not be null");
            messageArguments.add(messageArgument);
            return this;
        }

        public Builder messageArguments(Object... messageArguments) {
            for(Object messageArgument : messageArguments) {
                messageArgument(messageArgument);
            }

            return this;
        }

        public Builder labelType(LabelType labelType) {
            this.labelType = Objects.requireNonNull(labelType, "LabelType must not be null");

            return this;
        }

        public Builder label(String label) {
            this.label = Objects.requireNonNull(label, "Label must not be null");
            return this;
        }

        public Builder value(Object value){
            this.value = value;
            return this;
        }

        public Builder metadata(ValidationMetadata metadata) {
            return new Builder(metadata.getDefaultMessage())
                    .messageKey(metadata.getMessageKey())
                    .messageArguments(metadata.getMessageArguments())
                    .labelType(metadata.getLabelType())
                    .label(metadata.getLabel());
        }

        public ValidationResult ok() {
            state = ValidationState.SUCCESS;

            return new ValidationResult(this);
        }

        public ValidationResult fail() {
            state = ValidationState.FAILURE;

            return new ValidationResult(this);
        }

        public ValidationResult skip() {
            state = ValidationState.SKIPPED;

            return new ValidationResult(this);
        }

        private ValidationResult build() {
            return new ValidationResult(this);
        }
    }
}
