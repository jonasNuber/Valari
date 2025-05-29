package io.github.jonasnuber.valari.api.helpers;


import io.github.jonasnuber.valari.internal.SimpleValidation;
import io.github.jonasnuber.valari.spi.Validation;

/**
 * Utility class providing predefined validations for integer values.
 * These validations define conditions that an integer must meet to be considered valid.
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
     * @param exact The exact amount the integer value must equal.
     * @return The validation for equality.
     */
    public static Validation<Integer> sameAmount(int exact) {
        return SimpleValidation.from(i -> i == exact, String.format("must equal %d", exact));
    }

    /**
     * Returns a validation that passes only if the integer value is lower than the specified maximum.
     *
     * @param max The maximum value the integer must not exceed.
     * @return The validation for values lower than the maximum.
     */
    public static Validation<Integer> lowerThan(int max) {
        return SimpleValidation.from(i -> i < max, String.format("must be lower than %d", max));
    }

    /**
     * Returns a validation that passes only if the integer value is greater than the specified minimum.
     *
     * @param min The minimum value the integer must exceed.
     * @return The validation for values greater than the minimum.
     */
    public static Validation<Integer> greaterThan(int min) {
        return SimpleValidation.from(i -> i > min, String.format("must be greater than %d", min));
    }

    /**
     * Returns a validation that passes only if the integer value is within the specified range (exclusive).
     *
     * @param min The minimum value the integer must not be less than.
     * @param max The maximum value the integer must not exceed.
     * @return The validation for values within the range.
     */
    public static Validation<Integer> inBetween(int min, int max) {
        return greaterThan(min).and(lowerThan(max));
    }
}
