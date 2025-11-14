package io.github.jonasnuber.valari.api;

import java.util.Objects;

/**
 * Immutable descriptor containing metadata about a validation target.
 *
 * <p>{@code ValidationDescriptor} encapsulates information about the element being validated, such
 * as its Java class, semantic label type, and human-readable label. This descriptor is typically
 * used internally by {@link ValidationMetadata} and {@link Result} to provide context for
 * validation results and messages.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * ValidationDescriptor descriptor = ValidationDescriptor.builder()
 *     .validationClass(User.class)
 *     .labelType(LabelType.FIELD)
 *     .label("username")
 *     .build();
 * }</pre>
 *
 * @author Jonas Nuber
 * @see ValidationMetadata
 * @see LabelType
 */
public final class ValidationDescriptor {
  private final Class<?> validationClass;
  private final LabelType labelType;
  private final String label;

  private ValidationDescriptor(Builder builder) {
    this.validationClass = builder.validationClass;
    this.labelType = builder.labelType;
    this.label = builder.label;
  }

  /**
   * Returns a new builder for creating a {@link ValidationDescriptor}.
   *
   * @return a builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * @return the class of the element being validated
   */
  public Class<?> getValidationClass() {
    return validationClass;
  }

  /**
   * @return the semantic label type (e.g., field, parameter, attribute)
   */
  public LabelType getLabelType() {
    return labelType;
  }

  /**
   * @return the human-readable label identifying the validated element
   */
  public String getLabel() {
    return label;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ValidationDescriptor that = (ValidationDescriptor) o;
    return Objects.equals(validationClass, that.validationClass)
        && Objects.equals(labelType, that.labelType)
        && Objects.equals(label, that.label);
  }

  @Override
  public int hashCode() {
    return Objects.hash(validationClass, labelType, label);
  }

  @Override
  public String toString() {
    return "ValidationDescriptor{"
        + "validationClass="
        + validationClass
        + ", labelType="
        + labelType
        + ", label='"
        + label
        + '\''
        + '}';
  }

  /**
   * Builder for {@link ValidationDescriptor}.
   *
   * <p>Provides fluent methods for setting the class, label type, and label of a validation target.
   * Defaults are:
   *
   * <ul>
   *   <li>{@code validationClass} → {@link Object}
   *   <li>{@code labelType} → {@link LabelType#SUBJECT}
   *   <li>{@code label} → "&lt;unknown&gt;"
   * </ul>
   */
  public static final class Builder {
    private Class<?> validationClass = Object.class;
    private LabelType labelType = LabelType.SUBJECT;
    private String label = "<unknown>";

    private Builder() {}

    /**
     * Sets the class of the element being validated.
     *
     * @param validationClass the class to validate (must not be {@code null})
     * @return this builder
     */
    public Builder validationClass(Class<?> validationClass) {
      this.validationClass =
          Objects.requireNonNull(validationClass, "Class to validate may not be null");
      return this;
    }

    /**
     * Sets the semantic label type for the element.
     *
     * @param labelType the label type (must not be {@code null})
     * @return this builder
     */
    public Builder labelType(LabelType labelType) {
      this.labelType = Objects.requireNonNull(labelType, "LabelType must not be null");
      return this;
    }

    /**
     * Sets the human-readable label identifying the element.
     *
     * @param label the label (must not be {@code null})
     * @return this builder
     */
    public Builder label(String label) {
      this.label = Objects.requireNonNull(label, "Label must not be null");
      return this;
    }

    /**
     * Builds the immutable {@link ValidationDescriptor}.
     *
     * @return a new descriptor instance
     */
    public ValidationDescriptor build() {
      return new ValidationDescriptor(this);
    }
  }
}
