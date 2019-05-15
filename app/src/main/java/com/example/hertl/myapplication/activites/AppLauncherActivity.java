/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hertl.myapplication.R;
import com.example.hertl.myapplication.apps.App;
import com.example.hertl.myapplication.helpers.FavoriteAppsHelper;
import com.example.hertl.myapplication.helpers.LauncherActionsHelper;
import com.example.hertl.myapplication.helpers.ManagerActionsHelper;
import com.example.hertl.myapplication.listeners_adapters.AppsAdapter;
import com.example.hertl.myapplication.settings.AppComparator;
import com.example.hertl.myapplication.settings.Settings;
import com.example.hertl.myapplication.singletons.FileObserverSingleton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Main activity for the launcher
 */
public class AppLauncherActivity extends Activity {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    public static String filterString = "";
    long firstBackPressedTime;
    FileObserverSingleton fileObserverSingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Launcher");

        // Check permissions, different for lower and higher version of API

        if (Build.VERSION.SDK_INT >= 23) { // API level 23 and higher checks if this application already has needed permissions, else asks user to allow them
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                startLauncher();
            }
        } else { // Lower APIs do already have the needed permissions defined in manifest file from installation
            startLauncher();
        }
    }

    private void startLauncher() {

        final PackageManager pm = this.getPackageManager();
        fileObserverSingleton = FileObserverSingleton.getInstance();

        final ArrayList<App> apps = new ArrayList<>();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);

        // Get all installed apps
        final List<ResolveInfo> allAppsRI = pm.queryIntentActivities(intent, 0);
        final AppLauncherActivity act = this;

        /**
         * Do all the initialization in background thread because that can take some time so the user
         * doesn't get nervous.
         */
        class LoadAppLauncherTask extends AsyncTask<Void, Integer, String> {
            @Override
            protected void onPreExecute() {
                setContentView(R.layout.loading);
            }

            @Override
            protected String doInBackground(Void... s) {

                // Load all installed apps and init representing instances
                for (ResolveInfo ri : allAppsRI) {
                    App app;
                    try {
                        app = new App(pm, ri);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                        toastMsg("Some Apps didn't load correctly.");
                        continue;
                    }

                    // Don't show this app
                    if (app.getPackageName().equals("com.example.hertl.myapplication")) {
                        continue;
                    }

                    // Filter the apps
                    if (app.getLabel().toLowerCase().contains(filterString.toLowerCase())) {
                        switch (Settings.getLShowType()) {
                            case Settings.L_SHOWTYPE_SYSTEM:
                                if (app.isSystemApp()) {
                                    apps.add(app);
                                }
                                break;
                            case Settings.L_SHOWTYPE_INSTALLED:
                                if (!app.isSystemApp()) {
                                    apps.add(app);
                                }
                                break;
                            default:
                                apps.add(app);
                                break;
                        }
                    }
                }

                // Favorite apps check
                FavoriteAppsHelper favoriteAppsHelper = FileObserverSingleton.getInstance().favoriteAppsHelper;
                ArrayList<String> favAppsPackageNames = favoriteAppsHelper.getPackageNames();
                for (App a : apps) {
                    for (String favAppsPackageName : favAppsPackageNames) {
                        if (a.getPackageName().equals(favAppsPackageName)) {
                            a.setFavorite(true);
                        }
                    }
                }
                Collections.sort(apps, new AppComparator());

                // Show groups option
                if (!Settings.getLShowGroups().equals(Settings.L_SHOWGROUPS_NO)) {
                    if (Settings.getLShowGroups().equals(Settings.L_SHOWGROUPS_SYSTEM)) { // Separate system and installed apps
                        Collections.sort(apps, new Comparator<App>() {
                            @Override
                            public int compare(App a1, App a2) {
                                return a1.isSystemApp() == a2.isSystemApp() ? 0 : (a2.isSystemApp() ? 1 : -1);
                            }
                        });
                    } else if (Settings.getLShowGroups().equals(Settings.L_SHOWGROUPS_FAVORITE)) { // Separate favorite apps
                        Collections.sort(apps, new Comparator<App>() {
                            @Override
                            public int compare(App a1, App a2) {
                                return a1.isFavorite() == a2.isFavorite() ? 0 : (a2.isFavorite() ? 1 : -1);
                            }
                        });
                    }
                }
                return "End";
            }

            /**
             * After background initialization, list the apps and set adapters and listeners
             */
            @Override
            protected void onPostExecute(String result) {
                setContentView(R.layout.activity_main);
                final ListView listview = findViewById(R.id.listview);
                registerForContextMenu(listview);
                final AppsAdapter adapter = new AppsAdapter(act,
                        0, apps);
                listview.setAdapter(adapter);

                // Listener to open app in click
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, final View v,
                                            int position, long id) {
                        App app = (App) parent.getItemAtPosition(position);
                        Intent launchApp = pm.getLaunchIntentForPackage(app.getPackageName());

                        startActivity(launchApp);
                    }
                });
            }
        }
        new LoadAppLauncherTask().execute();
    }

    /**
     * Checks permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startLauncher();
            } else {
                ManagerActionsHelper.initError(this);
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ListView listView = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        App app = (App) listView.getItemAtPosition(acmi.position);
        String appName = app.getLabel();
        menu.setHeaderTitle(appName);

        getMenuInflater().inflate(R.menu.launcher_app_menu, menu);

        if (app.isFavorite()) {
            menu.removeItem(R.id.setFavorite);
        } else {
            menu.removeItem(R.id.unsetFavorite);
        }

    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {

        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();
        ListView lv = findViewById(R.id.listview);

        App app = (App) lv.getItemAtPosition(acmi.position);
        final String packageName = app.getPackageName();
        FavoriteAppsHelper favoriteAppsHelper = FileObserverSingleton.getInstance().favoriteAppsHelper;

        switch (menuItem.getItemId()) {
            case R.id.showInfo:
                LauncherActionsHelper.showInfoAction(this, app);
                break;

            case R.id.setFavorite:
                favoriteAppsHelper.addToFavoriteApps(app.getPackageName());
                refresh();
                break;

            case R.id.unsetFavorite:
                favoriteAppsHelper.removeFromFavoriteApps(app.getPackageName());
                refresh();
                break;

            case R.id.openSystem:
                LauncherActionsHelper.openInSystemApp(this, packageName);
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }

        return super.onOptionsItemSelected(menuItem);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.launcher_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {

        switch (menuItem.getItemId()) {
            case R.id.refresh:
                refresh();
                break;

            case R.id.lSettings:
                Intent startSettings = new Intent(this, SettingsLauncherActivity.class);
                startActivity(startSettings);
                break;

            case R.id.lFilter:
                LauncherActionsHelper.filterAction(this);
                break;

            case R.id.lRemoveFilter:
                filterString = "";
                refresh();
                break;

            case R.id.fileManager:
                Intent startManager = new Intent(this, MainActivity.class);
                startManager.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startManager);
                finish();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }

        return super.onOptionsItemSelected(menuItem);
    }

    /**
     * Redefined back button to wait for another press to exit the application
     * depending on the current path
     */
    @Override
    public void onBackPressed() {
        if (firstBackPressedTime + 2000 > System.currentTimeMillis()) {
            finish();
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        } else {
            toastMsg("Press again to exit!");
        }
        firstBackPressedTime = System.currentTimeMillis();

    }

    /**
     * Helper method for faster toast message. Shows message to the user by using toast.
     *
     * @param msg the message to show
     */
    public void toastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Refreshes the activity to view new data.
     */
    public void refresh() {
        Intent intent = getIntent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }

    /**
     * Redefined onRestart method to refresh this activity.
     */
    @Override
    public void onRestart() {
        super.onRestart();
        refresh();
    }

    /**
     * Opens keyboard.
     */
    public void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }
}
