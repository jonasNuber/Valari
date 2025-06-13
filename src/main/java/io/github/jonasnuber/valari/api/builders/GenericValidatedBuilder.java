package io.github.jonasnuber.valari.api.builders;

import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.api.validators.ConstructorValidator;
import io.github.jonasnuber.valari.internal.bindings.GenericBuilderRuleBinding;
import io.github.jonasnuber.valari.internal.bindings.ParameterRuleBinding;
import io.github.jonasnuber.valari.spi.RuleBinding;
import io.github.jonasnuber.valari.spi.ValidatedBuilder;
import io.github.jonasnuber.valari.spi.Validation;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

public class GenericValidatedBuilder<T> implements ValidatedBuilder<T, ValidationResultCollection> {
    private final Class<T> targetClass;
    private final List<Object> parameterValues = new ArrayList<>();
    private final ConstructorValidator<T> validator;

    private GenericValidatedBuilder(Class<T> targetClass) {
        this.targetClass = targetClass;
        validator = ConstructorValidator.of(targetClass);
    }

    public static <T> GenericValidatedBuilder<T> forType(Class<T> targetClass) {
        return new GenericValidatedBuilder<>(targetClass);
    }

    public <F> RuleBinding<GenericValidatedBuilder<T>, Validation<F>> parameter(String parameterName, F parameter) {
        RuleBinding<ConstructorValidator<T>, Validation<F>> parameterRuleBinding = validator.parameter(parameterName, parameter);
        GenericBuilderRuleBinding<T, F> builderRuleBinding = new GenericBuilderRuleBinding<>(parameterRuleBinding, this);

        parameterValues.add(parameter);

        return builderRuleBinding;
    }

    @Override
    public T build() {
        validateAndThrow();

        Class<?>[] parameterTypes = (Class<?>[]) parameterValues.stream().map(Object::getClass).toArray();


        try {
            Constructor<T> constructor = targetClass.getConstructor(parameterTypes);

        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    @Override
    public ValidationResultCollection validate() {
        return validator.validate();
    }

}
