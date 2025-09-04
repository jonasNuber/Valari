package io.github.jonasnuber.valari.api.helpers;


import io.github.jonasnuber.valari.api.SimpleValidation;
import io.github.jonasnuber.valari.api.results.ValidationMetadata;
import io.github.jonasnuber.valari.api.results.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;

/**
 * Utility class providing predefined validations for {@link Integer} values.
 * These validations define conditions that an integer must satisfy to be considered valid.
 *
 * @author Jonas Nuber
 */
public final class IntegerValidationHelpers {

    private IntegerValidationHelpers() throws IllegalAccessException {
        throw new IllegalAccessException("IntegerValidationHelpers is a utility class and cannot be instantiated");
    }

    /**
     * Returns a validation that passes only if the integer value equals the specified amount.
     *
     * @param exact the exact integer value required
     * @return the validation for equality
     */
    public static Validation<Integer> sameAmount(int exact) {
        return SimpleValidation.from(
                i -> i == exact,
                new ValidationMetadata.Builder("must equal {0}")
                        .messageKey("validation.integer.sameAmount")
                        .messageArgument(exact)
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if the integer value is strictly less than the specified maximum.
     *
     * @param max the maximum value (exclusive)
     * @return the validation for values lower than the maximum
     */
    public static Validation<Integer> lowerThan(int max) {
        return SimpleValidation.from(
                i -> i < max,
                new ValidationMetadata.Builder("must be lower than {0}")
                        .messageKey("validation.integer.lowerThan")
                        .messageArgument(max)
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if the integer value is strictly greater than the specified minimum.
     *
     * @param min the minimum value (exclusive)
     * @return the validation for values greater than the minimum
     */
    public static Validation<Integer> greaterThan(int min) {
        return SimpleValidation.from(
                i -> i > min,
                new ValidationMetadata.Builder("must be greater than {0}")
                        .messageKey("validation.integer.greaterThan")
                        .messageArgument(min)
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if the integer value is strictly between the specified minimum and maximum.
     *
     * @param min the minimum value (exclusive)
     * @param max the maximum value (exclusive)
     * @return the validation for values within the exclusive range
     */
    public static Validation<Integer> inBetween(int min, int max) {
        return greaterThan(min).and(lowerThan(max));
    }

    /**
     * Returns a validation that passes only if the integer value is between the specified minimum and maximum, inclusive.
     *
     * @param min the minimum value (inclusive)
     * @param max the maximum value (inclusive)
     * @return the validation for values within the inclusive range
     */
    public static Validation<Integer> inBetweenInclusive(int min, int max) {
        return greaterThan(--min).and(lowerThan(++max));
    }

    /**
     * Returns a validation that passes only if the integer value is even.
     *
     * @return the validation for even numbers
     */
    public static Validation<Integer> isEven() {
        return SimpleValidation.from(
                i -> i % 2 == 0,
                new ValidationMetadata.Builder("must be even")
                        .messageKey("validation.integer.isEven")
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if the integer value is odd.
     *
     * @return the validation for odd numbers
     */
    public static Validation<Integer> isOdd() {
        return SimpleValidation.from(
                i -> i % 2 != 0,
                new ValidationMetadata.Builder("must be odd")
                        .messageKey("validation.integer.isOdd")
                        .build()
        );
    }
}
