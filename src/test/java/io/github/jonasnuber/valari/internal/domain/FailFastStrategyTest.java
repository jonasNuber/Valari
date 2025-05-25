package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.ValidationResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;

class FailFastStrategyTest {

    @Test
    void validate_ShouldStopAtFirstFailure_WhenFieldsFail() {
        var strategy = new FailFastStrategy<Person>();
        var nameValidation = new FieldValidationBinding<>(null, Person::getName, "Name");
        nameValidation.mustSatisfy(notEmpty());
        var ageValidation = new FieldValidationBinding<>(null, Person::getAge, "Age");
        ageValidation.mustSatisfy(greaterThan(0));
        var invalidPerson = new Person(null, -5);

        var result = strategy.validate(
                invalidPerson,
                List.of(nameValidation, ageValidation) ,
                Person.class
        );

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .hasSize(1)
                .extracting(ValidationResult::getFieldName)
                .containsExactly("Name");
    }

    @Test
    void validate_ShouldReturnEmptyResult_ForFieldsPassing() {
        var strategy = new FailFastStrategy<Person>();
        var nameValidation = new FieldValidationBinding<>(null, Person::getName, "Name");
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