package org.yatopiamc.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.yatopiamc.bot.EmbedUtil;
import org.jetbrains.annotations.NotNull;

@CommandAliases("website|invite")
@CommandDescription("Yatopia specific commands")
@CommandUsage("website|?invite")
public class CommandYatopiaSpecific extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    String alias = context.getAlias();
    MessageChannel channel = context.getChannel();
    if ("website".equalsIgnoreCase(alias)) {
      channel
          .sendMessage(
              EmbedUtil.withAuthor(context.getAuthor())
                  .setDescription("https://yatopiamc.org/")
                  .build())
          .queue();
    } else if ("invite".equalsIgnoreCase(alias)) {
      channel
          .sendMessage(
              EmbedUtil.withAuthor(context.getAuthor())
                  .setDescription("<https://discord.io/YatopiaMC>")
                  .build())
          .queue();
    }
    return true;
  }
}
