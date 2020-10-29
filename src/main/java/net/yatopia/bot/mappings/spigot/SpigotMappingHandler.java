package net.yatopia.bot.mappings.spigot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.yatopia.bot.mappings.Mapping;
import net.yatopia.bot.mappings.MappingParser;
import net.yatopia.bot.mappings.MappingType;
import net.yatopia.bot.mappings.NoSuchVersionException;
import net.yatopia.bot.util.Utils;
import okhttp3.Call;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;

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
        Call classCall = Utils.newCall(Utils.newRequest(version.getClassMappings()));
        try (Response response = classCall.execute()) {
          try (InputStream in = response.body().byteStream()) {
            try (OutputStream out = new FileOutputStream(classMappingsFile)) {
              IOUtils.copy(in, out);
            }
          }
        }
        Call membersCall = Utils.newCall(Utils.newRequest(version.getMemberMappings()));
        try (Response response = membersCall.execute()) {
          try (InputStream in = response.body().byteStream()) {
            try (OutputStream out = new FileOutputStream(memberMappingsFile)) {
              IOUtils.copy(in, out);
            }
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
    List<Mapping> mappings = perVersion.get(mcVer);
    if (mappings == null) {
      throw new NoSuchVersionException(mcVer);
    }
    return Utils.parseMappings(mappings, type, input);
  }

  @Override
  public List<Mapping> getAllMappings(String mcVer) throws NoSuchVersionException {
    List<Mapping> mappings = perVersion.get(mcVer);
    if (mappings == null) {
      throw new NoSuchVersionException(mcVer);
    }
    return Collections.unmodifiableList(mappings);
  }
}
