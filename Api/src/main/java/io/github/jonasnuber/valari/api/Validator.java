package io.github.jonasnuber.valari.api;

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
 * @param <TYPE> the type of object to validate
 * @param <RESULT> the type of result returned by the validation process
 *
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
@FunctionalInterface
public non-sealed interface Validator<TYPE, RESULT extends Result<RESULT>> extends GenericValidator {

  /**
   * Validates the given object and returns the result.
   *
   * @param toValidate the object to validate
   * @return the result of the validation
   */
  RESULT validate(TYPE toValidate);

  //TODO:
  /*
    - Look at the Result implementations and make them better
    - The ValidationResultCollection still has not implemented methods and methods with too much content / repetition
    - Look in core what the different classes use from the Result interfaces / classes and use the lowest possible interface or class
    - The detailed Message and normal message options do not have enough differences yet. Think about how they can be bettered, else
      remove one of them entirely
    - give both api and core their own maven modules

   */
}
