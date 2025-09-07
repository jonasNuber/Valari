package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.api.results.LabelType;
import io.github.jonasnuber.valari.api.results.ValidationResult;
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
 * @param <TYPE> the type of the value being validated
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
     * Creates a {@code ValueValidator} for the given validation with a default name {@code "Value"}.
     *
     * @param validation the validation to wrap
     * @param <TYPE>        the type of the value being validated
     * @return a new {@code ValueValidator} instance
     */
    public static <TYPE> ValueValidator<TYPE> with(Validation<TYPE> validation) {
        return new ValueValidator<>("Value", validation, false);
    }

    /**
     * Creates a {@code ValueValidator} for the given validation with a custom value name.
     *
     * @param validation the validation to wrap
     * @param valueName  the name of the value being validated (used for error messages)
     * @param <TYPE>        the type of the value being validated
     * @return a new {@code ValueValidator} instance
     */
    public static <TYPE> ValueValidator<TYPE> with(String valueName, Validation<TYPE> validation) {
        return new ValueValidator<>(valueName, validation, false);
    }

    /**
     * Creates an optional {@code ValueValidator} for the given validation with a default name {@code "Value"}.
     * If the value is {@code null}, validation passes.
     *
     * @param validation the validation to wrap
     * @param <TYPE>        the type of the value being validated
     * @return a new optional {@code ValueValidator} instance
     */
    public static <TYPE> ValueValidator<TYPE> optional(Validation<TYPE> validation) {
        return new ValueValidator<>("Value", validation, true);
    }

    /**
     * Creates an optional {@code ValueValidator} for the given validation with a custom value name.
     * If the value is {@code null}, validation passes.
     *
     * @param validation the validation to wrap
     * @param valueName  the name of the value being validated (used for error messages)
     * @param <TYPE>        the type of the value being validated
     * @return a new optional {@code ValueValidator} instance
     */
    public static <TYPE> ValueValidator<TYPE> optional(String valueName, Validation<TYPE> validation) {
        return new ValueValidator<>(valueName, validation, true);
    }

    /**
     * Allows to attach another {@link LabelType} to the {@link ValidationResult} produced by this validator.
     * The default Value is {@link LabelType#VALUE}
     *
     * @param labelType the type of the label which should be used
     * @return this validator for method chaining
     */
    public ValueValidator<TYPE> withLabelType(LabelType labelType) {
        this.labelType = Objects.requireNonNull(labelType, "LabelType must not be null");

        return this;
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
    public ValidationResult validate(TYPE toValidate) {
        if (optional && Objects.isNull(toValidate)) {
            return ValidationResult.skip().withLabel(labelType, valueName);
        }

        return validation.test(toValidate).withLabel(labelType, valueName);
    }
}
