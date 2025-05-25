package io.github.jonasnuber.valari.api.exceptions;

import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.ValidationResultCollection;

import java.io.Serial;

/**
 * Exception representing a failure during validation that includes multiple {@link ValidationResult} errors.
 * <p>
 * This exception is typically thrown after collecting all validation errors for a given object
 * in a {@code ValidationResultCollection}. It aggregates those errors into a single exception to
 * simplify error handling and reporting.
 * </p>
 *
 * <p>
 * The message includes a summary of the number of failures and a detailed listing of each
 * failed field and its corresponding error message.
 * </p>
 *
 * @author Jonas Nuber
 */
public class AggregatedValidationException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 2591362906312346270L;

    /**
     * Constructs a new {@code AggregatedValidationException} with a combined error message.
     * <p>
     * This constructor is typically used by {@link ValidationResultCollection}
     * to report multiple validation failures in a single, aggregated exception.
     * </p>
     *
     * @param errorMessage the formatted message summarizing all validation issues
     */
    public AggregatedValidationException(String errorMessage) {
        super(errorMessage);
    }
}
