package io.github.jonasnuber.valari.core.i18n;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;

import java.util.Locale;

/**
 * Provides library-wide default configuration for the Valari core module.
 *
 * <p>This class centralizes bootstrapping of common infrastructure such as message resolution,
 * locale handling, and formatting. Calling {@link #initializeDefaults()} installs a standard
 * configuration that is suitable for most applications and ensures consistent behavior out of the
 * box.
 *
 * <h2>Installed Defaults</h2>
 *
 * <ul>
 *   <li>{@code MessageResolver}: a {@code ResourceBundleMessageResolver} using the base name {@code
 *       "ValidationMessages" which has a TTL of 5 Minutes for it's cache before it gets cleared}
 *   <li>{@code Locale}: {@link Locale#ENGLISH}
 *   <li>{@code ResultFormatter}: an instance of {@link DefaultResultFormatter}
 * </ul>
 *
 * <h2>Usage</h2>
 *
 * <p>Applications are encouraged to call {@code CoreDefaults.initializeDefaults()} during startup,
 * unless they prefer to install their own custom configuration.
 *
 * <pre>{@code
 * public class Application {
 *     public static void main(String[] args) {
 *         CoreDefaults.initializeDefaults();
 *         // continue application setup...
 *     }
 * }
 * }</pre>
 *
 * <h2>Customizing Defaults</h2>
 *
 * <p>Any of the installed defaults can later be overridden via:
 *
 * <ul>
 *   <li>{@link MessageResolutionContext#setResolver}
 *   <li>{@link MessageResolutionContext#setLocale}
 *   <li>{@link MessageResolutionContext#setFormatter}
 * </ul>
 *
 * <h2>Design Notes</h2>
 *
 * <p>This class cannot be instantiated; it is intended solely as a bootstrap utility.
 *
 * @see MessageResolutionContext
 * @see ResourceBundleMessageResolver
 * @see DefaultResultFormatter
 */
public class CoreDefaults {
  private CoreDefaults() throws IllegalAccessException {
    throw new IllegalAccessException("Global Bootstrap classes should not be instantiated");
  }

  /**
   * Initializes the global default configuration for message resolution, locale selection, and
   * result formatting.
   *
   * <p>The following defaults are applied:
   *
   * <ul>
   *   <li>A {@link ResourceBundleMessageResolver} bound to {@code "ValidationMessages"} with a
   *       cache TTL of 5 minutes
   *   <li>{@link Locale#ENGLISH} as the default locale
   *   <li>{@link DefaultResultFormatter} as the global result formatter
   * </ul>
   *
   * <p>This method is idempotent and may be safely called multiple times, although repeated calls
   * will re-install the same configuration.
   */
  public static void initializeDefaults() {
    MessageResolutionContext.setResolver(
        new ResourceBundleMessageResolver("ValidationMessages", true, 5 * 60_000));
    MessageResolutionContext.setLocale(Locale.ENGLISH);
    MessageResolutionContext.setFormatter(new DefaultResultFormatter());
  }
}
