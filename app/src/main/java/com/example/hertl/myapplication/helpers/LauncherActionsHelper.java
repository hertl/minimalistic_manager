/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.helpers;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.hertl.myapplication.R;
import com.example.hertl.myapplication.activites.AppLauncherActivity;
import com.example.hertl.myapplication.apps.App;
import com.example.hertl.myapplication.settings.SizeTruncator;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * Helper class for the launcher. Includes static classes that are called from the launcher activity.
 */
public class LauncherActionsHelper {

    /**
     * Opens the application in system app manager.
     *
     * @param appLauncherActivity the activity to refer to
     * @param packageName         package name of the app
     */
    public static void openInSystemApp(AppLauncherActivity appLauncherActivity, String packageName) {
        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + packageName));
        appLauncherActivity.startActivity(intent);
    }

    /**
     * The action that happen when show info is chosen. It creates a dialog with all important info
     * about the application and shows it.
     *
     * @param appLauncherActivity the activity to refer to
     * @param app                 the application
     */
    public static void showInfoAction(AppLauncherActivity appLauncherActivity, App app) {
        AlertDialog.Builder builder = new AlertDialog.Builder(appLauncherActivity);
        builder.setTitle(app.getLabel())
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

        LayoutInflater inflater = appLauncherActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_show_info, null);
        builder.setView(dialogView);

        String[] permissions = app.getPermissions();
        StringBuilder permissionsString = new StringBuilder();
        if (permissions == null) {
            permissionsString.append("No permissions needed for this app.");
        } else {
            for (String permission : permissions) {
                permissionsString.append(permission).append("\n");
            }
        }

        TextView versionView = dialogView.findViewById(R.id.version);
        versionView.setText(String.format(Locale.getDefault(), "%s", app.getVersionName()));

        TextView versionCodeView = dialogView.findViewById(R.id.versionCode);
        versionCodeView.setText(String.format(Locale.getDefault(), "%d", app.getVersionCode()));

        TextView packageNameView = dialogView.findViewById(R.id.packageName);
        packageNameView.setText(String.format(Locale.getDefault(), "%s", app.getPackageName()));

        TextView systemAppView = dialogView.findViewById(R.id.systemApp);
        systemAppView.setText(String.format(Locale.getDefault(), "%s", app.isSystemApp() ? "yes" : "no"));

        TextView appSizeView = dialogView.findViewById(R.id.appSize);
        appSizeView.setText(String.format(Locale.getDefault(), "%s", SizeTruncator.sizeToString(app.getApkSize(), false)));

        TextView targetSDKView = dialogView.findViewById(R.id.targetSDK);
        targetSDKView.setText(String.format(Locale.getDefault(), "%d API level", app.getTargetSDK()));

        TextView installedView = dialogView.findViewById(R.id.installed);
        installedView.setText(String.format(Locale.getDefault(), "%s", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(app.getInstalled())));

        TextView updatedView = dialogView.findViewById(R.id.updated);
        updatedView.setText(String.format(Locale.getDefault(), "%s", new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(app.getUpdated())));

        TextView permissionsView = dialogView.findViewById(R.id.permissions);
        permissionsView.setText(String.format(Locale.getDefault(), "%s", permissionsString.toString()));

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * The action to happen after option add filter was chosen. Opens a dialog with text field and
     * sets the filter string.
     *
     * @param appLauncherActivity the activity to refer to
     */
    public static void filterAction(final AppLauncherActivity appLauncherActivity) {
        final EditText filterText = new EditText(appLauncherActivity);

        AlertDialog.Builder filterBuilder = new AlertDialog.Builder(appLauncherActivity)
                .setTitle("Filter Apps:")
                .setView(filterText)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AppLauncherActivity.filterString = filterText.getText().toString();
                        appLauncherActivity.refresh();
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog filterDialog = filterBuilder.create();
        filterDialog.show();

        appLauncherActivity.openKeyboard();
    }
}
