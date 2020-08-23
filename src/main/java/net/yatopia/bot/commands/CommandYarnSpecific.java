package net.yatopia.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.args.FailReason;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import java.util.List;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.yatopia.bot.YatopiaBot;
import net.yatopia.bot.mappings.Mapping;
import net.yatopia.bot.mappings.MappingPaginator;
import net.yatopia.bot.mappings.MappingType;
import net.yatopia.bot.mappings.NoSuchVersionException;
import org.jetbrains.annotations.NotNull;

@CommandAliases("yc|ym|yf")
@CommandDescription("Yarn specific mappings")
@CommandUsage("yc|?ym|?yf [mapping] [version] (page)")
public class CommandYarnSpecific extends Command {

  private final YatopiaBot bot;

  public CommandYarnSpecific(YatopiaBot bot) {
    this.bot = bot;
  }

  @Override
  public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
    String alias = context.getAlias();
    MessageChannel channel = context.getChannel();
    MappingType mappingType;
    switch (alias) {
      case "yc":
        mappingType = MappingType.CLASS;
        break;
      case "ym":
        mappingType = MappingType.METHOD;
        break;
      case "yf":
        mappingType = MappingType.FIELD;
        break;
      default:
        throw new IllegalArgumentException("Wat did just happen? CommandYarnSpecific");
    }
    args.nextString()
        .ifPresent(
            mapping ->
                args.nextString()
                    .ifPresent(
                        version ->
                            args.nextInt()
                                .ifPresent(
                                    page -> handle(channel, mappingType, version, mapping, page))
                                .orElse(
                                    failReason ->
                                        handle(channel, mappingType, version, mapping, 1)))
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
      MessageChannel channel, MappingType mappingType, String version, String mapping, int page) {
    try {
      List<Mapping> mappings = bot.yarnParser.parseMapping(mappingType, version, mapping);
      if (mappings.isEmpty()) {
        channel.sendMessage("No information found for: " + mapping).queue();
        return;
      }
      MappingPaginator paginator = new MappingPaginator(mappings);
      List<Mapping> paged = paginator.getPage(page);
      if (paged.isEmpty()) {
        channel.sendMessage("Invalid page " + page).queue();
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
