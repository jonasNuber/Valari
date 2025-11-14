package io.github.jonasnuber.valari.api;

/**
 * A fluent interface representing the binding of a validation rule to a specific input value (such
 * as a field, property, or method reference) within a validator definition.
 *
 * <p>Instances of this interface are typically obtained from a higher-level validator builder,
 * where the user selects a validation target (e.g., a getter method, field extractor, or derived
 * value). Once a target is selected, {@code RuleBinding} allows attaching rules that govern the
 * validity of that value.
 *
 * <h2>Core Semantics</h2>
 *
 * <ul>
 *   <li>{@link #mustSatisfy(Object)} enforces that the value must obey the given rule regardless of
 *       whether it is {@code null} or non-{@code null}, depending on how the rule itself is
 *       defined.
 *   <li>{@link #ifPresent(Object)} applies the rule <em>only</em> when the value is present (i.e.,
 *       non-{@code null}). This is intended for optional fields.
 * </ul>
 *
 * <p>Both methods return the originating validator instance, enabling fluent chaining when
 * constructing complex validation definitions.
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * validator
 *     .field(User::getEmail, "email")
 *     .mustSatisfy(validEmail())
 *
 *     .field(User::getNickname, "nickname")
 *     .ifPresent(nonBlank());
 * }</pre>
 *
 * @param <VALIDATOR> the concrete validator type returned after binding a rule; enables fluent
 *     chaining
 * @param <RULE> the type of validation rule to bind (e.g., {@link Validation})
 * @see Validator
 * @see Validation
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public interface RuleBinding<VALIDATOR extends GenericValidator, RULE> {

  /**
   * Associates the given validation rule with the bound value, enforcing it unconditionally.
   *
   * <p>This method is used for mandatory fields or rules that must always be applied regardless of
   * whether the value is {@code null}. If {@code null} should be allowed, the rule itself must
   * encode that behavior or an alternative rule should be selected.
   *
   * @param rule the validation rule to apply to the bound value
   * @return the underlying validator instance for fluent chaining
   */
  VALIDATOR mustSatisfy(RULE rule);

  /**
   * Associates the given validation rule with the bound value, but only applies it when the value
   * is present (i.e., non-{@code null}).
   *
   * <p>This is intended for optional inputs, where omitting the value entirely is valid, but any
   * provided value must satisfy the specified constraint.
   *
   * @param rule the validation rule to apply if the bound value is non-null
   * @return the underlying validator instance for fluent chaining
   */
  VALIDATOR ifPresent(RULE rule);
}
