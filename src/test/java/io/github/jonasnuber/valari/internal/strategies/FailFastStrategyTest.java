package io.github.jonasnuber.valari.internal.strategies;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.validators.ConstructorValidator;
import io.github.jonasnuber.valari.api.validators.DomainValidator;
import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.internal.bindings.ParameterRuleBinding;
import io.github.jonasnuber.valari.internal.bindings.FieldRuleBinding;
import io.github.jonasnuber.valari.spi.NoInputValidator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

class FailFastStrategyTest {

    @Test
    void validate_ShouldThrowException_ForNullInputs() {
        var strategy = new FailFastStrategy<Person>();

        var nullValidations = catchThrowable(() -> strategy.validate(null, null));
        var nullClass = catchThrowable(() -> strategy.validate(
                List.of(new ParameterRuleBinding<>("Some Parameter", "Value", ConstructorValidator.of(String.class))),
                null));

        assertThat(nullValidations)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Validations to validate Object by must not be null");
        assertThat(nullClass)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("The class of the Object to validate must not be null");
    }

    @Test
    void validate_ShouldStopAtFirstFailure_WhenFieldsFail() {
        var strategy = new FailFastStrategy<Person>();
        var nameValidation = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        nameValidation.mustSatisfy(notEmpty());
        var ageValidation = new FieldRuleBinding<>("Age", Person::getAge, DomainValidator.of(Person.class));
        ageValidation.mustSatisfy(greaterThan(0));
        var validationBindings = List.of(nameValidation, ageValidation);
        var invalidPerson = new Person(null, -5);

        var result = strategy.validate(
                validationBindings
                        .stream()
                        .map(binding ->
                                (NoInputValidator<ValidationResult>) () -> binding.validate(invalidPerson))
                        .toList(), Person.class
        );

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .hasSize(1)
                .extracting(ValidationResult::getFieldName)
                .containsExactly("Name");
    }

    @Test
    void validate_ShouldReturnEmptyResult_ForFieldsPassing() {
        var strategy = new FailFastStrategy<Person>();
        var nameValidation = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
        nameValidation.mustSatisfy(notEmpty());
        var validPerson = new Person("Alice", 14);

        var result = strategy.validate(
                List.of(() -> nameValidation.validate(validPerson)),
                Person.class
        );

        assertThat(result.hasFailures()).isFalse();
        assertThat(result.getResults()).isEmpty();
    }
}