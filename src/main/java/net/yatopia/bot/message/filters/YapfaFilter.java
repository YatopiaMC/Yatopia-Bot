package net.yatopia.bot.message.filters;

public class YapfaFilter implements MessageFilter {

  @Override
  public void apply(FilterContext context) {
    if (applyEffectiveFilter(context.getMessage().getContentRaw(), "yapfa")) {
      context.respond(context.getAuthor().getAsMention() + " , we're now Yatopia.");
    }
  }
}
