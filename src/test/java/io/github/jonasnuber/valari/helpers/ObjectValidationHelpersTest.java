package io.github.jonasnuber.valari.helpers;

import io.github.jonasnuber.valari.BaseTest;
import org.junit.jupiter.api.Test;

class ObjectValidationHelpersTest extends BaseTest {

    @Test
    void notNull_ShouldReturnValidResult_ForNotNullObject() {
        var validation = ObjectValidationHelpers.notNull();

        assertValid(validation, new Object());
    }

    @Test
    void notNull_ShouldReturnInvalidResult_ForNullObject() {
        var validation = ObjectValidationHelpers.notNull();

        assertInvalid(validation, null);
    }

    @Test
    void isEqualTo_ShouldReturnValidResult_ForEqualObjects() {
        var testString = "test";

        var validation = ObjectValidationHelpers.isEqualTo(testString);

        assertValid(validation, "test");
    }

    @Test
    void isEqualTo_ShouldReturnInvalidResult_ForNotEqualObjects() {
        var testString = "test";

        var validation = ObjectValidationHelpers.isEqualTo(testString);

        assertInvalid(validation, "different");
    }
}