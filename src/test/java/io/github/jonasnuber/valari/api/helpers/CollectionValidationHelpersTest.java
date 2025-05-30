package io.github.jonasnuber.valari.api.helpers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CollectionValidationHelpersTest {

    @Test
    void notBlank_ShouldReturnValidResult_ForNotEmptyCollection() {
        var collection = List.of("String1", "String2");
        var validation = CollectionValidationHelpers.notEmpty();

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

    @ParameterizedTest(name = "Test with empty input collection: {0}")
    @MethodSource("emptyCollectionValues")
    void notBlank_ShouldReturnInvalidResult_ForEmptyCollection(Collection<?> emptyCollection) {
        var validation = CollectionValidationHelpers.notEmpty();

        var result = validation.test(emptyCollection);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("Collection must not be empty");
    }

    @Test
    void sizeBetween_ShouldReturnValidResult_ForCollectionSizeGreaterThanMinAndSmallerThanMax() {
        var minSize = 3;
        var maxSize = 5;
        var collection = List.of("Element1", "Element2", "Element3", "Element4");
        var validation = CollectionValidationHelpers.sizeBetween(minSize, maxSize);

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

    @ParameterizedTest(name = "Test with input collection: \"{0}\"")
    @MethodSource("collectionsWithVariousSizes")
    void sizeBetween_ShouldReturnInvalidResult_ForVariousCollectionSizes(Collection<?> collection) {
        var minSize = 3;
        var maxSize = 5;
        var validation = CollectionValidationHelpers.sizeBetween(minSize, maxSize);

        var result = validation.test(collection);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo(String.format("Size must be greater than %s and less than %s", minSize, maxSize));
    }

    @Test
    void sizeBetween_ShouldThrowException_ForNullCollection() {
        var validation = CollectionValidationHelpers.sizeBetween(0,2);

        var thrown = catchThrowable(() -> validation.test(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Collection must not be null");
    }

    @Test
    void contains_ShouldReturnValidResult_ForContainingObject() {
        var objectToContain = "String";
        var collection = Set.of(objectToContain, "OtherString");
        var validation = CollectionValidationHelpers.contains(objectToContain);

        var result = validation.test(collection);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void contains_ShouldReturnInvalid_ForNotContainingObject() {
        var objectToContain = "String";
        var collection = Set.of("OtherString");
        var validation = CollectionValidationHelpers.contains(objectToContain);

        var result = validation.test(collection);

        assertThat(result.isValid()).isFalse();
        assertThat(result.getCauseDescription())
                .isEqualTo("Collection must contain Object \"String\"");
    }
}