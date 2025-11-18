package io.github.jonasnuber.valari.core.strategies;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.core.ValidationResultCollection;
import java.util.List;
import java.util.Objects;

/**
 * A {@link ValidationStrategy} implementation that aborts validation as soon as the first failing
 * validator is encountered. This behavior is commonly referred to as <em>fail-fast</em>.
 *
 * <p>This strategy is particularly suitable for performance-sensitive validation pipelines where
 * evaluating all validations is unnecessary, such as:
 *
 * <ul>
 *   <li>scenarios where only the first error is relevant to the caller,
 *   <li>when the cost of running validators is high,
 *   <li>or when subsequent validations depend on earlier constraints.
 * </ul>
 *
 * <p>All validators are invoked in the order they appear in the provided list. Once a validator
 * returns an invalid {@link ThrowableResult}, remaining validators are <em>not</em> executed.
 *
 * <p>Even though only a subset of validators may run, this strategy still produces a {@link
 * ValidationResultCollection}, ensuring a consistent result type for all {@link ValidationStrategy}
 * implementations.
 *
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public final class FailFastStrategy implements ValidationStrategy<ValidationResultCollection> {

  /**
   * Creates a new {@code FailFastStrategy}.
   *
   * <p>No configuration is required. The strategy always stops at the first invalid validation
   * result.
   */
  public FailFastStrategy() {
    // No initialization required
  }

  /**
   * Executes the given validators sequentially and terminates as soon as a validation failure
   * occurs.
   *
   * @param validators the ordered list of validators to apply; must not be {@code null}
   * @param validationDescriptor descriptor providing metadata for the validation run; must not be
   *     {@code null}
   * @return a {@link ValidationResultCollection} containing:
   *     <ul>
   *       <li>All valid and the first invalid result, if any validator fails, or
   *       <li>only valid results if all validators succeed
   *     </ul>
   *
   * @throws NullPointerException if {@code validators} or {@code validationDescriptor} is {@code
   *     null}
   * @implNote Validators are executed strictly in the order provided. The collection builder always
   *     includes results of all validators executed up to the point of failure.
   */
  @Override
  public ValidationResultCollection validate(
      List<NoInputValidator<? extends ThrowableResult<?>>> validators,
      ValidationDescriptor validationDescriptor) {
    Objects.requireNonNull(validators, "Validations to validate Object by must not be null");
    Objects.requireNonNull(
        validationDescriptor, "The descriptor of the validation must not be null");

    ValidationResultCollection.Builder resultsBuilder =
        ValidationResultCollection.builder(validationDescriptor);

    for (NoInputValidator<? extends ThrowableResult<?>> validator : validators) {
      ThrowableResult<?> result = validator.validate();
      resultsBuilder.add(result);

      if (result.isInvalid()) {
        return resultsBuilder.build();
      }
    }

    return resultsBuilder.build();
  }
}
