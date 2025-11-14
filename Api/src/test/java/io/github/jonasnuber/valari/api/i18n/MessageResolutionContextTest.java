package io.github.jonasnuber.valari.api.i18n;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.mockito.Mockito.*;

class MessageResolutionContextTest {

  private final MessageResolver resolver = mock(MessageResolver.class);
  private final Locale locale = Locale.ENGLISH;
  private final ResultFormatter<String> formatter = mock(ResultFormatter.class);

  @BeforeEach
  void reset() {
    MessageResolutionContext.resetForTests();
  }

  @Test
  void getResolver_ShouldReturnDefault() {
    MessageResolutionContext.setResolver(resolver);

    var actualResolver = MessageResolutionContext.getResolver();

    assertThat(actualResolver).isEqualTo(resolver);
  }

  @Test
  void getResolver_ShouldThrowException_ForNotSetDefault() {
    var thrown = catchThrowable(MessageResolutionContext::getResolver);

    assertThat(thrown)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage(
            "No default MessageResolver configured. Call MessageResolutionContext.setResolver(...) in your application startup.");
  }

  @Test
  void getLocale_ShouldReturnDefault() {
    MessageResolutionContext.setLocale(locale);

    var actualLocale = MessageResolutionContext.getLocale();

    assertThat(actualLocale).isEqualTo(Locale.ENGLISH);
  }

  @Test
  void getLocale_ShouldThrowException_ForNotSetDefault() {
    var thrown = catchThrowable(MessageResolutionContext::getLocale);

    assertThat(thrown)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage(
            "No default Locale configured. Call MessageResolutionContext.setLocale(...) in your application startup.");
  }

  @Test
  void getFormatter_ShouldReturnDefault() {
    MessageResolutionContext.setFormatter(formatter);

    var actualFormatter = MessageResolutionContext.getFormatter();

    assertThat(actualFormatter).isEqualTo(formatter);
  }

  @Test
  void getFormatter_ShouldThrowException_ForNotSetDefault() {
    var thrown = catchThrowable(MessageResolutionContext::getFormatter);

    assertThat(thrown)
        .isInstanceOf(IllegalStateException.class)
        .hasMessage(
            "No default ResultFormatter configured. Call MessageResolutionContext.setFormatter(...) in your application startup.");
  }

  @Test
  void setResolver_ShouldSetNewResolver() {
    MessageResolutionContext.setResolver(resolver);
    var mockResolver = mock(MessageResolver.class);

    MessageResolutionContext.setResolver(mockResolver);

    assertThat(MessageResolutionContext.getResolver()).isSameAs(mockResolver).isNotSameAs(resolver);
  }

  @Test
  void setResolver_ShouldThrowException_ForNullResolver() {
    var thrown = catchThrowable(() -> MessageResolutionContext.setResolver(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Resolver must not be null");
  }

  @Test
  void setLocale_ShouldSetNewLocale() {
    MessageResolutionContext.setLocale(locale);
    var german = Locale.GERMAN;

    MessageResolutionContext.setLocale(german);

    assertThat(MessageResolutionContext.getLocale()).isEqualTo(german).isNotEqualTo(locale);
  }

  @Test
  void setLocale_ShouldThrowException_ForNullLocale() {
    var thrown = catchThrowable(() -> MessageResolutionContext.setLocale(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Locale must not be null");
  }

  @Test
  void setFormatter_ShouldSetNewFormatter() {
    MessageResolutionContext.setFormatter(formatter);
    var newFormatter = mock(ResultFormatter.class);

    MessageResolutionContext.setFormatter(newFormatter);

    assertThat(MessageResolutionContext.getFormatter())
        .isEqualTo(newFormatter)
        .isNotEqualTo(formatter);
  }

  @Test
  void setFormatter_ShouldThrowException_ForNullFormatter() {
    var thrown = catchThrowable(() -> MessageResolutionContext.setFormatter(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Formatter must not be null");
  }

  @Test
  void resolve_ShouldDelegateToResolver() {
    var mockResolver = mock(MessageResolver.class);
    MessageResolutionContext.setResolver(mockResolver);
    MessageResolutionContext.setLocale(locale);
    when(mockResolver.resolve("key", new Object[] {})).thenReturn("resolved!");

    var result = MessageResolutionContext.resolve("key");

    assertThat(result).isEqualTo("resolved!");
    verify(mockResolver).resolve("key", new Object[] {});
  }

  @Test
  void resolve_ShouldResolveArguments() {
    var mockResolver = mock(MessageResolver.class);
    MessageResolutionContext.setResolver(mockResolver);
    MessageResolutionContext.setLocale(locale);
    when(mockResolver.resolve("key", "arg1", 123)).thenReturn("resolvedWithArgs");

    String result = MessageResolutionContext.resolve("key", "arg1", 123);

    assertThat(result).isEqualTo("resolvedWithArgs");
    verify(mockResolver).resolve("key", "arg1", 123);
  }
}
