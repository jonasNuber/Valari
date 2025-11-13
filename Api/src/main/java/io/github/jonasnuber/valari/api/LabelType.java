package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;

import java.util.Locale;
import java.util.Objects;

/**
 * Represents the semantic type of label used in validation results.
 * <p>
 * A {@code LabelType} provides context for identifying <em>what</em>
 * a validation result refers to, such as a field, parameter, or property.
 * This helps generate meaningful, human-readable messages:
 * <pre>
 * "The field 'username' is invalid"
 * "The parameter 'age' must not be negative"
 * </pre>
 * </p>
 *
 * <h2>Predefined label types</h2>
 * The following constants are available out of the box:
 * <ul>
 *   <li>{@link #FIELD} – For validation of object fields.</li>
 *   <li>{@link #PARAMETER} – For validation of method or function parameters.</li>
 *   <li>{@link #ATTRIBUTE} – For validation of attributes or metadata values.</li>
 *   <li>{@link #VALUE} – For validation of plain values.</li>
 *   <li>{@link #PROPERTY} – For validation of properties (e.g. bean properties).</li>
 *   <li>{@link #SUBJECT} – Generic fallback for unspecified subjects.</li>
 * </ul>
 *
 * <h2>Custom label types</h2>
 * <p>
 * Additional label types can be created dynamically using {@link #of(String)}:
 * </p>
 * <pre>{@code
 * LabelType column = LabelType.of("column");
 * }</pre>
 *
 * <h2>Equality</h2>
 * <p>
 * Two {@code LabelType} instances are considered equal if their names are equal.
 * </p>
 *
 * @author Jonas Nuber
 */
public final class LabelType {
    private final String key;
    
    private LabelType(String key) {
        this.key = Objects.requireNonNull(key ,"Key might not be null");
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

    public static LabelType of(String key) {
        return new LabelType(Objects.requireNonNull(key, "Key must not be null"));
    }

    public String get() {
        return localize(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }

    public String localize(MessageResolver resolver, Locale locale) {
        return resolver.resolveOrDefault(key, locale, defaultLabel());
    }

    public String defaultLabel() {
        return Character.toUpperCase(key.charAt(0)) + key.substring(1);
    }

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
