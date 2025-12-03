package io.github.jonasnuber.valari.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Immutable container for metadata describing the context of a validation.
 *
 * <p>{@code ValidationMetadata} separates the <em>descriptive metadata</em> of a validation from
 * its <em>execution state</em> ({@link ValidationState}). Metadata includes messages, keys,
 * arguments, labels, and the class being validated. Typically, it is created first and then passed
 * into a {@link Result} to produce a concrete validation outcome.
 *
 * <h2>Example</h2>
 *
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
 * <h2>Internationalization</h2>
 *
 * <p>The {@code messageKey} should correspond to a key resolvable by the configured {@link
 * io.github.jonasnuber.valari.api.i18n.MessageResolver}. If the key cannot be resolved, a
 * human-readable fallback message (the {@code defaultMessage}) will be used.
 *
 * @author Jonas Nuber
 * @see Result
 * @see ValidationState
 * @see LabelType
 */
public final class ValidationMetadata {
  private final String defaultMessage;
  private final String messageKey;
  private final List<Object> messageArguments;
  private final ValidationDescriptor validationDescriptor;

  private ValidationMetadata(Builder builder) {
    this.defaultMessage = builder.defaultMessage;
    this.messageKey = builder.messageKey;
    this.messageArguments = builder.messageArguments;
    validationDescriptor =
        ValidationDescriptor.builder()
            .validationClass(builder.validationClass)
            .labelType(builder.labelType)
            .label(builder.label)
            .build();
  }

  /**
   * Returns a new builder for creating a {@link ValidationDescriptor}.
   *
   * @return a builder instance
   */
  public static Builder builder(String defaultMessage) {
    return new Builder(defaultMessage);
  }

  /**
   * @return the default human-readable validation message
   */
  public String getDefaultMessage() {
    return defaultMessage;
  }

  /**
   * @return the message key for internationalization / resolution
   */
  public String getMessageKey() {
    return messageKey;
  }

  /**
   * @return the arguments to use when formatting the message
   */
  public List<Object> getMessageArguments() {
    return messageArguments;
  }

  /**
   * @return the descriptor containing label and validation class information
   */
  public ValidationDescriptor getValidationDescriptor() {
    return validationDescriptor;
  }

  /**
   * @return the class being validated
   */
  public Class<?> getValidationClass() {
    return validationDescriptor.getValidationClass();
  }

  /**
   * @return the semantic type of the label (e.g., field, parameter, etc.)
   */
  public LabelType getLabelType() {
    return validationDescriptor.getLabelType();
  }

  /**
   * @return the name or identifier of the validated element
   */
  public String getLabel() {
    return validationDescriptor.getLabel();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ValidationMetadata that = (ValidationMetadata) o;
    return Objects.equals(defaultMessage, that.defaultMessage)
        && Objects.equals(messageKey, that.messageKey)
        && Objects.equals(messageArguments, that.messageArguments)
        && Objects.equals(validationDescriptor, that.validationDescriptor);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        defaultMessage,
        messageKey,
        messageArguments,
        validationDescriptor.getValidationClass(),
        validationDescriptor.getLabelType(),
        validationDescriptor.getLabel());
  }

  @Override
  public String toString() {
    return "ValidationMetadata{"
        + "defaultMessage='"
        + defaultMessage
        + '\''
        + ", messageKey='"
        + messageKey
        + '\''
        + ", messageArguments="
        + messageArguments
        + ", validationClass="
        + validationDescriptor.getValidationClass()
        + ", labelType="
        + validationDescriptor.getLabelType()
        + ", label='"
        + validationDescriptor.getLabel()
        + '\''
        + '}';
  }

  /**
   * Builder for {@link ValidationMetadata}.
   *
   * <p>Enforces that a {@code defaultMessage} is always provided. Supports optional attributes such
   * as message keys, arguments, label types, and labels. Defaults are: * *
   *
   * <ul>
   *   <li>{@code messageKey} → "not.provided" *
   *   <li>{@code validationClass} → {@link Object} *
   *   <li>{@code labelType} → {@link LabelType#SUBJECT} *
   *   <li>{@code label} → "&lt;unknown&gt;" *
   * </ul>
   */
  public static class Builder {
    private final String defaultMessage;
    private final List<Object> messageArguments = new ArrayList<>();

    private String messageKey = "not.provided";
    private Class<?> validationClass = Object.class;
    private LabelType labelType = LabelType.SUBJECT;
    private String label = "<unknown>";

    /**
     * Creates a new builder with the required default message.
     *
     * @param defaultMessage the default human-readable message (must not be {@code null})
     */
    private Builder(String defaultMessage) {
      this.defaultMessage =
          Objects.requireNonNull(defaultMessage, "DefaultMessage must not be null");
    }

    /**
     * Sets the message key for internationalization.
     *
     * @param messageKey the message key (must not be {@code null})
     * @return this builder
     */
    public Builder messageKey(String messageKey) {
      this.messageKey = Objects.requireNonNull(messageKey, "MessageKey must not be null");
      return this;
    }

    /**
     * Adds a single argument to the message formatting.
     *
     * @param messageArgument argument to insert into the formatted message
     * @return this builder
     */
    public Builder messageArgument(Object messageArgument) {
      Objects.requireNonNull(messageArgument, "MessageArgument must not be null");
      messageArguments.add(messageArgument);
      return this;
    }

    /**
     * Adds multiple arguments for message formatting.
     *
     * @param messageArguments the arguments to add
     * @return this builder
     */
    public Builder messageArguments(Object... messageArguments) {
      for (Object messageArgument : messageArguments) {
        messageArgument(messageArgument);
      }

      return this;
    }

    /**
     * Sets the class being validated.
     *
     * @param validationClass the class which is validated, must not be {@code null}
     * @return this builder
     */
    public Builder validationClass(Class<?> validationClass) {
      this.validationClass =
          Objects.requireNonNull(validationClass, "Class to validate may not be null");

      return this;
    }

    /**
     * Sets the label type describing the target of validation.
     *
     * @param labelType the label type (must not be {@code null})
     * @return this builder
     */
    public Builder labelType(LabelType labelType) {
      this.labelType = Objects.requireNonNull(labelType, "LabelType must not be null");

      return this;
    }

    /**
     * Sets the label identifying the target of validation.
     *
     * @param label the label value (must not be {@code null})
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
