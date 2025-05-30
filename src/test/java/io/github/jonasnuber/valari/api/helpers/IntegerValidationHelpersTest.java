package io.github.jonasnuber.valari.api.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

class IntegerValidationHelpersTest {

    @Test
    void sameAmount_ShouldReturnValidResult_ForSameAmount() {
        var exact = 5;
        var validation = IntegerValidationHelpers.sameAmount(exact);

        var result = validation.test(exact);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"6", "12", "25"})
    void sameAmount_ShouldReturnInvalidResult_ForHigherAmount(int higherValue) {
        var validation = IntegerValidationHelpers.sameAmount(5);

        var result = validation.test(higherValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must equal 5");
    }

    @ParameterizedTest
    @CsvSource(value = {"2", "4", "0"})
    void sameAmount_ShouldReturnInvalidResult_ForLowerAmount(int lowerValue) {
        var validation = IntegerValidationHelpers.sameAmount(5);

        var result = validation.test(lowerValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must equal 5");
    }

    @ParameterizedTest
    @CsvSource(value = {"2", "4", "9"})
    void lowerThan_ShouldReturnValidResult_ForLowerAmountThanMax(int lowerValue) {
        var validation = IntegerValidationHelpers.lowerThan(10);

        var result = validation.test(lowerValue);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void lowerThan_ShouldReturnInvalidResult_ForSameAmountThanMax() {
        var max = 10;
        var validation = IntegerValidationHelpers.lowerThan(max);

        var result = validation.test(max);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be lower than 10");
    }

    @ParameterizedTest
    @CsvSource(value = {"11", "20", "100"})
    void lowerThan_ShouldReturnInValidResult_ForHigherAmountThanMax(int higherValue) {
        var validation = IntegerValidationHelpers.lowerThan(10);

        var result = validation.test(higherValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be lower than 10");
    }

    @ParameterizedTest
    @CsvSource(value = {"4", "42", "69"})
    void greaterThan_ShouldReturnValidResult_ForGreaterAmountThanMin(int greaterValue) {
        var validation = IntegerValidationHelpers.greaterThan(3);

        var result = validation.test(greaterValue);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void greaterThan_ShouldReturnInvalidResult_ForSameAmountThanMin() {
        var min = 3;
        var validation = IntegerValidationHelpers.greaterThan(min);

        var result = validation.test(min);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be greater than 3");
    }

    @ParameterizedTest
    @CsvSource(value = {"0", "1", "2"})
    void greaterThan_ShouldReturnInvalidResult_ForSmallerAmountThanMin(int smallerValue) {
        var validation = IntegerValidationHelpers.greaterThan(3);

        var result = validation.test(smallerValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be greater than 3");
    }

    @ParameterizedTest
    @CsvSource(value = {"4", "9", "6"})
    void inBetween_ShouldReturnValidResult_ForValueInBetweenMinAndMax(int value) {
        var validation = IntegerValidationHelpers.inBetween(3, 10);

        var result = validation.test(value);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void inBetween_ShouldReturnInvalidResult_ForSameAmountAsMin() {
        var min = 3;
        var max = 10;
        var validation = IntegerValidationHelpers.inBetween(min, max);

        var result = validation.test(min);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be greater than 3");
    }

    @Test
    void inBetween_ShouldReturnInvalidResult_ForSameAmountAsMax() {
        var min = 3;
        var max = 10;
        var validation = IntegerValidationHelpers.inBetween(min, max);

        var result = validation.test(max);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be lower than 10");
    }

    @ParameterizedTest
    @CsvSource(value = {"0", "1", "2"})
    void inBetween_ShouldReturnInvalidResult_ForSmallerAmountThanMin(int smallerValue) {
        var validation = IntegerValidationHelpers.inBetween(3, 10);

        var result = validation.test(smallerValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be greater than 3");
    }

    @ParameterizedTest
    @CsvSource(value = {"11", "13", "23"})
    void inBetween_ShouldReturnInvalidResult_ForGreaterAmountThanMax(int greaterValue) {
        var validation = IntegerValidationHelpers.inBetween(3, 10);

        var result = validation.test(greaterValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must be lower than 10");
    }
}