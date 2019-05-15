/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.listeners_adapters;

import android.view.View;
import android.widget.AdapterView;

import com.example.hertl.myapplication.activites.MainActivity;
import com.example.hertl.myapplication.items.item.Item;
import com.example.hertl.myapplication.items.item.items.ItemUp;

import java.io.File;

/**
 * Listener for the main activity in normal mode.
 */
public class OnItemClickListenerNormalMode implements AdapterView.OnItemClickListener {

    private MainActivity mainActivity;

    public OnItemClickListenerNormalMode(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View v,
                            int position, long id) {

        Item item = (Item) parent.getItemAtPosition(position);
        String fullName = item.getFile().getAbsolutePath();

        if (item instanceof ItemUp && !mainActivity.isRootPath()){
            mainActivity.levelUp();
        } else {
            if (new File(fullName).isDirectory()) {
                mainActivity.levelDown(fullName);
            } else {
                mainActivity.openFileChooser(item);
            }
        }
    }
}
