package io.github.jonasnuber.valari.api;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class ValidationDescriptorTest {

  @Test
  void builder_ShouldCreateDescriptorWithAllValues_ForValidInput() {
    var descriptor =
        ValidationDescriptor.builder()
            .validationClass(String.class)
            .labelType(LabelType.FIELD)
            .label("SomeLabel")
            .build();

    assertThat(descriptor.getValidationClass()).isEqualTo(String.class);
    assertThat(descriptor.getLabelType()).isEqualTo(LabelType.FIELD);
    assertThat(descriptor.getLabel()).isEqualTo("SomeLabel");
  }

  @Test
  void builder_ShouldUseDefaults_WhenOptionalFieldsAreNotSet() {
    var descriptor = ValidationDescriptor.builder().build();

    assertThat(descriptor.getValidationClass()).isEqualTo(Object.class);
    assertThat(descriptor.getLabelType()).isEqualTo(LabelType.SUBJECT);
    assertThat(descriptor.getLabel()).isEqualTo("<unknown>");
  }

  @Test
  void builder_ShouldThrowException_ForNullValidationClass() {
    var builder = ValidationDescriptor.builder();

    var thrown = catchThrowable(() -> builder.validationClass(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Class to validate may not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullLabelType() {
    var builder = ValidationDescriptor.builder();

    var thrown = catchThrowable(() -> builder.labelType(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("LabelType must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullLabel() {
    var builder = ValidationDescriptor.builder();

    var thrown = catchThrowable(() -> builder.label(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Label must not be null");
  }

  @Test
  void equalsAndHashCode_ShouldBeConsistent_ForEqualObjects() {
    var descriptor1 =
        ValidationDescriptor.builder()
            .validationClass(String.class)
            .labelType(LabelType.FIELD)
            .label("SomeLabel")
            .build();

    var descriptor2 =
        ValidationDescriptor.builder()
            .validationClass(String.class)
            .labelType(LabelType.FIELD)
            .label("SomeLabel")
            .build();

    assertThat(descriptor1).isEqualTo(descriptor2).hasSameHashCodeAs(descriptor2);
  }

  @Test
  void equals_ShouldReturnFalse_ForDifferentObjects() {
    var descriptor1 = ValidationDescriptor.builder().validationClass(String.class).build();
    var descriptor2 = ValidationDescriptor.builder().build();

    assertThat(descriptor1).isNotEqualTo(descriptor2);
  }
}
