/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.items;

/**
 * The interface for all versions of item of file manager.
 */
public interface ItemInterface {

    /**
     * Info to be written on the second line of a Item.
     *
     * @return The text.
     */
    String secondLineText();
}
