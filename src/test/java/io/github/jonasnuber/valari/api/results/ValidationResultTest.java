package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.spi.Validation;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.internal.matchers.Null;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;

class ValidationResultTest{

    @Test
    void builder_ShouldCreateResultWithAllValues_ForValidInput() {
        var builder = new ValidationResult.Builder("default Message")
                .messageKey("message.key")
                .messageArgument("message Argument")
                .messageArguments("message Argument 2", "message Argument 3")
                .fieldName("fieldName")
                .value("Some Value");

        var result = builder.ok();

        assertThat(result.getState()).isEqualTo(ValidationState.SUCCESS);
        assertThat(result.getDefaultMessage()).isEqualTo("default Message");
        assertThat(result.getMessageKey()).isEqualTo("message.key");
        assertThat(result.getMessageArguments())
                .hasSize(3)
                .containsExactly("message Argument", "message Argument 2", "message Argument 3");
        assertThat(result.getFieldName()).isEqualTo("fieldName");
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
        var thrown = catchThrowable(() -> new ValidationResult.Builder(null));

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
    void builder_ShouldThrowException_ForNullFieldName() {
        var builder = new ValidationResult.Builder("default");

        var thrown = catchThrowable(() -> builder.fieldName(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FieldName must not be null");
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
    void withFieldName_ShouldCreateNewResultWithFieldName() {
        var result = new ValidationResult.Builder("default").fail();

        var changedResult = result.withFieldName("fieldName");

        assertThat(result).isNotEqualTo(changedResult);
        assertThat(result.getState()).isEqualTo(changedResult.getState());
        assertThat(changedResult.getFieldName()).isEqualTo("fieldName");
    }

    @Test
    void withFieldName_ShouldRetainAllOtherParameters() {
        var result = new ValidationResult.Builder("default Message")
                .messageKey("message.key")
                .messageArgument("message Argument")
                .messageArguments("message Argument 2", "message Argument 3")
                .fieldName("fieldName")
                .value("Some Value")
                .ok();

        var changedResult = result.withFieldName("Other FieldName");

        assertThat(result).isNotEqualTo(changedResult);
        assertThat(changedResult.getState()).isEqualTo(ValidationState.SUCCESS);
        assertThat(changedResult.getDefaultMessage()).isEqualTo("default Message");
        assertThat(changedResult.getMessageKey()).isEqualTo("message.key");
        assertThat(changedResult.getMessageArguments())
                .hasSize(3)
                .containsExactly("message Argument", "message Argument 2", "message Argument 3");
        assertThat(changedResult.getFieldName())
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
}