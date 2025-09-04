package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.results.ValidationMetadata;
import io.github.jonasnuber.valari.api.results.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;

import java.util.function.Predicate;

/**
 * A SimpleValidation validates a value against a predefined condition specified by a Predicate.
 *
 * @author Jonas Nuber
 *
 * @param <TYPE> Type of the value to test.
 */
@SuppressWarnings("java:S119")
public class SimpleValidation<TYPE> implements Validation<TYPE> {

	private final Predicate<TYPE> predicate;
	private final ValidationMetadata metadata;

	/**
	 * Constructs a new SimpleValidation object with the specified predicate and error message.
	 *
	 * @param predicate      The predicate to validate against.
	 * @param onErrorMessage The error message indicating why the validation failed.
	 */
	private SimpleValidation(Predicate<TYPE> predicate, ValidationMetadata metadata) {
		this.predicate = predicate;
		this.metadata = metadata;
	}

	/**
	 * Creates a new SimpleValidation object with the specified predicate and error message.
	 *
	 * @param <TYPE>            The type of the value to test.
	 * @param predicate      The predicate to validate against.
	 * @param onErrorMessage The error message indicating why the validation failed.
	 * @return The SimpleValidation object.
	 */
	public static <TYPE> SimpleValidation<TYPE> from(Predicate<TYPE> predicate, ValidationMetadata metadata) {
		return new SimpleValidation<>(predicate, metadata);
	}

	/**
	 * Validates the given object against the predicate.
	 *
	 * @param param The object to be validated.
	 * @return The ValidationResult of the validation.
	 */
	@Override
	public ValidationResult test(TYPE param) {
		ValidationResult.Builder resultBuilder = new ValidationResult.Builder(metadata)
				.value(param);

		return predicate.test(param) ? resultBuilder.ok() : resultBuilder.fail();
	}
}
