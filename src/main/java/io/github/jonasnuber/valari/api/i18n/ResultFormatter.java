package io.github.jonasnuber.valari.api.i18n;


import io.github.jonasnuber.valari.api.Result;

import java.util.Locale;

@SuppressWarnings("java:S119")
@FunctionalInterface
public interface ResultFormatter<FORMATTED> {
    FORMATTED format(Result<?> result, MessageResolver resolver, Locale locale);

    default FORMATTED format(Result<?> result) {
        return format(result, MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }
}
