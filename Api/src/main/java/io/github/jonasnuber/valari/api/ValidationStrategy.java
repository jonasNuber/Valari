package io.github.jonasnuber.valari.api;

import java.util.ArrayList;
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
 * of type {@code R}, which extends {@link ThrowableResult}.
 * </p>
 *
 * @param <RESULT> the result type returned by the strategy (e.g., {@link ValidationResultCollection})
 * @author Jonas Nuber
 */
@FunctionalInterface
@SuppressWarnings("java:S119")
public interface ValidationStrategy<RESULT extends ThrowableResult<RESULT>> {

    /**
     * Executes the validation strategy on the given list of field-level validators.
     *
     * @param validators           a list of deferred {@link NoInputValidator} instances to be invoked during validation
     * @param validationDescriptor the metadata of the object being validated, used to provide context in result reporting
     * @return a validation result of type {@code R}, as determined by the specific strategy implementation
     */
    RESULT validate(List<NoInputValidator<? extends ThrowableResult<?>>> validators, ValidationDescriptor validationDescriptor);

    @SuppressWarnings({"unchecked", "rawtypes"})
    default <TYPE> RESULT validate(
            List<Validator<TYPE, ? extends ThrowableResult<?>>> validationBindings,
            TYPE objectToValidate,
            ValidationDescriptor descriptor
    ) {
        List<NoInputValidator<? extends ThrowableResult<?>>> noInputValidators = new ArrayList<>();

        for (Validator<TYPE, ? extends ThrowableResult<?>> binding : validationBindings) {
            NoInputValidator<? extends ThrowableResult<?>> validator = (NoInputValidator) () -> binding.validate(objectToValidate);
            noInputValidators.add(validator);
        }

        return validate(noInputValidators, descriptor);
    }
}
