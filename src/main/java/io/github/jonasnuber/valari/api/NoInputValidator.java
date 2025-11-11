package io.github.jonasnuber.valari.api;

/**
 * A generic interface for performing validation without requiring an input parameter.
 * <p>
 * This interface is useful for scenarios where the validation logic is self-contained
 * or contextually bound (e.g., validating pre-bound fields or derived data).
 * It abstracts the validation logic and returns a {@link ThrowableResult}, which
 * can represent either a successful or failed validation outcome.
 * </p>
 *
 * <p>Typical implementations may use closures, field accessors, or internal state to determine validity,
 * and can optionally throw an exception if the result is invalid.
 * </p>
 *
 * @param <RESULT> the type of result returned by the validation process
 *
 * @author Jonas Nuber
 */
@FunctionalInterface
@SuppressWarnings("java:S119")
public non-sealed interface NoInputValidator<RESULT extends Result<RESULT>> extends GenericValidator {

    /**
     * Executes the validation logic and returns the result.
     *
     * @return the result of the validation
     */
    RESULT validate();
}
