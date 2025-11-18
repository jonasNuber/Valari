package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.LabelType;
import io.github.jonasnuber.valari.api.Validation;
import io.github.jonasnuber.valari.api.Validator;
import java.util.Objects;

/**
 * A thin wrapper around a {@link Validation} that associates a contextual name and a {@link
 * LabelType} with the validated value.
 *
 * <p>This validator is useful when validating standalone values—such as method parameters or
 * individual fields—where an explicit name should be included in the resulting {@link
 * ValidationResult}. This improves diagnostic quality, especially when messages are displayed in
 * logs, UI feedback, or structured error responses.
 *
 * <h2>Optional values</h2>
 *
 * If a {@code ValueValidator} is created in optional mode (via {@link #optional(Validation)} or
 * {@link #optional(String, Validation)}), then:
 *
 * <ul>
 *   <li>If the input value is {@code null}, validation is skipped and considered successful.
 *   <li>The produced {@code ValidationResult} has state {@link
 *       io.github.jonasnuber.valari.api.ValidationState#SKIPPED SKIPPED}.
 *   <li>A label is still attached so that the skipped result can be traced.
 * </ul>
 *
 * <h2>Labeling</h2>
 *
 * Each produced {@code ValidationResult} receives:
 *
 * <ul>
 *   <li>a label of the value name specified when constructing the validator, and
 *   <li>a {@link LabelType} (default: {@link LabelType#VALUE})
 * </ul>
 *
 * <p>The label type can be overridden using {@link #withLabelType(LabelType)}.
 *
 * @param <TYPE> the type of the value being validated
 * @see Validator
 * @see Validation
 * @see ValidationResult
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public class ValueValidator<TYPE> implements Validator<TYPE, ValidationResult> {
  private final String valueName;
  private final Validation<TYPE> validation;
  private final boolean optional;

  private LabelType labelType = LabelType.VALUE;

  private ValueValidator(String valueName, Validation<TYPE> validation, boolean optional) {
    this.valueName = Objects.requireNonNull(valueName, "Value Name must not be null");
    this.validation = Objects.requireNonNull(validation, "Validation must not be null");
    this.optional = optional;
  }

  /**
   * Creates a {@code ValueValidator} using {@code "Value"} as the label name.
   *
   * @param validation the validation rule to wrap
   * @param <TYPE> the type of value being validated
   * @return a new {@code ValueValidator} instance
   */
  public static <TYPE> ValueValidator<TYPE> with(Validation<TYPE> validation) {
    return new ValueValidator<>("Value", validation, false);
  }

  /**
   * Creates a {@code ValueValidator} with a custom label name.
   *
   * @param validation the validation rule to wrap
   * @param valueName the descriptive name used in result labels
   * @param <TYPE> the type of the value being validated
   * @return a new {@code ValueValidator} instance
   */
  public static <TYPE> ValueValidator<TYPE> with(String valueName, Validation<TYPE> validation) {
    return new ValueValidator<>(valueName, validation, false);
  }

  /**
   * Creates an optional {@code ValueValidator} using {@code "Value"} as the label name.
   *
   * <p>If the value being validated is {@code null}, the validation is skipped and considered
   * successful.
   *
   * @param validation the validation rule to wrap
   * @param <TYPE> the type of value being validated
   * @return a new optional {@code ValueValidator}
   */
  public static <TYPE> ValueValidator<TYPE> optional(Validation<TYPE> validation) {
    return new ValueValidator<>("Value", validation, true);
  }

  /**
   * Creates an optional {@code ValueValidator} using the given label name.
   *
   * <p>If the validated value is {@code null}, the validation is skipped and considered successful.
   *
   * @param validation the validation rule to wrap
   * @param valueName the descriptive name used in result labels
   * @param <TYPE> the type of the value being validated
   * @return a new optional {@code ValueValidator}
   */
  public static <TYPE> ValueValidator<TYPE> optional(
      String valueName, Validation<TYPE> validation) {
    return new ValueValidator<>(valueName, validation, true);
  }

  /**
   * Overrides the default {@link LabelType} ({@link LabelType#VALUE}) used when labeling produced
   * results.
   *
   * <p>This allows consumers to distinguish between values, fields, attributes, parameters, or
   * other contextual kinds of input.
   *
   * @param labelType the label type to apply to produced results
   * @return this validator instance for fluent chaining
   */
  public ValueValidator<TYPE> withLabelType(LabelType labelType) {
    this.labelType = Objects.requireNonNull(labelType, "LabelType must not be null");

    return this;
  }

  /**
   * Validates the given value and returns the resulting {@link ValidationResult}, enriched with the
   * configured label name and label type.
   *
   * <p>If this validator is configured as optional and {@code toValidate} is {@code null}, the
   * validation is skipped and the returned result has state {@link
   * io.github.jonasnuber.valari.api.ValidationState#SKIPPED SKIPPED}.
   *
   * <p>If this validator is not optional and the value is {@code null}, a {@link
   * NullPointerException} is thrown, mirroring the expectation that non-optional inputs must be
   * explicitly provided.
   *
   * @param toValidate the value to validate
   * @return a labeled {@code ValidationResult}
   * @throws NullPointerException if {@code toValidate} is {@code null} and this validator is not
   *     optional
   */
  @Override
  public ValidationResult validate(TYPE toValidate) {
    if (optional && Objects.isNull(toValidate)) {
      return ValidationResult.skip().withLabel(labelType, valueName);
    }

    return (ValidationResult) validation.test(toValidate).withLabel(labelType, valueName);
  }
}
