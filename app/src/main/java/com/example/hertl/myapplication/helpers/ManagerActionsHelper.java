/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.helpers;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.FileProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.hertl.myapplication.R;
import com.example.hertl.myapplication.activites.MainActivity;
import com.example.hertl.myapplication.exception.SomethingWrongException;
import com.example.hertl.myapplication.items.item.Item;
import com.example.hertl.myapplication.items.item.items.ItemDirectory;
import com.example.hertl.myapplication.items.item.items.ItemFile;
import com.example.hertl.myapplication.settings.SizeTruncator;
import com.example.hertl.myapplication.singletons.FileOperatorSingleton;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

/**
 * Helper class for the file manager. Includes static classes that are called from the main activity.
 */
public class ManagerActionsHelper {

    /**
     * Initializes error and doesn't allow user to continue if the permissions were not allowed.
     *
     * @param mainActivity the activity to refer to
     */
    public static void initError(final Activity mainActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity)
                .setTitle("Error")
                .setMessage("Can not show external storage without permission, please reload this app and allow permission.")
                .setPositiveButton(mainActivity.getString(R.string.ok_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mainActivity.finish();
                    }
                });
        builder.setCancelable(false);
        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Creates a dialog to confirm the deletion of selected items.
     *
     * @param mainActivity    the activity to refer to
     * @param deleteFilePaths the paths to be deleted
     */
    private static void deleteMoreConfirm(final MainActivity mainActivity, final ArrayList<String> deleteFilePaths) {
        AlertDialog.Builder fileBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle(String.valueOf(deleteFilePaths.size()) + " item(s)")
                .setMessage("Do you really want to delete " + String.valueOf(deleteFilePaths.size()) + " item(s)?")
                .setPositiveButton(mainActivity.getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            File deleteFile;
                            for (String f : deleteFilePaths) {
                                deleteFile = new File(f);
                                if (!deleteFile.isDirectory()) {
                                    delete(deleteFile);
                                } else {
                                    delete(deleteFile);
                                }
                            }
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        mainActivity.refresh();
                    }
                })
                .setNegativeButton(mainActivity.getString(R.string.no_button), null);

        AlertDialog dialog = fileBuilder.create();
        dialog.show();
    }

    /**
     * The action to happen after the option delete was chosen.
     *
     * @param mainActivity the activity to refer to
     * @param fileName     name of the file
     * @param file         the actual file
     */
    public static void deleteAction(final MainActivity mainActivity, String fileName, File file) {
        if (file.isDirectory()) {
            if (file.list().length > 0) {
                notEmptyDir(mainActivity, fileName, file);
            } else {
                deleteConfirm(mainActivity, fileName, file);
            }
        } else {
            deleteConfirm(mainActivity, fileName, file);
        }
    }

    /**
     * Delete confirmation dialog.
     *
     * @param mainActivity the activity to refer to
     * @param fileName     name of the file
     * @param file         the actual file
     */
    private static void deleteConfirm(final MainActivity mainActivity, String fileName, final File file) {
        AlertDialog.Builder fileBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle(fileName)
                .setMessage("Do you really want to delete " + fileName + "?")
                .setPositiveButton(mainActivity.getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        try {
                            delete(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        mainActivity.refresh();

                    }
                })
                .setNegativeButton(mainActivity.getString(R.string.no_button), null);

        AlertDialog dialog = fileBuilder.create();
        dialog.show();
    }

    /**
     * The action to happen after the option rename was chosen.
     *
     * @param mainActivity the activity to refer to
     * @param fileName     name of the file
     */
    public static void renameAction(final MainActivity mainActivity, final String fileName) {
        final EditText name = new EditText(mainActivity);

        name.setText(fileName);

        String fileExt = getFileExtension(fileName);
        if (fileExt != null) {
            name.setSelection(fileName.indexOf(fileExt) - 1);
        } else {
            name.setSelection(name.getText().length());
        }

        AlertDialog.Builder dirBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle("New name:")
                .setView(name)
                .setPositiveButton("Rename", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            renameActionCheck(mainActivity, name, fileName);
                        } catch (SomethingWrongException e) {
                            e.printStackTrace();
                            mainActivity.toastMsg(e.getMessage());
                        }
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dirDialog = dirBuilder.create();
        dirDialog.show();
        mainActivity.openKeyboard();
    }


    /**
     * Checks if the rename action can be done. If not, throws the exception with specified message.
     *
     * @param mainActivity the activity to refer to
     * @param name         the name to rename to
     * @param fileName     name of the file
     */
    private static void renameActionCheck(final MainActivity mainActivity, EditText name, String fileName) throws SomethingWrongException {
        String newName = name.getText().toString();

        File before = new File(mainActivity.currentPath + fileName);
        File after = new File(mainActivity.currentPath + newName);

        if (after.exists()) {
            renameRewriteConfirm(mainActivity, before, after);
        } else {
            renameFinish(mainActivity, before, after);
        }
    }

    /**
     * Confirmation dialog to rewrite if renaming.
     *
     * @param mainActivity the activity to refer to
     * @param before       representing the file before renaming
     * @param after        representing the file after renaming
     */
    private static void renameRewriteConfirm(final MainActivity mainActivity, final File before, final File after) {
        AlertDialog.Builder fileBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle(before.getName() + " -> " + after.getName())
                .setMessage("There is already file with this name, do you want to rewrite it?")
                .setPositiveButton(mainActivity.getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            renameFinish(mainActivity, before, after);
                        } catch (SomethingWrongException e) {
                            e.printStackTrace();
                            mainActivity.toastMsg(e.getMessage());
                        }
                    }
                })
                .setNegativeButton(mainActivity.getString(R.string.no_button), null);

        AlertDialog dialog = fileBuilder.create();
        dialog.show();
    }

    /**
     * Finishing the rename action.
     *
     * @param mainActivity the activity to refer to
     * @param before       representing the file before renaming
     * @param after        representing the file after renaming
     */
    private static void renameFinish(final MainActivity mainActivity, File before, File after) throws SomethingWrongException {
        if (before.renameTo(after)) {
            mainActivity.toastMsg(before.getName() + " renamed to " + after.getName());
        } else {
            throw new SomethingWrongException("Can not be renamed.");
        }
        mainActivity.refresh();
    }

    /**
     * The action to happen after option open with was chosen.
     *
     * @param mainActivity the activity to refer to
     * @param file         the actual file
     */
    public static void openWithAction(final MainActivity mainActivity, File file) {
        Uri uri;
        try {
            uri = FileProvider.getUriForFile(mainActivity, mainActivity.getApplicationContext().getPackageName() + ".fileprovider", file);
        } catch (Exception e) {
            try {
                uri = Uri.fromFile(file.getAbsoluteFile());
            } catch (Exception e1) {
                e1.printStackTrace();
                mainActivity.toastMsg("Couldn't get URI of this file.");
                return;
            }
        }
        Intent newIntent = new Intent(Intent.ACTION_VIEW);
        String mimeType = "*/*";
        newIntent.setDataAndType(uri, mimeType);
        newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent chooser = Intent.createChooser(newIntent, "Pick");
        mainActivity.startActivity(chooser);
    }

    /**
     * Alert that the directory is not empty at deleting action.
     *
     * @param mainActivity the activity to refer to
     * @param fileName     the name of the file
     * @param file         the file to be deleted
     */
    private static void notEmptyDir(final MainActivity mainActivity, final String fileName, final File file) {
        AlertDialog.Builder fileBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle(fileName)
                .setMessage("This directory is not empty, do you want to delete anyways?")
                .setPositiveButton(mainActivity.getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            delete(file);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        mainActivity.refresh();
                    }
                })
                .setNegativeButton(mainActivity.getString(R.string.no_button), null);

        AlertDialog dialog = fileBuilder.create();
        dialog.show();
    }

    /**
     * The action to happen after option properties in select more mode was chosen.
     *
     * @param mainActivity the activity to refer to
     * @param listView     the listview to get the positions of the selected items
     */
    public static void propertiesMoreAction(final MainActivity mainActivity, final ListView listView) {

        /**
         * AsyncTask to do process it all on the background thread, meanwhile the user can see how
         * many items were processed already.
         */
        class LoadPropertiesMoreTask extends AsyncTask<Void, Integer, String> {
            private ProgressBar progressBar;
            private TextView progressText;

            private long size = 0;
            private int count = 0;
            private int max = 0;

            @Override
            protected void onPreExecute() {
                mainActivity.setContentView(R.layout.loading);
                progressBar = mainActivity.findViewById(R.id.loadingBar);
                progressBar.setProgress(0);

                Item item;
                for (int i = 0; i < listView.getCount(); i++) {
                    item = (Item) listView.getItemAtPosition(i);
                    if (item == null) {
                        break;
                    }
                    if (item.isSelected()) {
                        max++;
                    }
                }
                progressBar.setMax(max);
                progressText = mainActivity.findViewById(R.id.loadingText);
                progressText.setText("0/" + max);
                count = 0;
                size = 0;
            }

            @Override
            protected String doInBackground(Void... s) {

                Item item;
                for (int i = 0; i < listView.getCount(); i++) {
                    item = (Item) listView.getItemAtPosition(i);
                    if (item == null) {
                        break;
                    }
                    if (item.isSelected()) {
                        count++;
                        publishProgress(count);
                        if (item instanceof ItemFile) {
                            size += item.getSize();
                        } else if (item instanceof ItemDirectory) {
                            size += calculateSizeAndNumOfSubItems(item.getFile())[0];
                        }
                    }
                }
                return "End";
            }

            @Override
            protected void onProgressUpdate(Integer... progress) {
                progressBar.setProgress(progress[0]);
                progressText.setText(progress[0] + "/" + max);
            }

            @Override
            protected void onPostExecute(String result) {
                setMoreProperties(mainActivity, size, count);
            }
        }
        new LoadPropertiesMoreTask().execute();
    }

    /**
     * The action to happen after option delete in select more mode was chosen.
     *
     * @param mainActivity the activity to refer to
     * @param listView     the listview to get the positions of the selected items
     */
    public static void deleteMoreAction(final MainActivity mainActivity, ListView listView) {
        Item item;
        ArrayList<String> deleteFilesPaths = new ArrayList<>();
        for (int i = 0; i < listView.getCount(); i++) {
            item = (Item) listView.getItemAtPosition(i);
            if (item == null) {
                break;
            }
            if (item.isSelected()) {
                deleteFilesPaths.add(item.getFile().getAbsolutePath());
            }
        }
        deleteMoreConfirm(mainActivity, deleteFilesPaths);
    }

    /**
     * The action to happen after option show clipboard was chosen. It shows the optimized paths in
     * the clipboard file. Show the result in dialog.
     *
     * @param mainActivity    the activity to refer to
     * @param clipboardHelper the clipboard helper which contains all clipboard info
     */
    public static void clipboardShowAction(final MainActivity mainActivity, ClipboardHelper clipboardHelper) {
        AlertDialog.Builder clipBuilder = new AlertDialog.Builder(mainActivity);
        clipBuilder.setTitle("Clipboard")
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.clipboard_info, null);

        clipBuilder.setView(dialogView);

        StringBuilder clipboardPathsBuilder = new StringBuilder();
        for (String str : clipboardHelper.getPaths()) {
            clipboardPathsBuilder.append(str).append("\n\n");
        }

        TextView clipboardSizeView = dialogView.findViewById(R.id.clipboardSize);
        clipboardSizeView.setText(String.format(Locale.getDefault(), "%d path(s)", clipboardHelper.checkClipboardSize()));

        TextView clipboardPathsView = dialogView.findViewById(R.id.clipboardPaths);
        clipboardPathsView.setText(String.format(Locale.getDefault(), "%s", clipboardPathsBuilder.toString()));

        AlertDialog clipDialog = clipBuilder.create();
        clipDialog.show();
    }

    /**
     * The action to happen after option select all in select more mode was chosen. it marks all items
     * as selected.
     *
     * @param mainActivity the activity to refer to
     * @param listView     the listview to get the positions of the selected items
     */
    public static void selectAllAction(final MainActivity mainActivity, ListView listView) {
        Item item;
        for (int i = 0; i < listView.getCount(); i++) {
            item = (Item) listView.getItemAtPosition(i);
            if (item == null) {
                break;
            }
            if (!item.isSelected()) {
                item.select();
                item.position = i;
            }
        }
        mainActivity.adapter.notifyDataSetChanged();
    }

    /**
     * The action to happen after option add filter was chosen. Opens a dialog with text field and
     * sets the filter string.
     *
     * @param mainActivity the activity to refer to
     */
    public static void filterAction(final MainActivity mainActivity) {
        final EditText filterText = new EditText(mainActivity);

        AlertDialog.Builder filterBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle("Filter Directory:")
                .setView(filterText)
                .setPositiveButton("Apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.filterString = filterText.getText().toString();
                        mainActivity.refresh();
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog filterDialog = filterBuilder.create();
        filterDialog.show();

        mainActivity.openKeyboard();
    }

    /**
     * The action to happen after option new directory was chosen. Opens a dialog with text field and
     * creates new directory.
     *
     * @param mainActivity the activity to refer to
     */
    public static void newDirAction(final MainActivity mainActivity) {
        final EditText dirName = new EditText(mainActivity);

        AlertDialog.Builder dirBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle("New directory name:")
                .setView(dirName)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newDirName = dirName.getText().toString();

                        if (newDirName.contains("/")) {
                            mainActivity.toastMsg("New directory can not have \"/\" in its name.");
                            return;
                        }
                        String fullName = mainActivity.currentPath + newDirName;
                        File dir = new File(fullName);
                        if (dir.mkdir()) {
                            mainActivity.toastMsg(fullName + " Directory created");
                        } else {
                            mainActivity.toastMsg(fullName + " Already exists");
                        }
                        mainActivity.refresh();
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dirDialog = dirBuilder.create();
        dirDialog.show();

        mainActivity.openKeyboard();
    }

    /**
     * The action to happen after option new file was chosen. Opens a dialog with text field and
     * creates new file.
     *
     * @param mainActivity the activity to refer to
     */
    public static void newFileAction(final MainActivity mainActivity) {
        final EditText fileName = new EditText(mainActivity);

        AlertDialog.Builder fileBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle("New file name:")
                .setView(fileName)
                .setPositiveButton("Create", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newFileName = fileName.getText().toString();

                        if (newFileName.contains("/")) {
                            mainActivity.toastMsg("New file can not have \"/\" in its name.");
                            return;
                        }
                        String fullName = mainActivity.currentPath + newFileName;
                        File file = new File(fullName);
                        try {
                            if (file.createNewFile()) {
                                mainActivity.toastMsg(fullName + " file created");
                            } else {
                                mainActivity.toastMsg(fullName + " already exists");
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            mainActivity.toastMsg("Could not create this file.");
                        }
                        mainActivity.refresh();
                    }
                })
                .setNegativeButton("Cancel", null);

        AlertDialog dialog = fileBuilder.create();
        dialog.show();

        mainActivity.openKeyboard();
    }

    /**
     * Sets and shows properties in select more mode.
     *
     * @param mainActivity the activity to refer to
     * @param size         the size of all selected items
     * @param count        the number of all selected items
     */
    private static void setMoreProperties(final MainActivity mainActivity, long size, int count) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Properties")
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        mainActivity.refresh();
                    }
                });
        builder.setCancelable(false);
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_properties_more, null);

        builder.setView(dialogView);

        TextView totalSizeView = dialogView.findViewById(R.id.totalSize);
        totalSizeView.setText(String.format(Locale.getDefault(), "%s", SizeTruncator.sizeToString(size, true)));

        TextView numOfItemsView = dialogView.findViewById(R.id.numOfItems);
        numOfItemsView.setText(String.format(Locale.getDefault(), "%d item(s)", count));

        AlertDialog dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Shows the properties dialog for one item.
     *
     * @param mainActivity the activity to refer to
     * @param fileName     the name of the item
     */
    public static void createPropertiesDialog(final MainActivity mainActivity, String fileName) {
        File file = new File(mainActivity.currentPath + fileName);
        String lastModified = new Date(file.lastModified()).toLocaleString();

        AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setTitle("Properties")
                .setPositiveButton(R.string.ok_button, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        LayoutInflater inflater = mainActivity.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_properties, null);

        builder.setView(dialogView);

        if (!file.isDirectory()) {
            setFileProperties(mainActivity, file, fileName, lastModified, dialogView);
        } else {
            setDirProperties(mainActivity, file, fileName, lastModified, dialogView);
        }
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * Sets the directory properties.
     *
     * @param mainActivity the activity to refer to
     * @param dir          the directory
     * @param dirName      the name of the directory
     * @param lastModified the last modified date in string format
     * @param dialogView   the dialog from xml file
     */
    private static void setDirProperties(final MainActivity mainActivity, File dir, String dirName, String lastModified, View dialogView) {
        long[] sizeAndNumOfSubItems = calculateSizeAndNumOfSubItems(dir);

        TextView sizeView = dialogView.findViewById(R.id.size);
        sizeView.setText(String.format(Locale.getDefault(), "%s", SizeTruncator.sizeToString(sizeAndNumOfSubItems[0], true)));

        TextView numOfSubItemsCaptionView = dialogView.findViewById(R.id.numOfSubItemsCaption);
        numOfSubItemsCaptionView.setVisibility(View.VISIBLE);

        TextView numOfSubItemsView = dialogView.findViewById(R.id.numOfSubItems);
        numOfSubItemsView.setVisibility(View.VISIBLE);
        numOfSubItemsView.setText(String.format(Locale.getDefault(), "%d item(s)", sizeAndNumOfSubItems[1]));

        ImageView numOfSubItemsLineView = dialogView.findViewById(R.id.numOfSubItemsLine);
        numOfSubItemsLineView.setVisibility(View.VISIBLE);

        TextView fileViewCaptionView = dialogView.findViewById(R.id.filenameCaption);
        fileViewCaptionView.setText(String.format(Locale.getDefault(), "%s", "Directory name:"));
        TextView filenameView = dialogView.findViewById(R.id.filename);
        filenameView.setText(String.format(Locale.getDefault(), "%s", dirName));

        TextView pathView = dialogView.findViewById(R.id.path);
        pathView.setText(String.format(Locale.getDefault(), "%s", mainActivity.currentPath));

        TextView lastModifiedView = dialogView.findViewById(R.id.lastModified);
        lastModifiedView.setText(String.format(Locale.getDefault(), "%s", lastModified));
    }

    /**
     * Sets the file properties.
     *
     * @param mainActivity the activity to refer to
     * @param file         the file
     * @param fileName     the name of the file
     * @param lastModified the last modified date in string format
     * @param dialogView   the dialog from xml file
     */
    private static void setFileProperties(final MainActivity mainActivity, File file, String fileName, String lastModified, View dialogView) {
        long size = Integer.parseInt(String.valueOf(file.length()));

        TextView sizeView = dialogView.findViewById(R.id.size);
        sizeView.setText(String.format(Locale.getDefault(), "%s", SizeTruncator.sizeToString(size, true)));

        TextView filenameView = dialogView.findViewById(R.id.filename);
        filenameView.setText(String.format(Locale.getDefault(), "%s", fileName));

        TextView pathView = dialogView.findViewById(R.id.path);
        pathView.setText(String.format(Locale.getDefault(), "%s", mainActivity.currentPath));

        TextView lastModifiedView = dialogView.findViewById(R.id.lastModified);
        lastModifiedView.setText(String.format(Locale.getDefault(), "%s", lastModified));
    }

    /**
     * Pastes one item.
     *
     * @param mainActivity the activity to refer to
     */
    public static void pasteOne(MainActivity mainActivity) throws SomethingWrongException {
        String pathToCopyTo = mainActivity.currentPath;
        File copiedFile = FileOperatorSingleton.getInstance().getCopiedFile();
        File out = new File(pathToCopyTo + copiedFile.getName());

        if (pathToCopyTo.contains(copiedFile.getAbsolutePath())) {
            throw new SomethingWrongException("This directory is a subdirectory of copied path.");
        }
        if (FileOperatorSingleton.getInstance().isCopy()) {
            paste(mainActivity, copiedFile, out);
        } else {
            pasteMove(copiedFile, out);
            FileOperatorSingleton.getInstance().setStartingPath(null);
        }
        mainActivity.refresh();
    }

    /**
     * Pastes whole clipboard.
     *
     * @param mainActivity the activity to refer to
     * @param paths        all the paths from clipboard
     * @param isMove       if the items should be moved or copied
     */
    public static void pasteClipboard(final MainActivity mainActivity, ArrayList<String> paths, boolean isMove) throws SomethingWrongException {
        String pathToCopyTo = mainActivity.currentPath;
        if (paths.size() == 0) {
            throw new SomethingWrongException("The clipboard is empty.");
        }
        for (String path : paths) {
            if (pathToCopyTo.contains(path)) {
                throw new SomethingWrongException("This directory is a subdirectory of at least one path from clipboard.");
            }
        }
        for (String path : paths) {
            File in = new File(path);
            File out = new File(pathToCopyTo + in.getName());

            if (!isMove) {
                paste(mainActivity, in, out);
            } else {
                pasteMove(in, out);
            }
        }
        mainActivity.refresh();
    }

    /**
     * Recursive method to paste in move mode (rename).
     *
     * @param in  the old item
     * @param out the new item
     */
    private static void pasteMove(File in, File out) throws SomethingWrongException {
        if (in.isDirectory()) {
            out.mkdir();
            for (File inChild : in.listFiles()) {
                pasteMove(inChild, new File(out + File.separator + inChild.getName()));
            }
            in.delete();
        } else {
            if (!in.renameTo(out)) {
                throw new SomethingWrongException("The item " + in.getAbsolutePath() + " could not be moved.");
            }
        }
    }

    /**
     * Paste in copy mode
     *
     * @param mainActivity the activity to refer to
     * @param in           the old item
     * @param out          the new item
     */
    private static void paste(final MainActivity mainActivity, final File in, final File out) {

        /**
         * AsyncTask to do the work in the background. Meanwhile user can do anything he wants.
         * After the work is finished, it returns him to this directory and says that the work was
         * finished.
         */
        class PasteTask extends AsyncTask<Void, Integer, String> {
            private Exception e = null;

            @Override
            protected String doInBackground(Void... s) {
                try {
                    pasteBackground(in, out);
                } catch (Exception e1) {
                    e = e1;
                }
                return "End";
            }

            @Override
            protected void onPostExecute(String result) {
                if (e != null) {
                    e.printStackTrace();
                    mainActivity.toastMsg(e.getMessage());
                }
                mainActivity.toastMsg("Pasting finished.");
                mainActivity.refresh();
            }
        }
        new PasteTask().execute();
    }

    /**
     * Recursive method which copies the selected item. This method is called in the background thread.
     *
     * @param in  the old item
     * @param out the new item
     */
    private static void pasteBackground(final File in, final File out) throws SomethingWrongException, IOException {
        if (!isEnoughSpace(in, out)) {
            throw new SomethingWrongException("There is not enough space in this storage.");
        }

        if (in.isDirectory()) {
            if (!out.exists()) {
                if (!out.mkdir()) {
                    throw new SomethingWrongException("Directory could not be created.");
                }
            }
            for (File c : in.listFiles()) {
                pasteBackground(new File(in, c.getName()), new File(out, c.getName()));
            }

        } else {
            if (out.exists()) {
                delete(out);
            }
            pasteFileWrite(in, out);
        }
    }

    /**
     * Creates a dialog which alerts the user that some files will be rewritten while pasting from clipboard.
     *
     * @param mainActivity the activity to refer to
     * @param paths        the list of paths to copy
     * @param isMove       check if the operation is move or copy
     */
    public static void pasteClipboardRewriteConfirm(final MainActivity mainActivity, final ArrayList<String> paths, final boolean isMove) {
        AlertDialog.Builder rewriteBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle("Rewrite")
                .setMessage("There is at least one file that will be rewritten, do you want to continue?")
                .setPositiveButton(mainActivity.getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            pasteClipboard(mainActivity, paths, isMove);
                        } catch (SomethingWrongException e) {
                            e.printStackTrace();
                            mainActivity.toastMsg(e.getMessage());
                        }
                    }
                })
                .setNegativeButton(mainActivity.getString(R.string.no_button), null);

        AlertDialog dialog = rewriteBuilder.create();
        dialog.show();
    }

    /**
     * Checks  recursively if some item will be rewritten.
     *
     * @param firstFile the file to be copied
     * @param secondDir the directory to which will be the file copied
     */
    public static void checkRewrite(final File firstFile, final File secondDir) throws SomethingWrongException {
        for (File f1 : secondDir.listFiles()) {
            if (f1.getName().equals(firstFile.getName())) {
                if (firstFile.isFile()) {
                    throw new SomethingWrongException("Some files will be rewritten.");
                } else {
                    if (f1.isDirectory()) {
                        for (File f2 : firstFile.listFiles()) {
                            checkRewrite(f2, f1);
                        }
                    } else {
                        throw new SomethingWrongException("Some files will be rewritten.");
                    }
                }
            }
        }
    }

    /**
     * Creates a dialog which alerts the user that some files will be rewritten while pasting one item.
     *
     * @param mainActivity the activity to refer to
     */
    public static void pasteOneRewriteConfirm(final MainActivity mainActivity) {
        AlertDialog.Builder rewriteBuilder = new AlertDialog.Builder(mainActivity)
                .setTitle("Rewrite")
                .setMessage("There is at least one file that will be rewritten, do you want to continue?")
                .setPositiveButton(mainActivity.getString(R.string.yes_button), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            pasteOne(mainActivity);
                        } catch (SomethingWrongException e) {
                            e.printStackTrace();
                            mainActivity.toastMsg(e.getMessage());
                        }
                    }
                })
                .setNegativeButton(mainActivity.getString(R.string.no_button), null);

        AlertDialog dialog = rewriteBuilder.create();
        dialog.show();
    }


    /**
     * The actual paste method.
     *
     * @param in  the old item
     * @param out the new item
     */
    private static void pasteFileWrite(File in, File out) throws IOException, SomethingWrongException {
        if (out.exists()) {
            throw new SomethingWrongException("This file already exists.");
        }
        InputStream inputStream = new FileInputStream(in.getAbsolutePath());
        OutputStream outputStream = new FileOutputStream(out.getAbsolutePath());

        byte[] buf = new byte[1024];
        int len;
        while ((len = inputStream.read(buf)) > 0) {
            outputStream.write(buf, 0, len);
        }
        inputStream.close();
        outputStream.close();
    }

    /**
     * The actual delete recursive method.
     *
     * @param file the item to be deleted
     */
    private static void delete(final File file) throws FileNotFoundException {
        if (file.isDirectory()) {
            for (File c : file.listFiles()) {
                delete(c);
            }
        }
        if (!(file.delete())) {
            throw new FileNotFoundException("Failed to delete " + file);
        }
    }

    /**
     * Gets the file extension from the url.
     *
     * @param url the url of the file.
     * @return the extension
     */
    public static String getFileExtension(String url) {
        if (url.substring(0, 1).equals(".")) {
            url = url.substring(1);
        }
        if (url.lastIndexOf(".") == -1) {
            return null;
        } else {
            String ext = url.substring(url.lastIndexOf(".") + 1);
            if (ext.contains("%")) {
                ext = ext.substring(0, ext.indexOf("%"));
            }
            if (ext.contains("/")) {
                ext = ext.substring(0, ext.indexOf("/"));
            }
            return ext.toLowerCase();
        }
    }

    /**
     * Calculates both total size and number of sub items of a directory.
     *
     * @param dir the directory
     * @return array of sizes and number of sub items
     */
    private static long[] calculateSizeAndNumOfSubItems(File dir) {
        long size = 0;
        long numOfSubFiles = 0;

        List<File> dirs = new LinkedList<>();
        dirs.add(dir);

        while (!dirs.isEmpty()) {
            File d = dirs.remove(0);
            if (!d.exists()) {
                continue;
            }
            File[] listFiles = d.listFiles();
            if (listFiles == null || listFiles.length == 0) {
                continue;
            }
            for (File subFile : listFiles) {
                numOfSubFiles++;

                if (subFile.isDirectory()) {
                    dirs.add(subFile);
                } else {
                    size += subFile.length();
                }
            }
        }
        return new long[]{size, numOfSubFiles};
    }

    /**
     * Checks if there is enough space in a storage to be pasted to.
     *
     * @param in  the old item
     * @param out the new item
     * @return the result
     */
    private static boolean isEnoughSpace(File in, File out) {
        long usableSpace = out.getParentFile().getUsableSpace();
        long sizeOfCopiedItem;
        if (in.isFile()) {
            sizeOfCopiedItem = in.length();
        } else {
            sizeOfCopiedItem = calculateSizeAndNumOfSubItems(in)[0];
        }
        return sizeOfCopiedItem <= usableSpace - 10000;
    }
}
