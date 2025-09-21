package io.github.jonasnuber.valari.internal.strategies;

import io.github.jonasnuber.valari.api.results.ValidationDescriptor;
import io.github.jonasnuber.valari.api.results.ValidationMetadata;
import io.github.jonasnuber.valari.api.results.ValidationResultCollection;
import io.github.jonasnuber.valari.spi.NoInputValidator;
import io.github.jonasnuber.valari.spi.ThrowingResult;

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
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public final class CollectFailuresStrategy implements ValidationStrategy<ValidationResultCollection> {

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
     * @param validationDescriptor the metadata of validation, used in the result context
     * @return a {@link ValidationResultCollection} containing all validation results,
     *         including valid and invalid ones
     * @throws NullPointerException if either parameter is {@code null}
     */
    @Override
    public ValidationResultCollection validate(List<NoInputValidator<ThrowingResult>> validators, ValidationDescriptor validationDescriptor) {
        Objects.requireNonNull(validators, "Validations to validate Object by must not be null");
        Objects.requireNonNull(validationDescriptor, "the validationDescriptor for the validation must not be null");

        ValidationResultCollection results = new ValidationResultCollection(validationDescriptor);

        for (NoInputValidator<ThrowingResult> validator : validators) {
            results.add(validator.validate());
        }

        return results;
    }
}
