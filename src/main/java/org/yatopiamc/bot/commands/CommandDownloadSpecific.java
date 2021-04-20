package org.yatopiamc.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.yatopiamc.bot.EmbedUtil;

@CommandAliases("?download|?1.16.5")
@CommandDescription("Download specific commands.")
@CommandUsage("?download|?1.16.5")
public class CommandDownloadSpecific extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    String alias = context.getAlias();
    MessageChannel channel = context.getChannel();
    switch (alias) {
      case "download":
        channel
          .sendMessage("<https://ci.codemc.io/job/YatopiaMC/job/Yatopia/job/ver%252F1.16.5/>")
          .queue();
        break;
      case "1.16.5":
        channel
            .sendMessage("<https://ci.codemc.io/job/YatopiaMC/job/Yatopia/job/ver%252F1.16.5/>")
            .queue();
        break;
    }
    return true;
  }
}
