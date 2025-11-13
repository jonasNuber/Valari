package io.github.jonasnuber.valari.core.validations;

import org.junit.jupiter.api.Test;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CollectionValidationsTest {

    @Test
    void notEmpty_ShouldReturnValidResult_ForNotEmptyCollection() {
        var collection = List.of("String1", "String2");
        var validation = CollectionValidations.notEmpty();

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

    static Stream<Collection<?>> emptyCollectionValues() {
        return Stream.of(
                null,
                Collections.emptyList(),
                Collections.emptySet()
        );
    }

//    @ParameterizedTest(name = "Test with empty input collection: {0}")
//    @MethodSource("emptyCollectionValues")
//    void notEmpty_ShouldReturnInvalidResult_ForEmptyCollection(Collection<?> emptyCollection) {
//        var validation = CollectionValidations.notEmpty();
//
//        var result = validation.test(emptyCollection);
//
//        assertThat(result.isValid()).isFalse();
//        assertThat(result.resolveValidationMessage())
//                .isEqualTo("Collection must not be empty");
//    }

    @Test
    void sizeBetween_ShouldReturnValidResult_ForCollectionSizeGreaterThanMinAndSmallerThanMax() {
        var minSize = 3;
        var maxSize = 5;
        var collection = List.of("Element1", "Element2", "Element3", "Element4");
        var validation = CollectionValidations.sizeBetween(minSize, maxSize);

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

    static Stream<Collection<?>> collectionsWithVariousSizes() {
        return Stream.of(
                List.of(1, 2, 3),        // Collection same size as min
                Set.of(1, 2, 3, 4, 5),   // Collection same size as max
                List.of(1),          // Collection smaller than min
                Set.of(1, 2, 3, 4, 5, 6) // Collection greater than max
        );
    }

//    @ParameterizedTest(name = "Test with input collection: \"{0}\"")
//    @MethodSource("collectionsWithVariousSizes")
//    void sizeBetween_ShouldReturnInvalidResult_ForVariousCollectionSizes(Collection<?> collection) {
//        var minSize = 3;
//        var maxSize = 5;
//        var validation = CollectionValidations.sizeBetween(minSize, maxSize);
//
//        var result = validation.test(collection);
//
//        assertThat(result.isValid()).isFalse();
//        assertThat(result.resolveValidationMessage())
//                .isEqualTo(String.format("Size must be greater than %s and less than %s", minSize, maxSize));
//    }

    @Test
    void sizeBetween_ShouldThrowException_ForNullCollection() {
        var validation = CollectionValidations.sizeBetween(0,2);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Collection must not be null");
    }

    @Test
    void contains_ShouldReturnValidResult_ForContainingObject() {
        var objectToContain = "String";
        var collection = Set.of(objectToContain, "OtherString");
        var validation = CollectionValidations.contains(objectToContain);

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

//    @Test
//    void contains_ShouldReturnInvalidResult_ForNotContainingObject() {
//        var objectToContain = "String";
//        var collection = Set.of("OtherString");
//        var validation = CollectionValidations.contains(objectToContain);
//
//        var result = validation.test(collection);
//
//        assertThat(result.isValid()).isFalse();
//        assertThat(result.resolveValidationMessage())
//                .isEqualTo("Collection must contain Object \"String\"");
//    }

    @Test
    void contains_ShouldThrowException_ForNullCollection() {
        var validation = CollectionValidations.contains("Value");

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Collection must not be null");
    }

    @Test
    void contains_ShouldThrowException_ForNullValueToContain() {
        var thrown = catchThrowable(() -> CollectionValidations.contains(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object which should be contained, must not be null");
    }

    @Test
    void hasNullElements_ShouldReturnValidResult_ForContainedNullElements() {
        var collection = new ArrayList<String>();
        collection.add("someValue");
        collection.add(null);
        var validation = CollectionValidations.<String>hasNullElements();

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

//    @Test
//    void hasNullElements_ShouldReturnInvalidResult_ForNoNullElements() {
//        var collection = List.of("someValue", "SomeOtherValue");
//        var validation = CollectionValidations.<String>hasNullElements();
//
//        var result = validation.test(collection);
//
//        assertThat(result.isValid()).isFalse();
//        assertThat(result.resolveValidationMessage())
//                .isEqualTo("Collection must contain at least one null element");
//    }

    @Test
    void hasNullElements_ShouldThrowException_ForNullCollection() {
        var validation = CollectionValidations.hasNullElements();

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Collection must not be null");
    }

    @Test
    void noNullElements_ShouldReturnValidResult_ForNoNullElements() {
        var collection = List.of("someValue", "SomeOtherValue");
        var validation = CollectionValidations.<String>noNullElements();

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

//    @Test
//    void noNullElements_ShouldReturnInvalidResult_ForContainingNullElements() {
//        var collection = new ArrayList<String>();
//        collection.add("someValue");
//        collection.add(null);
//        var validation = CollectionValidations.<String>noNullElements();
//
//        var result = validation.test(collection);
//
//        assertThat(result.isValid()).isFalse();
//        assertThat(result.resolveValidationMessage())
//                .isEqualTo("Collection must not contain null elements");
//    }

    @Test
    void noNullElements_ShouldThrowException_ForNullCollection() {
        var validation = CollectionValidations.noNullElements();

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Collection must not be null");
    }

    @Test
    void allMatch_ShouldReturnValidResult_ForAllElementsMatchingPredicate() {
        Predicate<String> predicate = s -> s.length() > 2;
        var collection = Set.of("String", "OtherValue", "AndAnother");
        var validation = CollectionValidations.allMatch(predicate);

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

//    @Test
//    void allMatch_ShouldReturnInvalidResult_ForNotAllElementsMatchingPredicate() {
//        Predicate<String> predicate = s -> s.length() > 5;
//        var collection = Set.of("String", "value", "1234");
//        var validation = CollectionValidations.allMatch(predicate);
//
//        var result = validation.test(collection);
//
//        assertThat(result.isValid()).isFalse();
//        assertThat(result.resolveValidationMessage())
//                .isEqualTo("All elements must match the Predicate");
//    }

    @Test
    void allMatch_ShouldThrowException_ForNullCollection() {
        var validation = CollectionValidations.allMatch(String.class::isInstance);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Collection must not be null");
    }

    @Test
    void allMatch_ShouldThrowException_ForNullPredicate() {
        var validation = CollectionValidations.allMatch(null);

        var thrown = catchThrowable(() -> validation.test(Collections.emptySet()));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Predicate all elements should match, must not be null");
    }

    @Test
    void anyMatch_ShouldReturnValidResult_ForAnyElementsMatchingPredicate() {
        Predicate<String> predicate = s -> s.length() > 2;
        var collection = Set.of("String", "I", "123");
        var validation = CollectionValidations.anyMatch(predicate);

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

//    @Test
//    void anyMatch_ShouldReturnInvalidResult_ForNoElementsMatchingPredicate() {
//        Predicate<String> predicate = s -> s.length() > 5;
//        var collection = Set.of("sad", "value", "1234");
//        var validation = CollectionValidations.anyMatch(predicate);
//
//        var result = validation.test(collection);
//
//        assertThat(result.isValid()).isFalse();
//        assertThat(result.resolveValidationMessage())
//                .isEqualTo("At least one element must match the Predicate");
//    }

    @Test
    void anyMatch_ShouldThrowException_ForNullCollection() {
        var validation = CollectionValidations.anyMatch(String.class::isInstance);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Collection must not be null");
    }

    @Test
    void anyMatch_ShouldThrowException_ForNullPredicate() {
        var validation = CollectionValidations.anyMatch(null);

        var thrown = catchThrowable(() -> validation.test(Collections.emptySet()));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Predicate elements should match, must not be null");
    }

    @Test
    void noneMatch_ShouldReturnValidResult_ForNoElementsMatchingPredicate() {
        Predicate<String> predicate = s -> s.length() < 2;
        var collection = Set.of("String", "Sad", "123");
        var validation = CollectionValidations.noneMatch(predicate);

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

//    @Test
//    void noneMatch_ShouldReturnInvalidResult_ForElementsMatchingPredicate() {
//        Predicate<String> predicate = s -> s.length() <= 5;
//        var collection = Set.of("sad", "value", "1234");
//        var validation = CollectionValidations.noneMatch(predicate);
//
//        var result = validation.test(collection);
//
//        assertThat(result.isValid()).isFalse();
//        assertThat(result.resolveValidationMessage())
//                .isEqualTo("No element should match the predicate");
//    }

    @Test
    void noneMatch_ShouldThrowException_ForNullCollection() {
        var validation = CollectionValidations.noneMatch(String.class::isInstance);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Collection must not be null");
    }

    @Test
    void noneMatch_ShouldThrowException_ForNullPredicate() {
        var validation = CollectionValidations.noneMatch(null);

        var thrown = catchThrowable(() -> validation.test(Collections.emptySet()));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Predicate no element should match, must not be null");
    }
}