package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;
import static org.assertj.core.api.ThrowableAssert.ThrowingCallable;

class ValidationResultTest{

    @Test
    void ok_ShouldReturnValidResult() {
        var result = ValidationResult.ok();

        var isValid = result.isValid();
        var isInvalid = result.isInvalid();

        assertThat(isValid).isTrue();
        assertThat(isInvalid).isFalse();
    }

    @Test
    void fail_ShouldReturnInvalidResult() {
        var causeDescription = "error message";
        var result = ValidationResult.fail(causeDescription);

        var isValid = result.isValid();
        var isInvalid = result.isInvalid();

        assertThat(isValid).isFalse();
        assertThat(isInvalid).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Unknown");
    }

    @Test
    void fail_ShouldReturnInvalidResult_WithFieldConstruction(){
        var causeDescription = "error message";
        var fieldName = "fieldName";
        var result = ValidationResult.fail(causeDescription, fieldName);

        var isValid = result.isValid();
        var isInvalid = result.isInvalid();

        assertThat(isValid).isFalse();
        assertThat(isInvalid).isTrue();
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