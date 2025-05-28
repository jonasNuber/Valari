package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.*;

class ValueValidatorTest {

    @Test
    void with_ShouldThrowException_ForNullValidation() {
        var thrown = catchThrowable(() -> ValueValidator.with(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Validation must not be null");
    }

    @Test
    void with_ShouldThrowException_ForNullValueName() {
        var thrown = catchThrowable(() -> ValueValidator.with(notEmpty(), null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Value Name must not be null");
    }

    @Test
    void validate_ShouldThrowException_ForNullObject() {
        var validator = ValueValidator.with(notEmpty());

        var nullToValidate = catchThrowable(() -> validator.validate(null));

        assertThat(nullToValidate)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object to validate must not be null");
    }

    @Test
    void validate_ShouldReturnValid_WhenValidationPasses() {
        var validator = ValueValidator.with(notEmpty());

        var result = validator.validate("someString");

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_ShouldReturnInvalid_WhenValidationFails() {
        var validator = ValueValidator.with(notEmpty());

        var result = validator.validate("");

        assertThat(result.isInvalid()).isTrue();
    }

    @Test
    void validateAndThrow_ShouldThrowException_ForNullObjectToValidate() {
        var validator = ValueValidator.with(notEmpty());

        var thrown = catchThrowable(() -> validator.validateAndThrow(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object to validate must not be null");
    }

    @Test
    void validateAndThrow_ShouldNotThrow_WhenValidationPasses() {
        var validator = ValueValidator.with(notEmpty());

        ThrowableAssert.ThrowingCallable validationCode = () -> validator.validateAndThrow("someValue");

        assertThatCode(validationCode).doesNotThrowAnyException();
    }

    @Test
    void validateAndThrow_ShouldThrowException_WhenValidationFails() {
        var validator = ValueValidator.with(notEmpty());

        var thrown = catchThrowable(() -> validator.validateAndThrow(""));

        assertThat(thrown)
                .isInstanceOf(InvalidAttributeValueException.class)
                .hasMessage("The field: \"Value\" is invalid: must not be blank");
    }
}