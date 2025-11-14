package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.exceptions.ValidationException;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.api.i18n.ResultFormatter;

import java.util.Locale;
import java.util.function.Function;

/**
 * A {@link Result} that can automatically throw an exception when representing an invalid state.
 *
 * <p>This interface adds convenience methods for “fail fast” semantics often used in imperative
 * applications, where validation failures should immediately interrupt control flow rather than
 * being accumulated.
 *
 * <h2>Design intention</h2>
 *
 * <p>A {@code ThrowableResult} behaves exactly like a normal {@link Result}, but provides methods
 * such as {@link #throwIfInvalid()} which:
 *
 * <ul>
 *   <li>Check whether the result is invalid.
 *   <li>Format the result message (using the global i18n context unless explicitly overridden).
 *   <li>Throw a runtime exception containing the formatted message.
 * </ul>
 *
 * <p>This makes it ideal for use cases such as request validation in APIs, parameter checks in
 * service layers, or any scenario where a single validation result should immediately trigger an
 * exception.
 *
 * <h2>Exception types</h2>
 *
 * <p>By default, {@link ValidationException} is thrown, but a custom exception may be supplied
 * through {@link #throwIfInvalid(Function)} or the fully configurable {@link
 * #throwIfInvalid(Function, ResultFormatter, MessageResolver, Locale)}.
 *
 * @param <SELF> the concrete subtype, enabling fluent APIs
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public interface ThrowableResult<SELF extends ThrowableResult<SELF>> extends Result<SELF> {

  /**
   * Throws a {@link ValidationException} if this result is invalid.
   *
   * <p>This is the simplest and most common fail-fast mechanism. The message is generated using the
   * global {@link ResultFormatter}, {@link MessageResolver}, and {@link Locale} from {@link
   * io.github.jonasnuber.valari.api.i18n.MessageResolutionContext}.
   *
   * <p>The formatted message is prefixed with a {@link System#lineSeparator()} to improve
   * readability when printed from within stack traces.
   *
   * @throws ValidationException if this result is invalid
   */
  default void throwIfInvalid() throws ValidationException {
    throwIfInvalid(ValidationException::new);
  }

  /**
   * Throws a custom exception if this result is invalid.
   *
   * <p>An exception is created by applying the given factory function to the formatted message of
   * this result. The message is produced using the globally configured formatter, resolver, and
   * locale.
   *
   * <p>The message passed to the factory is prefixed with a newline to improve readability in stack
   * traces.
   *
   * @param exceptionFactory a function mapping the formatted message to a runtime exception
   *     instance; must not be {@code null}
   * @throws RuntimeException if this result is invalid
   */
  default void throwIfInvalid(Function<String, ? extends RuntimeException> exceptionFactory) {
    if (isInvalid()) {
      String message = System.lineSeparator() + getMessage();
      throw exceptionFactory.apply(message);
    }
  }

  /**
   * Throws a custom exception if this result is invalid, using explicitly supplied i18n components
   * for formatting.
   *
   * <p>This is the most configurable form of fail-fast validation, allowing callers to override the
   * formatter, resolver, and locale for this operation only.
   *
   * <p>The message passed to the exception factory is prefixed with a newline to improve stack
   * trace readability.
   *
   * @param exceptionFactory a function converting the formatted message into a runtime exception;
   *     must not be {@code null}
   * @param formatter the formatter used to convert the result into a string
   * @param resolver the resolver responsible for resolving message keys
   * @param locale the locale used during message resolution
   * @throws RuntimeException if this result is invalid
   */
  default void throwIfInvalid(
      Function<String, ? extends RuntimeException> exceptionFactory,
      ResultFormatter<String> formatter,
      MessageResolver resolver,
      Locale locale) {
    if (isInvalid()) {
      String message = System.lineSeparator() + getMessage(formatter, resolver, locale);
      throw exceptionFactory.apply(message);
    }
  }
}
