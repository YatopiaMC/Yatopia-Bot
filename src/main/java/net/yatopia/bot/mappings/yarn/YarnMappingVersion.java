package net.yatopia.bot.mappings.yarn;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public final class YarnMappingVersion {

  private final String gameVersion, separator;
  private final int build;
  private final String maven, version;
  private final boolean stable;

  @JsonCreator
  public YarnMappingVersion(
      @JsonProperty("gameVersion") String gameVersion,
      @JsonProperty("separator") String separator,
      @JsonProperty("build") int build,
      @JsonProperty("maven") String maven,
      @JsonProperty("version") String version,
      @JsonProperty("stable") boolean stable) {
    this.gameVersion = gameVersion;
    this.separator = separator;
    this.build = build;
    this.maven = maven;
    this.version = version;
    this.stable = stable;
  }

  public String getGameVersion() {
    return gameVersion;
  }

  public String getSeparator() {
    return separator;
  }

  public int getBuild() {
    return build;
  }

  public String getMaven() {
    return maven;
  }

  public String getVersion() {
    return version;
  }

  public boolean isStable() {
    return stable;
  }

  @JsonIgnore
  public String getMavenUrl(String base, String classifier, String ext) {
    String[] maven = getMaven().split(":");
    return base
        + maven[0].replace(".", "/")
        + "/"
        + maven[1]
        + "/"
        + maven[2]
        + "/yarn-"
        + maven[2]
        + "-"
        + classifier
        + "."
        + ext;
  }
}
