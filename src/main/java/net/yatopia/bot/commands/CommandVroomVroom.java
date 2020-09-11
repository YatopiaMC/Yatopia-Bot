package net.yatopia.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import org.jetbrains.annotations.NotNull;

@CommandAliases("vromvroom|fastervroomvroom|faster")
@CommandDescription("Run it and see what will do.")
@CommandUsage("vromvroom|?fastervroomvroom|?faster")
public class CommandVroomVroom extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    context
        .getChannel()
        .sendMessage(
            ":faster1: :faster2: :faster3: achieved with :yatopia: made by :tr7zw: , :bgidiere: and :ivan:")
        .queue();
    return true;
  }
}
