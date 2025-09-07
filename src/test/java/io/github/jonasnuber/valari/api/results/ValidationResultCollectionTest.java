package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class ValidationResultCollectionTest{

    @Test
    void add_ShouldAddResultToCollection() {
        var result = ValidationResult.skip();
        var resultCollection = new ValidationResultCollection(Person.class);

        resultCollection.add(result);

        assertThat(resultCollection.getResults()).containsExactly(result);
    }

    @Test
    void add_ShouldThrowException_ForNullResult() {
        var resultCollection = new ValidationResultCollection(Person.class);

        var thrown = catchThrowable(() -> resultCollection.add(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ValidationResult cannot be added if null");
    }

    @Test
    void getState_ShouldReturnFailure_WhenAnyResultFailed() {
        var collection = new ValidationResultCollection(String.class);
        collection.add(new ValidationResult.Builder("msg").fail());
        collection.add(new ValidationResult.Builder("msg").ok());
        collection.add(new ValidationResult.Builder("msg").skip());

        assertThat(collection.getState()).isEqualTo(ValidationState.FAILURE);
    }

    @Test
    void getState_ShouldReturnSkipped_WhenAllResultsSkipped() {
        var collection = new ValidationResultCollection(String.class);
        collection.add(new ValidationResult.Builder("msg").skip());
        collection.add(new ValidationResult.Builder("msg").skip());

        assertThat(collection.getState()).isEqualTo(ValidationState.SKIPPED);
    }

    @Test
    void getState_ShouldReturnSuccess_WhenNoFailuresAndNotAllSkipped() {
        var collection = new ValidationResultCollection(String.class);
        collection.add(new ValidationResult.Builder("msg").ok());
        collection.add(new ValidationResult.Builder("msg").skip());

        assertThat(collection.getState()).isEqualTo(ValidationState.SUCCESS);
    }

    @Test
    void getMessage_ShouldResolveSuccessMessage() {
        var collection = new ValidationResultCollection(Person.class);
        collection.add(
                new ValidationResult.Builder("some validation it passed")
                .labelType(LabelType.FIELD)
                .label("some Field")
                .ok());

        var message = collection.getMessage();

        assertThat(message)
                .contains("Validation for class io.github.jonasnuber.valari.Person succeeded:")
                .contains("- Field \"some Field\": some validation it passed");
    }

    @Test
    void getMessage_ShouldResolveSkippedMessage() {
        var collection = new ValidationResultCollection(Person.class);
        collection.add(new ValidationResult.Builder("skipped").skip());

        var message = collection.getMessage();

        assertThat(message).isEqualTo("Validation for class io.github.jonasnuber.valari.Person was skipped entirely.");
    }

    @Test
    void getMessage_ShouldResolveFailureMessage() {
        var collection = new ValidationResultCollection(String.class);
        collection.add(
                new ValidationResult.Builder("some validation which was failed")
                        .labelType(LabelType.VALUE)
                        .label("age")
                        .fail());

        var message = collection.getMessage();

        assertThat(message)
                .contains("Validation for class java.lang.String failed with 1 error(s):")
                .contains("- Value \"age\": some validation which was failed");
    }

    @Test
    void throwIfInvalid_ShouldThrowException_WhenFailuresExist() {
        var collection = new ValidationResultCollection(Person.class);
        collection.add(new ValidationResult.Builder("fail").fail());

        var thrown = catchThrowable(collection::throwIfInvalid);

        assertThat(thrown)
                .isInstanceOf(AggregatedValidationException.class)
                .hasMessage("Validation for class io.github.jonasnuber.valari.Person failed with 1 error(s):\n" +
                        "- Subject \"<unknown>\": fail\n");
    }

    @Test
    void toValidationResult_ShouldReturnAggregatedValidationResult() {
        var collection = new ValidationResultCollection(Person.class);
        var aggregated = collection.toValidationResult();

        assertThat(aggregated).isInstanceOf(ValidationResult.class);
        assertThat(aggregated.getMessage())
                .isEqualTo(collection.getMessage());
    }

    @Test
    void aggregatedValidationResult_EqualsAndHashCode() {
        var collection1 = new ValidationResultCollection(Person.class);
        var collection2 = new ValidationResultCollection(Person.class);

        var result1 = collection1.toValidationResult();
        var result2 = collection2.toValidationResult();

        assertThat(result1).isNotEqualTo(result2);
        assertThat(result1.hashCode()).isNotEqualTo(result2.hashCode());
    }
}