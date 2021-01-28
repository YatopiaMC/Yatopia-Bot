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

@CommandAliases("jdk|flags|openj9|jvm|graalvm")
@CommandDescription("JDK specific commands")
@CommandUsage("jdk|?flags|?openj9|?jvm|?graalvm")
public class CommandJDKSpecific extends Command {

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    String alias = context.getAlias();
    MessageChannel channel = context.getChannel();
    switch (alias) {
      case "jdk":
        String descriptionJdk =
            "Our advice on JDK is to use the latest LTS (Long Term Support) version, currently JDK 11";
        channel
            .sendMessage(
                EmbedUtil.withAuthor(context.getAuthor()).setDescription(descriptionJdk).build())
            .queue();
        break;
      case "flags":
        String descriptionFlags =
            "We recommend using Aikar's flags since there's no evidence that other flags (with other VMs) work better than Aikar's. Of course, you're free to use and do whatever you want. https://mcflags.emc.gs/";
        channel
            .sendMessage(
                EmbedUtil.withAuthor(context.getAuthor()).setDescription(descriptionFlags).build())
            .queue();
        break;
      case "jvm":
      case "openj9":
      case "graalvm"
              channel
            .sendMessage(
                EmbedUtil.withAuthor(context.getAuthor())
                    .setDescription("We recommend using GraalVM or HotSpot JVM. GraalVM is known to have better performance than HotSpot JVM. Of course, you're free to use and do whatever you want.")
                    .build())
            .queue();
        break;:
        // String descriptionVm =
        //     "Due to the lack of information and benchmarks (evidence) that can prove "
        //         + alias
        //         + " is better than HotSpot, our 2 cents here are to use HotSpot, since its the official JVM, developed by Oracle themselves. By no means this should mean to not use other VMs, you are free to do whatever you want.";
        // channel
        //     .sendMessage(
        //         EmbedUtil.withAuthor(context.getAuthor()).setDescription(descriptionVm).build())
        //     .queue();
        // break;
    }
    return true;
  }
}
