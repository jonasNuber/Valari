package io.github.jonasnuber.valari.api.helpers;

import io.github.jonasnuber.valari.BaseTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

class CollectionValidationHelpersTest extends BaseTest {

    @Test
    void notBlank_ShouldReturnValidResult_ForNotBlankCollection() {
        var collection = List.of("String1", "String2");

        var validation = CollectionValidationHelpers.notBlank();

        assertValid(validation, collection);
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
    void notBlank_ShouldReturnInvalidResult_ForBlankCollection(Collection<?> emptyCollection) {
        var validation = CollectionValidationHelpers.notBlank();

        assertInvalid(validation, emptyCollection);
    }

    @Test
    void sizeBetween_ShouldReturnValidResult_ForCollectionSizeGreaterThanMinAndSmallerThanMax() {
        var minSize = 3;
        var maxSize = 5;
        var collection = List.of("Element1", "Element2", "Element3", "Element4");

        var validation = CollectionValidationHelpers.sizeBetween(minSize, maxSize);

        assertValid(validation, collection);
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

        assertInvalid(validation, collection);
    }

    @Test
    void sizeBetween_ShouldReturnInvalidResult_ForNullCollection() {
        var validation = CollectionValidationHelpers.sizeBetween(0,2);

        assertInvalid(validation, null);
    }
}