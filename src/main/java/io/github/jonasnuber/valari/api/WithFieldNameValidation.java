package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.results.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;

public final class WithFieldNameValidation<K> implements Validation<K> {
    private final Validation<K> delegate;
    private final String fieldName;

    public WithFieldNameValidation(Validation<K> delegate, String fieldName) {
        this.delegate = delegate;
        this.fieldName = fieldName;
    }

    @Override
    public ValidationResult test(K param) {
        ValidationResult result = delegate.test(param);
        return result.withFieldName(fieldName);
    }
}
