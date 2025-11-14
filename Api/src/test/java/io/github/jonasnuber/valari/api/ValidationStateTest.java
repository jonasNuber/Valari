package io.github.jonasnuber.valari.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ValidationStateTest {

  @Test
  void success_isValid() {
    var success = ValidationState.SUCCESS;

    assertThat(success.isValid()).isTrue();
    assertThat(success.isInvalid()).isFalse();
  }

  @Test
  void skipped_isValid() {
    var skipped = ValidationState.SKIPPED;

    assertThat(skipped.isValid()).isTrue();
    assertThat(skipped.isInvalid()).isFalse();
  }

  @Test
  void failure_isInvalid() {
    var failure = ValidationState.FAILURE;

    assertThat(failure.isValid()).isFalse();
    assertThat(failure.isInvalid()).isTrue();
  }
}
