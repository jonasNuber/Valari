package io.github.jonasnuber.valari.core.i18n.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.jonasnuber.valari.core.i18n.RenderConfig;
import io.github.jonasnuber.valari.core.i18n.RenderStyle;
import org.junit.jupiter.api.Test;

class PrefixBuilderTest {

  @Test
  void constructor_ShouldThrowException_ForNullConfig() {
    var thrown = catchThrowable(() -> new PrefixBuilder(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("RenderConfig must not be null");
  }

  @Test
  void prefix_ShouldUseBranch_WhenNotLastChild() {
    var config = RenderConfig.builder().style(RenderStyle.UNICODE).indentSize(1).build();
    var builder = new PrefixBuilder(config);
    var state = new PrefixState().isLastChild(false);

    var result = builder.prefix(state);

    assertThat(result).isEqualTo(RenderStyle.UNICODE.branch());
  }

  @Test
  void prefix_ShouldUseLastBranch_WhenLastChild() {
    var config = RenderConfig.builder().style(RenderStyle.UNICODE).indentSize(1).build();
    var builder = new PrefixBuilder(config);
    var state = new PrefixState().isLastChild(true);

    var result = builder.prefix(state);

    assertThat(result).isEqualTo(RenderStyle.UNICODE.lastBranch());
  }

  @Test
  void prefix_ShouldUseIndent_WhenParentWasLastChild() {
    var config = RenderConfig.builder().style(RenderStyle.UNICODE).indentSize(2).build();
    var builder = new PrefixBuilder(config);
    var state = new PrefixState().isLastChild(true).isLastChild(false);

    var result = builder.prefix(state);

    assertThat(result).isEqualTo("    " + RenderStyle.UNICODE.branch());
  }

  @Test
  void prefix_ShouldUseVertical_WhenParentWasNotLastChild() {
    var config = RenderConfig.builder().style(RenderStyle.UNICODE).indentSize(2).build();
    var builder = new PrefixBuilder(config);
    var state = new PrefixState().isLastChild(false).isLastChild(true);

    var result = builder.prefix(state);

    assertThat(result).isEqualTo(RenderStyle.UNICODE.vertical() + RenderStyle.UNICODE.lastBranch());
  }

  @Test
  void prefix_ShouldBuildCorrectPrefix_ForMixedDeepNesting() {
    /*
      State (flags at each depth):
      depth 0 → false → vertical
      depth 1 → true  → indent
      depth 2 → false → branch

      UNICODE vertical = "│   "
      indentUnit = "  " (size=2 → "    ")
      branch = "├── "
    */
    var config = RenderConfig.builder().style(RenderStyle.UNICODE).indentSize(2).build();
    var builder = new PrefixBuilder(config);
    var state = new PrefixState().isLastChild(false).isLastChild(true).isLastChild(false);

    var result = builder.prefix(state);

    assertThat(result)
        .isEqualTo(RenderStyle.UNICODE.vertical() + "    " + RenderStyle.UNICODE.branch());
  }

  @Test
  void prefix_ShouldReturnEmptyString_WhenNoLevels() {
    var config = RenderConfig.builder().style(RenderStyle.PLAIN).indentSize(2).build();
    var builder = new PrefixBuilder(config);
    var empty = new PrefixState();

    var result = builder.prefix(empty);

    assertThat(result).isEmpty();
  }
}
