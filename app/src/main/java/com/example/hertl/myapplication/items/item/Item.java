/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.items.item;

import com.example.hertl.myapplication.helpers.ManagerActionsHelper;
import com.example.hertl.myapplication.items.ItemInterface;
import com.example.hertl.myapplication.items.item.items.ItemDirectory;

import java.io.File;
import java.util.Date;

/**
 * The abstract class for all versions of item of file manager.
 */
public abstract class Item implements ItemInterface {
    public int position;
    public String secondLineText;
    protected String name;
    protected File file;
    private boolean selected;

    /**
     * Constructor. Fills the important info.
     *
     * @param file the file or directory which represents the item
     */
    public Item(File file) {
        this.name = file.getName();
        this.file = file;
        this.selected = false;
    }

    /**
     * Gets the name of the item.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the date of last modification of the item.
     *
     * @return the date
     */
    public Date getLastModified() {
        return new Date(file.lastModified());
    }

    /**
     * Gets the size of the file.
     *
     * @return the size
     */
    public long getSize() {
        return Integer.parseInt(String.valueOf(file.length()));
    }

    /**
     * Gets the extension of the file.
     *
     * @return the extension
     */
    public String getExt() {
        String ext = ManagerActionsHelper.getFileExtension(name);
        if (ext == null || this instanceof ItemDirectory) {
            return "";
        }
        return ext;
    }

    /**
     * Checks if the item is selected in select more mode.
     *
     * @return the result
     */
    public boolean isSelected() {
        return selected;
    }

    /**
     * Marks the item as selected in select more mode.
     */
    public void select() {
        this.selected = true;
    }

    /**
     * Marks the item as deselected in select more mode.
     */
    public void deselect() {
        this.selected = false;
    }

    /**
     * Gets the File object representing this item.
     *
     * @return the File object
     */
    public File getFile() {
        return file;
    }
}
