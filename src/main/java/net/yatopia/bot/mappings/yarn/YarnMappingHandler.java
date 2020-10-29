package net.yatopia.bot.mappings.yarn;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
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

public final class YarnMappingHandler implements MappingParser {

  private enum TinyType {
    V1,
    V2
  }

  private LoadingCache<String, List<Mapping>> mappingCache =
      Caffeine.newBuilder().expireAfterWrite(Duration.ofHours(4)).build(this::downloadForMcVersion);
  private Map<String, YarnMappingVersion> currentVersions = new ConcurrentHashMap<>();

  private List<Mapping> downloadForMcVersion(String mcVer) throws IOException {
    File dataFolder = new File(".", "data");
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    File mappingsFolder = new File(dataFolder + File.separator + "yarn" + File.separator + mcVer);
    if (currentVersions.get(mcVer) == null && mappingsFolder.exists()) {
      File version = new File(mappingsFolder, ".dataversion");
      if (version.exists()) {
        try (BufferedReader reader = new BufferedReader(new FileReader(version))) {
          currentVersions.put(mcVer, Utils.JSON_MAPPER.readValue(reader, YarnMappingVersion.class));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      }
    }
    YarnMappingVersion version;
    Call call =
        Utils.newCall(
            Utils.newRequest("https://meta.fabricmc.net/v1/versions/mappings/" + mcVer + "/"));
    try (Response response = call.execute()) {
      try (InputStream in = response.body().byteStream()) {
        YarnMappingVersion[] versions = Utils.JSON_MAPPER.readValue(in, YarnMappingVersion[].class);
        if (versions == null || versions.length == 0) {
          return null;
        }
        version = versions[0];
      }
    }
    YarnMappingVersion current = currentVersions.get(mcVer);
    if (current != null && current.getBuild() == version.getBuild()) {
      TinyType mappingsType = TinyType.V1;
      String[] maven = current.getMaven().split(":");
      String fileName = "yarn-" + maven[2] + "-mergedv2.jar";
      File file = mappingsFolder.toPath().resolve(fileName).toFile();
      if (file.exists()) {
        mappingsType = TinyType.V2;
      } else {
        fileName = "yarn-" + maven[2] + "-tiny.gz";
        file = mappingsFolder.toPath().resolve(fileName).toFile();
      }
      List<Mapping> mappings;
      if (mappingsType == TinyType.V1) {
        mappings = TinyV1Parser.INSTANCE.parse(file, mcVer, this);
      } else {
        mappings = TinyV2Parser.INSTANCE.parse(file, mcVer, this);
      }
      return mappings;
    }
    try {
      String mappingsUrl = version.getMavenUrl("https://maven.fabricmc.net/", "mergedv2", "jar");
      Call v2Call = Utils.newCall(Utils.newRequest(mappingsUrl));
      Response v2Response = v2Call.execute();
      if (isBadCode(v2Response.code())) {
        // v1 mappings
        v2Response.close();
        mappingsUrl = version.getMavenUrl("https://maven.fabricmc.net/", "tiny", "gz");
        Call v1Call = Utils.newCall(Utils.newRequest(mappingsUrl));
        try (Response v1Response = v1Call.execute()) {
          if (mappingsFolder.exists()) {
            mappingsFolder.delete();
          }
          mappingsFolder.mkdirs();
          String fileName = mappingsUrl.substring(mappingsUrl.lastIndexOf('/') + 1);
          File mappingFile = mappingsFolder.toPath().resolve(fileName).toFile();
          try (OutputStream out = new FileOutputStream(mappingFile)) {
            try (InputStream in = v1Response.body().byteStream()) {
              IOUtils.copy(in, out);
            }
          }
          return TinyV1Parser.INSTANCE.parse(mappingFile, mcVer, this);
        }
      } else {
        // v2 mappings
        if (mappingsFolder.exists()) {
          mappingsFolder.delete();
        }
        mappingsFolder.mkdirs();
        String fileName = mappingsUrl.substring(mappingsUrl.lastIndexOf('/') + 1);
        File mappingFile = mappingsFolder.toPath().resolve(fileName).toFile();
        try (OutputStream out = new FileOutputStream(mappingFile)) {
          try (InputStream in = v2Response.body().byteStream()) {
            IOUtils.copy(in, out);
          }
        } finally {
          v2Response.close();
        }
        return TinyV2Parser.INSTANCE.parse(mappingFile, mcVer, this);
      }
    } finally {
      File versionFile = new File(mappingsFolder, ".dataversion");
      if (versionFile.exists()) {
        versionFile.delete();
      }
      versionFile.createNewFile();
      try (Writer writer =
          new OutputStreamWriter(new FileOutputStream(versionFile), StandardCharsets.UTF_8)) {
        Utils.JSON_MAPPER.writeValue(writer, version);
      }
    }
  }

  private boolean isBadCode(int code) {
    return code == 404 || code == 400 || code == 500 || code == 403;
  }

  @Override
  public List<Mapping> parseMapping(MappingType type, String mcVer, String input)
      throws NoSuchVersionException {
    List<Mapping> mappings = mappingCache.get(mcVer);
    if (mappings == null) {
      throw new NoSuchVersionException(mcVer);
    }
    return Utils.parseMappings(mappings, type, input);
  }

  @Override
  public List<Mapping> getAllMappings(String mcVer) throws NoSuchVersionException {
    List<Mapping> mappings = mappingCache.get(mcVer);
    if (mappings == null) {
      throw new NoSuchVersionException(mcVer);
    }
    return Collections.unmodifiableList(mappings);
  }
}
