package org.yatopiamc.bot.listeners;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.yatopiamc.bot.message.filters.FilterContext;
import org.yatopiamc.bot.message.filters.FilterTriggers;
import org.yatopiamc.bot.message.filters.MessageFilterRegistry;

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
    MessageFilterRegistry.getInstance()
        .applyFilters(FilterContext.create(message, responseChannel));
    if (FilterTriggers.hasReachedTriggerThreshold(message.getAuthor().getIdLong())) {
      if (message.getMember() != null) {
        message.getMember().kick().queue();
      }
    }
  }
}
