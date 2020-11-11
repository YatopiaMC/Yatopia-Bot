package net.yatopia.bot.mappings.yarn;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
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
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import net.yatopia.bot.mappings.Mapping;
import net.yatopia.bot.mappings.MappingParser;
import net.yatopia.bot.mappings.MappingType;
import net.yatopia.bot.mappings.NoSuchVersionException;
import net.yatopia.bot.util.Utils;
import okhttp3.Call;
import okhttp3.Response;
import org.apache.commons.io.IOUtils;

public final class YarnMappingHandler implements MappingParser {

  private LoadingCache<String, YarnMappingsHolder> mappingCache =
      Caffeine.newBuilder()
          .expireAfter(
              new Expiry<String, YarnMappingsHolder>() {
                @Override
                public long expireAfterCreate(
                    String mcVer, YarnMappingsHolder mappings, long currentTime) {
                  return mappings == null
                      ? TimeUnit.MINUTES.toNanos(5)
                      : (mappings.getTinyType() == TinyType.V1
                          ? Long.MAX_VALUE
                          : TimeUnit.HOURS.toNanos(4));
                }

                @Override
                public long expireAfterUpdate(
                    String s, YarnMappingsHolder yarnMappingsHolder, long l, long currentDuration) {
                  return currentDuration;
                }

                @Override
                public long expireAfterRead(
                    String s, YarnMappingsHolder yarnMappingsHolder, long l, long currentDuration) {
                  return currentDuration;
                }
              })
          .build(this::downloadForMcVersion);
  private Map<String, YarnMappingVersion> currentVersions = new ConcurrentHashMap<>();

  private YarnMappingsHolder downloadForMcVersion(String mcVer) throws IOException {
    File dataFolder = new File(".", "data");
    if (!dataFolder.exists()) {
      dataFolder.mkdirs();
    }
    File mappingsFolder = new File(dataFolder + File.separator + "yarn" + File.separator + mcVer);
    YarnMappingVersion current = currentVersions.get(mcVer);
    if (current == null && mappingsFolder.exists()) {
      File version = new File(mappingsFolder, ".dataversion");
      if (version.exists()) {
        try (BufferedReader reader = new BufferedReader(new FileReader(version))) {
          current = Utils.JSON_MAPPER.readValue(reader, YarnMappingVersion.class);
          currentVersions.put(mcVer, current);
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
      return YarnMappingsHolder.createFor(mappingsType, mappings);
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
          return YarnMappingsHolder.createFor(
              TinyType.V1, TinyV1Parser.INSTANCE.parse(mappingFile, mcVer, this));
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
        return YarnMappingsHolder.createFor(
            TinyType.V1, TinyV2Parser.INSTANCE.parse(mappingFile, mcVer, this));
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
  public void preLoadDownloaded() throws IOException {
    File dataFolder = new File(".", "data");
    if (!dataFolder.exists()) {
      return;
    }
    File yarnFolder = new File(dataFolder + File.separator + "yarn");
    if (!yarnFolder.exists()) {
      return;
    }
    for (File mappingsFolder : yarnFolder.listFiles()) {
      if (!mappingsFolder.isDirectory()) {
        continue;
      }
      String mcVer = mappingsFolder.getName();
      File dataVersionFile = new File(mappingsFolder, ".dataversion");
      if (!dataVersionFile.exists()) {
        continue;
      }
      try (BufferedReader reader = new BufferedReader(new FileReader(dataVersionFile))) {
        YarnMappingVersion mappingVersion =
            Utils.JSON_MAPPER.readValue(reader, YarnMappingVersion.class);
        currentVersions.put(mcVer, mappingVersion);
        TinyType tinyVersion = TinyType.V1;
        String[] maven = mappingVersion.getMaven().split(":");
        String fileName = "yarn-" + maven[2] + "-mergedv2.jar";
        if (Files.exists(mappingsFolder.toPath().resolve(fileName))) {
          tinyVersion = TinyType.V2;
        } else {
          fileName = "yarn-" + maven[2] + "-tiny.gz";
        }
        File mappingsFile = new File(mappingsFolder, fileName);
        List<Mapping> mappings;
        if (tinyVersion == TinyType.V1) {
          mappings = TinyV1Parser.INSTANCE.parse(mappingsFile, mcVer, this);
        } else {
          mappings = TinyV2Parser.INSTANCE.parse(mappingsFile, mcVer, this);
        }
        mappingCache.put(mcVer, YarnMappingsHolder.createFor(tinyVersion, mappings));
      }
    }
  }

  @Override
  public List<Mapping> parseMapping(MappingType type, String mcVer, String input)
      throws NoSuchVersionException {
    YarnMappingsHolder mappings = mappingCache.get(mcVer);
    if (mappings == null) {
      throw new NoSuchVersionException(mcVer);
    }
    return Utils.parseMappings(mappings.getMappings(), type, input);
  }

  @Override
  public List<Mapping> getAllMappings(String mcVer) throws NoSuchVersionException {
    YarnMappingsHolder mappings = mappingCache.get(mcVer);
    if (mappings == null) {
      throw new NoSuchVersionException(mcVer);
    }
    return Collections.unmodifiableList(mappings.getMappings());
  }
}
