package io.github.jonasnuber.valari.core.validations;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;

class ObjectValidationsTest extends BaseValidationTest {

  @Test
  void notNull_ShouldReturnValidResult_ForNotNullObject() {
    var validation = ObjectValidations.notNull();

    var result = validation.test(new Object());

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void notNull_ShouldReturnInvalidResult_ForNullObject() {
    var validation = ObjectValidations.notNull();

    var result = validation.test(null);

    assertThat(result.isValid()).isFalse();
    assertThat(getMessage(result.getMetadata())).isEqualTo("must not be null");
  }

  @Test
  void isEqualTo_ShouldReturnValidResult_ForEqualObjects() {
    var testString = "test";
    var validation = ObjectValidations.isEqualTo(testString);

    var result = validation.test("test");

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void isEqualTo_ShouldReturnInvalidResult_ForNotEqualObjects() {
    var testString = "test";
    var validation = ObjectValidations.isEqualTo(testString);

    var result = validation.test("different");

    assertThat(result.isValid()).isFalse();
    assertThat(getMessage(result.getMetadata())).isEqualTo("must be equal to \"test\"");
  }

  @Test
  void isEqualTo_ShouldThrowException_ForNullObject() {
    var other = new Object();
    var validation = ObjectValidations.isEqualTo(other);

    var thrown = catchThrowable(() -> validation.test(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Object must not be null");
  }

  @Test
  void isEqualTo_ShouldThrowException_ForNullToEqualObject() {
    var thrown = catchThrowable(() -> ObjectValidations.isEqualTo(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Object to equal must not be null");
  }
}
