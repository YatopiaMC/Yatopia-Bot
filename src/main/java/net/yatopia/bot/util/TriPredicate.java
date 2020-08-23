package net.yatopia.bot.util;

@FunctionalInterface
public interface TriPredicate<T1, T2, T3> {

  boolean test(T1 t1, T2 t2, T3 t3);
}
