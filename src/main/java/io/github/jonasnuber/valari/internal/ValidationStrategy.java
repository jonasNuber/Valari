package io.github.jonasnuber.valari.internal;

import io.github.jonasnuber.valari.internal.domain.FieldValidationBinding;

import java.util.List;

/**
 * A strategy interface for validating an object of type {@code T} using a specific algorithm.
 * <p>
 * Implementations can define different behaviors such as {@link io.github.jonasnuber.valari.internal.domain.FailFastStrategy} (stop on first failure)
 * or collect-all (gather all validation errors) approaches.
 *
 * @param <T> the type of the object to validate
 * @param <R> the type of the result produced by the validation
 * @author Jonas Nuber
 */
public interface ValidationStrategy<T, R> {

    /**
     * Validates the given object using the provided field-level validations.
     *
     * @param toValidate      the object to validate
     * @param validations     a list of field-level validations to apply
     * @param validationClass the class of the object being validated, used for context in results
     * @return the result of the validation, determined by the specific strategy implementation
     */
    R validate(T toValidate, List<FieldValidationBinding<T, ?>> validations, Class<T> validationClass);
}
