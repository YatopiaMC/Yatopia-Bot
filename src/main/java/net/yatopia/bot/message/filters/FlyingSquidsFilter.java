package net.yatopia.bot.message.filters;

import java.util.Arrays;

public class FlyingSquidsFilter implements MessageFilter {

  @Override
  public void apply(FilterContext context) {
    if (applyEffectiveFilter(
        context.getMessage().getContentRaw(),
        2,
        Arrays.asList("flying", "fly", "squids", "squid"))) {
      context.respond(
          context.getAuthor().getAsMention()
              + " , NO!!!!!! We're only porting patches that make sense from game aspect and performance patches / patches that fix mojira reported bugs. NOTHING ELSE!!!!");
    }
  }
}
