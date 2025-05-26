package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.ValidationResult;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FieldValidationBindingTest {

    @Test
    void valid_ShouldReturnValidResult_WhenFieldIsValid() {
        var person = new Person("Alice", 25);
        var binding = new FieldValidationBinding<>(null, Person::getName, "Name");
        binding.mustSatisfy(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void valid_ShouldReturnInvalidResult_WhenFieldIsInvalid() {
        var person = new Person(null, 25);
        var binding = new FieldValidationBinding<>(null, Person::getName, "Name");
        binding.mustSatisfy(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Name");
        assertThat(result.getCauseDescription()).isEqualTo("must not be blank");
    }

    @Test
    void mustSatisfy_ShouldThrowException_WhenValidationIsNull() {
        var binding = new FieldValidationBinding<>(null, Person::getName, "Name");

        var thrown = catchThrowable(() -> binding.mustSatisfy(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("validation must not be null");
    }

    @Test
    void mustSatisfy_ShouldOverridePreviousValidation() {
        var person = new Person("Bob", 25);
        var binding = new FieldValidationBinding<>(null, Person::getName, "Name");
        binding.mustSatisfy(value -> ValidationResult.fail("first rule"));
        binding.mustSatisfy(notEmpty()); // overrides previous


        var result = binding.validate(person);
        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldThrowException_WhenValidationIsNull() {
        var binding = new FieldValidationBinding<>(null, Person::getName, "Name");

        var thrown = catchThrowable(() -> binding.ifPresent(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("validation must not be null");
    }


    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsNull() {
        var person = new Person(null, 25);
        var binding = new FieldValidationBinding<>(null, Person::getName, "Name");
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsPresentAndValid() {
        var person = new Person("Bob", 25);
        var binding = new FieldValidationBinding<>(null, Person::getName, "Name");
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnInvalid_WhenFieldIsPresentAndInvalid() {
        var person = new Person("", 25);
        var binding = new FieldValidationBinding<>(null, Person::getName, "Name");
        binding.ifPresent(notEmpty());

        var result = binding.validate(person);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Name");
        assertThat(result.getCauseDescription()).isEqualTo("must not be blank");
    }
}