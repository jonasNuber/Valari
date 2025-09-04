package io.github.jonasnuber.valari.spi;

import io.github.jonasnuber.valari.api.results.MessageResolutionContext;
import io.github.jonasnuber.valari.api.results.ValidationState;

import java.util.Locale;
import java.util.function.Function;

/**
 * Common contract for validation result types that support both
 * <em>state inspection</em> and <em>exception escalation</em>.
 * <p>
 * A {@code ThrowingResult} encapsulates the outcome of a validation
 * and provides:
 * <ul>
 *   <li>Access to the underlying {@link ValidationState} (success, failure, skipped).</li>
 *   <li>Convenience methods to check validity ({@link #isValid()}, {@link #isInvalid()}).</li>
 *   <li>Methods to obtain a human-readable message describing the result.</li>
 *   <li>Methods to throw an exception when the result indicates failure.</li>
 * </ul>
 * </p>
 *
 * <h2>Typical usage</h2>
 * <pre>{@code
 * ValidationResult result = validator.test(value);
 *
 * if (result.isInvalid()) {
 *     // Either inspect message/state directly
 *     System.out.println(result.getMessage());
 *
 *     // Or escalate via exception
 *     result.throwIfInvalid();
 * }
 * }</pre>
 *
 * Implementations (such as {@link io.github.jonasnuber.valari.api.results.ValidationResult})
 * are expected to provide concrete state handling and message resolution.
 *
 * @author Jonas Nuber
 */
public interface ThrowingResult {

    /**
     * Throws a default runtime exception if this result indicates failure.
     * <p>
     * Implementations typically use a standard exception type
     * (e.g. {@code InvalidAttributeValueException}) for convenience.
     * </p>
     *
     * @throws RuntimeException if the state is invalid.
     */
    void throwIfInvalid();

    /**
     * Returns the validation state for this result.
     *
     * @return the current {@link ValidationState}.
     */
    ValidationState getState();

    /**
     * Returns a localized message describing this result.
     * <p>
     * Unlike the raw validation message, this is typically a higher-level
     * formatted message including context such as label and state.
     *
     * @param resolver the resolver to use for i18n lookups.
     * @param locale the locale for which to resolve the message.
     * @return the resolved and formatted result message.
     */
    String getMessage(MessageResolver resolver, Locale locale);

    /**
     * Returns a localized message describing this result using the
     * defaults from {@link MessageResolutionContext}.
     *
     * @return the resolved result message.
     */
    default String getMessage() {
        return getMessage(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }

    /**
     * Throws a custom exception created by the given factory if this
     * result indicates failure.
     *
     * @param exceptionFactory a function mapping the result message to an exception.
     * @throws RuntimeException if the state is invalid.
     */
    default void throwIfInvalid(Function<String, ? extends RuntimeException> exceptionFactory) {
        if (isInvalid()) throw exceptionFactory.apply(getMessage());
    }

    /**
     * Returns whether this result represents a valid outcome.
     *
     * @return {@code true} if valid, {@code false} otherwise.
     */
    default boolean isValid() {
        return getState().isValid();
    }

    /**
     * Returns whether this result represents an invalid outcome.
     *
     * @return {@code true} if invalid, {@code false} otherwise.
     */
    default boolean isInvalid() {
        return getState().isInvalid();
    }
}
