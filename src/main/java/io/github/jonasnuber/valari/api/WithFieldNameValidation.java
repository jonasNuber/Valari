package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.results.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;

@SuppressWarnings("java:S119")
public final class WithFieldNameValidation<TYPE> implements Validation<TYPE> {
    private final Validation<TYPE> delegate;
    private final String fieldName;

    public WithFieldNameValidation(Validation<TYPE> delegate, String fieldName) {
        this.delegate = delegate;
        this.fieldName = fieldName;
    }

    @Override
    public ValidationResult test(TYPE param) {
        ValidationResult result = delegate.test(param);
        return result.withFieldName(fieldName);
    }
}
