package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.spi.MessageResolver;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RessourceBundleMessageResolver implements MessageResolver {
    private final ConcurrentMap<Locale, ResourceBundle> cache = new ConcurrentHashMap<>();
    private final String baseName;

    public RessourceBundleMessageResolver(String baseName) {
        this.baseName = baseName;
    }

    @Override
    public String resolve(String key, List<Object> args, String defaultMessage, Locale locale) {
       try {
           ResourceBundle bundle = cache.computeIfAbsent(locale, loc -> ResourceBundle.getBundle(baseName, loc));
           String pattern = bundle.getString(key);

           return MessageFormat.format(pattern, sanitizeArguments(args));
       } catch (MissingResourceException e) {
           return defaultMessage != null
                   ? MessageFormat.format(defaultMessage, sanitizeArguments(args))
                   : key;
       }
    }

    private static Object[] sanitizeArguments(List<Object> args) {
        return args != null ? args.toArray() : new Object[0];
    }
}
