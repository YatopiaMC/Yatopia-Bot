package org.yatopiamc.bot.message.filters;

public class YapfaFilter implements MessageFilter {

  @Override
  public void apply(FilterContext context) {
    if (applyEffectiveFilter(context.getMessage().getContentRaw(), "yapfa")) {
      FilterTriggers.registerTrigger(context.getAuthor().getIdLong());
      context.respond(context.getAuthor().getAsMention() + " , we're now Yatopia.");
    }
  }
}
