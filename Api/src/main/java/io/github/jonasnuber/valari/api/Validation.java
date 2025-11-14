package io.github.jonasnuber.valari.api;

/**
 * A {@code Validation} represents a single boolean-like rule that tests an input value against
 * predefined conditions. It produces a {@link Result} describing whether the value satisfies the
 * rule.
 *
 * <p>The {@code Result} returned by this validation is:
 *
 * <ul>
 *   <li><strong>valid</strong> if the input satisfies the rule, or
 *   <li><strong>invalid</strong> if the input violates the rule, typically containing a message,
 *       label, or metadata describing the reason for failure.
 * </ul>
 *
 * <p>This is a functional interface; the single abstract method is {@link #test(Object)}.
 * Implementations are commonly written as lambdas or used as building blocks in larger validation
 * pipelines.
 *
 * <h2>Combinators</h2>
 *
 * <p>The interface provides default combinator methods:
 *
 * <ul>
 *   <li>{@link #and(Validation)} — logical conjunction (short-circuits on first failure)
 *   <li>{@link #or(Validation)} — logical disjunction (short-circuits on first success)
 * </ul>
 *
 * <p>These combinators never merge or mutate {@code Result} objects. Instead, they return the first
 * definitive outcome according to the operator's semantics.
 *
 * @param <TYPE> the type of input value tested by this validation rule
 * @author Jonas Nuber
 */
@FunctionalInterface
@SuppressWarnings("java:S119")
public interface Validation<TYPE> {

  /**
   * Tests the given value against the validation logic implemented by this rule.
   *
   * @param param the value to validate (may be {@code null}, depending on the rule)
   * @return a {@link Result} describing whether the value is valid; never {@code null}
   */
  Result<?> test(TYPE param);

  /**
   * Returns a composed validation that first evaluates this validation, and only if it is valid,
   * evaluates the provided {@code other} validation.
   *
   * <p>Semantics:
   *
   * <ul>
   *   <li>If this validation is <strong>invalid</strong>, its result is returned immediately.
   *   <li>If this validation is <strong>valid</strong>, the {@code other} validation is evaluated
   *       and its result is returned.
   * </ul>
   *
   * <p>This method performs short-circuit evaluation and never merges result contents.
   *
   * @param other the validation to evaluate if this validation succeeds (must not be {@code null})
   * @return a composed validation reflecting logical AND semantics
   */
  default Validation<TYPE> and(Validation<TYPE> other) {
    return param -> {
      var firstResult = this.test(param);
      return !firstResult.isValid() ? firstResult : other.test(param);
    };
  }

  /**
   * Returns a composed validation that first evaluates this validation, and only if it is invalid,
   * evaluates the provided {@code other} validation.
   *
   * <p>Semantics:
   *
   * <ul>
   *   <li>If this validation is <strong>valid</strong>, its result is returned immediately.
   *   <li>If this validation is <strong>invalid</strong>, the {@code other} validation is evaluated
   *       and its result is returned.
   * </ul>
   *
   * <p>This method performs short-circuit evaluation and never merges result contents.
   *
   * @param other the validation to evaluate if this validation fails (must not be {@code null})
   * @return a composed validation reflecting logical OR semantics
   */
  default Validation<TYPE> or(Validation<TYPE> other) {
    return param -> {
      var firstResult = this.test(param);
      return firstResult.isValid() ? firstResult : other.test(param);
    };
  }
}
