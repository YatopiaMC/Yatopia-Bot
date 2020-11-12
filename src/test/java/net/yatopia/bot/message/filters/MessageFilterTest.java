package net.yatopia.bot.message.filters;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import org.junit.jupiter.api.Test;

public class MessageFilterTest {

  @Test
  public void testSimpleWordFilterMessage() {
    assertTrue(DummyMessageFilter.getInstance().applyEffectiveFilter("hello world", "hello"));
  }

  @Test
  public void testSequenceWordFilterMessage() {
    assertTrue(DummyMessageFilter.getInstance().applyEffectiveFilter("hel lo world", "hello"));
  }

  @Test
  public void testCharacterWordFilterMessage() {
    assertTrue(DummyMessageFilter.getInstance().applyEffectiveFilter("h e l l o world", "hello"));
  }

  @Test
  public void testWithDotWordFilterMessage() {
    assertTrue(DummyMessageFilter.getInstance().applyEffectiveFilter("he.llo world", "hello"));
  }

  @Test
  public void testWithOtherCharacter() {
    assertTrue(DummyMessageFilter.getInstance().applyEffectiveFilter("hellosh linux!", "hello"));
  }

  @Test
  public void testDepth1Word() {
    assertTrue(DummyMessageFilter.getInstance().applyEffectiveFilter("linux, h e l l o", "hello"));
  }

  @Test
  public void testMoreDepth1Word() {
    assertTrue(
        DummyMessageFilter.getInstance().applyEffectiveFilter("h e l l o w o r l d", "hello"));
  }

  @Test
  public void testAt2ndPlaceDepth1Word() {
    assertTrue(
        DummyMessageFilter.getInstance().applyEffectiveFilter("w o r l d h e l l o", "hello"));
  }

  @Test
  public void testMultipleWordsFilterMessage1WordInput() {
    assertFalse(
        DummyMessageFilter.getInstance()
            .applyEffectiveFilter("squids", 2, Arrays.asList("flying", "fly", "squids", "squid")));
  }

  @Test
  public void testMultipleWordsFilterMessage2WordsInput() {
    assertFalse(
        DummyMessageFilter.getInstance()
            .applyEffectiveFilter(
                "squids squid", 2, Arrays.asList("flying", "fly", "squids", "squid")));
  }

  @Test
  public void testMultipleWordsFilterMessage2DifferentWordsInput() {
    assertTrue(
        DummyMessageFilter.getInstance()
            .applyEffectiveFilter(
                "flying squids", 2, Arrays.asList("flying", "fly", "squids", "squid")));
  }

  @Test
  public void testMultipleExactWordsFilteringMessage() {
    assertFalse(
        DummyMessageFilter.getInstance()
            .applyEffectiveFilter(
                "squids that squid", 2, Arrays.asList("flying", "fly", "squids", "squid")));
  }

  @Test
  public void testIntendedWork() {
    assertTrue(
        DummyMessageFilter.getInstance()
            .applyEffectiveFilter(
                "squids that fly", 2, Arrays.asList("flying", "fly", "squids", "squid")));
  }

  @Test
  public void testAllSeparatedBySpaces() {
    assertTrue(
        DummyMessageFilter.getInstance()
            .applyEffectiveFilter(
                "f l y i n g s q u i d s", 2, Arrays.asList("flying", "fly", "squids", "squid")));
  }

  // @Test
  //  public void testMultipleWords() {
  //    assertFalse(
  //        DummyMessageFilter.getInstance()
  //            .applyEffectiveFilter(
  //                "squids that go squidding", 2, Arrays.asList("flying", "fly", "squids",
  // "squid")));
  //  }
}
