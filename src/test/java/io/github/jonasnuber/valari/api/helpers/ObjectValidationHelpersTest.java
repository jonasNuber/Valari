package io.github.jonasnuber.valari.api.helpers;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ObjectValidationHelpersTest {

    @Test
    void notNull_ShouldReturnValidResult_ForNotNullObject() {
        var validation = ObjectValidationHelpers.notNull();

        var result = validation.test(new Object());

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void notNull_ShouldReturnInvalidResult_ForNullObject() {
        var validation = ObjectValidationHelpers.notNull();

        var result = validation.test(null);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must not be null");
    }

    @Test
    void isEqualTo_ShouldReturnValidResult_ForEqualObjects() {
        var testString = "test";
        var validation = ObjectValidationHelpers.isEqualTo(testString);

        var result = validation.test("test");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void isEqualTo_ShouldReturnInvalidResult_ForNotEqualObjects() {
        var testString = "test";
        var validation = ObjectValidationHelpers.isEqualTo(testString);

        var result = validation.test("different");

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be equal to \"test\"");
    }

    @Test
    void isEqualTo_ShouldThrowException_ForNullObject() {
        var other = new Object();
        var validation = ObjectValidationHelpers.isEqualTo(other);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object must not be null");
    }

    @Test
    void isEqualTo_ShouldThrowException_ForNullToEqualObject() {
        var validation = ObjectValidationHelpers.isEqualTo(null);

        var thrown = catchThrowable(() -> validation.test(new Object()));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object to equal must not be null");
    }
}