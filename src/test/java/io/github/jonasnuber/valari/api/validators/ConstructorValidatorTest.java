package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.internal.bindings.ParameterRuleBinding;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.ObjectValidationHelpers.notNull;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.between;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.*;

class ConstructorValidatorTest {
    private ConstructorValidator<Person> validator;

    @BeforeEach
    void init() {
        validator = ConstructorValidator.of(Person.class)
                .parameter("Name", "Some Name")
                .mustSatisfy(notEmpty())
                .parameter("Age", 13)
                .mustSatisfy(greaterThan(12));
    }

    @Test
    void of_ShouldThrowException_ForNullClass() {
        var thrown = catchThrowable(() -> ConstructorValidator.of(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Class must not be null");
    }

    @Test
    void parameter_ShouldThrowException_ForNullParameterName() {
        var thrown = catchThrowable(() -> validator.parameter(null, null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ParameterName must not be null");
    }

    @Test
    void parameter_ShouldReturnValidParameterRuleBinding_ForValidInput() {
        var parameterName = "ParameterName";
        var value = "Value";

        var parameterRuleBinding = validator.parameter(parameterName, value);

        assertThat(parameterRuleBinding)
                .isInstanceOf(ParameterRuleBinding.class);
    }

    @Test
    void validate_ShouldReturnValid_WhenAllFieldsPass() {
        var result = validator.validate();

        assertThat(result.hasFailures()).isFalse();
        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void validate_ShouldReturnInvalid_WhenFieldsFail() {
        validator.parameter("invalidParam", 2).mustSatisfy(greaterThan(3));

        var result = validator.validate();

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .extracting(ValidationResult::getFieldName)
                .containsExactly("invalidParam");
    }

    @Test
    void validate_ShouldIgnoreNullFields_WhenUsingIfPresent() {
        validator.parameter("optional", null).ifPresent(notNull());

        var result = validator.validate();

        assertThat(result.hasFailures()).isFalse();
        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void validate_ShouldFail_WhenIfPresentFieldIsInvalid() {
        validator.parameter("optionalPresent", "String").ifPresent(between(2, 3));

        var result = validator.validate();

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .extracting(ValidationResult::getFieldName)
                .containsExactly("optionalPresent");
    }

    @Test
    void validateAndThrow_ShouldNotThrow_WhenFieldsPass() {
        assertThatCode(() -> validator.validateAndThrow())
                .doesNotThrowAnyException();
    }

    @Test
    void validateAndThrow_ShouldThrow_WhenFieldsFail() {
        validator.parameter("optionalPresent", "String").ifPresent(between(2, 3));

        var thrown = catchThrowable(() -> validator.validateAndThrow());

        assertThat(thrown)
                .isInstanceOf(AggregatedValidationException.class)
                .hasMessage("Validation for class io.github.jonasnuber.valari.Person failed with 1 error(s):\n" +
                        " - Field 'optionalPresent': must have less than 3 chars\n");
    }

    @Test
    void failFast_ShouldUseFailFastStrategy() {
        validator
                .parameter("invalidParam1", null)
                .mustSatisfy(notNull())
                .parameter("invalidParam2", null)
                .mustSatisfy(notNull());

        var result = validator
                .failFast()
                .validate();

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .extracting(ValidationResult::getFieldName)
                .containsExactly("invalidParam1");
    }

    @Test
    void collectFailures_ShouldUseCollectFailuresStrategy() {
        validator
                .parameter("invalidParam1", null)
                .mustSatisfy(notNull())
                .parameter("invalidParam2", null)
                .mustSatisfy(notNull());

        var result = validator
                .collectFailures()
                .validate();

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .hasSize(2)
                .extracting(ValidationResult::getFieldName)
                .containsExactlyInAnyOrder("invalidParam1", "invalidParam2");
    }

    @Test
    void and_ShouldReturnSameInstance() {
        var expectedValidator = validator;

        var actualValidator = validator.and();

        assertThat(actualValidator).isEqualTo(expectedValidator);
    }

    @Test
    void and_ShouldNotAffectValidation() {
        var andValidator = ConstructorValidator.of(Person.class)
                .parameter("Name", "Some Name")
                .mustSatisfy(notEmpty())
                .and()
                .parameter("Age", 13)
                .mustSatisfy(greaterThan(12));

        var result = andValidator.validate();

        assertThat(result.hasFailures()).isFalse();
        assertThat(result.getResults()).isEmpty();
    }

    @Test
    void shouldAllowStrategySwitching() {
        validator
                .parameter("invalidParam1", null)
                .mustSatisfy(notNull())
                .parameter("invalidParam2", null)
                .mustSatisfy(notNull());

        var result = validator
                .failFast()
                .collectFailures()
                .validate();

        assertThat(result.hasFailures()).isTrue();
        assertThat(result.getResults())
                .hasSize(2)
                .extracting(ValidationResult::getFieldName)
                .containsExactlyInAnyOrder("invalidParam1", "invalidParam2");
    }
}