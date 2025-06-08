package io.github.jonasnuber.valari.internal.strategies;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.spi.NoInputValidator;
import io.github.jonasnuber.valari.spi.ThrowingResult;

import java.util.List;

/**
 * Defines a strategy for orchestrating field-level validations for a given target type {@code T}.
 *
 * <p>
 * This interface decouples the validation execution logic from the validation definitions, allowing
 * pluggable strategies such as:
 * </p>
 * <ul>
 *   <li>{@link FailFastStrategy} – stops at the first validation failure</li>
 *   <li>{@link CollectFailuresStrategy} – aggregates all validation errors before returning</li>
 * </ul>
 *
 * <p>
 * Each strategy takes a list of {@link NoInputValidator} instances, which are deferred executable
 * validation steps (typically bound to specific fields or rules), and produces a structured result
 * of type {@code R}, which extends {@link ThrowingResult}.
 * </p>
 *
 * @param <T> the type of the object being validated (used for result context only)
 * @param <R> the result type returned by the strategy (e.g., {@link io.github.jonasnuber.valari.api.ValidationResultCollection})
 *
 * @author Jonas Nuber
 */
public interface ValidationStrategy<T, R extends ThrowingResult> {

    /**
     * Executes the validation strategy on the given list of field-level validators.
     *
     * @param validators a list of deferred {@link NoInputValidator} instances to be invoked during validation
     * @param validationContextClass the class of the object being validated, used to provide context in result reporting
     * @return a validation result of type {@code R}, as determined by the specific strategy implementation
     */
    R validate(List<NoInputValidator<ValidationResult>> validators, Class<T> validationContextClass);
}
