package net.yatopia.bot.mappings;

import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class Mapping {

  private final MappingType mappingType;
  private final String obfuscated, intermediate, name, minecraftVersion;
  private final Mapping parentMapping;
  private final MappingParser constructor;

  private Map<NameType, String> descriptionRemapped = new HashMap<>();
  private Map<String, String> obfuscatedProperties = new HashMap<>();

  public Mapping(
      MappingParser constructor,
      MappingType mappingType,
      String obfuscated,
      @Nullable String intermediate,
      String name,
      String minecraftVersion,
      @Nullable Mapping parentMapping) {
    this.constructor = constructor;
    this.mappingType = mappingType;
    this.obfuscated = obfuscated;
    this.intermediate = intermediate;
    this.name = name;
    this.minecraftVersion = minecraftVersion;
    this.parentMapping = parentMapping;
  }

  public MappingParser getConstructor() {
    return constructor;
  }

  public MappingType getMappingType() {
    return mappingType;
  }

  public Map<String, String> getObfuscatedProperties() {
    return obfuscatedProperties;
  }

  @Nullable
  public String getOwner() {
    return obfuscatedProperties.get("owner");
  }

  @Nullable
  public String getOwner(NameType nameType) {
    return parentMapping == null ? null : nameType.get(parentMapping);
  }

  @Nullable
  public String getDescription() {
    return obfuscatedProperties.get("description");
  }

  @Nullable
  public String getDescription(NameType nameType) {
    return descriptionRemapped.computeIfAbsent(
        nameType,
        t -> {
          String description = getDescription();
          if (description == null) {
            return null;
          }
          if (description.contains("(")) {
            return SignatureHelper.mapSignature(t, description, minecraftVersion, constructor);
          } else {
            return SignatureHelper.mapType(t, description, minecraftVersion, constructor)
                .getDescriptor();
          }
        });
  }

  public String getObfuscated() {
    return obfuscated;
  }

  @Nullable
  public String getIntermediate() {
    return intermediate;
  }

  public String getName() {
    return name;
  }

  public Mapping getParentMapping() {
    return parentMapping;
  }

  public String getMinecraftVersion() {
    return minecraftVersion;
  }

  public String formatMessage() {
    StringBuilder builder = new StringBuilder();
    if (parentMapping != null) {
      builder.append("Parent ").append("`").append(parentMapping.getName()).append("` ;\n");
    }
    builder.append("Obfuscated: ").append("`").append(obfuscated).append("` ;\n");
    builder.append("Intermediary: ").append("`").append(intermediate).append("` ;\n");
    builder.append("Named: ").append("`").append(name);
    String description = getDescription(NameType.NAME);
    if (description != null) {
      builder.append(description);
    }
    builder.append("`");
    return builder.toString();
  }
}
