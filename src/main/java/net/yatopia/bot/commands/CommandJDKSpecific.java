package net.yatopia.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.yatopia.bot.EmbedUtil;
import org.jetbrains.annotations.NotNull;

@CommandAliases("jdk14|jdk15|flags|openj9")
@CommandDescription("JDK specific commands")
@CommandUsage("jdk14|?jdk15|?flags|?openj9")
public class CommandJDKSpecific extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    String alias = context.getAlias();
    MessageChannel channel = context.getChannel();
    switch (alias) {
      case "jdk14":
        String description14 =
            "JDK 14 is one of the fastest stable JDKs so it's nice for a production environment"
                + '\n'
                + "Download from here: https://jdk.java.net/14/";
        channel
            .sendMessage(
                EmbedUtil.withAuthor(context.getAuthor()).setDescription(description14).build())
            .queue();
        break;
      case "jdk15":
        String description15 =
            "JDK 15 is the fastest JDK that works with Yatopia, you can download it here: https://jdk.java.net/15/ . Run `?flags` for more information on flags."
                + '\n'
                + "WARNING: JDK 15 is currently in the a pre-release state meaning it may contain issues, use it at your own risk.";
        channel
            .sendMessage(
                EmbedUtil.withAuthor(context.getAuthor()).setDescription(description15).build())
            .queue();
        break;
      case "flags":
        String descriptionFlags =
            "ZGC is the best garbage collector for Yatopia."
                + '\n'
                + "<https://frama.link/Yatopiazgcflags> are the recommended flags to use.";
        channel
            .sendMessage(
                EmbedUtil.withAuthor(context.getAuthor()).setDescription(descriptionFlags).build())
            .queue();
        break;
      case "openj9":
        channel
            .sendMessage(
                EmbedUtil.withAuthor(context.getAuthor())
                    .setDescription(
                        "OpenJ9 doesn't run correctly with Yatopia, please use ZGC and the recommended flags (run `?flags`)")
                    .build())
            .queue();
        break;
    }
    return true;
  }
}
