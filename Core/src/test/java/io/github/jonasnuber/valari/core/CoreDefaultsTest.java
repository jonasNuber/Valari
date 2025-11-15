package io.github.jonasnuber.valari.core;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class CoreDefaultsTest {

  @Test
  void initializeDefaults_ShouldSetCorrectDefaultsInMessageResolutionContext() {
    var resolver = new ResourceBundleMessageResolver("ValidationMessages");
    var locale = Locale.ENGLISH;

    CoreDefaults.initializeDefaults();

    assertThat(MessageResolutionContext.getResolver())
        .isInstanceOf(ResourceBundleMessageResolver.class)
        .isEqualTo(resolver);
    assertThat(MessageResolutionContext.getLocale()).isEqualTo(locale);
    assertThat(MessageResolutionContext.getFormatter()).isInstanceOf(DefaultResultFormatter.class);
  }
}
