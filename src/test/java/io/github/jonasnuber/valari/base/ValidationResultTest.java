package io.github.jonasnuber.valari.base;

import io.github.jonasnuber.valari.BaseTest;
import io.github.jonasnuber.valari.exceptions.InvalidAttributeValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;

class ValidationResultTest extends BaseTest {

    @Test
    void ok_ShouldReturnValidResult() {
        var result = ValidationResult.ok();

        var isValid = result.isValid();
        var isInvalid = result.isInvalid();

        assertValid(isValid);
        assertNotInvalid(isInvalid);
    }

    @Test
    void fail_ShouldReturnInvalidResult() {
        var errorMessage = "error message";
        var result = ValidationResult.fail(errorMessage);

        var isValid = result.isValid();
        var isInvalid = result.isInvalid();

        assertNotValid(isValid);
        assertInvalid(isInvalid);
    }

    @Test
    void throwIfInvalid_ShouldNotThrowException_ForValidResult() {
        var result = ValidationResult.ok();

        ThrowingCallable executable = () -> result.throwIfInvalid("fieldName");

        assertThatCode(executable).doesNotThrowAnyException();
    }

    @Test
    void throwIfInvalid_ShouldThrowException_ForInvalidResult() {
        var errorMessage = "is not valid";
        var result = ValidationResult.fail(errorMessage);

        var thrown = catchThrowable(() -> result.throwIfInvalid("fieldName"));

        assertThat(thrown)
                .isInstanceOf(InvalidAttributeValueException.class)
                .hasMessage(String.format("The field: \"%s\" is invalid, because %s", "fieldName", errorMessage));
    }
}