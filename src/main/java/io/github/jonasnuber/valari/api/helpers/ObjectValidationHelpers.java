package io.github.jonasnuber.valari.api.helpers;

import io.github.jonasnuber.valari.api.SimpleValidation;
import io.github.jonasnuber.valari.api.results.ValidationResult;
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
     * Returns a validation that passes only if the object is not {@code null}.
     *
     * @param <K> the type of the object
     * @return a validation ensuring the object is not null
     */
    public static <K> Validation<K> notNull(){
        return SimpleValidation.from(
                Objects::nonNull,
                new ValidationResult.Builder("must not be null")
                        .messageKey("validation.object.notNull")
        );
    }

    /**
     * Returns a validation that passes only if the object is equal to the specified value.
     * <p>
     * The provided comparison object must not be {@code null}.
     * </p>
     *
     * @param <K>   the type of the object
     * @param other the object to compare against (must not be {@code null})
     * @return a validation ensuring equality with the specified object
     * @throws NullPointerException if {@code other} is {@code null}
     */
    public static <K> Validation<K> isEqualTo(K other){
        Objects.requireNonNull(other, "Object to equal must not be null");

        return SimpleValidation.from(
                o -> notNull(o, "Object must not be null") &&
                        other.equals(o),
                new ValidationResult.Builder("must be equal to \"{0}\"")
                        .messageKey("validation.object.equalTo")
                        .messageArgument(other)
        );
    }

    /**
     * Internal helper method that asserts the provided object is not {@code null}.
     *
     * @param o            the object to check
     * @param errorMessage the exception message if the object is {@code null}
     * @return always {@code true} if no exception is thrown
     * @throws NullPointerException if {@code o} is {@code null}
     */
    static boolean notNull(Object o, String errorMessage) throws NullPointerException {
        Objects.requireNonNull(o, errorMessage);

        return true;
    }
}
