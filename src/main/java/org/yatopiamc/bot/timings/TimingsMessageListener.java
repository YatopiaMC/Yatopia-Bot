package org.yatopiamc.bot.timings;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yatopiamc.bot.util.NetworkUtils;

import java.awt.*;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class TimingsMessageListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimingsMessageListener.class);
    private static final Pattern VERSION = Pattern.compile("\\d+\\.\\d+\\.\\d+");

    private final LoadingCache<String, CompletableFuture<SimpleHttpResponse>> loadingCache;

    {
        loadingCache = CacheBuilder.newBuilder()
                .expireAfterWrite(30, TimeUnit.MINUTES)
                .softValues()
                .concurrencyLevel(4)
                .build(new CacheLoader<String, CompletableFuture<SimpleHttpResponse>>() {
                    @Override
                    public CompletableFuture<SimpleHttpResponse> load(String key) {
                        return NetworkUtils.execute(SimpleHttpRequests.get(key));
                    }
                });
    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        final long startTime = System.currentTimeMillis();
        final Message message = event.getMessage();
        final String[] words = message.getContentStripped().split("[ \n]");
        String url = null;
        for (String word : words) {
            if (word.startsWith("https://timin")) {
                if (word.contains("/d=")) word = word.replaceAll("/d=", "/?id=");
                word = word.split("#")[0];
                if (word.contains("/?id=")) {
                    try {
                        new URI(word);
                        new URL(word);
                        url = word;
                    } catch (MalformedURLException | URISyntaxException ignored) {
                    }
                }
            }
        }
        if (url == null) return;
        LOGGER.info("Querying {}", url);
        final String[] parts = url.split("\\?id=");
        final String timingsHost = parts[0];
        final String timingsId = parts[1];
        final CompletableFuture<Message> inProgress = inProgress(message);
        final CompletableFuture<SimpleHttpResponse> timingsJsonRequest = loadingCache.getUnchecked(timingsHost + "data.php?id=" + timingsId);
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final User messageAuthor = event.getAuthor();
        embedBuilder.setTitle("Timings Analysis"); //remove url because people delete timings reports
        embedBuilder.setColor(0xffff00);
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setAuthor(messageAuthor.getAsTag(), messageAuthor.getEffectiveAvatarUrl(), messageAuthor.getEffectiveAvatarUrl());
        timingsJsonRequest.handleAsync((response, throwable) -> {
            boolean hasError = false;
            long startProcessingTime = System.currentTimeMillis();
            try {
                if (response == null) {
                    loadingCache.asMap().remove(timingsHost + "data.php?id=" + timingsId);
                    hasError = true;
                    final RuntimeException exception = new RuntimeException(throwable);
                    LOGGER.warn("An unexpected error occurred while processing this request", exception);
                    embedBuilder.setTitle("An unexpected error occurred while processing this request");
                    embedBuilder.appendDescription(exception.toString());
                    return null;
                }
                final JsonElement jsonElement = new Gson().fromJson(response.getBodyText(), JsonElement.class);
                if (!jsonElement.isJsonObject()) {
                    embedBuilder.setTitle("Invalid Timings report");
                    hasError = true;
                    return null;
                }
                final JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has("timingsMaster")) {
                    final JsonObject timingsMaster = jsonObject.getAsJsonObject("timingsMaster");
                    checkMinecraftVersion(embedBuilder, timingsMaster);
                    checkSystem(embedBuilder, timingsMaster);
                    checkDataPacks(embedBuilder, timingsMaster);
                    checkPlugins(embedBuilder, timingsMaster);
                    checkServerConfigs(embedBuilder, timingsMaster);
                }
                return null;
            } catch (Throwable t) {
                loadingCache.asMap().remove(timingsHost + "data.php?id=" + timingsId);
                hasError = true;
                final RuntimeException exception = new RuntimeException(t);
                LOGGER.warn("An unexpected error occurred while processing this request", exception);
                embedBuilder.setTitle("An unexpected error occurred while processing this request");
                embedBuilder.appendDescription(exception.toString());
                return null;
            } finally {
                if (embedBuilder.getFields().isEmpty() && !hasError) {
                    embedBuilder.addField("All good", "Analyzed with no issues", true);
                }
                final int size = embedBuilder.getFields().size();
                if(size > 24) {
                    final List<MessageEmbed.Field> fields = new ArrayList<>(embedBuilder.getFields());
                    embedBuilder.clearFields();
                    for (int i = 0; i < 24; i++) {
                        MessageEmbed.Field field = fields.get(i);
                        embedBuilder.addField(field);
                    }
                    embedBuilder.addField(String.format("Plus %d more recommendations", size - 24), "Create a new timings report after resolving some of the above issues to see more.", false);
                }
                embedBuilder.setFooter(String.format("https://yatopiamc.org/ • Timing: %dms network, %dms processing", startProcessingTime - startTime, System.currentTimeMillis() - startProcessingTime),
                        event.getJDA().getSelfUser().getEffectiveAvatarUrl());
                inProgress.handle((msg, t) -> {
                    if(msg != null) {
                        msg.editMessage(embedBuilder.build()).queue();
                    }
                    if (t != null) {
                        LOGGER.warn("An unexpected error occurred while sending message", t);
                    }
                    return null;
                });
            }
        });
    }

    private void checkServerConfigs(EmbedBuilder embedBuilder, JsonObject timingsMaster) {
        final JsonObject configs = timingsMaster.getAsJsonObject("config");
        TimingsSuggestions.SERVER_CONFIG_SUGGESTIONS.entrySet().stream()
                .filter(entry -> configs.has(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> entry.getValue().suggestions.entrySet().stream().sorted(Map.Entry.comparingByKey()))
                .forEach(entry -> {
                    try {
                        if (entry.getValue().predicate.test(configs))
                            embedBuilder.addField(entry.getKey(), entry.getValue().prefix + " " + entry.getValue().warning, true);
                    } catch (NullPointerException ignored) {
                    } catch (Throwable t) {
                        embedBuilder.addField(entry.getKey(), "Error evaluating expression: " + t.toString(), true);
                        LOGGER.warn(t.toString());
                    }
                });
    }

    private void checkPlugins(EmbedBuilder embedBuilder, JsonObject timingsMaster) {
        if(!timingsMaster.get("plugins").isJsonObject()) return;
        final JsonObject plugins = timingsMaster.getAsJsonObject("plugins");
        final JsonObject configs = timingsMaster.getAsJsonObject("config");
        TimingsSuggestions.SERVER_PLUGIN_SUGGESTIONS.entrySet().stream().flatMap(entry -> {
            if (configs.has(entry.getKey()))
                return entry.getValue().suggestions.entrySet().stream();
            return Stream.empty();
        }).sorted(Map.Entry.comparingByKey()).filter(entry -> plugins.has(entry.getKey())).forEach(entry -> {
            embedBuilder.addField(String.format("%s %s", entry.getKey(), entry.getValue().prefix), entry.getValue().warning, true);
        });
        try {
            if (plugins.has("TCPShield") && configs.has("purpur") && configs.getAsJsonObject("purpur").getAsJsonObject("settings").get("use-alternate-keepalive").getAsBoolean())
                embedBuilder.addField("settings.use-alternate-keepalive", "Disable this in [purpur.yml](http://bit.ly/purpurc). It can cause issues with TCPShield", true);
        } catch (NullPointerException ignored) {
        }
        try {
            if (!plugins.has("TCPShield") && configs.has("purpur") && !configs.getAsJsonObject("purpur").getAsJsonObject("settings").get("use-alternate-keepalive").getAsBoolean())
                embedBuilder.addField("settings.use-alternate-keepalive", "Enable this in [purpur.yml](http://bit.ly/purpurc).", true);
        } catch (NullPointerException ignored) {
        }
        try {
            if ((plugins.has("PetBlocks") || plugins.has("BlockBalls") || plugins.has("ArmorStandTools")) &&
                    configs.has("paper") && configs.getAsJsonObject("world-settings").entrySet().stream().anyMatch(entry -> entry.getValue().getAsJsonObject().get("armor-stands-tick").getAsBoolean()))
                embedBuilder.addField("armor-stands-tick", "Disable this in [paper.yml](http://bit.ly/paperconf).", true);
        } catch (NullPointerException ignored) {
        }
    }

    private void checkDataPacks(EmbedBuilder embedBuilder, JsonObject timingsMaster) {
        final JsonObject handlerMap = timingsMaster.getAsJsonObject("idmap").getAsJsonObject("handlerMap");
        handlerMap.entrySet().stream().filter(entry -> {
            final String name = entry.getValue().getAsJsonObject().get("name").getAsString();
            return name.startsWith("Command Function - ") && name.endsWith(":tick");
        }).forEach(entry -> {
            final String name = entry.getValue().getAsJsonObject().get("name").getAsString().substring("Command Function - ".length()).split(":tick")[0];
            embedBuilder.addField(name, "This datapack uses command functions which are laggy.", true);
        });
    }

    private void checkSystem(EmbedBuilder embedBuilder, JsonObject timingsMaster) {
        final JsonObject system = timingsMaster.getAsJsonObject("system");
        checkTimingCost(embedBuilder, system);
        checkJvmVersion(embedBuilder, system);
        checkJvmFlags(embedBuilder, system);
        checkCPU(embedBuilder, system);
    }

    private void checkCPU(EmbedBuilder embedBuilder, JsonObject system) {
        final int cpu = system.get("cpu").getAsInt();
        if(cpu < 4)
            embedBuilder.addField("CPU Threads", String.format("You have only %d thread(s). Find a better host.", cpu), true);
    }

    private void checkJvmFlags(EmbedBuilder embedBuilder, JsonObject system) {
        final String jvmFlags = system.get("flags").getAsString();
        if (jvmFlags.contains("-XX:+UseZGC")) {
            final String jvmVersion = system.get("jvmversion").getAsString();
            if (Integer.parseInt(jvmVersion.split("\\.")[0]) < 14)
                embedBuilder.addField("Java version & ZGC", "If you are going to use ZGC, you should also use Java 14+.", true);
        } else if (jvmFlags.contains("-Daikars.new.flags=true")) {
            if (!jvmFlags.contains("XX:G1MixedGCCountTarget=4"))
                embedBuilder.addField("Outdated JVM Flags", "Add `-XX:G1MixedGCCountTarget=4` to flags.", true);
            if (!jvmFlags.contains("-XX:+UseG1GC"))
                embedBuilder.addField("Outdated JVM Flags", "You must use G1GC when using Aikar's flags.", true);
            if (jvmFlags.contains("-Xmx")) {
                String[] flagList = jvmFlags.split(" ");
                final Optional<String> maxMemString = Arrays.stream(flagList).filter(f -> f.startsWith("-Xmx")).findFirst();
                maxMemString.ifPresent(s -> {
                    long maxHeapBytes = parseMemory(s.substring("-Xmx".length()));
                    if (maxHeapBytes < 5_400L * 1024 * 1024)
                        embedBuilder.addField("Low Memory", "Allocate at least 6-10GB of ram to your server if you can afford it.", true);

                    final Optional<String> minMemString = Arrays.stream(flagList).filter(f -> f.startsWith("-Xms")).findFirst();
                    minMemString.ifPresent(s1 -> {
                        long minHeapMegabytes = parseMemory(s1.substring("-Xms".length()));
                        if (minHeapMegabytes != maxHeapBytes)
                            embedBuilder.addField("Outdated JVM Flags", "Your Xmx and Xms values should be equivalent when using Aikar's flags.", true);
                    });
                });
            }
        } else if (jvmFlags.contains("-Dusing.aikars.flags=mcflags.emc.gs")) {
            embedBuilder.addField("Outdated JVM Flags", "Update [Aikar's flags](https://aikar.co/2018/07/02/tuning-the-jvm-g1gc-garbage-collector-flags-for-minecraft/).", true);
        } else {
            embedBuilder.addField("Use Aikar's Flags", "Use [Aikar's flags](https://aikar.co/2018/07/02/tuning-the-jvm-g1gc-garbage-collector-flags-for-minecraft/).", true);
        }
    }

    private long parseMemory(String str) {
        long memBytes;
        try {
            memBytes = Integer.parseInt(str);
        } catch (NumberFormatException e) {
            final long number = Long.parseLong(str.substring(0, str.length() - 1));
            if (str.endsWith("k") || str.endsWith("K")) memBytes = number * 1024;
            else if (str.endsWith("m") || str.endsWith("M")) memBytes = number * 1024 * 1024;
            else if (str.endsWith("g") || str.endsWith("G")) memBytes = number * 1024 * 1024 * 1024;
            else if (str.endsWith("t") || str.endsWith("T")) memBytes = number * 1024 * 1024 * 1024 * 1024;
            else throw new NumberFormatException("For input string: " + str);
        }
        return memBytes;
    }

    private void checkJvmVersion(EmbedBuilder embedBuilder, JsonObject system) {
        final String jvmVersion = system.get("jvmversion").getAsString();
        if (jvmVersion.startsWith("1.8.") || jvmVersion.startsWith("9.") || jvmVersion.startsWith("10."))
            embedBuilder.addField("Java version", String.format("You are using Java %s. Update to [Java 11](https://adoptopenjdk.net/installation.html).", jvmVersion), true);
    }

    private void checkTimingCost(EmbedBuilder embedBuilder, JsonObject system) {
        final int timingcost = system.get("timingcost").getAsInt();
        if (timingcost > 300)
            embedBuilder.addField("Timingcost is high", String.format("Your timingcost is %d. Your cpu is overloaded and/or slow. Find a better host.", timingcost), true);
    }

    private void checkMinecraftVersion(EmbedBuilder embedBuilder, JsonObject timingsMaster) {
        final String versionString = timingsMaster.get("version").getAsString();
        final Matcher minecraftVersion = VERSION.matcher(versionString);
        if (minecraftVersion.find()) {
            if (!minecraftVersion.group().equals(TimingsSuggestions.CURRENT_MINECRAFT_VERSION))
                embedBuilder.addField("Legacy Build", String.format("You are using %s. Update to %s", versionString, TimingsSuggestions.CURRENT_MINECRAFT_VERSION), true);
        } else {
            embedBuilder.addField("Value Error", String.format("Could not locate version from %s", versionString), true);
        }
    }

    private CompletableFuture<Message> inProgress(Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        message.reply(new EmbedBuilder().setTitle("Query in progress").build()).queue(future::complete, future::completeExceptionally);
        return future;
    }
}
