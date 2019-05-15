/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.singletons;

import android.os.Environment;
import android.os.FileObserver;

import com.example.hertl.myapplication.helpers.ClipboardHelper;
import com.example.hertl.myapplication.helpers.FavoriteAppsHelper;
import com.example.hertl.myapplication.settings.Settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Singleton class that has instance that observes directory with configuration files. Its instance
 * has to be kept in the scope for the whole application run.
 */
public class FileObserverSingleton {

    private static final FileObserverSingleton ourInstance = new FileObserverSingleton();
    static private FileObserver observer;
    public ClipboardHelper clipboardHelper;
    public FavoriteAppsHelper favoriteAppsHelper;
    private String observedDir;

    private FileObserverSingleton() {
    }

    public static FileObserverSingleton getInstance() {
        return ourInstance;
    }

    /**
     * Starts observing the selected directory. It creates the instance of FileObserver class which
     * serves the observing purpose.
     *
     * @param observedDir the directory to be observed
     */
    public void startObserving(final String observedDir) {
        this.clipboardHelper = new ClipboardHelper(observedDir + File.separator + Settings.CLIPBOARD_FILE);
        this.favoriteAppsHelper = new FavoriteAppsHelper(observedDir + File.separator + Settings.FAVORITEAPPS_FILE);
        this.observedDir = observedDir;
        observer = new FileObserver(observedDir) {
            @Override
            public void onEvent(int event, String file) {
                if (event == FileObserver.MODIFY) { // If one of the files was modified, this method is called
                    switch (file) { // Checks what file was modified and refreshes the application settings
                        case Settings.CLIPBOARD_FILE:
                            refreshClipboardHelper();
                            break;

                        case Settings.FAVORITEAPPS_FILE:
                            refreshFavoriteAppsHelper();
                            break;

                        case Settings.MANAGER_FILE:
                            processManagerConfigFile();
                            break;

                        case Settings.LAUNCHER_FILE:
                            processLauncherConfigFile();
                            break;
                    }
                }
            }
        };
        observer.startWatching();
        processManagerConfigFile();
        processLauncherConfigFile();
    }

    /**
     * Refreshes the clipboard.
     */
    private void refreshClipboardHelper() {
        clipboardHelper.refreshClipboard();
    }

    /**
     * Refreshes the favorite apps.
     */
    private void refreshFavoriteAppsHelper() {
        favoriteAppsHelper.refreshFavoriteApps();
    }

    /**
     * Processes the manager config file and refreshes the settings.
     */
    private void processManagerConfigFile() {
        Properties properties = new Properties();
        InputStream configFileStream;
        try {
            configFileStream = new FileInputStream(this.observedDir + File.separator + Settings.MANAGER_FILE);

            properties.load(configFileStream);
            try {
                if (!properties.getProperty(Settings.ROOTPATH).equals(Settings.getRootPath())) {
                    Settings.setRootPath(properties.getProperty(Settings.ROOTPATH));
                }
            } catch (Exception e) {
                Settings.setRootPath(Environment.getExternalStorageDirectory().getAbsolutePath());
            }
            Settings.setSortBy(properties.getProperty(Settings.SORTBY));
            Settings.setSortOrder(properties.getProperty(Settings.SORTORDER));
            Settings.setShowExtension(properties.getProperty(Settings.SHOWEXTENSION));
            Settings.setShowHidden(properties.getProperty(Settings.SHOWHIDDEN));
            Settings.setSeparate(properties.getProperty(Settings.SEPARATE));
            Settings.setTruncateSize(properties.getProperty(Settings.TRUNCATESIZE));

            configFileStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Processes the launcher config file and refreshes the settings.
     */
    private void processLauncherConfigFile() {
        Properties properties = new Properties();
        InputStream configFileStream;
        try {
            configFileStream = new FileInputStream(this.observedDir + File.separator + Settings.LAUNCHER_FILE);

            properties.load(configFileStream);

            Settings.setLSortBy(properties.getProperty(Settings.L_SORTBY));
            Settings.setLSortOrder(properties.getProperty(Settings.L_SORTORDER));
            Settings.setLShowType(properties.getProperty(Settings.L_SHOWTYPE));
            Settings.setLShowGroups(properties.getProperty(Settings.L_SHOWGROUPS));
            Settings.setLTruncateSize(properties.getProperty(Settings.L_TRUNCATESIZE));

            configFileStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the desired new settings to the manager config file.
     *
     * @param key   the key of the changed setting
     * @param value the value of the changed setting
     */
    public void writeManagerSettings(String key, String value) {
        try {
            FileInputStream in = new FileInputStream(this.observedDir + File.separator + Settings.MANAGER_FILE);
            Properties props = new Properties();
            props.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream(this.observedDir + File.separator + Settings.MANAGER_FILE);
            props.setProperty(key, value);
            props.store(out, null);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the desired new settings to the launcher config file.
     *
     * @param key   the key of the changed setting
     * @param value the value of the changed setting
     */
    public void writeLauncherSettings(String key, String value) {
        try {
            FileInputStream in = new FileInputStream(this.observedDir + File.separator + Settings.LAUNCHER_FILE);
            Properties props = new Properties();
            props.load(in);
            in.close();

            FileOutputStream out = new FileOutputStream(this.observedDir + File.separator + Settings.LAUNCHER_FILE);
            props.setProperty(key, value);
            props.store(out, null);
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * At the garbage collection of this singleton, stop watching the config files directory.
     */
    @Override
    protected void finalize() throws Throwable {
        observer.stopWatching();
        super.finalize();
    }
}
