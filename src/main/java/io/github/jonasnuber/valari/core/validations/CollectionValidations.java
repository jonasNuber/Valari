package io.github.jonasnuber.valari.core.validations;

import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.Validation;
import io.github.jonasnuber.valari.core.SimpleValidation;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Predicate;

import static io.github.jonasnuber.valari.core.validations.ObjectValidations.notNull;

/**
 * Utility class providing predefined validations for {@link Collection} values.
 * <p>
 * These validations define conditions that a collection or its elements must meet to be considered valid.
 * They are intended as reusable building blocks for common collection-related validation scenarios.
 * </p>
 *
 * <p>All validations returned by this class ensure that the input collection itself is not {@code null},
 * unless explicitly documented otherwise.</p>
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
     * @return a validation ensuring the collection is not empty
     */
    public static Validation<Collection<?>> notEmpty() {
        return SimpleValidation.from(
                c -> !(c == null || c.isEmpty()),
                new ValidationMetadata.Builder("Collection must not be empty")
                        .messageKey("validation.collection.empty")
                        .build()
        );
    }

    /**
     * Returns a validation that passes if the collection size is strictly greater than {@code min}
     * and strictly less than {@code max}.
     *
     * @param min the minimum number of elements (exclusive)
     * @param max the maximum number of elements (exclusive)
     * @return a validation ensuring the collection size is within the given range
     */
    public static Validation<Collection<?>> sizeBetween(int min, int max) {
        return SimpleValidation.from(
                c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) &&
                        c.size() > min &&
                        c.size() < max,
                new ValidationMetadata.Builder("Size must be greater than {0} and less than {1}")
                        .messageKey("validation.collection.sizeBetween")
                        .messageArgument(min)
                        .messageArgument(max)
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if the collection contains the specified value.
     *
     * @param value the value that must be present in the collection (must not be {@code null})
     * @param <T>   the type of elements in the collection
     * @return a validation ensuring the collection contains the given value
     * @throws NullPointerException if {@code value} is {@code null}
     */
    public static <T> Validation<Collection<T>> contains(T value) {
        Objects.requireNonNull(value, "Object which should be contained, must not be null");

        return SimpleValidation.from(
                c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) &&
                        c.contains(value),
                new ValidationMetadata.Builder("Collection must contain Object \"{0}\"")
                        .messageKey("validation.collection.contains")
                        .messageArgument(value)
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if the collection contains at least one {@code null} element.
     *
     * @param <T> the type of elements in the collection
     * @return a validation ensuring the collection contains at least one {@code null} element
     */
    public static <T> Validation<Collection<T>> hasNullElements() {
        return SimpleValidation.from(
                c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) &&
                        c.stream().anyMatch(Objects::isNull),
                new ValidationMetadata.Builder("Collection must contain at least one null element")
                        .messageKey("validation.collection.hasNullElements")
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if the collection does not contain any {@code null} elements.
     *
     * @param <T> the type of elements in the collection
     * @return a validation ensuring no {@code null} elements are present
     */
    public static <T> Validation<Collection<T>> noNullElements() {
        return SimpleValidation.from(
                c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) &&
                        c.stream().noneMatch(Objects::isNull),
                new ValidationMetadata.Builder("Collection must not contain null elements")
                        .messageKey("validation.collection.noNullElements")
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if all elements in the collection match the given predicate.
     *
     * @param predicate the predicate that all elements must satisfy (must not be {@code null})
     * @param <T>       the type of elements in the collection
     * @return a validation ensuring all elements match the predicate
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    public static <T> Validation<Collection<T>> allMatch(Predicate<T> predicate) {
        return SimpleValidation.from(
                c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) &&
                        notNull(predicate, "Predicate all elements should match, must not be null") &&
                        c.stream().allMatch(predicate),
                new ValidationMetadata.Builder("All elements must match the Predicate")
                        .messageKey("validation.collection.allMatch")
                        .build()
        );
    }

    /**
     * Returns a validation that passes if at least one element in the collection matches the given predicate.
     *
     * @param predicate the predicate that at least one element must satisfy (must not be {@code null})
     * @param <T>       the type of elements in the collection
     * @return a validation ensuring at least one element matches the predicate
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    public static <T> Validation<Collection<T>> anyMatch(Predicate<T> predicate) {
        return SimpleValidation.from(
                c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) &&
                        notNull(predicate, "Predicate elements should match, must not be null") &&
                        c.stream().anyMatch(predicate),
                new ValidationMetadata.Builder("At least one element must match the Predicate")
                        .messageKey("validation.collection.anyMatch")
                        .build()
        );
    }

    /**
     * Returns a validation that passes only if no element in the collection matches the given predicate.
     *
     * @param predicate the predicate that no element should satisfy (must not be {@code null})
     * @param <T>       the type of elements in the collection
     * @return a validation ensuring no element matches the predicate
     * @throws NullPointerException if {@code predicate} is {@code null}
     */
    public static <T> Validation<Collection<T>> noneMatch(Predicate<T> predicate) {
        return SimpleValidation.from(
                c -> notNull(c, COLLECTION_MUST_NOT_BE_NULL) &&
                        notNull(predicate, "Predicate no element should match, must not be null") &&
                        c.stream().noneMatch(predicate),
                new ValidationMetadata.Builder("No element should match the predicate")
                        .messageKey("validation.collection.noneMatch")
                        .build()
        );
    }
}
