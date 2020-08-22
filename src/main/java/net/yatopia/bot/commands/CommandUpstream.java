package net.yatopia.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import org.jetbrains.annotations.NotNull;

@CommandAliases("upstream")
@CommandDescription("Run it and see what it will do")
@CommandUsage("upstream")
public class CommandUpstream extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    context.getChannel().sendMessage("https://yatopia.net/upstream.gif").queue();
    return true;
  }
}
