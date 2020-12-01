package org.yatopiamc.bot.mappings;

import java.util.function.Function;

public enum NameType {
  ORIGINAL(Mapping::getObfuscated),
  INTERMEDIATE(Mapping::getIntermediate),
  NAME(Mapping::getName);

  private final Function<Mapping, String> getter;

  NameType(Function<Mapping, String> getter) {
    this.getter = getter;
  }

  public String get(Mapping mapping) {
    return getter.apply(mapping);
  }
}
