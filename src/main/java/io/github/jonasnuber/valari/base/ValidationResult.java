package io.github.jonasnuber.valari.base;

import io.github.jonasnuber.valari.exceptions.InvalidAttributeValueException;

/**
 * The ValidationResult class represents the result of a validation process.
 * It can indicate whether the validation was successful (OK) or failed (FAIL), and can
 * initiate an exception throw in case of a failed validation.
 *
 * <p>
 *
 * @author Jonas Nuber
 * </p>
 */
public final class ValidationResult {

    private final boolean valid;
    private final String causeDescription;
    private String fieldName;

    private ValidationResult(boolean valid, String fieldName, String causeDescription) {
        this.valid = valid;
        this.fieldName = fieldName;
        this.causeDescription = causeDescription;
    }

    /**
     * Returns a ValidationResult with the state OK, representing a successful validation.
     *
     * @return The ValidationResult indicating a successful validation.
     */
    public static ValidationResult ok() {
        return new ValidationResult(true, "Unknown", null);
    }

    /**
     * Returns a ValidationResult with the state FAILED, representing an unsuccessful validation.
     * <p>
     * The field which was validated is not named and set to "Unknown"
     *
     * @param causeDescription The message describing the reason for the failed validation.
     * @return The ValidationResult indicating a failed validation.
     */
    public static ValidationResult fail(String causeDescription) {
        return new ValidationResult(false, "Unknown", causeDescription);
    }

    /**
     * Returns a ValidationResult with the state FAILED, representing an unsuccessful validation.
     * <p>
     * The field which was validated is named and will show in the Exception Message.
     *
     * @param causeDescription The message describing the reason for the failed validation.
     * @param fieldName        the name of the field which was validated.
     * @return The ValidationResult indicating a failed validation.
     */
    public static ValidationResult fail(String causeDescription, String fieldName) {
        return new ValidationResult(false, fieldName, causeDescription);
    }

    /**
     * Throws an InvalidAttributeValueException if the ValidationResult is FAILED. The thrown
     * exception contains the causeDescription provided in the {@linkplain #fail(String)} constructor as
     * well as the specified fieldName to reference where the validation failed.
     *
     * @param fieldName The name of the field being tested.
     * @throws InvalidAttributeValueException The exception thrown on failed validation.
     */
    public void throwIfInvalid(String fieldName) throws InvalidAttributeValueException {
        this.fieldName = fieldName;
        throwIfInvalid();
    }

    /**
     * Throws an InvalidAttributeValueException if the ValidationResult is FAILED. The thrown
     * exception contains the causeDescription and fieldName provided in the {@linkplain #fail(String)} or
     * {@linkplain #fail(String, String)} constructor to reference where the validation failed.
     *
     * @throws InvalidAttributeValueException The exception thrown on failed validation.
     */
    public void throwIfInvalid() throws InvalidAttributeValueException {
        if (isInvalid()) {
            throw new InvalidAttributeValueException(getErrorMessage());
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

    /**
     * Returns the name for the field which was validated. Default is "Unknown".
     *
     * @return the fieldName
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Returns the description of why the validation failed.
     *
     * @return the cause description
     */
    public String getCauseDescription() {
        return causeDescription;
    }

    /**
     * Returns a formatted error message combining the field name and the cause of the failure.
     * <p>
     * This message is suitable for logging or user feedback and is also used as the message
     * in {@link InvalidAttributeValueException}.
     * </p>
     *
     * @return a human-readable error message describing the validation failure
     */
    public String getErrorMessage() {
        return String.format("The field: \"%s\" is invalid: %s", fieldName, causeDescription);
    }
}
