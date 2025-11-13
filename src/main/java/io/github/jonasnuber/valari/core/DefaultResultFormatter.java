package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.AggregatedResult;
import io.github.jonasnuber.valari.api.Result;
import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.ValidationState;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.api.i18n.ResultFormatter;

import java.util.List;
import java.util.Locale;

public class DefaultResultFormatter implements ResultFormatter<String> {
  private static final String INDENT = "  ";

  @Override
  public String format(Result<?> result, MessageResolver resolver, Locale locale) {
    return format(result, resolver, locale, 0);
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
        .append(
            resolver.resolve(
                metadata.getMessageKey(),
                metadata.getMessageArguments(),
                metadata.getDefaultMessage(),
                locale))
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
    String validationMessage =
        resolver.resolve(
            metadata.getMessageKey(),
            metadata.getMessageArguments(),
            metadata.getDefaultMessage(),
            locale);

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
