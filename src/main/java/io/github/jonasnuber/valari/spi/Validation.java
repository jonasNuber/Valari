package io.github.jonasnuber.valari.spi;


import io.github.jonasnuber.valari.api.ValidationResult;

/**
 * A Validation is a test for the validity of any object or field against predefined conditions.
 * If the validation against these conditions is successful, an OK ValidationResult will be returned,
 * otherwise the ValidationResult will be FAILED.
 * <p>
 * This is a functional interface whose functional method is {@link #test(Object)}.
 * </p>
 * <p>
 * Default functionality is provided for logical {@linkplain #and} and {@linkplain #or} Validation chaining.
 * </p>
 * <p>
 *
 * @param <K> Type of object to be validated.
 * @author Jonas Nuber
 * </p>
 */
@FunctionalInterface
public interface Validation<K> {

    /**
     * Tests the given object against the validation logic.
     *
     * @param param The object to be validated.
     * @return ValidationResult indicating the outcome of the validation.
     */
    ValidationResult test(K param);

    /**
     * Combines the current Validation with another one using logical conjunction.
     * Testing against an object will only return a valid ValidationResult when both validations are valid.
     *
     * @param other The validation to conjunct.
     * @return A new Validation representing the conjunction of the current validation with the provided one.
     */
    default Validation<K> and(Validation<K> other) {
        return param -> {
            var firstResult = this.test(param);
            return !firstResult.isValid() ? firstResult : other.test(param);
        };
    }

    /**
     * Combines the current Validation with another one using logical disjunction.
     * Testing against an object will return a valid ValidationResult when one or both validations are valid.
     *
     * @param other The validation to disjunct.
     * @return A new Validation representing the disjunction of the current validation with the provided one.
     */
    default Validation<K> or(Validation<K> other) {
        return param -> {
            var firstResult = this.test(param);
            return firstResult.isValid() ? firstResult : other.test(param);
        };
    }
}
