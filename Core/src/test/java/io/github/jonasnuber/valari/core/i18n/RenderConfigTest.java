package io.github.jonasnuber.valari.core.i18n;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import org.junit.jupiter.api.Test;

class RenderConfigTest {

  @Test
  void builder_ShouldCreateConfigWithAllValues_ForValidInput() {
    var config =
        RenderConfig.builder()
            .hideClassNames()
            .showSuccesses()
            .hideErrorCounts()
            .hideLabelTypes()
            .indentSize(4)
            .style(RenderStyle.PLAIN)
            .build();

    assertThat(config.showClassNames()).isFalse();
    assertThat(config.showSuccesses()).isTrue();
    assertThat(config.showErrorCounts()).isFalse();
    assertThat(config.showLabelTypes()).isFalse();
    assertThat(config.indentSize()).isEqualTo(4);
    assertThat(config.style()).isEqualTo(RenderStyle.PLAIN);
  }

  @Test
  void builder_ShouldUseDefaults_WhenOptionalFieldsAreNotSet() {
    var config = RenderConfig.builder().build();

    assertThat(config.showClassNames()).isFalse();
    assertThat(config.showSuccesses()).isFalse();
    assertThat(config.showErrorCounts()).isTrue();
    assertThat(config.showLabelTypes()).isTrue();
    assertThat(config.indentSize()).isEqualTo(2);
    assertThat(config.style()).isEqualTo(RenderStyle.UNICODE);
  }

  @Test
  void builder_ShouldThrowException_ForNullStyle() {
    var builder = RenderConfig.builder();

    var thrown = catchThrowable(() -> builder.style(null).build());

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Render style must not be null");
  }

  @Test
  void builder_ShouldSetIndentSizeToZero_ForNegativeValue() {
    var builder = RenderConfig.builder();

    var config = builder.indentSize(-10).build();

    assertThat(config.indentSize()).isZero();
  }
}
