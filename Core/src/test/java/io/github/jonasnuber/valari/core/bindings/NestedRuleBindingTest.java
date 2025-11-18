package io.github.jonasnuber.valari.core.bindings;

import io.github.jonasnuber.valari.CreditCard;
import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.ValidationDescriptor;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.core.i18n.CoreDefaults;
import io.github.jonasnuber.valari.core.DomainValidator;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.core.validations.IntegerValidations.greaterThan;
import static io.github.jonasnuber.valari.core.validations.StringValidations.notEmpty;
import static org.assertj.core.api.Assertions.*;

class NestedRuleBindingTest {

  @BeforeAll
  static void init() {
    CoreDefaults.initializeDefaults();
  }

  @Test
  void constructor_ShouldThrowException_ForNullInputs() {
    var nullFieldName = catchThrowable(() -> new NestedRuleBinding<>(null, null, null));
    var nullValueExtractor =
        catchThrowable(
            () -> new NestedRuleBinding<>(ValidationDescriptor.builder().build(), null, null));
    var nullParent =
        catchThrowable(
            () ->
                new NestedRuleBinding<>(
                    ValidationDescriptor.builder().build(), Person::getName, null));

    assertThat(nullFieldName)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("ValidationDescriptor must not be null");
    assertThat(nullValueExtractor)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Extractor Method to get value for validation must not be null");
    assertThat(nullParent)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Parent Validator must not be null");
  }

  @Test
  void mustSatisfy_ShouldThrowException_WhenValidationIsNull() {
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().build(),
            Person::getName,
            DomainValidator.of(Person.class));

    var thrown = catchThrowable(() -> binding.mustSatisfy(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("The validator for the nested type must not be null");
  }

  @Test
  void mustSatisfy_ShouldSetValidationUsed_ForBinding() {
    var validCreditCard = new CreditCard("someId", new Person("Bob", 25));
    var invalidCreditCard = new CreditCard("someId", new Person(null, 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.mustSatisfy(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    var validResult = binding.validate(validCreditCard);
    var invalidResult = binding.validate(invalidCreditCard);

    assertThat(validResult.isValid()).isTrue();
    assertThat(invalidResult.isInvalid()).isTrue();
  }

  @Test
  void mustSatisfy_ShouldOverridePreviousValidation() {
    var creditCard = new CreditCard("someId", new Person("Bob", 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.mustSatisfy(
        DomainValidator.of(Person.class).field("Age", Person::getAge).mustSatisfy(greaterThan(26)));
    binding.mustSatisfy(
        DomainValidator.of(Person.class)
            .field("Age", Person::getAge)
            .mustSatisfy(greaterThan(24))); // overrides previous

    var result = binding.validate(creditCard);

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void ifPresent_ShouldThrowException_WhenValidationIsNull() {
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));

    var thrown = catchThrowable(() -> binding.ifPresent(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("The validator for the composite type must not be null");
  }

  @Test
  void ifPresent_ShouldReturnValid_WhenFieldIsNull() {
    var creditCard = new CreditCard("someId", null);
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.ifPresent(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    var result = binding.validate(creditCard);

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void ifPresent_ShouldReturnValid_WhenFieldIsPresentAndValid() {
    var creditCard = new CreditCard("someId", new Person("Bob", 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.ifPresent(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    var result = binding.validate(creditCard);

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void ifPresent_ShouldReturnInvalid_WhenFieldIsPresentAndInvalid() {
    var creditCard = new CreditCard("someId", new Person(null, 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.ifPresent(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    var result = binding.validate(creditCard);

    assertThat(result.isInvalid()).isTrue();
    assertThat(result.getMetadata().getLabel()).isEqualTo("Owner");
    assertThat((String) result.getMessage())
        .contains(
            "Validation for Subject \"Owner\" (class java.lang.Object) failed with 1 error(s):")
        .contains("The Field \"Name\" is invalid: must not be empty");
  }

  @Test
  void validate_ShouldReturnValidResult_WhenFieldIsValid() {
    var creditCard = new CreditCard("someId", new Person("Bob", 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.mustSatisfy(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    var result = binding.validate(creditCard);

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void validate_ShouldReturnInvalidResult_WhenFieldIsInvalid() {
    var creditCard = new CreditCard("someId", new Person(null, 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.mustSatisfy(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    var result = binding.validate(creditCard);

    assertThat(result.isInvalid()).isTrue();
    assertThat(result.getMetadata().getLabel()).isEqualTo("Owner");
    assertThat((String) result.getMessage())
        .contains(
            "Validation for Subject \"Owner\" (class java.lang.Object) failed with 1 error(s):")
        .contains("The Field \"Name\" is invalid: must not be empty");
  }

  @Test
  void validate_ShouldThrowException_WhenToValidateIsNull() {
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.mustSatisfy(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    var thrown = catchThrowable(() -> binding.validate(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Object to validate must not be null");
  }

  @Test
  void validate_ShouldThrowException_WhenCompositeValidatorIsNull() {
    var creditCard = new CreditCard("someId", new Person(null, 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));

    var thrown = catchThrowable(() -> binding.validate(creditCard));

    assertThat(thrown)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage(
            "No validator was set. Call mustSatisfy(...) or ifPresent(...) before validation");
  }

  @Test
  void validateAndThrow_ShouldNotThrowException_WhenValidationSucceeds() {
    var creditCard = new CreditCard("someId", new Person("Bob", 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.mustSatisfy(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    assertThatCode(() -> binding.validate(creditCard).throwIfInvalid()).doesNotThrowAnyException();
  }

  @Test
  void validateAndThrow_ShouldThrowException_WhenValidationFails() {
    var creditCard = new CreditCard("someId", new Person(null, 25));
    var binding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder().label("Owner").build(),
            CreditCard::getOwner,
            DomainValidator.of(CreditCard.class));
    binding.mustSatisfy(
        DomainValidator.of(Person.class).field("Name", Person::getName).mustSatisfy(notEmpty()));

    var thrown = catchThrowable(() -> binding.validate(creditCard).throwIfInvalid());

    assertThat(thrown)
        .isInstanceOf(AggregatedValidationException.class)
        .hasMessageContaining(
            "Validation for Subject \"Owner\" (class java.lang.Object) failed with 1 error(s):")
        .hasMessageContaining("The Field \"Name\" is invalid: must not be empty");
  }
}
