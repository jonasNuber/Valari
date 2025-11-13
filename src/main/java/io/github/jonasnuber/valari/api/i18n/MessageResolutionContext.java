package io.github.jonasnuber.valari.api.i18n;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Global context for message resolution and locale handling within the validation framework.
 *
 * <p>This class provides default values for {@link MessageResolver} and {@link Locale} that are
 * used whenever a caller does not explicitly provide them (e.g. through convenience methods on
 * validation results).
 *
 * <h2>Defaults</h2>
 *
 * <ul>
 *   <li>The default resolver is a {@code ResourceBundleMessageResolver} configured to load from a
 *       {@code ValidationMessages} bundle.
 *   <li>The default locale is {@link Locale#ENGLISH}.
 * </ul>
 *
 * <h2>Thread-safety</h2>
 *
 * <p>Internally, this class uses {@link AtomicReference} to store the resolver and locale, making
 * {@link #setResolver(MessageResolver)} and {@link #setLocale(Locale)} safe to call concurrently.
 * The values are stored globally and affect all code that relies on this context.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * // Get current defaults
 * MessageResolver resolver = MessageResolutionContext.getResolver();
 * Locale locale = MessageResolutionContext.getLocale();
 *
 * // Override globally for the entire application
 * MessageResolutionContext.setResolver(new ResourceBundleMessageResolver("customBundle"));
 * MessageResolutionContext.setLocale(Locale.GERMAN);
 *
 * // Resolve a message using current defaults
 * String msg = MessageResolutionContext.resolve("validation.object.notNull", "username");
 * }</pre>
 *
 * <p><strong>Note:</strong> Since this is a global static context, changing the resolver or locale
 * will affect all threads and consumers of this library within the same JVM. For more granular
 * control, consider passing a {@link MessageResolver} and {@link Locale} explicitly instead of
 * relying on this context.
 *
 * @author Jonas Nuber
 */
public final class MessageResolutionContext {
  private static final AtomicReference<MessageResolver> defaultResolver = new AtomicReference<>();
  private static final AtomicReference<Locale> defaultLocale = new AtomicReference<>();
  private static final AtomicReference<ResultFormatter<?>> defaultFormatter =
      new AtomicReference<>();

  private MessageResolutionContext() {
    throw new AssertionError("Global Context class cannot be instantiated");
  }

  public static MessageResolver getResolver() {
    return Objects.requireNonNull(
        defaultResolver.get(),
        "No default MessageResolver configured. Call MessageResolutionContext.setResolver(...) in your application startup.");
  }

  public static Locale getLocale() {
    return Objects.requireNonNull(defaultLocale.get(), "Default locale must not be null");
  }

  @SuppressWarnings("unchecked")
  public static <R> ResultFormatter<R> getFormatter() {
    ResultFormatter<R> f = (ResultFormatter<R>) defaultFormatter.get();
    return Objects.requireNonNull(f,
            "No default ResultFormatter configured. Call MessageResolutionContext.setFormatter(...) in your application startup.");
  }

  /**
   * Sets the global {@link MessageResolver} to use for message lookups.
   *
   * @param resolver the new resolver (must not be {@code null})
   */
  public static void setResolver(MessageResolver resolver) {
    defaultResolver.set(Objects.requireNonNull(resolver));
  }

  /**
   * Sets the global {@link Locale} to use for message lookups.
   *
   * @param locale the new locale (must not be {@code null})
   */
  public static void setLocale(Locale locale) {
    defaultLocale.set(Objects.requireNonNull(locale));
  }

  public static void setFormatter(ResultFormatter<?> formatter) {
    defaultFormatter.set(Objects.requireNonNull(formatter));
  }

  /**
   * Resolves a message using the current global resolver and locale.
   *
   * @param key the message key
   * @param args optional substitution arguments
   * @return the resolved message
   */
  public static String resolve(String key, Object... args) {
    return getResolver().resolve(key, args);
  }
}
