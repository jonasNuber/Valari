package io.github.jonasnuber.valari.api.i18n;

import java.text.MessageFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A {@link MessageResolver} implementation that resolves validation messages
 * using Java's {@link ResourceBundle} mechanism.
 * <p>
 * This class looks up message patterns from resource bundles based on a
 * configurable {@code baseName} and the given {@link Locale}. Resolved
 * messages can include parameter placeholders (e.g. {@code {0}, {1}, ...}),
 * which are formatted using {@link MessageFormat}.
 * </p>
 *
 * <h2>Caching</h2>
 * <p>
 * Resolved {@link ResourceBundle} instances are cached per locale in a
 * thread-safe {@link ConcurrentMap} to improve performance and avoid
 * repeatedly loading the same bundles.
 * </p>
 *
 * <h2>Fallback behavior</h2>
 * <ul>
 *   <li>If the message key cannot be found in the resource bundle,
 *       the {@code defaultMessage} will be returned if provided.</li>
 *   <li>If no default message is given, the unresolved message key itself
 *       will be returned.</li>
 * </ul>
 *
 * @author Jonas Nuber
 */
public class ResourceBundleMessageResolver implements MessageResolver {
    private final ConcurrentMap<Locale, ResourceBundle> cache = new ConcurrentHashMap<>();
    private final String baseName;

    /**
     * Creates a new resolver with the given resource bundle base name.
     *
     * @param baseName the base name of the resource bundle (must not be {@code null})
     * @throws NullPointerException if {@code baseName} is {@code null}
     */
    public ResourceBundleMessageResolver(String baseName) {
        this.baseName = Objects.requireNonNull(baseName, "Resource baseName must not be null");
    }

    /**
     * Resolves a message for the given key, arguments, and locale.
     * <p>
     * The lookup process works as follows:
     * <ol>
     *   <li>Attempt to load the {@link ResourceBundle} for the given locale
     *       (from cache if already loaded).</li>
     *   <li>Retrieve the message pattern associated with the key.</li>
     *   <li>Format the message with the provided arguments (if any).</li>
     *   <li>If the key is missing, return the formatted {@code defaultMessage}
     *       (if provided), otherwise return the key itself.</li>
     * </ol>
     *
     * @param key the message key to resolve (must not be {@code null})
     * @param args optional arguments used to format the resolved message
     * @param defaultMessage a fallback message pattern if the key is missing (it may be {@code null})
     * @param locale the target locale (must not be {@code null})
     * @return the resolved and formatted message, never {@code null}
     * @throws NullPointerException if {@code key} or {@code locale} is {@code null}
     */
    @Override
    public String resolve(String key, List<Object> args, String defaultMessage, Locale locale) {
        Objects.requireNonNull(key, "Message key must not be null");
        Objects.requireNonNull(locale, "Locale must not be null");

       try {
           ResourceBundle bundle = cache.computeIfAbsent(locale, loc -> ResourceBundle.getBundle(baseName, loc));
           String pattern = bundle.getString(key);

           return MessageFormat.format(pattern, sanitizeArguments(args));
       } catch (MissingResourceException e) {
           return defaultMessage != null
                   ? MessageFormat.format(defaultMessage, sanitizeArguments(args))
                   : key;
       }
    }

    private static Object[] sanitizeArguments(List<Object> args) {
        return args != null ? args.toArray() : new Object[0];
    }
}
