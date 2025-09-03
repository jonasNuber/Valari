package io.github.jonasnuber.valari.api.results;

import java.util.Objects;

public final class LabelType {
    private final String name;
    
    private LabelType(String name) {
        this.name = name;
    }
    
    public static final LabelType FIELD = new LabelType("field");
    public static final LabelType PARAMETER = new LabelType("parameter");
    public static final LabelType ATTRIBUTE = new LabelType("attribute");
    public static final LabelType VALUE = new LabelType("value");
    public static final LabelType PROPERTY = new LabelType("property");
    public static final LabelType SUBJECT = new LabelType("subject");
    
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
