package io.github.jonasnuber.valari.core.bindings;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.core.DomainValidator;
import io.github.jonasnuber.valari.api.RuleBinding;
import io.github.jonasnuber.valari.core.ValidationResult;

import java.util.Objects;
import java.util.function.Function;

/**
 * Internal binding connecting a specific field of a domain object to a {@link Validation} rule.
 *
 * <p>This class serves as the glue between a domain object's field and the validation
 * infrastructure provided by {@link DomainValidator}. It implements both:
 *
 * <ul>
 *   <li>{@link RuleBinding} – for fluently assigning validation rules via {@link
 *       #mustSatisfy(Validation)} or {@link #ifPresent(Validation)}
 *   <li>{@link Validator} – for executing the assigned validation on the field when the parent
 *       {@link DomainValidator} runs.
 * </ul>
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * DomainValidator<User> validator = DomainValidator.of(User.class);
 * validator.field("email", User::getEmail)
 *          .mustSatisfy(validEmail());
 * }</pre>
 *
 * <p>This class is internal and should only be used within Valari's fluent validation API.
 *
 * @param <TYPE> the type of the object being validated
 * @param <FIELD> the type of the field extracted from the object for validation
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public final class FieldRuleBinding<TYPE, FIELD>
    implements RuleBinding<DomainValidator<TYPE>, Validation<FIELD>>,
        Validator<TYPE, ValidationResult> {
  private final DomainValidator<TYPE> parent;
  private final String fieldName;
  private final Function<TYPE, FIELD> valueExtractor;

  private Validation<FIELD> validation;

  /**
   * Creates a new field binding.
   *
   * @param fieldName the name of the field being validated; used for labeling in validation
   *     results; must not be {@code null}
   * @param valueExtractor a function that extracts the field value from the object; must not be
   *     {@code null}
   * @param parent the parent {@link DomainValidator} managing this binding; must not be {@code
   *     null}
   * @throws NullPointerException if any parameter is {@code null}
   */
  public FieldRuleBinding(
      String fieldName, Function<TYPE, FIELD> valueExtractor, DomainValidator<TYPE> parent) {
    this.fieldName =
        Objects.requireNonNull(fieldName, "FieldName of the value to validate must not be null");
    this.valueExtractor =
        Objects.requireNonNull(
            valueExtractor, "Extractor Method to get value for validation must not be null");
    this.parent = Objects.requireNonNull(parent, "Parent Validator must not be null");
  }

  /**
   * Associates a validation rule with this field.
   *
   * <p>The rule will always be applied when validating the field, regardless of whether the field
   * value is {@code null}.
   *
   * @param rule the validation rule to apply; must not be {@code null}
   * @return the parent {@link DomainValidator} to allow fluent chaining
   * @throws NullPointerException if {@code rule} is {@code null}
   */
  @Override
  public DomainValidator<TYPE> mustSatisfy(Validation<FIELD> rule) {
    this.validation = Objects.requireNonNull(rule, "validation must not be null");

    return parent;
  }

  /**
   * Associates a validation rule that is only applied if the field value is non-null.
   *
   * <p>If the field value is {@code null}, the validation is skipped and considered valid.
   *
   * @param rule the validation rule to apply if the field value is present; must not be {@code
   *     null}
   * @return the parent {@link DomainValidator} to allow fluent chaining
   * @throws NullPointerException if {@code rule} is {@code null}
   */
  @Override
  public DomainValidator<TYPE> ifPresent(Validation<FIELD> rule) {
    this.validation =
        value ->
            Objects.isNull(value)
                ? ValidationResult.skip().withLabel(LabelType.FIELD, fieldName)
                : Objects.requireNonNull(rule, "validation must not be null")
                    .test(value)
                    .withLabel(LabelType.FIELD, fieldName);

    return parent;
  }

  /**
   * Executes the validation for the bound field on the given object.
   *
   * <p>This method applies the currently assigned {@link Validation} rule to the extracted field
   * value and returns a {@link ValidationResult} with the appropriate label set.
   *
   * @param toValidate the object to validate; must not be {@code null}
   * @return a {@link ValidationResult} representing the outcome of the validation
   * @throws NullPointerException if {@code toValidate} is {@code null}
   * @throws IllegalStateException if no validation rule has been assigned via {@link
   *     #mustSatisfy(Validation)} or {@link #ifPresent(Validation)}
   */
  @Override
  public ValidationResult validate(TYPE toValidate) {
    Objects.requireNonNull(toValidate, "Object to validate must not be null");

    if (validation == null) {
      throw new IllegalStateException(
          "No validation rule was set. Call mustSatisfy(...) or ifPresent(...) before validation");
    }

    FIELD value = valueExtractor.apply(toValidate);

    return (ValidationResult) validation.test(value).withLabel(LabelType.FIELD, fieldName);
  }
}
