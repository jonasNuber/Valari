package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.ValidationMetadata;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class SimpleValidationTest {

    private static SimpleValidation<Integer> validation;

    @BeforeAll
    static void init() {
        validation = SimpleValidation.from(i -> i == 2, new ValidationMetadata.Builder("error").build());
    }

    @Test
    void from_ShouldThrowException_ForNullPredicate() {
        var thrown = catchThrowable(() -> SimpleValidation.from(null, null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Predicate must not be null");
    }

    @Test
    void from_ShouldThrowException_ForNullMetadata() {
        var thrown = catchThrowable(() -> SimpleValidation.from(Objects::nonNull, null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ValidationMetadata must not be null");
    }

    @Test
    void test_ShouldReturnValidResult_ForValidInput() {
        var result = validation.test(2);

        var isValid = result.isValid();

        assertThat(isValid).isTrue();
    }

    @Test
    void test_ShouldReturnInvalidResult_ForInvalidInput() {
        var result = validation.test(3);

        var isInvalid = result.isInvalid();

        assertThat(isInvalid).isTrue();
    }
}