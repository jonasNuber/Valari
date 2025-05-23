package io.github.jonasnuber.valari.helpers;

import io.github.jonasnuber.valari.base.SimpleValidation;
import io.github.jonasnuber.valari.base.Validation;

/**
 * Utility class providing predefined validations for strings.
 * These validations define conditions that a string must meet to be considered valid.
 *
 * <p>
 * @author  Jonas Nuber
 * </p>
 */
public final class StringValidationHelpers {

	private StringValidationHelpers() throws IllegalAccessException {
		throw new IllegalAccessException("StringValidationHelpers is a utility class and cannot be instantiated");
	}

	/**
	 * Returns a validation that passes only if the string is neither null nor empty.
	 *
	 * @return the Validation for not empty
	 */
	public static Validation<String> notEmpty(){
		return SimpleValidation.from(s -> s != null && !s.trim().isEmpty(), "must not be blank");
	}

	/**
	 * Returns a validation that passes only if the string has exactly the specified number of characters.
	 *
	 * @param size The exact number of characters.
	 * @return The validation for exact string length.
	 */
	public static Validation<String> exactly(int size) {
		return SimpleValidation.from(s -> s.length() == size, String.format("must have exactly %s chars", size));
	}

	/**
	 * Returns a validation that passes only if the string has more than the specified number of characters.
	 *
	 * @param minimum The minimum number of characters.
	 * @return The validation for minimum string length.
	 */
	public static Validation<String> moreThan(int minimum) {
		return SimpleValidation.from(s -> s.length() > minimum, String.format("must have more than %s chars.", minimum));
	}

	/**
	 * Returns a validation that passes only if the string has fewer than the specified number of characters.
	 *
	 * @param maximum The maximum number of characters.
	 * @return The validation for maximum string length.
	 */
	public static Validation<String> lessThan(int maximum) {
		return SimpleValidation.from(s -> s.length() < maximum, String.format("must have less than %s chars", maximum));
	}

	/**
	 * Returns a validation that passes only if the string length is between the specified minimum and maximum values (exclusive).
	 *
	 * @param minSize The minimum number of characters.
	 * @param maxSize The maximum number of characters.
	 * @return The validation for string length within a range.
	 */
	public static Validation<String> between(int minSize, int maxSize) {
		return moreThan(minSize).and(lessThan(maxSize));
	}

	/**
	 * Returns a validation that passes only if the string contains the specified substring (case-sensitive).
	 *
	 * @param str The substring to search for.
	 * @return The validation for string containment.
	 */
	public static Validation<String> contains(String str) {
		return SimpleValidation.from(s -> s.contains(str), String.format("must contain \"%s\"", str));
	}

	/**
	 * Returns a validation that passes only if the string contains the specified substring (case-insensitive).
	 *
	 * @param str The substring to search for.
	 * @return The validation for case-insensitive string containment.
	 */
	public static Validation<String> containsIgnoreCase(String str) {
		return SimpleValidation.from(s -> s.toLowerCase().contains(str.toLowerCase()), String.format("must contain \"%s\"", str));
	}
}
