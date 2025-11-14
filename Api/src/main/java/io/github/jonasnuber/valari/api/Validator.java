package io.github.jonasnuber.valari.api;

/**
 * Functional interface representing a validator that inspects an input value of type {@code TYPE}
 * and produces a {@link Result} describing the outcome.
 *
 * <p>This interface forms the core abstraction for validation logic within the Valari framework. A
 * validator does not throw exceptions directly during validation; instead, it returns a {@link
 * Result} that expresses whether the input is valid, invalid, or carries additional metadata or
 * messages.
 *
 * <p>Implementations may represent:
 *
 * <ul>
 *   <li>simple single-constraint validators,
 *   <li>composite validators combining multiple rules,
 *   <li>domain-specific object validators, or
 *   <li>context-aware validators wrapping multiple delegated checks.
 * </ul>
 *
 * The caller may choose to act on the returned result or trigger exception-based flow using the
 * {@link io.github.jonasnuber.valari.api.ThrowableResult} mixin interfaces provided by the
 * framework.
 *
 * <p>This type is a functional interface, enabling validators to be implemented as lambdas or
 * method references.
 *
 * @param <TYPE> the type of object being validated
 * @param <RESULT> the result type produced, which must extend {@link Result}
 * @see Result
 * @see ThrowableResult
 * @see GenericValidator
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
@FunctionalInterface
public non-sealed interface Validator<TYPE, RESULT extends Result<RESULT>>
    extends GenericValidator {

  /**
   * Validates the given object and returns a {@link Result} describing success or failure.
   *
   * <p>Implementations must not throw exceptions for validation failure; callers may explicitly
   * convert the returned result into an exception using {@link
   * io.github.jonasnuber.valari.api.ThrowableResult#throwIfInvalid()} if desired.
   *
   * @param toValidate the object to validate; may be {@code null} depending on the validator
   * @return a {@code RESULT} describing the validation outcome
   */
  RESULT validate(TYPE toValidate);
}
