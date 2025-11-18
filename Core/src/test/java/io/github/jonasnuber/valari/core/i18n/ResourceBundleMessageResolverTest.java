package io.github.jonasnuber.valari.core.i18n;

import static org.assertj.core.api.Assertions.*;

import java.util.Locale;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class ResourceBundleMessageResolverTest {
  private static ResourceBundleMessageResolver resolver;

  @BeforeAll
  static void init() {
    resolver = new ResourceBundleMessageResolver("TestMessages");
  }

  @Test
  void constructor_ShouldCreateResolver_ForValidInput() {
    assertThatCode(() -> new ResourceBundleMessageResolver("someBaseFileName"))
        .doesNotThrowAnyException();
  }

  @Test
  void constructor_ShouldThrowException_ForNullBaseName() {
    var thrown = catchThrowable(() -> new ResourceBundleMessageResolver(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Resource baseName must not be null");
  }

  @Test
  void resolve_ShouldReturnCorrectMessage_ForValidInput() {
    var key = "test.message";
    var locale = Locale.ENGLISH;

    var result = resolver.resolve(key, locale);

    assertThat(result).isEqualTo("A Test Message");
  }

  static Stream<Arguments> localeMessagePairs() {
    return Stream.of(
        Arguments.of(Locale.ENGLISH, "A Test Message"),
        Arguments.of(Locale.GERMAN, "Eine Test Nachricht"));
  }

  @ParameterizedTest
  @MethodSource("localeMessagePairs")
  void resolve_ShouldReturnCorrectMessage_ForDifferentLocales(Locale locale, String message) {
    var key = "test.message";

    var result = resolver.resolve(key, locale);

    assertThat(result).isEqualTo(message);
  }

  @Test
  void resolve_ShouldReturnCorrectMessage_ForMessageArguments() {
    var key = "test.message.arguments";
    var firstArgument = "First Argument";
    var secondArgument = "Second Argument";

    var result = resolver.resolve(key, firstArgument, secondArgument);

    assertThat(result)
        .isEqualTo(
            String.format(
                "A Test Message with Argument: \"%s\" and \"%s\"", firstArgument, secondArgument));
  }

  @Test
  void resolve_ShouldReturnMessageArgumentPlaceholder_ForLessArgumentsThanRequired() {
    var key = "test.message.arguments";
    var firstArgument = "First Argument";

    var result = resolver.resolve(key, firstArgument);

    assertThat(result)
        .isEqualTo(
            String.format("A Test Message with Argument: \"%s\" and \"%s\"", firstArgument, "{1}"));
  }

  @Test
  void resolve_ShouldReturnMessageWithFirstArguments_ForMoreArgumentsThanRequired() {
    var key = "test.message.arguments";
    var firstArgument = "First Argument";
    var secondArgument = "Second Argument";
    var thirdArgument = "Third Argument";

    var result = resolver.resolve(key, firstArgument, secondArgument, thirdArgument);

    assertThat(result)
        .isEqualTo(
            String.format(
                "A Test Message with Argument: \"%s\" and \"%s\"", firstArgument, secondArgument));
  }

  @Test
  void resolve_ShouldReturnDefaultMessage_ForUnresolvableKey() {
    var invalidKey = "some.key";
    var defaultMessage = "Some default Message";

    var result = resolver.resolveOrDefault(invalidKey, defaultMessage);

    assertThat(result).isEqualTo(defaultMessage);
  }

  @Test
  void resolve_ShouldReturnDefaultMessageWithArguments_ForUnresolvableKeyAndArguments() {
    var invalidKey = "some.key";
    var firstArgument = "First Argument";
    var secondArgument = "Second Argument";

    var result =
        resolver.resolveOrDefault(
            invalidKey,
            "A Test Message with Argument: \"{0}\" and \"{1}\"",
            firstArgument,
            secondArgument);

    assertThat(result)
        .isEqualTo(
            String.format(
                "A Test Message with Argument: \"%s\" and \"%s\"", firstArgument, secondArgument));
  }

  @Test
  void
      resolve_ShouldReturnDefaultMessageWithArgumentPlaceholders_ForUnresolvableKeyAndArgumentsLessThanRequired() {
    var invalidKey = "some.key";
    var firstArgument = "First Argument";

    var result =
        resolver.resolveOrDefault(
            invalidKey, "A Test Message with Argument: \"{0}\" and \"{1}\"", firstArgument);

    assertThat(result)
        .isEqualTo(
            String.format("A Test Message with Argument: \"%s\" and \"%s\"", firstArgument, "{1}"));
  }

  @Test
  void
      resolve_ShouldReturnDefaultMessageWithFirstArguments_ForUnresolvableKeyAndArgumentsMoreThanRequired() {
    var invalidKey = "some.key";
    var firstArgument = "First Argument";
    var secondArgument = "Second Argument";
    var thirdArgument = "Third Argument";

    var result =
        resolver.resolveOrDefault(
            invalidKey,
            "A Test Message with Argument: \"{0}\" and \"{1}\"",
            firstArgument,
            secondArgument,
            thirdArgument);

    assertThat(result)
        .isEqualTo(
            String.format(
                "A Test Message with Argument: \"%s\" and \"%s\"", firstArgument, secondArgument));
  }

  @Test
  void resolve_ShouldReturnMessageKey_ForUnresolvableKeyAndNoDefaultMessage() {
    var invalidKey = "some.key";

    var result = resolver.resolve(invalidKey);

    assertThat(result).isEqualTo(invalidKey);
  }

  @Test
  void resolve_ShouldThrowException_ForNullKey() {
    var thrown = catchThrowable(() -> resolver.resolve(null, null, null, null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Message key must not be null");
  }

  @Test
  void resolve_ShouldThrowException_ForNullLocale() {
    var thrown = catchThrowable(() -> resolver.resolve("some.key", null, null, null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Locale must not be null");
  }
}
