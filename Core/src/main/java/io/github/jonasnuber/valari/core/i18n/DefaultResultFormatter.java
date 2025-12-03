package io.github.jonasnuber.valari.core.i18n;

import io.github.jonasnuber.valari.api.AggregatedResult;
import io.github.jonasnuber.valari.api.Result;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.ValidationState;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.api.i18n.ResultFormatter;
import io.github.jonasnuber.valari.core.i18n.internal.PrefixBuilder;
import io.github.jonasnuber.valari.core.i18n.internal.PrefixState;
import java.util.Locale;
import java.util.Objects;

/**
 * Default implementation of {@link ResultFormatter} that renders validation results as a
 * multi-line, tree-structured, human-readable text output.
 *
 * <h2>Overview</h2>
 *
 * <p>This formatter supports both single {@link Result} instances and hierarchical validation
 * structures expressed through {@link AggregatedResult}. Results are rendered recursively using
 * tree prefixes, depending on the configured {@link RenderStyle}.
 *
 * <p>The formatter is highly configurable through {@link RenderConfig}, enabling control over:
 *
 * <ul>
 *   <li>whether successful results are shown
 *   <li>whether label types (e.g., "Field", "Object") are included
 *   <li>whether validation class names are shown (for aggregated nodes only)
 *   <li>whether error counts appear on aggregated nodes
 *   <li>indentation size and symbol style
 * </ul>
 *
 * <h2>Formatting Behavior</h2>
 *
 * <h3>Leaf Results</h3>
 *
 * <p>Leaf nodes (non-aggregated results) are rendered with:
 *
 * <ul>
 *   <li>a success/error symbol from the configured {@link RenderStyle}
 *   <li>a subject composed of label type, label, and optionally the validation class
 *   <li>a localized status phrase (<em>is valid</em>, <em>is invalid</em>, <em>is skipped</em>)
 *   <li>for failures, a localized validation message derived from {@link ValidationMetadata}
 * </ul>
 *
 * <p>Success messages <em>do not</em> include the rule message. Failures always include their rule
 * message.
 *
 * <h3>Aggregated Results</h3>
 *
 * <p>An {@link AggregatedResult} is rendered as:
 *
 * <ul>
 *   <li>a summary line containing symbol, subject, and optionally error count
 *   <li>a recursive rendering of all child results
 * </ul>
 *
 * <p>Aggregated results may show their class name when {@link RenderConfig#showClassNames()} is
 * enabled.
 *
 * <h3>Tree Structure</h3>
 *
 * <p>Tree prefixes (e.g., {@code ├──}, {@code └──}, {@code │}) are produced by {@link
 * PrefixBuilder}. Indentation depth is configurable via {@link RenderConfig#indentSize()}.
 *
 * <h2>Internationalization (i18n)</h2>
 *
 * <p>All subject components, status phrases, and rule messages are resolved through the supplied
 * {@link MessageResolver}. If a message key cannot be resolved, fallbacks from {@link
 * ValidationMetadata} or built-in defaults are used.
 *
 * <h2>Null Handling</h2>
 *
 * <p>The formatter enforces non-null parameters for:
 *
 * <ul>
 *   <li>{@link Result}
 *   <li>{@link MessageResolver}
 *   <li>{@link Locale}
 * </ul>
 *
 * Violations cause a {@link NullPointerException}.
 *
 * <h2>Thread Safety</h2>
 *
 * <p>The formatter is <em>thread-safe</em> provided that the supplied {@link RenderConfig} is
 * immutable (the default). No mutable state is shared across invocations.
 *
 * @see Result
 * @see AggregatedResult
 * @see RenderConfig
 * @see MessageResolver
 * @see ValidationMetadata
 * @author Jonas Nuber
 */
public class DefaultResultFormatter implements ResultFormatter<String> {
  private final RenderConfig config;
  private final PrefixBuilder prefixBuilder;

  /**
   * Creates a new {@code DefaultResultFormatter} using the given {@link RenderConfig}.
   *
   * @param config the rendering configuration, must not be {@code null}
   * @throws NullPointerException if {@code config} is {@code null}
   */
  public DefaultResultFormatter(RenderConfig config) {
    this.config = Objects.requireNonNull(config, "RenderConfig must not be null");
    prefixBuilder = new PrefixBuilder(config);
  }

  /**
   * Creates a new {@code DefaultResultFormatter} using the default {@link RenderConfig}. This is
   * equivalent to:
   *
   * <pre>{@code
   * new DefaultResultFormatter(RenderConfig.builder().build());
   * }</pre>
   *
   * <p>The default configuration uses Unicode symbols, hides successful results, enables label
   * types, and uses an indentation size of 2.
   */
  public DefaultResultFormatter() {
    this(RenderConfig.builder().build());
  }

  /**
   * Formats the given {@link Result} into a human-readable multi-line string using the provided
   * {@link MessageResolver} and {@link Locale}.
   *
   * <p>This is the primary entry point for converting validation results into text. The output
   * consists of:
   *
   * <ul>
   *   <li>a top-level line describing the root result</li>
   *   <li>optionally, a recursively formatted indented tree for all nested results</li>
   * </ul>
   *
   * <p>The formatting behavior (symbols, visibility of successes, indentation, class names,
   * label types, and error counts) is controlled by the associated {@link RenderConfig}.
   *
   * <h2>Message Resolution</h2>
   *
   * <p>The formatter resolves localized messages using the supplied {@link MessageResolver}. It
   * attempts to resolve:
   *
   * <ul>
   *   <li>result status messages: {@code validation.result.success},
   *       {@code validation.result.failure}, etc.</li>
   *   <li>label type names</li>
   *   <li>validation rule messages specified in {@link ValidationMetadata}</li>
   * </ul>
   *
   * <p>If a message key cannot be resolved, the formatter falls back to the default message provided
   * by the metadata or a built-in English phrase.
   *
   * <h2>Null Handling</h2>
   *
   * <ul>
   *   <li>{@code result} must not be {@code null}</li>
   *   <li>{@code resolver} must not be {@code null}</li>
   *   <li>{@code locale} must not be {@code null}</li>
   * </ul>
   *
   * <p>Violating any of these constraints results in a {@link NullPointerException}.
   *
   * <h2>Output Characteristics</h2>
   *
   * <ul>
   *   <li>The returned text always ends with a newline.</li>
   *   <li>Aggregated results produce a multi-line, indented tree.</li>
   *   <li>The formatter never produces {@code null}; an empty tree still returns a valid string.</li>
   * </ul>
   *
   * @param result the validation result to format
   * @param resolver the resolver used for i18n lookups
   * @param locale the locale used for message resolution
   * @return a human-readable, multi-line representation of the result
   * @throws NullPointerException if any argument is {@code null}
   */
  @Override
  public String format(Result<?> result, MessageResolver resolver, Locale locale) {
    Objects.requireNonNull(result, "Result must not be null");
    Objects.requireNonNull(resolver, "Resolver must not be null");
    Objects.requireNonNull(locale, "Locale must not be null");

    StringBuilder sb = new StringBuilder();
    formatNode(result, resolver, locale, sb, new PrefixState(), true);
    return sb.toString();
  }

  private void formatNode(
      Result<?> result,
      MessageResolver resolver,
      Locale locale,
      StringBuilder sb,
      PrefixState prefixState,
      boolean root) {

    if (result instanceof AggregatedResult<?> agg) {
      formatAggregated(agg, resolver, locale, sb, prefixState, root);

      var children = agg.getResults().toArray(Result[]::new);
      for (int i = 0; i < children.length; i++) {
        formatNode(
            children[i],
            resolver,
            locale,
            sb,
            prefixState.isLastChild(i == children.length - 1),
            false);
      }

    } else {
      formatLeaf(result, resolver, locale, sb, prefixState, root);
    }
  }

  private void formatAggregated(
      AggregatedResult<?> result,
      MessageResolver resolver,
      Locale locale,
      StringBuilder sb,
      PrefixState prefixState,
      boolean root) {

    if (!root && !config.showSuccesses() && result.isValid()) {
      return;
    }

    sb.append(prefixBuilder.prefix(prefixState))
        .append(resolveSymbolFor(result))
        .append(" ")
        .append(resolveSubjectFor(result, resolver, locale, true))
        .append(" ")
        .append(resolveMessageFor(result, resolver, locale));

    if (result.isInvalid() && config.showErrorCounts()) {
      sb.append(" ").append(resolveErrorCountFor(result, resolver, locale));
    }

    sb.append(System.lineSeparator());
  }

  private void formatLeaf(
      Result<?> result,
      MessageResolver resolver,
      Locale locale,
      StringBuilder sb,
      PrefixState prefixState,
      boolean root) {

    if (!root && !config.showSuccesses() && result.isValid()) {
      return;
    }

    sb.append(prefixBuilder.prefix(prefixState))
        .append(resolveSymbolFor(result))
        .append(" ")
        .append(resolveSubjectFor(result, resolver, locale, false))
        .append(" ")
        .append(resolveStatusFor(result, resolver, locale));

    if (result.isInvalid()) {
      sb.append(": ").append(resolveMessageFor(result, resolver, locale));
    }

    sb.append(System.lineSeparator());
  }

  private String resolveSubjectFor(
      Result<?> result, MessageResolver resolver, Locale locale, boolean allowClasses) {

    ValidationMetadata metadata = result.getMetadata();
    boolean showTypes = config.showLabelTypes();
    boolean showClasses = allowClasses && config.showClassNames();
    String label = metadata.getLabel();
    String type = metadata.getLabelType().localize(resolver, locale);
    Class<?> clazz = metadata.getValidationClass();

    if (showTypes && showClasses) {
      return resolver.resolve("validation.subject.type_label_class", locale, type, label, clazz);
    }
    if (showTypes) {
      return resolver.resolve("validation.subject.type_label", locale, type, label);
    }
    if (showClasses) {
      return resolver.resolve("validation.subject.label_class", locale, label, clazz);
    }

    return resolver.resolve("validation.subject.label", locale, label);
  }

  private String resolveMessageFor(Result<?> result, MessageResolver resolver, Locale locale) {
    ValidationMetadata meta = result.getMetadata();
    return resolver.resolve(
        meta.getMessageKey(), meta.getMessageArguments(), meta.getDefaultMessage(), locale);
  }

  private String resolveSymbolFor(Result<?> result) {
    return result.isValid() ? config.style().okSymbol() : config.style().errSymbol();
  }

  private String resolveStatusFor(Result<?> result, MessageResolver resolver, Locale locale) {
    return switch (result.getState()) {
      case SUCCESS -> resolver.resolveOrDefault("validation.result.success", locale, "is valid");
      case SKIPPED -> resolver.resolveOrDefault("validation.result.skipped", locale, "is skipped");
      case FAILURE -> resolver.resolveOrDefault("validation.result.failure", locale, "is invalid");
    };
  }

  private String resolveErrorCountFor(
      AggregatedResult<?> result, MessageResolver resolver, Locale locale) {
    long failures =
        result.getResults().stream().filter(r -> r.getState() == ValidationState.FAILURE).count();

    return resolver.resolve("validation.aggregated.errors", locale, failures);
  }
}
