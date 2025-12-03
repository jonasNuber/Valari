package io.github.jonasnuber.valari.core.i18n;

/**
 * Defines a set of symbols and indentation rules used to render hierarchical validation output.
 *
 * <p>A {@code RenderStyle} encapsulates all visual elements involved in generating structured
 * output, such as:
 *
 * <ul>
 *   <li>Success and error markers (e.g., {@code ✓}, {@code ✗}, {@code [OK]}, {@code [ERR]})
 *   <li>Branching characters for tree-like rendering
 *   <li>Indentation and vertical connectors
 * </ul>
 *
 * <p>These styles are typically used by implementations of {@link
 * io.github.jonasnuber.valari.api.i18n.ResultFormatter} to produce human-readable validation
 * reports. They allow consumers to choose an output format that matches their environment (plain
 * text, Unicode consoles, legacy ASCII terminals, etc.).
 *
 * <h2>Predefined Styles</h2>
 *
 * <ul>
 *   <li>{@link #PLAIN} – Minimal formatting without Unicode or ASCII symbols
 *   <li>{@link #UNICODE} – Rich Unicode-based tree structure with checkmarks
 *   <li>{@link #ASCII} – Compatible ASCII-only tree structure
 * </ul>
 *
 * <p>Custom styles can be created by instantiating this record directly.
 *
 * @param okSymbol symbol used to represent successful validation nodes
 * @param errSymbol symbol used to represent validation failures
 * @param branch prefix for intermediate entries in a hierarchical structure
 * @param lastBranch prefix for the last entry in a hierarchical structure
 * @param vertical vertical connector displayed between sibling nodes
 * @param indentUnit indentation unit used to represent nesting levels
 * @author Jonas Nuber
 */
public record RenderStyle(
    String okSymbol,
    String errSymbol,
    String branch,
    String lastBranch,
    String vertical,
    String indentUnit) {

  /**
   * A minimal formatting style using only basic characters.
   *
   * <p>Does not use Unicode or ASCII art for success/error state. Best for environments where clean
   * text output is preferred or symbol rendering is unreliable.
   */
  public static final RenderStyle PLAIN = new RenderStyle("", "", "- ", "- ", "", "  ");

  /**
   * A Unicode-based rendering style offering visually appealing output.
   *
   * <p>Includes tree-drawing characters and checkmark symbols. Requires UTF-8-capable terminals or
   * consoles.
   */
  public static final RenderStyle UNICODE = new RenderStyle("✓", "✗", "├── ", "└── ", "│   ", "  ");

  /**
   * A fallback style using only ASCII characters.
   *
   * <p>Suitable for logs, legacy systems, or restricted text environments that lack proper Unicode
   * support.
   */
  public static final RenderStyle ASCII =
      new RenderStyle("[OK]", "[ERR]", "+-- ", "\\-- ", "|   ", "  ");
}
