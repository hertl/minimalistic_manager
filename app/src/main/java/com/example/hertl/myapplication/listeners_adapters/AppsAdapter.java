/* Bachelor Thesis - Minimalist File Manager and Launcher for Android OS
 * @author Vojtech Hertl, xhertl04@stud.fit.vutbr.cz
 * Faculty of Information Technology, Brno University of Technology
 */
package com.example.hertl.myapplication.listeners_adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.hertl.myapplication.R;
import com.example.hertl.myapplication.apps.App;

import java.util.ArrayList;

/**
 * The adapter which fills the layout with items representing applications.
 */
public class AppsAdapter extends ArrayAdapter<App> {
    private Context context;
    private ArrayList<App> apps;

    /**
     * Constructor
     *
     * @param apps the list of apps to be filled
     */
    public AppsAdapter(Context context, int resource,
                              ArrayList<App> apps) {
        super(context, resource, apps);
        this.context = context;
        this.apps = apps;

    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        App app = apps.get(position);
        String appLabel;
        if (app.isFavorite()){
            appLabel = "* " + app.getLabel();
        } else {
            appLabel = app.getLabel();
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

        viewHolder.firstLine.setText(appLabel);
        viewHolder.secondLine.setText(app.getPackageName());

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
