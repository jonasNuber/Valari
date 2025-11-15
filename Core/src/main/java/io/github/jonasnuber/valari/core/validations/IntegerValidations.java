package io.github.jonasnuber.valari.core.validations;

import io.github.jonasnuber.valari.api.Validation;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.core.SimpleValidation;

/**
 * Utility class providing predefined {@link Validation} instances for {@link Integer} values.
 *
 * <p>These validations cover common numerical constraints such as equality, range checks, and
 * parity checks. All validations assume the input value is non-{@code null}. If a {@code null}
 * value is passed, the validation will fail immediately.
 *
 * <p>Each validation is implemented using {@link SimpleValidation} and includes an associated
 * {@link ValidationMetadata} describing the constraint and message resolution details.
 *
 * <p>This class is a pure utility class and cannot be instantiated.
 *
 * @author Jonas Nuber
 */
public final class IntegerValidations {

  private IntegerValidations() throws IllegalAccessException {
    throw new IllegalAccessException(
        "IntegerValidationHelpers is a utility class and cannot be instantiated");
  }

  /**
   * Returns a validation that passes only if the input integer is equal to the specified value.
   *
   * <p>The check is performed using {@code i == exact}. A {@code null} input results in failure.
   *
   * @param exact the required integer value
   * @return a validation checking numerical equality
   */
  public static Validation<Integer> sameAmount(int exact) {
    return SimpleValidation.from(
        i -> i == exact,
        ValidationMetadata.builder("must equal {0}")
            .messageKey("validation.integer.sameAmount")
            .messageArgument(exact)
            .build());
  }

  /**
   * Returns a validation that passes only if the input integer is strictly less than the specified
   * maximum.
   *
   * <p>The validation checks {@code i < max}. A {@code null} input results in failure.
   *
   * @param max the exclusive upper bound
   * @return a validation ensuring the input is less than the given maximum
   */
  public static Validation<Integer> lowerThan(int max) {
    return SimpleValidation.from(
        i -> i < max,
        ValidationMetadata.builder("must be lower than {0}")
            .messageKey("validation.integer.lowerThan")
            .messageArgument(max)
            .build());
  }

  /**
   * Returns a validation that passes only if the input integer is strictly greater than the
   * specified minimum.
   *
   * <p>The validation checks {@code i > min}. A {@code null} input results in failure.
   *
   * @param min the exclusive lower bound
   * @return a validation ensuring the input is greater than the given minimum
   */
  public static Validation<Integer> greaterThan(int min) {
    return SimpleValidation.from(
        i -> i > min,
        ValidationMetadata.builder("must be greater than {0}")
            .messageKey("validation.integer.greaterThan")
            .messageArgument(min)
            .build());
  }

  /**
   * Returns a validation that passes only if the input integer is strictly between the specified
   * minimum and maximum.
   *
   * <p>This is equivalent to {@code i > min} <em>and</em> {@code i < max}. The input must not be
   * {@code null}.
   *
   * @param min the exclusive lower bound
   * @param max the exclusive upper bound
   * @return a validation ensuring the input lies strictly within the exclusive range
   */
  public static Validation<Integer> inBetween(int min, int max) {
    return greaterThan(min).and(lowerThan(max));
  }

  /**
   * Returns a validation that passes only if the input integer is between the specified minimum and
   * maximum, inclusive.
   *
   * <p>This is logically equivalent to {@code i >= min} and {@code i <= max}. Internally, the
   * implementation reuses the exclusive-range methods by adjusting boundaries.
   *
   * <p>The input must not be {@code null}.
   *
   * @param min the inclusive lower bound
   * @param max the inclusive upper bound
   * @return a validation ensuring the input lies within the inclusive range
   */
  public static Validation<Integer> inBetweenInclusive(int min, int max) {
    return greaterThan(--min).and(lowerThan(++max));
  }

  /**
   * Returns a validation that passes only if the input integer is even.
   *
   * <p>A number is considered even if {@code i % 2 == 0}. A {@code null} input fails the
   * validation.
   *
   * @return a validation checking for even integers
   */
  public static Validation<Integer> isEven() {
    return SimpleValidation.from(
        i -> i % 2 == 0,
        ValidationMetadata.builder("must be even").messageKey("validation.integer.isEven").build());
  }

  /**
   * Returns a validation that passes only if the input integer is odd.
   *
   * <p>A number is considered odd if {@code i % 2 != 0}. A {@code null} input fails the validation.
   *
   * @return a validation checking for odd integers
   */
  public static Validation<Integer> isOdd() {
    return SimpleValidation.from(
        i -> i % 2 != 0,
        ValidationMetadata.builder("must be odd").messageKey("validation.integer.isOdd").build());
  }
}
