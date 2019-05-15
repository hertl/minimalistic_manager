/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.settings;

import com.example.hertl.myapplication.items.item.Item;

import java.util.Comparator;

/**
 * The comparator class for the items. Used to sort the items by user settings.
 */
public class ItemComparator implements Comparator<Item> {

    /**
     * The compare method which is used to sort
     *
     * @param item1 the first item to compare
     * @param item2 the second item to compare
     * @return the result
     */
    @Override
    public int compare(Item item1, Item item2) {
        String sortBy = Settings.getSortBy();
        switch (sortBy) {
            case Settings.SORTBY_NAME:
                if (Settings.getSortOrder().equals(Settings.SORTORDER_ASCENDING)) {
                    return item1.getName().compareToIgnoreCase(item2.getName());
                } else if (Settings.getSortOrder().equals(Settings.SORTORDER_DESCENDING)) {
                    return item2.getName().compareToIgnoreCase(item1.getName());
                }
                return item1.getName().compareToIgnoreCase(item2.getName());

            case Settings.SORTBY_DATE:
                if (Settings.getSortOrder().equals(Settings.SORTORDER_ASCENDING)) {
                    return item1.getLastModified().compareTo(item2.getLastModified());
                } else if (Settings.getSortOrder().equals(Settings.SORTORDER_DESCENDING)) {
                    return item2.getLastModified().compareTo(item1.getLastModified());

                }
                return item2.getLastModified().compareTo(item1.getLastModified());

            case Settings.SORTBY_SIZE:
                if (Settings.getSortOrder().equals(Settings.SORTORDER_ASCENDING)) {
                    return Long.valueOf(item1.getSize()).compareTo(item2.getSize()); // warning Long.compare from API 19
                } else if (Settings.getSortOrder().equals(Settings.SORTORDER_DESCENDING)) {
                    return Long.valueOf(item2.getSize()).compareTo(item1.getSize()); // warning Long.compare from API 19
                }
                return Long.valueOf(item1.getSize()).compareTo(item2.getSize());

            case Settings.SORTBY_EXTENSION:
                if (Settings.getSortOrder().equals(Settings.SORTORDER_ASCENDING)) {
                    return item1.getExt().compareToIgnoreCase(item2.getExt());
                } else if (Settings.getSortOrder().equals(Settings.SORTORDER_DESCENDING)) {
                    return item2.getExt().compareToIgnoreCase(item1.getExt());
                }
                return item1.getExt().compareToIgnoreCase(item2.getExt());

            default:
                return item1.getName().compareToIgnoreCase(item2.getName());
        }
    }
}
