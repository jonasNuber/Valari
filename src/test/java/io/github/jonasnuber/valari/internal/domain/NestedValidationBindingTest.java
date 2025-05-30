package io.github.jonasnuber.valari.internal.domain;

import io.github.jonasnuber.valari.CreditCard;
import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.validators.DomainValidator;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class NestedValidationBindingTest {

    @Test
    void constructor_ShouldThrowException_ForNullInputs() {
        var nullParent = catchThrowable(() -> new NestedValidationBinding<>(null, null, null));
        var nullValueExtractor = catchThrowable(() -> new NestedValidationBinding<>(DomainValidator.of(Person.class), null, null));
        var nullFieldName = catchThrowable(() -> new NestedValidationBinding<>(DomainValidator.of(Person.class), Person::getName, null));

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
        var binding = new NestedValidationBinding<>(DomainValidator.of(Person.class), Person::getName, "Name");

        var thrown = catchThrowable(() -> binding.mustSatisfy(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The validator for the nested type must not be null");
    }

    @Test
    void mustSatisfy_ShouldSetValidationUsed_ForBinding(){
        var validCreditCard = new CreditCard("someId", new Person("Bob", 25));
        var invalidCreditCard = new CreditCard("someId", new Person(null, 25));
        var binding = new NestedValidationBinding<>(DomainValidator.of(CreditCard.class), CreditCard::getOwner, "Owner");
        binding.mustSatisfy(DomainValidator.of(Person.class).field(Person::getName, "Name").mustSatisfy(notEmpty()));

        var validResult = binding.validate(validCreditCard);
        var invalidResult = binding.validate(invalidCreditCard);

        assertThat(validResult.isValid()).isTrue();
        assertThat(invalidResult.isInvalid()).isTrue();
    }

    @Test
    void mustSatisfy_ShouldOverridePreviousValidation() {
        var creditCard = new CreditCard("someId", new Person("Bob", 25));
        var binding = new NestedValidationBinding<>(DomainValidator.of(CreditCard.class), CreditCard::getOwner, "Owner");
        binding.mustSatisfy(DomainValidator.of(Person.class).field(Person::getAge, "Age").mustSatisfy(greaterThan(26)));
        binding.mustSatisfy(DomainValidator.of(Person.class).field(Person::getAge, "Age").mustSatisfy(greaterThan(24))); // overrides previous

        var result = binding.validate(creditCard);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldThrowException_WhenValidationIsNull() {
        var binding = new NestedValidationBinding<>(DomainValidator.of(CreditCard.class), CreditCard::getOwner, "Owner");

        var thrown = catchThrowable(() -> binding.ifPresent(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The validator for the composite type must not be null");
    }

    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsNull() {
        var creditCard = new CreditCard("someId", null);

        var binding = new NestedValidationBinding<>(DomainValidator.of(CreditCard.class), CreditCard::getOwner, "Owner");
        binding.ifPresent(DomainValidator.of(Person.class).field(Person::getName, "Name").mustSatisfy(notEmpty()));

        var result = binding.validate(creditCard);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsPresentAndValid() {
        var creditCard = new CreditCard("someId", new Person("Bob", 25));

        var binding = new NestedValidationBinding<>(DomainValidator.of(CreditCard.class), CreditCard::getOwner, "Owner");
        binding.ifPresent(DomainValidator.of(Person.class).field(Person::getName, "Name").mustSatisfy(notEmpty()));

        var result = binding.validate(creditCard);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnInvalid_WhenFieldIsPresentAndInvalid() {
        var creditCard = new CreditCard("someId", new Person(null, 25));

        var binding = new NestedValidationBinding<>(DomainValidator.of(CreditCard.class), CreditCard::getOwner, "Owner");
        binding.ifPresent(DomainValidator.of(Person.class).field(Person::getName, "Name").mustSatisfy(notEmpty()));

        var result = binding.validate(creditCard);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Owner");
        assertThat(result.getCauseDescription()).isEqualTo(
                "Validation for class io.github.jonasnuber.valari.Person failed with 1 error(s):\n" +
                " - Field 'Name': must not be empty\n");
    }

    @Test
    void validate_ShouldReturnValidResult_WhenFieldIsValid() {
        var creditCard = new CreditCard("someId", new Person("Bob", 25));

        var binding = new NestedValidationBinding<>(DomainValidator.of(CreditCard.class), CreditCard::getOwner, "Owner");
        binding.mustSatisfy(DomainValidator.of(Person.class).field(Person::getName, "Name").mustSatisfy(notEmpty()));

        var result = binding.validate(creditCard);

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_ShouldReturnInvalidResult_WhenFieldIsInvalid() {
        var creditCard = new CreditCard("someId", new Person(null, 25));

        var binding = new NestedValidationBinding<>(DomainValidator.of(CreditCard.class), CreditCard::getOwner, "Owner");
        binding.mustSatisfy(DomainValidator.of(Person.class).field(Person::getName, "Name").mustSatisfy(notEmpty()));

        var result = binding.validate(creditCard);

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("Owner");
        assertThat(result.getCauseDescription()).isEqualTo(
                "Validation for class io.github.jonasnuber.valari.Person failed with 1 error(s):\n" +
                        " - Field 'Name': must not be empty\n");
    }
}