package io.github.jonasnuber.valari.core;

import static org.assertj.core.api.Assertions.*;

import io.github.jonasnuber.valari.Person;
import io.github.jonasnuber.valari.api.LabelType;
import io.github.jonasnuber.valari.api.ValidationDescriptor;
import io.github.jonasnuber.valari.api.ValidationState;
import io.github.jonasnuber.valari.api.exceptions.AggregatedValidationException;
import java.util.List;
import org.junit.jupiter.api.Test;

class ValidationResultCollectionTest {

  @Test
  void builder_ShouldCreateResultCollectionWithAllValues_ForBuildInput() {
    var builder =
        ValidationResultCollection.builder(ValidationDescriptor.builder().build())
            .add(ValidationResult.skip());

    var result = builder.build();

    assertThat(result.getResults()).containsExactly(ValidationResult.skip());
    assertThat(result.getState()).isEqualByComparingTo(ValidationState.SKIPPED);
    assertThat(result.getMetadata().getMessageKey()).isEqualTo("validation.aggregated.skipped");
  }

  @Test
  void builder_ShouldThrowException_ForNullDescriptor() {
    var thrown = catchThrowable(() -> ValidationResultCollection.builder(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Descriptor must not be null");
  }

  @Test
  void builder_ShouldThrowException_ForNullResultAdded() {
    var builder = ValidationResultCollection.builder(ValidationDescriptor.builder().build());

    var thrown = catchThrowable(() -> builder.add(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Result cannot be added if null");
  }

  @Test
  void builder_ShouldThrowException_ForNullResultList() {
    var builder = ValidationResultCollection.builder(ValidationDescriptor.builder().build());

    var thrown = catchThrowable(() -> builder.addAll(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Results must not be null");
  }

  @Test
  void builder_ShouldAddSingleResult() {
    var result =
        ValidationResultCollection.builder(ValidationDescriptor.builder().build())
            .add(ValidationResult.skip())
            .build();

    assertThat(result.getResults()).hasSize(1);
  }

  @Test
  void builder_ShouldAddAllResults() {
    var result =
        ValidationResultCollection.builder(ValidationDescriptor.builder().build())
            .addAll(List.of(ValidationResult.skip(), ValidationResult.skip()))
            .build();

    assertThat(result.getResults()).hasSize(2);
  }

  @Test
  void withLabel_ShouldReturnNewInstance_WithUpdatedDescriptor() {
    var result = ValidationResultCollection.builder(ValidationDescriptor.builder().build()).build();

    var changedResult = result.withLabel(LabelType.FIELD, "SomeLabel");

    assertThat(changedResult).isNotEqualTo(result);
    assertThat(changedResult.getMetadata().getLabelType())
        .isNotEqualTo(result.getMetadata().getLabelType())
        .isEqualTo(LabelType.FIELD);
    assertThat(changedResult.getMetadata().getLabel())
        .isNotEqualTo(result.getMetadata().getLabel())
        .isEqualTo("SomeLabel");
  }

  @Test
  void withLabel_ShouldRetainAllOtherParameters() {
    var singleResult = ValidationResult.skip();
    var result =
        ValidationResultCollection.builder(ValidationDescriptor.builder().build())
            .add(singleResult)
            .build();

    var changedResult = result.withLabel(LabelType.FIELD, "SomeLabel");

    assertThat(changedResult.getResults()).containsExactly(singleResult);
    assertThat(changedResult.getState()).isEqualTo(result.getState());
    assertThat(changedResult.getMetadata().getMessageKey())
        .isEqualTo(result.getMetadata().getMessageKey());
  }

  @Test
  void results_ShouldBeImmutable() {
    var col =
        ValidationResultCollection.builder(ValidationDescriptor.builder().build())
            .add(ValidationResult.skip())
            .build();

    var thrown = catchThrowable(() -> col.getResults().add(ValidationResult.skip()));

    assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void getState_ShouldReturnFailure_WhenAnyResultFailed() {
    var collection =
        ValidationResultCollection.builder(ValidationDescriptor.builder().build())
            .add(ValidationResult.builder("msg").fail())
            .add(ValidationResult.builder("msg").ok())
            .add(ValidationResult.builder("msg").skip())
            .build();

    assertThat(collection.getState()).isEqualTo(ValidationState.FAILURE);
  }

  @Test
  void getState_ShouldReturnSkipped_WhenAllResultsSkipped() {
    var collection =
        ValidationResultCollection.builder(ValidationDescriptor.builder().build())
            .add(ValidationResult.builder("msg").skip())
            .add(ValidationResult.builder("msg").skip())
            .build();

    assertThat(collection.getState()).isEqualTo(ValidationState.SKIPPED);
  }

  @Test
  void getState_ShouldReturnSuccess_WhenNoFailuresAndNotAllSkipped() {
    var collection =
        ValidationResultCollection.builder(ValidationDescriptor.builder().build())
            .add(ValidationResult.builder("msg").ok())
            .add(ValidationResult.builder("msg").skip())
            .build();

    assertThat(collection.getState()).isEqualTo(ValidationState.SUCCESS);
  }

  @Test
  void throwIfInvalid_ShouldThrowException_WhenFailuresExist() {
    var collection =
        ValidationResultCollection.builder(
                ValidationDescriptor.builder().validationClass(Person.class).build())
            .add(ValidationResult.builder("fail").fail())
            .build();

    var thrown = catchThrowable(collection::throwIfInvalid);

    assertThat(thrown)
        .isInstanceOf(AggregatedValidationException.class)
        .hasMessageContaining("is invalid");
  }
}
