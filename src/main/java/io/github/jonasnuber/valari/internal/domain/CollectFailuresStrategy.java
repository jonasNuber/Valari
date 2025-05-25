package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.internal.ValidationStrategy;

import java.util.List;

/**
 * A {@link ValidationStrategy} implementation that evaluates all field-level validations
 * and collects all validation failures into a {@link ValidationResultCollection}.
 * <p>
 * This strategy does not short-circuit on failure and ensures a comprehensive report
 * of all validation issues present in the object.
 *
 * @author Jonas Nuber
 *
 * @param <T> the type of object being validated
 */
public final class CollectFailuresStrategy<T> implements ValidationStrategy<T, ValidationResultCollection> {

    /**
     * Validates the given object by applying all specified field validations and collecting
     * all validation failures.
     *
     * @param toValidate      the object to validate
     * @param validations     the list of field-level validations to apply
     * @param validationClass the class of the object being validated
     * @return a {@link ValidationResultCollection} containing all validation failures (if any)
     */
    @Override
    public ValidationResultCollection validate(T toValidate, List<FieldValidationBinding<T, ?>> validations, Class<T> validationClass) {
        ValidationResultCollection results = new ValidationResultCollection(validationClass);

        for (FieldValidationBinding<T, ?> validation : validations) {
            results.add(validation.validate(toValidate));
        }

        return results;
    }
}
