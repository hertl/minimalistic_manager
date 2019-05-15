/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.settings;

import java.io.File;

/**
 * The class to keep static and static final information mostly about the user settings.
 */
public class Settings {

    /**
     * Files constants.
     */
    public static final String CLIPBOARD_FILE = "clipboard.txt";
    public static final String FAVORITEAPPS_FILE = "favoriteApps.txt";
    public static final String MANAGER_FILE = "manager.properties";
    public static final String LAUNCHER_FILE = "launcher.properties";

    /**
     * Constants for manager settings_manager.
     */
    public static final String ROOTPATH = "rootPath";


    public static final String SORTBY = "sortBy";

    public static final String SORTBY_NAME = "name";
    public static final String SORTBY_DATE = "date";
    public static final String SORTBY_SIZE = "size";
    public static final String SORTBY_EXTENSION = "extension";

    public static final String SORTBY_DEFAULT = SORTBY_NAME;


    public static final String SORTORDER = "sortOrder";

    public static final String SORTORDER_ASCENDING = "ascending";
    public static final String SORTORDER_DESCENDING = "descending";

    public static final String SORTORDER_DEFAULT = SORTORDER_ASCENDING;


    public static final String SHOWEXTENSION = "showExtension";

    public static final String SHOWEXTENSION_YES = "yes";
    public static final String SHOWEXTENSION_NO = "no";

    public static final String SHOWEXTENSION_DEFAULT = SHOWEXTENSION_YES;


    public static final String SHOWHIDDEN = "showHidden";

    public static final String SHOWHIDDEN_YES = "yes";
    public static final String SHOWHIDDEN_NO = "no";

    public static final String SHOWHIDDEN_DEFAULT = SHOWHIDDEN_NO;


    public static final String SEPARATE = "separate";
    public static final String SEPARATE_YES = "yes";
    public static final String SEPARATE_NO = "no";

    public static final String SEPARATE_DEFAULT = SEPARATE_YES;


    public static final String TRUNCATESIZE = "truncateSize";

    public static final String TRUNCATESIZE_YES = "yes";
    public static final String TRUNCATESIZE_NO = "no";

    public static final String TRUNCATESIZE_DEFAULT = TRUNCATESIZE_YES;
    /**
     * Constants for launcher settings_manager.
     */
    public static final String L_SORTBY = "sortBy";
    public static final String L_SORTBY_NAME = "name";
    public static final String L_SORTBY_PACKAGE = "package";
    public static final String L_SORTBY_INSTALL = "install";
    public static final String L_SORTBY_UPDATE = "update";
    public static final String L_SORTBY_APKSIZE = "apkSize";
    public static final String L_SORTBY_DEFAULT = L_SORTBY_NAME;
    public static final String L_SORTORDER = "sortOrder";
    public static final String L_SORTORDER_ASCENDING = "ascending";
    public static final String L_SORTORDER_DESCENDING = "descending";
    public static final String L_SORTORDER_DEFAULT = L_SORTORDER_ASCENDING;
    public static final String L_SHOWTYPE = "showType";
    public static final String L_SHOWTYPE_ALL = "all";
    public static final String L_SHOWTYPE_SYSTEM = "system";
    public static final String L_SHOWTYPE_INSTALLED = "installed";
    public static final String L_SHOWTYPE_DEFAULT = L_SHOWTYPE_ALL;
    public static final String L_SHOWGROUPS = "showGroups";
    public static final String L_SHOWGROUPS_NO = "no";
    public static final String L_SHOWGROUPS_SYSTEM = "system";
    public static final String L_SHOWGROUPS_FAVORITE = "favorite";
    public static final String L_SHOWGROUPS_DEFAULT = L_SHOWGROUPS_NO;
    public static final String L_TRUNCATESIZE = "truncateSize";
    public static final String L_TRUNCATESIZE_YES = "yes";
    public static final String L_TRUNCATESIZE_NO = "no";
    public static final String L_TRUNCATESIZE_DEFAULT = L_TRUNCATESIZE_YES;
    /**
     * Settings variables for manager.
     */
    private static String sortBy = SORTBY_DEFAULT;
    private static String sortOrder = SORTORDER_DEFAULT;
    private static String showExtension = SHOWEXTENSION_DEFAULT;
    private static String showHidden = SHOWHIDDEN_DEFAULT;
    private static String rootPath = File.separator;
    private static String separate = SEPARATE_DEFAULT;
    private static String truncateSize = TRUNCATESIZE_DEFAULT;
    /**
     * If root path is changed, this variable becomes true. If user returns to the
     * manager activity, current path gets updated
     */
    private static boolean recentlyUpdatedRootPath = false;
    /**
     * Settings variables for launcher.
     */
    private static String lSortBy = L_SORTBY_DEFAULT;
    private static String lSortOrder = L_SORTORDER_DEFAULT;
    private static String lShowType = L_SHOWTYPE_DEFAULT;
    private static String lShowGroups = L_SHOWGROUPS_DEFAULT;
    private static String lTruncateSize = L_TRUNCATESIZE_DEFAULT;

    /**
     * Setters and getters for manager settings_manager variables.
     */
    public static String getSortBy() {
        return sortBy;
    }

    public static void setSortBy(String sortBy) {
        Settings.sortBy = sortBy;
    }

    public static String getSortOrder() {
        return sortOrder;
    }

    public static void setSortOrder(String sortOrder) {
        Settings.sortOrder = sortOrder;
    }

    public static String getShowExtension() {
        return showExtension;
    }

    public static void setShowExtension(String showExtension) {
        Settings.showExtension = showExtension;
    }

    public static String getShowHidden() {
        return showHidden;
    }

    public static void setShowHidden(String showHidden) {
        Settings.showHidden = showHidden;
    }

    public static String getRootPath() {
        return rootPath;
    }

    public static void setRootPath(String rootPath) {
        Settings.rootPath = rootPath;
        Settings.recentlyUpdatedRootPath = true;
    }

    public static boolean isRecentlyUpdatedRootPath() {
        return recentlyUpdatedRootPath;
    }

    public static void setRecentlyUpdatedRootPath(boolean recentlyUpdatedRootPath) {
        Settings.recentlyUpdatedRootPath = recentlyUpdatedRootPath;
    }

    public static String getSeparate() {
        return separate;
    }

    public static void setSeparate(String separate) {
        Settings.separate = separate;
    }

    public static String getTruncateSize() {
        return truncateSize;
    }

    public static void setTruncateSize(String truncateSize) {
        Settings.truncateSize = truncateSize;
    }

    /**
     * Setters and getters for launcher settings_manager variables.
     */
    public static String getLSortBy() {
        return lSortBy;
    }

    public static void setLSortBy(String lSortBy) {
        Settings.lSortBy = lSortBy;
    }

    public static String getLSortOrder() {
        return lSortOrder;
    }

    public static void setLSortOrder(String lSortOrder) {
        Settings.lSortOrder = lSortOrder;
    }

    public static String getLShowType() {
        return lShowType;
    }

    public static void setLShowType(String lShowType) {
        Settings.lShowType = lShowType;
    }

    public static String getLShowGroups() {
        return lShowGroups;
    }

    public static void setLShowGroups(String lShowGroups) {
        Settings.lShowGroups = lShowGroups;
    }

    public static String getLTruncateSize() {
        return lTruncateSize;
    }

    public static void setLTruncateSize(String lTruncateSize) {
        Settings.lTruncateSize = lTruncateSize;
    }
}
