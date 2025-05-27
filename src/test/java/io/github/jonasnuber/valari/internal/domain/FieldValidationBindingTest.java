package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.DomainValidator;
import io.github.jonasnuber.valari.api.ValidationResult;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FieldValidationBindingTest {

    @Test
    void constructor_ShouldThrowException_ForNullInputs() {
        var nullParent = catchThrowable(() -> new FieldValidationBinding<>(null, null, null));
        var nullValueExtractor = catchThrowable(() -> new FieldValidationBinding<>(DomainValidator.of(Person.class), null, null));
        var nullFieldName = catchThrowable(() -> new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, null));

        assertThat(nullParent)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Parent Validator must not be null");
        assertThat(nullValueExtractor)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Extractor Method to get value for validation must not be null");
        assertThat(nullFieldName)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FieldName of the value to validate must not be null");
    }

    @Test
    void mustSatisfy_ShouldThrowException_WhenValidationIsNull() {
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");

        var thrown = catchThrowable(() -> binding.mustSatisfy(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("validation must not be null");
    }

    @Test
    void mustSatisfy_ShouldSetValidationUsed_ForBinding(){
        var validPerson = new Person("Bob", 25);
        var invalidPerson = new Person(null, 25);
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        binding.mustSatisfy(notEmpty());

        var validResult = binding.validate(validPerson);
        var invalidResult = binding.validate(invalidPerson);

        assertThat(validResult.isValid()).isTrue();
        assertThat(invalidResult.isInvalid()).isTrue();
    }

    @Test
    void mustSatisfy_ShouldOverridePreviousValidation() {
        var person = new Person("Bob", 25);
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        binding.mustSatisfy(value -> ValidationResult.fail("first rule"));
        binding.mustSatisfy(notEmpty()); // overrides previous

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldThrowException_WhenValidationIsNull() {
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");

        var thrown = catchThrowable(() -> binding.ifPresent(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("validation must not be null");
    }


    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsNull() {
        var person = new Person(null, 25);
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsPresentAndValid() {
        var person = new Person("Bob", 25);
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnInvalid_WhenFieldIsPresentAndInvalid() {
        var person = new Person("", 25);
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Name");
        assertThat(result.getCauseDescription()).isEqualTo("must not be blank");
    }

    @Test
    void validate_ShouldReturnValidResult_WhenFieldIsValid() {
        var person = new Person("Alice", 25);
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        binding.mustSatisfy(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_ShouldReturnInvalidResult_WhenFieldIsInvalid() {
        var person = new Person(null, 25);
        var binding = new FieldValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");
        binding.mustSatisfy(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Name");
        assertThat(result.getCauseDescription()).isEqualTo("must not be blank");
    }
}