package io.github.jonasnuber.valari.core.bindings;

import static io.github.jonasnuber.valari.core.validations.StringValidations.notEmpty;
import static org.assertj.core.api.Assertions.*;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.exceptions.ValidationException;
import io.github.jonasnuber.valari.core.DomainValidator;
import io.github.jonasnuber.valari.core.ValidationResult;
import io.github.jonasnuber.valari.core.i18n.CoreDefaults;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class FieldRuleBindingTest {

  @BeforeAll
  static void init() {
    CoreDefaults.initializeDefaults();
  }

  @Test
  void constructor_ShouldThrowException_ForNullInputs() {
    var nullFieldName = catchThrowable(() -> new FieldRuleBinding<>(null, null, null));
    var nullValueExtractor =
        catchThrowable(() -> new FieldRuleBinding<>("someFieldName", null, null));
    var nullParent =
        catchThrowable(() -> new FieldRuleBinding<>("someFieldName", Person::getName, null));

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
  void mustSatisfy_ShouldSetValidationUsed_ForBinding() {
    var validPerson = new Person("Bob", 25);
    var invalidPerson = new Person(null, 25);
    var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
    binding.mustSatisfy(notEmpty());

    var validResult = binding.validate(validPerson);
    var invalidResult = binding.validate(invalidPerson);

    assertThat(validResult.isValid()).isTrue();
    assertThat(invalidResult.isValid()).isFalse();
  }

  @Test
  void mustSatisfy_ShouldOverridePreviousValidation() {
    var person = new Person("Bob", 25);
    var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
    binding.mustSatisfy(value -> ValidationResult.builder("first rule").fail());
    binding.mustSatisfy(notEmpty()); // overrides previous

    var result = binding.validate(person);

    assertThat(result.isValid()).isTrue();
  }

  @Test
  void ifPresent_ShouldNotThrowException_WhenValidationIsNull() {
    var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));

    assertThatCode(() -> binding.ifPresent(null)).doesNotThrowAnyException();
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
    assertThat(result.getLabel()).isEqualTo("Name");
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
    assertThat(result.getLabel()).isEqualTo("Name");
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
        .hasMessage(
            "No validation rule was set. Call mustSatisfy(...) or ifPresent(...) before validation");
  }

  @Test
  void validateAndThrow_ShouldNotThrowException_WhenValidationSucceeds() {
    var validPerson = new Person("Name", 25);
    var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
    binding.mustSatisfy(notEmpty());

    assertThatCode(() -> binding.validate(validPerson).throwIfInvalid()).doesNotThrowAnyException();
  }

  @Test
  void validateAndThrow_ShouldThrowException_WhenValidationFails() {
    var invalidPerson = new Person(null, 25);
    var binding = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
    binding.mustSatisfy(notEmpty());

    var thrown = catchThrowable(() -> binding.validate(invalidPerson).throwIfInvalid());

    assertThat(thrown)
        .isInstanceOf(ValidationException.class)
        .hasMessageContaining("The Field \"Name\" is invalid: must not be empty");
  }
}
