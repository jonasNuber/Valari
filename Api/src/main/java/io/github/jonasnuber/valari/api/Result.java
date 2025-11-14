package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.api.i18n.ResultFormatter;

import java.util.Locale;

/**
 * Represents the outcome of a validation operation, including its state, optional metadata, and the
 * ability to generate localized, formatted messages.
 *
 * <p>This interface forms the core contract for all validation result types within the Valari
 * framework. Concrete implementations typically represent either a successful validation or one
 * containing error information.
 *
 * <h2>Self-typing</h2>
 *
 * <p>The generic parameter {@code SELF} ensures that fluent operations return the correct subtype.
 * For example:
 *
 * <pre>{@code
 * final class FieldResult extends SomeBaseResult<FieldResult> { ... }
 * }</pre>
 *
 * <p>This avoids the need for casts in user code and preserves type safety in method chaining.
 *
 * <h2>Message formatting</h2>
 *
 * <p>A {@link Result} does not contain user-facing text directly. Instead, messages are resolved
 * and formatted on demand through:
 *
 * <ul>
 *   <li>{@link MessageResolver} for resolving message keys
 *   <li>{@link ResultFormatter} for producing structured or textual output
 *   <li>{@link MessageResolutionContext} for global defaults
 * </ul>
 *
 * <p>This makes results lightweight, internationalized, and decoupled from any specific
 * presentation layer.
 *
 * @param <SELF> the concrete subtype, enabling fluent APIs
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public interface Result<SELF extends Result<SELF>> {

  /**
   * Returns a copy of this result with an additional label attached.
   *
   * <p>Labels allow frameworks or applications to annotate results with human-readable identifiers,
   * such as field names, object paths, or UI labels. Implementations define how labels are stored
   * and merged.
   *
   * @param labelType the label category (field name, object name, etc.)
   * @param label the label value (must not be {@code null})
   * @return a modified instance containing the new label
   */
  SELF withLabel(LabelType labelType, String label);

  /**
   * Returns the {@link ValidationState} representing whether this result is valid or invalid.
   *
   * @return the validation state (never {@code null})
   */
  ValidationState getState();

  /**
   * Returns {@link ValidationMetadata} associated with this result, such as message keys, error
   * parameters, or additional contextual information relevant to message resolution or application
   * logic.
   *
   * @return the metadata (never {@code null})
   */
  ValidationMetadata getMetadata();

  /**
   * Formats this result into a value of type {@code R} using the given formatter, resolver, and
   * locale.
   *
   * <p>This is the most explicit and configurable way to generate a human-readable or structured
   * representation of the result.
   *
   * @param formatter the formatter used to structure the result
   * @param resolver the message resolver used to resolve keys
   * @param locale the locale for localized formatting
   * @param <R> the output type
   * @return the formatted output
   */
  default <R> R getMessage(ResultFormatter<R> formatter, MessageResolver resolver, Locale locale) {
    return formatter.format(this, resolver, locale);
  }

  /**
   * Formats this result using the given {@link ResultFormatter} and the resolver and locale
   * supplied by the global {@link MessageResolutionContext}.
   *
   * @param formatter the formatter used to structure the result
   * @param <R> the output type
   * @return the formatted output using globally configured resolver and locale
   */
  default <R> R getMessage(ResultFormatter<R> formatter) {
    return getMessage(
        formatter, MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
  }

  /**
   * Formats this result using the globally configured {@link ResultFormatter}, {@link
   * MessageResolver}, and {@link Locale} from {@link MessageResolutionContext}.
   *
   * <p>This represents the most common way of producing user-facing output.
   *
   * @param <R> the output type
   * @return the formatted result
   */
  default <R> R getMessage() {
    return getMessage(
        MessageResolutionContext.getFormatter(),
        MessageResolutionContext.getResolver(),
        MessageResolutionContext.getLocale());
  }

  /**
   * Convenience method equivalent to {@link #getMessage()}.
   *
   * <p>Suitable for debugging, CLI tools, logs, or tests.
   *
   * @return the formatted, human-readable representation
   */
  default String prettyPrint() {
    return getMessage();
  }

  /**
   * Returns whether this validation result represents a successful outcome.
   *
   * @return {@code true} if valid, {@code false} otherwise
   */
  default boolean isValid() {
    return getState().isValid();
  }

  /**
   * Returns whether this validation result represents a failure.
   *
   * @return {@code true} if invalid, {@code false} otherwise
   */
  default boolean isInvalid() {
    return getState().isInvalid();
  }
}
