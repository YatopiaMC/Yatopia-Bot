package net.yatopia.bot.mappings;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.jetbrains.annotations.Nullable;

public final class Mapping {

  private final BaseMappingType baseType;
  private final MappingType mappingType;
  private final String obfuscated, intermediate, name, minecraftVersion, obfuscatedDescription;
  private final Mapping parentMapping;
  private final MappingParser constructor;

  private LoadingCache<NameType, String> descriptionRemapped =
      Caffeine.newBuilder()
          .build(
              new CacheLoader<NameType, String>() {
                @Override
                public String load(NameType nameType) {
                  if (obfuscatedDescription == null) {
                    return null;
                  }
                  if (baseType == BaseMappingType.SPIGOT) {
                    // spigot doesn't provide obfuscated descriptions
                    // or well, we have to parse them ourselves, but they're basically useless
                    return obfuscatedDescription;
                  }
                  if (obfuscatedDescription.contains("(")) {
                    return SignatureHelper.mapSignature(
                        nameType, obfuscatedDescription, minecraftVersion, constructor);
                  } else {
                    return SignatureHelper.mapType(
                            nameType, obfuscatedDescription, minecraftVersion, constructor)
                        .getDescriptor();
                  }
                }
              });

  public Mapping(
      BaseMappingType baseType,
      MappingParser constructor,
      MappingType mappingType,
      String obfuscated,
      @Nullable String intermediate,
      String name,
      String minecraftVersion,
      @Nullable String obfuscatedDescription,
      @Nullable Mapping parentMapping) {
    this.baseType = baseType;
    this.constructor = constructor;
    this.mappingType = mappingType;
    this.obfuscated = obfuscated;
    this.intermediate = intermediate;
    this.name = name;
    this.minecraftVersion = minecraftVersion;
    this.obfuscatedDescription = obfuscatedDescription;
    this.parentMapping = parentMapping;
  }

  public BaseMappingType getBaseType() {
    return baseType;
  }

  public MappingParser getConstructor() {
    return constructor;
  }

  public MappingType getMappingType() {
    return mappingType;
  }

  @Nullable
  public String getOwner() {
    return parentMapping == null ? null : parentMapping.getObfuscated();
  }

  @Nullable
  public String getOwner(NameType nameType) {
    return parentMapping == null ? null : nameType.get(parentMapping);
  }

  @Nullable
  public String getDescription() {
    return obfuscatedDescription;
  }

  @Nullable
  public String getDescription(NameType nameType) {
    return descriptionRemapped.get(nameType);
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
    if (intermediate != null) { // spigot doesn't have intermediary mappings
      builder.append("Intermediary: ").append("`").append(intermediate).append("` ;\n");
    }
    builder.append("Named: ").append("`").append(name);
    String description = getDescription(NameType.NAME);
    if (description != null) {
      builder.append(description);
    }
    builder.append("`");
    return builder.toString();
  }
}
