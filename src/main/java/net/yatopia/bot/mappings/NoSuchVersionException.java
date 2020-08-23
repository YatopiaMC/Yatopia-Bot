package net.yatopia.bot.mappings;

public class NoSuchVersionException extends RuntimeException {

  public NoSuchVersionException(String message) {
    super(message);
  }

  @Override
  public String toString() {
    return "No such version: " + getMessage();
  }
}
