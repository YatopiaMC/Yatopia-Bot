package org.yatopiamc.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import org.jetbrains.annotations.NotNull;

@CommandAliases("tryitandsee|tias")
@CommandDescription("Whenever someone asks \"whether or not sth works\"")
@CommandUsage("tias")
public class CommandTias extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    context.getChannel().sendMessage("https://tryitands.ee/").queue();
    return true;
  }
}
