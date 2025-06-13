package io.github.jonasnuber.valari.internal.bindings;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.validators.ConstructorValidator;
import io.github.jonasnuber.valari.api.validators.ValueValidator;
import io.github.jonasnuber.valari.spi.NoInputValidator;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.RuleBinding;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.Objects;

/**
 * Internal binding that connects a constructor parameter value to a corresponding {@link Validation} rule.
 *
 * <p>This class is primarily used by {@link ConstructorValidator} to define validation logic
 * for constructor parameters in a fluent style. It serves a dual purpose:</p>
 * <ul>
 *   <li>As a {@link RuleBinding}, it allows binding a validation rule via {@link #mustSatisfy(Validation)} or {@link #ifPresent(Validation)}.</li>
 *   <li>As a {@link NoInputValidator}, it performs validation against a known parameter value during construction-time validation.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * validator.parameter("age", age)
 *          .mustSatisfy(greaterThan(0));
 * }</pre>
 *
 * <p>This class is internal to the validation infrastructure and should not be used directly outside of {@code valari}'s fluent API.</p>
 *
 * @param <T> the type of the target object being constructed
 * @param <F> the type of the constructor parameter being validated
 *
 * @author Jonas Nuber
 */
public final class ParameterRuleBinding<T, F> implements RuleBinding<ConstructorValidator<T>, Validation<F>>, NoInputValidator<ValidationResult> {
    private final String parameterName;
    private final F parameter;
    private final ConstructorValidator<T> parent;

    private Validator<F, ValidationResult> delegate;

    /**
     * Creates a new binding between a constructor parameter and its validation logic.
     *
     * @param parameterName the name of the constructor parameter (used in error messages); must not be {@code null}
     * @param parameter     the actual value of the parameter to validate
     * @param parent        the parent {@link ConstructorValidator} managing this binding; must not be {@code null}
     * @throws NullPointerException if {@code parameterName} or {@code parent} is {@code null}
     */
    public ParameterRuleBinding(String parameterName, F parameter, ConstructorValidator<T> parent) {
        this.parameterName = Objects.requireNonNull(parameterName, "ParameterName must not be null");
        this.parameter = parameter;
        this.parent = Objects.requireNonNull(parent, "Constructor Validator must not be null");
    }

    /**
     * Associates a mandatory validation rule with this constructor parameter.
     *
     * @param validation the validation rule to apply (must not be {@code null})
     * @return the parent validator, enabling fluent chaining
     * @throws NullPointerException if {@code validation} is {@code null}
     */
    @Override
    public ConstructorValidator<T> mustSatisfy(Validation<F> validation) {
        delegate = ValueValidator.with(parameterName, validation);

        return parent;
    }

    /**
     * Applies the given validation rule only if the constructor parameter is non-null.
     * <p>
     * If the value is {@code null}, validation is skipped and considered successful.
     * </p>
     *
     * @param validation the validation rule to apply if the parameter value is present (must not be {@code null})
     * @return the parent validator, enabling fluent chaining
     * @throws NullPointerException if {@code validation} is {@code null}
     */
    @Override
    public ConstructorValidator<T> ifPresent(Validation<F> validation) {
        delegate = ValueValidator.optional(parameterName, validation);

        return parent;
    }

    /**
     * Validates the constructor parameter using the assigned validation rule.
     *
     * @return the result of validation, including failure reason and parameter name if invalid
     * @throws IllegalStateException if no validation rule was set via {@link #mustSatisfy} or {@link #ifPresent}
     */
    @Override
    public ValidationResult validate() {
        if(delegate == null) {
            throw new IllegalStateException("No validation rule was set. Call mustSatisfy(...) or ifPresent(...) before validation");
        }

        return delegate.validate(parameter);
    }
}
