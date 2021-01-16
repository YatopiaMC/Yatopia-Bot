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

@CommandAliases("download|1.15.2|1.16.1|1.16.2|1.16.3|1.16.4|1.16.5")
@CommandDescription("Download specific commands.")
@CommandUsage("download|?1.15.2|?1.16.1|?1.16.2|?1.16.3|?1.16.4|?1.16.5")
public class CommandDownloadSpecific extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    String alias = context.getAlias();
    MessageChannel channel = context.getChannel();
    switch (alias) {
      case "download":
        channel
            .sendMessage(
                EmbedUtil.withAuthor(context.getAuthor())
                    .setDescription(
                        "Run `?1.15.2` or `?1.16.1` or `?1.16.2` or `?1.16.3` or `?1.16.4`")
                    .build())
            .queue();
        break;
      case "1.15.2":
        channel
            .sendMessage(
                "<https://github.com/YatopiaMC/Yatopia/actions?query=branch%3Aver%2F1.15.2>")
            .queue();
        break;
      case "1.16.1":
        channel
            .sendMessage(
                "<https://github.com/YatopiaMC/Yatopia/actions?query=branch%3Aver%2F1.16.1>")
            .queue();
        break;
      case "1.16.2":
        channel
            .sendMessage("<https://ci.codemc.io/job/YatopiaMC/job/Yatopia/job/ver%252F1.16.2/>")
            .queue();
        break;
      case "1.16.3":
        channel
            .sendMessage("<https://ci.codemc.io/job/YatopiaMC/job/Yatopia/job/ver%252F1.16.3/>")
            .queue();
        break;
      case "1.16.4":
        channel
            .sendMessage("<https://ci.codemc.io/job/YatopiaMC/job/Yatopia/job/ver%252F1.16.4/>")
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
