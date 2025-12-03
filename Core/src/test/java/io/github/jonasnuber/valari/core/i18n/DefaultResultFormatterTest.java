package io.github.jonasnuber.valari.core.i18n;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class DefaultResultFormatterTest {
  private final MessageResolver resolver = new FakeResolver();
  private final DefaultResultFormatter formatter = new DefaultResultFormatter();
  private final Locale locale = Locale.ENGLISH;

  @Test
  void format_ShouldThrowException_ForNullResult() {
    var thrown = catchThrowable(() -> formatter.format(null, null, null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Result must not be null");
  }

  @Test
  void format_ShouldThrowException_ForNullResolver() {
    var result = new SimpleResult(ValidationState.SUCCESS, meta("must be greater than 3", "Id"));

    var thrown = catchThrowable(() -> formatter.format(result, null, null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Resolver must not be null");
  }

  @Test
  void format_ShouldThrowException_ForNullLocale() {
    var result = new SimpleResult(ValidationState.SUCCESS, meta("must be greater than 3", "Id"));

    var thrown = catchThrowable(() -> formatter.format(result, resolver, null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Locale must not be null");
  }

  @Test
  void format_ShouldReturnCorrectMessage_ForSingleResultSuccess() {
    var result = new SimpleResult(ValidationState.SUCCESS, meta("must be greater than 3", "Id"));

    String out = formatter.format(result, resolver, locale).trim();

    assertThat(out).contains("Field \"Id\" is valid").doesNotContain("must be greater than 3");
  }

  @Test
  void format_ShouldReturnCorrectMessage_ForSingleResultFailure() {
    var result = new SimpleResult(ValidationState.SKIPPED, meta("some message", "City"));

    String out = formatter.format(result, resolver, Locale.ENGLISH);

    assertThat(out.trim()).contains("Field \"City\" is skipped");
  }

  @Test
  void format_ShouldReturnCorrectMessage_ForSingleResultSkipped() {
    var result = new SimpleResult(ValidationState.FAILURE, meta("cannot contain digits", "Name"));

    String out = formatter.format(result, resolver, Locale.ENGLISH);

    assertThat(out.trim()).contains("Field \"Name\" is invalid: cannot contain digits");
  }

  @Test
  void format_ShouldReturnCorrectMessage_ForAggregatedResult() {
    var child1 = new SimpleResult(ValidationState.SUCCESS, meta("ok", "Name"));

    var child2 = new SimpleResult(ValidationState.FAILURE, meta("too young", "Age"));

    var aggregatedResult =
        new SimpleAggResult(
            ValidationState.FAILURE, meta("Person validation", "Person"), List.of(child1, child2));

    String out = formatter.format(aggregatedResult, resolver, Locale.ENGLISH);

    assertThat(out)
        .contains("Person validation")
        .contains("Field \"Age\" is invalid: too young")
        .doesNotContain("Field \"Name\" is valid")
        .doesNotContain("ok");
  }

  @Test
  void format_ShouldReturnCorrectMessage_ForAggregatedResultSkipped() {
    AggregatedResult<?> agg =
        new SimpleAggResult(
            ValidationState.SKIPPED,
            meta("Address validation skipped", "Adress"),
            List.of(new SimpleResult(ValidationState.FAILURE, meta("invalid", "City"))));

    String out = formatter.format(agg, resolver, Locale.ENGLISH);

    assertThat(out.trim()).contains("Address validation skipped");
  }

  @Test
  void format_ShouldReturnCorrectMessage_ForNestedAggregatedResult() {
    Result<?> child1 = new SimpleResult(ValidationState.SUCCESS, meta("valid zip", "Zip"));

    AggregatedResult<?> inner =
        new SimpleAggResult(
            ValidationState.FAILURE,
            meta("Coords invalid", "Coords"),
            List.of(
                new SimpleResult(ValidationState.FAILURE, meta("negative", "X")),
                new SimpleResult(ValidationState.SUCCESS, meta("ok", "Y"))));

    AggregatedResult<?> root =
        new SimpleAggResult(
            ValidationState.FAILURE, meta("Location broken", "Location"), List.of(child1, inner));

    String out = formatter.format(root, resolver, Locale.ENGLISH);

    assertThat(out)
        .contains("Location broken")
        .contains("Coords invalid")
        .contains("Field \"X\" is invalid: negative")
        .doesNotContain("Field \"Zip\" is valid")
        .doesNotContain("valid zip")
        .doesNotContain("Field \"Y\" is valid");
  }

  record SimpleResult(ValidationState state, ValidationMetadata metadata)
      implements Result<SimpleResult> {
    @Override
    public SimpleResult withLabel(LabelType lt, String l) {
      return this;
    }

    @Override
    public ValidationState getState() {
      return state();
    }

    @Override
    public ValidationMetadata getMetadata() {
      return metadata();
    }
  }

  record SimpleAggResult(
      ValidationState state, ValidationMetadata metadata, List<Result<?>> children)
      implements AggregatedResult<SimpleAggResult> {
    @Override
    public SimpleAggResult withLabel(LabelType lt, String l) {
      return this;
    }

    @Override
    public ValidationState getState() {
      return state();
    }

    @Override
    public ValidationMetadata getMetadata() {
      return metadata();
    }

    @Override
    public List<? extends Result<?>> getResults() {
      return children;
    }
  }

  static class FakeResolver implements MessageResolver {
    @Override
    public String resolve(String key, List<Object> args, String defaultMsg, Locale locale) {
      return MessageFormat.format(defaultMsg == null ? "{0} \"{1}\"" : defaultMsg, args.toArray());
    }

    @Override
    public String resolveOrDefault(
        String key, Locale locale, String defaultMessage, Object... args) {
      if ("labeltype.field".equals(key)) {
        return "Field";
      } else if ("validation.result.success".equals(key)) {
        return "is valid";
      } else if ("validation.result.failure".equals(key)) {
        return "is invalid";
      } else if ("validation.result.skipped".equals(key)) {
        return "is skipped";
      }

      return key;
    }
  }

  private ValidationMetadata meta(String msg, String label) {
    return ValidationMetadata.builder(msg).labelType(LabelType.FIELD).label(label).build();
  }
}
