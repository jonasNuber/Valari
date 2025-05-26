package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.*;

class DomainValidatorTest {
    private static DomainValidator<Person> validator;

    @BeforeAll
    static void init() {
        validator = DomainValidator.of(Person.class)
                .field(Person::getName, "Name")
                    .mustSatisfy(notEmpty())
                .field(Person::getAge, "Age")
                    .mustSatisfy(greaterThan(0));
    }

    @Test
    void validate_ShouldReturnValid_WhenAllFieldsPass() {
        var name = "Alice";
        var age = 30;

        var result = validator.validate(new Person(name, age));

        assertThat(result.hasFailures()).isFalse();
        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void validate_ShouldReturnInvalid_WhenFieldFails() {
        var age = -1;

        var result = validator.validate(new Person(null, age));

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .extracting(ValidationResult::getFieldName)
                .containsExactlyInAnyOrder("Name", "Age");
    }

    @Test
    void validate_ShouldIgnoreNullFields_WhenUsingIfPresent() {
        var validatorWithOptionalName = DomainValidator.of(Person.class)
                .field(Person::getName, "Name")
                .ifPresent(notEmpty())
                .field(Person::getAge, "Age")
                .mustSatisfy(greaterThan(0));
        var person = new Person(null, 42);

        var result = validatorWithOptionalName.validate(person);

        assertThat(result.hasFailures()).isFalse();
    }

    @Test
    void validate_ShouldFail_WhenPresentFieldIsInvalid_UsingIfPresent() {
        var validatorWithOptionalName = DomainValidator.of(Person.class)
                .field(Person::getName, "Name")
                .ifPresent(notEmpty())
                .field(Person::getAge, "Age")
                .mustSatisfy(greaterThan(0));
        var person = new Person("", 42);

        var result = validatorWithOptionalName.validate(person);

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .extracting(ValidationResult::getFieldName)
                .containsExactly("Name");
    }

    @Test
    void validateAndThrow_ShouldNotThrow_WhenFieldsPass() {
        var name = "Bob";
        var age = 25;

        ThrowableAssert.ThrowingCallable executable = () -> validator.validateAndThrow(new Person(name, age));

        assertThatCode(executable).doesNotThrowAnyException();
    }

    @Test
    void validateAndThrow_ShouldThrow_WhenFieldsFail() {
        var age = 0;

        var thrown = catchThrowable(() -> validator.validateAndThrow(new Person(null, age)));

        assertThat(thrown)
                .isInstanceOf(AggregatedValidationException.class)
                .hasMessage("Validation for class io.github.jonasnuber.valari.Person failed with 2 error(s):\n" +
                        " - Field 'Name': must not be blank\n" +
                        " - Field 'Age': must be greater than 0\n");
    }

    @Test
    void failFast_ShouldUseFailFastStrategy() {
        var age = 0;

        var result = validator
                .failFast()
                .validate(new Person(null, age));

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .hasSize(1)
                .extracting(ValidationResult::getFieldName)
                .containsExactly("Name");
    }

    @Test
    void collectFailures_ShouldUseCollectFailuresStrategy() {
        var age = 0;

        var result = validator
                .collectFailures()
                .validate(new Person(null, age));

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .hasSize(2)
                .extracting(ValidationResult::getFieldName)
                .containsExactlyInAnyOrder("Name", "Age");
    }

    @Test
    void and_ShouldReturnSameInstance(){
        var expectedValidator = validator;

        var actualValidator = validator.and();

        assertThat(actualValidator).isEqualTo(expectedValidator);
    }

    @Test
    void and_ShouldNotAffectValidation(){
        var andValidator = DomainValidator.of(Person.class)
                .field(Person::getName, "Name")
                    .mustSatisfy(notEmpty())
                .and()
                .field(Person::getAge, "Age")
                    .mustSatisfy(greaterThan(0));
        var age = -1;

        var result = andValidator.validate(new Person(null, age));

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .extracting(ValidationResult::getFieldName)
                .containsExactlyInAnyOrder("Name", "Age");
    }

    @Test
    void shouldAllowStrategySwitching() {
        var age = 0;

        var result = validator
                .failFast()
                .collectFailures()
                .validate(new Person(null, age));

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .hasSize(2)
                .extracting(ValidationResult::getFieldName)
                .containsExactlyInAnyOrder("Name", "Age");
    }
}