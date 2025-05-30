package io.github.jonasnuber.valari.internal;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SimpleValidationTest {

    private static SimpleValidation<Integer> validation;

    @BeforeAll
    static void init() {
        validation = SimpleValidation.from(i -> i == 2, "error");
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