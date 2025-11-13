package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.api.i18n.ResultFormatter;

import java.util.Locale;

@SuppressWarnings("java:S119")
public interface Result<SELF extends Result<SELF>> {

  SELF withLabel(LabelType labelType, String label);

  /**
   * Returns the validation state for this result.
   *
   * @return the current {@link ValidationState}.
   */
  ValidationState getState();

  ValidationMetadata getMetadata();

  default <R> R getMessage(ResultFormatter<R> formatter, MessageResolver resolver, Locale locale) {
    return formatter.format(this, resolver, locale);
  }

  default <R> R getMessage(ResultFormatter<R> formatter) {
    return getMessage(
        formatter, MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
  }

  default <R> R getMessage() {
    return getMessage(
        MessageResolutionContext.getFormatter(),
        MessageResolutionContext.getResolver(),
        MessageResolutionContext.getLocale());
  }

  /** Convenience alias for {@link #getMessage()}. */
  default String prettyPrint() {
    return getMessage();
  }

  /**
   * Returns whether this result represents a valid outcome.
   *
   * @return {@code true} if valid, {@code false} otherwise.
   */
  default boolean isValid() {
    return getState().isValid();
  }

  /**
   * Returns whether this result represents an invalid outcome.
   *
   * @return {@code true} if invalid, {@code false} otherwise.
   */
  default boolean isInvalid() {
    return getState().isInvalid();
  }
}
