package io.github.jonasnuber.valari.api.helpers;

import io.github.jonasnuber.valari.BaseTest;
import io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers;
import org.junit.jupiter.api.Test;

class IntegerValidationHelpersTest extends BaseTest {

    @Test
    void sameAmount_ShouldReturnValidResult_ForSameAmount() {
        var exact = 5;

        var validation = IntegerValidationHelpers.sameAmount(exact);

        assertValid(validation, exact);
    }

    @Test
    void sameAmount_ShouldReturnInvalidResult_ForHigherAmount() {
        var exact = 5;

        var validation = IntegerValidationHelpers.sameAmount(exact);

        assertInvalid(validation, exact + 1);
    }

    @Test
    void sameAmount_ShouldReturnInvalidResult_ForLowerAmount() {
        var exact = 5;

        var validation = IntegerValidationHelpers.sameAmount(exact);

        assertInvalid(validation, exact - 1);
    }

    @Test
    void lowerThan_ShouldReturnValidResult_ForLowerAmountThanMax() {
        var max = 10;

        var validation = IntegerValidationHelpers.lowerThan(max);

        assertValid(validation, max - 1);
    }

    @Test
    void lowerThan_ShouldReturnInvalidResult_ForSameAmountThanMax() {
        var max = 10;

        var validation = IntegerValidationHelpers.lowerThan(max);

        assertInvalid(validation, max);
    }

    @Test
    void lowerThan_ShouldReturnInValidResult_ForHigherAmountThanMax() {
        var max = 10;

        var validation = IntegerValidationHelpers.lowerThan(max);

        assertInvalid(validation, max + 1);
    }

    @Test
    void greaterThan_ShouldReturnValidResult_ForGreaterAmountThanMin() {
        var min = 3;

        var validation = IntegerValidationHelpers.greaterThan(min);

        assertValid(validation, min + 1);
    }

    @Test
    void greaterThan_ShouldReturnInvalidResult_ForSameAmountThanMin() {
        var min = 3;

        var validation = IntegerValidationHelpers.greaterThan(min);

        assertInvalid(validation, min);
    }

    @Test
    void greaterThan_ShouldReturnInvalidResult_ForSmallerAmountThanMin() {
        var min = 3;

        var validation = IntegerValidationHelpers.greaterThan(min);

        assertInvalid(validation, min - 1);
    }

    @Test
    void inBetween_ShouldReturnValidResult_ForValueInBetweenMinAndMax() {
        var min = 3;
        var max = 10;

        var validation = IntegerValidationHelpers.inBetween(min, max);

        assertValid(validation, min + 1);
        assertValid(validation, max - 1);
    }

    @Test
    void inBetween_ShouldReturnInvalidResult_ForSameAmountAsMin() {
        var min = 3;
        var max = 10;

        var validation = IntegerValidationHelpers.inBetween(min, max);

        assertInvalid(validation, min);
    }

    @Test
    void inBetween_ShouldReturnInvalidResult_ForSameAmountAsMax() {
        var min = 3;
        var max = 10;

        var validation = IntegerValidationHelpers.inBetween(min, max);

        assertInvalid(validation, max);
    }

    @Test
    void inBetween_ShouldReturnInvalidResult_ForSmallerAmountThanMin() {
        var min = 3;
        var max = 10;

        var validation = IntegerValidationHelpers.inBetween(min, max);

        assertInvalid(validation, min - 1);
    }

    @Test
    void inBetween_ShouldReturnInvalidResult_ForGreaterAmountThanMax() {
        var min = 3;
        var max = 10;

        var validation = IntegerValidationHelpers.inBetween(min, max);

        assertInvalid(validation, max + 1);
    }
}