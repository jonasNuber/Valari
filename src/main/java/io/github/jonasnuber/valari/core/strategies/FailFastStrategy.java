package io.github.jonasnuber.valari.core.strategies;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.core.ValidationResultCollection;

import java.util.List;
import java.util.Objects;

/**
 * A {@link ValidationStrategy} implementation that stops validation upon encountering
 * the first failure. This is also known as "fail-fast" behavior.
 * <p>
 * This strategy improves performance in scenarios where early termination is acceptable,
 * such as when only the first error is needed or validation is expensive.
 * </p>
 *
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public final class FailFastStrategy implements ValidationStrategy<ValidationResultCollection> {

    /**
     * Constructs a new {@code FailFastStrategy}.
     * <p>
     * This strategy stops evaluation on the first validation failure.
     * </p>
     */
    public FailFastStrategy() {
        // No initialization required
    }

    /**
     * Executes each validator in order and returns after the first failure.
     *
     * @param validators              the list of validators to apply
     * @param validationDescriptor the metadata of validation, used in the result context
     * @return a {@link ValidationResultCollection} containing the first failure (if any),
     *         or empty if all validations succeed
     * @throws NullPointerException if either parameter is {@code null}
     */
    @Override
    public ValidationResultCollection validate(List<NoInputValidator<? extends ThrowableResult<?>>> validators, ValidationDescriptor validationDescriptor) {
        Objects.requireNonNull(validators, "Validations to validate Object by must not be null");
        Objects.requireNonNull(validationDescriptor, "The descriptor of the validation must not be null");

        ValidationResultCollection.Builder resultsBuilder = new ValidationResultCollection.Builder(validationDescriptor);

        for (NoInputValidator<? extends ThrowableResult<?>> validator : validators) {
            ThrowableResult<?> result = validator.validate();

            if (result.isInvalid()) {
                resultsBuilder.add(result);
                return resultsBuilder.build();
            }
        }

        return resultsBuilder.build();
    }
}
