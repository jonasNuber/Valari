package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ValidationMetadataTest {

  @Test
  void builder_ShouldCreateMetadataWithAllValues_ForValidInput() {
    var metadata =
        ValidationMetadata.builder("default msg")
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
    var metadata = ValidationMetadata.builder("default msg").build();

    assertThat(metadata.getMessageKey()).isEqualTo("not.provided");
    assertThat(metadata.getMessageArguments()).isEmpty();
    assertThat(metadata.getLabelType()).isEqualTo(LabelType.SUBJECT);
    assertThat(metadata.getLabel()).isEqualTo("<unknown>");
  }

  @Test
  void builder_ShouldThrowException_ForNullDefaultMessage() {
    var thrown = catchThrowable(() -> ValidationMetadata.builder(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("DefaultMessage must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullMessageKey() {
    var builder = ValidationMetadata.builder("default");

    var thrown = catchThrowable(() -> builder.messageKey(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("MessageKey must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullMessageArgument() {
    var builder = ValidationMetadata.builder("default");

    var thrown = catchThrowable(() -> builder.messageArgument(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("MessageArgument must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullValidationClass() {
    var builder = ValidationMetadata.builder("default");

    var thrown = catchThrowable(() -> builder.validationClass(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Class to validate may not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullLabelType() {
    var builder = ValidationMetadata.builder("default");

    var thrown = catchThrowable(() -> builder.labelType(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("LabelType must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullLabel() {
    var builder = ValidationMetadata.builder("default");

    var thrown = catchThrowable(() -> builder.label(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Label must not be null");
  }

  @Test
  void equalsAndHashCode_ShouldBeConsistent_ForEqualObjects() {
    var metadata1 =
        ValidationMetadata.builder("msg")
            .messageKey("key")
            .labelType(LabelType.FIELD)
            .label("username")
            .build();

    var metadata2 =
        ValidationMetadata.builder("msg")
            .messageKey("key")
            .labelType(LabelType.FIELD)
            .label("username")
            .build();

    assertThat(metadata1).isEqualTo(metadata2).hasSameHashCodeAs(metadata2);
  }

  @Test
  void equals_ShouldReturnFalse_ForDifferentObjects() {
    var metadata1 = ValidationMetadata.builder("msg").messageKey("key1").build();
    var metadata2 = ValidationMetadata.builder("msg").messageKey("key2").build();

    assertThat(metadata1).isNotEqualTo(metadata2);
  }

  @Test
  void resolveMessage_ShouldReturnMessage_ForValidInput() {
    var resolver = mock(MessageResolver.class);
    var locale = Locale.ENGLISH;
    var metadata =
        ValidationMetadata.builder("someDefault")
            .messageKey("some.key")
            .messageArguments(List.of("argument").toArray())
            .build();
    when(resolver.resolve("some.key", List.of("argument"), "someDefault", locale))
        .thenReturn("Some resolved message with argument");

    var message = metadata.resolveMessage(resolver, locale);

    assertThat(message).isEqualTo("Some resolved message with argument");
  }

  @Test
  void resolveMessage_ShouldThrowException_ForNullResolver() {
    var metadata = ValidationMetadata.builder("default").build();

    var thrown = catchThrowable(() -> metadata.resolveMessage(null, null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Resolver must not be null");
  }

  @Test
  void resolveMessage_ShouldThrowException_ForNullLocale() {
    var metadata = ValidationMetadata.builder("default").build();

    var thrown = catchThrowable(() -> metadata.resolveMessage(mock(MessageResolver.class), null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Locale must not be null");
  }
}
