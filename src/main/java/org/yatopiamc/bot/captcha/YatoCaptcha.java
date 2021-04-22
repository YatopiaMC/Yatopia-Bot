package org.yatopiamc.bot.captcha;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yatopiamc.bot.timings.TimingsMessageListener;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class YatoCaptcha extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimingsMessageListener.class);
    private static final HashMap<User, String> codes = new HashMap<>();
    private static TextChannel helpChannel = null;

    private static InputStream generateImage(User u) {
        InputStream is = null;
        try {
            final BufferedImage image = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("yatopialogo.png")));
            final String code = String.valueOf(ThreadLocalRandom.current().nextInt(1000000 - 50000) + 50000);

            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("LoveBaby.ttf"))).deriveFont(40f));
            AffineTransform at = new AffineTransform();
            at.setToRotation(Math.random());
            g.setTransform(at);
            g.drawString(code, 130, 130);
            g.dispose();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            is = new ByteArrayInputStream(os.toByteArray());
            os.close();
            codes.put(u, code);
        } catch (Exception e) {
            LOGGER.error(String.valueOf(e));
        }
        return is;
    }

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if (e.getGuild().getId().equals("743126646617407649" /*GUILD ID*/))
            sendCaptcha(e.getUser());
    }

    private static void sendCaptcha(User u) {
        u.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Hello, please send me the code on this image so I can verify you are not a robot.").addFile(generateImage(u), "yatocatpcha.png").onErrorFlatMap(throwable -> helpChannel.sendMessage("Hello " + u.getAsMention() + ", I need that you open your DMs so that I can send you a captcha.\n When you did it, add a reaction to this message and I'll send you a captcha verification."))).queue();
    }

    public void onMessageReactionAdd(MessageReactionAddEvent e) {
        e.getChannel().retrieveMessageById(e.getMessageId()).queue(message -> {
            if (message.getAuthor().getId().equals("806584703687065632") && message.getContentRaw().contains("I need that you open your DMs") && message.getMentionedMembers().contains(e.getMember())) {
                sendCaptcha(Objects.requireNonNull(e.getUser()));
                message.delete().queue();
            }
        });
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if(codes.containsKey(e.getAuthor())) {
            try {
                helpChannel.getGuild().retrieveMember(e.getAuthor()).queue(member -> {
                    if (codes.get(e.getAuthor()).equals(e.getMessage().getContentRaw())) {
                        helpChannel.getGuild().addRoleToMember(member, Objects.requireNonNull(helpChannel.getGuild().getRoleById("808517734577078302" /*ROLE TO GIVE*/))).queue();
                        codes.remove(e.getAuthor());
                        e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Verification successful.\nWelcome to YatopiaMC.\nHave a nice day!")).queue();
                    } else {
                        e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Verification unsuccessful.\nPlease try again.").addFile(generateImage(e.getAuthor()), "yatocatpcha.png").onErrorFlatMap(throwable -> helpChannel.sendMessage("Hello " + e.getAuthor().getAsMention() + ", I need you to open your DMs so that I can send you a captcha."))).queue();
                    }
                });
            } catch (Exception e1) {
                e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("You left YatopiaMC, so there is no need to verify you.")).queue();
            }
        }
    }

    public void onReady(ReadyEvent e) {
        helpChannel = e.getJDA().getTextChannelById("808517832408301598");
    }
}
