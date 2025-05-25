package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * A container class for collecting multiple {@link ValidationResult} instances
 * during the validation of an object.
 * <p>
 * This class is typically used to accumulate all invalid results for a specific target class.
 * It provides utility methods to check if any validation failures occurred and to throw a
 * single {@link AggregatedValidationException} encapsulating all collected errors.
 * </p>
 *
 * <p>
 * Each {@link ValidationResult} added to the collection is only stored if it is invalid,
 * allowing consumers to ignore successful results and focus on failure aggregation.
 * </p>
 *
 * @author Jonas Nuber
 */
public final class ValidationResultCollection {

  private final List<ValidationResult> results = new ArrayList<>();
  private final Class<?> clazz;

  /**
   * Constructs a new {@code ValidationResultCollection} associated with the specified class.
   *
   * @param clazz the class being validated, used for contextual information in error reporting
   */
  public ValidationResultCollection(Class<?> clazz) {
    this.clazz = clazz;
  }

  /**
   * Adds a {@link ValidationResult} to this collection if it is invalid.
   *
   * @param result the validation result to add
   */
  public void add(ValidationResult result) {
    if (result.isInvalid()) {
      results.add(result);
    }
  }

  /**
   * Returns {@code true} if there are any validation failures in this collection.
   *
   * @return {@code true} if at least one invalid result is present; {@code false} otherwise
   */
  public boolean hasFailures() {
    return !results.isEmpty();
  }

  /**
   * Throws an {@link AggregatedValidationException} if any invalid results exist.
   * <p>
   * This method should be called after all validations have been performed
   * to trigger a single exception containing all validation errors.
   * </p>
   *
   * @throws AggregatedValidationException if any invalid results are present
   */
  public void throwIfInvalid() throws AggregatedValidationException {
    if (hasFailures()) {
      throw new AggregatedValidationException(getErrorMessage());
    }
  }

  /**
   * Returns the list of all collected {@link ValidationResult} instances.
   * <p>
   * This list contains only invalid results.
   * </p>
   *
   * @return the list of invalid validation results
   */
  public List<ValidationResult> getResults() {
    return results;
  }

  /**
   * Builds and returns a comprehensive error message summarizing all validation failures.
   * <p>
   * The message includes the name of the class being validated and details each field's failure reason.
   * </p>
   *
   * @return a formatted error message string representing all validation errors
   */
  public String getErrorMessage() {
    StringBuilder sb = new StringBuilder(
            String.format("Validation for %s failed with %d error(s):%n", clazz, results.size()));

    for (ValidationResult result : results) {
      sb.append(
              String.format(" - Field '%s': %s%n", result.getFieldName(), result.getCauseDescription()));
    }

    return sb.toString();
  }
}
