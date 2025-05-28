package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.internal.domain.CollectFailuresStrategy;
import io.github.jonasnuber.valari.internal.domain.NestedValidationBinding;
import io.github.jonasnuber.valari.internal.domain.FailFastStrategy;
import io.github.jonasnuber.valari.internal.domain.FieldValidationBinding;
import io.github.jonasnuber.valari.internal.ValidationStrategy;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.ValidationBinding;
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
 * <p>
 * Example usage:
 * <pre>{@code
 * DomainValidator<User> validator = DomainValidator.of(User.class)
 *     .field(User::getName, "name")
 *          .mustSatisfy(notBlank())
 *     .field(User::getNickname, "nickname")
 *          .ifPresent(notBlank()) // optional field, validated only if non-null
 *     .field(User::getEmail, "email")
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
     *
     * @param extractor the function to extract the field value from the object
     * @param fieldName the logical name of the field (used in error reporting)
     * @param <F>       the field type
     * @return a binding that allows attaching a validation rule via {@code mustSatisfy} or {@code ifPresent}
     */
    public <F> ValidationBinding<DomainValidator<T>, Validation<F>> field(Function<T, F> extractor, String fieldName) {
        Objects.requireNonNull(extractor, "Extractor Function must not be null");
        Objects.requireNonNull(fieldName, "FieldName must not be null");

        FieldValidationBinding<T,F> fieldValidationBinding = new FieldValidationBinding<>(this,
                extractor, fieldName);
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
     * <p>Example usage:</p>
     * <pre>{@code
     * DomainValidator<User> validator = DomainValidator.of(User.class)
     *     .field(User::getName, "name")
     *         .mustSatisfy(notBlank())
     *     .nested(User::getAddress, "address")
     *         .mustSatisfy(
     *             DomainValidator.of(Address.class)
     *                 .field(Address::getStreet, "street").mustSatisfy(notBlank())
     *                 .and()
     *                 .field(Address::getZipCode, "zipCode").mustSatisfy(validZip())
     *         );
     * }</pre>
     *
     * @param extractor the function to extract the nested object
     * @param fieldName the logical name of the nested field (used in error messages)
     * @param <F>       the type of the nested object
     * @return a binding that allows specifying required or optional nested validation
     */
    public <F> ValidationBinding<DomainValidator<T>, DomainValidator<F>> nested(Function<T, F> extractor, String fieldName) {
        Objects.requireNonNull(extractor, "Extractor Function must not be null");
        Objects.requireNonNull(fieldName, "FieldName must not be null");

        NestedValidationBinding<T,F> nestedValidationBinding = new NestedValidationBinding<>(this, extractor, fieldName);
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
     * <p>Example usage:</p>
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

        return validationStrategy.validate(toValidate, validationBindings, clazz);
    }

    /**
     * Validates the given object and throws an {@link AggregatedValidationException}
     * if any validations fail.
     *
     * @param toValidate the object to validate
     * @throws AggregatedValidationException if any field fails validation
     */
    @Override
    public void validateAndThrow(T toValidate) throws AggregatedValidationException {
        validate(toValidate).throwIfInvalid();
    }

    /**
     * Sets the validation strategy to fail-fast mode.
     * <p>
     * In fail-fast mode, validation stops on the first failed rule.
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
     *
     * @return this validator instance (for chaining)
     */
    public DomainValidator<T> collectFailures() {
        validationStrategy = new CollectFailuresStrategy<>();
        return this;
    }
}
