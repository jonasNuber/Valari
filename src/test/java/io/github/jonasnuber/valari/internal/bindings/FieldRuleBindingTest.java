package io.github.jonasnuber.valari.internal.bindings;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import io.github.jonasnuber.valari.api.validators.DomainValidator;
import io.github.jonasnuber.valari.api.ValidationResult;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.*;

class FieldRuleBindingTest {

    @Test
    void constructor_ShouldThrowException_ForNullInputs() {
        var nullFieldName = catchThrowable(() -> new FieldRuleBinding<>(null, null, null));
        var nullValueExtractor = catchThrowable(() -> new FieldRuleBinding<>("someFieldName", null, null));
        var nullParent = catchThrowable(() -> new FieldRuleBinding<>("someFieldName", Person::getName, null));

        assertThat(nullFieldName)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("FieldName of the value to validate must not be null");
        assertThat(nullValueExtractor)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Extractor Method to get value for validation must not be null");
        assertThat(nullParent)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Parent Validator must not be null");
    }

    @Test
    void mustSatisfy_ShouldThrowException_WhenValidationIsNull() {
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));

        var thrown = catchThrowable(() -> binding.mustSatisfy(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("validation must not be null");
    }

    @Test
    void mustSatisfy_ShouldSetValidationUsed_ForBinding(){
        var validPerson = new Person("Bob", 25);
        var invalidPerson = new Person(null, 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.mustSatisfy(notEmpty());

        var validResult = binding.validate(validPerson);
        var invalidResult = binding.validate(invalidPerson);

        assertThat(validResult.isValid()).isTrue();
        assertThat(invalidResult.isInvalid()).isTrue();
    }

    @Test
    void mustSatisfy_ShouldOverridePreviousValidation() {
        var person = new Person("Bob", 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.mustSatisfy(value -> ValidationResult.fail("first rule"));
        binding.mustSatisfy(notEmpty()); // overrides previous

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldThrowException_WhenValidationIsNull() {
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));

        var thrown = catchThrowable(() -> binding.ifPresent(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("validation must not be null");
    }


    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsNull() {
        var person = new Person(null, 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsPresentAndValid() {
        var person = new Person("Bob", 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnInvalid_WhenFieldIsPresentAndInvalid() {
        var person = new Person("", 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Name");
        assertThat(result.getCauseDescription()).isEqualTo("must not be empty");
    }

    @Test
    void validate_ShouldReturnValidResult_WhenFieldIsValid() {
        var person = new Person("Alice", 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.mustSatisfy(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_ShouldReturnInvalidResult_WhenFieldIsInvalid() {
        var person = new Person(null, 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.mustSatisfy(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Name");
        assertThat(result.getCauseDescription()).isEqualTo("must not be empty");
    }

    @Test
    void validate_ShouldThrowException_WhenToValidateIsNull() {
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.mustSatisfy(notEmpty());

        var thrown = catchThrowable(() -> binding.validate(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Object to validate must not be null");
    }

    @Test
    void validate_ShouldThrowException_WhenValidationIsNull() {
        var person = new Person("Name", 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));

        var thrown = catchThrowable(() -> binding.validate(person));

        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No validation rule was set. Call mustSatisfy(...) or ifPresent(...) before validation");
    }

    @Test
    void validateAndThrow_ShouldNotThrowException_WhenValidationSucceeds() {
        var validPerson = new Person("Name", 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.mustSatisfy(notEmpty());

        assertThatCode(() -> binding.validateAndThrow(validPerson))
                .doesNotThrowAnyException();
    }

    @Test
    void validateAndThrow_ShouldThrowException_WhenValidationFails() {
        var invalidPerson = new Person(null, 25);
        var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        binding.mustSatisfy(notEmpty());

        var thrown = catchThrowable(() -> binding.validateAndThrow(invalidPerson));

        assertThat(thrown)
                .isInstanceOf(InvalidAttributeValueException.class)
                .hasMessage("The field: \"Name\" is invalid: must not be empty");
    }
}