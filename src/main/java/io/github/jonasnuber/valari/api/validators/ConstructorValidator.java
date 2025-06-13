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
import java.util.Objects;

/**
 * A {@code ConstructorValidator} is a fluent, type-safe validator for constructor parameters or similar grouped values.
 * <p>
 * It allows defining validations for named parameters independently of a source object, making it suitable for
 * validating constructor or factory method arguments. You can specify per-parameter validation rules and configure
 * the validation strategy to either fail fast or collect all validation failures.
 * </p>
 *
 * Example usage:
 * <pre>{@code
 * ConstructorValidator<User> validator = ConstructorValidator.of(User.class)
 *     .parameter("name", name)
 *         .mustSatisfy(notBlank())
 *     .parameter("email", email)
 *         .mustSatisfy(validEmail())
 *     .failFast(); // or .collectFailures();
 *
 * ValidationResultCollection results = validator.validate();
 * results.throwIfInvalid(); // throws AggregatedValidationException if any validation failed
 * }</pre>
 *
 * @param <T> the target class for which parameters are being validated (for context/reference only)
 * @see ValidationResultCollection
 * @see io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException
 * @author Jonas Nuber
 */
public class ConstructorValidator<T> implements NoInputValidator<ValidationResultCollection> {
    private final Class<T> clazz;
    private final List<NoInputValidator<ValidationResult>> parameterValidators = new ArrayList<>();

    private ValidationStrategy<T, ValidationResultCollection> validationStrategy;

    private ConstructorValidator(Class<T> clazz) {
        this.clazz = Objects.requireNonNull(clazz, "Class must not be null");
        validationStrategy = new CollectFailuresStrategy<>();
    }

    /**
     * Creates a new {@code ConstructorValidator} instance for the specified target class.
     *
     * @param clazz the class representing the target of validation
     * @param <T>   the type parameter
     * @return a new instance of {@code ConstructorValidator}
     */
    public static <T> ConstructorValidator<T> of(Class<T> clazz) {
        return new ConstructorValidator<>(clazz);
    }

    /**
     * Begins validation configuration for a constructor parameter or similar value.
     * <p>
     * This method allows defining a named parameter and associating validation rules with it.
     * </p>
     *
     * Example usage:
     * <pre>{@code
     * validator.parameter("email", email)
     *     .mustSatisfy(validEmail());
     * }</pre>
     *
     * @param parameterName the logical name of the parameter (used in error messages)
     * @param parameter     the value to validate
     * @param <F>           the type of the parameter
     * @return a binding that allows attaching a validation rule via {@code mustSatisfy} or {@code ifPresent}
     */
    public <F> RuleBinding<ConstructorValidator<T>, Validation<F>> parameter(String parameterName, F parameter) {
        Objects.requireNonNull(parameterName, "ParameterName must not be null");

        ParameterRuleBinding<T, F> parameterBinding = new ParameterRuleBinding<>(parameterName, parameter, this);
        parameterValidators.add(parameterBinding);

        return parameterBinding;
    }

    /**
     * A no-op method for visual separation in the fluent DSL.
     * <p>
     * Use {@code and()} to improve readability when chaining multiple parameter validations.
     * This method has no effect on validation behavior and simply returns {@code this}.
     * </p>
     *
     * @return this validator instance (for chaining)
     */
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

    /**
     * Validates all configured parameter rules using the current validation strategy.
     *
     * @return a collection of validation results
     */
    @Override
    public ValidationResultCollection validate() {
        return validationStrategy.validate(parameterValidators, clazz);
    }
}
