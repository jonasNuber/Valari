package io.github.jonasnuber.valari.internal.bindings;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.validators.ConstructorValidator;
import io.github.jonasnuber.valari.api.validators.ValueValidator;
import io.github.jonasnuber.valari.spi.NoInputValidator;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.RuleBinding;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.Objects;


public class ParameterRuleBinding<T, F> implements RuleBinding<ConstructorValidator<T>, Validation<F>>, NoInputValidator<ValidationResult> {
    private final String parameterName;
    private final F parameter;
    private final ConstructorValidator<T> parent;

    private Validator<F, ValidationResult> delegate;

    public ParameterRuleBinding(String parameterName, F parameter, ConstructorValidator<T> parent) {
        this.parameterName = parameterName;
        this.parameter = parameter;
        this.parent = Objects.requireNonNull(parent, "Constructor Validator must not be null");
    }

    @Override
    public ConstructorValidator<T> mustSatisfy(Validation<F> validation) {
        delegate = ValueValidator.with(parameterName, validation);

        return parent;
    }

    @Override
    public ConstructorValidator<T> ifPresent(Validation<F> validation) {
        delegate = ValueValidator.optional(parameterName, validation);

        return parent;
    }

    @Override
    public ValidationResult validate() {
        return delegate.validate(parameter);
    }
}
