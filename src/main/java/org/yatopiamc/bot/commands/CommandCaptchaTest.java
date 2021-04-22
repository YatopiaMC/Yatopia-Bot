package org.yatopiamc.bot.commands;

import com.mrivanplays.jdcf.Command;
import com.mrivanplays.jdcf.CommandExecutionContext;
import com.mrivanplays.jdcf.args.CommandArguments;
import com.mrivanplays.jdcf.data.CommandAliases;
import com.mrivanplays.jdcf.data.CommandDescription;
import com.mrivanplays.jdcf.data.CommandUsage;
import net.dv8tion.jda.api.entities.MessageChannel;
import org.jetbrains.annotations.NotNull;
import org.yatopiamc.bot.captcha.YatoCaptchaTest;

@CommandAliases("captchatest")
@CommandDescription("Tests captcha")
@CommandUsage("captchatest")
public class CommandCaptchaTest extends Command {

    @Override
    public boolean execute(@NotNull CommandExecutionContext context, @NotNull CommandArguments args) {
        MessageChannel channel = context.getChannel();
        YatoCaptchaTest.sendCaptcha(context.getAuthor(), channel);
        return true;
    }
}
