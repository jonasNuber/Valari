package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.results.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;

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
	private final ValidationResult.Builder resultBuilder;

	/**
	 * Constructs a new SimpleValidation object with the specified predicate and error message.
	 *
	 * @param predicate      The predicate to validate against.
	 * @param onErrorMessage The error message indicating why the validation failed.
	 */
	private SimpleValidation(Predicate<K> predicate, ValidationResult.Builder resultBuilder) {
		this.predicate = predicate;
		this.resultBuilder = resultBuilder;
	}

	/**
	 * Creates a new SimpleValidation object with the specified predicate and error message.
	 *
	 * @param <K>            The type of the field to test.
	 * @param predicate      The predicate to validate against.
	 * @param onErrorMessage The error message indicating why the validation failed.
	 * @return The SimpleValidation object.
	 */
	public static <K> SimpleValidation<K> from(Predicate<K> predicate, ValidationResult.Builder resultBuilder) {
		return new SimpleValidation<>(predicate, resultBuilder);
	}

	/**
	 * Validates the given object against the predicate.
	 *
	 * @param param The object to be validated.
	 * @return The ValidationResult of the validation.
	 */
	@Override
	public ValidationResult test(K param) {
		resultBuilder.value(param);

		return predicate.test(param) ? resultBuilder.ok() : resultBuilder.fail();
	}
}
