package net.yatopia.bot.listeners;

import javax.annotation.Nonnull;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class MessageListener extends ListenerAdapter {

  @Override
  public void onGuildMessageReceived(@Nonnull GuildMessageReceivedEvent event) {
    messageChecks(event.getMessage(), event.getChannel(), event.getAuthor());
  }

  @Override
  public void onGuildMessageUpdate(@Nonnull GuildMessageUpdateEvent event) {
    messageChecks(event.getMessage(), event.getChannel(), event.getAuthor());
  }

  private void messageChecks(Message message, MessageChannel responseChannel, User author) {
    if (author.isBot()) {
      return;
    }
    String contentRaw = message.getContentRaw().toLowerCase();
    String authorMention = author.getAsMention();
    if (contentRaw.contains("yapfa")) {
      responseChannel.sendMessage(authorMention + " , we're now Yatopia.").queue();
    }
    if (contentRaw.contains("minecraft")
        && contentRaw.contains("single")
        && (contentRaw.contains("thread") || contentRaw.contains("threaded"))) {
      responseChannel
          .sendMessage(
              authorMention
                  + " , minecraft is NOT single threaded. There's a DIFFERENCE FROM THE EARTH TO THE MOON between a \"thread\" and a \"core\". Minecraft in MOST OF THE TIMES can take up advantage only of 1 CORE, and that's why cpu clock speed is more important when you're getting a machine for your server.")
          .queue();
    }
    if (contentRaw.contains("flying squids")
        || (contentRaw.contains("flying")
            && (contentRaw.contains("squids") || contentRaw.contains("squid")))) {
      responseChannel
          .sendMessage(
              authorMention
                  + " NO!!!!!! We're only porting patches that make sense from game aspect and performance patches / patches that fix mojira reported bugs. NOTHING ELSE!!!!")
          .queue();
    }
  }
}
