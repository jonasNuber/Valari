package io.github.jonasnuber.valari.internal.strategies;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.spi.NoInputValidator;

import java.util.List;
import java.util.Objects;

/**
 * A {@link ValidationStrategy} implementation that evaluates all field-level validations
 * and collects all validation failures into a {@link ValidationResultCollection}.
 * <p>
 * This strategy does not short-circuit on failure and ensures a comprehensive report
 * of all validation issues present in the object.
 * </p>
 *
 * @param <T> the type of object being validated
 * @author Jonas Nuber
 */
public final class CollectFailuresStrategy<T> implements ValidationStrategy<T, ValidationResultCollection> {

    /**
     * Constructs a new {@code CollectFailuresStrategy}.
     * <p>
     * This strategy is suitable for use cases where all validation issues should be reported at once.
     * </p>
     */
    public CollectFailuresStrategy() {
        // No initialization required
    }

    /**
     * Executes all validators and aggregates their results.
     *
     * @param validators              the list of validators to apply
     * @param validationContextClass the class of the object being validated, used in the result context
     * @return a {@link ValidationResultCollection} containing all validation results,
     *         including valid and invalid ones
     * @throws NullPointerException if either parameter is {@code null}
     */
    @Override
    public ValidationResultCollection validate(List<NoInputValidator<ValidationResult>> validators, Class<T> validationContextClass) {
        Objects.requireNonNull(validators, "Validations to validate Object by must not be null");
        Objects.requireNonNull(validationContextClass, "The class of the Object to validate must not be null");

        ValidationResultCollection results = new ValidationResultCollection(validationContextClass);

        for (NoInputValidator<ValidationResult> validator : validators) {
            results.add(validator.validate());
        }

        return results;
    }
}
