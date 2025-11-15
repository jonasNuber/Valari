package io.github.jonasnuber.valari.core.validations;

import io.github.jonasnuber.valari.api.Validation;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.core.SimpleValidation;
import java.util.Objects;

/**
 * Collection of predefined {@link io.github.jonasnuber.valari.api.Validation} instances for general
 * object handling. These validations cover fundamental object-level constraints such as null-checks
 * and equality checks.
 *
 * <p>All validations assume the input value is non-{@code null}. If a {@code null} value is passed,
 * the validation will fail immediately.
 *
 * <p>Each validation is implemented using {@link SimpleValidation} and includes an associated
 * {@link ValidationMetadata} describing the constraint and message resolution details.
 *
 * <p>This class cannot be instantiated.
 *
 * @author Jonas Nuber
 */
public final class ObjectValidations {

  private ObjectValidations() throws IllegalAccessException {
    throw new IllegalAccessException(
        "ObjectValidationHelpers is a utility class and cannot be instantiated");
  }

  /**
   * Returns a validation that succeeds only if the evaluated object is <strong>not</strong> {@code
   * null}.
   *
   * <p>This is one of the most commonly used validations and is particularly useful as a
   * prerequisite for subsequent constraint checks.
   *
   * @param <K> the type of the validated value
   * @return a validation that fails when the value is {@code null}
   */
  public static <K> Validation<K> notNull() {
    return SimpleValidation.from(
        Objects::nonNull,
        ValidationMetadata.builder("must not be null")
            .messageKey("validation.object.notNull")
            .build());
  }

  /**
   * Returns a validation that succeeds only if the evaluated object is equal to the given {@code
   * other} object as determined by {@link Object#equals(Object)}.
   *
   * <p>The comparison target must not be {@code null}. This ensures a consistent equality check and
   * avoids ambiguity between "value is null" and "value differs from expected". If a {@code null}
   * value must be permitted or explicitly checked, combine this validation with {@link #notNull()}
   * or use a custom validation.
   *
   * @param <K> the type of the validated values
   * @param other the expected value (must not be {@code null})
   * @return a validation ensuring that the evaluated value equals {@code other}
   * @throws NullPointerException if {@code other} is {@code null}
   */
  public static <K> Validation<K> isEqualTo(K other) {
    Objects.requireNonNull(other, "Object to equal must not be null");

    return SimpleValidation.from(
        o -> notNull(o, "Object must not be null") && other.equals(o),
        ValidationMetadata.builder("must be equal to \"{0}\"")
            .messageKey("validation.object.equalTo")
            .messageArgument(other)
            .build());
  }

  /**
   * Internal helper that checks whether the given object is not {@code null}.
   *
   * <p>This method is used internally to ensure safe execution of validations that require a
   * non-null input before applying further logic.
   *
   * @param o the object to test
   * @param errorMessage message for the resulting {@link NullPointerException}
   * @return always {@code true} if the object is non-null
   * @throws NullPointerException if the object is {@code null}
   */
  static boolean notNull(Object o, String errorMessage) throws NullPointerException {
    Objects.requireNonNull(o, errorMessage);

    return true;
  }
}
