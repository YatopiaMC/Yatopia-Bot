package net.yatopia.bot.mappings.yarn;

import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipError;
import net.yatopia.bot.mappings.BaseMappingType;
import net.yatopia.bot.mappings.Mapping;
import net.yatopia.bot.mappings.MappingParser;
import net.yatopia.bot.mappings.MappingType;
import net.yatopia.bot.mappings.NameType;
import org.apache.commons.lang3.StringEscapeUtils;

public class TinyV2Parser {

  public static final TinyV2Parser INSTANCE = new TinyV2Parser();

  public List<Mapping> parse(File mappingFile, String minecraftVersion, MappingParser constructor)
      throws IOException {
    URI uri = URI.create("jar:" + mappingFile.toPath().toUri());
    try {
      try (FileSystem fs = FileSystems.newFileSystem(uri, Collections.emptyMap())) {
        Path mappings = fs.getPath("mappings", "mappings.tiny");
        return parseV2(Files.readAllLines(mappings), minecraftVersion, constructor);
      }
    } catch (ZipError error) {
      throw new RuntimeException(error);
    }
  }

  private static class PartialMapping {

    private final MappingType type;
    private final String minecraftVersion;
    private final MappingParser constructor;

    public PartialMapping(MappingType type, String minecraftVersion, MappingParser constructor) {
      this.type = type;
      this.minecraftVersion = minecraftVersion;
      this.constructor = constructor;
    }

    private String original;
    private String intermediate;
    private String name;
    private String owner;
    private String description;
    private PartialMapping parent;

    public MappingType type() {
      return type;
    }

    public String original() {
      return original;
    }

    public PartialMapping original(String original) {
      this.original = original;
      return this;
    }

    public String intermediate() {
      return intermediate;
    }

    public PartialMapping intermediate(String intermediate) {
      this.intermediate = intermediate;
      return this;
    }

    public String name() {
      return name;
    }

    public PartialMapping name(String name) {
      this.name = name;
      return this;
    }

    public String owner() {
      return owner;
    }

    public PartialMapping owner(String owner) {
      this.owner = owner;
      return this;
    }

    public String desc() {
      return description;
    }

    public PartialMapping desc(String description) {
      this.description = description;
      return this;
    }

    public PartialMapping parent() {
      return parent;
    }

    public PartialMapping parent(PartialMapping parent) {
      this.parent = parent;
      return this;
    }

    PartialMapping name(NameType type, String name) {
      switch (type) {
        case ORIGINAL:
          return original(name);
        case INTERMEDIATE:
          return intermediate(name);
        case NAME:
          return name(name);
        default:
          throw new IllegalArgumentException(type.name());
      }
    }

    PartialMapping applyNames(String[] values, Object2IntMap<NameType> order, int start) {
      for (Object2IntMap.Entry<NameType> e : order.object2IntEntrySet()) {
        name(e.getKey(), values[e.getIntValue() + start]);
      }
      return this;
    }

    Mapping bake() {
      Mapping parent = null;
      if (this.parent != null) {
        parent = this.parent.bake();
      }
      Mapping mapping =
          new Mapping(
              BaseMappingType.YARN,
              constructor,
              type,
              original,
              intermediate,
              name,
              minecraftVersion,
              parent);
      mapping.getObfuscatedProperties().put("owner", owner);
      mapping.getObfuscatedProperties().put("description", description);
      return mapping;
    }
  }

  private static class Dummy extends PartialMapping {

    public Dummy() {
      super(null, null, null);
    }

    @Override
    Mapping bake() {
      return null;
    }
  }

  private final PartialMapping HEADER = new Dummy();
  private final PartialMapping UNKNOWN = new Dummy();

  private List<Mapping> parseV2(
      List<String> lines, String minecraftVersion, MappingParser constructor) throws IOException {
    List<Mapping> ret = new ArrayList<>();
    Deque<PartialMapping> sections = new LinkedList<>();
    Map<String, String> properties = new HashMap<>();
    Object2IntMap<NameType> names = new Object2IntArrayMap<>(4);
    for (String s : lines) {
      int depth = sections.size();
      for (int i = 0; i < depth; i++) {
        if (s.charAt(0) == '\t') {
          s = s.substring(1);
        } else {
          Mapping mapping = sections.pop().bake();
          if (mapping != null) {
            ret.add(mapping);
          }
        }
      }
      depth = sections.size();
      String[] values = s.split("\t", -1);
      if (properties.containsKey("escaped-names")) {
        for (int i = 0; i < values.length; i++) {
          values[i] = StringEscapeUtils.unescapeJava(values[i]);
        }
      }
      PartialMapping context = depth == 0 ? UNKNOWN : sections.peek();
      switch (values[0]) {
        case "tiny":
          sections.push(HEADER);
          int maj = Integer.parseInt(values[1]);
          int min = Integer.parseInt(values[2]);
          if (maj != 2) {
            throw new IllegalStateException("Unsupported tiny format: " + maj + "." + min);
          }
          for (int i = 3; i < values.length; i++) {
            names.put(TinyV1Parser.BY_NAME.get(values[i]), i - 3);
          }
          break;
        case "c":
          if (depth == 0) {
            sections.push(
                new PartialMapping(MappingType.CLASS, minecraftVersion, constructor)
                    .applyNames(values, names, 1));
          }
          break;
        case "m":
          sections.push(
              new PartialMapping(MappingType.METHOD, minecraftVersion, constructor)
                  .desc(values[1])
                  .owner(context.original())
                  .parent(context)
                  .applyNames(values, names, 2));
          break;
        case "p":
          // we're not getting property mappings as spigot doesn't have them.
          break;
        case "f":
          sections.push(
              new PartialMapping(MappingType.FIELD, minecraftVersion, constructor)
                  .desc(values[1])
                  .owner(context.original())
                  .parent(context)
                  .applyNames(values, names, 2));
          break;
        case "v":
          break; // No variable mappings (yet?)
        default:
          if (depth == 1 && context == HEADER) { // properties
            properties.put(values[1], values.length > 2 ? values[2] : null);
          } else {
            sections.push(UNKNOWN);
          }
      }
    }
    // Must add mappings in this order since params can reference methods/fields/classes
    ret.sort(Comparator.comparing(Mapping::getMappingType));
    return ret;
  }
}
