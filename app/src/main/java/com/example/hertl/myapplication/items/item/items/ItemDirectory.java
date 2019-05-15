/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.items.item.items;

import com.example.hertl.myapplication.items.item.Item;

import java.io.File;

/**
 * The item representing directory.
 */
public class ItemDirectory extends Item {

    /**
     * Constructor.
     *
     * @param file the File object representing the directory
     */
    public ItemDirectory(File file) {
        super(file);
        this.secondLineText = this.secondLineText();
    }

    @Override
    public String secondLineText() {
        try {
            int numOfSubItems = this.file.list().length;

            return "Dir - " + numOfSubItems + " item(s)";
        } catch (NullPointerException e) {
            e.printStackTrace();
            return "Dir - unidentified item(s)";
        }
    }
}
