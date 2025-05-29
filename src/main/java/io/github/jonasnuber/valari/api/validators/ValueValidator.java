package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.Objects;

/**
 * A simple wrapper around a {@link Validation} for validating a single value with a contextual name.
 *
 * <p>
 * This validator allows you to associate a name (e.g., a field or parameter name) with the validated value,
 * making the resulting {@link ValidationResult} more meaningful and easier to trace back in error reporting.
 * </p>
 *
 * @param <T> the type of the value being validated
 * @author Jonas Nuber
 */
public class ValueValidator<T> implements Validator<T, ValidationResult> {
    private final Validation<T> validation;
    private final String valueName;
    private final boolean optional;

    private ValueValidator(Validation<T> validation, String valueName, boolean optional) {
        this.validation = Objects.requireNonNull(validation, "Validation must not be null");
        this.valueName = Objects.requireNonNull(valueName, "Value Name must not be null");
        this.optional = optional;
    }

    /**
     * Creates a {@code ValueValidator} for the given validation with a default name {@code "Value"}.
     *
     * @param validation the validation to wrap
     * @param <T>        the type of the value being validated
     * @return a new {@code ValueValidator} instance
     */
    public static <T> ValueValidator<T> with(Validation<T> validation) {
        return new ValueValidator<>(validation, "Value", false);
    }

    /**
     * Creates a {@code ValueValidator} for the given validation with a custom value name.
     *
     * @param validation the validation to wrap
     * @param valueName  the name of the value being validated (used for error messages)
     * @param <T>        the type of the value being validated
     * @return a new {@code ValueValidator} instance
     */
    public static <T> ValueValidator<T> with(Validation<T> validation, String valueName) {
        return new ValueValidator<>(validation, valueName, false);
    }

    /**
     * Creates an optional {@code ValueValidator} for the given validation with a default name {@code "Value"}.
     * If the value is {@code null}, validation passes.
     *
     * @param validation the validation to wrap
     * @param <T>        the type of the value being validated
     * @return a new optional {@code ValueValidator} instance
     */
    public static <T> ValueValidator<T> optional(Validation<T> validation) {
        return new ValueValidator<>(validation, "Value", true);
    }

    /**
     * Creates an optional {@code ValueValidator} for the given validation with a custom value name.
     * If the value is {@code null}, validation passes.
     *
     * @param validation the validation to wrap
     * @param valueName  the name of the value being validated (used for error messages)
     * @param <T>        the type of the value being validated
     * @return a new optional {@code ValueValidator} instance
     */
    public static <T> ValueValidator<T> optional(Validation<T> validation, String valueName) {
        return new ValueValidator<>(validation, valueName, true);
    }

    /**
     * Validates the provided value and returns the resulting {@link ValidationResult},
     * enriched with the configured value name.
     *
     * <p>
     * If {@link #optional} is {@code true} and the input is {@code null}, validation is skipped and considered valid.
     * </p>
     *
     * @param toValidate the value to validate
     * @return the validation result
     * @throws NullPointerException if the input value is {@code null} and {@code optional} is {@code false}
     */
    @Override
    public ValidationResult validate(T toValidate) {
        if (optional && Objects.isNull(toValidate)) {
            return ValidationResult.ok();
        }

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
