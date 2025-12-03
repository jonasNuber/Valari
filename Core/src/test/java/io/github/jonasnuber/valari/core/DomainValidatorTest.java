package io.github.jonasnuber.valari.core;

import static io.github.jonasnuber.valari.core.validations.IntegerValidations.greaterThan;
import static io.github.jonasnuber.valari.core.validations.StringValidations.notEmpty;
import static org.assertj.core.api.Assertions.*;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.core.bindings.FieldRuleBinding;
import io.github.jonasnuber.valari.core.bindings.NestedRuleBinding;
import java.util.function.Function;
import org.assertj.core.api.ThrowableAssert;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class DomainValidatorTest {
  private static DomainValidator<Person> validator;

  @BeforeAll
  static void init() {
    validator =
        DomainValidator.of(Person.class)
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
    var nullExtractor =
        catchThrowable(() -> DomainValidator.of(Person.class).field("someFieldName", null));

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

    assertThat(fieldRuleBinding).isInstanceOf(FieldRuleBinding.class);
  }

  @Test
  void nested_ShouldThrowException_ForNullInput() {
    var nullFieldName =
        catchThrowable(() -> DomainValidator.of(Person.class).nested(null, null, null));
    var nullValidationClass =
        catchThrowable(() -> DomainValidator.of(Person.class).nested("someFieldName", null, null));
    var nullExtractor =
        catchThrowable(
            () -> DomainValidator.of(Person.class).nested("someFieldName", String.class, null));

    assertThat(nullFieldName)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("FieldName must not be null");
    assertThat(nullValidationClass)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Class to validate may not be null");
    assertThat(nullExtractor)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Extractor Function must not be null");
  }

  @Test
  void nested_ShouldReturnNestedValidationBinding_ForValidInput() {
    Function<Person, Integer> extractor = Person::getAge;
    var fieldName = "Age";

    var nestedValidationBinding =
        DomainValidator.of(Person.class).nested(fieldName, Integer.class, extractor);

    assertThat(nestedValidationBinding).isInstanceOf(NestedRuleBinding.class);
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

    assertThat(result.isInvalid()).isFalse();
  }

  @Test
  void validate_ShouldReturnInvalid_WhenFieldFails() {
    var age = -1;

    var result = validator.validate(new Person(null, age));

    assertThat(result.isInvalid()).isTrue();
    assertThat((result.getResults()))
        .extracting(r -> r.getMetadata().getLabel())
        .containsExactlyInAnyOrder("Name", "Age");
  }

  @Test
  void validate_ShouldIgnoreNullFields_WhenUsingIfPresent() {
    var validatorWithOptionalName =
        DomainValidator.of(Person.class)
            .field("Name", Person::getName)
            .ifPresent(notEmpty())
            .field("Age", Person::getAge)
            .mustSatisfy(greaterThan(0));
    var person = new Person(null, 42);

    var result = validatorWithOptionalName.validate(person);

    assertThat(result.isInvalid()).isFalse();
  }

  @Test
  void validate_ShouldFail_WhenIfPresentFieldIsInvalid() {
    var validatorWithOptionalName =
        DomainValidator.of(Person.class)
            .field("Name", Person::getName)
            .ifPresent(notEmpty())
            .field("Age", Person::getAge)
            .mustSatisfy(greaterThan(0));
    var person = new Person("", 42);

    var result = validatorWithOptionalName.validate(person);

    assertThat(result.isInvalid()).isTrue();
  }

  @Test
  void failFast_ShouldUseFailFastStrategy() {
    var age = 0;

    var result = validator.failFast().validate(new Person(null, age));

    assertThat(result.isInvalid()).isTrue();
    assertThat(result.getResults())
        .hasSize(1)
        .extracting(r -> r.getMetadata().getLabel())
        .containsExactly("Name");
  }

  @Test
  void collectFailures_ShouldUseCollectFailuresStrategy() {
    var age = 0;

    var result = validator.collectFailures().validate(new Person(null, age));

    assertThat(result.isInvalid()).isTrue();
    assertThat(result.getResults())
        .hasSize(2)
        .extracting(r -> r.getMetadata().getLabel())
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
    var andValidator =
        DomainValidator.of(Person.class)
            .field("Name", Person::getName)
            .mustSatisfy(notEmpty())
            .and()
            .field("Age", Person::getAge)
            .mustSatisfy(greaterThan(0));
    var age = -1;

    var result = andValidator.validate(new Person(null, age));

    assertThat(result.isInvalid()).isTrue();
    assertThat(result.getResults())
        .extracting(r -> r.getMetadata().getLabel())
        .containsExactlyInAnyOrder("Name", "Age");
  }

  @Test
  void shouldAllowStrategySwitching() {
    var age = 0;

    var result = validator.failFast().collectFailures().validate(new Person(null, age));

    assertThat(result.isInvalid()).isTrue();
    assertThat(result.getResults())
        .hasSize(2)
        .extracting(r -> r.getMetadata().getLabel())
        .containsExactlyInAnyOrder("Name", "Age");
  }
}
