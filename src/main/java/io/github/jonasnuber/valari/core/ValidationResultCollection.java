package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;

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
 *   <li>Throw an {@link AggregatedValidationException}
 *       if the validation is invalid.</li>
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
public final class ValidationResultCollection implements AggregatedResult<ValidationResultCollection> {

  private final List<ThrowableResult<?>> results = new ArrayList<>();
  private final ValidationDescriptor validationDescriptor;

  /**
   * Creates a new collection for the given class.
   *
   * @param validationDescriptor the metadata for the given Validation
   */
  public ValidationResultCollection(ValidationDescriptor validationDescriptor) {
    this.validationDescriptor = validationDescriptor;
  }

  /**
   * Adds a validation result to this collection.
   *
   * @param result the result to add, must not be {@code null}
   */
  public void add(ThrowableResult<?> result) {
    results.add(Objects.requireNonNull(result, "ValidationResult cannot be added if null"));
  }

  public void addAll(Collection<ThrowableResult<?>> results) {
    this.results.addAll(results);
  }

  /**
   * @return the list of contained validation results (mutable view).
   */
  @Override
  public List<ThrowableResult<?>> getResults() {
    return results;
  }

  @Override
  public LabelType getLabelType() {
    return validationDescriptor.getLabelType();
  }

  @Override
  public String getLabel() {
    return validationDescriptor.getLabel();
  }

  /**
   * @return the class type that was validated
   */
  public Class<?> getValidationClass() {
    return validationDescriptor.getValidationClass();
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

  @Override
  public String resolveValidationMessage(MessageResolver resolver, Locale locale) {
    return "";
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
  public String getDetailedMessage(MessageResolver resolver, Locale locale) {
    StringBuilder sb = new StringBuilder();
    buildDetailedMessage(sb, 0, resolver, locale);

    return sb.toString();
  }

  @Override
  public ValidationResultCollection withLabel(LabelType labelType, String label) {
    return null;
  }

  @Override
  public String getMessage(MessageResolver resolver, Locale locale) {
    StringBuilder sb = new StringBuilder();
    buildMessage(sb, 0, resolver, locale);

    return sb.toString();
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

  private void buildDetailedMessage(StringBuilder sb, int depth, MessageResolver resolver, Locale locale) {
    String indent = "  ";

    sb
            .append(indent.repeat(depth))
            .append(
                    switch (getState()) {
                      case SUCCESS -> resolver.resolve(
                              "validation.result.aggregated.success",
                              List.of(
                                      validationDescriptor.getLabelType(),
                                      validationDescriptor.getLabel(),
                                      validationDescriptor.getValidationClass()
                              ),
                              "Validation for {0} \"{1}\" ({2}) succeeded:",
                              locale
                      );
                      case SKIPPED -> resolver.resolve(
                              "validation.result.aggregated.skipped",
                              List.of(
                                      validationDescriptor.getLabelType(),
                                      validationDescriptor.getLabel(),
                                      validationDescriptor.getValidationClass()
                              ),
                              "Validation for {0} \"{1}\" ({2}) was skipped entirely.",
                              locale
                      );
                      case FAILURE -> resolver.resolve(
                              "validation.result.aggregated.failure",
                              List.of(
                                      validationDescriptor.getLabelType(),
                                      validationDescriptor.getLabel(),
                                      validationDescriptor.getValidationClass(),
                                      countFailures()
                              ),
                              "Validation for {0} \"{1}\" ({2}) failed with {3} error(s):",
                              locale
                      );
                    }
            )
            .append(System.lineSeparator());

    List<ThrowableResult<?>> currentResults = results;

    if(getState() == ValidationState.SKIPPED) return;
    if(getState() == ValidationState.FAILURE) currentResults = getFailedResults();

    for (ThrowableResult<?> result : currentResults) {
      if(result instanceof ValidationResultCollection resultCollection) {
        resultCollection.buildDetailedMessage(sb, depth + 1, resolver, locale);

      } else {
        sb.append(indent.repeat(depth + 1))
                .append(result.getDetailedMessage(resolver, locale))
                .append(System.lineSeparator());
      }
    }
  }

  private void buildMessage(StringBuilder sb, int depth, MessageResolver resolver, Locale locale) {
    String indent = "  ";

    sb
            .append(indent.repeat(depth))
            .append(
                    switch (getState()) {
                      case SUCCESS -> resolver.resolve(
                              "validation.result.aggregated.success",
                              List.of(
                                      validationDescriptor.getLabelType(),
                                      validationDescriptor.getLabel(),
                                      validationDescriptor.getValidationClass().getSimpleName()
                              ),
                              "Validation for {0} succeeded:",
                              locale
                      );
                      case SKIPPED -> resolver.resolve(
                              "validation.result.aggregated.skipped",
                              List.of(
                                      validationDescriptor.getLabelType(),
                                      validationDescriptor.getLabel(),
                                      validationDescriptor.getValidationClass().getSimpleName()
                              ),
                              "Validation for {0} \"{1}\" ({2}) was skipped entirely.",
                              locale
                      );
                      case FAILURE -> resolver.resolve(
                              "validation.result.aggregated.failure",
                              List.of(
                                      validationDescriptor.getLabelType(),
                                      validationDescriptor.getLabel(),
                                      validationDescriptor.getValidationClass().getSimpleName(),
                                      countFailures()
                              ),
                              "Validation for {0} \"{1}\" ({2}) failed with {3} error(s):",
                              locale
                      );
                    }
            )
            .append(System.lineSeparator());

    List<ThrowableResult<?>> currentResults = results;

    if(getState() == ValidationState.SKIPPED) return;
    if(getState() == ValidationState.FAILURE) currentResults = getFailedResults();

    for (ThrowableResult<?> result : currentResults) {
      if(result instanceof ValidationResultCollection resultCollection) {
        resultCollection.buildMessage(sb, depth + 1, resolver, locale);

      } else {
        sb.append(indent.repeat(depth + 1))
                .append("- ")
                .append(result.getMessage(resolver, locale))
                .append(System.lineSeparator());
      }
    }
  }

  private List<ThrowableResult<?>> getFailedResults() {
    return results.stream()
            .filter(r -> r.getState() == ValidationState.FAILURE)
            .toList();
  }

  private long countFailures() {
    return results.stream()
            .filter(r -> r.getState() == ValidationState.FAILURE)
            .count();
  }
}
