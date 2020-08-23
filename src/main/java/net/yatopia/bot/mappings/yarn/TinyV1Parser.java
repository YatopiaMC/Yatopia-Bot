package net.yatopia.bot.mappings.yarn;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;
import net.yatopia.bot.mappings.BaseMappingType;
import net.yatopia.bot.mappings.Mapping;
import net.yatopia.bot.mappings.MappingParser;
import net.yatopia.bot.mappings.MappingType;
import net.yatopia.bot.mappings.NameType;
import org.apache.commons.io.IOUtils;

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
    List<String> lines =
        IOUtils.readLines(
            new GZIPInputStream(new FileInputStream(mappingFile)), StandardCharsets.UTF_8);

    String header = lines.remove(0);
    String[] headerInfo = header.split("\t");
    int tinyver = Integer.parseInt(headerInfo[0].substring(1));
    if (tinyver != 1) {
      throw new IllegalArgumentException("Unsupported mappings version");
    }
    IntList order =
        new IntArrayList(
            Arrays.stream(headerInfo)
                .skip(1)
                .map(BY_NAME::get)
                .mapToInt(t -> t.ordinal() + 1)
                .toArray());

    return lines.stream()
        .map(s -> fromString(s, order, minecraftVersion, constructor))
        .collect(Collectors.toList());
  }

  private Mapping fromString(
      String line, IntList order, String minecraftVersion, MappingParser constructor) {
    String[] info = line.split("\t");
    MappingType type = MappingType.valueOf(info[0]);
    switch (type) {
      case CLASS:
        String intermediate = info[order.getInt(1)];
        String name = info[order.getInt(2)];
        return new Mapping(
            BaseMappingType.YARN,
            constructor,
            type,
            info[order.getInt(0)],
            intermediate,
            name,
            minecraftVersion,
            null);
      case METHOD:
      case FIELD:
        intermediate = info[order.getInt(1) + 2];
        name = info[order.getInt(2) + 2];
        Mapping mapping =
            new Mapping(
                BaseMappingType.YARN,
                constructor,
                type,
                info[order.getInt(0) + 2],
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
