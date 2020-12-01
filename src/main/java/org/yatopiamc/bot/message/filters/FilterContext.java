package org.yatopiamc.bot.message.filters;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;

public final class FilterContext {

  public static FilterContext create(Message message, MessageChannel channel) {
    return new FilterContext(message, channel);
  }

  private final Message message;
  private final MessageChannel channel;

  private FilterContext(Message message, MessageChannel channel) {
    this.message = message;
    this.channel = channel;
  }

  public Message getMessage() {
    return message;
  }

  public MessageChannel getChannel() {
    return channel;
  }

  public User getAuthor() {
    return message.getAuthor();
  }

  public void respond(String message) {
    channel.sendMessage(message).queue();
  }
}
