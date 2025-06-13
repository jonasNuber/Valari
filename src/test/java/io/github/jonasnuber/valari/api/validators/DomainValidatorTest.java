package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.internal.bindings.FieldRuleBinding;
import io.github.jonasnuber.valari.internal.bindings.NestedRuleBinding;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.*;

class DomainValidatorTest {
    private static DomainValidator<Person> validator;

    @BeforeAll
    static void init() {
        validator = DomainValidator.of(Person.class)
                .field("Name", Person::getName)
                .mustSatisfy(notEmpty())
                .field("Age", Person::getAge)
                .mustSatisfy(greaterThan(0));
    }

    @Test
    void of_ShouldThrowException_ForNullClass() {
        var thrown = catchThrowable(() -> DomainValidator.of(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Class must not be null");
    }

    @Test
    void field_ShouldThrowException_ForNullInput() {
        var nullFieldName = catchThrowable(() -> DomainValidator.of(Person.class).field(null, null));
        var nullExtractor = catchThrowable(() -> DomainValidator.of(Person.class).field("someFieldName", null));

        assertThat(nullFieldName)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FieldName must not be null");
        assertThat(nullExtractor)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Extractor Function must not be null");
    }

    @Test
    void field_ShouldReturnFieldRuleBinding_ForValidInput() {
        var fieldName = "Age";
        Function<Person, Integer> extractor = Person::getAge;

        var fieldRuleBinding = DomainValidator.of(Person.class).field(fieldName, extractor);

        assertThat(fieldRuleBinding)
                .isInstanceOf(FieldRuleBinding.class);
    }

    @Test
    void nested_ShouldThrowException_ForNullInput() {
        var nullFieldName = catchThrowable(() -> DomainValidator.of(Person.class).nested(null, null));
        var nullExtractor = catchThrowable(() -> DomainValidator.of(Person.class).nested("someFieldName", null));

        assertThat(nullFieldName)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FieldName must not be null");
        assertThat(nullExtractor)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Extractor Function must not be null");
    }

    @Test
    void nested_ShouldReturnNestedValidationBinding_ForValidInput() {
        Function<Person, Integer> extractor = Person::getAge;
        var fieldName = "Age";

        var nestedValidationBinding = DomainValidator.of(Person.class).nested(fieldName, extractor);

        assertThat(nestedValidationBinding)
                .isInstanceOf(NestedRuleBinding.class);
    }

    @Test
    void validate_ShouldThrowException_ForNullObject() {
        var nullToValidate = catchThrowable(() -> validator.validate(null));

        assertThat(nullToValidate)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object to validate must not be null");
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
                .field("Name", Person::getName)
                .ifPresent(notEmpty())
                .field("Age", Person::getAge)
                .mustSatisfy(greaterThan(0));
        var person = new Person(null, 42);

        var result = validatorWithOptionalName.validate(person);

        assertThat(result.hasFailures()).isFalse();
    }

    @Test
    void validate_ShouldFail_WhenIfPresentFieldIsInvalid() {
        var validatorWithOptionalName = DomainValidator.of(Person.class)
                .field("Name", Person::getName)
                .ifPresent(notEmpty())
                .field("Age", Person::getAge)
                .mustSatisfy(greaterThan(0));
        var person = new Person("", 42);

        var result = validatorWithOptionalName.validate(person);

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .extracting(ValidationResult::getFieldName)
                .containsExactly("Name");
    }

    @Test
    void validateAndThrow_ShouldThrowException_ForNullObject() {
        var nullToValidate = catchThrowable(() -> validator.validateAndThrow(null));

        assertThat(nullToValidate)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object to validate must not be null");
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
                        " - Field 'Name': must not be empty\n" +
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
    void and_ShouldReturnSameInstance() {
        var expectedValidator = validator;

        var actualValidator = validator.and();

        assertThat(actualValidator).isEqualTo(expectedValidator);
    }

    @Test
    void and_ShouldNotAffectValidation() {
        var andValidator = DomainValidator.of(Person.class)
                .field("Name", Person::getName)
                .mustSatisfy(notEmpty())
                .and()
                .field("Age", Person::getAge)
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