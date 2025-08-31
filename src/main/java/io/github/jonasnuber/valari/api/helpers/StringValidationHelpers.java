package io.github.jonasnuber.valari.api.helpers;

import io.github.jonasnuber.valari.api.SimpleValidation;
import io.github.jonasnuber.valari.api.results.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;

import java.util.Objects;
import java.util.regex.Pattern;

import static io.github.jonasnuber.valari.api.helpers.ObjectValidationHelpers.notNull;

/**
 * Utility class providing predefined validations for strings.
 *
 * <p>
 *      Each method returns a {@link Validation} that encapsulates a specific rule
 *   	a string must satisfy to be considered valid.
 * </p>
 *
 * @author  Jonas Nuber
 */
public final class StringValidationHelpers {

	private static final String STRING_MUST_NOT_BE_NULL = "String must not be null";

	private StringValidationHelpers() throws IllegalAccessException {
		throw new IllegalAccessException("StringValidationHelpers is a utility class and cannot be instantiated");
	}

	/**
	 * Returns a validation that passes if the string is not {@code null} and not empty.
	 *
	 * @return a validation that rejects {@code null} or empty strings
	 */
	public static Validation<String> notEmpty(){
		return SimpleValidation.from(
				s -> s != null && !s.isEmpty(),
				new ValidationResult.Builder("must not be empty")
						.messageKey("validation.string.notEmpty")
		);
	}

	/**
	 * Returns a validation that passes if the string is not {@code null} and contains
	 * at least one non-whitespace character.
	 *
	 * @return a validation that rejects {@code null} or blank strings
	 */
	public static Validation<String> notBlank(){
		return SimpleValidation.from(
				s -> s != null && !s.trim().isEmpty(),
				new ValidationResult.Builder("must not be blank")
						.messageKey("validation.string.notBlank")
		);
	}

	/**
	 * Returns a validation that passes if the string has exactly the specified length.
	 *
	 * @param size the required number of characters
	 * @return a validation that rejects strings not exactly {@code size} characters long
	 */
	public static Validation<String> exactly(int size) {
		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						s.length() == size,
				new ValidationResult.Builder("must have exactly {0} chars")
						.messageKey("validation.string.exactly")
						.messageArgument(size)
		);
	}

	/**
	 * Returns a validation that passes if the string has more than the given number of characters.
	 *
	 * @param minimum the minimum exclusive length
	 * @return a validation that rejects strings with length {@code <= minimum}
	 */
	public static Validation<String> moreThan(int minimum) {
		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						s.length() > minimum,
				new ValidationResult.Builder("must have more than {0} chars")
						.messageKey("validation.string.moreThan")
						.messageArgument(minimum)
		);
	}

	/**
	 * Returns a validation that passes if the string has fewer than the given number of characters.
	 *
	 * @param maximum the maximum exclusive length
	 * @return a validation that rejects strings with length {@code >= maximum}
	 */
	public static Validation<String> lessThan(int maximum) {
		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						s.length() < maximum,
				new ValidationResult.Builder("must have less than {0} chars")
						.messageKey("validation.string.lessThan")
						.messageArgument(maximum)
		);
	}

	/**
	 * Returns a validation that passes if the string length is strictly between
	 * the given minimum and maximum values.
	 *
	 * @param minSize the minimum exclusive length
	 * @param maxSize the maximum exclusive length
	 * @return a validation that rejects strings outside the given range
	 */
	public static Validation<String> between(int minSize, int maxSize) {
		return moreThan(minSize).and(lessThan(maxSize));
	}

	/**
	 * Returns a validation that passes if the string contains the given substring
	 * (case-sensitive).
	 *
	 * @param str the substring to search for, must not be {@code null}
	 * @return a validation that rejects strings not containing {@code str}
	 * @throws NullPointerException if {@code str} is {@code null}
	 */
	public static Validation<String> contains(String str) {
		Objects.requireNonNull(str, "String which should be contained, must not be null");

		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						s.contains(str),
				new ValidationResult.Builder("must contain \"{0}\"")
						.messageKey("validation.string.contains")
						.messageArgument(str)
		);
	}

	/**
	 * Returns a validation that passes if the string contains the given substring,
	 * ignoring case.
	 *
	 * @param str the substring to search for, must not be {@code null}
	 * @return a validation that rejects strings not containing {@code str} (case-insensitive)
	 * @throws NullPointerException if {@code str} is {@code null}
	 */
	public static Validation<String> containsIgnoreCase(String str) {
		Objects.requireNonNull(str, "String which should be contained, must not be null");

		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						s.toLowerCase().contains(str.toLowerCase()),
				new ValidationResult.Builder("must contain \"{0}\"")
						.messageKey("validation.string.contains")
						.messageArgument(str)
		);
	}

	/**
	 * Returns a validation that passes if the string fully matches the given regular expression.
	 *
	 * @param regex the regex pattern to match against, must not be {@code null}
	 * @return a validation that rejects strings that do not match the regex completely
	 * @throws NullPointerException if {@code regex} is {@code null}
	 */
	public static Validation<String> regex(String regex) {
		Objects.requireNonNull(regex, "Regular Expression must not be null");

		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						s.matches(regex),
				new ValidationResult.Builder("must fully match regex \" {0} \"")
						.messageKey("validation.string.matchRegex")
						.messageArgument(regex)
		);
	}

	/**
	 * Returns a validation that passes if the string contains a substring matching
	 * the given regular expression.
	 *
	 * @param regex the regex pattern to search for, must not be {@code null}
	 * @return a validation that rejects strings that do not contain a matching substring
	 * @throws NullPointerException if {@code regex} is {@code null}
	 */
	public static Validation<String> containsRegex(String regex) {
		Objects.requireNonNull(regex, "Regular Expression must not be null");
		Pattern pattern = Pattern.compile(regex);

		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						pattern.matcher(s).find(),
				new ValidationResult.Builder("must contain substring matching regex \" {0} \"")
						.messageKey("validation.string.containRegex")
						.messageArgument(regex)
		);
	}

	/**
	 * Returns a validation that passes if the string starts with the given prefix
	 * (case-sensitive).
	 *
	 * @param prefix the required starting substring, must not be {@code null}
	 * @return a validation that rejects strings not starting with {@code prefix}
	 * @throws NullPointerException if {@code prefix} is {@code null}
	 */

	public static Validation<String> startsWith(String prefix) {
		Objects.requireNonNull(prefix, "Prefix must not be null");

		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						s.startsWith(prefix),
				new ValidationResult.Builder("must start with \"{0}\"")
						.messageKey("validation.string.startsWith")
						.messageArgument(prefix)
		);
	}

	/**
	 * Returns a validation that passes if the string starts with the given prefix,
	 * ignoring case.
	 *
	 * @param prefix the required starting substring, must not be {@code null}
	 * @return a validation that rejects strings not starting with {@code prefix} (case-insensitive)
	 * @throws NullPointerException if {@code prefix} is {@code null}
	 */
	public static Validation<String> startsWithIgnoreCase(String prefix) {
		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						notNull(prefix, "Prefix must not be null") &&
						s.toLowerCase().startsWith(prefix.toLowerCase()),
				new ValidationResult.Builder("must start with \"{0}\"")
						.messageKey("validation.string.startsWith")
						.messageArgument("(case-insensitive) " + prefix)
		);
	}

	/**
	 * Returns a validation that passes if the string ends with the given suffix
	 * (case-sensitive).
	 *
	 * @param suffix the required ending substring, must not be {@code null}
	 * @return a validation that rejects strings not ending with {@code suffix}
	 * @throws NullPointerException if {@code suffix} is {@code null}
	 */

	public static Validation<String> endsWith(String suffix) {
		Objects.requireNonNull(suffix, "Suffix must not be null");

		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						s.endsWith(suffix),
				new ValidationResult.Builder("must end with \"{0}\"")
						.messageKey("validation.string.endsWith")
						.messageArgument(suffix)
		);
	}

	/**
	 * Returns a validation that passes if the string ends with the given suffix,
	 * ignoring case.
	 *
	 * @param suffix the required ending substring, must not be {@code null}
	 * @return a validation that rejects strings not ending with {@code suffix} (case-insensitive)
	 * @throws NullPointerException if {@code suffix} is {@code null}
	 */
	public static Validation<String> endsWithIgnoreCase(String suffix) {
		return SimpleValidation.from(
				s -> notNull(s, STRING_MUST_NOT_BE_NULL) &&
						notNull(suffix, "Suffix must not be null") &&
						s.toLowerCase().endsWith(suffix.toLowerCase()),
				new ValidationResult.Builder("must end with \"{0}\"")
						.messageKey("validation.string.endsWith")
						.messageArgument("(case-insensitive) " + suffix)
		);
	}
}
