package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.results.ValidationResult;
import io.github.jonasnuber.valari.spi.Validation;

import java.util.function.Predicate;

/**
 * A SimpleValidation validates a field against a predefined condition specified by a Predicate.
 *
 * @author Jonas Nuber
 *
 * @param <TYPE> Type of the field to test.
 */
@SuppressWarnings("java:S119")
public class SimpleValidation<TYPE> implements Validation<TYPE> {

	private final Predicate<TYPE> predicate;
	private final ValidationResult.Builder resultBuilder;

	/**
	 * Constructs a new SimpleValidation object with the specified predicate and error message.
	 *
	 * @param predicate      The predicate to validate against.
	 * @param onErrorMessage The error message indicating why the validation failed.
	 */
	private SimpleValidation(Predicate<TYPE> predicate, ValidationResult.Builder resultBuilder) {
		this.predicate = predicate;
		this.resultBuilder = resultBuilder;
	}

	/**
	 * Creates a new SimpleValidation object with the specified predicate and error message.
	 *
	 * @param <TYPE>            The type of the field to test.
	 * @param predicate      The predicate to validate against.
	 * @param onErrorMessage The error message indicating why the validation failed.
	 * @return The SimpleValidation object.
	 */
	public static <TYPE> SimpleValidation<TYPE> from(Predicate<TYPE> predicate, ValidationResult.Builder resultBuilder) {
		return new SimpleValidation<>(predicate, resultBuilder);
	}

	/**
	 * Validates the given object against the predicate.
	 *
	 * @param param The object to be validated.
	 * @return The ValidationResult of the validation.
	 */
	@Override
	public ValidationResult test(TYPE param) {
		resultBuilder.value(param);

		return predicate.test(param) ? resultBuilder.ok() : resultBuilder.fail();
	}
}
