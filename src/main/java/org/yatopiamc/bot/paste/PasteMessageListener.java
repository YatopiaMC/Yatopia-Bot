package org.yatopiamc.bot.paste;

import com.mrivanplays.binclient.servers.HasteServer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yatopiamc.bot.timings.TimingsMessageListener;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PasteMessageListener extends ListenerAdapter {
    private static final Logger LOGGER = LoggerFactory.getLogger(TimingsMessageListener.class);
    private static final Pattern VERSION = Pattern.compile("\\d+\\.\\d+\\.\\d+");

    private static final HasteServer pasteServer = new HasteServer("https://bin.birdflop.com/"); //Might change this in the future but it works for now normal haste bin seems to not work

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        List<Message.Attachment> attachments = event.getMessage().getAttachments();
        if (attachments.isEmpty()) return;
        CopyOnWriteArraySet<String> pasteIds = new CopyOnWriteArraySet<>();
        AtomicInteger waitingPastes = new AtomicInteger(0);
        for (Message.Attachment item : attachments) {
            if (item.getSize() > 5000000) continue;
            CompletableFuture<File> file = item.downloadToFile();
            try {
                if (isBinaryFile(file.join())) continue;
                Stream<String> lines = Files.lines(file.join().toPath());
                waitingPastes.getAndIncrement();
                pasteServer.createPaste(lines.collect(Collectors.joining(System.lineSeparator()))).async(paste -> {
                    pasteIds.add(paste);
                    waitingPastes.getAndDecrement();
                }, e -> {LOGGER.warn("Failed to upload to paste service");
                waitingPastes.getAndDecrement(); });
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    file.join().delete();
                } catch (Exception e) {
                    LOGGER.warn("Error Deleting File.");
                }
            }
        }
        while (waitingPastes.get() > 0) { }
        if (pasteIds.isEmpty()) return;
        JDA jda = event.getJDA();
        final EmbedBuilder embedBuilder = new EmbedBuilder();
        final User messageAuthor = event.getAuthor();
        embedBuilder.setTitle("Pastebin"); //remove url because people delete timings reports
        embedBuilder.setColor(0xffff00);
        embedBuilder.setTimestamp(Instant.now());
        embedBuilder.setAuthor(messageAuthor.getAsTag(), messageAuthor.getEffectiveAvatarUrl(), messageAuthor.getEffectiveAvatarUrl());
        embedBuilder.setFooter("https://yatopiamc.org/", jda.getSelfUser().getEffectiveAvatarUrl());
        int i = 0;
        for (String paste : pasteIds) {
            embedBuilder.addField(String.format("Paste %d", i), pasteServer.retrievePaste(paste).sync().getUrl(), true);
            i++;
        }

        event.getChannel().sendMessage(embedBuilder.build()).queue();
    }

    // stack copy
    Boolean isBinaryFile(File f) throws IOException {
        String type = Files.probeContentType(f.toPath());
        //type isn't text
        if (type == null) {
            //type couldn't be determined, assume binary
            return true;
        } else return !type.startsWith("text");
    }

}
