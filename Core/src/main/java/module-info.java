/**
 * The core module of the Valari validation framework.
 *
 * <p>This module provides the standard runtime implementation of the validation APIs defined in
 * {@code io.github.jonasnuber.valari.api}. It contains:
 *
 * <ul>
 *   <li>Default validator implementations and result types
 *   <li>Core validation utilities and helper classes
 *   <li>The framework’s default internationalization (i18n) support
 *   <li>Reusable, ready-to-use validation primitives and compositions
 * </ul>
 *
 * <h2>Module Responsibilities</h2>
 *
 * <p>The {@code valari.core} module is responsible for:
 *
 * <ul>
 *   <li>Executing validations declared through the API module
 *   <li>Constructing {@link io.github.jonasnuber.valari.api.Result Result} and {@link
 *       io.github.jonasnuber.valari.api.ValidationMetadata ValidationMetadata} instances
 *   <li>Aggregating complex validation results
 *   <li>Managing the default message resolution pipeline (resource bundles, locale, formatters)
 *   <li>Providing built-in validation rules in the {@code
 *       io.github.jonasnuber.valari.core.validations} package
 * </ul>
 *
 * <h2>Exported Packages</h2>
 *
 * <dl>
 *   <dt>{@code io.github.jonasnuber.valari.core}
 *   <dd>Core framework classes such as result containers and validators.
 *   <dt>{@code io.github.jonasnuber.valari.core.i18n}
 *   <dd>Internationalization support, including default implementations of message resolvers and
 *       the integration with the resolution context defined in the API module and the * {@link
 *       io.github.jonasnuber.valari.core.i18n.CoreDefaults} bootstrap utility.
 *   <dt>{@code io.github.jonasnuber.valari.core.validations}
 *   <dd>A collection of built-in validation rules that can be used directly or as building blocks
 *       for custom validators.
 * </dl>
 *
 * <p>Applications using the Valari framework typically depend on this module alongside {@code
 * io.github.jonasnuber.valari.api}. The core module does not include any external dependencies and
 * is suitable for use in standalone applications, library environments, and larger modular systems.
 */
module io.github.jonasnuber.valari.core {
  requires transitive io.github.jonasnuber.valari.api;

  exports io.github.jonasnuber.valari.core;
  exports io.github.jonasnuber.valari.core.i18n;
  exports io.github.jonasnuber.valari.core.validations;
}
