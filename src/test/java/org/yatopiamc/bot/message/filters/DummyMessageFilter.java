package org.yatopiamc.bot.message.filters;

public class DummyMessageFilter implements MessageFilter {

  private static DummyMessageFilter instance;

  public static DummyMessageFilter getInstance() {
    if (instance == null) {
      instance = new DummyMessageFilter();
    }
    return instance;
  }

  private DummyMessageFilter() {
  }

  @Override
  public void apply(FilterContext context) {
    // noop
  }
}
