package org.yatopiamc.bot.mappings;

public class NoSuchVersionException extends RuntimeException {

  public NoSuchVersionException(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return "No such version: " + getMessage();
  }

  @Override
  public Throwable initCause(Throwable cause) {
    return this;
  }

  @Override
  public Throwable fillInStackTrace() {
    return this;
  }
}
