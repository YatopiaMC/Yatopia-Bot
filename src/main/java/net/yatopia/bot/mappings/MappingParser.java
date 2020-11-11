package net.yatopia.bot.mappings;

import java.io.IOException;
import java.util.List;

public interface MappingParser {

  void preLoadDownloaded() throws IOException;

  List<Mapping> parseMapping(MappingType type, String mcVer, String input)
      throws NoSuchVersionException;

  List<Mapping> getAllMappings(String mcVer) throws NoSuchVersionException;
}
