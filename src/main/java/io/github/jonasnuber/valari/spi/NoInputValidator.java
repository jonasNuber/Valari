package io.github.jonasnuber.valari.spi;

import io.github.jonasnuber.valari.internal.BaseValidator;

/**
 * A generic interface for performing validation without requiring an input parameter.
 * <p>
 * This interface is useful for scenarios where the validation logic is self-contained
 * or contextually bound (e.g., validating pre-bound fields or derived data).
 * It abstracts the validation logic and returns a {@link ThrowingResult}, which
 * can represent either a successful or failed validation outcome.
 * </p>
 *
 * <p>Typical implementations may use closures, field accessors, or internal state to determine validity,
 * and can optionally throw an exception if the result is invalid.
 * </p>
 *
 * @param <R> the type of result returned by the validation process
 *
 * @author Jonas Nuber
 */
@FunctionalInterface
public interface NoInputValidator<R extends ThrowingResult> extends BaseValidator {

    /**
     * Executes the validation logic and returns the result.
     *
     * @return the result of the validation
     */
    R validate();

    /**
     * Executes the validation and throws an exception if the result is invalid.
     * <p>
     * The type and content of the exception is defined by the result's {@link ThrowingResult#throwIfInvalid()} method.
     * </p>
     *
     * @throws RuntimeException if the validation result is invalid
     */
    default void validateAndThrow() {
        validate().throwIfInvalid();
    }
}
