package io.github.jonasnuber.valari.api;

/**
 * Functional interface representing a validator that performs validation without requiring an
 * explicit input value. Instead of validating a provided object, the validator determines validity
 * based on internal state, captured context, or fixed rules.
 *
 * <p>This interface is useful in scenarios such as:
 *
 * <ul>
 *   <li>pre-bound or pre-extracted values (e.g. field-level validators),
 *   <li>lazy or deferred validation within larger validation pipelines,
 *   <li>context-aware rules that rely on environmental conditions rather than parameters,
 *   <li>validators created using closures or factory methods that internalize the value to check.
 * </ul>
 *
 * <p>Implementations must not throw exceptions directly upon validation failure. Instead, they
 * return a {@link Result} (often implementing {@link
 * io.github.jonasnuber.valari.api.ThrowableResult}) which can optionally be converted into an
 * exception by the caller via {@code throwIfInvalid()}.
 *
 * <p>This type is a functional interface and is typically implemented via lambdas or method
 * references.
 *
 * @param <RESULT> the result type produced by the validation, which must extend {@link Result}
 * @see Result
 * @see ThrowableResult
 * @see Validator
 * @see GenericValidator
 * @author Jonas Nuber
 */
@FunctionalInterface
@SuppressWarnings("java:S119")
public non-sealed interface NoInputValidator<RESULT extends Result<RESULT>>
    extends GenericValidator {

  /**
   * Executes the validation logic and returns a {@link Result} describing the outcome.
   *
   * <p>Since this validator does not accept input, the logic is typically derived from pre-bound
   * state, captured variables, or constant rules.
   *
   * @return a {@code RESULT} representing success or failure; never {@code null}
   */
  RESULT validate();
}
