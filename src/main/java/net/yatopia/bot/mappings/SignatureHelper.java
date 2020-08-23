package net.yatopia.bot.mappings;

import clojure.asm.Type;
import java.util.List;
import java.util.regex.Pattern;

/** @author tterrag1098 */
public class SignatureHelper {

  private static final Pattern NOTCH_PARAM = Pattern.compile("([a-z]+\\\\$)*([a-z]+|\\\\d+)");

  public static String mapSignature(
      NameType nameType, String sig, String mcVer, MappingParser parser) {
    Type ret = Type.getReturnType(sig);
    Type[] args = Type.getArgumentTypes(sig);
    for (int i = 0; i < args.length; i++) {
      args[i] = mapType(nameType, args[i], mcVer, parser);
    }
    ret = mapType(nameType, ret, mcVer, parser);
    return Type.getMethodDescriptor(ret, args);
  }

  public static Type mapType(
      NameType nameType, String original, String mcVer, MappingParser parser) {
    return mapType(nameType, Type.getObjectType(original), mcVer, parser);
  }

  public static Type mapType(NameType nameType, Type original, String mcVer, MappingParser parser) {
    Type type = original;
    if (original.getSort() == Type.ARRAY) {
      type = type.getElementType();
    }
    if (type.getSort() == Type.OBJECT) {
      String name = type.getInternalName();
      Mapping match = null;
      if (NOTCH_PARAM.matcher(name).matches()) {
        List<Mapping> found =
            parser.parseMappingExact(NameType.ORIGINAL, MappingType.CLASS, mcVer, name);
        for (Mapping f : found) {
          if (f.getObfuscated().equals(name)) {
            match = f;
            break;
          }
        }
      } else {
        List<Mapping> found = parser.parseMapping(MappingType.CLASS, mcVer, name);
        match = found.isEmpty() ? null : found.get(0);
      }
      if (match != null) {
        String mappedName = nameType.get(match);
        if (mappedName == null && nameType == NameType.NAME) {
          mappedName = NameType.INTERMEDIATE.get(match);
        }
        type = Type.getType("L" + mappedName + ";");
      }
      if (original.getSort() == Type.ARRAY) {
        type = Type.getType(repeat('[', original.getDimensions()) + type.getDescriptor());
      }
      return type;
    } else {
      return original;
    }
  }

  private static String repeat(char ch, int count) {
    StringBuilder buffer = new StringBuilder();

    for (int i = 0; i < count; ++i) {
      buffer.append(ch);
    }

    return buffer.toString();
  }
}
