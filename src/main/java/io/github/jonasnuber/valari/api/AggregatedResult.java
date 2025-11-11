package io.github.jonasnuber.valari.api;

import java.util.Collection;

@SuppressWarnings("java:S119")
public interface AggregatedResult<SELF extends AggregatedResult<SELF>> extends ThrowableResult<SELF> {

    Collection<? extends Result<?>> getResults();

    default int size() {
        return getResults().size();
    }
}
