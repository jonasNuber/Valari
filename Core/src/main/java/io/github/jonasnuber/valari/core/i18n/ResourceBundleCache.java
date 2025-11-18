package io.github.jonasnuber.valari.core.i18n;

import java.util.Locale;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * A thread-safe cache for {@link ResourceBundle} instances keyed by {@link Locale}.
 *
 * <p>This class improves performance for repeated message resolution by avoiding redundant {@code
 * ResourceBundle} loading. It optionally supports time-to-live (TTL) expiration, allowing cached
 * bundles to be refreshed after a specified duration.
 *
 * <h2>Features</h2>
 *
 * <ul>
 *   <li>Thread-safe concurrent caching using {@link ConcurrentHashMap}
 *   <li>Optional TTL-based bundle invalidation
 *   <li>Fast access to cached bundles without repeated lookup overhead
 *   <li>Manual clearing of either all cached bundles or a specific locale
 * </ul>
 *
 * <h2>TTL Behavior</h2>
 *
 * <p>If TTL is enabled, each cached bundle stores a creation timestamp. When retrieving a bundle:
 *
 * <ul>
 *   <li>If no entry exists for the locale, a new bundle is loaded.
 *   <li>If TTL is enabled and the cached bundle is older than {@code ttlMillis}, a new bundle is
 *       loaded and replaces the stale one.
 *   <li>If TTL is disabled, bundles remain cached indefinitely unless cleared manually.
 * </ul>
 *
 * <h2>Usage Example</h2>
 *
 * <pre>{@code
 * ResourceBundleCache cache = new ResourceBundleCache("ValidationMessages", true, 30_000);
 *
 * ResourceBundle bundleEn = cache.get(Locale.ENGLISH);
 * ResourceBundle bundleDe = cache.get(Locale.GERMAN);
 *
 * cache.clear(Locale.ENGLISH); // Drop only EN bundle
 * cache.clear();               // Drop entire cache
 * }</pre>
 *
 * <p>This class is typically used internally by {@code MessageResolver} implementations to optimize
 * message lookup performance.
 *
 * @author Jonas Nuber
 * @see java.util.ResourceBundle
 * @see io.github.jonasnuber.valari.api.i18n.MessageResolver
 */
public class ResourceBundleCache {
  private final ConcurrentMap<Locale, CachedResourceBundle> cache = new ConcurrentHashMap<>();
  private final String baseName;
  private final boolean ttlEnabled;
  private final long ttlMillis;

  /**
   * Creates a {@code ResourceBundleCache} with no TTL expiration.
   *
   * <p>Bundles stored in the cache remain valid indefinitely until explicitly cleared via {@link
   * #clear()} or {@link #clear(Locale)}.
   *
   * @param baseName the base name of the resource bundle family
   * @throws NullPointerException if {@code baseName} is {@code null}
   */
  public ResourceBundleCache(String baseName) {
    this(baseName, false, 0);
  }

  /**
   * Creates a {@code ResourceBundleCache} with optional TTL support.
   *
   * @param baseName the base name of the resource bundle family
   * @param ttlEnabled whether TTL expiration should be applied
   * @param ttlMillis the time-to-live in milliseconds; ignored if {@code ttlEnabled} is {@code
   *     false}
   * @throws NullPointerException if {@code baseName} is {@code null}
   */
  public ResourceBundleCache(String baseName, boolean ttlEnabled, long ttlMillis) {
    this.baseName = Objects.requireNonNull(baseName, "Resource baseName must not be null");
    this.ttlEnabled = ttlEnabled;
    this.ttlMillis = ttlMillis;
  }

  /**
   * Retrieves the {@link ResourceBundle} for the given locale, optionally loading or refreshing it.
   *
   * <p>If TTL is disabled, the bundle is loaded only once per locale and then reused. If TTL is
   * enabled, the cached bundle is replaced once its age exceeds the configured {@code ttlMillis}.
   *
   * <p>This method is thread-safe and guarantees atomic load/refresh behavior per locale.
   *
   * @param locale the locale whose bundle should be retrieved
   * @return the cached or newly loaded {@code ResourceBundle} instance
   * @throws NullPointerException if {@code locale} is {@code null}
   */
  public ResourceBundle get(Locale locale) {
    Objects.requireNonNull(locale, "Locale must not be null");

    CachedResourceBundle cached =
        cache.compute(
            locale,
            (loc, old) -> {
              if (old == null
                  || (ttlEnabled && System.currentTimeMillis() - old.timestamp > ttlMillis)) {
                return new CachedResourceBundle(ResourceBundle.getBundle(baseName, loc));
              }
              return old;
            });

    return cached.bundle;
  }

  /**
   * Clears all cached {@link ResourceBundle} instances.
   *
   * <p>Subsequent calls to {@link #get(Locale)} will reload bundles from the classpath.
   */
  public void clear() {
    cache.clear();
  }

  /**
   * Removes the cached bundle for the specified locale.
   *
   * <p>This is useful when only one locale's bundle should be refreshed, without affecting others.
   *
   * @param locale the locale to clear from the cache
   * @throws NullPointerException if {@code locale} is {@code null}
   */
  public void clear(Locale locale) {
    cache.remove(Objects.requireNonNull(locale, "Locale must not be null"));
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    ResourceBundleCache that = (ResourceBundleCache) o;
    return ttlEnabled == that.ttlEnabled
        && ttlMillis == that.ttlMillis
        && Objects.equals(baseName, that.baseName);
  }

  /**
   * Internal wrapper for storing a {@link ResourceBundle} together with its creation timestamp.
   *
   * <p>This class is used only when TTL is enabled, allowing the cache to determine whether a
   * bundle has expired and requires refreshing.
   */
  @Override
  public int hashCode() {
    return Objects.hash(baseName, ttlEnabled, ttlMillis);
  }

  private static class CachedResourceBundle {
    final ResourceBundle bundle;
    final long timestamp;

    CachedResourceBundle(ResourceBundle bundle) {
      this.bundle = bundle;
      this.timestamp = System.currentTimeMillis();
    }
  }
}
