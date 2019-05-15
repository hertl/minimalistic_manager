/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.listeners_adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hertl.myapplication.R;
import com.example.hertl.myapplication.helpers.ManagerActionsHelper;
import com.example.hertl.myapplication.items.item.Item;
import com.example.hertl.myapplication.items.item.items.ItemFile;
import com.example.hertl.myapplication.settings.Settings;

import java.util.ArrayList;
import java.util.List;

/**
 * The adapter which fills the layout with items representing files, directories and level up.
 */
public class ItemAdapter extends ArrayAdapter<Item> {
    private Context context;
    private List<Item> items;

    /**
     * Constructor.
     *
     * @param items the items to be filled
     */
    public ItemAdapter(Context context, int resource,
                       ArrayList<Item> items) {
        super(context, resource, items);
        this.context = context;
        this.items = items;
    }

    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder;

        Item item = items.get(position);

        String name = item.getName();
        if (Settings.getShowExtension().equals("no") && item instanceof ItemFile) {
            String ext = ManagerActionsHelper.getFileExtension(name);
            if (ext != null) {
                name = name.replace("." + ext, "");
            }
        }

        if (convertView == null) { // recycling and reusing with view holder
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.firstLine = convertView.findViewById(R.id.text1);
            viewHolder.secondLine = convertView.findViewById(R.id.text2);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.firstLine.setText(name);
        if (item.getFile().getParent() != null) {
            viewHolder.secondLine.setText(item.secondLineText);
        }

        if (item.isSelected()) {
            convertView.setBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
        }

        return convertView;
    }

    /**
     * View holder pattern to make scrolling smooth
     */
    private static class ViewHolder {
        private TextView firstLine;
        private TextView secondLine;
    }

}
