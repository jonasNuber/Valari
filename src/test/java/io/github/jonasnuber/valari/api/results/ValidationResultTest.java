package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.security.InvalidParameterException;
import java.util.Locale;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ValidationResultTest{

    @Test
    void builder_ShouldCreateResultWithAllValues_ForBuildUpInput() {
        var builder = new ValidationResult.Builder("default Message")
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
        var metadata = new ValidationMetadata.Builder("default Message")
                .messageKey("message.key")
                .messageArgument("message Argument")
                .messageArguments("message Argument 2", "message Argument 3")
                .labelType(LabelType.FIELD)
                .label("fieldName")
                .build();
        var builder = new ValidationResult.Builder(metadata)
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
    void builder_ok_ShouldReturnSuccessResult() {
        var builder = new ValidationResult.Builder("default");

        var result = builder.ok();

        assertThat(result.getState()).isEqualTo(ValidationState.SUCCESS);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void builder_skip_ShouldReturnSkippedResult() {
        var builder = new ValidationResult.Builder("default");

        var result = builder.skip();

        assertThat(result.getState()).isEqualTo(ValidationState.SKIPPED);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void builder_fail_ShouldReturnFailureResult() {
        var builder = new ValidationResult.Builder("default");

        var result = builder.fail();

        assertThat(result.getState()).isEqualTo(ValidationState.FAILURE);
        assertThat(result.isValid()).isFalse();
    }

    @Test
    void builder_ShouldThrowException_ForNullDefaultMessage() {
        var thrown = catchThrowable(() -> new ValidationResult.Builder((String) null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DefaultMessage must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForNullMessageKey() {
        var builder = new ValidationResult.Builder("default");

        var thrown = catchThrowable(() -> builder.messageKey(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("MessageKey must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForNullMessageArgument() {
        var builder = new ValidationResult.Builder("default");

        var thrown = catchThrowable(() -> builder.messageArgument(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("MessageArgument must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForMultipleNullMessageArguments() {
        var builder = new ValidationResult.Builder("default");

        var thrown = catchThrowable(() -> builder.messageArguments(null, null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("MessageArgument must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForNullLabelType() {
        var builder = new ValidationResult.Builder("default");

        var thrown = catchThrowable(() -> builder.labelType(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("LabelType must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForNullLabel() {
        var builder = new ValidationResult.Builder("default");

        var thrown = catchThrowable(() -> builder.label(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Label must not be null");
    }

    @Test
    void builder_ShouldNotThrowException_ForNullValue() {
        var builder = new ValidationResult.Builder("default");

        assertThatCode(() -> builder.value(null)).doesNotThrowAnyException();
    }

    @Test
    void constructor_ShouldNotThrowException_ForFieldsNotSet() {
        var builder = new ValidationResult.Builder("default");

        assertThatCode(builder::ok).doesNotThrowAnyException();
    }

    @Test
    void withLabel_ShouldCreateNewResultWithLabel() {
        var result = new ValidationResult.Builder("default").fail();

        var changedResult = result.withLabel(LabelType.FIELD, "fieldName");

        assertThat(result).isNotEqualTo(changedResult);
        assertThat(result.getState()).isEqualTo(changedResult.getState());
        assertThat(changedResult.getLabelType()).isEqualTo(LabelType.FIELD);
        assertThat(changedResult.getLabel()).isEqualTo("fieldName");
    }

    @Test
    void withLabel_ShouldRetainAllOtherParameters() {
        var result = new ValidationResult.Builder("default Message")
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
        assertThat(changedResult.getLabel())
                .isNotEqualTo("fieldName")
                .isEqualTo("Other FieldName");
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
                Arguments.of(new ValidationResult.Builder("default")
                        .messageKey("validation.object.notNull")
                        .ok()),
                Arguments.of(new ValidationResult.Builder("default")
                        .messageKey("validation.object.notNull")
                        .skip()),
                Arguments.of(new ValidationResult.Builder("default")
                        .messageKey("validation.object.notNull")
                        .fail())
        );
    }

    @ParameterizedTest
    @MethodSource("resultWithAllStates")
    void resolveValidationMessage_ShouldResolveTheMessage_ForTheMessageKey(ValidationResult result) {
        var resolvedMessage = result.resolveValidationMessage();

        assertThat(resolvedMessage).isEqualTo("must not be null");
    }

    @ParameterizedTest
    @MethodSource("resultWithAllStates")
    void resolveValidationMessage_ShouldResolveTheMessage_ForTheMessageKeyAndCustomResolver(ValidationResult result) {
        var resolver = new ResourceBundleMessageResolver("ValidationMessages");
        var locale = Locale.ENGLISH;
        var resolvedMessage = result.resolveValidationMessage(resolver, locale);

        assertThat(resolvedMessage).isEqualTo("must not be null");
    }

    @Test
    void getMessage_ShouldReturnSuccessMessage_ForSuccessfulResult() {
        var result = new ValidationResult.Builder("default")
                .messageKey("validation.object.notNull")
                .ok();

        var message = result.getMessage();

        assertThat(message).isEqualTo("The Subject \"<unknown>\" is valid: must not be null");
    }

    @Test
    void getMessage_ShouldReturnSkippedMessage_ForSkippedResult() {
        var result = new ValidationResult.Builder("default")
                .messageKey("validation.object.notNull")
                .skip();

        var message = result.getMessage();

        assertThat(message).isEqualTo("Validation for Subject \"<unknown>\" was skipped");
    }

    @Test
    void getMessage_ShouldReturnFailureMessage_ForFailedResult() {
        var result = new ValidationResult.Builder("default")
                .messageKey("validation.object.notNull")
                .fail();

        var message = result.getMessage();

        assertThat(message).isEqualTo("The Subject \"<unknown>\" is invalid: must not be null");
    }

    @Test
    void getMessage_ShouldReturnCorrectMessage_ForCustomResolver() {
        var resolver = new ResourceBundleMessageResolver("ValidationMessages");
        var locale = Locale.ENGLISH;
        var result = new ValidationResult.Builder("default")
                .messageKey("validation.object.notNull")
                .fail();

        var message = result.getMessage(resolver, locale);

        assertThat(message).isEqualTo("The Subject \"<unknown>\" is invalid: must not be null");
    }

    @Test
    void throwIfInvalid_ShouldThrowException_ForInvalidResult() {
        var result = new ValidationResult.Builder("default message").fail();

        var thrown = catchThrowable(result::throwIfInvalid);

        assertThat(thrown)
                .isInstanceOf(InvalidAttributeValueException.class)
                .hasMessage("The Subject \"<unknown>\" is invalid: default message");
    }

    @Test
    void throwIfInvalid_ShouldNotThrowException_ForValidResult() {
        var result = new ValidationResult.Builder("default message").ok();

        assertThatCode(result::throwIfInvalid).doesNotThrowAnyException();
    }

    @Test
    void throwIfInvalid_ShouldNotThrowException_ForSkippedResult() {
        var result = new ValidationResult.Builder("default message").skip();

        assertThatCode(result::throwIfInvalid).doesNotThrowAnyException();
    }

    @Test
    void throwIfInvalid_ShouldThrowException_ForInvalidResultAndProvidedExceptionType() {
        var result = new ValidationResult.Builder("default message").fail();

        var thrown = catchThrowable(() -> result.throwIfInvalid(InvalidParameterException::new));

        assertThat(thrown)
                .isInstanceOf(InvalidParameterException.class)
                .hasMessage("The Subject \"<unknown>\" is invalid: default message");
    }
}