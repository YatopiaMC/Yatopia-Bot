package org.yatopiamc.bot.server;

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
import java.util.Random;

public class YatoCaptcha extends ListenerAdapter {
    private static final HashMap<User, String> codes = new HashMap<>();
    private static TextChannel general = null;

    private static InputStream generateImage(User u) {
        InputStream is = null;
        try {
            final BufferedImage image = ImageIO.read(ClassLoader.getSystemClassLoader().getResourceAsStream("yatopialogo.png"));
            final String code = String.valueOf(new Random().nextInt(1000000 - 50000) + 50000);

            Graphics2D g = (Graphics2D) image.getGraphics();
            g.setFont(Font.createFont(Font.TRUETYPE_FONT, ClassLoader.getSystemClassLoader().getResourceAsStream("LoveBaby.ttf")).deriveFont(40f));
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
            e.getUser().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Hi, please send me the code on this image to be able to talk in the Discord ^^\n> Have a nice day !").addFile(generateImage(e.getUser()), "yatocatpcha.png").onErrorFlatMap(throwable -> general.sendMessage("Hi " + e.getMember().getAsMention() + ", i need that you open your DMs for that I can send you the captcha code, thanks."))).queue();
    }

    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if(codes.containsKey(e.getAuthor())) {
            Member m = general.getGuild().getMember(e.getAuthor());
            if(m == null) {
                e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("You are no longer on the Yatopia's server, so i can't give you the role to make you able to talk.")).queue();
                return;
            }
            if(codes.get(e.getAuthor()).equals(e.getMessage().getContentRaw())) {
                general.getGuild().addRoleToMember(m, general.getGuild().getRoleById("member role id here" /*ROLE TO GIVE*/)).queue();
                codes.remove(e.getAuthor());
                e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("I gave you the member role ! \n> **You are now able to talk** :)\nBye.")).queue();
            } else
                e.getAuthor().openPrivateChannel().flatMap(privateChannel -> privateChannel.sendMessage("Ohh nooo, you failed. **Try again ^^**").addFile(generateImage(e.getAuthor()), "yatocatpcha.png").onErrorFlatMap(throwable -> general.sendMessage("Hi " + e.getAuthor().getAsMention() + ", i need that you open your DMs for that I can send you the captcha code, thanks."))).queue();
        }
    }

    public void onReady(ReadyEvent e) {
        general = e.getJDA().getTextChannelById("748481491008225351");
    }
}
