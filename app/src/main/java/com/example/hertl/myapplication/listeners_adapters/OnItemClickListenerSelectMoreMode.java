/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.listeners_adapters;

import android.view.View;
import android.widget.AdapterView;

import com.example.hertl.myapplication.activites.MainActivity;
import com.example.hertl.myapplication.items.item.Item;

/**
 * Listener for the main activity in select more mode.
 */
public class OnItemClickListenerSelectMoreMode implements AdapterView.OnItemClickListener {

    private MainActivity mainActivity;

    public OnItemClickListenerSelectMoreMode(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, final View v,
                            int position, long id) {

        Item item = (Item) parent.getItemAtPosition(position);
        if (item.isSelected()) {
            item.deselect();
        } else {
            item.select();
        }
        item.position = position;
        mainActivity.adapter.notifyDataSetChanged();
    }
}
