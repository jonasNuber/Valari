package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.Objects;

/**
 * A simple wrapper around a {@link Validation} for validating a single value with a contextual name.
 *
 * <p>This validator allows you to associate a name (e.g., a field or parameter name) with the validated value,
 * making the resulting {@link ValidationResult} more meaningful and easier to trace back in error reporting.</p>
 *
 * @author Jonas Nuber
 *
 * @param <T> the type of the value being validated
 */
public class ValueValidator<T> implements Validator<T, ValidationResult> {
    private final Validation<T> validation;
    private final String valueName;

    private ValueValidator(Validation<T> validation, String valueName) {
        this.validation = Objects.requireNonNull(validation, "Validation must not be null");
        this.valueName = Objects.requireNonNull(valueName, "Value Name must not be null");
    }

    /**
     * Creates a {@code ValueValidator} for the given validation with a default name {@code "Value"}.
     *
     * @param validation the validation to wrap
     * @param <T> the type of the value being validated
     * @return a new {@code ValueValidator} instance
     */
    public static <T> ValueValidator<T> with(Validation<T> validation) {
        return new ValueValidator<>(validation, "Value");
    }

    /**
     * Creates a {@code ValueValidator} for the given validation with a custom value name.
     *
     * @param validation the validation to wrap
     * @param valueName the name of the value being validated (used for error messages)
     * @param <T> the type of the value being validated
     * @return a new {@code ValueValidator} instance
     */
    public static <T> ValueValidator<T> with(Validation<T> validation, String valueName) {
        return new ValueValidator<>(validation, valueName);
    }

    /**
     * Validates the provided value and returns the resulting {@link ValidationResult},
     * enriched with the configured value name.
     *
     * @param toValidate the value to validate
     * @return the validation result
     * @throws NullPointerException if the input value is {@code null}
     */
    @Override
    public ValidationResult validate(T toValidate) {
        Objects.requireNonNull(toValidate, "Object to validate must not be null");

        return validation.test(toValidate).withFieldName(valueName);
    }

    /**
     * Validates the provided value and throws an exception if the result is invalid.
     *
     * @param toValidate the value to validate
     * @throws RuntimeException if validation fails
     */
    @Override
    public void validateAndThrow(T toValidate) {
        validate(toValidate).throwIfInvalid();
    }
}
