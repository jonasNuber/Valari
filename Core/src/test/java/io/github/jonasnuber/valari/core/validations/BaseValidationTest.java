package io.github.jonasnuber.valari.core.validations;

import io.github.jonasnuber.valari.api.ValidationMetadata;
import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import io.github.jonasnuber.valari.core.i18n.CoreDefaults;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseValidationTest {
  private static MessageResolver resolver;

  @BeforeAll
  static void initResolver() {
    CoreDefaults.initializeDefaults();
    resolver = MessageResolutionContext.getResolver();
  }

  protected static String getMessage(ValidationMetadata metadata) {
    return resolver.resolve(
        metadata.getMessageKey(),
        metadata.getMessageArguments(),
        metadata.getDefaultMessage(),
        Locale.ENGLISH);
  }
}
