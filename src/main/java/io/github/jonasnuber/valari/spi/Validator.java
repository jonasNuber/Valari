package io.github.jonasnuber.valari.spi;

/**
 * A generic interface for performing validation on an object of type {@code T},
 * producing a result of type {@code R}.
 * <p>
 * This interface abstracts the validation logic and allows flexible result handling,
 * such as collecting all validation failures or stopping at the first failure.
 * </p>
 *
 * <p>Typical implementations may return a result object containing success or failure
 * information, and optionally throw an exception for invalid objects.
 * </p>
 *
 * @param <T> the type of object to validate
 * @param <R> the type of result returned by the validation process
 *
 * @author Jonas Nuber
 */
public interface Validator<T, R> {

  /**
   * Validates the given object and returns the result.
   *
   * @param toValidate the object to validate
   * @return the result of the validation
   */
  R validate(T toValidate);

  /**
   * Validates the given object and throws an exception if the validation fails.
   * <p>
   * The type and content of the exception is defined by the implementing class.
   * </p>
   *
   * @param toValidate the object to validate
   * @throws RuntimeException if the validation fails
   */
  void validateAndThrow(T toValidate);
}
