package io.github.jonasnuber.valari.api.helpers;

import io.github.jonasnuber.valari.internal.SimpleValidation;
import io.github.jonasnuber.valari.spi.Validation;

import java.util.regex.Pattern;

import static io.github.jonasnuber.valari.api.helpers.ObjectValidationHelpers.notNull;

/**
 * Utility class providing predefined validations for strings.
 * These validations define conditions that a string must meet to be considered valid.
 *
 * @author  Jonas Nuber
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
		return SimpleValidation.from(
				s -> s != null && !s.isEmpty(),
				"must not be empty");
	}

	public static Validation<String> notBlank(){
		return SimpleValidation.from(
				s -> s != null && !s.trim().isEmpty(),
				"must not be blank");
	}

	/**
	 * Returns a validation that passes only if the string has exactly the specified number of characters.
	 *
	 * @param size The exact number of characters.
	 * @return The validation for exact string length.
	 */
	public static Validation<String> exactly(int size) {
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						s.length() == size,
				String.format("must have exactly %s chars", size));
	}

	/**
	 * Returns a validation that passes only if the string has more than the specified number of characters.
	 *
	 * @param minimum The minimum number of characters.
	 * @return The validation for minimum string length.
	 */
	public static Validation<String> moreThan(int minimum) {
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						s.length() > minimum,
				String.format("must have more than %s chars", minimum));
	}

	/**
	 * Returns a validation that passes only if the string has fewer than the specified number of characters.
	 *
	 * @param maximum The maximum number of characters.
	 * @return The validation for maximum string length.
	 */
	public static Validation<String> lessThan(int maximum) {
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						s.length() < maximum,
				String.format("must have less than %s chars", maximum));
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
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						notNull(str, "String which should be contained, must not be null") &&
						s.contains(str),
				String.format("must contain \"%s\"", str));
	}

	/**
	 * Returns a validation that passes only if the string contains the specified substring (case-insensitive).
	 *
	 * @param str The substring to search for.
	 * @return The validation for case-insensitive string containment.
	 */
	public static Validation<String> containsIgnoreCase(String str) {
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						notNull(str, "String which should be contained, must not be null") &&
						s.toLowerCase().contains(str.toLowerCase()),
				String.format("must contain \"%s\"", str));
	}

	/**
	 * Returns a validation that passes only if the entire string matches the given regex.
	 *
	 * @param regex The regular expression to match against the whole string.
	 * @return The validation for full regex match.
	 */
	public static Validation<String> regex(String regex) {
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						notNull(regex, "Regular Expression must not be null") &&
						s.matches(regex),
				String.format("must fully match regex '%s'", regex)
		);
	}

	/**
	 * Returns a validation that passes if any substring of the string matches the given regex.
	 *
	 * @param regex The regular expression to search for within the string.
	 * @return The validation for substring regex match.
	 */
	public static Validation<String> containsRegex(String regex) {
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						notNull(regex, "Regular Expression must not be null") &&
						Pattern.compile(regex).matcher(s).find(),
				String.format("must contain substring matching regex '%s'", regex)
		);
	}

	public static Validation<String> startsWith(String prefix) {
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						notNull(prefix, "Prefix must not be null") &&
						s.startsWith(prefix),
				String.format("must start with %s", prefix)
		);
	}

	public static Validation<String> endsWith(String suffix) {
		return SimpleValidation.from(
				s -> notNull(s, "String must not be null") &&
						notNull(suffix, "Suffix must not be null") && s.endsWith(suffix),
				String.format("must end with %s", suffix)
		);
	}
}
