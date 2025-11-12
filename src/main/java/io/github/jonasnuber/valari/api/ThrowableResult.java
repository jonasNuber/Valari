package io.github.jonasnuber.valari.api;

import java.util.function.Function;

@SuppressWarnings("java:S119")
public interface ThrowableResult<SELF extends ThrowableResult<SELF>> extends Result<SELF> {

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
     * Throws a custom exception created by the given factory if this
     * result indicates failure.
     *
     * @param exceptionFactory a function mapping the result message to an exception.
     * @throws RuntimeException if the state is invalid.
     */
    default void throwIfInvalid(Function<String, ? extends RuntimeException> exceptionFactory) {
        if (isInvalid()) throw exceptionFactory.apply(System.lineSeparator() + getDetailedMessage());
    }
}
