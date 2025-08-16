package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.spi.Validation;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ValidationResultCollectionTest{

    @Test
    void add_ShouldOnlyAddInvalidResults() {
        var resultCollection = new ValidationResultCollection(Validation.class);
        var firstFailedResult = new ValidationResult.Builder("Some Error Message").fail();
        var secondFailedResult = new ValidationResult.Builder("Some Other Error Message").fail();
        var firstSucceededResult = new ValidationResult.Builder("Some Success Message").ok();
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
        resultCollection.add(new ValidationResult.Builder("Some Success Message").ok());

        ThrowableAssert.ThrowingCallable executable = resultCollection::throwIfInvalid;

        assertThatCode(executable).doesNotThrowAnyException();
    }

    @Test
    void throwIfInvalid_ShouldThrowException_ForInvalidResults() {
        var causeDescription = "some Error Message";
        var fieldName = "fieldName";
        var resultCollection = new ValidationResultCollection((Validation.class));
        resultCollection.add(new ValidationResult.Builder("Some Error Message").fail());
        resultCollection.add(new ValidationResult.Builder("Some Other Error Message").fail());

        var thrown = catchThrowable(resultCollection::throwIfInvalid);

        assertThat(thrown)
                .isInstanceOf(AggregatedValidationException.class)
                .hasMessage(
                        String.format("Validation for %s failed with %d error(s):%n - Field '%s': %s%n - Field '%s': %s%n",
                                Validation.class, 2, "Unknown", causeDescription, fieldName, causeDescription));
    }
}