package io.github.jonasnuber.valari.api.results;

import java.util.Objects;

public final class ValidationDescriptor {
    private final Class<?> validationClass;
    private final LabelType labelType;
    private final String label;

    private ValidationDescriptor(Builder builder) {
        this.validationClass = builder.validationClass;
        this.labelType = builder.labelType;
        this.label = builder.label;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Class<?> getValidationClass() {
        return validationClass;
    }

    public LabelType getLabelType() {
        return labelType;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ValidationDescriptor that = (ValidationDescriptor) o;
        return Objects.equals(validationClass, that.validationClass) && Objects.equals(labelType, that.labelType) && Objects.equals(label, that.label);
    }

    @Override
    public int hashCode() {
        return Objects.hash(validationClass, labelType, label);
    }

    @Override
    public String toString() {
        return "ValidationDescriptor{" +
                "validationClass=" + validationClass +
                ", labelType=" + labelType +
                ", label='" + label + '\'' +
                '}';
    }

    public static final class Builder {
        private Class<?> validationClass = Object.class;
        private LabelType labelType = LabelType.of("UnknownType");
        private String label = "<unknown>";

        private Builder() {}

        public Builder validationClass(Class<?> validationClass) {
            this.validationClass = Objects.requireNonNull(validationClass, "ValidationClass must not be null")  ;
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

        public ValidationDescriptor build() {
            return new ValidationDescriptor(this);
        }
    }
}
