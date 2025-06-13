package io.github.jonasnuber.valari.internal.bindings;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.ValidationResult;
import io.github.jonasnuber.valari.api.exceptions.InvalidAttributeValueException;
import io.github.jonasnuber.valari.api.validators.ConstructorValidator;
import org.junit.jupiter.api.Test;

import static io.github.jonasnuber.valari.api.helpers.ObjectValidationHelpers.notNull;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notBlank;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;
import static org.assertj.core.api.Assertions.*;

class ParameterRuleBindingTest {

    @Test
    void constructor_ShouldThrowException_ForNullInputs() {
        var nullParameterName = catchThrowable(() -> new ParameterRuleBinding<>(null, null, null));
        var nullParent = catchThrowable(() -> new ParameterRuleBinding<>("someName", null, null));

        assertThat(nullParameterName)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("ParameterName must not be null");
        assertThat(nullParent)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Constructor Validator must not be null");
    }

    @Test
    void mustSatisfy_ShouldThrowException_WhenValidationIsNull() {
        var binding = new ParameterRuleBinding<>("SomeName", "Value", ConstructorValidator.of(Person.class));

        var thrown = catchThrowable(() -> binding.mustSatisfy(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Validation must not be null");
    }

    @Test
    void mustSatisfy_ShouldSetValidationUsed_ForBinding(){
        var validPersonField = new ParameterRuleBinding<>("SomeName", "Value", ConstructorValidator.of(Person.class));
        var invalidPersonField = new ParameterRuleBinding<>("SomeName", null, ConstructorValidator.of(Person.class));
        validPersonField.mustSatisfy(notEmpty());
        invalidPersonField.mustSatisfy(notNull());

        var validResult = validPersonField.validate();
        var invalidResult = invalidPersonField.validate();

        assertThat(validResult.isValid()).isTrue();
        assertThat(invalidResult.isValid()).isFalse();
    }

    @Test
    void mustSatisfy_ShouldOverridePreviousValidation() {
        var binding = new ParameterRuleBinding<>("SomeName", "Value", ConstructorValidator.of(Person.class));
        binding.mustSatisfy(value -> ValidationResult.fail("first rule"));
        binding.mustSatisfy(notEmpty()); // overrides previous

        var result = binding.validate();

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldThrowException_WhenValidationIsNull() {
        var binding = new ParameterRuleBinding<>("SomeName", "Value", ConstructorValidator.of(Person.class));

        var thrown = catchThrowable(() -> binding.ifPresent(null));

        assertThat(thrown)
                .isInstanceOf(NullPointerException.class)
                .hasMessage("Validation must not be null");
    }

    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsNull() {
        var binding = new ParameterRuleBinding<>("SomeName", null, ConstructorValidator.of(Person.class));
        binding.ifPresent(notNull());

        var result = binding.validate();

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnValid_WhenFieldIsPresentAndValid() {
        var binding = new ParameterRuleBinding<>("SomeName", "value", ConstructorValidator.of(Person.class));
        binding.ifPresent(notEmpty());

        var result = binding.validate();

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void ifPresent_ShouldReturnInvalid_WhenFieldIsPresentAndInvalid() {
        var binding = new ParameterRuleBinding<>("SomeName", "  ", ConstructorValidator.of(Person.class));
        binding.ifPresent(notBlank());

        var result = binding.validate();

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("SomeName");
        assertThat(result.getCauseDescription()).isEqualTo("must not be blank");
    }
    @Test
    void validate_ShouldReturnValidResult_WhenFieldIsValid() {
        var binding = new ParameterRuleBinding<>("SomeName", "value", ConstructorValidator.of(Person.class));
        binding.mustSatisfy(notBlank());

        var result = binding.validate();

        assertThat(result.isValid()).isTrue();
    }

    @Test
    void validate_ShouldReturnInvalidResult_WhenFieldIsInvalid() {
        var binding = new ParameterRuleBinding<>("SomeName", null, ConstructorValidator.of(Person.class));
        binding.mustSatisfy(notNull());

        var result = binding.validate();

        assertThat(result.isInvalid()).isTrue();
        assertThat(result.getFieldName()).isEqualTo("SomeName");
    }

    @Test
    void validate_ShouldThrowException_WhenNoValidationIsSet() {
        var binding = new ParameterRuleBinding<>("SomeName", null, ConstructorValidator.of(Person.class));

        var thrown = catchThrowable(binding::validate);

        assertThat(thrown)
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("No validation rule was set. Call mustSatisfy(...) or ifPresent(...) before validation");
    }

    @Test
    void validateAndThrow_ShouldNotThrowException_WhenValidationSucceeds() {
        var binding = new ParameterRuleBinding<>("SomeName", "value", ConstructorValidator.of(Person.class));
        binding.mustSatisfy(notBlank());

        assertThatCode(binding::validateAndThrow).doesNotThrowAnyException();
    }

    @Test
    void validateAndThrow_ShouldThrowException_WhenValidationFails() {
        var binding = new ParameterRuleBinding<>("SomeName", null, ConstructorValidator.of(Person.class));
        binding.mustSatisfy(notNull());

        var thrown = catchThrowable(binding::validateAndThrow);

        assertThat(thrown)
                .isInstanceOf(InvalidAttributeValueException.class)
                .hasMessage("The field: \"SomeName\" is invalid: must not be null");
    }
}