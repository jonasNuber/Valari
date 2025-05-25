package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.BaseTest;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.spi.Validation;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ValidationResultCollectionTest extends BaseTest {

    @Test
    void add_ShouldOnlyAddInvalidResults() {
        var resultCollection = new ValidationResultCollection(Validation.class);
        var firstFailedResult = ValidationResult.fail("Some Error Message");
        var secondFailedResult = ValidationResult.fail("Some other Error Message");
        var firstSucceededResult = ValidationResult.ok();
        resultCollection.add(firstFailedResult);
        resultCollection.add(secondFailedResult);
        resultCollection.add(firstSucceededResult);

        var resultList = resultCollection.getResults();

        assertThat(resultList)
                .hasSize(2)
                .doesNotContain(firstSucceededResult)
                .containsExactlyInAnyOrder(firstFailedResult, secondFailedResult);
    }

    @Test
    void throwIfInvalid_ShouldNotThrowException_ForNoInvalidResults() {
        var resultCollection = new ValidationResultCollection(Validation.class);
        resultCollection.add(ValidationResult.ok());

        ThrowableAssert.ThrowingCallable executable = resultCollection::throwIfInvalid;

        assertThatCode(executable).doesNotThrowAnyException();
    }

    @Test
    void throwIfInvalid_ShouldThrowException_ForInvalidResults() {
        var causeDescription = "some Error Message";
        var fieldName = "fieldName";
        var resultCollection = new ValidationResultCollection((Validation.class));
        resultCollection.add(ValidationResult.fail(causeDescription));
        resultCollection.add(ValidationResult.fail(causeDescription, fieldName));

        var thrown = catchThrowable(resultCollection::throwIfInvalid);

        assertThat(thrown)
                .isInstanceOf(AggregatedValidationException.class)
                .hasMessage(
                        String.format("Validation for %s failed with %d error(s):%n - Field '%s': %s%n - Field '%s': %s%n",
                                Validation.class, 2, "Unknown", causeDescription, fieldName, causeDescription));
    }
}