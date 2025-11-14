package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;

import java.util.*;

/**
 * A container for multiple {@link ValidationResult} instances that belong to a particular validated
 * class or object.
 *
 * <p>{@code ValidationResultCollection} aggregates results from individual validations and provides
 * a unified view of their outcome. It can:
 *
 * <ul>
 *   <li>Determine the overall {@link ValidationState} (success, failure, skipped).
 *   <li>Provide a detailed or aggregated validation message.
 *   <li>Throw an {@link AggregatedValidationException} if the validation is invalid.
 * </ul>
 *
 * <h2>Example</h2>
 *
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
public final class ValidationResultCollection
    implements AggregatedResult<ValidationResultCollection> {
  private final ValidationDescriptor validationDescriptor;
  private final List<ThrowableResult<?>> results;

  /** Creates a new collection for the given class. */
  private ValidationResultCollection(Builder builder) {
    this.validationDescriptor = builder.descriptor;
    this.results = Collections.unmodifiableList(builder.results);
  }

  @Override
  public ValidationResultCollection withLabel(LabelType labelType, String label) {
    ValidationDescriptor newDescriptor =
        ValidationDescriptor.builder()
            .labelType(labelType)
            .label(label)
            .validationClass(validationDescriptor.getValidationClass())
            .build();

    return new Builder(newDescriptor).addAll(results).build();
  }

  /**
   * Determines the aggregated {@link ValidationState}.
   *
   * <ul>
   *   <li>FAILURE if any contained result failed.
   *   <li>SKIPPED if all contained results were skipped.
   *   <li>SUCCESS otherwise.
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
  public ValidationMetadata getMetadata() {
    var labelType = validationDescriptor.getLabelType();
    var label = validationDescriptor.getLabel();
    var clazz = validationDescriptor.getValidationClass();

    var baseArgs = new ArrayList<>(List.of(labelType, label, clazz));

    String defaultMessage;
    String key;

    switch (getState()) {
      case SUCCESS -> {
        defaultMessage = "Validation for {0} \"{1}\" ({2}) succeeded:";
        key = "validation.result.aggregated.success";
      }
      case SKIPPED -> {
        defaultMessage = "Validation for {0} \"{1}\" ({2}) was skipped entirely.";
        key = "validation.result.aggregated.skipped";
      }
      case FAILURE -> {
        defaultMessage = "Validation for {0} \"{1}\" ({2}) failed with {3} error(s):";
        key = "validation.result.aggregated.failure";
        baseArgs.add(
            getResults().stream().filter(r -> r.getState() == ValidationState.FAILURE).count());
      }
      default -> throw new IllegalStateException("Unexpected state: " + getState());
    }

    return ValidationMetadata.builder(defaultMessage)
        .messageKey(key)
        .validationClass(clazz)
        .labelType(labelType)
        .label(label)
        .messageArguments(baseArgs.toArray())
        .build();
  }

  /**
   * @return the list of contained validation results.
   */
  @Override
  public List<ThrowableResult<?>> getResults() {
    return results;
  }

  public static class Builder {
    private final ValidationDescriptor descriptor;
    private final List<ThrowableResult<?>> results = new ArrayList<>();

    public Builder(ValidationDescriptor descriptor) {
      this.descriptor = descriptor;
    }

    public Builder add(ThrowableResult<?> result) {
      results.add(Objects.requireNonNull(result, "ValidationResult cannot be added if null"));

      return this;
    }

    public Builder addAll(Collection<ThrowableResult<?>> results) {
      for (ThrowableResult<?> result : results) {
        add(result);
      }

      return this;
    }

    public ValidationResultCollection build() {
      return new ValidationResultCollection(this);
    }
  }
}
