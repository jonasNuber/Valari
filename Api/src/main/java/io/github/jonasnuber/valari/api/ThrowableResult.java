package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.api.i18n.ResultFormatter;

import java.util.Locale;
import java.util.function.Function;

@SuppressWarnings("java:S119")
public interface ThrowableResult<SELF extends ThrowableResult<SELF>> extends Result<SELF> {

  /**
   * Throws a default runtime exception if this result indicates failure.
   *
   * <p>Implementations typically use a standard exception type (e.g. {@code
   * InvalidAttributeValueException}) for convenience.
   *
   * @throws RuntimeException if the state is invalid.
   */
  default void orThrowIfInvalid() {
    orThrowIfInvalid(InvalidAttributeValueException::new);
  }

  /**
   * Throws a custom exception created by the given factory if this result indicates failure.
   *
   * @param exceptionFactory a function mapping the result message to an exception.
   * @throws RuntimeException if the state is invalid.
   */
  default void orThrowIfInvalid(Function<String, ? extends RuntimeException> exceptionFactory) {
    if (isInvalid()) {
      String message = System.lineSeparator() + getMessage();
      throw exceptionFactory.apply(message);
    }
  }

  default void orThrowIfInvalid(
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
