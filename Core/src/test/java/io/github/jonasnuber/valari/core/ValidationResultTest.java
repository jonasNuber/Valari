package io.github.jonasnuber.valari.core;

import static org.assertj.core.api.Assertions.*;

import io.github.jonasnuber.valari.api.LabelType;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.ValidationState;
import io.github.jonasnuber.valari.api.exceptions.ValidationException;
import io.github.jonasnuber.valari.core.i18n.CoreDefaults;
import io.github.jonasnuber.valari.core.i18n.DefaultResultFormatter;
import io.github.jonasnuber.valari.core.i18n.ResourceBundleMessageResolver;
import java.security.InvalidParameterException;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.provider.Arguments;

class ValidationResultTest {

  @BeforeAll
  static void init() {
    CoreDefaults.initializeDefaults();
  }

  @Test
  void builder_ShouldCreateResultWithAllValues_ForBuildUpInput() {
    var builder =
        ValidationResult.builder("default Message")
            .messageKey("message.key")
            .messageArgument("message Argument")
            .messageArguments("message Argument 2", "message Argument 3")
            .labelType(LabelType.FIELD)
            .label("fieldName")
            .value("Some Value");

    var result = builder.ok();

    assertThat(result.getState()).isEqualTo(ValidationState.SUCCESS);
    assertThat(result.getDefaultMessage()).isEqualTo("default Message");
    assertThat(result.getMessageKey()).isEqualTo("message.key");
    assertThat(result.getMessageArguments())
        .hasSize(3)
        .containsExactly("message Argument", "message Argument 2", "message Argument 3");
    assertThat(result.getLabelType()).isEqualTo(LabelType.FIELD);
    assertThat(result.getLabel()).isEqualTo("fieldName");
    assertThat(result.getValue()).isEqualTo("Some Value");
  }

  @Test
  void builder_ShouldCreateResultWithAllValues_ForMetadataInput() {
    var metadata =
        ValidationMetadata.builder("default Message")
            .messageKey("message.key")
            .messageArgument("message Argument")
            .messageArguments("message Argument 2", "message Argument 3")
            .labelType(LabelType.FIELD)
            .label("fieldName")
            .build();
    var builder = ValidationResult.builder(metadata).value("Some Value");

    var result = builder.ok();

    assertThat(result.getState()).isEqualTo(ValidationState.SUCCESS);
    assertThat(result.getDefaultMessage()).isEqualTo("default Message");
    assertThat(result.getMessageKey()).isEqualTo("message.key");
    assertThat(result.getMessageArguments())
        .hasSize(3)
        .containsExactly("message Argument", "message Argument 2", "message Argument 3");
    assertThat(result.getLabelType()).isEqualTo(LabelType.FIELD);
    assertThat(result.getLabel()).isEqualTo("fieldName");
    assertThat(result.getValue()).isEqualTo("Some Value");
  }

  @Test
  void builder_ok_ShouldReturnSuccessResult() {
    var builder = ValidationResult.builder("default");

    var result = builder.ok();

    assertThat(result.getState()).isEqualTo(ValidationState.SUCCESS);
    assertThat(result.isValid()).isTrue();
  }

  @Test
  void builder_skip_ShouldReturnSkippedResult() {
    var builder = ValidationResult.builder("default");

    var result = builder.skip();

    assertThat(result.getState()).isEqualTo(ValidationState.SKIPPED);
    assertThat(result.isValid()).isTrue();
  }

  @Test
  void builder_fail_ShouldReturnFailureResult() {
    var builder = ValidationResult.builder("default");

    var result = builder.fail();

    assertThat(result.getState()).isEqualTo(ValidationState.FAILURE);
    assertThat(result.isValid()).isFalse();
  }

  @Test
  void builder_ShouldThrowException_ForNullDefaultMessage() {
    var thrown = catchThrowable(() -> ValidationResult.builder((String) null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("DefaultMessage must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullMessageKey() {
    var builder = ValidationResult.builder("default");

    var thrown = catchThrowable(() -> builder.messageKey(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("MessageKey must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullMessageArgument() {
    var builder = ValidationResult.builder("default");

    var thrown = catchThrowable(() -> builder.messageArgument(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("MessageArgument must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForMultipleNullMessageArguments() {
    var builder = ValidationResult.builder("default");

    var thrown = catchThrowable(() -> builder.messageArguments(null, null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("MessageArgument must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullLabelType() {
    var builder = ValidationResult.builder("default");

    var thrown = catchThrowable(() -> builder.labelType(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("LabelType must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullLabel() {
    var builder = ValidationResult.builder("default");

    var thrown = catchThrowable(() -> builder.label(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Label must not be null");
  }

  @Test
  void builder_ShouldNotThrowException_ForNullValue() {
    var builder = ValidationResult.builder("default");

    assertThatCode(() -> builder.value(null)).doesNotThrowAnyException();
  }

  @Test
  void constructor_ShouldNotThrowException_ForFieldsNotSet() {
    var builder = ValidationResult.builder("default");

    assertThatCode(builder::ok).doesNotThrowAnyException();
  }

  @Test
  void withLabel_ShouldReturnNewInstance_WithUpdatedDescriptor() {
    var result = ValidationResult.builder("default").fail();

    var changedResult = result.withLabel(LabelType.FIELD, "fieldName");

    assertThat(result).isNotEqualTo(changedResult);
    assertThat(result.getState()).isEqualTo(changedResult.getState());
    assertThat(changedResult.getLabelType()).isEqualTo(LabelType.FIELD);
    assertThat(changedResult.getLabel()).isEqualTo("fieldName");
  }

  @Test
  void withLabel_ShouldRetainAllOtherParameters() {
    var result =
        ValidationResult.builder("default Message")
            .messageKey("message.key")
            .messageArgument("message Argument")
            .messageArguments("message Argument 2", "message Argument 3")
            .label("fieldName")
            .value("Some Value")
            .ok();

    var changedResult = result.withLabel(LabelType.FIELD, "Other FieldName");

    assertThat(result).isNotEqualTo(changedResult);
    assertThat(changedResult.getState()).isEqualTo(ValidationState.SUCCESS);
    assertThat(changedResult.getDefaultMessage()).isEqualTo("default Message");
    assertThat(changedResult.getMessageKey()).isEqualTo("message.key");
    assertThat(changedResult.getMessageArguments())
        .hasSize(3)
        .containsExactly("message Argument", "message Argument 2", "message Argument 3");
    assertThat(changedResult.getLabelType())
        .isNotEqualTo(LabelType.SUBJECT)
        .isEqualTo(LabelType.FIELD);
    assertThat(changedResult.getLabel()).isNotEqualTo("fieldName").isEqualTo("Other FieldName");
    assertThat(changedResult.getValue()).isEqualTo("Some Value");
  }

  @Test
  void skip_ShouldReturnSkippedResult() {
    var result = ValidationResult.skip();

    assertThat(result.getState()).isEqualTo(ValidationState.SKIPPED);
    assertThat(result.isValid()).isTrue();
  }

  static Stream<Arguments> resultWithAllStates() {
    return Stream.of(
        Arguments.of(
            ValidationResult.builder("default").messageKey("validation.object.notNull").ok()),
        Arguments.of(
            ValidationResult.builder("default").messageKey("validation.object.notNull").skip()),
        Arguments.of(
            ValidationResult.builder("default").messageKey("validation.object.notNull").fail()));
  }

  @Test
  void getMessage_ShouldReturnSuccessMessage_ForSuccessfulResult() {
    var result = ValidationResult.builder("default").messageKey("validation.object.notNull").ok();

    var message = result.getMessage();

    assertThat(message).isEqualTo("The Subject \"<unknown>\" is valid: must not be null\n");
  }

  @Test
  void getMessage_ShouldReturnSkippedMessage_ForSkippedResult() {
    var result = ValidationResult.builder("default").messageKey("validation.object.notNull").skip();

    var message = result.getMessage();

    assertThat(message).isEqualTo("Validation for Subject \"<unknown>\" was skipped\n");
  }

  @Test
  void getMessage_ShouldReturnFailureMessage_ForFailedResult() {
    var result = ValidationResult.builder("default").messageKey("validation.object.notNull").fail();

    var message = result.getMessage();

    assertThat(message).isEqualTo("The Subject \"<unknown>\" is invalid: must not be null\n");
  }

  @Test
  void getMessage_ShouldReturnCorrectMessage_ForCustomFormatterAndResolver() {
    var formatter = new DefaultResultFormatter();
    var resolver = new ResourceBundleMessageResolver("ValidationMessages");
    var locale = Locale.ENGLISH;
    var result = ValidationResult.builder("default").messageKey("validation.object.notNull").fail();

    var message = result.getMessage(formatter, resolver, locale);

    assertThat(message).isEqualTo("The Subject \"<unknown>\" is invalid: must not be null\n");
  }

  @Test
  void throwIfInvalid_ShouldThrowException_ForInvalidResult() {
    var result = ValidationResult.builder("default message").fail();

    var thrown = catchThrowable(result::throwIfInvalid);

    assertThat(thrown)
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("The Subject \"<unknown>\" is invalid: default message");
  }

  @Test
  void throwIfInvalid_ShouldNotThrowException_ForValidResult() {
    var result = ValidationResult.builder("default message").ok();

    assertThatCode(result::throwIfInvalid).doesNotThrowAnyException();
  }

  @Test
  void throwIfInvalid_ShouldNotThrowException_ForSkippedResult() {
    var result = ValidationResult.builder("default message").skip();

    assertThatCode(result::throwIfInvalid).doesNotThrowAnyException();
  }

  @Test
  void throwIfInvalid_ShouldThrowException_ForInvalidResultAndProvidedExceptionType() {
    var result = ValidationResult.builder("default message").fail();

    var thrown = catchThrowable(() -> result.throwIfInvalid(InvalidParameterException::new));

    assertThat(thrown)
        .isInstanceOf(InvalidParameterException.class)
        .hasMessageContaining("The Subject \"<unknown>\" is invalid: default message");
  }
}
