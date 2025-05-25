package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.Person;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;

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
}