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
        var causeDescription = "error message";
        var result = ValidationResult.fail(causeDescription);

        var isValid = result.isValid();
        var isInvalid = result.isInvalid();

        assertNotValid(isValid);
        assertInvalid(isInvalid);
        assertThat(result.getFieldName()).isEqualTo("Unknown");
    }

    @Test
    void fail_ShouldReturnInvalidResult_WithFieldConstruction(){
        var causeDescription = "error message";
        var fieldName = "fieldName";
        var result = ValidationResult.fail(causeDescription, fieldName);

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
        var causeDescription = "is not valid";
        var result = ValidationResult.fail(causeDescription);

        var thrown = catchThrowable(() -> result.throwIfInvalid("fieldName"));

        assertThat(thrown)
                .isInstanceOf(InvalidAttributeValueException.class)
                .hasMessage(String.format("The field: \"%s\" is invalid: %s", "fieldName", causeDescription));
    }

    @Test
    void throwIfInvalid_ShouldThrowException_ForInvalidResultWithoutFieldName() {
        var causeDescription = "is not valid";
        var result = ValidationResult.fail(causeDescription);

        var thrown = catchThrowable(result::throwIfInvalid);

        assertThat(thrown)
                .isInstanceOf(InvalidAttributeValueException.class)
                .hasMessage(String.format("The field: \"%s\" is invalid: %s", "Unknown", causeDescription));
    }
}