package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.DomainValidator;
import io.github.jonasnuber.valari.api.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class CollectFailuresStrategyTest {

    @Test
    void validate_ShouldThrowException_ForNullInputs() {
        var strategy = new CollectFailuresStrategy<Person>();

        var nullToValidate = catchThrowable(() -> strategy.validate(null, null, null));
        var nullValidations = catchThrowable(() -> strategy.validate(new Person("name", 3), null, null));
        var nullClass = catchThrowable(() -> strategy.validate(
                new Person("name", 3),
                List.of(new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name")),
                null));

        assertThat(nullToValidate)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object to validate must not be null");
        assertThat(nullValidations)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Validations to validate Object by must not be null");
        assertThat(nullClass)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The class of the Object to validate must not be null");
    }

    @Test
    void validate_ShouldCollectAllValidationFailures_WhenFieldsFail() {
        var strategy = new CollectFailuresStrategy<Person>();
        var nameValidation = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        nameValidation.mustSatisfy(notEmpty());
        var ageValidation = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getAge, "Age");
        ageValidation.mustSatisfy(greaterThan(0));
        var invalidPerson = new Person(null, -5);

        var result = strategy.validate(
                invalidPerson,
                List.of(nameValidation, ageValidation),
                Person.class
        );

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .hasSize(2)
                .extracting(ValidationResult::getFieldName)
                .containsExactlyInAnyOrder("Name", "Age");
    }

    @Test
    void validate_ShouldReturnEmptyResult_ForFieldsPassing() {
        var strategy = new CollectFailuresStrategy<Person>();
        var nameValidation = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        nameValidation.mustSatisfy(notEmpty());
        var validPerson = new Person("Alice", 14);

        var result = strategy.validate(
                validPerson,
                List.of(nameValidation),
                Person.class
        );

        assertThat(result.hasFailures()).isFalse();
        assertThat(result.getResults()).isEmpty();
    }
}