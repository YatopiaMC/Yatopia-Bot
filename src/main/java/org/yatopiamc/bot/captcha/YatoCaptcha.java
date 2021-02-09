package org.yatopiamc.bot.captcha;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Objects;
import java.util.Random;

public class YatoCaptcha extends ListenerAdapter {
    private static final HashMap<User, String> codes = new HashMap<>();
    private static TextChannel helpChannel = null;

    private static InputStream generateImage(User u) {
        InputStream is = null;
        try {
            final BufferedImage image = ImageIO.read(Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("yatopialogo.png")));
            final String code = String.valueOf(new Random().nextInt(1000000 - 50000) + 50000);

            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setFont(Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(ClassLoader.getSystemClassLoader().getResourceAsStream("LoveBaby.ttf"))).deriveFont(40f));
            AffineTransform at = new AffineTransform();
            at.setToRotation(Math.random());
            g.setTransform(at);
            g.drawString(code, 50, 50);
            g.dispose();

            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            is = new ByteArrayInputStream(os.toByteArray());
            os.close();
            codes.put(u, code);
        } catch (Exception ignored) {
        }
        return is;
    }

    public void onGuildMemberJoin(GuildMemberJoinEvent e) {
        if(e.getGuild().getId().equals("743126646617407649" /*GUILD ID*/))
            e.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Hello, please send me the code on this image so I can verify you are not a robot.").addFile(generateImage(e.getUser()), "yatocatpcha.png").onErrorFlatMap(throwable -> helpChannel.sendMessage("Hello " + e.getMember().getAsMention() + ", I need you to open your DMs so that I can send you a captcha."))).queue();
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if(codes.containsKey(e.getAuthor())) {
            Member m;
            try {
                m = helpChannel.getGuild().getMember(e.getAuthor());
            } catch (Exception e1) {
                e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("You left YatopiaMC, so there is no need to verify you.")).queue();
                return;
            }

            if(codes.get(e.getAuthor()).equals(e.getMessage().getContentRaw())) {
                assert m != null;
                helpChannel.getGuild().addRoleToMember(m, Objects.requireNonNull(helpChannel.getGuild().getRoleById("808517734577078302" /*ROLE TO GIVE*/))).queue();
                codes.remove(e.getAuthor());
                e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Verification successful.\nWelcome to YatopiaMC.\nHave a nice day!")).queue();
            } else
                e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Verification unsuccessful.\nPlease try again.").addFile(generateImage(e.getAuthor()), "yatocatpcha.png").onErrorFlatMap(throwable -> helpChannel.sendMessage("Hello " + e.getAuthor().getAsMention() + ", I need you to open your DMs so that I can send you a captcha."))).queue();
        }
    }

    public void onReady(ReadyEvent e) {
        helpChannel = e.getJDA().getTextChannelById("808517832408301598");
    }
}
