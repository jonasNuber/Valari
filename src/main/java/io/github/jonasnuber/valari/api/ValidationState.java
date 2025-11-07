package io.github.jonasnuber.valari.api;

/**
 * Represents the outcome of a validation.
 * <p>
 * A {@code ValidationState} captures whether a validation succeeded,
 * failed, or was intentionally skipped. It is the core status indicator
 * for {@link ValidationResult}
 * and related types.
 *
 * <h2>States</h2>
 * <ul>
 *   <li>{@link #SUCCESS} – The validation passed successfully and the
 *       input is considered valid.</li>
 *   <li>{@link #SKIPPED} – The validation was deliberately bypassed,
 *       e.g. due to conditional rules. Skipped validations are still
 *       considered valid for aggregation purposes.</li>
 *   <li>{@link #FAILURE} – The validation failed and the input is
 *       considered invalid.</li>
 * </ul>
 *
 * <h2>Validity helpers</h2>
 * Each state carries a boolean flag indicating whether it is considered
 * valid. By convention:
 * <ul>
 *   <li>{@code SUCCESS} → valid</li>
 *   <li>{@code SKIPPED} → valid</li>
 *   <li>{@code FAILURE} → invalid</li>
 * </ul>
 *
 * The methods {@link #isValid()} and {@link #isInvalid()} provide a
 * convenient way to test this.
 *
 * @author Jonas Nuber
 * @since 1.0
 */
public enum ValidationState {

    /**
     * Validation succeeded. Input is valid.
     */
    SUCCESS(true),

    /**
     * Validation was skipped but is still considered valid in aggregate.
     */
    SKIPPED(true),

    /**
     * Validation failed. Input is invalid.
     */
    FAILURE(false);

    private final boolean valid;

    ValidationState(boolean valid) {
        this.valid = valid;
    }

    /**
     * @return {@code true} if this state represents a valid outcome
     *         ({@link #SUCCESS} or {@link #SKIPPED}), {@code false} otherwise
     */
    public boolean isValid() {
        return valid;
    }

    /**
     * @return {@code true} if this state represents an invalid outcome
     *         ({@link #FAILURE}), {@code false} otherwise
     */
    public boolean isInvalid() {
        return !valid;
    }
}
