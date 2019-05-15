/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.items.item.items;

import com.example.hertl.myapplication.items.item.Item;

import java.io.File;

/**
 * The item representing item to go level up in the file system.
 */
public class ItemUp extends Item {

    /**
     * Constructor.
     *
     * @param file the File object representing the item
     */
    public ItemUp(File file) {
        super(file);
        this.name = "Level Up ▲";
        this.secondLineText = this.secondLineText();
    }

    @Override
    public String secondLineText() {
        return this.file.getParent() + File.separator;
    }
}
