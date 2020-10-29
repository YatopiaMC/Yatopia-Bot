package net.yatopia.bot.mappings;

import java.util.List;

public interface MappingParser {

  List<Mapping> parseMapping(MappingType type, String mcVer, String input)
      throws NoSuchVersionException;

  List<Mapping> getAllMappings(String mcVer) throws NoSuchVersionException;
}
