package io.github.jonasnuber.valari.internal.bindings;

import io.github.jonasnuber.valari.api.validators.DomainValidator;
import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.spi.RuleBinding;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.Objects;
import java.util.function.Function;

/**
 * Binds a nested (composite) object of a domain entity to a {@link DomainValidator},
 * enabling recursive validation of complex object graphs.
 * <p>
 * This class supports both required and optional validation modes:
 * <ul>
 *   <li>{@link #mustSatisfy(DomainValidator)} requires the nested value to be non-null.</li>
 *   <li>{@link #ifPresent(DomainValidator)} only validates if the nested value is present.</li>
 * </ul>
 *
 * Example:
 * <pre>{@code
 * validator.field("address", User::getAddress)
 *          .mustSatisfy(addressValidator);
 * }</pre>
 *
 * @param <T> the type of the parent object being validated
 * @param <F> the type of the nested/composite field being validated
 *
 * @author Jonas Nuber
 */
public final class NestedRuleBinding<T, F> implements RuleBinding<DomainValidator<T>, DomainValidator<F>>, Validator<T, ValidationResult> {
    private final DomainValidator<T> parent;
    private final String fieldName;
    private final Function<T, F> valueExtractor;

    private DomainValidator<F> compositeValidator;
    private boolean required;

    /**
     * Constructs a new {@code NestedValidationBinding} for a specific nested field.
     * <p>
     * This constructor sets up the binding between a parent domain validator and a nested field,
     * enabling recursive validation logic for complex object graphs.
     * </p>
     *
     * @param fieldName     the name of the nested field (used for error reporting, must not be {@code null})
     * @param valueExtractor a function to extract the nested field value from the parent object (must not be {@code null})
     * @param parent        the parent domain validator (must not be {@code null})
     * @throws NullPointerException if any argument is {@code null}
     */
    public NestedRuleBinding(String fieldName, Function<T, F> valueExtractor, DomainValidator<T> parent) {
        this.fieldName = Objects.requireNonNull(fieldName, "FieldName of the value to validate must not be null");
        this.valueExtractor = Objects.requireNonNull(valueExtractor, "Extractor Method to get value for validation must not be null");
        this.parent = Objects.requireNonNull(parent, "Parent Validator must not be null");
    }

    /**
     * Marks this field as required and associates a validator for the nested type.
     * <p>
     * If the value is {@code null}, validation fails with a {@link NullPointerException}.
     * </p>
     *
     * @param compositeValidator the validator for the nested field (must not be {@code null})
     * @return the parent validator for fluent chaining
     * @throws NullPointerException if {@code compositeValidator} is {@code null}
     */
    @Override
    public DomainValidator<T> mustSatisfy(DomainValidator<F> compositeValidator) {
        this.compositeValidator = Objects.requireNonNull(compositeValidator, "The validator for the nested type must not be null");
        required = true;

        return parent;
    }

    /**
     * Associates a validator for the nested field, but only applies it if the value is present.
     * <p>
     * If the value is {@code null}, validation passes automatically.
     * </p>
     *
     * @param compositeValidator the validator for the nested field (must not be {@code null})
     * @return the parent validator for fluent chaining
     * @throws NullPointerException if {@code compositeValidator} is {@code null}
     */
    @Override
    public DomainValidator<T> ifPresent(DomainValidator<F> compositeValidator) {
        this.compositeValidator = Objects.requireNonNull(compositeValidator, "The validator for the composite type must not be null");
        required = false;

        return parent;
    }

    /**
     * Validates the nested field using the bound {@link DomainValidator}.
     * <p>
     * If {@link #mustSatisfy(DomainValidator)} was used and the value is {@code null},
     * a {@link NullPointerException} is thrown.
     * </p>
     *
     * @param toValidate the object to validate (must not be {@code null})
     * @return a {@link ValidationResult} representing success or failure
     * @throws NullPointerException if the object or a required value is {@code null}
     */
    @Override
    public ValidationResult validate(T toValidate) {
        Objects.requireNonNull(toValidate, "Object to validate must not be null");

        F value = extractValue(toValidate);

        if (shouldSkipValidation(value)) {
            return ValidationResult.ok();
        }

        return validateNested(value);
    }

    private F extractValue(T toValidate) {
        return valueExtractor.apply(toValidate);
    }

    private boolean shouldSkipValidation(F value) {
        if (required) {
            Objects.requireNonNull(value, "Required field value must not be null");
            return false;
        }
        return value == null;
    }

    private ValidationResult validateNested(F value) {
        ValidationResultCollection results = compositeValidator.validate(value);

        if (results.hasFailures()) {
            return ValidationResult.fail(results.getErrorMessage(), fieldName);
        }

        return ValidationResult.ok();
    }
}
