package io.github.jonasnuber.valari.core;

import static io.github.jonasnuber.valari.core.validations.ObjectValidations.notNull;
import static io.github.jonasnuber.valari.core.validations.StringValidations.notEmpty;
import static org.assertj.core.api.Assertions.*;

import io.github.jonasnuber.valari.api.LabelType;
import io.github.jonasnuber.valari.api.exceptions.ValidationException;
import io.github.jonasnuber.valari.core.i18n.CoreDefaults;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class ValueValidatorTest {

  @BeforeAll
  static void init() {
    CoreDefaults.initializeDefaults();
  }

  @Test
  void with_ShouldThrowException_ForNullValidation() {
    var thrown = catchThrowable(() -> ValueValidator.with(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Validation must not be null");
  }

  @Test
  void with_ShouldThrowException_ForNullValueName() {
    var thrown = catchThrowable(() -> ValueValidator.with(null, notEmpty()));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Value Name must not be null");
  }

  @Test
  void with_ShouldSetDefaultValueNameInException_WhenValueNameNotProvided() {
    var validator = ValueValidator.with(notEmpty());

    var thrown = catchThrowable(() -> validator.validate("").throwIfInvalid());

    assertThat(thrown).isInstanceOf(ValidationException.class).hasMessageContaining("Value");
  }

  @Test
  void with_ShouldSetValueNameInException_WhenValueNameProvided() {
    var valueName = "VName";
    var validator = ValueValidator.with(valueName, notEmpty());

    var thrown = catchThrowable(() -> validator.validate("").throwIfInvalid());

    assertThat(thrown).isInstanceOf(ValidationException.class).hasMessageContaining(valueName);
  }

  @Test
  void optional_ShouldThrowException_ForNullValidation() {
    var thrown = catchThrowable(() -> ValueValidator.optional(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Validation must not be null");
  }

  @Test
  void optional_ShouldSetDefaultValueNameInException_WhenValueNameNotProvided() {
    var validator = ValueValidator.optional(notEmpty());

    var thrown = catchThrowable(() -> validator.validate("").throwIfInvalid());

    assertThat(thrown).isInstanceOf(ValidationException.class).hasMessageContaining("Value");
  }

  @Test
  void optional_ShouldSetValueNameInException_WhenValueNameProvided() {
    var valueName = "VName";
    var validator = ValueValidator.optional(valueName, notEmpty());

    var thrown = catchThrowable(() -> validator.validate("").throwIfInvalid());

    assertThat(thrown).isInstanceOf(ValidationException.class).hasMessageContaining(valueName);
  }

  @Test
  void optional_ShouldThrowException_ForNullValueName() {
    var thrown = catchThrowable(() -> ValueValidator.optional(null, notEmpty()));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Value Name must not be null");
  }

  @Test
  void withLabelType_ShouldChangeTheLabelType_ForValidInput() {
    var defaultValidator = ValueValidator.with(notNull());
    var changedValidator = ValueValidator.with(notNull()).withLabelType(LabelType.PARAMETER);

    var defaultResult = defaultValidator.validate(null);
    var changedResult = changedValidator.validate(null);

    assertThat(changedResult.getLabelType()).isNotEqualTo(defaultResult.getLabelType());
    assertThat((String) changedResult.getMessage())
        .isNotEqualTo(defaultResult.getMessage())
        .contains("is invalid");
  }

  @Test
  void withLabelType_ShouldThrowException_ForNullLabelType() {
    var validator = ValueValidator.optional(notEmpty());

    var thrown = catchThrowable(() -> validator.withLabelType(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("LabelType must not be null");
  }

  @Test
  void validate_ShouldReturnValid_WhenValidationPasses() {
    var validator = ValueValidator.with(notEmpty());

    var result = validator.validate("someString");

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void validate_ShouldReturnInvalid_WhenValidationFails() {
    var validator = ValueValidator.with(notEmpty());

    var result = validator.validate("");

    assertThat(result.isInvalid()).isTrue();
  }

  @Test
  void validate_ShouldReturnValid_WhenOptionalAndNullObject() {
    var validator = ValueValidator.optional(notEmpty());

    var result = validator.validate(null);

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void validate_ShouldReturnValid_WhenOptionalAndObjectPresent() {
    var validator = ValueValidator.optional(notEmpty());

    var result = validator.validate("someString");

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void validate_ShouldReturnInvalid_WhenOptionalAndValidationFails() {
    var validator = ValueValidator.optional(notEmpty());

    var result = validator.validate("");

    assertThat(result.isInvalid()).isTrue();
  }

  @Test
  void validateAndThrow_ShouldNotThrowException_WhenOptionalAndNullObject() {
    var validator = ValueValidator.optional(notEmpty());

    ThrowableAssert.ThrowingCallable executable = () -> validator.validate(null).throwIfInvalid();

    assertThatCode(executable).doesNotThrowAnyException();
  }

  @Test
  void validateAndThrow_ShouldNotThrow_WhenValidationPasses() {
    var validator = ValueValidator.with(notEmpty());

    ThrowableAssert.ThrowingCallable validationCode =
        () -> validator.validate("someValue").throwIfInvalid();

    assertThatCode(validationCode).doesNotThrowAnyException();
  }

  @Test
  void validateAndThrow_ShouldNotThrow_WhenOptionalAndValidationPasses() {
    var validator = ValueValidator.optional(notEmpty());

    ThrowableAssert.ThrowingCallable validationCode =
        () -> validator.validate("someValue").throwIfInvalid();

    assertThatCode(validationCode).doesNotThrowAnyException();
  }

  @Test
  void validateAndThrow_ShouldThrowException_WhenValidationFails() {
    var validator = ValueValidator.with(notEmpty());

    var thrown = catchThrowable(() -> validator.validate("").throwIfInvalid());

    assertThat(thrown)
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("is invalid");
  }

  @Test
  void validateAndThrow_ShouldThrowException_WhenOptionalAndValidationFails() {
    var validator = ValueValidator.optional(notEmpty());

    var thrown = catchThrowable(() -> validator.validate("").throwIfInvalid());

    assertThat(thrown)
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("is invalid");
  }
}
