package net.yatopia.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import org.jetbrains.annotations.NotNull;

@CommandAliases("vroomvroom|fastervroomvroom|faster")
@CommandDescription("Run it and see what will do.")
@CommandUsage("vroomvroom|?fastervroomvroom|?faster")
public class CommandVroomVroom extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    context
        .getChannel()
        .sendMessage(
            "<:faster1:753671545670336558> <:faster2:753671545796427826> <:faster3:753671545615810671> achieved with <:yatopia:745656023871782993> made by <:tr7zw:753678069688172694> , <:bgidiere:753678129968578562> and <:ivan:753671888315613335>")
        .queue();
    return true;
  }
}
