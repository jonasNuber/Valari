package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;

import java.util.Locale;

@SuppressWarnings("java:S119")
public interface Result<SELF extends Result<SELF>> {

    /**
     * Returns the validation state for this result.
     *
     * @return the current {@link ValidationState}.
     */
    ValidationState getState();

    /**
     * Resolves the validation-specific message (not the full result message).
     *
     * @param resolver the message resolver to use.
     * @param locale the locale for which to resolve the message.
     * @return the resolved validation message.
     */
    String resolveValidationMessage(MessageResolver resolver, Locale locale);

    String getMessage(MessageResolver resolver, Locale locale);

    String getDetailedMessage(MessageResolver resolver, Locale locale);

    /**
     * Resolves the validation-specific message (not the full result message).
     * <p>
     * This uses the global defaults from {@link MessageResolutionContext}.
     * </p>
     *
     * @return the resolved validation message.
     */
    default String resolveValidationMessage() {
        return resolveValidationMessage(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }

    default String getMessage() {
        return getMessage(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }

    default String getDetailedMessage() {
        return getDetailedMessage(MessageResolutionContext.getResolver(), MessageResolutionContext.getLocale());
    }

    SELF withLabel(LabelType labelType, String label);

    /**
     * Returns whether this result represents a valid outcome.
     *
     * @return {@code true} if valid, {@code false} otherwise.
     */
    default boolean isValid() {
        return getState().isValid();
    }

    /**
     * Returns whether this result represents an invalid outcome.
     *
     * @return {@code true} if invalid, {@code false} otherwise.
     */
    default boolean isInvalid() {
        return getState().isInvalid();
    }
}
