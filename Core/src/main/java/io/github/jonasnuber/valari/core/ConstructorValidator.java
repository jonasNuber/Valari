package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.ValidationStrategy;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.core.bindings.ParameterRuleBinding;
import io.github.jonasnuber.valari.core.strategies.CollectFailuresStrategy;
import io.github.jonasnuber.valari.core.strategies.FailFastStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A fluent, type-safe validator designed for validating constructor parameters, factory method
 * inputs, or any other grouped values that do not originate from a single source object.
 *
 * <p>A {@code ConstructorValidator} allows defining validation rules for individually named
 * parameters. Each parameter can have one or more {@link Validation} rules attached, enabling a
 * highly expressive and readable DSL for validating object creation logic.
 *
 * <h2>Key Features</h2>
 *
 * <ul>
 *   <li>Type-safe parameter-specific validation
 *   <li>User-defined parameter names for precise error messages
 *   <li>Supports fail-fast or collect-all-failures validation strategies
 *   <li>Produces a {@link ValidationResultCollection} that can be inspected or thrown
 *   <li>Integrates naturally with {@link Validation} and {@link RuleBinding}
 * </ul>
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * ConstructorValidator<User> validator = ConstructorValidator.of(User.class)
 *     .parameter("name", name)
 *         .mustSatisfy(notBlank())
 *     .parameter("email", email)
 *         .mustSatisfy(validEmail())
 *     .collectFailures(); // or .failFast();
 *
 * ValidationResultCollection results = validator.validate();
 * results.throwIfInvalid(); // throws AggregatedValidationException if any violations occur
 * }</pre>
 *
 * <h2>Validation Strategies</h2>
 *
 * <ul>
 *   <li>{@link io.github.jonasnuber.valari.core.strategies.FailFastStrategy Fail-fast} — stops at
 *       the first failing rule.
 *   <li>{@link io.github.jonasnuber.valari.core.strategies.CollectFailuresStrategy Collect-all} —
 *       evaluates all rules and aggregates all failures (default).
 * </ul>
 *
 * <h2>When to Use</h2>
 *
 * <p>Use {@code ConstructorValidator} when:
 *
 * <ul>
 *   <li>Validating constructor or factory parameters
 *   <li>You have independent values that do not exist inside a containing object
 *   <li>You want readable, declarative validation of input parameters
 * </ul>
 *
 * @param <TYPE> the logical target type for which parameters are being validated; used for
 *     contextual information and in error descriptions
 * @see Validation
 * @see ValidationResultCollection
 * @see AggregatedValidationException
 * @see RuleBinding
 * @see NoInputValidator
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public class ConstructorValidator<TYPE> implements NoInputValidator<ValidationResultCollection> {
  private final Class<TYPE> clazz;
  private final List<NoInputValidator<? extends ThrowableResult<?>>> parameterValidators =
      new ArrayList<>();

  private ValidationStrategy<ValidationResultCollection> validationStrategy =
      new CollectFailuresStrategy();

  private ConstructorValidator(Class<TYPE> clazz) {
    this.clazz = Objects.requireNonNull(clazz, "Class must not be null");
  }

  /**
   * Creates a new {@code ConstructorValidator} instance for the specified target class.
   *
   * @param clazz the class representing the target of validation
   * @param <TYPE> the type parameter
   * @return a new instance of {@code ConstructorValidator}
   */
  public static <TYPE> ConstructorValidator<TYPE> of(Class<TYPE> clazz) {
    return new ConstructorValidator<>(clazz);
  }

  /**
   * Begins validation configuration for a constructor parameter or similar value.
   *
   * <p>This method allows defining a named parameter and associating validation rules with it.
   * Example usage:
   *
   * <pre>{@code
   * validator.parameter("email", email)
   *     .mustSatisfy(validEmail());
   * }</pre>
   *
   * @param parameterName the logical name of the parameter (used in error messages)
   * @param parameter the value to validate
   * @param <PARAMETER> the type of the parameter
   * @return a binding that allows attaching a validation rule via {@code mustSatisfy} or {@code
   *     ifPresent}
   */
  public <PARAMETER> RuleBinding<ConstructorValidator<TYPE>, Validation<PARAMETER>> parameter(
      String parameterName, PARAMETER parameter) {
    Objects.requireNonNull(parameterName, "ParameterName must not be null");

    ParameterRuleBinding<TYPE, PARAMETER> parameterBinding =
        new ParameterRuleBinding<>(parameterName, parameter, this);
    parameterValidators.add(parameterBinding);

    return parameterBinding;
  }

  /**
   * A no-op method for visual separation in the fluent DSL.
   *
   * <p>Use {@code and()} to improve readability when chaining multiple parameter validations. This
   * method has no effect on validation behavior and simply returns {@code this}.
   *
   * @return this validator instance (for chaining)
   */
  public ConstructorValidator<TYPE> and() {
    return this;
  }

  /**
   * Sets the validation strategy to fail-fast mode.
   *
   * <p>In fail-fast mode, validation stops on the first failed rule.
   *
   * @return this validator instance (for chaining)
   */
  public ConstructorValidator<TYPE> failFast() {
    validationStrategy = new FailFastStrategy();
    return this;
  }

  /**
   * Sets the validation strategy to collect-all-failures mode.
   *
   * <p>In this mode, all validation rules are evaluated, and all failures are reported together.
   * This is the default behavior.
   *
   * @return this validator instance (for chaining)
   */
  public ConstructorValidator<TYPE> collectFailures() {
    validationStrategy = new CollectFailuresStrategy();
    return this;
  }

  /**
   * Validates all configured parameter rules using the current validation strategy.
   *
   * @return a collection of validation results
   */
  @Override
  public ValidationResultCollection validate() {
    return validationStrategy.validate(
        parameterValidators,
        ValidationDescriptor.builder()
            .validationClass(clazz)
            .labelType(LabelType.PARAMETER)
            .label(clazz.getSimpleName())
            .build());
  }
}
