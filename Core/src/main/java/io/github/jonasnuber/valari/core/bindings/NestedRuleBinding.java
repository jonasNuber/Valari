package io.github.jonasnuber.valari.core.bindings;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.RuleBinding;
import io.github.jonasnuber.valari.core.DomainValidator;
import io.github.jonasnuber.valari.core.ValidationResultCollection;
import java.util.Objects;
import java.util.function.Function;

/**
 * A rule binding that connects a nested (composite) property of a domain object to a dedicated
 * {@link DomainValidator}, enabling recursive validation of hierarchical object structures.
 *
 * <p>This binding supports two validation modes:
 *
 * <ul>
 *   <li><b>Required</b> — configured via {@link #mustSatisfy(DomainValidator)}. The nested value
 *       must be non-{@code null}; otherwise validation fails with a {@link NullPointerException}.
 *       The nested validator is always applied.
 *   <li><b>Optional</b> — configured via {@link #ifPresent(DomainValidator)}. Validation of the
 *       nested value only takes place if it is not {@code null}. A {@code null} value is treated as
 *       a valid state.
 * </ul>
 *
 * <p>A typical usage pattern within a parent {@link DomainValidator} is:
 *
 * <pre>{@code
 * validator.field("address", User::getAddress)
 *          .mustSatisfy(addressValidator);
 * }</pre>
 *
 * <p>This class integrates with {@link ValidationResultCollection}, enabling aggregation of child
 * validation errors under a single validation descriptor representing the nested field.
 *
 * @param <TYPE> the type of the parent object being validated
 * @param <NESTED> the type of the nested/composite field being validated
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public final class NestedRuleBinding<TYPE, NESTED>
    implements RuleBinding<DomainValidator<TYPE>, DomainValidator<NESTED>>,
        Validator<TYPE, ValidationResultCollection> {
  private final DomainValidator<TYPE> parent;
  private final ValidationDescriptor validationDescriptor;
  private final Function<TYPE, NESTED> valueExtractor;

  private DomainValidator<NESTED> compositeValidator;
  private boolean required;

  /**
   * Creates a new binding that links a nested property to a {@link DomainValidator}.
   *
   * <p>The provided {@link ValidationDescriptor} is used as the context under which all results of
   * the nested validator will be grouped. The {@code valueExtractor} retrieves the nested value
   * from the parent object during validation.
   *
   * @param validationDescriptor metadata describing the nested field (never {@code null})
   * @param valueExtractor a function that extracts the nested value from the parent (never {@code
   *     null})
   * @param parent the parent validator to which this rule belongs (never {@code null})
   * @throws NullPointerException if any argument is {@code null}
   */
  public NestedRuleBinding(
      ValidationDescriptor validationDescriptor,
      Function<TYPE, NESTED> valueExtractor,
      DomainValidator<TYPE> parent) {
    this.validationDescriptor =
        Objects.requireNonNull(validationDescriptor, "ValidationDescriptor must not be null");
    this.valueExtractor =
        Objects.requireNonNull(
            valueExtractor, "Extractor Method to get value for validation must not be null");
    this.parent = Objects.requireNonNull(parent, "Parent Validator must not be null");
  }

  /**
   * Marks the nested field as required and assigns a validator for it.
   *
   * <p>When configured as required, the nested value must not be {@code null}, and validation will
   * fail immediately with a {@link NullPointerException} if it is. Otherwise, the assigned
   * validator will always be invoked.
   *
   * @param compositeValidator the validator to apply to the nested value (never {@code null})
   * @return the parent validator, enabling fluent method chaining
   * @throws NullPointerException if {@code compositeValidator} is {@code null}
   */
  @Override
  public DomainValidator<TYPE> mustSatisfy(DomainValidator<NESTED> compositeValidator) {
    this.compositeValidator =
        Objects.requireNonNull(
            compositeValidator, "The validator for the nested type must not be null");
    required = true;

    return parent;
  }

  /**
   * Assigns a validator for the nested field that is only applied when the nested value is present.
   *
   * <p>If the nested value is {@code null}, validation succeeds implicitly and the nested validator
   * is not executed.
   *
   * @param compositeValidator the validator to apply to the nested value (never {@code null})
   * @return the parent validator, enabling fluent method chaining
   * @throws NullPointerException if {@code compositeValidator} is {@code null}
   */
  @Override
  public DomainValidator<TYPE> ifPresent(DomainValidator<NESTED> compositeValidator) {
    this.compositeValidator =
        Objects.requireNonNull(
            compositeValidator, "The validator for the composite type must not be null");
    required = false;

    return parent;
  }

  /**
   * Validates the nested field using the configured {@link DomainValidator}.
   *
   * <p>The nested value is extracted using the configured extractor function. Depending on the
   * chosen mode ({@link #mustSatisfy(DomainValidator)} or {@link #ifPresent(DomainValidator)}), the
   * nested validator may be invoked or skipped.
   *
   * <p>All results of the nested validation are collected into a {@link ValidationResultCollection}
   * under this binding's {@link ValidationDescriptor}.
   *
   * @param toValidate the object containing the nested field (never {@code null})
   * @return a collection of validation results for the nested field
   * @throws NullPointerException if {@code toValidate} is {@code null}, or if the nested value is
   *     required but {@code null}
   * @throws IllegalStateException if no nested validator has been configured
   */
  @Override
  public ValidationResultCollection validate(TYPE toValidate) {
    Objects.requireNonNull(toValidate, "Object to validate must not be null");

    if (compositeValidator == null) {
      throw new IllegalStateException(
          "No validator was set. Call mustSatisfy(...) or ifPresent(...) before validation");
    }

    NESTED value = valueExtractor.apply(toValidate);
    ValidationResultCollection.Builder builder =
        ValidationResultCollection.builder(validationDescriptor);

    if (shouldSkipValidation(value)) {
      return builder.build();
    }

    builder.addAll(compositeValidator.validate(value).getResults());

    return builder.build();
  }

  private boolean shouldSkipValidation(NESTED value) {
    if (required) {
      Objects.requireNonNull(value, "Required field value must not be null");
      return false;
    }
    return value == null;
  }
}
