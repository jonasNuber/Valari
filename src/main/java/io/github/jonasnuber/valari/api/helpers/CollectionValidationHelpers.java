package io.github.jonasnuber.valari.api.helpers;

import io.github.jonasnuber.valari.internal.SimpleValidation;
import io.github.jonasnuber.valari.spi.Validation;

import java.util.Collection;
import java.util.function.Predicate;

import static io.github.jonasnuber.valari.api.helpers.ObjectValidationHelpers.notNull;

/**
 * Utility class providing predefined validations for Collection values.
 * These validations define conditions that a collection, or elements of a collection must meet to be considered valid.
 *
 * @author Jonas Nuber
 */
public final class CollectionValidationHelpers {

    private CollectionValidationHelpers() throws IllegalAccessException {
        throw new IllegalAccessException("Utility classes should not be instantiated");
    }

    /**
     * Returns a validation that passes only if the collection is neither null nor empty.
     *
     * @return the Validation for not empty
     */
    public static Validation<Collection<?>> notEmpty() {
        return SimpleValidation.from(
                c -> !(c == null || c.isEmpty()),
                "Collection must not be empty");
    }

    /**
     * Returns a validation that checks if the collection size is between the specified minimum and maximum values (exclusive).
     *
     * @param min The minimum number of elements.
     * @param max The maximum number of elements.
     * @return The validation for collection size within a range.
     */
    public static Validation<Collection<?>> sizeBetween(int min, int max) {
        return SimpleValidation.from(
                c -> notNull(c, "Collection must not be null") &&
                        c.size() > min &&
                        c.size() < max,
                String.format("Size must be greater than %d and less than %d", min, max));
    }

    public static <T> Validation<Collection<T>> contains(T value) {
        return SimpleValidation.from(
                c -> notNull(c, "Collection must not be null") &&
                        notNull(value, "Object which should be contained, must not be null") &&
                        c.contains(value),
                String.format("Collection must contain Object \"%s\"", value)
        );
    }

    public static <T> Validation<Collection<T>> allMatch(Predicate<T> predicate) {
        return SimpleValidation.from(
                c -> notNull(c, "Collection must not be null") &&
                        notNull(predicate, "Predicate all elements should match, must not be null") &&
                        c.stream().allMatch(predicate),
                String.format("All elements must match the Predicate %s", predicate)
        );
    }

    public static <T> Validation<Collection<T>> noneMatch(Predicate<T> predicate) {
        return SimpleValidation.from(
                c -> notNull(c, "Collection must not be null") &&
                        notNull(predicate, "Predicate no element should match, must not be null") &&
                        c.stream().noneMatch(predicate),
                String.format("No element should match the predicate %s", predicate)
        );
    }
}
