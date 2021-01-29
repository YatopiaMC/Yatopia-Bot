package org.yatopiamc.bot.timings;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequest;
import org.apache.hc.client5.http.async.methods.SimpleHttpRequests;
import org.apache.hc.client5.http.async.methods.SimpleHttpResponse;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.async.CloseableHttpAsyncClient;
import org.apache.hc.client5.http.impl.async.HttpAsyncClients;
import org.apache.hc.client5.http.impl.nio.PoolingAsyncClientConnectionManager;
import org.apache.hc.core5.concurrent.FutureCallback;
import org.apache.hc.core5.http.HeaderElement;
import org.apache.hc.core5.http.impl.DefaultConnectionReuseStrategy;
import org.apache.hc.core5.http.message.BasicHeaderElementIterator;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.reactor.IOReactorConfig;
import org.apache.hc.core5.util.TimeValue;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TimingsMessageListener extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimingsMessageListener.class);
    private static final Pattern VERSION = Pattern.compile("\\d+\\.\\d+\\.\\d+");

    private final CloseableHttpAsyncClient httpAsyncClient;
    private final PoolingAsyncClientConnectionManager connectionManager;

    {
        connectionManager = new PoolingAsyncClientConnectionManager();
        connectionManager.setMaxTotal(16);
        connectionManager.setDefaultMaxPerRoute(8);
        httpAsyncClient = HttpAsyncClients.custom()
                .setConnectionReuseStrategy(new DefaultConnectionReuseStrategy())
                .setKeepAliveStrategy((response, context) -> {
                    BasicHeaderElementIterator it = new BasicHeaderElementIterator(
                            response.headerIterator("connection"));
                    while (it.hasNext()) {
                        HeaderElement he = it.next();
                        String param = he.getName();
                        String value = he.getValue();
                        if (value != null && param.equalsIgnoreCase("timeout")) {
                            return TimeValue.ofSeconds(Long.parseLong(value));
                        }
                    }
                    return TimeValue.ofSeconds(60);
                })
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(10, TimeUnit.SECONDS)
                        .setResponseTimeout(10, TimeUnit.SECONDS)
                        .setConnectionRequestTimeout(10, TimeUnit.SECONDS)
                        .build()
                )
                .setConnectionManager(connectionManager)
                .setIOReactorConfig(IOReactorConfig.custom()
                        .setSoKeepAlive(true)
                        .setSoReuseAddress(true)
                        .setTcpNoDelay(true)
                        .setSoTimeout(10, TimeUnit.SECONDS)
                        .build()
                )
                .build();
        httpAsyncClient.start();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            httpAsyncClient.close(CloseMode.GRACEFUL);
            connectionManager.close(CloseMode.GRACEFUL);
        }));
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
        final CompletableFuture<SimpleHttpResponse> timingsJsonRequest = execute(SimpleHttpRequests.get(timingsHost + "data.php?id=" + timingsId));
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Timings Analysis", url);
        timingsJsonRequest.handle((response, throwable) -> {
            try {
                if (response == null) {
                    final RuntimeException exception = new RuntimeException(throwable);
                    LOGGER.warn("An unexpected error occurred while processing this request", exception);
                    embedBuilder.setTitle("An unexpected error occurred while processing this request");
                    embedBuilder.appendDescription(exception.toString());
                    return null;
                }
                final JsonObject jsonObject = new Gson().fromJson(response.getBodyText(), JsonObject.class);
                if (jsonObject.has("timingsMaster")) {
                    final JsonObject timingsMaster = jsonObject.getAsJsonObject("timingsMaster");
                    checkMinecraftVersion(embedBuilder, timingsMaster);
                    final JsonObject system = timingsMaster.getAsJsonObject("system");
                    checkTimingCost(embedBuilder, system);
                    checkJvmVersion(embedBuilder, system);
                    checkJvmFlags(embedBuilder, system);
                }
                return null;
            } catch (Throwable t) {
                final RuntimeException exception = new RuntimeException(t);
                LOGGER.warn("An unexpected error occurred while processing this request", exception);
                embedBuilder.setTitle("An unexpected error occurred while processing this request");
                embedBuilder.appendDescription(exception.toString());
                return null;
            } finally {
                if(embedBuilder.getFields().isEmpty()) {
                    embedBuilder.addField("All good", "Analyzed with no issues", true);
                }
                embedBuilder.setFooter("Processed in " + (System.currentTimeMillis() - startTime) + "ms");
                inProgress.handle((msg, t) -> {
                    if(msg != null) {
                        msg.editMessage(embedBuilder.build()).queue();
                    }
                    if(t != null) {
                        LOGGER.warn("An unexpected error occurred while sending message", t);
                    }
                    return null;
                });
            }
        });
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
                    int maxHeapMegabytes = Integer.parseInt(s.substring("-Xmx".length()).replace("G", "000").replace("g", "000").replace("M", "").replace("m", ""));
                    if (maxHeapMegabytes < 5400)
                        embedBuilder.addField("Low Memory", "Allocate at least 6-10GB of ram to your server if you can afford it.", true);

                    final Optional<String> minMemString = Arrays.stream(flagList).filter(f -> f.startsWith("-Xms")).findFirst();
                    minMemString.ifPresent(s1 -> {
                        int minHeapMegabytes = Integer.parseInt(s1.substring("-Xms".length()).replace("G", "000").replace("g", "000").replace("M", "").replace("m", ""));
                        if (minHeapMegabytes != maxHeapMegabytes)
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

    private CompletableFuture<SimpleHttpResponse> execute(SimpleHttpRequest request) {
        CompletableFuture<SimpleHttpResponse> future = new CompletableFuture<>();
        try {
            httpAsyncClient.execute(request, new FutureCallback<SimpleHttpResponse>() {
                @Override
                public void completed(SimpleHttpResponse result) {
                    future.complete(result);
                }

                @Override
                public void failed(Exception ex) {
                    future.completeExceptionally(ex);
                }

                @Override
                public void cancelled() {
                    future.completeExceptionally(new CancellationException());
                }
            });
        } catch (Throwable t) {
            future.completeExceptionally(t);
        }
        return future;
    }

    private CompletableFuture<Message> inProgress(Message message) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        message.reply(new EmbedBuilder().setTitle("Query in progress").build()).queue(future::complete, future::completeExceptionally);
        return future;
    }

}
