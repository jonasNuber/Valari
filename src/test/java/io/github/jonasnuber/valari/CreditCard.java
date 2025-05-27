package io.github.jonasnuber.valari;

public class CreditCard {

    private String id;
    private Person owner;

    public CreditCard(String id, Person owner) {
        this.id = id;
        this.owner = owner;
    }

    public String getId() {
        return id;
    }

    public Person getOwner() {
        return owner;
    }
}
