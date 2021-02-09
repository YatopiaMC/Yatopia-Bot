package org.yatopiamc.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@CommandAliases("giverole")
@CommandDescription("Give role to every member")
@CommandUsage("giverole")
public class CommandGiveRole extends Command {

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        MessageChannel channel = context.getChannel();
        String[] cmdArgs = context.getMessage().getContentRaw().split(" ");
        if(Arrays.asList("356822848641171456", "361319428169662474", "202500610686320649").contains(context.getAuthor().getId())) {
            if(cmdArgs.length >= 1) {
                Role role;
                try {
                    role = Objects.requireNonNull(context.getGuild()).getRoleById(cmdArgs[1]);
                } catch (Exception e) {
                    channel.sendMessage("Unknown role.").queue(message -> message.delete().queueAfter(10, TimeUnit.SECONDS));
                    return true;
                }
                channel.sendMessage("Giving role to all users. This may take awhile.").queue();
                for (Member m : context.getGuild().getMembers()) {
                    try {
                        assert role != null;
                        context.getGuild().addRoleToMember(m, role).queue();
                        System.out.println("Added role to " + m.getUser().getAsTag());
                    } catch (Exception e) {
                        System.out.println("Failed to add role to " + m.getUser().getAsTag() + ".\nI might lack the permissions to do so.");
                    }
                }
                channel.sendMessage("Finished adding role to all users.").queue();
            } else
                channel.sendMessage("Missing role ID argument.").queue();
        }
        return true;
    }
}
