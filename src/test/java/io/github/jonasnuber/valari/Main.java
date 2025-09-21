package io.github.jonasnuber.valari;

import io.github.jonasnuber.valari.api.validators.DomainValidator;

import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.greaterThan;
import static io.github.jonasnuber.valari.api.helpers.IntegerValidationHelpers.lowerThan;
import static io.github.jonasnuber.valari.api.helpers.StringValidationHelpers.*;

public class Main {

    public static void main(String[] args) {
        var validator = DomainValidator.of(CreditCard.class)
                .field("Id", CreditCard::getId)
                    .mustSatisfy(notEmpty().and(moreThan(3)))
                .nested("Owner", Person.class, CreditCard::getOwner)
                    .ifPresent(DomainValidator.of(Person.class)
                            .field("Name", Person::getName)
                            .ifPresent(contains("so"))
                            .field("Age", Person::getAge)
                            .mustSatisfy(greaterThan(18).and(lowerThan(20))))
                .collectFailures();

        var creditCard = new CreditCard("2f2s", new Person("so", 19));

        var result = validator.validate(creditCard);

        System.out.println(result.getMessage());
        System.out.println(result.getDetailedMessage());
    }
}
