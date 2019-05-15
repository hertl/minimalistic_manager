/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.activites;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.hertl.myapplication.R;
import com.example.hertl.myapplication.exception.SomethingWrongException;
import com.example.hertl.myapplication.helpers.ClipboardHelper;
import com.example.hertl.myapplication.helpers.ManagerActionsHelper;
import com.example.hertl.myapplication.items.item.Item;
import com.example.hertl.myapplication.items.item.items.ItemDirectory;
import com.example.hertl.myapplication.items.item.items.ItemFile;
import com.example.hertl.myapplication.items.item.items.ItemUp;
import com.example.hertl.myapplication.listeners_adapters.ItemAdapter;
import com.example.hertl.myapplication.listeners_adapters.OnItemClickListenerNormalMode;
import com.example.hertl.myapplication.listeners_adapters.OnItemClickListenerSelectMoreMode;
import com.example.hertl.myapplication.settings.ItemComparator;
import com.example.hertl.myapplication.settings.Settings;
import com.example.hertl.myapplication.singletons.FileObserverSingleton;
import com.example.hertl.myapplication.singletons.FileOperatorSingleton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Properties;

/**
 * Main activity for whole project and also for file manager.
 * This activity runs first and has to initialize all needed variables.
 */
public class MainActivity extends Activity {

    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0;
    public static String filterString = "";
    private static String ROOT_PATH;
    private static boolean firstTime = true;
    private static boolean isSelectMoreMode = false;
    public String currentPath;
    public ItemAdapter adapter;
    private long firstBackPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check permissions, different for lower and higher version of API

        if (Build.VERSION.SDK_INT >= 23) { // API level 23 and higher checks if this application already has needed permissions, else asks user to allow them
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
            } else {
                startFileManager();
            }
        } else { // Lower APIs do already have the needed permissions defined in manifest file from installation
            startFileManager();
        }
    }

    /**
     * Starts and initializes file manager. Sets up the listeners and adapters.
     */
    private void startFileManager() {

        if (firstTime) { // Will run first time every session
            initFileManager();
        }

        final ListView listview = findViewById(R.id.listview);
        registerForContextMenu(findViewById(R.id.listview));

        ROOT_PATH = Settings.getRootPath();
        getCurrentPathAndSetTitle();

        File dir = new File(currentPath);
        if (!dir.exists()) {
            toastMsg("This directory is unavailable in this device.");
            return;
        }

        final ArrayList<Item> items = new ArrayList<>();

        FilenameFilter filter = new FilenameFilter() { // Set the filter
            @Override
            public boolean accept(File dir, String name) {
                return name.toLowerCase().contains(filterString.toLowerCase());
            }
        };

        // Fill items and sort them
        fillItems(items, dir, filter);
        Collections.sort(items, new ItemComparator());

        if (Settings.getSeparate().equals(Settings.SEPARATE_YES)) { // Separate directories and file, keep sorting
            Collections.sort(items, new Comparator<Item>() {
                @Override
                public int compare(Item i1, Item i2) {
                    return i1.getFile().isDirectory() == i2.getFile().isDirectory() ? 0 : (i2.getFile().isDirectory() ? 1 : -1);
                }
            });
        }

        if (!isSelectMoreMode) {
            if (!isRootPath()) {
                items.add(0, new ItemUp(new File(currentPath))); // Add level up item
            }
        }

        // Set adapter
        adapter = new ItemAdapter(this, 0, items);
        listview.setAdapter(adapter);

        // Set listener
        if (!isSelectMoreMode) {
            listview.setOnItemClickListener(new OnItemClickListenerNormalMode(this));
        } else { // Select more mode
            listview.setOnItemClickListener(new OnItemClickListenerSelectMoreMode(this));
        }

    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        ListView listView = (ListView) v;
        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuInfo;
        Item item = (Item) listView.getItemAtPosition(acmi.position);

        if (item instanceof ItemUp) {
            return;
        }
        String fileName = item.getName();
        menu.setHeaderTitle(fileName);

        if (!isSelectMoreMode) {
            getMenuInflater().inflate(R.menu.item_menu, menu);
        }

        if (item instanceof ItemDirectory) {
            menu.removeItem(R.id.openWith);
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem menuItem) {

        AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) menuItem.getMenuInfo();

        Item item = (Item) ((ListView) findViewById(R.id.listview)).getItemAtPosition(acmi.position);

        final String fileName = item.getName();

        final File file = item.getFile();

        switch (menuItem.getItemId()) {

            case R.id.openWith:
                ManagerActionsHelper.openWithAction(this, file);
                break;

            case R.id.copy:
                FileOperatorSingleton.getInstance().updatePath(currentPath, fileName, true);
                toastMsg("Path updated.");
                break;

            case R.id.move:
                FileOperatorSingleton.getInstance().updatePath(currentPath, fileName, false);
                toastMsg("Path updated.");
                break;

            case R.id.addToClipboard:
                FileObserverSingleton.getInstance().clipboardHelper.addToClipboard(file.getPath());
                toastMsg("Clipboard updated.");
                break;

            case R.id.rename:
                ManagerActionsHelper.renameAction(this, fileName);
                break;

            case R.id.delete:
                ManagerActionsHelper.deleteAction(this, fileName, file);
                break;

            case R.id.properties:
                ManagerActionsHelper.createPropertiesDialog(this, fileName);
                break;

            default:
                return super.onContextItemSelected(menuItem);
        }

        return super.onContextItemSelected(menuItem);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (isSelectMoreMode) {
            inflater.inflate(R.menu.main_menu_more, menu);
        } else {
            inflater.inflate(R.menu.main_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        ListView listView = findViewById(R.id.listview);

        ClipboardHelper clipboardHelper = FileObserverSingleton.getInstance().clipboardHelper;

        switch (menuItem.getItemId()) {

            case R.id.newFile:
                ManagerActionsHelper.newFileAction(this);
                break;

            case R.id.newDir:
                ManagerActionsHelper.newDirAction(this);
                break;

            case R.id.paste:
                if (FileOperatorSingleton.getInstance().getStartingPath() == null) {
                    toastMsg("Nothing selected to paste.");
                } else {
                    try {
                        File startingFile = new File(FileOperatorSingleton.getInstance().getCopiedFile().getAbsolutePath());
                        ManagerActionsHelper.checkRewrite(startingFile, new File(currentPath));

                        try {
                            ManagerActionsHelper.pasteOne(this);
                        } catch (SomethingWrongException e) {
                            toastMsg(e.getMessage());
                        }

                    } catch (SomethingWrongException e) {
                        ManagerActionsHelper.pasteOneRewriteConfirm(this);
                    }
                }

                break;

            case R.id.settings:
                Intent startSettings = new Intent(this, SettingsManagerActivity.class);
                startActivity(startSettings);
                break;

            case R.id.filter:
                ManagerActionsHelper.filterAction(this);
                break;

            case R.id.removeFilter:
                filterString = "";
                refresh();
                break;

            case R.id.selectMore:
                isSelectMoreMode = true;
                refresh();
                break;

            case R.id.launcher:
                Intent startLauncher = new Intent(this, AppLauncherActivity.class);
                startLauncher.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startLauncher);
                finish();
                break;

            //****************************Select More*****************************//

            case R.id.selectAll:
                ManagerActionsHelper.selectAllAction(this, listView);
                break;

            case R.id.clipboardCopy: {

                ArrayList<String> clipboardPaths = clipboardHelper.getPaths();

                try {
                    for (String clipboardPath : clipboardPaths) {
                        File clipboardFile = new File(clipboardPath);
                        ManagerActionsHelper.checkRewrite(clipboardFile, new File(currentPath));
                    }
                    try {
                        ManagerActionsHelper.pasteClipboard(this, clipboardPaths, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        toastMsg(e.getMessage());
                    }
                } catch (SomethingWrongException e) {
                    ManagerActionsHelper.pasteClipboardRewriteConfirm(this, clipboardPaths, false);
                }

                break;
            }

            case R.id.clipboardMove:
                ArrayList<String> clipboardPaths = clipboardHelper.getPaths();
                try {
                    for (String clipboardPath : clipboardPaths) {
                        File clipboardFile = new File(clipboardPath);
                        ManagerActionsHelper.checkRewrite(clipboardFile, new File(currentPath));
                    }
                    try {
                        ManagerActionsHelper.pasteClipboard(this, clipboardPaths, true);
                    } catch (SomethingWrongException e) {
                        toastMsg(e.getMessage());
                    }
                } catch (SomethingWrongException e) {
                    ManagerActionsHelper.pasteClipboardRewriteConfirm(this, clipboardPaths, true);
                }

                break;

            case R.id.clipboardClear:
                try {
                    clipboardHelper.clearClipboard();
                    toastMsg("Clipboard was cleared");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;

            case R.id.clipboardShow:
                ManagerActionsHelper.clipboardShowAction(this, clipboardHelper);
                break;

            case R.id.addToClipboardMore:
                Item item;
                ArrayList<String> selectedItems = new ArrayList<>();
                for (int i = 0; i < listView.getCount(); i++) {
                    item = (Item) listView.getItemAtPosition(i);
                    if (item == null) {
                        break;
                    }
                    if (item.isSelected()) {
                        selectedItems.add(item.getFile().getAbsolutePath());
                    }
                }
                FileObserverSingleton.getInstance().clipboardHelper.addToClipboard(selectedItems);
                toastMsg("Clipboard updated.");
                break;

            case R.id.deleteMore:
                ManagerActionsHelper.deleteMoreAction(this, listView);
                break;

            case R.id.propertiesMore:
                ManagerActionsHelper.propertiesMoreAction(this, listView);
                break;

            case R.id.cancelSelectMore:
                isSelectMoreMode = false;
                refresh();
                break;

            default:
                return super.onOptionsItemSelected(menuItem);
        }

        return super.onOptionsItemSelected(menuItem);
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
     * Redefined back button to go level up or to wait for another press to exit the application
     * depending on the current path
     */
    @Override
    public void onBackPressed() {
        if (isSelectMoreMode) {
            isSelectMoreMode = false;
            refresh();
        } else if (isRootPath()) {

            if (firstBackPressedTime + 2000 > System.currentTimeMillis()) {
                finish();
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            } else {
                toastMsg("Press again to exit!");
            }
            firstBackPressedTime = System.currentTimeMillis();
        } else {
            levelUp();
        }
    }


    /**
     * Initializes the file manager. Runs every time the application opens for the first time in a
     * session. Creates and sets up config files if it runs for the first time ever. Starts file
     * observer.
     */
    private void initFileManager() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        try {
            String dirPath = getExternalFilesDir(null).getAbsolutePath();
            if (dirPath == null) {
                dirPath = getFilesDir().getAbsolutePath();
                if (dirPath == null) {
                    throw new SomethingWrongException("Couldn't create app files. Settings and clipboard will not work.");
                }
                toastMsg("External memory not available. Creating files in internal memory");
            }

            if (sharedPreferences.getBoolean(getString(R.string.preferenceFirst), true)) { // Will run only first time after installation
                createAndInitFiles(dirPath);
                sharedPreferences.edit().putBoolean(getString(R.string.preferenceFirst), false).apply();
            }

            checkIfFilesExist(dirPath); // Check if the files exists after every start, if they don't, create them

            // Start watching config files for a change
            FileObserverSingleton instance = FileObserverSingleton.getInstance();
            instance.startObserving(dirPath);
            firstTime = false;

        } catch (IOException e) {
            toastMsg("Couldn't create app files. Settings will not work.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            toastMsg(e.getMessage());
        }
    }

    private void checkIfFilesExist(String dirPath) throws IOException, SomethingWrongException {
        if (!(new File(dirPath)).exists()) {
            createAndInitFiles(dirPath);
        } else {
            File managerConfigFilePath = new File(dirPath + File.separator + Settings.MANAGER_FILE);
            File launcherConfigFilePath = new File(dirPath + File.separator + Settings.LAUNCHER_FILE);
            File clipboardFile = new File(dirPath + File.separator + Settings.CLIPBOARD_FILE);
            File favoriteAppsFile = new File(dirPath + File.separator + Settings.FAVORITEAPPS_FILE);

            if (!managerConfigFilePath.exists()) {
                createManagerConfigFile(dirPath);
            }
            if (!launcherConfigFilePath.exists()) {
                createLauncherConfigFile(dirPath);
            }
            if (!clipboardFile.exists()) {
                createClipboardFile(dirPath);
            }
            if (!favoriteAppsFile.exists()) {
                createFavoriteAppsFile(dirPath);
            }
        }
    }


    /**
     * Fills the list with items in current showed directory.
     *
     * @param items  The list of items
     * @param dir    Current showed directory
     * @param filter Filter to filter the items
     */
    private void fillItems(ArrayList<Item> items, File dir, FilenameFilter filter) {
        String showHidden = Settings.getShowHidden();
        File[] list = dir.listFiles(filter);
        if (list != null) {
            for (File file : list) {
                if (showHidden.equals(getString(R.string.no))) {
                    if (file.getName().startsWith(".")) {
                        continue;
                    }
                }
                if (file.isDirectory()) {
                    items.add(new ItemDirectory(file));
                } else {
                    items.add(new ItemFile(file));
                }
            }
        }
    }


    /**
     * Opens file chooser for chosen item to choose available application to run with. If there is
     * only one application that can open chosen file, it opens instantly.
     * Differs for older and newer Android. Android 7.0 and higher needs to have implemented a
     * FileProvider defined in manifest file.
     *
     * @param item item to open
     */
    public void openFileChooser(Item item) {
        Uri uri;
        String type;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                String fileProviderName = "com.example.hertl.myapplication.fileprovider";
                uri = FileProvider.getUriForFile(MainActivity.this, fileProviderName, item.getFile());
                type = getContentResolver().getType(uri);
            } catch (Exception e) {
                try {
                    uri = Uri.fromFile(item.getFile().getAbsoluteFile());
                    String ext = ManagerActionsHelper.getFileExtension(item.getName());
                    type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
                } catch (Exception e1) {
                    e1.printStackTrace();
                    toastMsg("Couldn't get URI of this file.");
                    return;
                }
            }
        } else {
            uri = Uri.fromFile(item.getFile().getAbsoluteFile());
            String ext = ManagerActionsHelper.getFileExtension(item.getName());
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext);
        }
        Intent newIntent = new Intent(Intent.ACTION_VIEW);

        newIntent.setDataAndType(uri, type);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        newIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        newIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        Intent chooser = Intent.createChooser(newIntent, "Pick");
        startActivity(chooser);
    }


    /**
     * Gets current path and sets the title to show in title bar.
     */
    private void getCurrentPathAndSetTitle() {
        if (Settings.isRecentlyUpdatedRootPath()) {
            currentPath = ROOT_PATH;
            Settings.setRecentlyUpdatedRootPath(false);
        } else {
            if (getIntent().hasExtra("currentPath")) {
                String curr = getIntent().getStringExtra("currentPath");
                if (!curr.contains(ROOT_PATH)) {
                    currentPath = ROOT_PATH;
                } else {
                    currentPath = curr;
                }
            } else {
                currentPath = ROOT_PATH;
            }
        }
        if (isSelectMoreMode) {
            setTitle("Select more items:");
        } else {
            if (isRootPath()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    if (Environment.getExternalStorageState(new File(currentPath)).equals(Environment.MEDIA_MOUNTED)) {
                        if (Environment.isExternalStorageRemovable(new File(currentPath))) {
                            setTitle(Settings.getRootPath() + " (r-o)");
                        }
                    }
                }
                setTitle(Settings.getRootPath());
            } else {
                setTitle("../" + (new File(currentPath)).getName());
            }

        }
    }

    /**
     * Checks if current path is root path.
     *
     * @return The result
     */
    public boolean isRootPath() {
        return currentPath.equals(ROOT_PATH);
    }

    /**
     * Creates and initializes the config files. Runs first time after installation only.
     *
     * @param dirPath The path where will be the files created
     */
    private void createAndInitFiles(String dirPath) throws IOException, SomethingWrongException {
        createManagerConfigFile(dirPath);
        createLauncherConfigFile(dirPath);
        createClipboardFile(dirPath);
        createFavoriteAppsFile(dirPath);
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
     * Creates the clipboard file.
     *
     * @param dirPath The path where will be the file created
     */
    private void createClipboardFile(String dirPath) throws IOException {
        File clipboardFile = new File(dirPath + File.separator + Settings.CLIPBOARD_FILE);
        clipboardFile.createNewFile();
    }

    /**
     * Creates the favorite apps file.
     *
     * @param dirPath The path where will be the file created
     */
    private void createFavoriteAppsFile(String dirPath) throws IOException {
        File favoriteAppsFile = new File(dirPath + File.separator + Settings.FAVORITEAPPS_FILE);
        favoriteAppsFile.createNewFile();
    }

    /**
     * Creates the manager config file and initializes.
     *
     * @param dirPath The path where will be the file created
     */
    private void createManagerConfigFile(String dirPath) throws SomethingWrongException {
        File projDir = new File(dirPath);
        if (!projDir.exists()) {
            if (projDir.mkdirs()) {
                throw new SomethingWrongException("Config file could not be created.");
            }
        }

        String managerConfigFilePath = dirPath + File.separator + Settings.MANAGER_FILE;

        Properties properties = new Properties();
        OutputStream managerConfigFileStream;

        try {
            managerConfigFileStream = new FileOutputStream(managerConfigFilePath);
            String initialPath;

            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                initialPath = (Environment.getExternalStorageDirectory()).getAbsolutePath() + File.separator;
            } else {
                toastMsg("No external storage available or not permitted.");
                initialPath = File.separator;
            }
            properties.setProperty(Settings.ROOTPATH, initialPath);
            properties.setProperty(Settings.SORTBY, Settings.SORTBY_DEFAULT);
            properties.setProperty(Settings.SORTORDER, Settings.SORTORDER_DEFAULT);
            properties.setProperty(Settings.SHOWEXTENSION, Settings.SHOWEXTENSION_DEFAULT);
            properties.setProperty(Settings.SHOWHIDDEN, Settings.SHOWHIDDEN_DEFAULT);
            properties.setProperty(Settings.SEPARATE, Settings.SEPARATE_DEFAULT);
            properties.setProperty(Settings.TRUNCATESIZE, Settings.TRUNCATESIZE_DEFAULT);

            properties.store(managerConfigFileStream, null);
            managerConfigFileStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * Creates the launcher config file and initializes.
     *
     * @param dirPath The path where will be the file created
     */
    private void createLauncherConfigFile(String dirPath) {
        String launcherConfigFilePath = dirPath + File.separator + Settings.LAUNCHER_FILE;
        Properties properties = new Properties();
        OutputStream launcherConfigFileStream;

        try {
            launcherConfigFileStream = new FileOutputStream(launcherConfigFilePath);
            properties.setProperty(Settings.L_SORTBY, Settings.L_SORTBY_DEFAULT);
            properties.setProperty(Settings.L_SORTORDER, Settings.L_SORTORDER_DEFAULT);
            properties.setProperty(Settings.L_SHOWTYPE, Settings.L_SHOWTYPE_DEFAULT);
            properties.setProperty(Settings.L_SHOWGROUPS, Settings.L_SHOWGROUPS_DEFAULT);
            properties.setProperty(Settings.L_TRUNCATESIZE, Settings.L_TRUNCATESIZE_DEFAULT);

            properties.store(launcherConfigFileStream, null);
            launcherConfigFileStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks permissions
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startFileManager();
            } else {
                ManagerActionsHelper.initError(this);
            }
        }
    }

    /**
     * Navigate one level up in the file system.
     */
    public void levelUp() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currentPath", currentPath.substring(0, currentPath.lastIndexOf(File.separator, currentPath.lastIndexOf(File.separator) - 1) + 1));
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    /**
     * Navigate one level down in the file system.
     */
    public void levelDown(String fullName) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("currentPath", fullName + File.separator);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
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
     * Opens keyboard.
     */
    public void openKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

}
