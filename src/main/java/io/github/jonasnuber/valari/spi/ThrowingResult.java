package io.github.jonasnuber.valari.spi;

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
}
