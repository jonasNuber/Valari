package io.github.jonasnuber.valari.internal.bindings;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.validators.DomainValidator;
import io.github.jonasnuber.valari.api.SimpleValidation;
import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.spi.RuleBinding;
import io.github.jonasnuber.valari.spi.Validator;

import java.util.Objects;
import java.util.function.Function;

/**
 * Internal binding that connects a specific field of a domain object to a corresponding {@link Validation} rule.
 *
 * <p>This class is primarily used by {@link DomainValidator} to create fluent, composable field validations.
 * It serves a dual role:</p>
 * <ul>
 *   <li>As a {@link RuleBinding}, it allows assignment of validation rules via {@link #mustSatisfy(Validation)} or {@link #ifPresent(Validation)}.</li>
 *   <li>As a {@link Validator}, it performs validation logic on the associated field during evaluation.</li>
 * </ul>
 *
 * <p>Example usage:</p>
 * <pre>{@code
 * validator.field("email", User::getEmail)
 *          .mustSatisfy(validEmail());
 * }</pre>
 *
 * <p>This class is internal to the validation infrastructure and should not be used directly outside of {@code valari}'s fluent API.</p>
 *
 * @param <T> the type of the object being validated
 * @param <F> the type of the field extracted from the object for validation
 * @author Jonas Nuber
 */
public final class FieldRuleBinding<T, F> implements RuleBinding<DomainValidator<T>, Validation<F>>, Validator<T, ValidationResult> {
    private final DomainValidator<T> parent;
    private final String fieldName;
    private final Function<T, F> valueExtractor;

    private Validation<F> validation;

    /**
     * Creates a new binding between a domain object's field and its validation logic.
     *
     * @param fieldName      the name of the field being validated (used in error messages); must not be {@code null}
     * @param valueExtractor function to extract the field value from the object being validated; must not be {@code null}
     * @param parent         the parent {@link DomainValidator} managing this binding; must not be {@code null}
     * @throws NullPointerException if any parameter is {@code null}
     */
    public FieldRuleBinding(String fieldName, Function<T, F> valueExtractor, DomainValidator<T> parent) {
        this.fieldName = Objects.requireNonNull(fieldName, "FieldName of the value to validate must not be null");
        this.valueExtractor = Objects.requireNonNull(valueExtractor, "Extractor Method to get value for validation must not be null");
        this.parent = Objects.requireNonNull(parent, "Parent Validator must not be null");
    }

    /**
     * Associates a validation rule with this field.
     *
     * @param rule the validation rule to apply (must not be {@code null})
     * @return the parent validator, enabling fluent chaining
     * @throws NullPointerException if {@code rule} is {@code null}
     */
    @Override
    public DomainValidator<T> mustSatisfy(Validation<F> rule) {
        this.validation = Objects.requireNonNull(rule, "validation must not be null");

        return parent;
    }

    /**
     * Applies the given validation rule only if the field value is non-null.
     * <p>
     * If the value is {@code null}, validation is skipped and considered successful.
     * </p>
     *
     * @param rule the validation rule to apply if the field value is present (must not be {@code null})
     * @return the parent validator, enabling fluent chaining
     * @throws NullPointerException if {@code rule} is {@code null}
     */
    @Override
    public DomainValidator<T> ifPresent(Validation<F> rule) {
        this.validation = SimpleValidation.<F>from(Objects::isNull, "")
                .or(Objects.requireNonNull(rule, "validation must not be null"));
        return parent;
    }

    /**
     * Validates the field of the given object using the bound validation rule.
     *
     * @param toValidate the object to validate
     * @return the result of validation, including failure reason and field name if invalid
     * @throws NullPointerException if {@code toValidate} is {@code null}
     */
    @Override
    public ValidationResult validate(T toValidate) {
        Objects.requireNonNull(toValidate, "Object to validate must not be null");

        F value = valueExtractor.apply(toValidate);
        ValidationResult result = validation.test(value);

        if (result.isInvalid()) {
            return ValidationResult.fail(result.getCauseDescription(), fieldName);
        }

        return result;
    }

    /**
     * Validates the object and throws an exception if the field is invalid.
     *
     * @param toValidate the object to validate
     * @throws io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException if validation fails
     */
    @Override
    public void validateAndThrow(T toValidate) {
        validate(toValidate).throwIfInvalid(fieldName);
    }
}
