package io.github.jonasnuber.valari.api.i18n;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * Strategy interface for resolving message keys into localized, human-readable text. This
 * abstraction decouples the Valari validation framework from any concrete internationalization
 * technology.
 *
 * <p>Implementations may be backed by:
 *
 * <ul>
 *   <li>{@link java.util.ResourceBundle}-based resolvers
 *   <li>database-backed message catalogs
 *   <li>remote translation services
 *   <li>custom logic for dynamic or computed messages
 * </ul>
 *
 * <h2>General contract</h2>
 *
 * <ul>
 *   <li>Each message is identified by a unique, non-null {@code key}.
 *   <li>Placeholders (e.g., <code>{0}</code>, <code>{1}</code>) MAY appear in the message and will
 *       be replaced using the provided {@code args} list.
 *   <li>If the key cannot be resolved, implementations MUST:
 *       <ul>
 *         <li>use {@code defaultMessage} if provided, otherwise
 *         <li>fallback to the key itself
 *       </ul>
 *   <li>Resolution is locale-specific and MUST use the supplied {@link Locale}.
 * </ul>
 *
 * <h2>Convenience methods</h2>
 *
 * <p>This interface provides several default methods that delegate to the main {@link
 * #resolve(String, List, String, Locale)} method:
 *
 * <ul>
 *   <li>Overloads accepting varargs instead of {@code List<Object>}.
 *   <li>Overloads using the current locale from {@link MessageResolutionContext}.
 *   <li>Overloads that include a {@code defaultMessage} fallback.
 * </ul>
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * MessageResolver resolver = new ResourceBundleMessageResolver("messages");
 *
 * String plain = resolver.resolve("validation.missingField");
 * String withArgs = resolver.resolve("validation.range", minValue, maxValue);
 * String withFallback = resolver.resolveOrDefault("unknown.key", "Fallback text");
 * }</pre>
 *
 * @author Jonas Nuber
 */
@FunctionalInterface
public interface MessageResolver {

  /**
   * Resolves a message for the given key, arguments, and locale.
   *
   * <p>This is the central resolution method, called by all convenience overloads. Implementations
   * should:
   *
   * <ul>
   *   <li>look up the message for the given key
   *   <li>apply placeholder substitution using {@code args}
   *   <li>fallback to {@code defaultMessage} if lookup fails
   *   <li>fallback to the key itself if both lookup and {@code defaultMessage} fail
   * </ul>
   *
   * @param key the message key to resolve (must not be {@code null})
   * @param args optional arguments for placeholder substitution; may be {@code null}
   * @param defaultMessage optional fallback message if the key cannot be resolved; may be {@code
   *     null}
   * @param locale the locale to use for resolution (must not be {@code null})
   * @return the resolved and formatted message; never {@code null}
   */
  String resolve(String key, List<Object> args, String defaultMessage, Locale locale);

  /**
   * Resolves a message for the given key using the current locale from {@link
   * MessageResolutionContext}.
   *
   * @param key the message key
   * @return the resolved message (never {@code null})
   */
  default String resolve(String key) {
    return resolve(key, null, null, MessageResolutionContext.getLocale());
  }

  /**
   * Resolves a message for the given key and arguments using the current locale.
   *
   * @param key the message key
   * @param args arguments for placeholder substitution
   * @return the resolved message (never {@code null})
   */
  default String resolve(String key, Object... args) {
    return resolve(key, Arrays.asList(args), null, MessageResolutionContext.getLocale());
  }

  /**
   * Resolves a message or returns the provided default message if the key cannot be resolved.
   *
   * @param key the message key
   * @param defaultMessage the fallback message to use if lookup fails
   * @return the resolved message or {@code defaultMessage} (never {@code null})
   */
  default String resolveOrDefault(String key, String defaultMessage) {
    return resolve(key, null, defaultMessage, MessageResolutionContext.getLocale());
  }

  /**
   * Resolves a message with arguments or returns the provided default message if the key cannot be
   * resolved.
   *
   * @param key the message key
   * @param defaultMessage the fallback message
   * @param args arguments for placeholder substitution
   * @return the resolved message or {@code defaultMessage} (never {@code null})
   */
  default String resolveOrDefault(String key, String defaultMessage, Object... args) {
    return resolve(key, Arrays.asList(args), defaultMessage, MessageResolutionContext.getLocale());
  }

  /**
   * Resolves a message for the given key using the specified locale.
   *
   * @param key the message key
   * @param locale the target locale
   * @return the resolved message (never {@code null})
   */
  default String resolve(String key, Locale locale) {
    return resolve(key, null, null, locale);
  }

  /**
   * Resolves a message with arguments for the specified locale.
   *
   * @param key the message key
   * @param locale the target locale
   * @param args arguments for placeholder substitution
   * @return the resolved message (never {@code null})
   */
  default String resolve(String key, Locale locale, Object... args) {
    return resolve(key, Arrays.asList(args), null, locale);
  }

  /**
   * Resolves a message or returns the provided default message for the specified locale if the key
   * cannot be resolved.
   *
   * @param key the message key
   * @param locale the target locale
   * @param defaultMessage the fallback message
   * @param args arguments for placeholder substitution
   * @return the resolved message or {@code defaultMessage} (never {@code null})
   */
  default String resolveOrDefault(
      String key, Locale locale, String defaultMessage, Object... args) {
    return resolve(key, Arrays.asList(args), defaultMessage, locale);
  }
}
