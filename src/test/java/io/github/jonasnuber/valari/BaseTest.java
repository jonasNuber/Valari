package io.github.jonasnuber.valari;


import io.github.jonasnuber.valari.spi.Validation;

import static org.assertj.core.api.Assertions.assertThat;

public class BaseTest {

    protected <K> void assertValid(Validation<K> validation, K value) {
        assertThat(validation.test(value).isValid()).isTrue();
    }

    protected void assertValid(boolean isValid){
        assertThat(isValid).isTrue();
    }

    protected void assertNotValid(boolean isValid) {assertThat(isValid).isFalse(); }

    protected <K> void assertInvalid(Validation<K> validation, K value) {
        assertThat(validation.test(value).isInvalid()).isTrue();
    }

    protected void assertInvalid(boolean isInvalid){
        assertThat(isInvalid).isTrue();
    }

    protected void assertNotInvalid(boolean isInvalid) { assertThat(isInvalid).isFalse(); }
}
