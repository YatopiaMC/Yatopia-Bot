package net.yatopia.bot.message.filters;

import java.util.ArrayList;
import java.util.List;

public final class MessageFilterRegistry {

  private static MessageFilterRegistry instance;

  public static MessageFilterRegistry getInstance() {
    if (instance == null) {
      instance = new MessageFilterRegistry();
    }
    return instance;
  }

  private MessageFilterRegistry() {
//    registerFilter(new YapfaFilter());
//    registerFilter(new FlyingSquidsFilter());
  }

  private List<MessageFilter> filters = new ArrayList<>();

  public void registerFilter(MessageFilter filter) {
    if (!filters.contains(filter)) {
      filters.add(filter);
    }
  }

  public void applyFilters(FilterContext context) {
    for (MessageFilter filter : filters) {
      filter.apply(context);
    }
  }
}
