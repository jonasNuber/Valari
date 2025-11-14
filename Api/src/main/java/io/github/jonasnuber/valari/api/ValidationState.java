package io.github.jonasnuber.valari.api;

/**
 * Represents the outcome of a validation step.
 *
 * <p>A {@code ValidationState} expresses whether a validation rule <em>succeeded</em>,
 * <em>failed</em>, or was <em>intentionally skipped</em>. It serves as the core status indicator
 * for {@link Result} and related types within the validation framework.
 *
 * <h2>Defined States</h2>
 *
 * <ul>
 *   <li>{@link #SUCCESS} – The validation completed normally and the value is considered valid.
 *   <li>{@link #SKIPPED} – The validation was intentionally not executed, for example due to a
 *       conditional rule or short-circuit logic. Skipped validations are still treated as valid.
 *   <li>{@link #FAILURE} – The validation failed and the value is considered invalid.
 * </ul>
 *
 * <h2>Validity Classification</h2>
 *
 * Each state carries an internal {@code boolean} flag indicating whether it should be interpreted
 * as valid. By convention:
 *
 * <ul>
 *   <li>{@code SUCCESS} → valid
 *   <li>{@code SKIPPED} → valid
 *   <li>{@code FAILURE} → invalid
 * </ul>
 *
 * Use {@link #isValid()} or {@link #isInvalid()} as convenience helpers to inspect this flag.
 *
 * <p>These methods allow higher-level result types (such as aggregated or throwable results) to
 * classify their overall state based on multiple {@code ValidationState} entries.
 *
 * @author Jonas Nuber
 * @since 1.0
 */
public enum ValidationState {

  /**
   * The validation completed successfully.
   *
   * <p>This indicates that the value meets the validation criteria.
   */
  SUCCESS(true),

  /**
   * The validation was intentionally bypassed.
   *
   * <p>Skipped rules do not contribute negatively to an aggregated validation result and are
   * treated as valid. Strategies may use this state for conditional or optional rules.
   */
  SKIPPED(true),

  /**
   * The validation failed.
   *
   * <p>This indicates that the value did not satisfy the validation rule. When aggregated, this
   * state typically marks the entire validation as invalid unless overridden by the evaluation
   * strategy.
   */
  FAILURE(false);

  private final boolean valid;

  ValidationState(boolean valid) {
    this.valid = valid;
  }

  /**
   * Indicates whether this state represents a valid outcome.
   *
   * @return {@code true} if this state is {@link #SUCCESS} or {@link #SKIPPED}, {@code false} if it
   *     is {@link #FAILURE}
   */
  public boolean isValid() {
    return valid;
  }

  /**
   * Indicates whether this state represents an invalid outcome.
   *
   * @return {@code true} if this state is {@link #FAILURE}, {@code false} otherwise
   */
  public boolean isInvalid() {
    return !valid;
  }
}
