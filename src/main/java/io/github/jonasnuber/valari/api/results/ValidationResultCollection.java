package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.spi.MessageResolver;
import io.github.jonasnuber.valari.spi.ThrowingResult;

import java.util.*;

/**
 * A container for multiple {@link ValidationResult} instances that belong
 * to a particular validated class or object.
 * <p>
 * {@code ValidationResultCollection} aggregates results from individual
 * validations and provides a unified view of their outcome. It can:
 * <ul>
 *   <li>Determine the overall {@link ValidationState} (success, failure, skipped).</li>
 *   <li>Provide a detailed or aggregated validation message.</li>
 *   <li>Throw an {@link io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException}
 *       if the validation is invalid.</li>
 *   <li>Be converted into a single synthetic {@link ValidationResult}
 *       via {@link #toValidationResult()}.</li>
 * </ul>
 *
 * <h2>Example</h2>
 * <pre>{@code
 * ValidationResultCollection collection = new ValidationResultCollection(User.class);
 * collection.add(usernameValidationResult);
 * collection.add(emailValidationResult);
 *
 * if (collection.isInvalid()) {
 *     collection.throwIfInvalid(); // throws AggregatedValidationException
 * }
 *
 * System.out.println(collection.getMessage());
 * }</pre>
 *
 * @author Jonas Nuber
 */
public class ValidationResultCollection implements ThrowingResult {

  private final Set<ValidationResult> results = new HashSet<>();
  private final Class<?> clazz;

  /**
   * Creates a new collection for the given class.
   *
   * @param clazz the type being validated
   */
  public ValidationResultCollection(Class<?> clazz) {
    this.clazz = clazz;
  }

  /**
   * Adds a validation result to this collection.
   *
   * @param result the result to add, must not be {@code null}
   */
  public void add(ValidationResult result) {
    results.add(Objects.requireNonNull(result, "ValidationResult cannot be added if null"));
  }

  /**
   * Converts this collection into a {@link ValidationResult}
   * that represents the aggregated outcome.
   *
   * @return an aggregated {@code ValidationResult}
   */
  public ValidationResult toValidationResult() {
    return new AggregatedValidationResult(this);
  }

  /**
   * @return the set of contained validation results (mutable view).
   */
  public Set<ValidationResult> getResults() {
    return results;
  }

  /**
   * @return the class type that was validated
   */
  public Class<?> getClazz() {
    return clazz;
  }

  /**
   * Determines the aggregated {@link ValidationState}.
   * <ul>
   *   <li>FAILURE if any contained result failed.</li>
   *   <li>SKIPPED if all contained results were skipped.</li>
   *   <li>SUCCESS otherwise.</li>
   * </ul>
   */
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

  /**
   * Builds a localized message that describes the outcome of this collection.
   * The message may include both a header and a detailed breakdown of
   * individual results.
   *
   * @param resolver the message resolver
   * @param locale   the target locale
   * @return a human-readable validation message
   */
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

  /**
   * Throws an {@link AggregatedValidationException} if this collection
   * is invalid (contains at least one failed result).
   *
   * @throws AggregatedValidationException if validation failed
   */
  @Override
  public void throwIfInvalid() throws AggregatedValidationException {
    throwIfInvalid(AggregatedValidationException::new);
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
                      List.of(result.getLabelType(), result.getLabel(), result.resolveValidationMessage(resolver, locale)),
                      " - {0} \"{1}\": {2}",
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

  /**
   * Synthetic {@link ValidationResult} implementation that wraps an
   * entire {@link ValidationResultCollection}. It delegates its
   * {@link #getState()} and {@link #getMessage(MessageResolver, Locale)}
   * to the underlying collection.
   */
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
