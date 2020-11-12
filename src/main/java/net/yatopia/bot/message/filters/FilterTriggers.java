package net.yatopia.bot.message.filters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class FilterTriggers {

  private static Map<Long, AtomicInteger> filterTriggersBySnowflake = new ConcurrentHashMap<>();

  public static void registerTrigger(Long snowflake) {
    if (!filterTriggersBySnowflake.containsKey(snowflake)) {
      AtomicInteger integer = new AtomicInteger(1);
      filterTriggersBySnowflake.put(snowflake, integer);
    } else {
      AtomicInteger integer = filterTriggersBySnowflake.get(snowflake);
      integer.getAndIncrement();
      filterTriggersBySnowflake.replace(snowflake, integer);
    }
  }

  public static boolean hasReachedTriggerThreshold(Long snowflake) {
    AtomicInteger integer = filterTriggersBySnowflake.get(snowflake);
    if (integer == null) {
      return false;
    }
    return integer.get() > 3;
  }
}
