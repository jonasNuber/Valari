/**
 * The {@code io.github.jonasnuber.valari.api} module provides the public API for the Valari
 * validation framework.
 *
 * <p>This module defines the core interfaces, result types, validation descriptors, i18n
 * abstractions, and exceptions needed to perform validation in a type-safe, flexible, and
 * internationalization-friendly manner. It is designed to be independent of any concrete
 * implementation.
 *
 * <h2>Contents</h2>
 *
 * <ul>
 *   <li>{@link io.github.jonasnuber.valari.api} – Core interfaces and types for validation,
 *       including {@link io.github.jonasnuber.valari.api.Validator}, {@link
 *       io.github.jonasnuber.valari.api.Result}, {@link
 *       io.github.jonasnuber.valari.api.Validation}, and supporting classes.
 *   <li>{@link io.github.jonasnuber.valari.api.exceptions} – Exceptions representing validation
 *       failures, such as {@link
 *       io.github.jonasnuber.valari.api.exceptions.ValidationException} and {@link
 *       io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException}.
 *   <li>{@link io.github.jonasnuber.valari.api.i18n} – Abstractions for internationalization
 *       (i18n), including {@link io.github.jonasnuber.valari.api.i18n.MessageResolver}, {@link
 *       io.github.jonasnuber.valari.api.i18n.ResultFormatter}, and {@link
 *       io.github.jonasnuber.valari.api.i18n.MessageResolutionContext}.
 * </ul>
 *
 * <h2>Purpose</h2>
 *
 * <p>This API module is intended to be used independently of the concrete validation
 * implementation. It defines all contracts required to perform validation, format results, and
 * support internationalized messages. Implementations should reside in a separate module, e.g.,
 * {@code valari.core}, to allow multiple implementations and easy testing.
 *
 * <h2>Usage</h2>
 *
 * <pre>{@code
 * Validator<User, Result<?>> validator = ...
 * ValidationMetadata metadata = new ValidationMetadata.Builder("must not be null")
 *     .labelType(LabelType.FIELD)
 *     .label("username")
 *     .build();
 * }</pre>
 */
module io.github.jonasnuber.valari.api {
  exports io.github.jonasnuber.valari.api;
  exports io.github.jonasnuber.valari.api.exceptions;
  exports io.github.jonasnuber.valari.api.i18n;
}
