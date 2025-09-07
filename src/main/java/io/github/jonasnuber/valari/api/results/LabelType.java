package io.github.jonasnuber.valari.api.results;

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
    private final String name;
    
    private LabelType(String name) {
        this.name = name;
    }

    /** Predefined label type for object fields. */
    public static final LabelType FIELD = new LabelType("field");

    /** Predefined label type for method or function parameters. */
    public static final LabelType PARAMETER = new LabelType("parameter");

    /** Predefined label type for attributes or metadata values. */
    public static final LabelType ATTRIBUTE = new LabelType("attribute");

    /** Predefined label type for plain values. */
    public static final LabelType VALUE = new LabelType("value");

    /** Predefined label type for bean or configuration properties. */
    public static final LabelType PROPERTY = new LabelType("property");

    /** Generic fallback label type when no specific type is provided. */
    public static final LabelType SUBJECT = new LabelType("subject");

    /**
     * Creates a custom {@code LabelType} with the given name.
     *
     * @param name the name for the custom label type
     * @return a new {@code LabelType} instance
     */
    public static LabelType of(String name) {
        return new LabelType(name);
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        LabelType labelType = (LabelType) o;
        return Objects.equals(name, labelType.name);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
