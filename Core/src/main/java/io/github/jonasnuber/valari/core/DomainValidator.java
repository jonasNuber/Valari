package io.github.jonasnuber.valari.core;

import io.github.jonasnuber.valari.api.*;
import io.github.jonasnuber.valari.api.ValidationStrategy;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import io.github.jonasnuber.valari.core.bindings.FieldRuleBinding;
import io.github.jonasnuber.valari.core.bindings.NestedRuleBinding;
import io.github.jonasnuber.valari.core.strategies.CollectFailuresStrategy;
import io.github.jonasnuber.valari.core.strategies.FailFastStrategy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

/**
 * A {@code DomainValidator} is a fluent, type-safe validator for domain model objects.
 *
 * <h2>Overview</h2>
 *
 * <p>This validator allows defining validation rules for individual fields as well as recursively
 * validating nested objects. Each field or nested value is associated with a human-readable logical
 * name, which is included in the validation metadata and contributes to readable, localized error
 * messages.
 *
 * <h2>Field and Nested Validation</h2>
 *
 * <p>Fields are validated by providing an extractor function along with one or more rules. Required
 * fields use {@link RuleBinding#mustSatisfy(Object)}, while optional fields can be validated
 * conditionally via {@link RuleBinding#ifPresent(Object)}.
 *
 * <p>Nested objects are validated through sub-validators, allowing composition and reuse of
 * domain-specific validation logic. Nested validators follow the same fluent DSL.
 *
 * <h2>Validation Strategies</h2>
 *
 * <p>Two strategies are supported:
 *
 * <ul>
 *   <li><b>Fail-fast:</b> Stops at the first validation failure.
 *   <li><b>Collect-all-failures:</b> Evaluates all rules and aggregates their failures. This is the
 *       default.
 * </ul>
 *
 * <h2>Example</h2>
 *
 * <pre>{@code
 * DomainValidator<User> validator = DomainValidator.of(User.class)
 *     .field("name", User::getName)
 *         .mustSatisfy(notBlank())
 *     .field("nickname", User::getNickname)
 *         .ifPresent(notBlank())
 *     .nested("address", Address.class, User::getAddress)
 *         .mustSatisfy(
 *             DomainValidator.of(Address.class)
 *                 .field("street", Address::getStreet).mustSatisfy(notBlank())
 *                 .and()
 *                 .field("zip", Address::getZip).mustSatisfy(validZip())
 *         );
 *
 * ValidationResultCollection results = validator.validate(user);
 * results.throwIfInvalid(); // throws AggregatedValidationException if errors exist
 * }</pre>
 *
 * <h2>Thread Safety</h2>
 *
 * <p>Instances of {@code DomainValidator} are mutable during configuration but become effectively
 * immutable once rules are defined. Validators can be reused safely to validate multiple objects as
 * long as the configured rules remain unchanged.
 *
 * @param <TYPE> the type of the root object being validated
 * @see ValidationResultCollection
 * @see AggregatedValidationException
 * @see RuleBinding
 * @see ValidationDescriptor
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public class DomainValidator<TYPE> implements Validator<TYPE, ValidationResultCollection> {
  private final Class<TYPE> clazz;
  private final List<Validator<TYPE, ? extends ThrowableResult<?>>> validationBindings =
      new ArrayList<>();

  private ValidationStrategy<ValidationResultCollection> validationStrategy =
      new CollectFailuresStrategy();

  private DomainValidator(Class<TYPE> clazz) {
    this.clazz = Objects.requireNonNull(clazz, "Class must not be null");
  }

  /**
   * Creates a new {@code DomainValidator} instance for the specified type.
   *
   * @param clazz the class of the object to validate (must not be {@code null})
   * @param <TYPE> the domain type to validate
   * @return a new {@code DomainValidator} instance
   * @throws NullPointerException if {@code clazz} is {@code null}
   */
  public static <TYPE> DomainValidator<TYPE> of(Class<TYPE> clazz) {
    return new DomainValidator<>(clazz);
  }

  /**
   * Begins validation configuration for a specific field of the domain object.
   *
   * <h2>Usage</h2>
   *
   * <p>A <em>field binding</em> associates:
   *
   * <ul>
   *   <li>a logical field name (used in error messages), and
   *   <li>a field extractor function for retrieving the value from the root object.
   * </ul>
   *
   * You may then attach required rules via {@link RuleBinding#mustSatisfy(Object)} or optional
   * validations via {@link RuleBinding#ifPresent(Object)}.
   *
   * <h3>Example</h3>
   *
   * <pre>{@code
   * DomainValidator<User> validator = DomainValidator.of(User.class)
   *     .field("email", User::getEmail)
   *         .mustSatisfy(validEmail());
   * }</pre>
   *
   * @param fieldName the descriptive name of the field (must not be {@code null})
   * @param extractor a function extracting the field value from the domain object
   * @param <FIELD> the field type
   * @return a rule binding that allows attaching validation rules
   * @throws NullPointerException if {@code fieldName} or {@code extractor} is {@code null}
   */
  public <FIELD> RuleBinding<DomainValidator<TYPE>, Validation<FIELD>> field(
      String fieldName, Function<TYPE, FIELD> extractor) {
    Objects.requireNonNull(fieldName, "FieldName must not be null");
    Objects.requireNonNull(extractor, "Extractor Function must not be null");

    FieldRuleBinding<TYPE, FIELD> fieldValidationBinding =
        new FieldRuleBinding<>(fieldName, extractor, this);
    validationBindings.add(fieldValidationBinding);

    return fieldValidationBinding;
  }

  /**
   * Begins validation configuration for a nested object.
   *
   * <h2>Purpose</h2>
   *
   * <p>Nested validation allows delegating validation of a sub-object to another {@code
   * DomainValidator}, enabling recursive domain validation structures.
   *
   * <h2>Behavior</h2>
   *
   * <ul>
   *   <li>The nested validator's metadata is wrapped into a {@link ValidationDescriptor}.
   *   <li>Depending on the applied binding method:
   *       <ul>
   *         <li>{@code mustSatisfy(...)} → nested object is required
   *         <li>{@code ifPresent(...)} → nested object validated only when non-null
   *       </ul>
   * </ul>
   *
   * <h3>Example</h3>
   *
   * <pre>{@code
   * validator
   *     .nested("address", Address.class, User::getAddress)
   *         .mustSatisfy(
   *             DomainValidator.of(Address.class)
   *                 .field("zip", Address::getZip).mustSatisfy(validZip())
   *         );
   * }</pre>
   *
   * @param label the name of the nested field shown in validation messages
   * @param validationClass the class of the nested type being validated
   * @param extractor extracts the nested object from the root object
   * @param <FIELD> the nested object's type
   * @return a binding used to attach required or optional nested validation rules
   * @throws NullPointerException if any argument is {@code null}
   */
  public <FIELD> RuleBinding<DomainValidator<TYPE>, DomainValidator<FIELD>> nested(
      String label, Class<FIELD> validationClass, Function<TYPE, FIELD> extractor) {
    Objects.requireNonNull(label, "FieldName must not be null");
    Objects.requireNonNull(validationClass, "Class to validate may not be null");
    Objects.requireNonNull(extractor, "Extractor Function must not be null");

    NestedRuleBinding<TYPE, FIELD> nestedValidationBinding =
        new NestedRuleBinding<>(
            ValidationDescriptor.builder()
                .validationClass(validationClass)
                .labelType(LabelType.FIELD)
                .label(label)
                .build(),
            extractor,
            this);

    validationBindings.add(nestedValidationBinding);

    return nestedValidationBinding;
  }

  /**
   * A no-op method used to improve readability in the fluent validation DSL.
   *
   * <p>This method allows visually separating rule chains without affecting behavior. It simply
   * returns this validator instance.
   *
   * <h3>Example</h3>
   *
   * <pre>{@code
   * DomainValidator<User> validator = DomainValidator.of(User.class)
   *     .field("name", User::getName).mustSatisfy(notBlank())
   *     .and()
   *     .field("email", User::getEmail).mustSatisfy(validEmail());
   * }</pre>
   *
   * @return this validator instance
   */
  public DomainValidator<TYPE> and() {
    return this;
  }

  /**
   * Configures this validator to operate in fail-fast mode.
   *
   * <p>In fail-fast mode, validation stops as soon as the first rule fails, and no further fields
   * or nested validators are evaluated.
   *
   * @return this validator instance (for chaining)
   */
  public DomainValidator<TYPE> failFast() {
    validationStrategy = new FailFastStrategy();
    return this;
  }

  /**
   * Configures this validator to collect all failures.
   *
   * <p>This strategy evaluates all field and nested rules and aggregates failures into a single
   * {@link ValidationResultCollection}. This is the default behavior.
   *
   * @return this validator instance (for chaining)
   */
  public DomainValidator<TYPE> collectFailures() {
    validationStrategy = new CollectFailuresStrategy();
    return this;
  }

  /**
   * Validates the given domain object using the configured validation strategy.
   *
   * <h2>Behavior</h2>
   *
   * <ul>
   *   <li>All field bindings and nested validations are evaluated according to the selected
   *       strategy (fail-fast or collect-all).
   *   <li>A {@link ValidationDescriptor} for the root object is created and passed to the strategy
   *       implementation.
   *   <li>The validation result may contain arbitrarily deep nested structures.
   * </ul>
   *
   * @param toValidate the object to validate (must not be {@code null})
   * @return a {@link ValidationResultCollection} representing all validation outcomes
   * @throws NullPointerException if {@code toValidate} is {@code null}
   */
  @Override
  public ValidationResultCollection validate(TYPE toValidate) {
    Objects.requireNonNull(toValidate, "Object to validate must not be null");

    return validationStrategy.validate(
        validationBindings,
        toValidate,
        ValidationDescriptor.builder()
            .validationClass(clazz)
            .labelType(LabelType.FIELD)
            .label(clazz.getSimpleName())
            .build());
  }
}
