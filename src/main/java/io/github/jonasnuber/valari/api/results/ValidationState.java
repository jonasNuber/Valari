package io.github.jonasnuber.valari.api.results;

public enum ValidationState {
    SUCCESS(true),
    SKIPPED(true),
    FAILURE(false);

    private final boolean valid;

    ValidationState(boolean valid) {
        this.valid = valid;
    }

    public boolean isValid() {
        return valid;
    }

    public boolean isInvalid() {
        return !valid;
    }
}
