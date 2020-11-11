package net.yatopia.bot.mappings.yarn;

import java.util.List;
import net.yatopia.bot.mappings.Mapping;

public final class YarnMappingsHolder {

  public static YarnMappingsHolder createFor(TinyType tinyType, List<Mapping> mappings) {
    return new YarnMappingsHolder(tinyType, mappings);
  }

  private final TinyType tinyType;
  private final List<Mapping> mappings;

  private YarnMappingsHolder(TinyType tinyType, List<Mapping> mappings) {
    this.tinyType = tinyType;
    this.mappings = mappings;
  }

  public TinyType getTinyType() {
    return tinyType;
  }

  public List<Mapping> getMappings() {
    return mappings;
  }
}
