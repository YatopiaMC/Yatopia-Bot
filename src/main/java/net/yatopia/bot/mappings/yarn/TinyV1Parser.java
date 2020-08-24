package net.yatopia.bot.mappings.yarn;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;
import net.yatopia.bot.mappings.BaseMappingType;
import net.yatopia.bot.mappings.Mapping;
import net.yatopia.bot.mappings.MappingParser;
import net.yatopia.bot.mappings.MappingType;
import net.yatopia.bot.mappings.NameType;

public class TinyV1Parser {

  public static final TinyV1Parser INSTANCE = new TinyV1Parser();

  static final Map<String, NameType> BY_NAME =
      new HashMap<String, NameType>() {
        {
          put("official", NameType.ORIGINAL);
          put("intermediary", NameType.INTERMEDIATE);
          put("named", NameType.NAME);
        }
      };

  public List<Mapping> parse(File mappingFile, String minecraftVersion, MappingParser constructor)
      throws IOException {
    List<String> lines = new ArrayList<>();
    try (BufferedReader reader =
        new BufferedReader(
            new InputStreamReader(
                new GZIPInputStream(new FileInputStream(mappingFile)), StandardCharsets.UTF_8))) {
      String line;
      while ((line = reader.readLine()) != null) {
        lines.add(line);
      }
    }

    String header = lines.remove(0);
    String[] headerInfo = header.split("\t");
    int tinyver = Integer.parseInt(headerInfo[0].substring(1));
    if (tinyver != 1) {
      throw new IllegalArgumentException("Unsupported mappings version");
    }
    List<Integer> order = new ArrayList<>();
    for (int i = 1, len = headerInfo.length; i < len; i++) {
      order.add(BY_NAME.get(headerInfo[i]).ordinal() + 1);
    }

    List<Mapping> ret = new ArrayList<>();
    for (String line : lines) {
      ret.add(fromString(line, order, minecraftVersion, constructor));
    }
    return ret;
  }

  private Mapping fromString(
      String line, List<Integer> order, String minecraftVersion, MappingParser constructor) {
    String[] info = line.split("\t");
    MappingType type = MappingType.valueOf(info[0]);
    switch (type) {
      case CLASS:
        String intermediate = info[order.get(1)];
        String name = info[order.get(2)];
        return new Mapping(
            BaseMappingType.YARN,
            constructor,
            type,
            info[order.get(0)],
            intermediate,
            name,
            minecraftVersion,
            null);
      case METHOD:
      case FIELD:
        intermediate = info[order.get(1) + 2];
        name = info[order.get(2) + 2];
        Mapping mapping =
            new Mapping(
                BaseMappingType.YARN,
                constructor,
                type,
                info[order.get(0) + 2],
                intermediate,
                name,
                minecraftVersion,
                null);
        mapping.getObfuscatedProperties().put("owner", info[1]);
        mapping.getObfuscatedProperties().put("description", info[2]);
        return mapping;
      default:
        throw new IllegalArgumentException("Unknown type");
    }
  }
}
