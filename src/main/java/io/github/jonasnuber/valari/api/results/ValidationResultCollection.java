package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.spi.MessageResolver;
import io.github.jonasnuber.valari.spi.ThrowingResult;

import java.util.*;

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
public final class ValidationResultCollection implements ThrowingResult {

  private final Set<ValidationResult> results = new HashSet<>();
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
    results.add(Objects.requireNonNull(result, "ValidationResult cannot be added if null"));
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
  @Override
  public void throwIfInvalid() throws AggregatedValidationException {
    throwIfInvalid(AggregatedValidationException::new);
  }

  public ValidationResult toValidationResult() {
    return new AggregatedValidationResult(this);
  }

  /**
   * Returns the list of all collected {@link ValidationResult} instances.
   * <p>
   * This list contains only invalid results.
   * </p>
   *
   * @return the set of invalid validation results
   */
  public Set<ValidationResult> getResults() {
    return results;
  }

  @Override
  public ValidationState getState() {
    if (results.stream().anyMatch(r -> r.getState() == ValidationState.FAILURE)) {
      return ValidationState.FAILURE;
    }

    if (results.stream().allMatch(r -> r.getState() == ValidationState.SKIPPED)) {
      return ValidationState.SKIPPED;
    }

    return ValidationState.SUCCESS;
  }

  public Class<?> getClazz() {
    return clazz;
  }

  @Override
  public String getMessage(MessageResolver resolver, Locale locale) {
    return switch (getState()) {
      case SUCCESS -> buildDetailedMessage(
              resolver,
              locale,
              "validation.result.aggregated.success",
              List.of(clazz),
              "Validation for {0} succeeded:",
              results
      );
      case SKIPPED -> resolver.resolve(
              "validation.result.aggregated.skipped",
              List.of(clazz),
              "Validation for {0} was skipped entirely.",
              locale
      );
      case FAILURE -> buildDetailedMessage(
              resolver,
              locale,
              "validation.result.aggregated.failure",
              List.of(clazz, countFailures()),
              "Validation for {0} failed with {1} error(s):",
              getFailedResults()
      );
    };
  }

  private String buildDetailedMessage(
          MessageResolver resolver,
          Locale locale,
          String headerKey,
          List<Object> headerArgs,
          String defaultHeader,
          Collection<ValidationResult> resultsToInclude
  ) {
    StringBuilder sb = new StringBuilder(
            resolver.resolve(headerKey, headerArgs, defaultHeader, locale)
    ).append(System.lineSeparator());

    for (ValidationResult result : resultsToInclude) {
      sb.append(
              resolver.resolve(
                      "validation.result.aggregated.field",
                      List.of(result.getLabel(), result.resolveValidationMessage(resolver, locale)),
                      " - Field '{0}': {1}",
                      locale
              )
      ).append(System.lineSeparator());
    }

    return sb.toString();
  }

  private List<ValidationResult> getFailedResults() {
    return results.stream()
            .filter(r -> r.getState() == ValidationState.FAILURE)
            .toList();
  }

  private long countFailures() {
    return results.stream()
            .filter(r -> r.getState() == ValidationState.FAILURE)
            .count();
  }

  private static final class AggregatedValidationResult extends ValidationResult {
    private final ValidationResultCollection collection;

    AggregatedValidationResult(ValidationResultCollection collection) {
      super(new Builder("Aggregated validation result for {0}")
              .messageKey("validation.result.collection")
              .messageArgument(collection.getClazz())
              .label(collection.getClazz().getCanonicalName()));
      this.collection = collection;
    }

    @Override
    public ValidationState getState() {
      return collection.getState();
    }

    @Override
    public String getMessage(MessageResolver resolver, Locale locale) {
      return collection.getMessage(resolver, locale);
    }

    @Override
    public boolean equals(Object o) {
      if (o == null || getClass() != o.getClass()) return false;
      if (!super.equals(o)) return false;
      AggregatedValidationResult that = (AggregatedValidationResult) o;
      return Objects.equals(collection, that.collection);
    }

    @Override
    public int hashCode() {
      return Objects.hash(super.hashCode(), collection);
    }
  }
}
