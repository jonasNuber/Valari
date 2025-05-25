package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.internal.ValidationStrategy;

import java.util.List;

/**
 * A {@link ValidationStrategy} implementation that stops validation upon encountering
 * the first failure. This is also known as "fail-fast" behavior.
 * <p>
 * This strategy improves performance in scenarios where early termination is acceptable,
 * such as when only the first error is needed or validation is expensive.
 *
 * @author Jonas Nuber
 *
 * @param <T> the type of object being validated
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
     *         or empty if all validations pass
     */
    @Override
    public ValidationResultCollection validate(T toValidate, List<FieldValidationBinding<T, ?>> validations, Class<T> validationClass) {
        ValidationResultCollection results = new ValidationResultCollection(validationClass);

        for (FieldValidationBinding<T, ?> validation : validations) {
            ValidationResult result = validation.validate(toValidate);

            if (result.isInvalid()) {
                results.add(result);
                return results;
            }
        }

        return results;
    }
}
