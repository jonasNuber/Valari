package io.github.jonasnuber.valari.spi;

public interface ValidatedBuilder<T, R extends ThrowingResult> extends NoInputValidator<R> {
    T build();
}
