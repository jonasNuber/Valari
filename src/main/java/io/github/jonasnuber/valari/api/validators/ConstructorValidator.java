package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.internal.strategies.ValidationStrategy;
import io.github.jonasnuber.valari.internal.bindings.ParameterRuleBinding;
import io.github.jonasnuber.valari.internal.strategies.CollectFailuresStrategy;
import io.github.jonasnuber.valari.internal.strategies.FailFastStrategy;
import io.github.jonasnuber.valari.spi.NoInputValidator;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.RuleBinding;

import java.util.ArrayList;
import java.util.List;

public class ConstructorValidator<T> implements NoInputValidator<ValidationResultCollection> {
    private final Class<T> clazz;
    private final List<NoInputValidator<ValidationResult>> parameterValidators = new ArrayList<>();

    private ValidationStrategy<T, ValidationResultCollection> validationStrategy;

    private ConstructorValidator(Class<T> clazz) {
        this.clazz = clazz;
        validationStrategy = new CollectFailuresStrategy<>();
    }

    public static <T> ConstructorValidator<T> of(Class<T> clazz) {
        return new ConstructorValidator<>(clazz);
    }

    public <F> RuleBinding<ConstructorValidator<T>, Validation<F>> parameter(String parameterName, F parameter) {
        ParameterRuleBinding<T, F> parameterBinding = new ParameterRuleBinding<>(parameterName, parameter, this);
        parameterValidators.add(parameterBinding);

        return parameterBinding;
    }

    public ConstructorValidator<T> and() {return this;}

    /**
     * Sets the validation strategy to fail-fast mode.
     * <p>
     * In fail-fast mode, validation stops on the first failed rule.
     * </p>
     *
     * @return this validator instance (for chaining)
     */
    public ConstructorValidator<T> failFast() {
        validationStrategy = new FailFastStrategy<>();
        return this;
    }

    /**
     * Sets the validation strategy to collect-all-failures mode.
     * <p>
     * In this mode, all validation rules are evaluated, and all failures are reported together.
     * This is the default behavior.
     * </p>
     *
     * @return this validator instance (for chaining)
     */
    public ConstructorValidator<T> collectFailures() {
        validationStrategy = new CollectFailuresStrategy<>();
        return this;
    }

    @Override
    public ValidationResultCollection validate() {
        return validationStrategy.validate(parameterValidators, clazz);
    }
}
