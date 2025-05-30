package io.github.jonasnuber.valari.api.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class StringValidationHelpersTest {

    @Test
    void notEmpty_ShouldReturnValidResult_ForNotEmptyString() {
        var value = "notEmpty";
        var validation = StringValidationHelpers.notEmpty();

        var result = validation.test(value);

        assertThat(result.isValid()).isTrue();
    }

    static Stream<String> emptyStringValues() {
        return Stream.of("", null);
    }

    @ParameterizedTest(name = "Test with empty input string: {0}")
    @MethodSource("emptyStringValues")
    void notEmpty_ShouldReturnInvalidResult_ForEmptyString(String emptyValue) {
        var validation = StringValidationHelpers.notEmpty();

        var result = validation.test(emptyValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must not be empty");
    }

    @ParameterizedTest
    @CsvSource(value = {"abcde", "12345", "test1"})
    void exactly_ShouldReturnValidResult_ForStringOfExactLength(String value) {
        var size = 5;
        var validation = StringValidationHelpers.exactly(size);

        var result = validation.test(value);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"abc", "1234", "test"})
    void exactly_ShouldReturnInvalidResult_ForStringOfSmallerLength(String smallerValue) {
        var size = 5;
        var validation = StringValidationHelpers.exactly(size);

        var result = validation.test(smallerValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must have exactly 5 chars");
    }

    @ParameterizedTest
    @CsvSource(value = {"abcdef", "123456", "testing"})
    void exactly_ShouldReturnInvalidResult_ForStringOfGreaterLength(String greaterValue) {
        var size = 5;
        var validation = StringValidationHelpers.exactly(size);

        var result = validation.test(greaterValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must have exactly 5 chars");
    }

    @Test
    void exactly_ShouldThrowException_ForNullString() {
        var validation = StringValidationHelpers.exactly(2);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String must not be null");
    }

    @ParameterizedTest
    @CsvSource(value = {"abcd", "12345", "testing"})
    void moreThan_ShouldReturnValidResult_ForStringOfGreaterLengthThanMin(String greaterValue) {
        var minimum = 3;
        var validation = StringValidationHelpers.moreThan(minimum);

        var result = validation.test(greaterValue);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"abc", "123", "tom"})
    void moreThan_ShouldReturnInvalidResult_ForStringOfSameLengthThanMin(String value) {
        var minimum = 3;
        var validation = StringValidationHelpers.moreThan(minimum);

        var result = validation.test(value);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must have more than 3 chars");
    }

    @ParameterizedTest
    @CsvSource(value = {"ab", "12", "is"})
    void moreThan_ShouldReturnInvalidResult_ForStringOfSmallerLengthThanMin(String smallerValue) {
        var minimum = 3;
        var validation = StringValidationHelpers.moreThan(minimum);

        var result = validation.test(smallerValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must have more than 3 chars");
    }

    @Test
    void moreThan_ShouldThrowException_ForNullString() {
        var validation = StringValidationHelpers.moreThan(2);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String must not be null");
    }

    @ParameterizedTest
    @CsvSource(value = {"abcd", "123", "is"})
    void lessThan_ShouldReturnValidResult_ForStringOfSmallerLengthThanMax(String smallerValue) {
        var maximum = 5;
        var validation = StringValidationHelpers.lessThan(maximum);

        var result = validation.test(smallerValue);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"abcde", "12345", "test1"})
    void lessThan_ShouldReturnInvalidResult_ForStringOfSameLengthAsMax(String value) {
        var maximum = 5;
        var validation = StringValidationHelpers.lessThan(maximum);

        var result = validation.test(value);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must have less than 5 chars");
    }

    @ParameterizedTest
    @CsvSource(value = {"abcdef", "1234567", "test12345"})
    void lessThan_ShouldReturnInvalidResult_ForStringOfGreaterLengthThanMax(String greaterValue) {
        var maximum = 5;
        var validation = StringValidationHelpers.lessThan(maximum);

        var result = validation.test(greaterValue);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must have less than 5 chars");
    }

    @Test
    void lessThan_ShouldThrowException_ForNullString() {
        var validation = StringValidationHelpers.lessThan(2);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String must not be null");
    }

    @ParameterizedTest
    @CsvSource(value = {"abcdef", "1234567", "test12345"})
    void between_ShouldReturnValidResult_ForStringOfLengthSmallerThanMaxAndGreaterThanMin(String value) {
        var minSize = 3;
        var maxSize = 10;
        var validation = StringValidationHelpers.between(minSize, maxSize);

        var result = validation.test(value);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest(name = "Test with input string: \"{0}\" of length {1}")
    @CsvSource({
            "abc, 3",          // String same length as min
            "abcdefghij, 10",  // String same length as max
            "ab, 2",           // String smaller length than min
            "abcdefghijk, 11"  // String greater length than max
    })
    void between_ShouldReturnInvalidResult_ForVariousStringLengths(String value, int length) {
        var minSize = 3;
        var maxSize = 10;
        var validation = StringValidationHelpers.between(minSize, maxSize);

        var result = validation.test(value);

        assertThat(result.isValid()).isFalse();
    }

    @Test
    void between_ShouldThrowException_ForNullString() {
        var validation = StringValidationHelpers.between(2, 10);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String must not be null");
    }

    @ParameterizedTest
    @CsvSource(value = {"this is a test", "testing", "test of the century"})
    void contains_ShouldReturnValidResult_ForContainedString(String sentence) {
        var str = "test";
        var validation = StringValidationHelpers.contains(str);

        var result = validation.test(sentence);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"this is a Test", "Testing", "Test of the century"})
    void contains_ShouldReturnInvalidResult_ForCaseSensitiveString(String sentence) {
        var str = "test";
        var validation = StringValidationHelpers.contains(str);

        var result = validation.test(sentence);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must contain \"test\"");
    }

    @ParameterizedTest
    @CsvSource(value = {"this is a Tesk", "Tesling", "Text of the century"})
    void contains_ShouldReturnInvalidResult_ForNotContainingString(String sentence) {
        var str = "test";
        var validation = StringValidationHelpers.contains(str);

        var result = validation.test(sentence);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must contain \"test\"");
    }

    @Test
    void contains_ShouldThrowException_ForNullString() {
        var validation = StringValidationHelpers.contains("substring");

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String must not be null");
    }

    @Test
    void contains_ShouldThrowException_ForNullSubString() {
        var validation = StringValidationHelpers.contains(null);

        var thrown = catchThrowable(() -> validation.test("some String"));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String which should be contained, must not be null");
    }

    @ParameterizedTest
    @CsvSource(value = {"this is a Test", "TeStIng", "TEST of the century"})
    void containsIgnoreCase_ShouldReturnValidResult_ForCaseInsensitiveString(String sentence) {
        var str = "test";
        var validation = StringValidationHelpers.containsIgnoreCase(str);

        var result = validation.test(sentence);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"this is a Tes", "Tefting", "Terra of the century"})
    void containsIgnoreCase_ShouldReturnInvalidResult_ForNotContainedString(String sentence) {
        var str = "test";
        var validation = StringValidationHelpers.containsIgnoreCase(str);

        var result = validation.test(sentence);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must contain \"test\"");
    }

    @Test
    void containsIgnoreCase_ShouldThrowException_ForNullString() {
        var validation = StringValidationHelpers.containsIgnoreCase("substring");

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String must not be null");
    }

    @Test
    void containsIgnoreCase_ShouldThrowException_ForNullSubString() {
        var validation = StringValidationHelpers.containsIgnoreCase(null);

        var thrown = catchThrowable(() -> validation.test("some String"));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String which should be contained, must not be null");
    }

    @ParameterizedTest
    @CsvSource(value = {"hello1234", "hello2"})
    void regex_ShouldReturnValidResult_ForMatchingRegex(String value) {
        var regex = "hello\\d+";
        var validation = StringValidationHelpers.regex(regex);

        var result = validation.test(value);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"hello", "Some Text", "123456"})
    void regex_ShouldReturnInvalidResult_ForNoneMatchingRegex(String value) {
        var regex = "hello\\d+";
        var validation = StringValidationHelpers.regex(regex);

        var result = validation.test(value);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must fully match regex 'hello\\d+'");
    }

    @Test
    void regex_ShouldThrowException_ForNullString() {
        var validation = StringValidationHelpers.regex("hello\\d+");

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String must not be null");
    }

    @Test
    void regex_ShouldThrowException_ForNullRegex() {
        var validation = StringValidationHelpers.regex(null);

        var thrown = catchThrowable(() -> validation.test("some String"));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Regular Expression must not be null");
    }

    @ParameterizedTest
    @CsvSource(value = {"hello1234", "12hello34", "heft hello1"})
    void containsRegex_ShouldReturnValidResult_ForSubstringMatchingPattern(String value) {
        var regex = "\\d+";
        var validation = StringValidationHelpers.containsRegex(regex);

        var result = validation.test(value);

        assertThat(result.isValid()).isTrue();
    }

    @ParameterizedTest
    @CsvSource(value = {"hello", "no numbers here", "hello world"})
    void containsRegex_ShouldReturnInvalidResult_ForNoSubstringMatchingPattern(String value) {
        var regex = "\\d+";
        var validation = StringValidationHelpers.containsRegex(regex);

        var result = validation.test(value);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("must contain substring matching regex '\\d+'");
    }

    @Test
    void containsRegex_ShouldThrowException_ForNullString() {
        var validation = StringValidationHelpers.containsRegex("\\d+");

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("String must not be null");
    }

    @Test
    void containsRegex_ShouldThrowException_ForNullRegex() {
        var validation = StringValidationHelpers.containsRegex(null);

        var thrown = catchThrowable(() -> validation.test("some String"));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Regular Expression must not be null");
    }
}