package io.github.jonasnuber.valari.api.results;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LabelTypeTest {

    @Test
    void of_ShouldCreateNewInstance_ForCustomName() {
        LabelType custom = LabelType.of("custom");

        assertThat(custom)
                .hasToString("custom")
                .isEqualTo(LabelType.of("custom"));
    }

    @Test
    void predefinedConstants_ShouldHaveCorrectNames() {
        assertThat(LabelType.FIELD).hasToString("field");
        assertThat(LabelType.PARAMETER).hasToString("parameter");
        assertThat(LabelType.ATTRIBUTE).hasToString("attribute");
        assertThat(LabelType.VALUE).hasToString("value");
        assertThat(LabelType.PROPERTY).hasToString("property");
        assertThat(LabelType.SUBJECT).hasToString("subject");
    }

    @Test
    void equals_ShouldReturnTrue_ForObjectsWithSameName() {
        LabelType one = LabelType.of("field");
        LabelType two = LabelType.of("field");

        assertThat(one)
                .isEqualTo(two)
                .hasSameHashCodeAs(two);
    }

    @Test
    void equals_ShouldReturnFalse_ForDifferentNames() {
        LabelType one = LabelType.of("field");
        LabelType two = LabelType.of("different");

        assertThat(one).isNotEqualTo(two);
    }

    @Test
    void equals_ShouldReturnFalse_WhenComparingWithNullOrOtherClass() {
        LabelType one = LabelType.of("field");

        assertThat(one)
                .isNotEqualTo(null)
                .isNotEqualTo("field");
    }
}