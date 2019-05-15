/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.apps;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import java.io.File;

/**
 * Class that represents one installed application in the device.
 */
public class App {
    private String label;
    private String packageName;
    private boolean systemApp;
    private int versionCode;
    private String versionName;
    private long apkSize;
    private int targetSDK;
    private long installed;
    private long updated;
    private String[] permissions;
    private boolean favorite;

    /**
     * Constructor where is the application initialized.
     *
     * @param pm package manager
     * @param ri reslove info
     */
    public App(PackageManager pm, ResolveInfo ri) throws PackageManager.NameNotFoundException {
        this.packageName = ri.activityInfo.packageName;
        PackageInfo packageInfo = pm.getPackageInfo(this.packageName, 0);
        ApplicationInfo applicationInfo = pm.getApplicationInfo(this.packageName, 0);

        this.label = ((String) ri.loadLabel(pm));
        this.versionName = packageInfo.versionName;
        this.versionCode = packageInfo.versionCode;
        this.installed = packageInfo.firstInstallTime;
        this.updated = packageInfo.lastUpdateTime;
        this.systemApp = ((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);

        File file = new File(applicationInfo.publicSourceDir);
        this.apkSize = file.length();
        targetSDK = applicationInfo.targetSdkVersion;

        PackageInfo packageInfoPermissions = pm.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS);

        this.permissions = packageInfoPermissions.requestedPermissions;

    }

    /**
     * Gets label
     *
     * @return the label
     */
    public String getLabel() {
        return label;
    }

    /**
     * Gets package name
     *
     * @return the package name
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * Checks if app is system app
     *
     * @return true if system app, false otherwise
     */
    public boolean isSystemApp() {
        return systemApp;
    }

    /**
     * Gets the version code
     *
     * @return the version code
     */
    public int getVersionCode() {
        return versionCode;
    }

    /**
     * Gets the version name
     *
     * @return the version name
     */
    public String getVersionName() {
        return versionName;
    }

    /**
     * Gets the APK size
     *
     * @return APK size
     */
    public long getApkSize() {
        return apkSize;
    }

    /**
     * Gets target SDK level
     *
     * @return target SDK level
     */
    public int getTargetSDK() {
        return targetSDK;
    }

    /**
     * Gets date of installation
     *
     * @return the date
     */
    public long getInstalled() {
        return installed;
    }

    /**
     * Gets date of last update
     *
     * @return the date
     */
    public long getUpdated() {
        return updated;
    }

    /**
     * Gets list of permission
     *
     * @return permissions
     */
    public String[] getPermissions() {
        return permissions;
    }

    /**
     * Checks if app is favourite
     *
     * @return result
     */
    public boolean isFavorite() {
        return favorite;
    }

    /**
     * Sets the app as favorite or unfavorite
     *
     * @param favorite if the app should be favorite or not
     */
    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
