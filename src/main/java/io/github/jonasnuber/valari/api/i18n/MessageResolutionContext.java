package io.github.jonasnuber.valari.api.i18n;

import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Global context for message resolution and locale handling within the validation framework.
 * <p>
 * This class provides default values for {@link MessageResolver} and {@link Locale} that are used
 * whenever a caller does not explicitly provide them (e.g. through convenience methods on
 * validation results).
 * </p>
 *
 * <h2>Defaults</h2>
 * <ul>
 *   <li>The default resolver is a {@code ResourceBundleMessageResolver} configured to load
 *       from a {@code ValidationMessages} bundle.</li>
 *   <li>The default locale is {@link Locale#ENGLISH}.</li>
 * </ul>
 *
 * <h2>Thread-safety</h2>
 * <p>
 * Internally, this class uses {@link AtomicReference} to store the resolver and locale,
 * making {@link #setResolver(MessageResolver)} and {@link #setLocale(Locale)} safe to call
 * concurrently. The values are stored globally and affect all code that relies on this context.
 * </p>
 *
 * <h2>Usage</h2>
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
 * </p>
 *
 * @author Jonas Nuber
 */
public final class MessageResolutionContext {
    private static final AtomicReference<MessageResolver> defaultResolver;
    private static final AtomicReference<Locale> defaultLocale;

    static {
        defaultResolver = new AtomicReference<>(new ResourceBundleMessageResolver("ValidationMessages"));
        defaultLocale = new AtomicReference<>(Locale.ENGLISH);
    }

    private MessageResolutionContext() {
        throw new AssertionError("Global Context class cannot be instantiated");
    }

    /**
     * Returns the current global {@link MessageResolver}.
     *
     * @return the resolver, never {@code null}
     */
    public static MessageResolver getResolver() {
        return defaultResolver.get();
    }

    /**
     * Returns the current global {@link Locale}.
     *
     * @return the locale, never {@code null}
     */
    public static Locale getLocale() {
        return defaultLocale.get();
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
