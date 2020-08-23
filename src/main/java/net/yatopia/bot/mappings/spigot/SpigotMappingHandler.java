package net.yatopia.bot.mappings.spigot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.yatopia.bot.mappings.Mapping;
import net.yatopia.bot.mappings.MappingParser;
import net.yatopia.bot.mappings.MappingType;
import net.yatopia.bot.mappings.NameType;
import net.yatopia.bot.mappings.NoSuchVersionException;
import net.yatopia.bot.util.TriPredicate;
import net.yatopia.bot.util.Utils;
import org.apache.commons.io.IOUtils;
import org.jetbrains.annotations.Nullable;

public final class SpigotMappingHandler implements MappingParser {

  private Map<String, List<Mapping>> perVersion = new ConcurrentHashMap<>();

  public SpigotMappingHandler() {
    File baseFolder = new File("." + File.separator + "data", "spigot");
    if (!baseFolder.exists()) {
      baseFolder.mkdirs();
    }
    for (SpigotMappingVersion version : SpigotMappingVersion.values()) {
      try {
        File mappingsFolder = new File(baseFolder, version.getMcVersion());
        File classMappingsFile =
            new File(mappingsFolder, "bukkit-" + version.getMcVersion() + "-cl.csrg");
        File memberMappingsFile =
            new File(mappingsFolder, "bukkit-" + version.getMcVersion() + "-members.csrg");
        if (mappingsFolder.exists()) {
          perVersion.put(
              version.getMcVersion(),
              SpigotMappingParser.parse(
                  classMappingsFile, memberMappingsFile, version.getMcVersion(), this));
          continue;
        }
        mappingsFolder.mkdirs();
        classMappingsFile.createNewFile();
        memberMappingsFile.createNewFile();
        URL url = new URL(version.getClassMappings());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", "Yatopia-Bot");
        try (InputStream in = connection.getInputStream()) {
          try (OutputStream out = new FileOutputStream(classMappingsFile)) {
            IOUtils.copy(in, out);
          }
        }
        connection.disconnect();
        url = new URL(version.getMemberMappings());
        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.addRequestProperty("User-Agent", "Yatopia-Bot");
        try (InputStream in = connection.getInputStream()) {
          try (OutputStream out = new FileOutputStream(memberMappingsFile)) {
            IOUtils.copy(in, out);
          }
        }
        perVersion.put(
            version.getMcVersion(),
            SpigotMappingParser.parse(
                classMappingsFile, memberMappingsFile, version.getMcVersion(), this));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
  }

  @Override
  public List<Mapping> parseMapping(MappingType type, String mcVer, String input)
      throws NoSuchVersionException {
    return parseMappings(null, type, mcVer, input, Utils.ENDS_WITH);
  }

  @Override
  public List<Mapping> parseMappingExact(
      NameType nameType, MappingType mappingType, String mcVer, String input)
      throws NoSuchVersionException {
    return parseMappings(nameType, mappingType, mcVer, input, Utils.EXACT);
  }

  private List<Mapping> parseMappings(
      @Nullable NameType nameType,
      MappingType mappingType,
      String mcVer,
      String input,
      TriPredicate<NameType, Mapping, String> filter)
      throws NoSuchVersionException {
    List<Mapping> mappings = perVersion.get(mcVer);
    if (mappings == null) {
      throw new NoSuchVersionException(mcVer);
    }
    return Utils.parseMappings(mappings, nameType, mappingType, input, filter);
  }
}
