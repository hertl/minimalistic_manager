/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.items.item.items;

import com.example.hertl.myapplication.helpers.ManagerActionsHelper;
import com.example.hertl.myapplication.items.item.Item;
import com.example.hertl.myapplication.settings.SizeTruncator;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * The item representing file.
 */
public class ItemFile extends Item {

    /**
     * Constructor.
     *
     * @param file the File object representing the file
     */
    public ItemFile(File file) {
        super(file);
        this.secondLineText = secondLineText();
    }

    @Override
    public String secondLineText() {
        String ext = ManagerActionsHelper.getFileExtension(this.getName());
        if (ext == null) {
            ext = "no ext";
        }
        String lastModified = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(getLastModified());
        return "File - " + ext + " | " + lastModified + " | " + SizeTruncator.sizeToString(getSize(), true);
    }
}
