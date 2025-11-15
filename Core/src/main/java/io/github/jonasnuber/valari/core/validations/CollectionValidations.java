package io.github.jonasnuber.valari.core.validations;

import static io.github.jonasnuber.valari.core.validations.ObjectValidations.notNull;

import io.github.jonasnuber.valari.api.Validation;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.core.SimpleValidation;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

/**
 * Utility class providing predefined {@link Validation} implementations for {@link Collection}
 * values.
 *
 * <p>This class offers reusable building blocks for common collection-based validation scenarios,
 * such as size constraints, element existence checks, and predicate-based evaluation.
 *
 * <h2>Null Handling</h2>
 *
 * <ul>
 *   <li>All validations produced by this class require the collection itself to be non-{@code
 *       null}.
 *   <li>Unless explicitly stated otherwise, the validation fails immediately if the collection is
 *       {@code null}.
 *   <li>Where applicable, method arguments such as predicates or required element values must also
 *       be non-{@code null}.
 * </ul>
 *
 * <p>All validations are implemented using {@link SimpleValidation} and return a {@code
 * boolean}-style validator combined with a corresponding {@link ValidationMetadata} describing the
 * constraint violation.
 *
 * <p>This class cannot be instantiated.
 *
 * @author Jonas Nuber
 */
public final class CollectionValidations {

  private static final String COLLECTION_MUST_NOT_BE_NULL = "Collection must not be null";

  private CollectionValidations() throws IllegalAccessException {
    throw new IllegalAccessException("Utility classes should not be instantiated");
  }

  /**
   * Returns a validation that passes only if the collection is neither {@code null} nor empty.
   *
   * <p>The validation fails if the collection is {@code null} or {@code empty()}.
   *
   * @return a validation ensuring the collection contains at least one element
   */
  public static Validation<Collection<?>> notEmpty() {
    return SimpleValidation.from(
        c -> !(c == null || c.isEmpty()),
        ValidationMetadata.builder("Collection must not be empty")
            .messageKey("validation.collection.empty")
            .build());
  }

  /**
   * Returns a validation that passes if the collection size is strictly greater than {@code min}
   * and strictly less than {@code max}.
   *
   * <p>If the collection is {@code null}, the validation fails.
   *
   * @param min the exclusive lower bound for the collection size
   * @param max the exclusive upper bound for the collection size
   * @return a validation ensuring the collection size is within the given bounds
   */
  public static Validation<Collection<?>> sizeBetween(int min, int max) {
    return SimpleValidation.from(
        c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) && c.size() > min && c.size() < max,
        ValidationMetadata.builder("Size must be greater than {0} and less than {1}")
            .messageKey("validation.collection.sizeBetween")
            .messageArgument(min)
            .messageArgument(max)
            .build());
  }

  /**
   * Returns a validation that passes only if the collection contains the specified {@code value}.
   *
   * <p>The collection must be non-{@code null}. The {@code value} must also be non-{@code null};
   * otherwise a {@link NullPointerException} is thrown.
   *
   * @param value the value that must be present in the collection
   * @param <T> the element type
   * @return a validation ensuring the collection contains the given value
   * @throws NullPointerException if {@code value} is {@code null}
   */
  public static <T> Validation<Collection<T>> contains(T value) {
    Objects.requireNonNull(value, "Object which should be contained, must not be null");

    return SimpleValidation.from(
        c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) && c.contains(value),
        ValidationMetadata.builder("Collection must contain Object \"{0}\"")
            .messageKey("validation.collection.contains")
            .messageArgument(value)
            .build());
  }

  /**
   * Returns a validation that passes only if the collection contains at least one {@code null}
   * element.
   *
   * <p>The collection must be non-{@code null}. The validation succeeds if {@code
   * stream().anyMatch(Objects::isNull)} is true.
   *
   * @param <T> the element type
   * @return a validation ensuring the collection contains at least one {@code null} value
   */
  public static <T> Validation<Collection<T>> hasNullElements() {
    return SimpleValidation.from(
        c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) && c.stream().anyMatch(Objects::isNull),
        ValidationMetadata.builder("Collection must contain at least one null element")
            .messageKey("validation.collection.hasNullElements")
            .build());
  }

  /**
   * Returns a validation that passes only if the collection does <em>not</em> contain any {@code
   * null} elements.
   *
   * <p>The collection must be non-{@code null}. The validation succeeds if {@code
   * stream().noneMatch(Objects::isNull)} is true.
   *
   * @param <T> the element type
   * @return a validation ensuring no {@code null} elements are present
   */
  public static <T> Validation<Collection<T>> noNullElements() {
    return SimpleValidation.from(
        c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) && c.stream().noneMatch(Objects::isNull),
        ValidationMetadata.builder("Collection must not contain null elements")
            .messageKey("validation.collection.noNullElements")
            .build());
  }

  /**
   * Returns a validation that passes only if all elements in the collection satisfy the given
   * predicate.
   *
   * <p>The collection must be non-{@code null}. The predicate must also be non-{@code null},
   * otherwise a {@link NullPointerException} is thrown.
   *
   * <p>If the collection is empty, the result is {@code true}, matching the semantics of {@link
   * java.util.stream.Stream#allMatch(Predicate)}.
   *
   * @param predicate the condition each element must satisfy
   * @param <T> the element type
   * @return a validation ensuring all elements match the predicate
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  public static <T> Validation<Collection<T>> allMatch(Predicate<T> predicate) {
    return SimpleValidation.from(
        c ->
            notNull(c, COLLECTION_MUST_NOT_BE_NULL)
                && notNull(predicate, "Predicate all elements should match, must not be null")
                && c.stream().allMatch(predicate),
        ValidationMetadata.builder("All elements must match the Predicate")
            .messageKey("validation.collection.allMatch")
            .build());
  }

  /**
   * Returns a validation that passes if at least one element in the collection satisfies the given
   * predicate.
   *
   * <p>The collection must be non-{@code null}. The predicate must also be non-{@code null},
   * otherwise a {@link NullPointerException} is thrown.
   *
   * @param predicate the condition at least one element must satisfy
   * @param <T> the element type
   * @return a validation ensuring at least one element matches the predicate
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  public static <T> Validation<Collection<T>> anyMatch(Predicate<T> predicate) {
    return SimpleValidation.from(
        c ->
            notNull(c, COLLECTION_MUST_NOT_BE_NULL)
                && notNull(predicate, "Predicate elements should match, must not be null")
                && c.stream().anyMatch(predicate),
        ValidationMetadata.builder("At least one element must match the Predicate")
            .messageKey("validation.collection.anyMatch")
            .build());
  }

  /**
   * Returns a validation that passes only if no element in the collection satisfies the given
   * predicate.
   *
   * <p>The collection must be non-{@code null}. The predicate must also be non-{@code null},
   * otherwise a {@link NullPointerException} is thrown.
   *
   * @param predicate the predicate no element should satisfy
   * @param <T> the element type
   * @return a validation ensuring no element matches the predicate
   * @throws NullPointerException if {@code predicate} is {@code null}
   */
  public static <T> Validation<Collection<T>> noneMatch(Predicate<T> predicate) {
    return SimpleValidation.from(
        c ->
            notNull(c, COLLECTION_MUST_NOT_BE_NULL)
                && notNull(predicate, "Predicate no element should match, must not be null")
                && c.stream().noneMatch(predicate),
        ValidationMetadata.builder("No element should match the predicate")
            .messageKey("validation.collection.noneMatch")
            .build());
  }
}
