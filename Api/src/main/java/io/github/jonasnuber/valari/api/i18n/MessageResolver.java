package io.github.jonasnuber.valari.api.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Abstraction for resolving message keys into localized, human-readable messages.
 * <p>
 * Implementations of this interface allow the validation framework to remain
 * independent of any specific internationalization (i18n) technology. For example,
 * a {@code MessageResolver} may be backed by {@link java.util.ResourceBundle},
 * a database, or a custom translation service.
 * </p>
 *
 * <h2>Contract</h2>
 * <ul>
 *   <li>Messages are identified by a unique {@code key}.</li>
 *   <li>Optional {@code args} can be supplied for parameterized formatting
 *       (e.g. {@code {0}, {1}, ...} placeholders).</li>
 *   <li>A {@code defaultMessage} may be provided as a fallback if the key
 *       cannot be resolved.</li>
 *   <li>All lookups are locale-specific, using the given {@link Locale}.</li>
 * </ul>
 *
 * <h2>Default methods</h2>
 * <p>
 * This interface provides several convenience methods that delegate to the main
 * {@link #resolve(String, List, String, Locale)} method:
 * </p>
 * <ul>
 *   <li>Overloads accepting varargs for message arguments.</li>
 *   <li>Overloads that automatically use the current locale from
 *       {@link MessageResolutionContext}.</li>
 *   <li>Overloads that provide a {@code defaultMessage} fallback.</li>
 * </ul>
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * MessageResolver resolver = new ResourceBundleMessageResolver("messages");
 *
 * String msg1 = resolver.resolve("validation.object.notNull");
 * String msg2 = resolver.resolve("validation.object.equalTo", "expectedValue");
 * String msg3 = resolver.resolveOrDefault("missing.key", "Default fallback");
 * }</pre>
 *
 * @author Jonas Nuber
 */
@FunctionalInterface
public interface MessageResolver {

    /**
     * Resolves a message for the given key, arguments, and locale.
     *
     * @param key the message key to resolve (must not be {@code null})
     * @param args optional arguments to be substituted into the resolved message, may be {@code null}
     * @param defaultMessage a fallback message if the key cannot be resolved, may be {@code null}
     * @param locale the target locale (must not be {@code null})
     * @return the resolved and formatted message; never {@code null} but may fall back to
     *         {@code defaultMessage} or the {@code key} itself if no resolution was possible
     */
    String resolve(String key, List<Object> args, String defaultMessage, Locale locale);

    /**
     * Resolves a message for the given key using the current locale
     * from {@link MessageResolutionContext}.
     *
     * @param key the message key
     * @return the resolved message
     */
    default String resolve(String key) {
        return resolve(key, null, null, MessageResolutionContext.getLocale());
    }

    /**
     * Resolves a message for the given key and arguments using the current locale
     * from {@link MessageResolutionContext}.
     *
     * @param key the message key
     * @param args arguments for parameter substitution
     * @return the resolved message
     */
    default String resolve(String key, Object... args) {
        return resolve(key, Arrays.asList(args), null, MessageResolutionContext.getLocale());
    }

    /**
     * Resolves a message for the given key using the current locale,
     * or falls back to the given default message if the key cannot be resolved.
     *
     * @param key the message key
     * @param defaultMessage the fallback message
     * @return the resolved message or the fallback
     */
    default String resolveOrDefault(String key, String defaultMessage) {
        return resolve(key, null, defaultMessage, MessageResolutionContext.getLocale());
    }

    /**
     * Resolves a message for the given key and arguments using the current locale,
     * or falls back to the given default message if the key cannot be resolved.
     *
     * @param key the message key
     * @param defaultMessage the fallback message
     * @param args arguments for parameter substitution
     * @return the resolved message or the fallback
     */
    default String resolveOrDefault(String key, String defaultMessage, Object... args) {
        return resolve(key, Arrays.asList(args), defaultMessage, MessageResolutionContext.getLocale());
    }

    /**
     * Resolves a message for the given key and locale.
     *
     * @param key the message key
     * @param locale the target locale
     * @return the resolved message
     */
    default String resolve(String key, Locale locale) {
        return resolve(key, null, null, locale);
    }

    /**
     * Resolves a message for the given key, arguments, and locale.
     *
     * @param key the message key
     * @param locale the target locale
     * @param args arguments for parameter substitution
     * @return the resolved message
     */
    default String resolve(String key, Locale locale, Object... args) {
        return resolve(key, Arrays.asList(args), null, locale);
    }

    /**
     * Resolves a message for the given key, locale, arguments,
     * and a fallback default message.
     *
     * @param key the message key
     * @param locale the target locale
     * @param defaultMessage the fallback message
     * @param args arguments for parameter substitution
     * @return the resolved message or the fallback
     */
    default String resolveOrDefault(String key, Locale locale, String defaultMessage, Object... args) {
        return resolve(key, Arrays.asList(args), defaultMessage, locale);
    }
}
