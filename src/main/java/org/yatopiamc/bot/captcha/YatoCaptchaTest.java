package org.yatopiamc.bot.captcha;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
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
import net.dv8tion.jda.api.entities.MessageChannel;

public class YatoCaptchaTest extends ListenerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(TimingsMessageListener.class);
    private static final HashMap<User, String> codes = new HashMap<>();
    private static MessageChannel helpChannel = null;

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

    public static void sendCaptcha(User u, MessageChannel channel) {
        helpChannel = channel;
        u.openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Hello, please send me the code on this image so I can verify you are not a robot.").addFile(generateImage(u), "yatocatpcha.png").onErrorFlatMap(throwable -> helpChannel.sendMessage("Hello " + u.getAsMention() + ", I need that you open your DMs so that I can send you a captcha."))).queue();
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (codes.containsKey(e.getAuthor())) {
            try {
                if (codes.get(e.getAuthor()).equals(e.getMessage().getContentRaw())) {
                    codes.remove(e.getAuthor());
                    e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Verification successful.")).queue();
                } else {
                    e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Verification unsuccessful.\nPlease try again.").addFile(generateImage(e.getAuthor()), "yatocatpcha.png").onErrorFlatMap(throwable -> helpChannel.sendMessage("Hello " + e.getAuthor().getAsMention() + ", I need you to open your DMs so that I can send you a captcha."))).queue();
                }
            } catch (Exception e1) {
                e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("You left the discord server you share with me, so there is no need to verify you.")).queue();
            }
        }
    }
}
