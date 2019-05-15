/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.activites;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.example.hertl.myapplication.R;
import com.example.hertl.myapplication.settings.Settings;
import com.example.hertl.myapplication.singletons.FileObserverSingleton;

import java.io.File;
import java.util.ArrayList;

/**
 * Activity for manager settings. Initializes the layout and waits for user input.
 */
public class SettingsManagerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_manager);

        final RadioGroup rootDirGroup = findViewById(R.id.rootDirectory);

        final ArrayList<String> allRootDirs = getRootDirs();
        ArrayList<String> allExtRootDirs = getAllExtRootDirs();


        // Dynamically adds all root directories available internal and external storages
        final String rootDirPath = Settings.getRootPath();

        TextView textView = new TextView(this);
        textView.setText("Root Dirs:");
        rootDirGroup.addView(textView);

        for (String path : allRootDirs) {
            RadioButton btn = new RadioButton(this);
            btn.setText(path);
            btn.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootDirGroup.addView(btn);
            if (path.equals(rootDirPath)) {
                btn.toggle();
            }
        }

        TextView textView1 = new TextView(this);
        textView1.setText("External Root Dirs:");
        rootDirGroup.addView(textView1);

        for (String path : allExtRootDirs) {
            RadioButton btn = new RadioButton(this);
            btn.setText(path);
            btn.setLayoutParams(new RadioGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            rootDirGroup.addView(btn);
            if (path.equals(rootDirPath)) {
                btn.toggle();
            }
        }


        rootDirGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                RadioButton checkedRB;
                for (int i = 0; i < group.getChildCount(); i++) {
                    if (!(group.getChildAt(i) instanceof RadioButton)) {
                        continue;
                    }
                    checkedRB = ((RadioButton) group.getChildAt(i));
                    if (checkedRB.isChecked()) {
                        FileObserverSingleton.getInstance().writeManagerSettings(Settings.ROOTPATH, (String) checkedRB.getText());
                        break;
                    }
                }
            }
        });

        RadioGroup sortByGroup = findViewById(R.id.sortByGroup);
        String sortBy = Settings.getSortBy();

        switch (sortBy) {
            case Settings.SORTBY_NAME:
                sortByGroup.check(R.id.sortByName);
                break;
            case Settings.SORTBY_DATE:
                sortByGroup.check(R.id.sortByDate);
                break;
            case Settings.SORTBY_SIZE:
                sortByGroup.check(R.id.sortBySize);
                break;
            case Settings.SORTBY_EXTENSION:
                sortByGroup.check(R.id.sortByExt);
                break;
            default:
                sortByGroup.check(R.id.sortByName);
                break;
        }

        sortByGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.sortByName:

                        FileObserverSingleton.getInstance().writeManagerSettings(Settings.SORTBY, Settings.SORTBY_NAME);
                        break;
                    case R.id.sortByDate:

                        FileObserverSingleton.getInstance().writeManagerSettings(Settings.SORTBY, Settings.SORTBY_DATE);
                        break;
                    case R.id.sortBySize:

                        FileObserverSingleton.getInstance().writeManagerSettings(Settings.SORTBY, Settings.SORTBY_SIZE);
                        break;
                    case R.id.sortByExt:

                        FileObserverSingleton.getInstance().writeManagerSettings(Settings.SORTBY, Settings.SORTBY_EXTENSION);
                        break;
                }

            }
        });

        RadioGroup sortOrderGroup = findViewById(R.id.sortOrderGroup);
        String sortOrder = Settings.getSortOrder();

        if (sortOrder.equals(Settings.SORTORDER_ASCENDING)) {
            sortOrderGroup.check(R.id.sortOrderAsc);
        } else if (sortOrder.equals(Settings.SORTORDER_DESCENDING)) {
            sortOrderGroup.check(R.id.sortOrderDesc);
        } else {
            sortOrderGroup.check(R.id.sortOrderAsc);
        }

        sortOrderGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.sortOrderAsc) {

                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.SORTORDER, Settings.SORTORDER_ASCENDING);
                } else if (checkedId == R.id.sortOrderDesc) {

                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.SORTORDER, Settings.SORTORDER_DESCENDING);
                }
            }
        });

        RadioGroup showExtGroup = findViewById(R.id.showExtGroup);
        String showExt = Settings.getShowExtension();


        if (showExt.equals(Settings.SHOWEXTENSION_YES)) {
            showExtGroup.check(R.id.miscShowExtYes);
        } else if (showExt.equals(Settings.SHOWEXTENSION_NO)) {
            showExtGroup.check(R.id.miscShowExtNo);
        } else {
            showExtGroup.check(R.id.miscShowExtYes);
        }

        showExtGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.miscShowExtYes) {

                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.SHOWEXTENSION, Settings.SHOWEXTENSION_YES);
                } else if (checkedId == R.id.miscShowExtNo) {

                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.SHOWEXTENSION, Settings.SHOWEXTENSION_NO);
                }
            }
        });

        RadioGroup showHiddenGroup = findViewById(R.id.showHiddenGroup);
        String showHidden = Settings.getShowHidden();


        if (showHidden.equals(Settings.SHOWHIDDEN_YES)) {
            showHiddenGroup.check(R.id.showHiddenYes);
        } else if (showHidden.equals(Settings.SHOWHIDDEN_NO)) {
            showHiddenGroup.check(R.id.showHiddenNo);
        } else {
            showHiddenGroup.check(R.id.showHiddenYes);
        }

        showHiddenGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.showHiddenYes) {

                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.SHOWHIDDEN, Settings.SHOWHIDDEN_YES);
                } else if (checkedId == R.id.showHiddenNo) {

                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.SHOWHIDDEN, Settings.SHOWHIDDEN_NO);
                }
            }
        });

        RadioGroup separateGroup = findViewById(R.id.separateGroup);
        String separate = Settings.getSeparate();


        if (separate.equals(Settings.SEPARATE_YES)) {
            separateGroup.check(R.id.separateYes);
        } else if (separate.equals(Settings.SEPARATE_NO)) {
            separateGroup.check(R.id.separateNo);
        } else {
            separateGroup.check(R.id.separateYes);
        }

        separateGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.separateYes) {
                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.SEPARATE, Settings.SEPARATE_YES);
                } else if (checkedId == R.id.separateNo) {
                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.SEPARATE, Settings.SEPARATE_NO);
                }
            }
        });

        RadioGroup truncateSizeGroup = findViewById(R.id.truncateSizeGroup);
        String truncateSize = Settings.getTruncateSize();


        if (truncateSize.equals(Settings.TRUNCATESIZE_YES)) {
            truncateSizeGroup.check(R.id.truncateSizeYes);
        } else if (truncateSize.equals(Settings.TRUNCATESIZE_NO)) {
            truncateSizeGroup.check(R.id.truncateSizeNo);
        } else {
            truncateSizeGroup.check(R.id.truncateSizeYes);
        }

        truncateSizeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.truncateSizeYes) {
                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.TRUNCATESIZE, Settings.TRUNCATESIZE_YES);
                } else if (checkedId == R.id.truncateSizeNo) {
                    FileObserverSingleton.getInstance().writeManagerSettings(Settings.TRUNCATESIZE, Settings.TRUNCATESIZE_NO);
                }
            }
        });
    }

    /**
     * Gets all external root directories.
     *
     * @return the list of all available root directories
     */
    private ArrayList<String> getAllExtRootDirs() {

        File[] allExternal = ContextCompat.getExternalFilesDirs(this, null);

        ArrayList<String> allExternalPaths = new ArrayList<>();

        for (File f : allExternal) {
            if (f == null) {
                continue;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (Environment.isExternalStorageRemovable(f)) {
                    if (Environment.getExternalStorageState(f).equals(Environment.MEDIA_MOUNTED)) {
                        String path = f.getAbsolutePath() + File.separator;
                        allExternalPaths.add(path.split("Android/")[0]);
                    }
                } else {
                    String path = f.getAbsolutePath() + File.separator;
                    allExternalPaths.add(path.split("Android/")[0]);
                }
            } else {
                String path = f.getAbsolutePath() + File.separator;
                allExternalPaths.add(path.split("Android/")[0]);
            }
        }
        return allExternalPaths;

    }

    /**
     * Gets all available root directories.
     *
     * @return the list of all available root directories
     */
    private ArrayList<String> getRootDirs() {
        File rootDir = Environment.getRootDirectory();
        String rootDirPath = rootDir.getAbsolutePath() + File.separator;
        String trueRootPath = File.separator;
        ArrayList<String> rootDirs = new ArrayList<>();
        rootDirs.add(rootDirPath);
        if (!rootDirPath.equals(trueRootPath)) {
            rootDirs.add(trueRootPath);
        }
        return rootDirs;
    }
}
