package io.github.jonasnuber.valari.base;

import io.github.jonasnuber.valari.exceptions.InvalidAttributeValueException;

/**
 * The ValidationResult class represents the result of a validation process.
 * It can indicate whether the validation was successful (OK) or failed (FAIL), and can
 * initiate an exception throw in case of a failed validation.
 *
 * <p>
 * @author Jonas Nuber
 * </p>
 */
public final class ValidationResult {

	private final boolean valid;
	private final String onErrorMessage;

	private ValidationResult(boolean valid, String onErrorMessage) {
		this.valid = valid;
		this.onErrorMessage = onErrorMessage;
	}

	/**
	 * Returns a ValidationResult with the state OK, representing a successful validation.
	 *
	 * @return The ValidationResult indicating a successful validation.
	 */
	public static ValidationResult ok() {
		return new ValidationResult(true, null);
	}

	/**
	 * Returns a ValidationResult with the state FAILED, representing an unsuccessful validation.
	 *
	 * @param onErrorMessage The message describing the reason for the failed validation.
	 * @return The ValidationResult indicating a failed validation.
	 */
	public static ValidationResult fail(String onErrorMessage) {
		return new ValidationResult(false, onErrorMessage);
	}

	/**
	 * Throws an InvalidAttributeValueException if the ValidationResult is FAILED.
	 * The thrown exception contains the onErrorMessage provided in the {@linkplain #fail(String)}
	 * constructor as well as the specified fieldName to reference where the validation failed.
	 *
	 * @param fieldName The name of the field being tested.
	 * @throws InvalidAttributeValueException The exception thrown on failed validation.
	 */
	public void throwIfInvalid(String fieldName) throws InvalidAttributeValueException {
		if (isInvalid()) {
			throw new InvalidAttributeValueException(
					String.format("The field: \"%s\" is invalid, because %s", fieldName, onErrorMessage));
		}
	}

	/**
	 * Checks if the ValidationResult indicates a valid validation.
	 *
	 * @return True if the validation is valid, otherwise false.
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * Checks if the ValidationResult indicates an invalid validation.
	 *
	 * @return True if the validation is invalid, otherwise false.
	 */
	public boolean isInvalid() {
		return !isValid();
	}
}
