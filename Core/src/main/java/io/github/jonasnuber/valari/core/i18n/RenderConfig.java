package io.github.jonasnuber.valari.core.i18n;

import java.util.Objects;

/**
 * Configuration container controlling how hierarchical validation results are rendered.
 *
 * <p>{@code RenderConfig} defines all structural and stylistic rules used when producing
 * human-readable output through a {@link io.github.jonasnuber.valari.api.i18n.ResultFormatter}. It
 * specifies:
 *
 * <ul>
 *   <li>the {@link RenderStyle} used to draw symbols and tree branches
 *   <li>whether successful validations should be included
 *   <li>whether class names and label types appear in the output
 *   <li>whether aggregated error counts should be displayed
 *   <li>the indentation size applied per nesting level
 * </ul>
 *
 * <p>Instances are immutable and must be created via the {@link Builder}. All settings have
 * sensible defaults to ensure readable output without requiring manual configuration.
 *
 * <h2>Typical Usage</h2>
 *
 * <pre>{@code
 * RenderConfig config = RenderConfig.builder()
 *     .style(RenderStyle.UNICODE)
 *     .showSuccesses()
 *     .showClassNames()
 *     .indentSize(4)
 *     .build();
 *
 * ResultFormatter<String> formatter = new DefaultResultFormatter(config);
 * String formatted = formatter.format(result);
 * }</pre>
 *
 * <h2>Immutability</h2>
 *
 * <p>All fields are final, and the class performs no defensive copying because it holds only
 * primitive values and immutable {@link RenderStyle} instances.
 *
 * @author Jonas Nuber
 */
public final class RenderConfig {
  private final RenderStyle style;
  private final boolean showSuccesses;
  private final boolean showClassNames;
  private final boolean showLabelTypes;
  private final boolean showErrorCounts;
  private final int indentSize;

  private RenderConfig(Builder builder) {
    this.style = builder.style;
    this.showSuccesses = builder.showSuccesses;
    this.showClassNames = builder.showClassNames;
    this.showLabelTypes = builder.showLabelTypes;
    this.showErrorCounts = builder.showErrorCounts;
    this.indentSize = builder.indentSize;
  }

  /**
   * Creates a new {@link Builder} with default settings.
   *
   * <p>Defaults:
   *
   * <ul>
   *   <li>{@link RenderStyle#UNICODE} as style
   *   <li>{@code showSuccesses = false}
   *   <li>{@code showClassNames = false}
   *   <li>{@code showLabelTypes = true}
   *   <li>{@code showErrorCounts = true}
   *   <li>{@code indentSize = 2}
   * </ul>
   *
   * @return a new builder instance
   */
  public static Builder builder() {
    return new Builder();
  }

  /**
   * @return the {@link RenderStyle} defining how structural and symbolic output is drawn
   */
  public RenderStyle style() {
    return style;
  }

  /**
   * @return {@code true} if successful validation entries should be rendered
   */
  public boolean showSuccesses() {
    return showSuccesses;
  }

  /**
   * @return {@code true} if class names should be included in the output
   */
  public boolean showClassNames() {
    return showClassNames;
  }

  /**
   * @return {@code true} if label types (e.g. {@code FIELD}, {@code OBJECT}) should be shown
   */
  public boolean showLabelTypes() {
    return showLabelTypes;
  }

  /**
   * @return {@code true} if aggregated error counts should be displayed for failing nodes
   */
  public boolean showErrorCounts() {
    return showErrorCounts;
  }

  /**
   * @return the number of spaces added per indentation level when rendering nested nodes
   */
  public int indentSize() {
    return indentSize;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    RenderConfig that = (RenderConfig) o;
    return showSuccesses == that.showSuccesses
        && showClassNames == that.showClassNames
        && showLabelTypes == that.showLabelTypes
        && showErrorCounts == that.showErrorCounts
        && indentSize == that.indentSize
        && Objects.equals(style, that.style);
  }

  @Override
  public int hashCode() {
    return Objects.hash(
        style, showSuccesses, showClassNames, showLabelTypes, showErrorCounts, indentSize);
  }

  /**
   * Builder for constructing immutable {@link RenderConfig} instances.
   *
   * <p>All builder methods return the builder itself to enable fluent chaining. Boolean flags use
   * dedicated {@code showX()} and {@code hideX()} methods for readability.
   */
  public static final class Builder {
    private RenderStyle style = RenderStyle.UNICODE;
    private int indentSize = 2;
    private boolean showSuccesses = false;
    private boolean showClassNames = false;
    private boolean showLabelTypes = true;
    private boolean showErrorCounts = true;

    private Builder() {}

    /**
     * Sets the {@link RenderStyle} used for rendering symbols and tree branches.
     *
     * @param style non-null render style
     * @return this builder
     * @throws NullPointerException if {@code style} is {@code null}
     */
    public Builder style(RenderStyle style) {
      this.style = Objects.requireNonNull(style, "Render style must not be null");
      return this;
    }

    /**
     * Enables the rendering of successful validation nodes.
     *
     * @return this builder
     */
    public Builder showSuccesses() {
      this.showSuccesses = true;
      return this;
    }

    /**
     * Disables the rendering of successful validation nodes.
     *
     * @return this builder
     */
    public Builder hideSuccesses() {
      this.showSuccesses = false;
      return this;
    }

    /**
     * Enables class name rendering for each validation node.
     *
     * @return this builder
     */
    public Builder showClassNames() {
      this.showClassNames = true;
      return this;
    }

    /**
     * Disables class name rendering.
     *
     * @return this builder
     */
    public Builder hideClassNames() {
      this.showClassNames = false;
      return this;
    }

    /**
     * Enables rendering of label types (e.g., {@code FIELD}, {@code OBJECT}).
     *
     * @return this builder
     */
    public Builder showLabelTypes() {
      this.showLabelTypes = true;
      return this;
    }

    /**
     * Disables rendering of label types.
     *
     * @return this builder
     */
    public Builder hideLabelTypes() {
      this.showLabelTypes = false;
      return this;
    }

    /**
     * Enables aggregated error count display for each failing node.
     *
     * @return this builder
     */
    public Builder showErrorCounts() {
      this.showErrorCounts = true;
      return this;
    }

    /**
     * Disables aggregated error count display.
     *
     * @return this builder
     */
    public Builder hideErrorCounts() {
      this.showErrorCounts = false;
      return this;
    }

    /**
     * Sets the number of spaces used per indentation level.
     *
     * <p>Values below zero are normalized to {@code 0}.
     *
     * @param indentSize indentation size per nesting level
     * @return this builder
     */
    public Builder indentSize(int indentSize) {
      this.indentSize = Math.max(0, indentSize);
      return this;
    }

    /**
     * Builds a new immutable {@link RenderConfig} instance.
     *
     * @return the constructed configuration
     */
    public RenderConfig build() {
      return new RenderConfig(this);
    }
  }
}
