/**
 * The Valari module provides a functional and extensible validation framework.
 * <p>
 * It includes a core API for defining and composing validations, helper utilities,
 * exception handling, and support for implementing custom validators through SPI.
 * </p>
 *
 * <ul>
 *   <li>{@code api} – Core validations</li>
 *   <li>{@code api.helpers} – Utility classes to simplify validator creation</li>
 *   <li>{@code api.results} - Core validation result types and language resolvers</li>
 *   <li>{@code api.exceptions} – Custom exceptions for validation failures</li>
 *   <li>{@code api.validators} – Built-in validators for common use cases</li>
 *   <li>{@code spi} – Service provider interface for custom validator extensions</li>
 * </ul>
 *
 * @author Jonas Nuber
 * @since 1.0
 */
module io.github.jonasnuber.valari {
    requires java.xml;
    exports io.github.jonasnuber.valari.api;
    exports io.github.jonasnuber.valari.api.exceptions;
    exports io.github.jonasnuber.valari.api.i18n;
    exports io.github.jonasnuber.valari.core;
    exports io.github.jonasnuber.valari.core.validations;
}