package io.github.jonasnuber.valari.core.i18n;

import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.core.i18n.internal.ResourceBundleCache;

import java.text.MessageFormat;
import java.util.*;

/**
 * A {@link MessageResolver} implementation that resolves validation messages using Java's {@link
 * ResourceBundle} mechanism.
 *
 * <p>This resolver loads message patterns from resource bundles identified by a configurable {@code
 * baseName}. Messages are resolved using the provided {@link Locale} and formatted with {@link
 * MessageFormat} if parameter values are supplied.
 *
 * <h2>Message Resolution Workflow</h2>
 *
 * <ol>
 *   <li>The {@link ResourceBundle} for the given locale is retrieved using an internal {@link
 *       ResourceBundleCache}.
 *   <li>The message pattern for the given key is looked up in the bundle.
 *   <li>If found, the pattern is formatted using the supplied arguments.
 *   <li>If no bundle entry exists for the key:
 *       <ul>
 *         <li>If a {@code defaultMessage} is provided, it is formatted and returned.
 *         <li>Otherwise, the unresolved key itself is returned verbatim.
 *       </ul>
 * </ol>
 *
 * <h2>Caching Behavior</h2>
 *
 * <p>This resolver uses {@link ResourceBundleCache} to store bundles per locale in a thread-safe
 * cache. This minimizes repeated classpath lookups and improves performance for repeated message
 * resolution.
 *
 * <h2>Fallback Behavior</h2>
 *
 * <ul>
 *   <li>If a key does not exist in the bundle, the resolver returns the formatted {@code
 *       defaultMessage}, if supplied.
 *   <li>If no default message is given, the key itself is returned unchanged.
 *   <li>This behavior ensures i18n-friendly fallback semantics without throwing exceptions on
 *       missing keys.
 * </ul>
 *
 * <p>This implementation is typically used internally by the validation framework to support
 * internationalized validation messages.
 *
 * @author Jonas Nuber
 * @see ResourceBundleCache
 * @see MessageResolver
 * @see java.util.ResourceBundle
 * @see java.text.MessageFormat
 */
public class ResourceBundleMessageResolver implements MessageResolver {
  private final ResourceBundleCache cache;

  /**
   * Creates a new {@code ResourceBundleMessageResolver} using the given base name for locating
   * resource bundles.
   *
   * <p>The resolver will use a {@link ResourceBundleCache} without TTL expiration.
   *
   * @param baseName the base name of the resource bundle family
   * @throws NullPointerException if {@code baseName} is {@code null}
   */
  public ResourceBundleMessageResolver(String baseName) {
    this.cache = new ResourceBundleCache(baseName);
  }

  /**
   * Creates a new {@code ResourceBundleMessageResolver} with optional TTL-based caching behavior.
   *
   * @param baseName the base name of the resource bundle family (must not be {@code null})
   * @param ttlEnabled whether cached bundles should expire after a time-to-live duration
   * @param ttlMillis the TTL duration in milliseconds, used only if {@code ttlEnabled} is {@code
   *     true}
   * @throws NullPointerException if {@code baseName} is {@code null}
   */
  public ResourceBundleMessageResolver(String baseName, boolean ttlEnabled, long ttlMillis) {
    this.cache =
        new ResourceBundleCache(
            Objects.requireNonNull(baseName, "Resource baseName must not be null"),
            ttlEnabled,
            ttlMillis);
  }

  /**
   * Resolves a localized message using the configured resource bundles.
   *
   * <p>The resolution process:
   *
   * <ol>
   *   <li>Retrieves the {@link ResourceBundle} for the specified locale.
   *   <li>Attempts to look up the given message key.
   *   <li>If found, formats it using {@link MessageFormat} and supplied arguments.
   *   <li>If not found, returns the formatted {@code defaultMessage} if provided.
   *   <li>If no default message is provided, returns the key itself as a fallback.
   * </ol>
   *
   * <p>Arguments are sanitized to ensure that {@code null} lists result in an empty parameter list.
   *
   * @param key the message key to look up (must not be {@code null})
   * @param args optional arguments to be used during message formatting (may be {@code null})
   * @param defaultMessage an optional fallback pattern to use when the key is missing
   * @param locale the locale used to fetch the resource bundle (must not be {@code null})
   * @return the localized and formatted message; never {@code null}
   * @throws NullPointerException if {@code key} or {@code locale} is {@code null}
   */
  @Override
  public String resolve(String key, List<Object> args, String defaultMessage, Locale locale) {
    Objects.requireNonNull(key, "Message key must not be null");
    Objects.requireNonNull(locale, "Locale must not be null");

    try {
      String pattern = cache.get(locale).getString(key);

      return MessageFormat.format(pattern, sanitizeArguments(args));
    } catch (MissingResourceException e) {
      return defaultMessage != null
          ? MessageFormat.format(defaultMessage, sanitizeArguments(args))
          : key;
    }
  }

  /**
   * Clears the entire resource bundle cache.
   *
   * <p>All cached bundles for all locales will be discarded. Subsequent message resolutions will
   * reload bundles from the classpath.
   */
  public void clearCache() {
    cache.clear();
  }

  /**
   * Clears the cached resource bundle for a specific locale.
   *
   * <p>This allows selectively refreshing one locale without affecting others.
   *
   * @param locale the locale whose cached bundle should be removed
   * @throws NullPointerException if {@code locale} is {@code null}
   */
  public void clearCache(Locale locale) {
    cache.clear(Objects.requireNonNull(locale, "Locale must not be null"));
  }

  private static Object[] sanitizeArguments(List<Object> args) {
    return args != null ? args.toArray() : new Object[0];
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ResourceBundleMessageResolver that = (ResourceBundleMessageResolver) o;
    return Objects.equals(cache, that.cache);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(cache);
  }
}
