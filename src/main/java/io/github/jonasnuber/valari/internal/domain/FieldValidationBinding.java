package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.internal.SimpleValidation;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.ValidationBinding;

import java.util.Objects;
import java.util.function.Function;

/**
 * Internal binding between a field of a domain object and its associated validation logic.
 * <p>
 * Used by {@link DomainValidator} to register field-level validations and evaluate them during validation.
 *
 * @author Jonas Nuber
 *
 * @param <T> the type of the object being validated
 * @param <F> the type of the field being validated
 */
public class FieldValidationBinding<T, F> implements ValidationBinding<DomainValidator<T>, F> {
  private final DomainValidator<T> parent;
  private final String fieldName;
  private final Function<T, F> valueExtractor;

  private Validation<F> validation;

  public FieldValidationBinding(DomainValidator<T> parent, Function<T, F> valueExtractor, String fieldName) {
    this.parent = parent;
    this.fieldName = fieldName;
    this.valueExtractor = valueExtractor;
  }

  /**
   * Associates a validation rule with this field.
   * <p>
   * The provided validation must not be {@code null}.
   *
   * @param validation the validation rule to apply (must not be null)
   * @return the parent validator, enabling fluent chaining
   * @throws NullPointerException if the validation is {@code null}
   */
  @Override
  public DomainValidator<T> mustSatisfy(Validation<F> validation) {
    this.validation = Objects.requireNonNull(validation, "validation must not be null");
    return parent;
  }

  /**
   * Applies the given validation rule only if the field value is non-null.
   * <p>
   * If the value is {@code null}, validation is skipped and considered valid.
   * The provided validation must not be {@code null}.
   *
   * @param validation the validation rule to apply if the field value is present (must not be null)
   * @return the parent validator, enabling fluent chaining
   * @throws NullPointerException if the validation is {@code null}
   */
  @Override
  public DomainValidator<T> ifPresent(Validation<F> validation) {
    this.validation = SimpleValidation.<F>from(Objects::isNull, "")
            .or(Objects.requireNonNull(validation, "validation must not be null"));
    return parent;
  }

  /**
   * Validates the field of the given object using the bound validation rule.
   *
   * @param toValidate the object to validate
   * @return the {@link ValidationResult} for this field
   */
  ValidationResult validate(T toValidate) {
    F value = valueExtractor.apply(toValidate);
    ValidationResult result = validation.test(value);

    if (result.isInvalid()) {
      return ValidationResult.fail(result.getCauseDescription(), fieldName);
    }

    return result;
  }
}
