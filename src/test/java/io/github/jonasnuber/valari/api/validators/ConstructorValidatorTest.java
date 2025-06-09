package io.github.jonasnuber.valari.api.validators;

import io.github.jonasnuber.valari.Person;
import org.junit.jupiter.api.BeforeAll;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.notEmpty;

class ConstructorValidatorTest {
    private static ConstructorValidator<Person> validator;

    @BeforeAll
    static void init() {
        validator = ConstructorValidator.of(Person.class)
                .parameter("Name", "Some Name")
                    .mustSatisfy(notEmpty())
                .and()
                .parameter("Age", 13)
                    .mustSatisfy(greaterThan(12));
    }


}