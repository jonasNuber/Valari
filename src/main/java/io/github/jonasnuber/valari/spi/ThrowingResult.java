package io.github.jonasnuber.valari.spi;

import io.github.jonasnuber.valari.api.results.MessageResolutionContext;
import io.github.jonasnuber.valari.api.results.ValidationState;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Common contract for validation result types that can throw an exception
 * if the validation failed.
 *
 * @author Jonas Nuber
 */
public interface ThrowingResult {

    /**
     * Throws an exception if the validation result is invalid.
     */
    void throwIfInvalid();

    ValidationState getState();

    String getMessage(MessageResolver resolver, Locale locale);

    default String getMessage() {
        return getMessage(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }

    default void throwIfInvalid(Function<String, ? extends RuntimeException> exceptionFactory) {
        if (isInvalid()) throw exceptionFactory.apply(getMessage());
    }

    default boolean isValid() {
        return getState().isValid();
    }

    default boolean isInvalid() {
        return getState().isInvalid();
    }
}
