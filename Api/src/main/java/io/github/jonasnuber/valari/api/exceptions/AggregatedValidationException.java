package io.github.jonasnuber.valari.api.exceptions;

import java.io.Serial;

/**
 * Exception representing a failure during validation that contains multiple {@link
 * io.github.jonasnuber.valari.api.Result} errors.
 *
 * <p>This exception is typically created by {@link
 * io.github.jonasnuber.valari.api.AggregatedResult} when validation produces more than one failure.
 * Instead of throwing individual exceptions for each error, all failures are combined into a single
 * exception instance to simplify error handling, logging, and reporting.
 *
 * <h2>Usage</h2>
 *
 * <p>Consumers normally do not instantiate this exception directly. It is thrown by {@code
 * ThrowableResult.throwIfInvalid()} or similar utility methods once all validation steps for an
 * object or request have been evaluated.
 *
 * <pre>{@code
 * AggregatedResult result = validator.validate(user);
 * result.throwIfInvalid(); // may throw AggregatedValidationException
 * }</pre>
 *
 * <h2>Exception Message</h2>
 *
 * <p>The exception message passed to this class should contain:
 *
 * <ul>
 *   <li>a human-readable summary of the number of validation errors, and
 *   <li>a detailed listing of each failing field or value along with its error message.
 * </ul>
 *
 * <p>Formatting of this summary is handled by the caller (usually the validation engine) and may
 * depend on the configured {@link io.github.jonasnuber.valari.api.i18n.ResultFormatter}.
 *
 * <p>This exception is <strong>unchecked</strong> because validation failures typically represent
 * user input errors or domain rule violations rather than programmer errors, and therefore should
 * not require mandatory catch blocks.
 *
 * @author Jonas Nuber
 */
public class AggregatedValidationException extends RuntimeException {

  @Serial private static final long serialVersionUID = 2591362906312346270L;

  /**
   * Constructs a new {@code AggregatedValidationException} with the specified combined error
   * message.
   *
   * <p>The provided message should already contain a fully formatted summary of all validation
   * issues. It is generally produced by {@link io.github.jonasnuber.valari.api.AggregatedResult}
   * using the currently configured message resolver, locale, and formatter.
   *
   * <p>No additional processing of the message is performed by this constructor.
   *
   * @param errorMessage the formatted, human-readable message summarizing all validation errors;
   *     must not be {@code null}
   */
  public AggregatedValidationException(String errorMessage) {
    super(errorMessage);
  }
}
