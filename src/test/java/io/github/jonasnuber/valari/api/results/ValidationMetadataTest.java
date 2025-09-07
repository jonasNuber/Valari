package io.github.jonasnuber.valari.api.results;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ValidationMetadataTest {

    @Test
    void builder_ShouldCreateMetadataWithAllValues_ForValidInput() {
        var metadata = new ValidationMetadata.Builder("default msg")
                .messageKey("msg.key")
                .messageArgument("arg1")
                .messageArguments("arg2", "arg3")
                .labelType(LabelType.FIELD)
                .label("username")
                .build();

        assertThat(metadata.getDefaultMessage()).isEqualTo("default msg");
        assertThat(metadata.getMessageKey()).isEqualTo("msg.key");
        assertThat(metadata.getMessageArguments()).containsExactly("arg1", "arg2", "arg3");
        assertThat(metadata.getLabelType()).isEqualTo(LabelType.FIELD);
        assertThat(metadata.getLabel()).isEqualTo("username");
    }

    @Test
    void builder_ShouldUseDefaults_WhenOptionalFieldsAreNotSet() {
        var metadata = new ValidationMetadata.Builder("default msg").build();

        assertThat(metadata.getMessageKey()).isEqualTo("not.provided");
        assertThat(metadata.getMessageArguments()).isEmpty();
        assertThat(metadata.getLabelType()).isEqualTo(LabelType.SUBJECT);
        assertThat(metadata.getLabel()).isEqualTo("<unknown>");
    }

    @Test
    void builder_ShouldThrowException_ForNullDefaultMessage() {
        var thrown = catchThrowable(() -> new ValidationMetadata.Builder(null));


        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("DefaultMessage must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForNullMessageKey() {
        var builder = new ValidationMetadata.Builder("default");

        var thrown = catchThrowable(() -> builder.messageKey(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("MessageKey must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForNullMessageArgument() {
        var builder = new ValidationMetadata.Builder("default");

        var thrown = catchThrowable(() -> builder.messageArgument(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("MessageArgument must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForNullLabelType() {
        var builder = new ValidationMetadata.Builder("default");

        var thrown = catchThrowable(() -> builder.labelType(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("LabelType must not be null");
    }

    @Test
    void builder_ShouldThrowException_ForNullLabel() {
        var builder = new ValidationMetadata.Builder("default");

        var thrown = catchThrowable(() -> builder.label(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Label must not be null");
    }

    @Test
    void equalsAndHashCode_ShouldBeConsistent_ForEqualObjects() {
        var metadata1 = new ValidationMetadata.Builder("msg")
                .messageKey("key")
                .labelType(LabelType.FIELD)
                .label("username")
                .build();

        var metadata2 = new ValidationMetadata.Builder("msg")
                .messageKey("key")
                .labelType(LabelType.FIELD)
                .label("username")
                .build();

        assertThat(metadata1)
                .isEqualTo(metadata2).
                hasSameHashCodeAs(metadata2);
    }

    @Test
    void equals_ShouldReturnFalse_ForDifferentObjects() {
        var metadata1 = new ValidationMetadata.Builder("msg").messageKey("key1").build();
        var metadata2 = new ValidationMetadata.Builder("msg").messageKey("key2").build();

        assertThat(metadata1).isNotEqualTo(metadata2);
    }

    @Test
    void toString_ShouldContainAllFields() {
        var metadata = new ValidationMetadata.Builder("msg")
                .messageKey("key")
                .messageArgument("arg")
                .labelType(LabelType.VALUE)
                .label("myField")
                .build();

        assertThat(metadata.toString())
                .contains("defaultMessage='msg'")
                .contains("messageKey='key'")
                .contains("messageArguments=[arg]")
                .contains("labelType=value")
                .contains("label='myField'");
    }
}