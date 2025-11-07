package io.github.jonasnuber.valari.core.strategies;

import static org.assertj.core.api.Assertions.assertThat;

class FailFastStrategyTest {

//    @Test
//    void validate_ShouldThrowException_ForNullInputs() {
//        var strategy = new FailFastStrategy<Person>();
//
//        var nullValidations = catchThrowable(() -> strategy.validate(null, null));
//        var nullClass = catchThrowable(() -> strategy.validate(
//                List.of(new ParameterRuleBinding<>("Some Parameter", "Value", ConstructorValidator.of(String.class))),
//                null));
//
//        assertThat(nullValidations)
//                .isInstanceOf(NullPointerException.class)
//                .hasMessage("Validations to validate Object by must not be null");
//        assertThat(nullClass)
//                .isInstanceOf(NullPointerException.class)
//                .hasMessage("The class of the Object to validate must not be null");
//    }
//
//    @Test
//    void validate_ShouldStopAtFirstFailure_WhenFieldsFail() {
//        var strategy = new FailFastStrategy<Person>();
//        var nameValidation = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
//        nameValidation.mustSatisfy(notEmpty());
//        var ageValidation = new FieldRuleBinding<>("Age", Person::getAge, DomainValidator.of(Person.class));
//        ageValidation.mustSatisfy(greaterThan(0));
//        var validationBindings = List.of(nameValidation, ageValidation);
//        var invalidPerson = new Person(null, -5);
//
////        var result = strategy.validate(
////                validationBindings
////                        .stream()
////                        .map(binding ->
////                                (NoInputValidator<ValidationResult>) () -> binding.validate(invalidPerson))
////                        .toList(), Person.class
////        );
////
////        assertThat(result.isInvalid()).isTrue();
////        assertThat(result.getResults())
////                .hasSize(1)
////                .extracting(ValidationResult::getLabel)
////                .containsExactly("Name");
//    }
//
//    @Test
//    void validate_ShouldReturnEmptyResult_ForFieldsPassing() {
//        var strategy = new FailFastStrategy<Person>();
//        var nameValidation = new FieldRuleBinding<>("Name", Person::getName, DomainValidator.of(Person.class));
//        nameValidation.mustSatisfy(notEmpty());
//        var validPerson = new Person("Alice", 14);
//
//        var result = strategy.validate(
//                List.of(() -> nameValidation.validate(validPerson)),
//                Person.class
//        );
//
//        assertThat(result.isInvalid()).isFalse();
//        assertThat(result.getResults()).isEmpty();
//    }
}