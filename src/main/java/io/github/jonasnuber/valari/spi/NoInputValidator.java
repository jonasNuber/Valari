package io.github.jonasnuber.valari.spi;

import io.github.jonasnuber.valari.internal.BaseValidator;

public interface NoInputValidator<R> extends BaseValidator {

    R validate();

    void validateAndThrow();
}
