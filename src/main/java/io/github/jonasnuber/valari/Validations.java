package io.github.jonasnuber.valari;

import io.github.jonasnuber.valari.base.Validation;
import io.github.jonasnuber.valari.helpers.CollectionValidationHelpers;
import io.github.jonasnuber.valari.helpers.IntegerValidationHelpers;
import io.github.jonasnuber.valari.helpers.ObjectValidationHelpers;
import io.github.jonasnuber.valari.helpers.StringValidationHelpers;

import java.util.Collection;

/**
 * {@code Validations} is a utility class which provides default methods for various pre-defined validations using helper classes:
 * <ul>
 *     <li>{@link ObjectValidationHelpers}: {@code notNull()}, {@code isEqualTo(K other)}</li>
 *     <li>{@link StringValidationHelpers}: {@code notEmpty(), @code exactly(int size)}, {@code moreThan(int size)}, {@code lessThan(int size)},
 *         {@code between(int minSize, int maxSize)}, {@code contains(String str)}, {@code containsIgnoreCase(String str)}</li>
 *     <li>{@link IntegerValidationHelpers}: {@code sameAmount(int exact)}, {@code lowerThan(int max)}, {@code greaterThan(int min)},
 *         {@code inBetween(int min, int max)}</li>
 *     <li>{@link CollectionValidationHelpers}: {@code notEmpty()}, {@code sizeBetween(int min, int max)}</li>
 * </ul>
 *
 * @author Jonas Nuber
 */
public final class Validations {

    private Validations() throws IllegalAccessException {
        throw new IllegalAccessException("Utility class should not be instantiated");
    }

    /**
     * Returns a validation that checks if the object is not null.
     *
     * @return The validation for not null.
     */
    public static <K> Validation<K> notNull() {
        return ObjectValidationHelpers.notNull();
    }

    /**
     * Returns a validation that checks if the object is equal to the specified value.
     *
     * @param other The value to compare with.
     * @return The validation for equality.
     */
    public static <K> Validation<K> isEqualTo(K other) {
        return ObjectValidationHelpers.isEqualTo(other);
    }

    /**
     * Returns a validation that passes only if the string is neither null nor empty.
     *
     * @return the Validation for not empty
     */
    public static Validation<String> notEmpty() {
        return StringValidationHelpers.notEmpty();
    }

    /**
     * Returns a validation that checks if the string has exactly the specified number of characters.
     *
     * @param size The exact number of characters.
     * @return The validation for exact string length.
     */
    public static Validation<String> exactly(int size) {
        return StringValidationHelpers.exactly(size);
    }

    /**
     * Returns a validation that checks if the string has more than the specified number of characters.
     *
     * @param size The minimum number of characters.
     * @return The validation for minimum string length.
     */
    public static Validation<String> moreThan(int size) {
        return StringValidationHelpers.moreThan(size);
    }

    /**
     * Returns a validation that checks if the string has less than the specified number of characters.
     *
     * @param size The maximum number of characters.
     * @return The validation for maximum string length.
     */
    public static Validation<String> lessThan(int size) {
        return StringValidationHelpers.lessThan(size);
    }

    /**
     * Returns a validation that checks if the string length is between the specified minimum and maximum values (exclusive).
     *
     * @param minSize The minimum number of characters.
     * @param maxSize The maximum number of characters.
     * @return The validation for string length within a range.
     */
    public static Validation<String> between(int minSize, int maxSize) {
        return StringValidationHelpers.between(minSize, maxSize);
    }

    /**
     * Returns a validation that checks if the string contains the specified substring.
     *
     * @param str The substring to search for.
     * @return The validation for string containment.
     */
    public static Validation<String> contains(String str) {
        return StringValidationHelpers.contains(str);
    }

    /**
     * Returns a validation that checks if the string contains the specified substring (case-insensitive).
     *
     * @param str The substring to search for.
     * @return The validation for case-insensitive string containment.
     */
    public static Validation<String> containsIgnoreCase(String str) {
        return StringValidationHelpers.containsIgnoreCase(str);
    }

    /**
     * Returns a validation that checks if the integer value is equal to the specified value.
     *
     * @param exact The exact value to compare with.
     * @return The validation for equality.
     */
    public static Validation<Integer> sameAmount(int exact) {
        return IntegerValidationHelpers.sameAmount(exact);
    }

    /**
     * Returns a validation that checks if the integer value is lower than the specified maximum.
     *
     * @param max The maximum value the integer must not exceed.
     * @return The validation for values lower than the maximum.
     */
    public static Validation<Integer> lowerThan(int max) {
        return IntegerValidationHelpers.lowerThan(max);
    }

    /**
     * Returns a validation that checks if the integer value is greater than the specified minimum.
     *
     * @param min The minimum value the integer must exceed.
     * @return The validation for values greater than the minimum.
     */
    public static Validation<Integer> greaterThan(int min) {
        return IntegerValidationHelpers.greaterThan(min);
    }

    /**
     * Returns a validation that checks if the integer value is within the specified range (exclusive).
     *
     * @param min The minimum value the integer must not be less than.
     * @param max The maximum value the integer must not exceed.
     * @return The validation for values within the range.
     */
    public static Validation<Integer> inBetween(int min, int max) {
        return IntegerValidationHelpers.inBetween(min, max);
    }

    /**
     * Returns a validation that passes only if the collection is neither null nor empty.
     *
     * @return the Validation for not empty
     */
    public static Validation<Collection<?>> notBlank() {
        return CollectionValidationHelpers.notBlank();
    }

    /**
     * Returns a validation that checks if the collection size is between the specified minimum and maximum values (exclusive).
     *
     * @param min The minimum number of elements.
     * @param max The maximum number of elements.
     * @return The validation for collection size within a range.
     */
    public static Validation<Collection<?>> sizeBetween(int min, int max) {
        return CollectionValidationHelpers.sizeBetween(min, max);
    }
}
