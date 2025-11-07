package io.github.jonasnuber.valari.core.bindings;

import io.github.jonasnuber.valari.api.BaseValidator;
import io.github.jonasnuber.valari.api.Validator;

/**
 * A fluent interface for associating validation rules with a specific input value,
 * such as a field, parameter, or attribute, in a validator.
 * <p>
 * This interface is typically used within a {@link Validator} to define rules for validating
 * individual named inputs of a domain object. It is returned when declaring a validation
 * target using {@code validator.field(...)}, {@code validator.parameter(...)}, etc., and
 * allows attaching validation rules via {@link #mustSatisfy(Object)} or {@link #ifPresent(Object)}.
 * </p>
 *
 * Usage example:
 * <pre>{@code
 * validator.field(User::getEmail, "email")
 *          .mustSatisfy(validEmail());
 *
 * validator.parameter(User::getNickname, "nickname")
 *          .ifPresent(nonBlank());
 * }</pre>
 *
 * @param <VALIDATOR> the type of the validator (used to return the validator for fluent chaining)
 * @param <RULE> the type of the validation rule (e.g., {@code Validation<F>})
 *
 * @author Jonas Nuber
 * @see Validator
 */
@SuppressWarnings("java:S119")
public interface RuleBinding<VALIDATOR extends BaseValidator, RULE> {

    /**
     * Specifies that the field must satisfy the given validation rule.
     *
     * @param rule the validation rule to apply (e.g., {@code notBlank()}, {@code between()})
     * @return the validator instance for fluent chaining
     */
    VALIDATOR mustSatisfy(RULE rule);

    /**
     * Applies the rule only if the field's value is present (i.e., not {@code null}).
     * <p>
     * This is useful for optional fields where {@code null} is valid, but non-null values
     * must still adhere to a constraint.
     * </p>
     *
     * @param rule the validation rule to apply if the field value is non-null
     * @return the validator instance for fluent chaining
     */
    VALIDATOR ifPresent(RULE rule);
}
