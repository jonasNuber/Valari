package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.internal.ValidationStrategy;
import io.github.jonasnuber.valari.spi.Validator;

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
     * Constructs a new {@code CollectFailuresStrategy} instance.
     * <p>
     * This strategy collects all validation failures instead of short-circuiting on the first failure,
     * making it useful when a complete list of validation issues is needed.
     * </p>
     */
    public CollectFailuresStrategy() {
        // No initialization required
    }

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
    public ValidationResultCollection validate(T toValidate, List<Validator<T, ValidationResult>> validations, Class<T> validationClass) {
        Objects.requireNonNull(toValidate, "Object to validate must not be null");
        Objects.requireNonNull(validations, "Validations to validate Object by must not be null");
        Objects.requireNonNull(validationClass, "The class of the Object to validate must not be null");

        ValidationResultCollection results = new ValidationResultCollection(validationClass);

        for (Validator<T, ValidationResult> validation : validations) {
            results.add(validation.validate(toValidate));
        }

        return results;
    }
}
