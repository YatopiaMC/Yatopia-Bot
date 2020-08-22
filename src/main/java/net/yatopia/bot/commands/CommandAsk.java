package net.yatopia.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import net.dv8tion.jda.api.EmbedBuilder;
import net.yatopia.bot.EmbedUtil;
import org.jetbrains.annotations.NotNull;

@CommandAliases("ask")
@CommandDescription("Whenever someone is asking if he can ask")
@CommandUsage("ask")
public class CommandAsk extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    EmbedBuilder embed = EmbedUtil.withAuthor(context.getAuthor());
    embed.setImage(
        "https://media.discordapp.net/attachments/613163671870242842/674294268646391828/93qXFd0-2.png");
    context.getChannel().sendMessage(embed.build()).queue();
    return true;
  }
}
