package org.yatopiamc.bot.fun;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class RandomSentences extends ListenerAdapter {
    private static final ArrayList<String> sentences = new ArrayList<>(Arrays.asList("Hello", "Welcome to Yatopia", "Lol"));

    public void onGuildMessageReceived(GuildMessageReceivedEvent e) {
        String m = e.getMessage().getContentRaw();

        if(m.contains("Zeus") && !e.getAuthor().isBot())
            e.getChannel().sendMessage(sentences.get(new Random().nextInt(sentences.size()))).queue();
    }
}
