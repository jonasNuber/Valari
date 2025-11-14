package io.github.jonasnuber.valari.api;

import io.github.jonasnuber.valari.api.i18n.MessageResolutionContext;
import io.github.jonasnuber.valari.api.i18n.MessageResolver;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class LabelTypeTest {

  @Test
  void predefinedConstants_ShouldHaveCorrectKeys() {
    assertThat(LabelType.FIELD.getKey()).isEqualTo("labeltype.field");
    assertThat(LabelType.PARAMETER.getKey()).isEqualTo("labeltype.parameter");
    assertThat(LabelType.ATTRIBUTE.getKey()).isEqualTo("labeltype.attribute");
    assertThat(LabelType.VALUE.getKey()).isEqualTo("labeltype.value");
    assertThat(LabelType.PROPERTY.getKey()).isEqualTo("labeltype.property");
    assertThat(LabelType.SUBJECT.getKey()).isEqualTo("labeltype.subject");
  }

  @Test
  void defaultLabel_ShouldReturnKey_WithUpperCaseFirstLetter() {
    var newLabel = LabelType.of("test");

    assertThat(newLabel.defaultLabel()).isEqualTo("Test");
  }

  @Test
  void get_ShouldUseDefaultResolver() {
    var resolverMock = mock(MessageResolver.class);
    MessageResolutionContext.setResolver(resolverMock);
    MessageResolutionContext.setLocale(Locale.GERMAN);

    LabelType.SUBJECT.get();

    verify(resolverMock)
        .resolveOrDefault(
            LabelType.SUBJECT.getKey(), Locale.GERMAN, LabelType.SUBJECT.defaultLabel());
  }

  @Test
  void equals_ShouldReturnTrue_ForObjectsWithSameKey() {
    LabelType one = LabelType.of("field");
    LabelType two = LabelType.of("field");

    assertThat(one).isEqualTo(two).hasSameHashCodeAs(two);
  }

  @Test
  void equals_ShouldReturnFalse_ForDifferentKeys() {
    LabelType one = LabelType.of("field");
    LabelType two = LabelType.of("different");

    assertThat(one).isNotEqualTo(two);
  }

  @Test
  void equals_ShouldReturnFalse_WhenComparingWithNullOrOtherClass() {
    LabelType one = LabelType.of("field");

    assertThat(one).isNotEqualTo(null).isNotEqualTo(new Object());
  }
}
