package io.github.jonasnuber.valari.internal.bindings;

import io.github.jonasnuber.valari.api.builders.GenericValidatedBuilder;
import io.github.jonasnuber.valari.api.validators.ConstructorValidator;
import io.github.jonasnuber.valari.spi.RuleBinding;
import io.github.jonasnuber.valari.spi.Validation;

public final class GenericBuilderRuleBinding<T, F> implements RuleBinding<GenericValidatedBuilder<T>, Validation<F>> {
    private final RuleBinding<ConstructorValidator<T>,Validation<F>> delegate;
    private final GenericValidatedBuilder<T> builder;

    public GenericBuilderRuleBinding(RuleBinding<ConstructorValidator<T>,Validation<F>> delegate, GenericValidatedBuilder<T> builder) {
        this.delegate = delegate;
        this.builder = builder;
    }

    @Override
    public GenericValidatedBuilder<T> mustSatisfy(Validation<F> rule) {
        delegate.mustSatisfy(rule);
        return builder;
    }

    @Override
    public GenericValidatedBuilder<T> ifPresent(Validation<F> rule) {
        delegate.ifPresent(rule);
        return builder;
    }
}
