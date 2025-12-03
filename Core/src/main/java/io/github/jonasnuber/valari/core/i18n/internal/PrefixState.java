package io.github.jonasnuber.valari.core.i18n.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Maintains contextual prefix information used while rendering hierarchical validation result
 * trees.
 *
 * <p>The {@code PrefixState} tracks, for each nesting level, whether the corresponding node was the
 * <em>last child</em> of its parent. This information is used by tree renderers to decide whether
 * to draw vertical continuation lines, branch symbols, or end-branch symbols when building
 * multi-line textual output.
 *
 * <h2>Immutability</h2>
 *
 * <p>This class is immutable. Each call to {@link #isLastChild(boolean)} returns a new instance
 * with the updated prefix history, ensuring thread safety and predictable behavior during recursive
 * rendering.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * PrefixState root = new PrefixState();       // []
 * PrefixState child = root.isLastChild(false); // [false]
 * PrefixState lastChild = child.isLastChild(true); // [false, true]
 *
 * // Used by renderers to determine which prefix characters to emit.
 * }</pre>
 *
 * @author Jonas Nuber
 */
public final class PrefixState {
  private final List<Boolean> lastChildFlags;

  /** Creates an empty {@code PrefixState} representing the root level of a rendered tree. */
  public PrefixState() {
    this.lastChildFlags = List.of();
  }

  /**
   * Creates a new {@code PrefixState} with the given sequence of last-child flags.
   *
   * @param flags the immutable list of booleans indicating last-child status per depth level
   */
  private PrefixState(List<Boolean> flags) {
    this.lastChildFlags = flags;
  }

  /**
   * Returns a new {@code PrefixState} with an additional flag indicating whether the current node
   * is the last child in its parent's list.
   *
   * <p>The returned state reflects one level deeper in the tree and preserves all previously
   * recorded flags.
   *
   * @param isLastChild {@code true} if the current node is the last child, {@code false} otherwise
   * @return a new {@code PrefixState} containing the extended prefix information
   */
  public PrefixState isLastChild(boolean isLastChild) {
    List<Boolean> list = new ArrayList<>(lastChildFlags);
    list.add(isLastChild);
    return new PrefixState(Collections.unmodifiableList(list));
  }

  /**
   * Returns the immutable sequence of last-child flags recorded for all levels of the tree.
   *
   * <p>The list's size corresponds to the current depth. Each element indicates, for that level,
   * whether the node was the final child among siblings.
   *
   * @return an immutable list of booleans representing last-child flags
   */
  List<Boolean> lastChildFlags() {
    return lastChildFlags;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    PrefixState that = (PrefixState) o;
    return Objects.equals(lastChildFlags, that.lastChildFlags);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(lastChildFlags);
  }
}
