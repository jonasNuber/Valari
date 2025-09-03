package io.github.jonasnuber.valari.api.results;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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

    public String getDefaultMessage() {
        return defaultMessage;
    }

    public String getMessageKey() {
        return messageKey;
    }

    public List<Object> getMessageArguments() {
        return messageArguments;
    }

    public LabelType getLabelType() {
        return labelType;
    }

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

    public static class Builder  {
        private final String defaultMessage;
        private final List<Object> messageArguments = new ArrayList<>();

        private String messageKey = "not.provided";
        private LabelType labelType = LabelType.SUBJECT;
        private String label = "<unknown>";

        public Builder (String defaultMessage) {
            this.defaultMessage = Objects.requireNonNull(defaultMessage, "DefaultMessage must not be null");
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

        public ValidationMetadata build() {
            return new ValidationMetadata(this);
        }
    }
}
