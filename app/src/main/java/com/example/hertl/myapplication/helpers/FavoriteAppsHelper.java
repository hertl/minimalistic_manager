/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.helpers;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

/**
 * Helper class for the favorite apps function.
 */
public class FavoriteAppsHelper {

    private int numberOfItems;
    private ArrayList<String> packageNames;
    private String favoriteAppsPath;

    /**
     * Constructor
     *
     * @param favoriteAppsPath the path of the favorite apps file
     */
    public FavoriteAppsHelper(String favoriteAppsPath) {
        this.favoriteAppsPath = favoriteAppsPath;
        refreshFavoriteApps();
    }

    /**
     * Adds a package name to the favorite apps file.
     *
     * @param packageName the package name of the app
     */
    public void addToFavoriteApps(String packageName) {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.favoriteAppsPath, true)));
            out.println(packageName);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a package name from the favorite apps file.
     *
     * @param removePackageName the package name of the app
     */
    public void removeFromFavoriteApps(String removePackageName) {
        BufferedReader reader;
        ArrayList<String> packageNames = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(this.favoriteAppsPath));
            String packageName = reader.readLine();
            while (packageName != null) {
                packageNames.add(packageName);
                packageName = reader.readLine();
            }
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(this.favoriteAppsPath)));
            for (String eachPackageName : packageNames) {
                if (eachPackageName.equals(removePackageName)) {
                    continue;
                }
                out.write(eachPackageName);
            }
            reader.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets all the package names of favorite apps
     *
     * @return the list of package names
     */
    public ArrayList<String> getPackageNames() {
        return packageNames;
    }

    /**
     * Refreshes the favorite apps package names if the file was changed.
     */
    public void refreshFavoriteApps() {
        BufferedReader reader;
        ArrayList<String> packageNames = new ArrayList<>();
        try {
            reader = new BufferedReader(new FileReader(this.favoriteAppsPath));
            String packageName = reader.readLine();
            while (packageName != null) {
                packageNames.add(packageName);
                packageName = reader.readLine();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        this.numberOfItems = packageNames.size();
        this.packageNames = packageNames;
    }
}
