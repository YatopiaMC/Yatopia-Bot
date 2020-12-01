package org.yatopiamc.bot.message.filters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class StringReaderTest {

  @Test
  public void testReadString() {
    StringReader reader = new StringReader("hello world");
    assertTrue(reader.canRead(5));
    assertEquals("hello", new String(reader.readNextChars(5)));
  }
}
