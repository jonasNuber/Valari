package io.github.jonasnuber.valari.core.i18n.internal;

import io.github.jonasnuber.valari.core.i18n.RenderConfig;
import io.github.jonasnuber.valari.core.i18n.RenderStyle;
import java.util.Objects;

/**
 * Builds textual tree-style prefixes for rendering hierarchical validation output.
 *
 * <p>The {@code PrefixBuilder} converts a {@link PrefixState}—which tracks whether nodes at each
 * depth are the last child—into a formatted prefix string. These prefixes are used by Valari’s
 * result renderers to draw branch symbols, continuation lines, and indentation. The exact symbols
 * used are defined by the configured {@link RenderStyle}.
 *
 * <h2>Usage</h2>
 *
 * <p>A {@code PrefixBuilder} is typically created once per rendering operation using a {@link
 * RenderConfig}:
 *
 * <pre>{@code
 * RenderConfig config = RenderConfig.builder()
 *     .style(RenderStyle.UNICODE)
 *     .indentSize(2)
 *     .build();
 *
 * PrefixBuilder builder = new PrefixBuilder(config);
 *
 * PrefixState state = new PrefixState()
 *     .isLastChild(false)
 *     .isLastChild(true);
 *
 * String prefix = builder.prefix(state);
 * // Produces something like: "│   └── "
 * }</pre>
 *
 * <h2>Immutability</h2>
 *
 * <p>{@code PrefixBuilder} is immutable and thread-safe as long as the provided {@link
 * RenderConfig} is not modified after construction.
 *
 * <h2>Rendering Rules</h2>
 *
 * <ul>
 *   <li>For intermediate levels:
 *       <ul>
 *         <li>If that ancestor was the last child, indentation is used.
 *         <li>Otherwise, a vertical continuation line is drawn.
 *       </ul>
 *   <li>For the final level (current node):
 *       <ul>
 *         <li>{@code style.lastBranch()} is used for the last child.
 *         <li>{@code style.branch()} is used otherwise.
 *       </ul>
 * </ul>
 *
 * @see PrefixState
 * @see RenderStyle
 * @see RenderConfig
 * @author Jonas Nuber
 */
public final class PrefixBuilder {
  private final RenderStyle style;
  private final int indentSize;

  /**
   * Creates a new {@code PrefixBuilder} based on the provided render configuration.
   *
   * @param config the configuration determining style and indentation behavior
   * @throws NullPointerException if {@code config} is {@code null}
   */
  public PrefixBuilder(RenderConfig config) {
    Objects.requireNonNull(config, "RenderConfig must not be null");
    this.style = config.style();
    this.indentSize = Math.max(0, config.indentSize());
  }

  /**
   * Builds the prefix string corresponding to the given {@link PrefixState}.
   *
   * @param state the prefix state describing last-child flags per depth
   * @return the formatted prefix string
   */
  public String prefix(PrefixState state) {
    return buildPrefix(state);
  }

  private String buildPrefix(PrefixState state) {
    StringBuilder sb = new StringBuilder();
    var flags = state.lastChildFlags();

    for (int depth = 0; depth < flags.size(); depth++) {
      boolean parentWasLast = flags.get(depth);
      boolean isCurrentNodeLevel = (depth == flags.size() - 1);

      if (isCurrentNodeLevel) {
        appendNodePrefix(sb, parentWasLast);
      } else {
        appendParentPrefix(sb, parentWasLast);
      }
    }

    return sb.toString();
  }

  private void appendParentPrefix(StringBuilder sb, boolean parentWasLast) {
    if (parentWasLast) {
      sb.append(indent());
    } else {
      sb.append(style.vertical());
    }
  }

  private void appendNodePrefix(StringBuilder sb, boolean parentWasLast) {
    sb.append(parentWasLast ? style.lastBranch() : style.branch());
  }

  private String indent() {
    return style.indentUnit().repeat(indentSize);
  }
}
