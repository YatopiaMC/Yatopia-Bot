package org.yatopiamc.bot.mappings;

import java.util.Collections;
import java.util.List;
import org.yatopiamc.bot.util.Utils;

public final class MappingPaginator {

  private final List<List<Mapping>> pages;

  public MappingPaginator(List<Mapping> mappings) {
    pages = Utils.getPages(mappings, 4);
  }

  public List<Mapping> getPage(int page) {
    try {
      return pages.get(page - 1);
    } catch (IndexOutOfBoundsException e) {
      return Collections.emptyList();
    }
  }

  public boolean hasNext(int current) {
    return pages.size() > current;
  }

  public int pageSize() {
    return pages.size();
  }
}
