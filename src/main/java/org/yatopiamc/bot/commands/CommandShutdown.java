package org.yatopiamc.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.PermissionCheckContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.MarkGuildOnly;

import net.dv8tion.jda.api.JDA;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.Member;
import java.util.List;

@CommandAliases("shutdown")
@MarkGuildOnly
public class CommandShutdown extends Command {

    private String botAdminRoleID;
    private Role botAdminRole;

    public CommandShutdown(@NotNull String botAdminRoleID) {
        this.botAdminRoleID = botAdminRoleID;
    }

    @Override
    public boolean hasPermission(@NotNull PermissionCheckContext context) {
        if (botAdminRole == null) {
            this.botAdminRole = context.getGuild().getRoleById(botAdminRoleID);
        }
        return this.findRole(context.getMember(), botAdminRole) != null;
    }

    public Role findRole(Member member, Role targetrole) {
        List<Role> roles = member.getRoles();
        return roles.stream()
                    .filter(role -> role.equals(targetrole)) // filter by role name
                    .findFirst() // take first result
                    .orElse(null); // else return null
    }

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        context.getJda().shutdownNow();
        context.getJda().getHttpClient().connectionPool().evictAll();
        context.getJda().getHttpClient().dispatcher().executorService().shutdown();
        return true;
    }
}
