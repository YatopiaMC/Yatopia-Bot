package org.yatopiamc.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;

@CommandAliases("shitspiller")
@CommandDescription("Whenever someone starts spilling shit in the chat")
@CommandUsage("shitspiller")
public class CommandShitspiller extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    MessageChannel channel = context.getChannel();
    String message =
        "Stop spilling shit! We provide you information that is confirmed to be true "
            + "which works for all the other people. Facts are that we (probably) know more than you, "
            + "so stop spilling shit.";
    channel.sendMessage(message).queue();
    return true;
  }
}
