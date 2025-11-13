package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.api.i18n.ResultFormatter;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents the outcome of a single validation operation.
 *
 * <p>A {@code ValidationResult} encapsulates both the logical state of the validation (success,
 * failure, or skipped) and the metadata necessary to produce human-readable or localized validation
 * messages. It acts as the standard result container for all {@link Validation} executions within
 * the Valari framework.
 *
 * <h2>Message resolution</h2>
 *
 * Messages can be resolved via the configured {@link MessageResolver} and {@link Locale}. For
 * convenience, the {@link #resolveValidationMessage()} method uses the global defaults from {@link
 * MessageResolutionContext}.
 *
 * <p>The higher-level {@link #getDetailedMessage(MessageResolver, Locale)} method wraps the raw
 * validation message into a standardized result message such as:
 *
 * <ul>
 *   <li>"The field 'username' is valid: must not be null"
 *   <li>"Validation for field 'email' was skipped"
 *   <li>"The attribute 'age' is invalid: must be greater than 0"
 * </ul>
 *
 * <h2>Construction</h2>
 *
 * Instances are created using the nested {@link Builder} class, which ensures consistency of state
 * and metadata.
 *
 * @author Jonas Nuber
 */
public final class ValidationResult implements ThrowableResult<ValidationResult> {
  private final ValidationMetadata metadata;
  private final Object value;
  private final ValidationState state;

  private ValidationResult(Builder builder) {
    metadata =
        new ValidationMetadata.Builder(builder.defaultMessage)
            .messageKey(builder.messageKey)
            .messageArguments(builder.messageArguments.toArray())
            .labelType(builder.labelType)
            .label(builder.label)
            .build();
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
  @Override
  public ValidationResult withLabel(LabelType labelType, String label) {
    return new Builder(this).labelType(labelType).label(label).build();
  }

  /**
   * Creates a new result representing a skipped validation.
   *
   * <p>Skipped results indicate that the validation was intentionally not executed (e.g. due to a
   * precondition).
   *
   * @return a {@code ValidationResult} with state {@link ValidationState#SKIPPED}.
   */
  public static ValidationResult skip() {
    return new Builder("Validation was skipped").skip();
  }

  @Override
  public ValidationMetadata getMetadata() {
    return metadata;
  }

  /**
   * @return the default message to use if no localized message is found.
   */
  public String getDefaultMessage() {
    return metadata.getDefaultMessage();
  }

  /**
   * @return the message key for localization.
   */
  public String getMessageKey() {
    return metadata.getMessageKey();
  }

  /**
   * @return the arguments used for message formatting.
   */
  public List<Object> getMessageArguments() {
    return metadata.getMessageArguments();
  }

  /**
   * @return the type of label describing the validated subject.
   */
  public LabelType getLabelType() {
    return metadata.getLabelType();
  }

  /**
   * @return the label (e.g. field name or object name).
   */
  public String getLabel() {
    return metadata.getLabel();
  }

  /**
   * @return the value that was validated, or {@code null} if not set.
   */
  public Object getValue() {
    return value;
  }

  /**
   * @return the current validation state.
   */
  @Override
  public ValidationState getState() {
    return state;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ValidationResult that = (ValidationResult) o;
    return Objects.equals(metadata, that.metadata)
        && Objects.equals(value, that.value)
        && state == that.state;
  }

  @Override
  public int hashCode() {
    return Objects.hash(metadata, value, state);
  }

  @Override
  public String toString() {
    return "ValidationResult{"
        + "metadata="
        + metadata
        + ", value="
        + value
        + ", state="
        + state
        + '}';
  }

  /**
   * Builder for constructing {@link ValidationResult} instances.
   *
   * <p>The builder collects message metadata, labeling information, and an optional value, then
   * finalizes the result into one of the supported states: {@link #ok()}, {@link #fail()}, or
   * {@link #skip()}.
   *
   * <h2>Usage example</h2>
   *
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
      this.defaultMessage =
          Objects.requireNonNull(defaultMessage, "DefaultMessage must not be null");
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

      defaultMessage = existing.getDefaultMessage();
      messageArguments.addAll(existing.getMessageArguments());
      messageKey = existing.getMessageKey();
      labelType = existing.getLabelType();
      label = existing.getLabel();
      value = existing.getValue();
      state = existing.getState();
    }

    /**
     * Sets the message key used for localization.
     *
     * @param messageKey the message key.
     * @return this builder for chaining.
     */
    public Builder messageKey(String messageKey) {
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
      for (Object messageArgument : messageArguments) {
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
    public Builder value(Object value) {
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
