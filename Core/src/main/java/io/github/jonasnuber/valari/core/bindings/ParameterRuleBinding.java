package io.github.jonasnuber.valari.core.bindings;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.RuleBinding;
import io.github.jonasnuber.valari.core.ConstructorValidator;
import io.github.jonasnuber.valari.core.ValidationResult;
import io.github.jonasnuber.valari.core.ValueValidator;
import java.util.Objects;

/**
 * A binding that associates a constructor parameter value with a {@link Validation} rule.
 *
 * <p>This class is a key component of {@link ConstructorValidator} and is responsible for
 * validating constructor parameters at object-creation time. It acts in two roles:
 *
 * <ul>
 *   <li>As a {@link RuleBinding}, it allows attaching validation rules to a constructor parameter
 *       using either {@link #mustSatisfy(Validation)} for mandatory rules or {@link
 *       #ifPresent(Validation)} for optional rules.
 *   <li>As a {@link NoInputValidator}, it executes the assigned validation rule against the known
 *       constructor-parameter value and produces a {@link ValidationResult}.
 * </ul>
 *
 * <p>Usage typically occurs through the fluent API exposed by {@link ConstructorValidator}:
 *
 * <pre>{@code
 * validator.parameter("age", age)
 *          .mustSatisfy(greaterThan(0));
 * }</pre>
 *
 * <p>This class is part of the internal validation infrastructure and is not intended to be
 * instantiated directly by consumers of the library.
 *
 * @param <TYPE> the type of the object being constructed
 * @param <PARAMETER> the type of the constructor parameter being validated
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public class ParameterRuleBinding<TYPE, PARAMETER>
    implements RuleBinding<ConstructorValidator<TYPE>, Validation<PARAMETER>>,
        NoInputValidator<ValidationResult> {
  private final String parameterName;
  private final PARAMETER parameter;
  private final ConstructorValidator<TYPE> parent;

  private Validator<PARAMETER, ValidationResult> delegate;

  /**
   * Creates a new binding between a constructor parameter and the validation rules that apply to
   * it.
   *
   * <p>The parameter name is used for error messages and labeling. The value is stored and
   * validated during construction-time validation. The parent validator manages this binding as
   * part of a larger validation chain.
   *
   * @param parameterName the name of the constructor parameter; used for reporting (never {@code
   *     null})
   * @param parameter the actual value of the parameter that will be validated
   * @param parent the parent {@link ConstructorValidator} coordinating validation (never {@code
   *     null})
   * @throws NullPointerException if {@code parameterName} or {@code parent} is {@code null}
   */
  public ParameterRuleBinding(
      String parameterName, PARAMETER parameter, ConstructorValidator<TYPE> parent) {
    this.parameterName = Objects.requireNonNull(parameterName, "ParameterName must not be null");
    this.parameter = parameter;
    this.parent = Objects.requireNonNull(parent, "Constructor Validator must not be null");
  }

  /**
   * Assigns a mandatory validation rule to this constructor parameter.
   *
   * <p>The provided {@link Validation} is wrapped in a {@link ValueValidator} so that the resulting
   * {@link ValidationResult} automatically includes contextual information such as parameter name
   * and label type.
   *
   * @param validation the validation rule to apply (never {@code null})
   * @return the parent {@link ConstructorValidator}, enabling fluent chaining
   * @throws NullPointerException if {@code validation} is {@code null}
   */
  @Override
  public ConstructorValidator<TYPE> mustSatisfy(Validation<PARAMETER> validation) {
    delegate = ValueValidator.with(parameterName, validation).withLabelType(LabelType.PARAMETER);

    return parent;
  }

  /**
   * Assigns an optional validation rule to this parameter.
   *
   * <p>The validation rule is executed only if the parameter value is non-{@code null}. If the
   * value is {@code null}, validation succeeds implicitly.
   *
   * @param validation the validation rule to apply when the parameter value is present (never
   *     {@code null})
   * @return the parent {@link ConstructorValidator}, enabling fluent chaining
   * @throws NullPointerException if {@code validation} is {@code null}
   */
  @Override
  public ConstructorValidator<TYPE> ifPresent(Validation<PARAMETER> validation) {
    delegate =
        ValueValidator.optional(parameterName, validation).withLabelType(LabelType.PARAMETER);

    return parent;
  }

  /**
   * Executes the validation logic associated with this constructor parameter.
   *
   * <p>If neither {@link #mustSatisfy(Validation)} nor {@link #ifPresent(Validation)} has been
   * called beforehand, invoking this method indicates a misconfigured validation chain and results
   * in an {@link IllegalStateException}.
   *
   * @return the {@link ValidationResult} produced by the rule
   * @throws IllegalStateException if no validation rule was configured before calling this method
   */
  @Override
  public ValidationResult validate() {
    if (delegate == null) {
      throw new IllegalStateException(
          "No validation rule was set. Call mustSatisfy(...) or ifPresent(...) before validation");
    }

    return delegate.validate(parameter);
  }
}
