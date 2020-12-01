package org.yatopiamc.bot.message.filters;

public class FlyingSquidsFilter implements MessageFilter {

  @Override
  public void apply(FilterContext context) {
    //    if (applyEffectiveFilter(
    //        context.getMessage().getContentRaw(),
    //        2,
    //        Arrays.asList("flying", "fly", "squids", "squid"))) { // todo: make it work with this
    String message = context.getMessage().getContentRaw().toLowerCase();
    if ((message.contains("squid") && (message.contains("fly") || message.contains("flying")))
        || (message.contains("squids")
            && (message.contains("fly") || message.contains("flying")))) {
      FilterTriggers.registerTrigger(context.getAuthor().getIdLong());
      context.respond(
          context.getAuthor().getAsMention()
              + " , NO!!!!!! We're only porting patches that make sense from game aspect and performance patches / patches that fix mojira reported bugs. NOTHING ELSE!!!!");
    }
  }
}
