package net.yatopia.bot.message.filters;

import java.util.Arrays;

public final class StringReader {

  private final char[] chars;
  private int cursor;

  public StringReader(String readString) {
    this.chars = readString.toCharArray();
    this.cursor = 0;
  }

  public boolean canRead() {
    return (cursor + 1) < chars.length;
  }

  public boolean canRead(int length) {
    return canRead() && cursor < (length - 1) && length <= chars.length;
  }

  public char readNextChar() {
    char nextChar = chars[cursor];
    cursor++;
    return nextChar;
  }

  public char[] readNextChars(int length) {
    rangeCheck(length);
    char[] ret = new char[0];
    int i = 0;
    while (i < length) {
      if (ret.length < (i + 1)) {
        ret = Arrays.copyOf(ret, i + 1);
      }
      ret[i] = readNextChar();
      i++;
    }
    return ret;
  }

  private void rangeCheck(int length) {
    if ((cursor + 1) == chars.length) {
      throw new IllegalArgumentException("string already read out");
    }
    if (length > chars.length) {
      throw new IllegalArgumentException("specified length is bigger than the read string length");
    }
  }
}
