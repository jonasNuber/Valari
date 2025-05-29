package io.github.jonasnuber.valari.internal;

import io.github.jonasnuber.valari.spi.Validation;
import io.github.jonasnuber.valari.api.ValidationResult;

import java.util.function.Predicate;

/**
 * A SimpleValidation validates a field against a predefined condition specified by a Predicate.
 *
 * @author Jonas Nuber
 *
 * @param <K> Type of the field to test.
 */
public class SimpleValidation<K> implements Validation<K> {

	private final Predicate<K> predicate;
	private final String onErrorMessage;

	/**
	 * Constructs a new SimpleValidation object with the specified predicate and error message.
	 *
	 * @param predicate      The predicate to validate against.
	 * @param onErrorMessage The error message indicating why the validation failed.
	 */
	private SimpleValidation(Predicate<K> predicate, String onErrorMessage) {
		this.predicate = predicate;
		this.onErrorMessage = onErrorMessage;
	}

	/**
	 * Creates a new SimpleValidation object with the specified predicate and error message.
	 *
	 * @param <K>            The type of the field to test.
	 * @param predicate      The predicate to validate against.
	 * @param onErrorMessage The error message indicating why the validation failed.
	 * @return The SimpleValidation object.
	 */
	public static <K> SimpleValidation<K> from(Predicate<K> predicate, String onErrorMessage) {
		return new SimpleValidation<>(predicate, onErrorMessage);
	}

	/**
	 * Validates the given object against the predicate.
	 *
	 * @param param The object to be validated.
	 * @return The ValidationResult of the validation.
	 */
	@Override
	public ValidationResult test(K param) {
		return predicate.test(param) ? ValidationResult.ok() : ValidationResult.fail(onErrorMessage);
	}
}
