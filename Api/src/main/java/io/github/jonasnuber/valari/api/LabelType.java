package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents the semantic type of a label used in validation results.
 *
 * <p>A {@code LabelType} provides contextual information about <em>what</em> a validation result
 * refers to, such as a field, method parameter, property, or generic value. Label types are used to
 * generate human-readable messages:
 *
 * <pre>
 * "The field 'username' is invalid"
 * "The parameter 'age' must not be negative"
 * </pre>
 *
 * <h2>Predefined label types</h2>
 *
 * The library provides common built-in label types:
 *
 * <ul>
 *   <li>{@link #FIELD} – For object fields.
 *   <li>{@link #PARAMETER} – For method or function parameters.
 *   <li>{@link #ATTRIBUTE} – For attributes or metadata values.
 *   <li>{@link #VALUE} – For plain or untyped values.
 *   <li>{@link #PROPERTY} – For bean or configuration properties.
 *   <li>{@link #SUBJECT} – Generic fallback for unspecified subjects.
 * </ul>
 *
 * <h2>Custom label types</h2>
 *
 * <p>Users can create custom label types dynamically using {@link #of(String)}:
 *
 * <pre>{@code
 * LabelType column = LabelType.of("column");
 * }</pre>
 *
 * <h2>Localization</h2>
 *
 * <p>Labels can be localized using a {@link MessageResolver} and a {@link Locale}. The {@link
 * #get()} method automatically uses the globally configured resolver and locale from {@link
 * MessageResolutionContext}.
 *
 * <p>The {@code key} of the label type should ideally correspond to a message entry that the {@link
 * MessageResolver} can resolve. If the key cannot be resolved by the provided resolver, a fallback
 * human-readable label is used, as returned by {@link #defaultLabel()}.
 *
 * <h2>Equality</h2>
 *
 * <p>Two {@code LabelType} instances are considered equal if their internal keys are equal.
 *
 * @author Jonas Nuber
 * @see MessageResolver
 * @see MessageResolutionContext
 */
public final class LabelType {
  private final String key;

  private LabelType(String key) {
    this.key = Objects.requireNonNull(key, "Key might not be null");
  }

  /** Predefined label type for object fields. */
  public static final LabelType FIELD = new LabelType("labeltype.field");

  /** Predefined label type for method or function parameters. */
  public static final LabelType PARAMETER = new LabelType("labeltype.parameter");

  /** Predefined label type for attributes or metadata values. */
  public static final LabelType ATTRIBUTE = new LabelType("labeltype.attribute");

  /** Predefined label type for plain values. */
  public static final LabelType VALUE = new LabelType("labeltype.value");

  /** Predefined label type for bean or configuration properties. */
  public static final LabelType PROPERTY = new LabelType("labeltype.property");

  /** Generic fallback label type when no specific type is provided. */
  public static final LabelType SUBJECT = new LabelType("labeltype.subject");

  /**
   * Creates a custom label type with the given key.
   *
   * @param key the unique key for this label type (must not be {@code null})
   * @return a new {@code LabelType} instance
   */
  public static LabelType of(String key) {
    return new LabelType(Objects.requireNonNull(key, "Key must not be null"));
  }

  /**
   * Returns the localized label using the globally configured {@link MessageResolver} and {@link
   * Locale}.
   *
   * @return the localized label
   */
  public String get() {
    return localize(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
  }

  /**
   * Returns the localized label using the specified resolver and locale.
   *
   * @param resolver the resolver to use
   * @param locale the locale to use
   * @return the localized label
   */
  public String localize(MessageResolver resolver, Locale locale) {
    return resolver.resolveOrDefault(key, locale, defaultLabel());
  }

  /**
   * Returns the default human-readable label derived from the key.
   *
   * @return the default label string
   */
  public String defaultLabel() {
    return Character.toUpperCase(key.charAt(0)) + key.substring(1);
  }

  /**
   * Returns the internal key of this label type.
   *
   * @return the label key
   */
  public String getKey() {
    return key;
  }

  @Override
  public String toString() {
    return get();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    LabelType labelType = (LabelType) o;
    return Objects.equals(key, labelType.key);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(key);
  }
}
