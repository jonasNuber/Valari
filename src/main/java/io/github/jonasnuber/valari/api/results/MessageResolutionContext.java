package io.github.jonasnuber.valari.api.results;

import io.github.jonasnuber.valari.spi.MessageResolver;

import java.util.Locale;
import java.util.Objects;

public final class MessageResolutionContext {
    private static volatile MessageResolver defaultResolver = new RessourceBundleMessageResolver("ValidationMessages");
    private static volatile Locale defaultLocale = Locale.ENGLISH;

    private MessageResolutionContext() throws IllegalAccessException {
        throw new IllegalAccessException("Global Context class cannot be instantiated");
    }

    public static MessageResolver getResolver() {return defaultResolver;}
    public static Locale getLocale(){return defaultLocale;}

    public static void setResolver(MessageResolver resolver) {
        defaultResolver = Objects.requireNonNull(resolver);
    }

    public static void setLocale(Locale locale) {
        defaultLocale = Objects.requireNonNull(locale);
    }
}
