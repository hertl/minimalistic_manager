/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.settings;

import java.util.Locale;

/**
 * Class for size truncation if requested.
 */
public class SizeTruncator {
    /**
     * Constructor, which will set final variables to the created object.
     *
     * @param size the size to be finalized
     */
    public static String sizeToString(long size, boolean isManager) {
        boolean doTruncate;
        if (isManager) {
            doTruncate = Settings.getTruncateSize().equals(Settings.TRUNCATESIZE_YES);
        } else {
            doTruncate = Settings.getLTruncateSize().equals(Settings.L_TRUNCATESIZE_YES);
        }
        if (doTruncate) { // Truncation requested
            double d;
            if (size < 1000) { // Size smaller than 1KB, display as eg. 700 B
                return Long.toString(size) + " B";
            } else if (size < 10000) { // Size smaller than 10KB, display as eg. 5,54 KB
                d = (size / 1000D);
                return String.format(Locale.getDefault(), "%.2f KB", d);

            } else if (size < 100000) { // Size smaller than 100KB, display as eg. 80,3 KB
                d = (size / 1000D);
                return String.format(Locale.getDefault(), "%.1f KB", d);

            } else if (size < 1000000) { // Size smaller than 1MB, display as eg. 700 KB
                d = (size / 1000D);
                return String.format(Locale.getDefault(), "%.0f KB", d);

            } else if (size < 10000000) { // Size smaller than 10MB, display as eg. 5,54 MB
                d = (size / 1000000D);
                return String.format(Locale.getDefault(), "%.2f MB", d);

            } else if (size < 100000000) { // Size smaller than 100MB, display as eg. 80,3 MB
                d = (size / 1000000D);
                return String.format(Locale.getDefault(), "%.1f MB", d);

            } else if (size < 1000000000) { // Size smaller than 1GB, display as eg. 700 MB
                d = (size / 1000000D);
                return String.format(Locale.getDefault(), "%.0f MB", d);

            } else if (size < 10000000000L) { // Size smaller than 10GB, display as eg. 5,54 GB
                d = (size / 1000000000D);
                return String.format(Locale.getDefault(), "%.2f GB", d);

            } else if (size < 100000000000L) { // Size smaller than 100GB, display as eg. 80,3 GB
                d = (size / 1000000000D);
                return String.format(Locale.getDefault(), "%.1f GB", d);

            } else { // Size larger than 100GB, display as eg. 700GB
                d = (size / 1000000000D);
                return String.format(Locale.getDefault(), "%.0f GB", d);

            }
        } else { // Display full byte size eg. 23145 B
            return String.valueOf(size) + " B";
        }
    }
}


