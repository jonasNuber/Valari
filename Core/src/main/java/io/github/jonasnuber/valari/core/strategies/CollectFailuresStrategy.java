package io.github.jonasnuber.valari.core.strategies;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.core.ValidationResultCollection;
import java.util.List;
import java.util.Objects;

/**
 * A {@link ValidationStrategy} that evaluates all provided validators and aggregates every
 * resulting {@link ThrowableResult}—including successes, skips, and failures— into a single {@link
 * ValidationResultCollection}.
 *
 * <p>Unlike short-circuiting strategies, this implementation always executes all validators. Its
 * primary purpose is to produce a comprehensive set of validation results for the validated object,
 * making it particularly well-suited for:
 *
 * <ul>
 *   <li>Form validation
 *   <li>Domain model validation with multiple constraints
 *   <li>Batch validation or bulk error reporting
 * </ul>
 *
 * <p>Failed validations are not returned immediately; instead, they are collected and presented
 * together in the final aggregated result. This gives consumers full insight into all problems
 * instead of stopping at the first encountered issue.
 *
 * <h2>Examples</h2>
 *
 * <pre>{@code
 * ValidationStrategy<ValidationResultCollection> strategy = new CollectFailuresStrategy();
 *
 * ValidationResultCollection results = strategy.validate(validators, descriptor);
 *
 * if (results.isInvalid()) {
 *     // inspect all failures for detailed reporting
 * }
 * }</pre>
 *
 * @see ValidationResultCollection
 * @see ValidationStrategy
 * @see NoInputValidator
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public final class CollectFailuresStrategy
    implements ValidationStrategy<ValidationResultCollection> {

  /**
   * Creates a new {@code CollectFailuresStrategy}.
   *
   * <p>This strategy requires no configuration and is typically reused across multiple validation
   * invocations.
   *
   * <p>Use this strategy when you want to ensure that <em>all</em> validation results are evaluated
   * and aggregated, regardless of whether earlier validations fail.
   */
  public CollectFailuresStrategy() {
    // No initialization required
  }

  /**
   * Executes all validators and aggregates their individual results into a {@link
   * ValidationResultCollection}.
   *
   * <p>The returned collection will contain a result entry for each validator, enabling downstream
   * consumers to analyze all validation outcomes.
   *
   * @param validators the list of {@link NoInputValidator} instances to execute (never {@code
   *     null})
   * @param validationDescriptor metadata describing the validation context; included in the final
   *     result (never {@code null})
   * @return a {@link ValidationResultCollection} containing the aggregated results of all provided
   *     validators
   * @throws NullPointerException if {@code validators} or {@code validationDescriptor} is {@code
   *     null}
   */
  @Override
  public ValidationResultCollection validate(
      List<NoInputValidator<? extends ThrowableResult<?>>> validators,
      ValidationDescriptor validationDescriptor) {
    Objects.requireNonNull(validators, "Validations to validate Object by must not be null");
    Objects.requireNonNull(
        validationDescriptor, "The validationDescriptor for the validation must not be null");

    ValidationResultCollection.Builder resultsBuilder =
        new ValidationResultCollection.Builder(validationDescriptor);

    for (NoInputValidator<? extends ThrowableResult<?>> validator : validators) {
      resultsBuilder.add(validator.validate());
    }

    return resultsBuilder.build();
  }
}
