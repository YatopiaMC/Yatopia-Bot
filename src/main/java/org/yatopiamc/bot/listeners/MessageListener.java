package org.yatopiamc.bot.listeners;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    messageChecks(event.getMessage(), event.getChannel());
  }

  @Override
  public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
    messageChecks(event.getMessage(), event.getChannel());
  }

  private void messageChecks(Message message, MessageChannel responseChannel) {
    if (message.getAuthor().isBot()) {
      return;
    }
  }
}
