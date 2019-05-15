/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.settings;

import com.example.hertl.myapplication.apps.App;

import java.util.Comparator;

/**
 * The comparator class for the applications. Used to sort the application by user settings.
 */
public class AppComparator implements Comparator<App> {

    /**
     * The compare method which is used to sort
     *
     * @param app1 the first app to compare
     * @param app2 the second app to compare
     * @return the result
     */
    @Override
    public int compare(App app1, App app2) {
        String sortBy = Settings.getLSortBy();
        switch (sortBy) {
            case Settings.L_SORTBY_NAME:
                if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_ASCENDING)) {
                    return app1.getLabel().compareToIgnoreCase(app2.getLabel());
                } else if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_DESCENDING)) {
                    return app2.getLabel().compareToIgnoreCase(app1.getLabel());
                }
                return app1.getLabel().compareToIgnoreCase(app2.getLabel());

            case Settings.L_SORTBY_PACKAGE:
                if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_ASCENDING)) {
                    return app1.getPackageName().compareTo(app2.getPackageName());
                } else if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_DESCENDING)) {
                    return app2.getPackageName().compareTo(app1.getPackageName());
                }
                return app1.getPackageName().compareTo(app2.getPackageName());

            case Settings.L_SORTBY_APKSIZE:
                if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_ASCENDING)) {
                    return Long.valueOf(app1.getApkSize()).compareTo(app2.getApkSize()); // warning Long.compare from API 19
                } else if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_DESCENDING)) {
                    return Long.valueOf(app2.getApkSize()).compareTo(app1.getApkSize()); // warning Long.compare from API 19
                }
                return Long.valueOf(app1.getApkSize()).compareTo(app2.getApkSize());

            case Settings.L_SORTBY_INSTALL:
                if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_ASCENDING)) {
                    return Long.valueOf(app1.getInstalled()).compareTo(app2.getInstalled());
                } else if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_DESCENDING)) {
                    return Long.valueOf(app2.getInstalled()).compareTo(app1.getInstalled());
                }
                return Long.valueOf(app1.getInstalled()).compareTo(app2.getInstalled());

            case Settings.L_SORTBY_UPDATE:
                if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_ASCENDING)) {
                    return Long.valueOf(app1.getUpdated()).compareTo(app2.getUpdated());
                } else if (Settings.getLSortOrder().equals(Settings.L_SORTORDER_DESCENDING)) {
                    return Long.valueOf(app2.getUpdated()).compareTo(app1.getUpdated());
                }
                return Long.valueOf(app1.getUpdated()).compareTo(app2.getUpdated());

            default:
                return app1.getLabel().compareToIgnoreCase(app2.getLabel());
        }
    }
}
