package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.validators.DomainValidator;
import io.github.jonasnuber.valari.internal.SimpleValidation;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.ValidationBinding;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.Objects;
import java.util.function.Function;

/**
 * Internal binding between a field of a domain object and a corresponding {@link Validation} rule.
 * <p>
 * This class is used by {@link DomainValidator} to associate field extractors with validation logic
 * in a fluent and composable manner. It implements both {@link ValidationBinding} for rule assignment
 * and {@link Validator} for applying the bound rule during validation.
 *
 * <p>Example usage (within a validator):
 * <pre>{@code
 * validator.field(User::getEmail, "email")
 *          .mustSatisfy(validEmail());
 * }</pre>
 *
 * @param <T> the type of the object being validated
 * @param <F> the type of the field being validated
 *
 * @author Jonas Nuber
 */
public class FieldValidationBinding<T, F> implements ValidationBinding<DomainValidator<T>, Validation<F>>, Validator<T, ValidationResult> {
  private final DomainValidator<T> parent;
  private final String fieldName;
  private final Function<T, F> valueExtractor;

  private Validation<F> validation;

  public FieldValidationBinding(DomainValidator<T> parent, Function<T, F> valueExtractor, String fieldName) {
    Objects.requireNonNull(parent, "Parent Validator must not be null");
    Objects.requireNonNull(valueExtractor, "Extractor Method to get value for validation must not be null");
    Objects.requireNonNull(fieldName, "FieldName of the value to validate must not be null");

    this.parent = parent;
    this.fieldName = fieldName;
    this.valueExtractor = valueExtractor;
  }

  /**
   * Associates a validation rule with this field.
   *
   * @param rule the validation rule to apply (must not be {@code null})
   * @return the parent validator, enabling fluent chaining
   * @throws NullPointerException if {@code rule} is {@code null}
   */
  @Override
  public DomainValidator<T> mustSatisfy(Validation<F> rule) {
    this.validation = Objects.requireNonNull(rule, "validation must not be null");

    return parent;
  }

  /**
   * Applies the given validation rule only if the field value is non-null.
   * <p>
   * If the value is {@code null}, validation is skipped and considered successful.
   *
   * @param rule the validation rule to apply if the field value is present (must not be {@code null})
   * @return the parent validator, enabling fluent chaining
   * @throws NullPointerException if {@code rule} is {@code null}
   */
  @Override
  public DomainValidator<T> ifPresent(Validation<F> rule) {
    this.validation = SimpleValidation.<F>from(Objects::isNull, "")
            .or(Objects.requireNonNull(rule, "validation must not be null"));
    return parent;
  }

  /**
   * Validates the field of the given object using the bound validation rule.
   *
   * @param toValidate the object to validate
   * @return the result of validation, including failure reason and field name if invalid
   * @throws NullPointerException if {@code toValidate} is {@code null}
   */
  @Override
   public ValidationResult validate(T toValidate) {
    Objects.requireNonNull(toValidate, "Object to validate must not be null");

    F value = valueExtractor.apply(toValidate);
    ValidationResult result = validation.test(value);

    if (result.isInvalid()) {
      return ValidationResult.fail(result.getCauseDescription(), fieldName);
    }

    return result;
  }

  /**
   * Validates the object and throws an exception if the field is invalid.
   *
   * @param toValidate the object to validate
   * @throws io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException if validation fails
   */
  @Override
  public void validateAndThrow(T toValidate) {
    validate(toValidate).throwIfInvalid(fieldName);
  }
}
