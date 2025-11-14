package io.github.jonasnuber.valari.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Defines a strategy for orchestrating multiple field-level or rule-level validations for a given
 * target type {@code T}.
 *
 * <p>A {@code ValidationStrategy} separates the <em>definition</em> of validation rules from the
 * <em>execution</em> of those rules. This allows different strategies (e.g., fail-fast, full
 * aggregation, short-circuit per-field, etc.) to be plugged in without changing the validation
 * definitions themselves.
 *
 * <p>The strategy operates on a list of {@link NoInputValidator} instances, which are deferred
 * validation steps. These validators are typically produced by binding rules to fields or objects,
 * but they no longer require the object instance at execution time.
 *
 * <p>A strategy produces a {@link ThrowableResult}, which can either represent a successful
 * validation or contain error information, aggregated or otherwise, depending on the specific
 * strategy implementation.
 *
 * @param <RESULT> the result type returned by the strategy. Must be a subtype of {@link
 *     ThrowableResult}, such as {@link AggregatedResult}.
 * @author Jonas Nuber
 */
@FunctionalInterface
@SuppressWarnings("java:S119")
public interface ValidationStrategy<RESULT extends ThrowableResult<RESULT>> {

  /**
   * Executes the validation strategy using the given list of deferred validators.
   *
   * <p>Implementations may execute all validators, stop on the first failure, group results by
   * field, or apply other evaluation semantics depending on the chosen strategy.
   *
   * @param validators a list of deferred {@link NoInputValidator} instances representing individual
   *     validation steps to be executed
   * @param validationDescriptor metadata describing the validated object (e.g., class name, field
   *     names), which can be included in error messages or result context
   * @return a result of type {@code RESULT}, encapsulating success or any validation failures
   */
  RESULT validate(
      List<NoInputValidator<? extends ThrowableResult<?>>> validators,
      ValidationDescriptor validationDescriptor);

  /**
   * Convenience method that converts a list of {@link Validator} instances—each still bound to a
   * specific object instance—into deferred {@link NoInputValidator} instances before delegating to
   * {@link #validate(List, ValidationDescriptor)}.
   *
   * <p>This method:
   *
   * <ol>
   *   <li>wraps each {@link Validator} into a lambda-based {@link NoInputValidator}
   *   <li>invokes {@link Validator#validate(Object)} lazily only when the strategy executes
   *   <li>preserves type safety via erasure-compatible casting
   * </ol>
   *
   * <p>This allows strategies to work entirely on {@code NoInputValidator} instances, keeping them
   * independent of the target object type {@code TYPE}.
   *
   * @param <TYPE> the type of the object being validated
   * @param validationBindings a list of {@link Validator} instances bound to {@code
   *     objectToValidate}
   * @param objectToValidate the instance being validated; passed to each validator during execution
   * @param descriptor metadata describing the validated object or validation context
   * @return the validation result produced by the strategy
   */
  @SuppressWarnings({"unchecked", "rawtypes"})
  default <TYPE> RESULT validate(
      List<Validator<TYPE, ? extends ThrowableResult<?>>> validationBindings,
      TYPE objectToValidate,
      ValidationDescriptor descriptor) {
    List<NoInputValidator<? extends ThrowableResult<?>>> noInputValidators = new ArrayList<>();

    for (Validator<TYPE, ? extends ThrowableResult<?>> binding : validationBindings) {
      NoInputValidator<? extends ThrowableResult<?>> validator =
          (NoInputValidator) () -> binding.validate(objectToValidate);
      noInputValidators.add(validator);
    }

    return validate(noInputValidators, descriptor);
  }
}
