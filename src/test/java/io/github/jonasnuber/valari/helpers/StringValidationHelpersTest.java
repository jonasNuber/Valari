package io.github.jonasnuber.valari.helpers;

import io.github.jonasnuber.valari.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

class StringValidationHelpersTest extends BaseTest {

    @Test
    void notEmpty_ShouldReturnValidResult_ForNotEmptyString() {
        var value = "notEmpty";

        var validation = StringValidationHelpers.notEmpty();

        assertValid(validation, value);
    }

    static Stream<String> emptyStringValues() {
        return Stream.of("", null, "     ");
    }

    @ParameterizedTest(name = "Test with empty input string: {0}")
    @MethodSource("emptyStringValues")
    void notEmpty_ShouldReturnInvalidResult_ForEmptyString(String value) {
        var validation = StringValidationHelpers.notEmpty();

        assertInvalid(validation, value);
    }

    @Test
    void exactly_ShouldReturnValidResult_ForStringOfExactLength() {
        var size = 5;

        var validation = StringValidationHelpers.exactly(size);

        assertValid(validation, "abcde");
    }

    @Test
    void exactly_ShouldReturnInvalidResult_ForStringOfSmallerLength() {
        var size = 5;

        var validation = StringValidationHelpers.exactly(size);

        assertInvalid(validation, "abcd");
    }

    @Test
    void exactly_ShouldReturnInvalidResult_ForStringOfGreaterLength() {
        var size = 5;

        var validation = StringValidationHelpers.exactly(size);

        assertInvalid(validation, "abcdef");
    }

    @Test
    void moreThan_ShouldReturnValidResult_ForStringOfGreaterLengthThanMin() {
        var minimum = 3;

        var validation = StringValidationHelpers.moreThan(minimum);

        assertValid(validation, "abcd");
    }

    @Test
    void moreThan_ShouldReturnInvalidResult_ForStringOfSameLengthThanMin() {
        var minimum = 3;

        var validation = StringValidationHelpers.moreThan(minimum);

        assertInvalid(validation, "abc");
    }

    @Test
    void moreThan_ShouldReturnInvalidResult_ForStringOfSmallerLengthThanMin() {
        var minimum = 3;

        var validation = StringValidationHelpers.moreThan(minimum);

        assertInvalid(validation, "ab");
    }

    @Test
    void lessThan_ShouldReturnValidResult_ForStringOfSmallerLengthThanMax() {
        var maximum = 5;

        var validation = StringValidationHelpers.lessThan(maximum);

        assertValid(validation, "abcd");
    }

    @Test
    void lessThan_ShouldReturnInvalidResult_ForStringOfSameLengthAsMax() {
        var maximum = 5;

        var validation = StringValidationHelpers.lessThan(maximum);

        assertInvalid(validation, "abcde");
    }

    @Test
    void lessThan_ShouldReturnInvalidResult_ForStringOfGreaterLengthThanMax() {
        var maximum = 5;

        var validation = StringValidationHelpers.lessThan(maximum);

        assertInvalid(validation, "abcdef");
    }

    @Test
    void between_ShouldReturnValidResult_ForStringOfLengthSmallerThanMaxAndGreaterThanMin() {
        var minSize = 3;
        var maxSize = 10;

        var validation = StringValidationHelpers.between(minSize, maxSize);

        assertValid(validation, "abcd");
        assertValid(validation, "abcdefg");
    }

    @ParameterizedTest(name = "Test with input string: \"{0}\" of length {1}")
    @CsvSource({
            "abc, 3",          // String same length as min
            "abcdefghij, 10",  // String same length as max
            "ab, 2",           // String smaller length than min
            "abcdefghijk, 11"  // String greater length than max
    })
    void between_ShouldReturnInvalidResult_ForVariousStringLengths(String input, int length) {
        var minSize = 3;
        var maxSize = 10;

        var validation = StringValidationHelpers.between(minSize, maxSize);

        assertInvalid(validation, input);
    }

    @Test
    void contains_ShouldReturnValidResult_ForContainedString() {
        var str = "test";

        var validation = StringValidationHelpers.contains(str);

        assertValid(validation, "this is a test");
    }

    @Test
    void contains_ShouldReturnInvalidResult_ForCaseSensitiveString() {
        var str = "test";

        var validation = StringValidationHelpers.contains(str);

        assertInvalid(validation, "this is a Test");
    }

    @Test
    void containsIgnoreCase_ShouldReturnValidResult_ForCaseInsensitiveString() {
        var str = "test";

        var validation = StringValidationHelpers.containsIgnoreCase(str);

        assertValid(validation, "this is a Test");
    }

    @Test
    void containsIgnoreCase_ShouldReturnInvalidResult_ForNotContainedString() {
        var str = "test";

        var validation = StringValidationHelpers.containsIgnoreCase(str);

        assertInvalid(validation, "this is a tes");
    }
}