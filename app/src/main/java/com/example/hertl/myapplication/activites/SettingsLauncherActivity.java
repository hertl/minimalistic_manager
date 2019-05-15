/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.activites;

import android.app.Activity;
import android.os.Bundle;
import android.widget.RadioGroup;

import com.example.hertl.myapplication.R;
import com.example.hertl.myapplication.settings.Settings;
import com.example.hertl.myapplication.singletons.FileObserverSingleton;

/**
 * Activity for launcher settings. Initializes the layout and waits for user input.
 */
public class SettingsLauncherActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_launcher);

        RadioGroup sortByGroup = findViewById(R.id.lSortByGroup);
        String sortBy = Settings.getLSortBy();

        switch (sortBy) {
            case Settings.L_SORTBY_NAME:
                sortByGroup.check(R.id.lSortByName);
                break;
            case Settings.L_SORTBY_PACKAGE:
                sortByGroup.check(R.id.lSortByPackage);
                break;
            case Settings.L_SORTBY_APKSIZE:
                sortByGroup.check(R.id.lSortByApkSize);
                break;
            case Settings.L_SORTBY_INSTALL:
                sortByGroup.check(R.id.lSortByInstall);
                break;
            case Settings.L_SORTBY_UPDATE:
                sortByGroup.check(R.id.lSortByUpdate);
                break;
            default:
                sortByGroup.check(R.id.lSortByName);
        }

        sortByGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.lSortByName:
                        FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SORTBY, Settings.L_SORTBY_NAME);
                        break;
                    case R.id.lSortByPackage:
                        FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SORTBY, Settings.L_SORTBY_PACKAGE);
                        break;
                    case R.id.lSortByApkSize:
                        FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SORTBY, Settings.L_SORTBY_APKSIZE);
                        break;
                    case R.id.lSortByInstall:
                        FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SORTBY, Settings.L_SORTBY_INSTALL);
                        break;
                    case R.id.lSortByUpdate:
                        FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SORTBY, Settings.L_SORTBY_UPDATE);
                        break;
                }
            }
        });

        RadioGroup sortOrderGroup = findViewById(R.id.lSortOrderGroup);
        String sortOrder = Settings.getLSortOrder();


        if (sortOrder.equals(Settings.L_SORTORDER_ASCENDING)) {
            sortOrderGroup.check(R.id.lSortOrderAsc);
        } else if (sortOrder.equals(Settings.L_SORTORDER_DESCENDING)) {
            sortOrderGroup.check(R.id.lSortOrderDesc);
        } else {
            sortOrderGroup.check(R.id.lSortOrderAsc);
        }

        sortOrderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lSortOrderAsc) {

                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SORTORDER, Settings.L_SORTORDER_ASCENDING);
                } else if (checkedId == R.id.lSortOrderDesc) {

                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SORTORDER, Settings.L_SORTORDER_DESCENDING);
                }
            }
        });

        RadioGroup showTypeGroup = findViewById(R.id.lShowTypeGroup);
        String showType = Settings.getLShowType();


        if (showType.equals(Settings.L_SHOWTYPE_ALL)) {
            showTypeGroup.check(R.id.lShowTypeAll);
        } else if (showType.equals(Settings.L_SHOWTYPE_INSTALLED)) {
            showTypeGroup.check(R.id.lShowTypeInstalled);
        } else if (showType.equals(Settings.L_SHOWTYPE_SYSTEM)) {
            showTypeGroup.check(R.id.lShowTypeSystem);
        } else {
            showTypeGroup.check(R.id.lShowTypeAll);
        }

        showTypeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lShowTypeAll) {
                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SHOWTYPE, Settings.L_SHOWTYPE_ALL);
                } else if (checkedId == R.id.lShowTypeInstalled) {
                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SHOWTYPE, Settings.L_SHOWTYPE_INSTALLED);
                } else if (checkedId == R.id.lShowTypeSystem) {
                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SHOWTYPE, Settings.L_SHOWTYPE_SYSTEM);
                }
            }
        });

        RadioGroup showGroupsGroup = findViewById(R.id.lShowGroupsGroup);
        String showGroups = Settings.getLShowGroups();


        if (showGroups.equals(Settings.L_SHOWGROUPS_NO)) {
            showGroupsGroup.check(R.id.lShowGroupsNo);
        } else if (showGroups.equals(Settings.L_SHOWGROUPS_SYSTEM)) {
            showGroupsGroup.check(R.id.lShowGroupsSystem);
        } else if (showGroups.equals(Settings.L_SHOWGROUPS_FAVORITE)) {
            showGroupsGroup.check(R.id.lShowGroupsFavorite);
        } else {
            showGroupsGroup.check(R.id.lShowGroupsNo);
        }

        showGroupsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lShowGroupsNo) {
                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SHOWGROUPS, Settings.L_SHOWGROUPS_NO);
                } else if (checkedId == R.id.lShowGroupsSystem) {
                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SHOWGROUPS, Settings.L_SHOWGROUPS_SYSTEM);
                } else if (checkedId == R.id.lShowGroupsFavorite) {
                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_SHOWGROUPS, Settings.L_SHOWGROUPS_FAVORITE);
                }
            }
        });

        RadioGroup truncateSizeGroup = findViewById(R.id.lTruncateSizeGroup);
        String truncateSize = Settings.getLTruncateSize();


        if (truncateSize.equals(Settings.L_TRUNCATESIZE_YES)) {
            truncateSizeGroup.check(R.id.lTruncateSizeYes);
        } else if (truncateSize.equals(Settings.L_TRUNCATESIZE_NO)) {
            truncateSizeGroup.check(R.id.lTruncateSizeNo);
        } else {
            truncateSizeGroup.check(R.id.lTruncateSizeYes);
        }

        truncateSizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.lTruncateSizeYes) {
                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_TRUNCATESIZE, Settings.L_TRUNCATESIZE_YES);
                } else if (checkedId == R.id.lTruncateSizeNo) {
                    FileObserverSingleton.getInstance().writeLauncherSettings(Settings.L_TRUNCATESIZE, Settings.L_TRUNCATESIZE_NO);
                }
            }
        });
    }
}
