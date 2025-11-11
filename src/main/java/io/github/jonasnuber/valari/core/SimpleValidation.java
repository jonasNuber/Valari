package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.Validation;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.ValidationState;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A simple {@link Validation} implementation based on a {@link Predicate}.
 * <p>
 * {@code SimpleValidation} allows wrapping a boolean condition into a reusable
 * validation rule, enriched with {@link ValidationMetadata} describing how
 * failures (or successes) should be reported.
 * <p>
 * It is the most straightforward way to create custom validations within
 * the framework.
 *
 * <h2>Usage example</h2>
 * <pre>{@code
 * Validation<String> notEmpty = SimpleValidation.from(
 *     s -> s != null && !s.isEmpty(),
 * 	   new ValidationMetadata.Builder("must not be empty")
 * 				.messageKey("validation.string.notEmpty")
 * 				.build()
 * );
 *
 * ValidationResult result = notEmpty.test("foo"); // SUCCESS
 * ValidationResult fail   = notEmpty.test("");    // FAILURE
 * }</pre>
 *
 * @param <TYPE> the type of value being validated
 *
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public class SimpleValidation<TYPE> implements Validation<TYPE> {

	private final Predicate<TYPE> predicate;
	private final ValidationMetadata metadata;

	private SimpleValidation(Predicate<TYPE> predicate, ValidationMetadata metadata) {
		this.predicate = Objects.requireNonNull(predicate, "Predicate must not be null");
		this.metadata = Objects.requireNonNull(metadata, "ValidationMetadata must not be null");
	}

	/**
	 * Creates a new {@code SimpleValidation} from the given predicate and metadata.
	 *
	 * @param predicate the condition to test values against (must not be {@code null})
	 * @param metadata the validation metadata (messages, labels, etc.) to associate (must not be {@code null})
	 * @param <TYPE> the type of value being validated
	 * @return a new {@code SimpleValidation} instance
	 */
	public static <TYPE> SimpleValidation<TYPE> from(Predicate<TYPE> predicate, ValidationMetadata metadata) {
		return new SimpleValidation<>(predicate, metadata);
	}

	/**
	 * Tests the given input value against the underlying predicate.
	 * <p>
	 * Returns a {@link ValidationResult} indicating success or failure,
	 * enriched with the configured metadata and the tested value.
	 *
	 * @param param the input value to validate
	 * @return a {@link ValidationResult} with {@link ValidationState#SUCCESS}
	 *         if the predicate matches, or {@link ValidationState#FAILURE}
	 *         otherwise
	 */
	@Override
	public ValidationResult test(TYPE param) {
		ValidationResult.Builder resultBuilder = new ValidationResult.Builder(metadata)
				.value(param);

		return predicate.test(param) ? resultBuilder.ok() : resultBuilder.fail();
	}
}
