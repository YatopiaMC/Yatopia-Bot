package org.yatopiamc.bot;

import com.mrivanplays.jdcf.CommandManager;
import com.mrivanplays.jdcf.builtin.CommandShutdown;
import com.mrivanplays.jdcf.settings.CommandSettings;
import com.mrivanplays.jdcf.settings.prefix.ImmutablePrefixHandler;
import java.awt.Color;
import java.io.IOException;
import java.time.Instant;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.yatopiamc.bot.commands.CommandAsk;
import org.yatopiamc.bot.commands.CommandDownloadSpecific;
import org.yatopiamc.bot.commands.CommandJDKSpecific;
import org.yatopiamc.bot.commands.CommandMappingSpecific;
import org.yatopiamc.bot.commands.CommandPing;
import org.yatopiamc.bot.commands.CommandShitspiller;
import org.yatopiamc.bot.commands.CommandUpstream;
import org.yatopiamc.bot.commands.CommandVanilla;
import org.yatopiamc.bot.commands.CommandVroomVroom;
import org.yatopiamc.bot.commands.CommandYatopiaSpecific;
import org.yatopiamc.bot.mappings.MappingParser;
import org.yatopiamc.bot.mappings.spigot.SpigotMappingHandler;
import org.yatopiamc.bot.mappings.yarn.YarnMappingHandler;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class YatopiaBot {

  public static final Logger LOGGER = LoggerFactory.getLogger(YatopiaBot.class);

  public static void main(String[] args) throws LoginException, InterruptedException, IOException {
    ConfigInitializer config = new ConfigInitializer();
    if (!config.shouldStart()) {
      LOGGER.error("Couldn't find token; disabling");
      LOGGER.info("A config.json file was generated; put your bot token there.");
      System.exit(0);
      return;
    }
    new YatopiaBot(config.getToken()).start();
  }

  private final String token;
  private final ScheduledExecutorService executor;

  public MappingParser yarnParser;
  public MappingParser spigotParser;

  private YatopiaBot(String token) {
    this.token = token;
    this.executor =
        Executors.newScheduledThreadPool(
            8,
            new ThreadFactory() {
              private AtomicInteger count = new AtomicInteger(0);

              @Override
              public Thread newThread(@NotNull Runnable r) {
                Thread thread = new Thread(r, "Yatopia-Bot Thread #" + count.getAndIncrement());
                thread.setDaemon(true);
                return thread;
              }
            });
  }

  public void start() throws LoginException, InterruptedException, IOException {
    JDA jda =
        JDABuilder.create(GatewayIntent.GUILD_MESSAGES, GatewayIntent.GUILD_EMOJIS)
            .setToken(token)
            .setGatewayPool(executor)
            .setCallbackPool(executor)
            .setRateLimitPool(executor)
            .setActivity(Activity.playing("Yatopia.jar"))
            .disableCache(CacheFlag.VOICE_STATE, CacheFlag.ACTIVITY)
            //.addEventListeners(new MessageListener())
            .build()
            .awaitReady();

    EmbedUtil.setDefaultEmbed(
        () ->
            new EmbedBuilder()
                .setColor(0xffff00)
                .setTimestamp(Instant.now())
                .setFooter("https://yatopiamc.org/", jda.getSelfUser().getEffectiveAvatarUrl()));

    CommandSettings settings = CommandSettings.defaultSettings();
    settings.setEnablePrefixCommand(false);
    settings.setPrefixHandler(new ImmutablePrefixHandler("?"));
    settings.setEnableHelpCommand(true);
    settings.setErrorEmbed(() -> EmbedUtil.defaultEmbed().setColor(Color.RED).setTitle("Error"));
    settings.setExecutorService(executor);
    settings.setSuccessEmbed(
        () ->
            EmbedUtil.defaultEmbed()
                .setTimestamp(Instant.now())
                .setColor(Color.GREEN)
                .setTitle("Success"));
    settings.setNoPermissionEmbed(
        () ->
            EmbedUtil.defaultEmbed()
                .setColor(Color.RED)
                .setTimestamp(Instant.now())
                .setTitle("Insufficient permissions")
                .setDescription("You don't have permission to perform this command."));
    settings.setHelpCommandEmbed(
        () ->
            EmbedUtil.defaultEmbed()
                .setTitle("`()` - optional ; `[]` - required")
                .setThumbnail(
                    "https://cdn.discordapp.com/attachments/745296670631395482/745298764788400238/yatipia.png"));
    settings.setFailReasonHandler(null);
    settings.setCommandsPerHelpPage(10);
    settings.setLogExecutedCommands(true);

    CommandManager commandManager = new CommandManager(jda, settings);
    yarnParser = new YarnMappingHandler();
    spigotParser = new SpigotMappingHandler();
    yarnParser.preLoadDownloaded();
    spigotParser.preLoadDownloaded();
    commandManager.registerCommands(
        new CommandJDKSpecific(),
        new CommandDownloadSpecific(),
        new CommandYatopiaSpecific(),
        new CommandAsk(),
        new CommandVanilla(),
        new CommandUpstream(),
        new CommandMappingSpecific(this),
        new CommandPing(),
        new CommandShutdown("252049584598024192"),
        new CommandShitspiller(),
        new CommandVroomVroom());

    executor.scheduleAtFixedRate(
        new Runnable() {
          private int i = 0;

          @Override
          public void run() {
            if (i == 0) {
              i = 1;
              jda.getPresence().setActivity(Activity.playing("Yatopia.jar"));
            } else {
              jda.getPresence().setActivity(Activity.watching("over you ♥"));
              i = 0;
            }
          }
        },
        15,
        15,
        TimeUnit.SECONDS);
    LOGGER.info("Online");
  }
}
