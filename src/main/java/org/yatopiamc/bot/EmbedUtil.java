package org.yatopiamc.bot;

import java.util.function.Supplier;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

public class EmbedUtil {

  private static Supplier<EmbedBuilder> defaultEmbed;

  public static void setDefaultEmbed(Supplier<EmbedBuilder> defaultEmbed) {
    EmbedUtil.defaultEmbed = defaultEmbed;
  }

  public static EmbedBuilder defaultEmbed() {
    return defaultEmbed.get();
  }

  public static EmbedBuilder withAuthor(User author) {
    return defaultEmbed()
        .setAuthor(
            author.getAsTag(), author.getEffectiveAvatarUrl(), author.getEffectiveAvatarUrl());
  }
}
