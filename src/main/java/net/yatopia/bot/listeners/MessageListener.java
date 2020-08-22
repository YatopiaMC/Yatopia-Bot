package net.yatopia.bot.listeners;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    if (event.getAuthor().isBot()) {
      return;
    }
    if (event.getMessage().getContentRaw().toLowerCase().contains("yapfa")) {
      event
          .getChannel()
          .sendMessage(event.getAuthor().getAsMention() + " , we're now Yatopia.")
          .queue();
    }
  }
}
