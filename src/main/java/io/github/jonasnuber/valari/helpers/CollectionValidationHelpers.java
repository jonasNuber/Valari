package io.github.jonasnuber.valari.helpers;

import io.github.jonasnuber.valari.base.SimpleValidation;
import io.github.jonasnuber.valari.base.Validation;

import java.util.Collection;

/**
 * Utility class providing predefined validations for Collection values.
 * These validations define conditions that a collection, or elements of a collection must meet to be considered valid.
 *
 * <p>
 *
 * @author Jonas Nuber
 * </p>
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
    public static Validation<Collection<?>> notBlank() {
        return SimpleValidation.from(c -> !(c == null || c.isEmpty()), "Collection must not be empty");
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
                c -> c != null && c.size() > min && c.size() < max,
                String.format("Size must be between %d and %d", min, max));
    }

    //match all
}
