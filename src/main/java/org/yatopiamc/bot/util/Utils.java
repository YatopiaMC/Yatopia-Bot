package org.yatopiamc.bot.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.yatopiamc.bot.mappings.Mapping;
import org.yatopiamc.bot.mappings.MappingType;
import org.yatopiamc.bot.mappings.NameType;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public final class Utils {

  // cuz jvm complains every time I do NameType.values()
  public static NameType[] NAMETYPE_VALUES = NameType.values();

  public static final ObjectMapper JSON_MAPPER = new ObjectMapper();
  public static final OkHttpClient HTTP_CLIENT = new OkHttpClient();

  public static <T> List<List<T>> getPages(Collection<T> c, int pageSize) {
    List<T> list = new ArrayList<>(c);
    if (pageSize <= 0 || pageSize > list.size()) {
      pageSize = list.size();
    }
    int numPages = (int) Math.ceil((double) list.size() / (double) pageSize);
    List<List<T>> pages = new ArrayList<>(numPages);
    for (int pageNum = 0; pageNum < numPages; ) {
      pages.add(list.subList(pageNum * pageSize, Math.min(++pageNum * pageSize, list.size())));
    }
    return pages;
  }

  public static Call newCall(Request request) {
    return HTTP_CLIENT.newCall(request);
  }

  public static Request newRequest(String url) {
    return newRequestBuilder(url).build();
  }

  public static Request.Builder newRequestBuilder(String url) {
    return new Request.Builder().get().url(url).addHeader("User-Agent", "Yatopia-Bot");
  }

  public static List<Mapping> parseMappings(
      List<Mapping> mappings, MappingType mappingType, String input) {
    String parentSearched = null;
    if (input.indexOf('#') != -1) {
      parentSearched = input.substring(0, input.indexOf('#'));
    }
    if (input.indexOf('.') != -1 && parentSearched == null) {
      parentSearched = input.substring(0, input.indexOf('.'));
    }
    List<Mapping> ret = new ArrayList<>();
    String parentSearchCut =
        parentSearched == null ? null : input.substring(parentSearched.length() + 1);
    if (parentSearched != null) {
      for (Mapping mapping : mappings) {
        if (mapping.getMappingType() == mappingType && mapping.getParentMapping() != null) {
          for (NameType nameType : NAMETYPE_VALUES) {
            String parent = nameType.get(mapping.getParentMapping());
            if (parent != null && parent.endsWith(parentSearched)) {
              // separately lookup
              for (NameType nameType1 : NAMETYPE_VALUES) {
                String t = nameType1.get(mapping);
                if (t != null && t.endsWith(parentSearchCut) && !ret.contains(mapping)) {
                  ret.add(mapping);
                }
              }
            }
          }
        }
      }
    } else {
      for (Mapping mapping : mappings) {
        if (mapping.getMappingType() == mappingType) {
          for (NameType nameType : NAMETYPE_VALUES) {
            String t = nameType.get(mapping);
            if (t != null && t.endsWith(input) && !ret.contains(mapping)) {
              ret.add(mapping);
            }
          }
        }
      }
    }
    return ret;
  }
}
