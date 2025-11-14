package io.github.jonasnuber.valari.api.exceptions;

import java.io.Serial;

/**
 * Exception thrown when a validation operation produces a single failing {@link
 * io.github.jonasnuber.valari.api.Result}.
 *
 * <p>This exception represents the non-aggregated case where exactly one validation failure
 * occurred. It is typically used by convenience methods such as {@code
 * ValidationResult.throwIfInvalid()} to immediately signal a problem when no further errors need to
 * be accumulated.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * Result result = validator.test(value);
 * result.throwIfInvalid(); // may throw ValidationException
 * }</pre>
 *
 * <p>This exception contains only the formatted error message associated with the single failing
 * Result. If multiple validation failures occur, an {@link AggregatedValidationException} is
 * typically used instead.
 *
 * <p>This exception is unchecked because single-result validation failures represent input or
 * domain rule violations, not programmer errors, and do not require mandatory catch blocks.
 *
 * @author Jonas Nuber
 */
public class ValidationException extends RuntimeException {

  @Serial private static final long serialVersionUID = 2590905136290636270L;

  /**
   * Constructs a new {@code ValidationException} with the specified error message.
   *
   * <p>The message should contain the human-readable explanation of the single validation failure,
   * usually produced by a {@link io.github.jonasnuber.valari.api.i18n.ResultFormatter}.
   *
   * @param errorMessage the error message describing the validation failure; must not be {@code
   *     null}
   */
  public ValidationException(String errorMessage) {
    super(errorMessage);
  }
}
