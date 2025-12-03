package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import java.util.*;

/**
 * A container for grouping multiple {@link Result} instances that belong to the validation of a
 * specific object, class, or domain model.
 *
 * <p>{@code ValidationResultCollection} provides:
 *
 * <ul>
 *   <li>Aggregation of multiple {@link ThrowableResult} instances.
 *   <li>Computation of an overall {@link ValidationState}.
 *   <li>A unified validation message via {@link ValidationMetadata}.
 *   <li>Convenient transformation through {@link #withLabel(LabelType, String)}.
 *   <li>Exception-based error escalation via {@link AggregatedResult#throwIfInvalid()}.
 * </ul>
 *
 * <p>This class is immutable and is constructed using its {@link Builder}.
 *
 * <h2>State Aggregation Rules</h2>
 *
 * The resulting {@link ValidationState} is computed as follows:
 *
 * <ul>
 *   <li>{@link ValidationState#FAILURE} – if <em>any</em> contained result is invalid.
 *   <li>{@link ValidationState#SKIPPED} – if <em>all</em> contained results are skipped.
 *   <li>{@link ValidationState#SUCCESS} – otherwise.
 * </ul>
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * ValidationResultCollection collection =
 *     ValidationResultCollection.builder(ValidationDescriptor.forClass(User.class))
 *         .add(usernameResult)
 *         .add(emailResult)
 *         .build();
 *
 * if (collection.isInvalid()) {
 *     collection.throwIfInvalid(); // throws AggregatedValidationException
 * }
 *
 * System.out.println(collection.getMetadata().formattedMessage());
 * }</pre>
 *
 * @see AggregatedResult
 * @see ValidationResult
 * @see ValidationDescriptor
 * @see AggregatedValidationException
 * @author Jonas Nuber
 */
public final class ValidationResultCollection
    implements AggregatedResult<ValidationResultCollection> {
  private final ValidationDescriptor validationDescriptor;
  private final List<ThrowableResult<?>> results;

  private ValidationResultCollection(Builder builder) {
    this.validationDescriptor = builder.descriptor;
    this.results = Collections.unmodifiableList(builder.results);
  }

  /**
   * Creates a builder with the given {@link ValidationDescriptor descriptor}
   *
   * @param descriptor the descriptor defining the context data of the result
   */
  public static Builder builder(ValidationDescriptor descriptor) {
    return new Builder(descriptor);
  }

  /**
   * Creates a new {@code ValidationResultCollection} that is identical to this one, but with a
   * replaced label and {@link LabelType}.
   *
   * <p>This operation does not modify the current instance. Instead, it produces a new collection
   * with an updated {@link ValidationDescriptor}. The contained validation results remain
   * unchanged.
   *
   * @param labelType the new {@link LabelType} to associate
   * @param label the new label text
   * @return a new {@code ValidationResultCollection} with updated labelling metadata
   * @throws NullPointerException if {@code labelType} or {@code label} is {@code null}
   */
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
   * Computes the aggregated {@link ValidationState} of all contained results.
   *
   * <p>Rules:
   *
   * <ul>
   *   <li>If any contained result is {@link ValidationState#FAILURE}, the collection state is
   *       {@code FAILURE}.
   *   <li>If all results are {@link ValidationState#SKIPPED}, the collection state is {@code
   *       SKIPPED}.
   *   <li>Otherwise, the collection state is {@code SUCCESS}.
   * </ul>
   *
   * @return the aggregated validation state
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
   * Produces a {@link ValidationMetadata} describing the aggregated validation outcome.
   *
   * <p>The metadata contains:
   *
   * <ul>
   *   <li>The label and {@link LabelType} of the validated object ({@link ValidationDescriptor}).
   *   <li>A localized or default base message describing the result.
   *   <li>The validation clazz.
   *   <li>An appropriate message key for i18n resolution.
   * </ul>
   *
   * <p>The message varies depending on {@link #getState()}:
   *
   * <ul>
   *   <li><strong>SUCCESS:</strong> indicates overall success.
   *   <li><strong>SKIPPED:</strong> indicates that all validations were skipped.
   *   <li><strong>FAILURE:</strong> includes the number of individual failures.
   * </ul>
   *
   * @return a metadata object describing this aggregated result
   */
  @Override
  public ValidationMetadata getMetadata() {
    LabelType labelType = validationDescriptor.getLabelType();
    String label = validationDescriptor.getLabel();
    Class<?> clazz = validationDescriptor.getValidationClass();

    String defaultMessage;
    String key;

    switch (getState()) {
      case SUCCESS -> {
        defaultMessage = "succeeded";
        key = "validation.aggregated.success";
      }
      case SKIPPED -> {
        defaultMessage = "skipped";
        key = "validation.aggregated.skipped";
      }
      case FAILURE -> {
        defaultMessage = "failed";
        key = "validation.aggregated.failure";
      }
      default -> throw new IllegalStateException("Unexpected state: " + getState());
    }

    return ValidationMetadata.builder(defaultMessage)
        .messageKey(key)
        .validationClass(clazz)
        .labelType(labelType)
        .label(label)
        .build();
  }

  /**
   * Returns an immutable list of all contained validation results.
   *
   * <p>The returned list preserves insertion order.
   *
   * @return the list of contained {@link ThrowableResult} instances
   */
  @Override
  public List<ThrowableResult<?>> getResults() {
    return results;
  }

  /**
   * A builder for {@link ValidationResultCollection}.
   *
   * <p>The builder allows incremental addition of {@link ThrowableResult} instances and produces an
   * immutable {@link ValidationResultCollection}.
   *
   * <h2>Usage</h2>
   *
   * <pre>{@code
   * ValidationResultCollection collection =
   *     ValidationResultCollection.builder(descriptor)
   *         .add(result1)
   *         .add(result2)
   *         .build();
   * }</pre>
   */
  public static class Builder {
    private final ValidationDescriptor descriptor;
    private final List<ThrowableResult<?>> results = new ArrayList<>();

    private Builder(ValidationDescriptor descriptor) {
      this.descriptor = Objects.requireNonNull(descriptor, "Descriptor must not be null");
    }

    /**
     * Adds a single validation result to the collection.
     *
     * @param result the result to add
     * @return this builder for method chaining
     * @throws NullPointerException if {@code result} is {@code null}
     */
    public Builder add(ThrowableResult<?> result) {
      results.add(Objects.requireNonNull(result, "Result cannot be added if null"));

      return this;
    }

    /**
     * Adds all results from the given collection into the builder.
     *
     * <p>Null values within the provided collection are not permitted.
     *
     * @param results the collection of results to add
     * @return this builder for method chaining
     * @throws NullPointerException if {@code results} or any of its elements are {@code null}
     */
    public Builder addAll(Collection<ThrowableResult<?>> results) {
      Objects.requireNonNull(results, "Results must not be null");

      for (ThrowableResult<?> result : results) {
        add(result);
      }

      return this;
    }

    /**
     * Builds an immutable {@link ValidationResultCollection} instance.
     *
     * @return the constructed collection
     */
    public ValidationResultCollection build() {
      return new ValidationResultCollection(this);
    }
  }
}
