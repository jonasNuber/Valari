package io.github.jonasnuber.valari.core.i18n.internal;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PrefixStateTest {

  @Test
  void constructor_ShouldAlwaysCreateNewStateWithEmptyList() {
    var prefixState = new PrefixState();

    var lastChildFlags = prefixState.lastChildFlags();

    assertThat(lastChildFlags).isEmpty();
  }

  @Test
  void isLastChild_ShouldAddBooleanAndCreateNewState() {
    var initialState = new PrefixState();
    var isLastChild = false;

    var newState = initialState.isLastChild(isLastChild);

    assertThat(initialState.lastChildFlags()).isEmpty();
    assertThat(newState.lastChildFlags()).containsExactly(isLastChild);
    assertThat(newState).isNotEqualTo(initialState);
  }
}
