package io.github.jonasnuber.valari.api;

/**
 * Marker interface for validator types within the Valari validation framework.
 *
 * <p>This sealed interface serves as a common supertype for all validator variants, enabling
 * framework components to operate on validators in a general and type-safe manner.
 *
 * <p>It is intentionally minimal and does not define behavior; concrete functionality is provided
 * by the permitted subtypes:
 *
 * <ul>
 *   <li>{@code Validator} — validators that operate on an input value, and
 *   <li>{@code NoInputValidator} — validators that do not require an explicit input
 * </ul>
 *
 * <p>Framework code may use {@code GenericValidator} when handling validators abstractly,
 * regardless of whether they consume an input value or validate contextual state.
 *
 * @see Validator
 * @see NoInputValidator
 * @author Jonas Nuber
 */
public sealed interface GenericValidator permits Validator, NoInputValidator {}
