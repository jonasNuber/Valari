package io.github.jonasnuber.valari.api.exceptions;

import java.io.Serial;

/**
 * Exception thrown when an attribute value is deemed invalid based on a validation result.
 * This exception type is used for the throw statement of an invalid ValidationResult.
 *
 * @author  Jonas Nuber
 *
 */
public class InvalidAttributeValueException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = 2590905136290636270L;

	/**
	 * Constructs a new InvalidAttributeValueException with the specified error message.
	 *
	 * @param errorMessage The error message describing the cause of the exception.
	 */
	public InvalidAttributeValueException(String errorMessage) {
		super(errorMessage);
	}
}
