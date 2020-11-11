package net.yatopia.bot.mappings.spigot;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.yatopia.bot.mappings.BaseMappingType;
import net.yatopia.bot.mappings.Mapping;
import net.yatopia.bot.mappings.MappingParser;
import net.yatopia.bot.mappings.MappingType;

public final class SpigotMappingParser {

  public static List<Mapping> parse(
      File classMappings, File memberMappings, String mcVer, MappingParser parser)
      throws IOException {
    List<Mapping> mappings = new ArrayList<>();
    List<Mapping> mappingsClass = parseClass(classMappings, mcVer, parser);
    mappings.addAll(mappingsClass);
    mappings.addAll(parseMember(mappingsClass, memberMappings, mcVer, parser));
    return mappings;
  }

  private static List<Mapping> parseClass(File classMappings, String mcVer, MappingParser parser)
      throws IOException {
    List<Mapping> ret = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(classMappings))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] values = line.split(" ");
        ret.add(
            new Mapping(
                BaseMappingType.SPIGOT,
                parser,
                MappingType.CLASS,
                values[0],
                null,
                "net/minecraft/server/" + values[1],
                mcVer,
                null,
                null));
      }
    }
    return ret;
  }

  private static List<Mapping> parseMember(
      List<Mapping> classMappings, File memberMappings, String mcVer, MappingParser parser)
      throws IOException {
    List<Mapping> ret = new ArrayList<>();
    try (BufferedReader reader = new BufferedReader(new FileReader(memberMappings))) {
      String line;
      while ((line = reader.readLine()) != null) {
        String[] values = line.split(" ");
        String owner = values[0];
        String obfuscatedName = values[1];
        String name = values[2];
        if (values.length == 4) {
          name = values[3];
        }
        String description = values.length == 4 ? values[2] : null;
        Mapping parent = null;
        for (Mapping mapping : classMappings) {
          if (mapping.getName().equalsIgnoreCase("net/minecraft/server/" + owner)) {
            parent = mapping;
            break;
          }
        }

        ret.add(
            new Mapping(
                BaseMappingType.SPIGOT,
                parser,
                description != null ? MappingType.METHOD : MappingType.FIELD,
                obfuscatedName,
                null,
                name,
                mcVer,
                description,
                parent));
      }
    }
    return ret;
  }
}
