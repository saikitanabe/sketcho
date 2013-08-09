package net.sevenscales.server.domain;

import java.util.Comparator;

public class PageComparator implements Comparator<Page> {
//    @Override
    public int compare(Page o1, Page o2) {
      int result = o1.getOrderValue() - o2.getOrderValue();
      if (result == 0) {
        if (o1.getId() - o2.getId() == 0) {
          result = 0;
        } else {
          result = o1.getId() - o2.getId() > 0 ? 1 : -1;
        }
      }
      return result;
    }
}
