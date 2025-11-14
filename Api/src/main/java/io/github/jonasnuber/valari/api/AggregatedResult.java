package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;

import java.util.Collection;

/**
 * Represents a validation result that aggregates multiple individual {@link Result} instances.
 *
 * <p>This allows grouping several results — for example, from validating multiple fields,
 * collection elements, or composite domain objects — into a single combined outcome.
 *
 * <p>An {@code AggregatedResult} is considered invalid if <strong>any</strong> contained result is
 * invalid. Its {@link #throwIfInvalid()} behavior throws a consolidated {@link
 * AggregatedValidationException}, typically containing all relevant error messages.
 *
 * @param <SELF> the concrete subtype, enabling fluent method chaining
 * @author Jonas Nuber
 */
@SuppressWarnings("java:S119")
public interface AggregatedResult<SELF extends AggregatedResult<SELF>>
    extends ThrowableResult<SELF> {

  /**
   * Returns the collection of underlying results that were aggregated to form this result.
   *
   * <p>The returned collection is expected to contain:
   *
   * <ul>
   *   <li>individual field-level {@link Result}s, or
   *   <li>nested {@code AggregatedResult}s
   * </ul>
   *
   * <p>Implementations should guarantee that this collection is never {@code null}.
   *
   * @return the collection of aggregated results
   */
  Collection<? extends Result<?>> getResults();

  /**
   * Returns the number of results contained in this aggregation.
   *
   * <p>This is a convenience method equivalent to:
   *
   * <pre>{@code getResults().size()}</pre>
   *
   * @return the number of aggregated results
   */
  default int size() {
    return getResults().size();
  }

  /**
   * Throws an {@link AggregatedValidationException} if this aggregated result represents any
   * validation failure.
   *
   * <p>This overrides {@link ThrowableResult#throwIfInvalid()} to guarantee that the aggregated-use
   * case always throws the appropriate specialized exception type.
   *
   * @throws AggregatedValidationException if any contained result is invalid
   */
  @Override
  default void throwIfInvalid() throws AggregatedValidationException {
    throwIfInvalid(AggregatedValidationException::new);
  }
}
