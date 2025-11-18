package io.github.jonasnuber.valari.core.i18n;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;

import java.util.Locale;
import java.util.concurrent.ConcurrentMap;
import org.junit.jupiter.api.Test;

class ResourceBundleCacheTest {

  private static final String BASE_NAME = "TestMessages";

  @Test
  void constructor_ShouldThrowException_ForNullBaseName() {
    var thrown = catchThrowable(() -> new ResourceBundleCache(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Resource baseName must not be null");
  }

  @Test
  void get_ShouldReturnSameInstance_ForCachedBundle() throws Exception {
    var cache = new ResourceBundleCache(BASE_NAME);

    cache.get(Locale.ENGLISH);
    var firstTimestamp = getTimestamp(cache, Locale.ENGLISH);
    cache.get(Locale.ENGLISH);
    var secondTimeStamp = getTimestamp(cache, Locale.ENGLISH);

    assertThat(firstTimestamp).isEqualTo(secondTimeStamp);
  }

  @Test
  void get_ShouldReturnDifferentInstance_AfterTtlExpires() throws Exception {
    var ttlMs = 50L;
    var cache = new ResourceBundleCache(BASE_NAME, true, ttlMs);
    cache.get(Locale.ENGLISH);
    var firstTimestamp = getTimestamp(cache, Locale.ENGLISH);

    Thread.sleep(ttlMs + 20);
    cache.get(Locale.ENGLISH);
    var secondTimeStamp = getTimestamp(cache, Locale.ENGLISH);

    assertThat(secondTimeStamp).isGreaterThan(firstTimestamp);
  }

  @Test
  void get_ShouldReturnSameInstance_ForTtsTurnedOff() throws Exception {
    var cache = new ResourceBundleCache(BASE_NAME, false, 2);
    cache.get(Locale.ENGLISH);
    var firstTimestamp = getTimestamp(cache, Locale.ENGLISH);

    Thread.sleep(50);
    cache.get(Locale.ENGLISH);
    var secondTimeStamp = getTimestamp(cache, Locale.ENGLISH);

    assertThat(secondTimeStamp).isEqualTo(firstTimestamp);
  }

  @Test
  void get_ShouldThrowException_ForNullLocale() {
    var cache = new ResourceBundleCache(BASE_NAME);

    var thrown = catchThrowable(() -> cache.get(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Locale must not be null");
  }

  @Test
  void clear_ShouldClearSpecificCache_ForSpecifiedLocale() throws Exception {
    var cache = new ResourceBundleCache(BASE_NAME);
    cache.get(Locale.ENGLISH);
    var firstEnglishTimestamp = getTimestamp(cache, Locale.ENGLISH);
    cache.get(Locale.GERMAN);
    var firstGermanTimestamp = getTimestamp(cache, Locale.GERMAN);

    cache.clear(Locale.ENGLISH);
    Thread.sleep(1);
    cache.get(Locale.ENGLISH);
    var secondEnglishTimestamp = getTimestamp(cache, Locale.ENGLISH);
    cache.get(Locale.GERMAN);
    var secondGermanTimestamp = getTimestamp(cache, Locale.GERMAN);

    assertThat(firstGermanTimestamp).isEqualTo(secondGermanTimestamp);
    assertThat(secondEnglishTimestamp).isGreaterThan(firstEnglishTimestamp);
  }

  @Test
  void clear_ShouldClearCompleteCache_ForNoSpecifiedLocale() throws Exception {
    var cache = new ResourceBundleCache(BASE_NAME);
    cache.get(Locale.ENGLISH);
    var firstEnglishTimestamp = getTimestamp(cache, Locale.ENGLISH);
    cache.get(Locale.GERMAN);
    var firstGermanTimestamp = getTimestamp(cache, Locale.GERMAN);

    cache.clear();
    Thread.sleep(1);
    cache.get(Locale.ENGLISH);
    var secondEnglishTimestamp = getTimestamp(cache, Locale.ENGLISH);
    cache.get(Locale.GERMAN);
    var secondGermanTimestamp = getTimestamp(cache, Locale.GERMAN);

    assertThat(secondGermanTimestamp).isGreaterThan(firstGermanTimestamp);
    assertThat(secondEnglishTimestamp).isGreaterThan(firstEnglishTimestamp);
  }

  @Test
  void clear_ShouldThrowException_ForNullLocale() {
    var cache = new ResourceBundleCache(BASE_NAME);

    var thrown = catchThrowable(() -> cache.clear(null));

    assertThat(thrown)
        .isInstanceOf(NullPointerException.class)
        .hasMessage("Locale must not be null");
  }

  private long getTimestamp(ResourceBundleCache cache, Locale locale) throws Exception {
    var cacheField = ResourceBundleCache.class.getDeclaredField("cache");
    cacheField.setAccessible(true);

    @SuppressWarnings("unchecked")
    ConcurrentMap<Locale, ?> map = (ConcurrentMap<Locale, ?>) cacheField.get(cache);

    Object wrapper = map.get(locale);

    var tsField = wrapper.getClass().getDeclaredField("timestamp");
    tsField.setAccessible(true);
    return (long) tsField.get(wrapper);
  }
}
