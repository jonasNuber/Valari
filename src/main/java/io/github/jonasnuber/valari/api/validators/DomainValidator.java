package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.internal.strategies.CollectFailuresStrategy;
import io.github.jonasnuber.valari.internal.bindings.NestedRuleBinding;
import io.github.jonasnuber.valari.internal.strategies.FailFastStrategy;
import io.github.jonasnuber.valari.internal.bindings.FieldRuleBinding;
import io.github.jonasnuber.valari.internal.strategies.ValidationStrategy;
import io.github.jonasnuber.valari.spi.NoInputValidator;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.RuleBinding;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@code DomainValidator} is a fluent, type-safe validator for domain models.
 * <p>
 * It supports defining per-field and nested validation logic using extractors and rules,
 * with options for mandatory ({@code mustSatisfy(...)}) and optional ({@code ifPresent(...)}) validation.
 * It also provides two validation strategies: fail-fast and collect-all-failures.
 * </p>
 *
 * Example usage:
 * <pre>{@code
 * DomainValidator<User> validator = DomainValidator.of(User.class)
 *     .field("name", User::getName)
 *          .mustSatisfy(notBlank())
 *     .field("nickname", User::getNickname)
 *          .ifPresent(notBlank()) // optional field, validated only if non-null
 *     .field("email", User::getEmail)
 *          .mustSatisfy(validEmail());
 *
 * ValidationResultCollection results = validator.validate(user);
 * results.throwIfInvalid(); // throws AggregatedValidationException if any validation failed
 * }</pre>
 *
 * @param <T> the type of object to validate
 * @see ValidationResultCollection
 * @see AggregatedValidationException
 *
 * @author Jonas Nuber
 */
public class DomainValidator<T> implements Validator<T, ValidationResultCollection> {
    private final Class<T> clazz;
    private final List<Validator<T, ValidationResult>> validationBindings = new ArrayList<>();

    private ValidationStrategy<T, ValidationResultCollection> validationStrategy;

    private DomainValidator(Class<T> clazz) {
        this.clazz = Objects.requireNonNull(clazz, "Class must not be null");
        validationStrategy = new CollectFailuresStrategy<>();
    }

    /**
     * Creates a new {@code DomainValidator} instance for the specified type.
     *
     * @param clazz the class of the object to validate
     * @param <T>   the type parameter
     * @return a new instance of {@code DomainValidator}
     */
    public static <T> DomainValidator<T> of(Class<T> clazz) {
        return new DomainValidator<>(clazz);
    }

    /**
     * Begins validation configuration for a specific field.
     * <p>
     * This method allows binding a field extractor and associating it with a name,
     * which will be used in validation results.
     * </p>
     *
     * Example usage:
     * <pre>{@code
     * DomainValidator<User> validator = DomainValidator.of(User.class)
     *     .field("name", User::getName)
     *         .mustSatisfy(notBlank());
     * }</pre>
     *
     * @param fieldName the logical name of the field (used in error reporting)
     * @param extractor the function to extract the field value from the object
     * @param <F>       the field type
     * @return a binding that allows attaching a validation rule via {@code mustSatisfy} or {@code ifPresent}
     */
    public <F> RuleBinding<DomainValidator<T>, Validation<F>> field(String fieldName, Function<T, F> extractor) {
        Objects.requireNonNull(fieldName, "FieldName must not be null");
        Objects.requireNonNull(extractor, "Extractor Function must not be null");

        FieldRuleBinding<T,F> fieldValidationBinding = new FieldRuleBinding<>(fieldName, extractor, this);
        validationBindings.add(fieldValidationBinding);

        return fieldValidationBinding;
    }

    /**
     * Begins nested validation for a composite (nested) field.
     * <p>
     * This method allows specifying a sub-validator for a nested object, enabling recursive validation.
     * The nested validator can itself contain validations for its fields using the same fluent API.
     * You can choose whether the nested field is required using {@code mustSatisfy(...)} or optional using {@code ifPresent(...)}.
     * </p>
     *
     * Example usage:
     * <pre>{@code
     * DomainValidator<User> validator = DomainValidator.of(User.class)
     *     .field("name", User::getName)
     *         .mustSatisfy(notBlank())
     *     .nested("address", User::getAddress)
     *         .mustSatisfy(
     *             DomainValidator.of(Address.class)
     *                 .field("street", Address::getStreet).mustSatisfy(notBlank())
     *                 .and()
     *                 .field("zipCode", Address::getZipCode).mustSatisfy(validZip())
     *         );
     * }</pre>
     *
     * @param fieldName the logical name of the nested field (used in error messages)
     * @param extractor the function to extract the nested object
     * @param <F>       the type of the nested object
     * @return a binding that allows specifying required or optional nested validation
     */
    public <F> RuleBinding<DomainValidator<T>, DomainValidator<F>> nested(String fieldName, Function<T, F> extractor) {
        Objects.requireNonNull(fieldName, "FieldName must not be null");
        Objects.requireNonNull(extractor, "Extractor Function must not be null");

        NestedRuleBinding<T,F> nestedValidationBinding = new NestedRuleBinding<>(fieldName, extractor, this);
        validationBindings.add(nestedValidationBinding);

        return nestedValidationBinding;
    }

    /**
     * A no-op method for visual separation in the fluent DSL.
     * <p>
     * Use {@code and()} to improve readability when chaining multiple field validations.
     * This method has no effect on validation behavior and simply returns {@code this}.
     * </p>
     *
     * Example usage:
     * <pre>{@code
     * DomainValidator<User> validator = DomainValidator.of(User.class)
     *     .field(User::getName, "name")
     *         .mustSatisfy(notBlank())
     *     .and()
     *     .field(User::getEmail, "email")
     *         .mustSatisfy(validEmail());
     * }</pre>
     *
     * @return this validator instance (for chaining)
     */
    public DomainValidator<T> and(){
        return this;
    }

    /**
     * Sets the validation strategy to fail-fast mode.
     * <p>
     * In fail-fast mode, validation stops on the first failed rule.
     * </p>
     *
     * @return this validator instance (for chaining)
     */
    public DomainValidator<T> failFast() {
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
    public DomainValidator<T> collectFailures() {
        validationStrategy = new CollectFailuresStrategy<>();
        return this;
    }

    /**
     * Validates the given object using the current validation strategy.
     * <p>
     * This includes both field-level and nested object validations if configured.
     * </p>
     *
     * @param toValidate the object to validate
     * @return a collection of validation results
     */
    @Override
    public ValidationResultCollection validate(T toValidate) {
        Objects.requireNonNull(toValidate, "Object to validate must not be null");

        return validationStrategy.validate(validationBindings
                .stream()
                .map(binding ->
                        (NoInputValidator<ValidationResult>) () -> binding.validate(toValidate))
                .toList(), clazz);
    }
}
