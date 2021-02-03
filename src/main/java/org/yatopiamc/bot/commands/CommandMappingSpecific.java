package org.yatopiamc.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.yatopiamc.bot.YatopiaBot;
import org.yatopiamc.bot.mappings.BaseMappingType;
import org.yatopiamc.bot.mappings.Mapping;
import org.yatopiamc.bot.mappings.MappingPaginator;
import org.yatopiamc.bot.mappings.MappingType;
import org.yatopiamc.bot.mappings.NoSuchVersionException;
import org.jetbrains.annotations.NotNull;

@CommandAliases("yc|ym|yf|bc|bm|bf")
@CommandDescription("Mapping specific commands. `y` for yarn, `b` for bukkit/spigot/md_5's")
@CommandUsage("yc|?ym|?yf|?bc|?bm|?bf [mapping] [version] (page)")
public class CommandMappingSpecific extends Command {

  private final YatopiaBot bot;

  public CommandMappingSpecific(YatopiaBot bot) {
    this.bot = bot;
  }

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    String alias = context.getAlias();
    MessageChannel channel = context.getChannel();
    MappingType mappingType;
    BaseMappingType baseType;
    switch (alias) {
      case "yc":
        mappingType = MappingType.CLASS;
        baseType = BaseMappingType.YARN;
        break;
      case "ym":
        mappingType = MappingType.METHOD;
        baseType = BaseMappingType.YARN;
        break;
      case "yf":
        mappingType = MappingType.FIELD;
        baseType = BaseMappingType.YARN;
        break;
      case "bc":
        mappingType = MappingType.CLASS;
        baseType = BaseMappingType.SPIGOT;
        break;
      case "bm":
        mappingType = MappingType.METHOD;
        baseType = BaseMappingType.SPIGOT;
        break;
      case "bf":
        mappingType = MappingType.FIELD;
        baseType = BaseMappingType.SPIGOT;
        break;
      default:
        throw new IllegalArgumentException("Wat did just happen? CommandMappingSpecific");
    }
    args.nextString()
        .ifPresent(
            mapping ->
                args.nextString()
                    .ifPresent(
                        version ->
                            args.nextInt()
                                .ifPresent(
                                    page ->
                                        handle(
                                            channel, baseType, mappingType, version, mapping, page))
                                .orElse(
                                    failReason ->
                                        handle(
                                            channel, baseType, mappingType, version, mapping, 1)))
                    .orElse(
                        failReason -> {
                          if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                            channel.sendMessage("Invalid usage!").queue();
                          }
                        }))
        .orElse(
            failReason -> {
              if (failReason == FailReason.ARGUMENT_NOT_TYPED) {
                channel.sendMessage("Invalid usage!").queue();
              }
            });
    return true;
  }

  private void handle(
      MessageChannel channel,
      BaseMappingType baseType,
      MappingType mappingType,
      String version,
      String mapping,
      int page) {
    try {
      List<Mapping> mappings =
          (baseType == BaseMappingType.SPIGOT ? bot.spigotParser : bot.yarnParser)
              .parseMapping(mappingType, version, mapping);
      if (mappings.isEmpty()) {
        if (mapping.contains("@")) {
          channel.sendMessage("No information found.").queue();
        } else {
          channel.sendMessage("No information found for: " + mapping).queue();
        }
        return;
      }
      MappingPaginator paginator = new MappingPaginator(mappings);
      List<Mapping> paged = paginator.getPage(page);
      if (paged.isEmpty()) {
        if (mapping.contains("@")) {
          channel.sendMessage("Invalid page.").queue();
        } else {
          channel.sendMessage("Invalid page " + page).queue();
        }
        return;
      }
      StringBuilder messageBuilder = new StringBuilder();
      for (Mapping map : paged) {
        messageBuilder.append(map.formatMessage()).append('\n').append('\n');
      }
      if (paginator.hasNext(page)) {
        messageBuilder.append("Page ").append(page).append("/").append(paginator.pageSize());
      }
      channel.sendMessage(messageBuilder.toString()).queue();
    } catch (NoSuchVersionException e) {
      channel.sendMessage(e.toString()).queue();
    }
  }
}
