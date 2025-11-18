package io.github.jonasnuber.valari.core.strategies;

import static io.github.jonasnuber.valari.core.validations.IntegerValidations.greaterThan;
import static io.github.jonasnuber.valari.core.validations.StringValidations.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.core.ConstructorValidator;
import io.github.jonasnuber.valari.core.i18n.CoreDefaults;
import io.github.jonasnuber.valari.core.DomainValidator;
import io.github.jonasnuber.valari.core.bindings.FieldRuleBinding;
import io.github.jonasnuber.valari.core.bindings.ParameterRuleBinding;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CollectFailuresStrategyTest {

  @BeforeAll
  static void init() {
    CoreDefaults.initializeDefaults();
  }

  @Test
  void validate_ShouldThrowException_ForNullInputs() {
    var strategy = new CollectFailuresStrategy();

    var nullValidations = catchThrowable(() -> strategy.validate(null, null));
    var nullClass =
        catchThrowable(
            () ->
                strategy.validate(
                    List.of(
                        new ParameterRuleBinding<>(
                            "Some Parameter", "Value", ConstructorValidator.of(String.class))),
                    null));

    assertThat(nullValidations)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Validations to validate Object by must not be null");
    assertThat(nullClass)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("The validationDescriptor for the validation must not be null");
  }

  @Test
  void validate_ShouldCollectAllValidationFailures_WhenFieldsFail() {
    var strategy = new CollectFailuresStrategy();
    var nameValidation =
        new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
    nameValidation.mustSatisfy(notEmpty());
    var ageValidation =
        new FieldRuleBinding<>("Age", Person::getAge, DomainValidator.of(Person.class));
    ageValidation.mustSatisfy(greaterThan(0));
    List<Validator<Person, ? extends ThrowableResult<?>>> validationBindings =
        List.of(nameValidation, ageValidation);
    Person invalidPerson = new Person(null, -5);

    var result =
        strategy.validate(
            validationBindings,
            invalidPerson,
            ValidationDescriptor.builder().validationClass(Person.class).build());

    assertThat(result.isInvalid()).isTrue();
    assertThat(result.getResults())
        .hasSize(2)
        .extracting(v -> v.getMetadata().getLabel())
        .containsExactlyInAnyOrder("Name", "Age");
  }

  @Test
  void validate_ShouldReturnAllResults_ForFieldsPassing() {
    var strategy = new CollectFailuresStrategy();
    var nameValidation =
        new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
    nameValidation.mustSatisfy(notEmpty());
    var validPerson = new Person("Alice", 14);

    var result =
        strategy.validate(
            List.of(nameValidation),
            validPerson,
            ValidationDescriptor.builder().validationClass(Person.class).build());

    assertThat(result.isInvalid()).isFalse();
    assertThat(result.getResults()).hasSize(1);
  }
}
