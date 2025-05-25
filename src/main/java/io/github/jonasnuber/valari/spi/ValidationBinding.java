package io.github.jonasnuber.valari.spi;

/**
 * A binding interface for associating a {@link Validation} rule with a specific field in a validator.
 * <p>
 * This interface enables fluent validation configuration. It is typically returned by a validator
 * when declaring a field, allowing the caller to attach a validation rule using {@link #mustSatisfy(Validation)}.
 *
 * <p>Example usage:
 * <pre>{@code
 * validator.field(User::getEmail, "email")
 *          .mustSatisfy(validEmail());
 * }</pre>
 *
 * @param <V> the type of the validator that owns the binding (used for fluent chaining)
 * @param <F> the type of the field to be validated
 * @see Validation
 * @see Validator
 *
 * @author Jonas Nuber
 */
public interface ValidationBinding<V extends Validator<?, ?>, F> {

    /**
     * Specifies the validation rule that the field must satisfy.
     *
     * @param validation the validation logic for the field
     * @return the owning validator instance for fluent chaining
     */
    V mustSatisfy(Validation<F> validation);
}
