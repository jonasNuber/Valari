package io.github.jonasnuber.valari.core.validations;

import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.Validation;
import io.github.jonasnuber.valari.core.SimpleValidation;

import java.util.Objects;
import java.util.regex.Pattern;

import static io.github.jonasnuber.valari.core.validations.ObjectValidations.notNull;

/**
 * Collection of predefined {@link io.github.jonasnuber.valari.api.Validation} instances dedicated
 * to validating {@link String} values.
 *
 * <p>Each method provides a specific constraint — such as non-emptiness, length checks, substring
 * conditions, or regular-expression matching — wrapped in a reusable {@link Validation}. All
 * validations assume the input value is non-{@code null}. If a {@code null} value is passed, the
 * validation will fail immediately.
 *
 * <p>All validations are implemented using {@link SimpleValidation} and return a {@code
 * boolean}-style validator combined with a corresponding {@link ValidationMetadata} describing the
 * constraint violation.
 *
 * <p>This class cannot be instantiated.
 *
 * @author Jonas Nuber
 */
public final class StringValidations {

  private static final String STRING_MUST_NOT_BE_NULL = "String must not be null";

  private StringValidations() throws IllegalAccessException {
    throw new IllegalAccessException(
        "StringValidationHelpers is a utility class and cannot be instantiated");
  }

  /**
   * Returns a validation that succeeds only if the string is not {@code null} and not empty.
   *
   * <p>An empty string ({@code ""}) is considered invalid. Whitespace-only strings are allowed.
   *
   * @return a validation rejecting {@code null} or empty strings
   */
  public static Validation<String> notEmpty() {
    return SimpleValidation.from(
        s -> s != null && !s.isEmpty(),
        ValidationMetadata.builder("must not be empty")
            .messageKey("validation.string.notEmpty")
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string is not {@code null} and contains at least
   * one non-whitespace character.
   *
   * <p>A string consisting solely of whitespace (e.g. {@code " "}) is considered blank and
   * therefore invalid.
   *
   * @return a validation rejecting {@code null} or blank strings
   */
  public static Validation<String> notBlank() {
    return SimpleValidation.from(
        s -> s != null && !s.trim().isEmpty(),
        ValidationMetadata.builder("must not be blank")
            .messageKey("validation.string.notBlank")
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string has exactly the specified length.
   *
   * @param size the required length
   * @return a validation rejecting strings that do not have {@code size} characters
   */
  public static Validation<String> exactly(int size) {
    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && s.length() == size,
        ValidationMetadata.builder("must have exactly {0} chars")
            .messageKey("validation.string.exactly")
            .messageArgument(size)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string has more than the given number of
   * characters.
   *
   * @param minimum the exclusive minimum length
   * @return a validation rejecting strings with length {@code <= minimum}
   */
  public static Validation<String> moreThan(int minimum) {
    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && s.length() > minimum,
        ValidationMetadata.builder("must have more than {0} chars")
            .messageKey("validation.string.moreThan")
            .messageArgument(minimum)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string has fewer than the given number of
   * characters.
   *
   * @param maximum the exclusive maximum length
   * @return a validation rejecting strings with length {@code >= maximum}
   */
  public static Validation<String> lessThan(int maximum) {
    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && s.length() < maximum,
        ValidationMetadata.builder("must have less than {0} chars")
            .messageKey("validation.string.lessThan")
            .messageArgument(maximum)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string length is strictly between the given
   * bounds.
   *
   * @param minSize the exclusive minimum length
   * @param maxSize the exclusive maximum length
   * @return a validation rejecting strings outside the specified range
   */
  public static Validation<String> between(int minSize, int maxSize) {
    return moreThan(minSize).and(lessThan(maxSize));
  }

  /**
   * Returns a validation that succeeds only if the string contains the given substring
   * (case-sensitive).
   *
   * @param str the substring to search for (must not be {@code null})
   * @return a validation rejecting strings that do not contain {@code str}
   * @throws NullPointerException if {@code str} is {@code null}
   */
  public static Validation<String> contains(String str) {
    Objects.requireNonNull(str, "String which should be contained, must not be null");

    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && s.contains(str),
        ValidationMetadata.builder("must contain \"{0}\"")
            .messageKey("validation.string.contains")
            .messageArgument(str)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string contains the given substring
   * (case-insensitive).
   *
   * @param str the substring to search for (must not be {@code null})
   * @return a validation rejecting strings that do not contain {@code str} ignoring case
   * @throws NullPointerException if {@code str} is {@code null}
   */
  public static Validation<String> containsIgnoreCase(String str) {
    Objects.requireNonNull(str, "String which should be contained, must not be null");

    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && s.toLowerCase().contains(str.toLowerCase()),
        ValidationMetadata.builder("must contain \"{0}\"")
            .messageKey("validation.string.contains")
            .messageArgument(str)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the entire string matches the given regular
   * expression.
   *
   * <p>This uses {@link String#matches(String)}, which requires <em>full-string</em> matching.
   *
   * @param regex the regular expression to match (must not be {@code null})
   * @return a validation rejecting strings that do not match {@code regex} completely
   * @throws NullPointerException if {@code regex} is {@code null}
   */
  public static Validation<String> regex(String regex) {
    Objects.requireNonNull(regex, "Regular Expression must not be null");

    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && s.matches(regex),
        ValidationMetadata.builder("must fully match regex \" {0} \"")
            .messageKey("validation.string.matchRegex")
            .messageArgument(regex)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string contains a substring matching the given
   * regular expression.
   *
   * <p>This uses {@link java.util.regex.Matcher#find()}, meaning partial matches anywhere within
   * the string will satisfy the validation.
   *
   * @param regex the regular expression to search for (must not be {@code null})
   * @return a validation rejecting strings with no matching substring
   * @throws NullPointerException if {@code regex} is {@code null}
   */
  public static Validation<String> containsRegex(String regex) {
    Objects.requireNonNull(regex, "Regular Expression must not be null");
    Pattern pattern = Pattern.compile(regex);

    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && pattern.matcher(s).find(),
        ValidationMetadata.builder("must contain substring matching regex \" {0} \"")
            .messageKey("validation.string.containRegex")
            .messageArgument(regex)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string starts with the given prefix.
   *
   * @param prefix the prefix to check for (must not be {@code null})
   * @return a validation rejecting strings that do not start with {@code prefix}
   * @throws NullPointerException if {@code prefix} is {@code null}
   */
  public static Validation<String> startsWith(String prefix) {
    Objects.requireNonNull(prefix, "Prefix must not be null");

    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && s.startsWith(prefix),
        ValidationMetadata.builder("must start with \"{0}\"")
            .messageKey("validation.string.startsWith")
            .messageArgument(prefix)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string starts with the given prefix, ignoring
   * case.
   *
   * @param prefix the prefix to check for (must not be {@code null})
   * @return a validation rejecting strings that do not start with the prefix ignoring case
   * @throws NullPointerException if {@code prefix} is {@code null}
   */
  public static Validation<String> startsWithIgnoreCase(String prefix) {
    return SimpleValidation.from(
        s ->
            notNull(s, STRING_MUST_NOT_BE_NULL)
                && notNull(prefix, "Prefix must not be null")
                && s.toLowerCase().startsWith(prefix.toLowerCase()),
        ValidationMetadata.builder("must start with \"{0}\"")
            .messageKey("validation.string.startsWith")
            .messageArgument("(case-insensitive) " + prefix)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string ends with the given suffix.
   *
   * @param suffix the suffix to check for (must not be {@code null})
   * @return a validation rejecting strings that do not end with {@code suffix}
   * @throws NullPointerException if {@code suffix} is {@code null}
   */
  public static Validation<String> endsWith(String suffix) {
    Objects.requireNonNull(suffix, "Suffix must not be null");

    return SimpleValidation.from(
        s -> notNull(s, STRING_MUST_NOT_BE_NULL) && s.endsWith(suffix),
        ValidationMetadata.builder("must end with \"{0}\"")
            .messageKey("validation.string.endsWith")
            .messageArgument(suffix)
            .build());
  }

  /**
   * Returns a validation that succeeds only if the string ends with the given suffix, ignoring
   * case.
   *
   * @param suffix the suffix to check for (must not be {@code null})
   * @return a validation rejecting strings that do not end with the suffix ignoring case
   * @throws NullPointerException if {@code suffix} is {@code null}
   */
  public static Validation<String> endsWithIgnoreCase(String suffix) {
    return SimpleValidation.from(
        s ->
            notNull(s, STRING_MUST_NOT_BE_NULL)
                && notNull(suffix, "Suffix must not be null")
                && s.toLowerCase().endsWith(suffix.toLowerCase()),
        ValidationMetadata.builder("must end with \"{0}\"")
            .messageKey("validation.string.endsWith")
            .messageArgument("(case-insensitive) " + suffix)
            .build());
  }
}
