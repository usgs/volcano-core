package gov.usgs.volcanoes.core.legacy.ew;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class that holds the information from an Earthworm Wave Server MENU
 * request.
 *
 * @author Dan Cervelli
 */
public class Menu {
  private static final String MENU_DELIMITER = "  ";
  private boolean isScnl;
  private final List<MenuItem> items;

  public Menu(String menu) {
    isScnl = false;
    items = new ArrayList<>();
    parseMenu(menu);
  }

  public boolean channelExists(String channel) {
    for (int i = 0; i < items.size(); i++) {
      final MenuItem mi = items.get(i);
      if (mi.getSCNSCNL("$").equals(channel)) {
        return true;
      }
    }
    return false;
  }

  @Deprecated
  public MenuItem getItem(SCN scn) {
    for (int i = 0; i < items.size(); i++) {
      final MenuItem mi = items.get(i);
      if (mi.isSCN(scn.station, scn.channel, scn.network)) {
        return mi;
      }
    }
    return null;
  }

  public List<MenuItem> getItems() {
    return items;
  }

  public List<MenuItem> getSortedItems() {
    final List<MenuItem> list = new ArrayList<>(items);
    Collections.sort(list);
    return list;
  }

  public boolean isSCNL() {
    return isScnl;
  }

  public int numItems() {
    return items.size();
  }

  private void parseMenu(String menu) {
    // ignore starting delimiter
    menu = menu.substring(menu.indexOf(MENU_DELIMITER) + 2);

    final String[] entries = menu.split(MENU_DELIMITER);
    for (final String entry : entries) {
      items.add(new MenuItem(entry));
    }

    if (items.get(0).location != null) {
      isScnl = true;
    }
  }

  @Override
  public String toString() {
    final StringBuilder sb = new StringBuilder();
    for (final MenuItem mi : items) {
      sb.append(mi.toFullString() + "\n");
    }

    return sb.toString();
  }
}
