package io.github.jonasnuber.valari.spi;

import io.github.jonasnuber.valari.BaseTest;
import io.github.jonasnuber.valari.api.ValidationResult;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ValidationTest extends BaseTest {

    private static Validation<Object> valid;
    private static Validation<Object> inValid;

    @BeforeAll
    public static void init() {
        valid = (k) -> ValidationResult.ok();
        inValid = (k) -> ValidationResult.fail("Validation failed");
    }

    @Test
    void and_ShouldBeTrue_ForTwoValidValidation() {
        var result = valid.and(valid).test(null);

        var isValid = result.isValid();

        assertValid(isValid);
    }

    @Test
    void and_ShouldBeInvalid_ForTwoInvalidValidations() {
        var result = inValid.and(inValid).test(null);

        var isInvalid = result.isInvalid();

        assertInvalid(isInvalid);
    }

    @Test
    void and_ShouldBeInvalid_ForOneValidAndOneInvalidValidation() {
        var resultValidInvalid = valid.and(inValid).test(null);
        var resultInvalidValid = inValid.and(valid).test(null);

        var validInvalid_isInvalid = resultValidInvalid.isInvalid();
        var invalidValid_isInvalid = resultInvalidValid.isInvalid();

        assertValid(validInvalid_isInvalid);
        assertValid(invalidValid_isInvalid);
    }

    @Test
    void or_ShouldBeValid_ForTwoValidValidations() {
        var result = valid.or(valid).test(null);

        var isValid = result.isValid();

        assertValid(isValid);
    }

    @Test
    void or_ShouldBeInvalid_ForTwoInvalidValidations() {
        var result = inValid.or(inValid).test(null);

        var isInvalid = result.isInvalid();

        assertInvalid(isInvalid);
    }

    @Test
    void or_ShouldBeValid_ForOneInvalidAndOneValidValidation() {
        var resultInvalidValid = inValid.or(valid).test(null);
        var resultValidInvalid = valid.or(inValid).test(null);

        var invalidValid_isValid = resultInvalidValid.isValid();
        var validInvalid_isValid = resultValidInvalid.isValid();

        assertValid(invalidValid_isValid);
        assertValid(validInvalid_isValid);
    }
}