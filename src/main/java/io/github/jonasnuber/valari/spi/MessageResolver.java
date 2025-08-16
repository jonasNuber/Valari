package io.github.jonasnuber.valari.spi;

import java.util.List;
import java.util.Locale;

public interface MessageResolver {
    String resolve(String key, List<Object> args, String defaultMessage, Locale locale);
}
