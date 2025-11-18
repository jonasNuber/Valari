package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.Validation;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.ValidationState;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * A simple {@link Validation} implementation backed by a {@link Predicate}.
 *
 * <p>{@code SimpleValidation} converts a boolean condition into a reusable validation rule enriched
 * with {@link ValidationMetadata}, which determines how success or failure should be described
 * (including message keys, labels, and fallbacks).
 *
 * <p>This is the most lightweight way to define custom validations within Valari. It is especially
 * useful for domain-specific rules where no additional contextual logic is required.
 *
 * <h2>Internationalization (i18n)</h2>
 *
 * If the {@linkplain ValidationMetadata#getMessageKey() message key} configured in the metadata can
 * be resolved by the active message resolvers registered in {@code MessageResolutionContext}, the
 * resolved internationalized text will be used. Otherwise, the textual fallback from {@link
 * ValidationMetadata} is used.
 *
 * <h2>Usage example</h2>
 *
 * <pre>{@code
 * Validation<String> notEmpty = SimpleValidation.from(
 *     s -> s != null && !s.isEmpty(),
 *     new ValidationMetadata.Builder("must not be empty")
 *         .messageKey("validation.string.notEmpty")
 *         .build()
 * );
 *
 * ValidationResult ok   = notEmpty.test("foo"); // SUCCESS
 * ValidationResult fail = notEmpty.test("");    // FAILURE
 * }</pre>
 *
 * @param <TYPE> the value type validated by this rule
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public class SimpleValidation<TYPE> implements Validation<TYPE> {
  private final Predicate<TYPE> predicate;
  private final ValidationMetadata metadata;

  private SimpleValidation(Predicate<TYPE> predicate, ValidationMetadata metadata) {
    this.predicate = Objects.requireNonNull(predicate, "Predicate must not be null");
    this.metadata = Objects.requireNonNull(metadata, "ValidationMetadata must not be null");
  }

  /**
   * Creates a new {@code SimpleValidation} using the given predicate and metadata.
   *
   * @param predicate the condition evaluated during validation (must not be {@code null})
   * @param metadata the metadata defining validation messages and description (must not be {@code
   *     null})
   * @param <TYPE> the type being validated
   * @return a new {@code SimpleValidation} instance
   */
  public static <TYPE> SimpleValidation<TYPE> from(
      Predicate<TYPE> predicate, ValidationMetadata metadata) {
    return new SimpleValidation<>(predicate, metadata);
  }

  /**
   * Validates the given value by evaluating the underlying predicate.
   *
   * <p>Returns a new {@link ValidationResult} that includes:
   *
   * <ul>
   *   <li>the {@linkplain ValidationMetadata metadata} associated with this rule
   *   <li>the input value via {@link ValidationResult.Builder#value(Object)}
   *   <li>the appropriate {@link ValidationState#SUCCESS} or {@link ValidationState#FAILURE}
   * </ul>
   *
   * @param param the value to validate (may be {@code null} unless the rule forbids it)
   * @return a {@link ValidationResult} describing the outcome
   */
  @Override
  public ValidationResult test(TYPE param) {
    ValidationResult.Builder resultBuilder = ValidationResult.builder(metadata).value(param);

    return predicate.test(param) ? resultBuilder.ok() : resultBuilder.fail();
  }
}
