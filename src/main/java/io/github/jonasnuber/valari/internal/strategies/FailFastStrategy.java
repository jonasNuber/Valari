package io.github.jonasnuber.valari.internal.strategies;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.spi.NoInputValidator;

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
 * @param <T> the type of object being validated
 * @author Jonas Nuber
 */
public final class FailFastStrategy<T> implements ValidationStrategy<T, ValidationResultCollection> {

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
     * @param validationContextClass the class of the object being validated, used in the result context
     * @return a {@link ValidationResultCollection} containing the first failure (if any),
     *         or empty if all validations succeed
     * @throws NullPointerException if either parameter is {@code null}
     */
    @Override
    public ValidationResultCollection validate(List<NoInputValidator<ValidationResult>> validators, Class<T> validationContextClass) {
        Objects.requireNonNull(validators, "Validations to validate Object by must not be null");
        Objects.requireNonNull(validationContextClass, "The class of the Object to validate must not be null");

        ValidationResultCollection results = new ValidationResultCollection(validationContextClass);

        for (NoInputValidator<ValidationResult> validator : validators) {
            ValidationResult result = validator.validate();

            if (result.isInvalid()) {
                results.add(result);
                return results;
            }
        }

        return results;
    }
}
