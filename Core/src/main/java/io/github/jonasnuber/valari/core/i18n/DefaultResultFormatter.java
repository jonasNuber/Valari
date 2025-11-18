package io.github.jonasnuber.valari.core.i18n;

import io.github.jonasnuber.valari.api.AggregatedResult;
import io.github.jonasnuber.valari.api.Result;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.ValidationState;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.api.i18n.ResultFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Default implementation of {@link ResultFormatter} that produces a human-readable, multi-line
 * textual representation of validation results.
 *
 * <h2>Formatting Behavior</h2>
 *
 * <p>This formatter supports both single results and nested hierarchies through {@link
 * AggregatedResult}. Nested results are rendered recursively using indentation to visually
 * represent the tree structure of validation outcomes.
 *
 * <h3>Single Results</h3>
 *
 * <p>A single {@link Result} is formatted using the following components:
 *
 * <ul>
 *   <li>the localized {@link io.github.jonasnuber.valari.api.LabelType} of the value
 *   <li>the label associated with the value (e.g., parameter name)
 *   <li>the resolved validation message derived from {@link ValidationMetadata}
 * </ul>
 *
 * Depending on the {@link ValidationState}, one of the following message keys is used:
 *
 * <ul>
 *   <li>{@code validation.result.success}
 *   <li>{@code validation.result.skipped}
 *   <li>{@code validation.result.failure}
 * </ul>
 *
 * <p>If a key cannot be resolved by the provided {@link MessageResolver}, the formatter falls back
 * to built-in English templates:
 *
 * <pre>
 * "The {0} \"{1}\" is valid: {2}"
 * "Validation for {0} \"{1}\" was skipped"
 * "The {0} \"{1}\" is invalid: {2}"
 * </pre>
 *
 * <h3>Aggregated Results</h3>
 *
 * <p>When formatting an {@link AggregatedResult}, the formatter:
 *
 * <ol>
 *   <li>prints the resolved message of the aggregated node
 *   <li>recursively formats all child results (unless the aggregated result state is {@code
 *       SKIPPED})
 *   <li>indents each level by two spaces to create a tree-like output
 * </ol>
 *
 * Example output:
 *
 * <pre>{@code
 * User
 *   The parameter "name" is invalid: must not be blank
 *   The parameter "email" is valid: must be a valid email
 * }</pre>
 *
 * <h2>Null Handling</h2>
 *
 * <p>This formatter enforces non-null values for:
 *
 * <ul>
 *   <li>{@link Result}
 *   <li>{@link MessageResolver}
 *   <li>{@link Locale}
 * </ul>
 *
 * Violations result in a {@link NullPointerException}.
 *
 * <h2>Thread Safety</h2>
 *
 * <p>This class is stateless and fully thread-safe.
 *
 * @see Result
 * @see AggregatedResult
 * @see MessageResolver
 * @see ValidationMetadata
 * @author Jonas Nuber
 */
public class DefaultResultFormatter implements ResultFormatter<String> {
  private static final String INDENT = "  ";

  /**
   * Formats the given {@link Result} into a human-readable, multi-line string using the provided
   * {@link MessageResolver} and {@link Locale}.
   *
   * <p>This method is the primary entry point for converting validation results into a textual
   * representation. It supports both single results and hierarchical {@link AggregatedResult
   * aggregated results}, producing an indented tree-like output. The formatting logic automatically
   * resolves localized messages using the supplied resolver and falls back to default English
   * templates when a message key cannot be resolved.
   *
   * <h2>Null Handling</h2>
   *
   * <ul>
   *   <li>{@code result} must not be {@code null}
   *   <li>{@code resolver} must not be {@code null}
   *   <li>{@code locale} must not be {@code null}
   * </ul>
   *
   * <p>Violating any of the above constraints results in a {@link NullPointerException}.
   *
   * <h2>Output</h2>
   *
   * <p>The returned string ends with a trailing newline for single results and contains nested
   * newlines for aggregated results. The returned representation is intended for diagnostic or
   * logging purposes.
   *
   * @param result the validation result to format, must not be {@code null}
   * @param resolver the message resolver used to localize output, must not be {@code null}
   * @param locale the locale used for message resolution, must not be {@code null}
   * @return a formatted, human-readable textual representation of the given result
   * @throws NullPointerException if any of the parameters is {@code null}
   */
  @Override
  public String format(Result<?> result, MessageResolver resolver, Locale locale) {
    return format(
        Objects.requireNonNull(result, "Result must not be null"),
        Objects.requireNonNull(resolver, "Resolver must not be null"),
        Objects.requireNonNull(locale, "Locale must not be null"),
        0);
  }

  private String format(Result<?> result, MessageResolver resolver, Locale locale, int depth) {
    StringBuilder sb = new StringBuilder();

    if (result instanceof AggregatedResult<?> aggregated) {
      formatAggregatedResult(sb, aggregated, depth, resolver, locale);
    } else {
      sb.append(INDENT.repeat(depth))
          .append(formatSingleResult(result, resolver, locale))
          .append(System.lineSeparator());
    }

    return sb.toString();
  }

  private void formatAggregatedResult(
      StringBuilder sb,
      AggregatedResult<?> aggregated,
      int depth,
      MessageResolver resolver,
      Locale locale) {
    ValidationMetadata metadata = aggregated.getMetadata();
    ValidationState state = aggregated.getState();

    sb.append(INDENT.repeat(depth))
        .append(metadata.resolveMessage(resolver, locale))
        .append(System.lineSeparator());

    if (state == ValidationState.SKIPPED) return;

    for (Result<?> child : aggregated.getResults()) {
      sb.append(format(child, resolver, locale, depth + 1));
    }
  }

  private String formatSingleResult(Result<?> result, MessageResolver resolver, Locale locale) {
    ValidationState state = result.getState();
    ValidationMetadata metadata = result.getMetadata();

    String labelType = metadata.getLabelType().localize(resolver, locale);
    String label = metadata.getLabel();
    String validationMessage = metadata.resolveMessage(resolver, locale);

    return switch (state) {
      case SUCCESS ->
          resolver.resolve(
              "validation.result.success",
              List.of(labelType, label, validationMessage),
              "The {0} \"{1}\" is valid: {2}",
              locale);
      case SKIPPED ->
          resolver.resolve(
              "validation.result.skipped",
              List.of(labelType, label),
              "Validation for {0} \"{1}\" was skipped",
              locale);
      case FAILURE ->
          resolver.resolve(
              "validation.result.failure",
              List.of(labelType, label, validationMessage),
              "The {0} \"{1}\" is invalid: {2}",
              locale);
    };
  }
}
