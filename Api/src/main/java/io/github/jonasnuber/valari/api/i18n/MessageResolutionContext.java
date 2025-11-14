package io.github.jonasnuber.valari.api.i18n;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Global context for message handling within the Valari validation framework.
 *
 * <p>This class stores JVM-wide default instances of {@link MessageResolver}, {@link Locale}, and
 * {@link ResultFormatter}. These defaults are used whenever a validation component does not
 * explicitly specify its own resolver, locale, or formatter.
 *
 * <h2>Initialization</h2>
 *
 * <p>Before using any validation features that rely on message resolution, your application must
 * call {@link #setResolver(MessageResolver)}, {@link #setLocale(Locale)}, and {@link
 * #setFormatter(ResultFormatter)} to initialize the global defaults. Until configured, calls to the
 * getter methods will throw an {@link IllegalStateException}.
 *
 * <p>Higher-level integration modules such as <em>valari-core</em> may provide convenience methods
 * to initialize these defaults automatically (e.g., through a framework bootstrap step).
 * Applications depending on the API module alone must configure these defaults manually.
 *
 * <h2>Thread-safety</h2>
 *
 * <p>All operations in this class are thread-safe. Internally, {@link AtomicReference} is used to
 * store the default resolver, locale, and formatter. Updating the global defaults is safe to
 * perform concurrently, but care should be taken: these values are global, and changes affect all
 * consumers and all threads within the same JVM.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * // Application startup
 * MessageResolutionContext.setResolver(new ResourceBundleMessageResolver("ValidationMessages"));
 * MessageResolutionContext.setLocale(Locale.GERMAN);
 * MessageResolutionContext.setFormatter(new DefaultResultFormatter());
 *
 * // During validation
 * String message = MessageResolutionContext.resolve("validation.object.notNull", "username");
 * }</pre>
 *
 * <p><strong>Note:</strong> Because this class manages global mutable state, callers should
 * generally prefer explicitly passing a {@link MessageResolver}, {@link Locale}, or {@link
 * ResultFormatter} when greater isolation or predictability is required.
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

  /**
   * Returns the globally configured {@link MessageResolver}.
   *
   * <p>This method never returns {@code null}. If no resolver has been configured via {@link
   * #setResolver(MessageResolver)}, an {@link IllegalStateException} is thrown. This ensures that
   * message resolution always has an explicit configuration.
   *
   * @return the configured global message resolver
   * @throws IllegalStateException if no resolver has been configured
   */
  public static MessageResolver getResolver() {
    MessageResolver resolver = defaultResolver.get();

    if (resolver == null) {
      throw new IllegalStateException(
          "No default MessageResolver configured. Call MessageResolutionContext.setResolver(...) in your application startup.");
    }

    return resolver;
  }

  /**
   * Returns the globally configured {@link Locale}.
   *
   * <p>If no locale has been set via {@link #setLocale(Locale)}, an {@link IllegalStateException}
   * is thrown. This guarantees that message resolution always uses an explicitly defined locale
   * rather than relying on platform defaults.
   *
   * @return the configured global locale
   * @throws IllegalStateException if no locale has been configured
   */
  public static Locale getLocale() {
    Locale locale = defaultLocale.get();

    if(locale == null) {
      throw new IllegalStateException(
              "No default Locale configured. Call MessageResolutionContext.setLocale(...) in your application startup."
      );
    }

    return locale;
  }

  /**
   * Returns the globally configured {@link ResultFormatter}.
   *
   * <p>The returned formatter is guaranteed to be non-null. If no formatter has been set via {@link
   * #setFormatter(ResultFormatter)}, an {@link IllegalStateException} is thrown. The generic return
   * type ensures callers do not need to cast.
   *
   * @param <R> the result representation type handled by the formatter
   * @return the configured global formatter
   * @throws IllegalStateException if no formatter has been configured
   */
  @SuppressWarnings("unchecked")
  public static <R> ResultFormatter<R> getFormatter() {
    ResultFormatter<R> formatter = (ResultFormatter<R>) defaultFormatter.get();

    if(formatter == null) {
      throw new IllegalStateException(
              "No default ResultFormatter configured. Call MessageResolutionContext.setFormatter(...) in your application startup."
      );
    }

    return formatter;
  }

  /**
   * Sets the global {@link MessageResolver} to use for all message lookups unless a resolver is
   * explicitly provided by a caller.
   *
   * <p>This method is thread-safe and may be called concurrently. The new resolver takes effect
   * immediately and affects all threads within the same JVM.
   *
   * @param resolver the resolver to install; must not be {@code null}
   * @throws NullPointerException if {@code resolver} is {@code null}
   */
  public static void setResolver(MessageResolver resolver) {
    defaultResolver.set(Objects.requireNonNull(resolver, "Resolver must not be null"));
  }

  /**
   * Sets the global {@link Locale} to use for all message resolution operations.
   *
   * <p>The locale determines which localized message variant is selected by the {@link
   * MessageResolver}. The update is thread-safe and becomes effective immediately for all threads.
   *
   * @param locale the locale to install; must not be {@code null}
   * @throws NullPointerException if {@code locale} is {@code null}
   */
  public static void setLocale(Locale locale) {
    defaultLocale.set(Objects.requireNonNull(locale, "Locale must not be null"));
  }

  /**
   * Sets the global {@link ResultFormatter} used to convert validation results into different
   * representations.
   *
   * <p>This method is thread-safe and updates the global formatter immediately. The formatter is
   * used whenever no explicit formatter is supplied by the caller.
   *
   * @param formatter the formatter instance to install; must not be {@code null}
   * @throws NullPointerException if {@code formatter} is {@code null}
   */
  public static void setFormatter(ResultFormatter<?> formatter) {
    defaultFormatter.set(Objects.requireNonNull(formatter, "Formatter must not be null"));
  }

  /**
   * Resolves a message using the globally configured {@link MessageResolver} and {@link Locale}.
   *
   * <p>This is a convenience method equivalent to:
   *
   * <pre>{@code
   * MessageResolutionContext.getResolver().resolve(key, args);
   * }</pre>
   *
   * <p>It is the caller's responsibility to ensure that a default resolver and locale have been
   * configured before invoking this method.
   *
   * @param key the message key to resolve; must not be {@code null}
   * @param args optional substitution arguments for the message
   * @return the resolved message string
   * @throws IllegalStateException if no resolver or locale is configured
   * @throws NullPointerException if {@code key} is {@code null}
   */
  public static String resolve(String key, Object... args) {
    return getResolver().resolve(key, args);
  }

  /**
   * Resets all global context fields to {@code null} for testing purposes.
   *
   * <p>This method is intended strictly for use in unit tests. It allows each test to run with a
   * clean, deterministic global configuration. Production code must never call this method.
   *
   * <p>Because this method is package-private, it is accessible only to test classes placed in the
   * same package.
   */
  static void resetForTests() {
    defaultResolver.set(null);
    defaultLocale.set(null);
    defaultFormatter.set(null);
  }
}
