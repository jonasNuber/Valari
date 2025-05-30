package io.github.jonasnuber.valari.api.helpers;

import io.github.jonasnuber.valari.internal.SimpleValidation;
import io.github.jonasnuber.valari.spi.Validation;

import java.util.Objects;

/**
 * Utility class providing predefined validations for objects.
 * These validations define conditions that an object must meet to be considered valid.
 *
 * @author Jonas Nuber
 */
public final class ObjectValidationHelpers {

    private ObjectValidationHelpers() throws IllegalAccessException {
        throw new IllegalAccessException("ObjectValidationHelpers is a utility class and cannot be instantiated");
    }

    /**
     * Returns a validation that passes only if the object is not null.
     *
     * @param <K> The type of the object.
     * @return The validation for not null.
     */
    public static <K> Validation<K> notNull(){
        return SimpleValidation.from(Objects::nonNull, "must not be null");
    }

    /**
     * Returns a validation that passes only if the object is equal to the specified other object.
     *
     * @param <K> The type of the object.
     * @param other The object to test equality against.
     * @return The validation for equality.
     */
    public static <K> Validation<K> isEqualTo(K other){
        return SimpleValidation.from(
                o -> notNull(o, "Object must not be null") &&
                        notNull(other, "Object to equal must not be null") &&
                        other.equals(o),
                String.format("must be equal to \"%s\"", other));
    }

    static boolean notNull(Object o, String errorMessage) throws NullPointerException {
        Objects.requireNonNull(o, errorMessage);

        return true;
    }
}
