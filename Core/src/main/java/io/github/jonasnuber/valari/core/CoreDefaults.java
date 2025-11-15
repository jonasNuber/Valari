package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;

import java.util.Locale;

/**
 * Provides library-wide default configuration for the Valari core module.
 * <p>
 * This class centralizes bootstrapping of common infrastructure such as message resolution,
 * locale handling, and formatting. Calling {@link #initializeDefaults()} installs a standard
 * configuration that is suitable for most applications and ensures consistent behavior
 * out of the box.
 * </p>
 *
 * <h2>Installed Defaults</h2>
 * <ul>
 *   <li>{@code MessageResolver}: a {@code ResourceBundleMessageResolver} using the base name
 *       {@code "ValidationMessages"}</li>
 *   <li>{@code Locale}: {@link Locale#ENGLISH}</li>
 *   <li>{@code ResultFormatter}: an instance of {@link DefaultResultFormatter}</li>
 * </ul>
 *
 * <h2>Usage</h2>
 * <p>
 * Applications are encouraged to call {@code CoreDefaults.initializeDefaults()} during startup,
 * unless they prefer to install their own custom configuration.
 * </p>
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
 * <p>
 * Any of the installed defaults can later be overridden via:
 * </p>
 * <ul>
 *   <li>{@link MessageResolutionContext#setResolver}</li>
 *   <li>{@link MessageResolutionContext#setLocale}</li>
 *   <li>{@link MessageResolutionContext#setFormatter}</li>
 * </ul>
 *
 * <h2>Design Notes</h2>
 * <p>
 * This class cannot be instantiated; it is intended solely as a bootstrap utility.
 * </p>
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
   * Initializes the global default configuration for message resolution, locale selection,
   * and result formatting.
   * <p>
   * The following defaults are applied:
   * </p>
   * <ul>
   *   <li>A {@code ResourceBundleMessageResolver} bound to {@code "ValidationMessages"}</li>
   *   <li>{@link Locale#ENGLISH} as the default locale</li>
   *   <li>{@link DefaultResultFormatter} as the global result formatter</li>
   * </ul>
   *
   * <p>
   * This method is idempotent and may be safely called multiple times, although repeated
   * calls will re-install the same configuration.
   * </p>
   */
  public static void initializeDefaults() {
    MessageResolutionContext.setResolver(new ResourceBundleMessageResolver("ValidationMessages"));
    MessageResolutionContext.setLocale(Locale.ENGLISH);
    MessageResolutionContext.setFormatter(new DefaultResultFormatter());
  }
}
