package io.github.jonasnuber.valari.api.i18n;

import io.github.jonasnuber.valari.api.Result;

import java.util.Locale;

/**
 * Strategy interface for converting a {@link Result} into a formatted output of type {@code
 * FORMATTED}. This abstraction allows result formatting to be decoupled from:
 *
 * <ul>
 *   <li>localization concerns handled by {@link MessageResolver}
 *   <li>the currently active locale (via {@link MessageResolutionContext})
 *   <li>presentation formats (e.g. plain text, JSON, HTML, DTOs)
 * </ul>
 *
 * <p>A {@code ResultFormatter} can be plugged into different layers of an application to render
 * validation results appropriately for their context: command line, logs, REST responses, GUIs,
 * etc.
 *
 * <h2>Formatting rules</h2>
 *
 * <ul>
 *   <li>The formatter implementation is responsible for resolving all message keys in the {@link
 *       Result} using {@link MessageResolver}.
 *   <li>The chosen {@link Locale} determines which translations are used.
 *   <li>The output type {@code FORMATTED} is defined by the implementation (e.g. {@code String},
 *       {@code Map<String,Object>}, custom DTO).
 * </ul>
 *
 * <h2>Example usage</h2>
 *
 * <pre>{@code
 * ResultFormatter<String> formatter = new SimpleTextResultFormatter();
 *
 * String formatted = formatter.format(
 *         result,
 *         new ResourceBundleMessageResolver("messages"),
 *         Locale.GERMANY
 * );
 *
 * // or using the default context:
 * String auto = formatter.format(result);
 * }</pre>
 *
 * @param <FORMATTED> the type of the formatted output produced by this formatter
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
@FunctionalInterface
public interface ResultFormatter<FORMATTED> {

  /**
   * Formats the given {@link Result} using the provided message resolver and locale.
   *
   * <p>This is the primary method to implement. All other default methods delegate to it.
   *
   * @param result the result to format (must not be {@code null})
   * @param resolver the message resolver used to localize messages (must not be {@code null})
   * @param locale the locale used during formatting (must not be {@code null})
   * @return the formatted representation of the result (never {@code null})
   */
  FORMATTED format(Result<?> result, MessageResolver resolver, Locale locale);

  /**
   * Formats the given {@link Result} using the current message resolver and locale from {@link
   * MessageResolutionContext}.
   *
   * @param result the result to format
   * @return the formatted representation using the global context
   */
  default FORMATTED format(Result<?> result) {
    return format(
        result, MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
  }
}
