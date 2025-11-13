package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;

import java.util.Locale;

public class CoreDefaults {
    private CoreDefaults() {}

    public static void initializeDefaults() {
        MessageResolutionContext.setResolver(new ResourceBundleMessageResolver("ValidationMessages"));
        MessageResolutionContext.setLocale(Locale.ENGLISH);
        MessageResolutionContext.setFormatter(new DefaultResultFormatter());
    }
}
