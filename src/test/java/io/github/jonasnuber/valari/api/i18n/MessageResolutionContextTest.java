package io.github.jonasnuber.valari.api.i18n;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class MessageResolutionContextTest {

    private final MessageResolver originalResolver = MessageResolutionContext.getResolver();
    private final Locale originalLocale = MessageResolutionContext.getLocale();

    @AfterEach
    void resetContext() {
        MessageResolutionContext.setResolver(originalResolver);
        MessageResolutionContext.setLocale(originalLocale);
    }

    @Test
    void getResolver_ShouldReturnDefault() {
        var resolver = MessageResolutionContext.getResolver();

        assertThat(resolver)
                .isInstanceOf(ResourceBundleMessageResolver.class)
                .isEqualTo(originalResolver);
    }

    @Test
    void getLocale_ShouldReturnDefault() {
        var locale = MessageResolutionContext.getLocale();

        assertThat(locale).isEqualTo(Locale.ENGLISH);
    }

    @Test
    void setResolver_ShouldSetNewResolver(){
        var mockResolver = mock(MessageResolver.class);

        MessageResolutionContext.setResolver(mockResolver);

        assertThat(MessageResolutionContext.getResolver()).isSameAs(mockResolver);
    }

    @Test
    void setLocale_ShouldSetNewLocale() {
        var german = Locale.GERMAN;

        MessageResolutionContext.setLocale(german);

        assertThat(MessageResolutionContext.getLocale()).isEqualTo(german);
    }

    @Test
    void resolve_ShouldDelegateToResolver() {
        var mockResolver = mock(MessageResolver.class);
        MessageResolutionContext.setResolver(mockResolver);
        when(mockResolver.resolve("key", new Object[]{})).thenReturn("resolved!");

        var result = MessageResolutionContext.resolve("key");

        assertThat(result).isEqualTo("resolved!");
        verify(mockResolver).resolve("key", new Object[]{});
    }

    @Test
    void resolve_ShouldResolveArguments() {
        var mockResolver = mock(MessageResolver.class);
        MessageResolutionContext.setResolver(mockResolver);
        when(mockResolver.resolve("key", "arg1", 123)).thenReturn("resolvedWithArgs");

        String result = MessageResolutionContext.resolve("key", "arg1", 123);

        assertThat(result).isEqualTo("resolvedWithArgs");
        verify(mockResolver).resolve("key", "arg1", 123);
    }
}