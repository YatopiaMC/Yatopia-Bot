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
    String contentRaw = event.getMessage().getContentRaw().toLowerCase();
    String authorMention = event.getAuthor().getAsMention();
    if (contentRaw.contains("yapfa")) {
      event.getChannel().sendMessage(authorMention + " , we're now Yatopia.").queue();
    }
    if (contentRaw.contains("minecraft")
        && contentRaw.contains("single")
        && (contentRaw.contains("thread") || contentRaw.contains("threaded"))) {
      event
          .getChannel()
          .sendMessage(
              authorMention
                  + " , minecraft is NOT single threaded. There's a DIFFERENCE FROM THE EARTH TO THE MOON between a \"thread\" and a \"core\". Minecraft in MOST OF THE TIMES can take up advantage only of 1 CORE, and that's why cpu clock speed is more important when you're getting a machine for your server.")
          .queue();
    }
  }
}
