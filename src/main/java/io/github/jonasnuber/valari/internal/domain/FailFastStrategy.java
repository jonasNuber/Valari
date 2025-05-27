package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.internal.ValidationStrategy;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.List;
import java.util.Objects;

/**
 * A {@link ValidationStrategy} implementation that stops validation upon encountering
 * the first failure. This is also known as "fail-fast" behavior.
 * <p>
 * This strategy improves performance in scenarios where early termination is acceptable,
 * such as when only the first error is needed or validation is expensive.
 *
 * @param <T> the type of object being validated
 * @author Jonas Nuber
 */
public final class FailFastStrategy<T> implements ValidationStrategy<T, ValidationResultCollection> {

    /**
     * Validates the given object by applying field-level validations in order
     * and returning after the first validation failure.
     *
     * @param toValidate      the object to validate
     * @param validations     the list of field-level validations to apply
     * @param validationClass the class of the object being validated
     * @return a {@link ValidationResultCollection} containing the first validation failure (if any),
     * or empty if all validations pass
     */
    @Override
    public ValidationResultCollection validate(T toValidate, List<Validator<T, ValidationResult>> validations, Class<T> validationClass) {
        Objects.requireNonNull(toValidate, "Object to validate must not be null");
        Objects.requireNonNull(validations, "Validations to validate Object by must not be null");
        Objects.requireNonNull(validationClass, "The class of the Object to validate must not be null");

        ValidationResultCollection results = new ValidationResultCollection(validationClass);

        for (Validator<T, ValidationResult> validation : validations) {
            ValidationResult result = validation.validate(toValidate);

            if (result.isInvalid()) {
                results.add(result);
                return results;
            }
        }

        return results;
    }
}
