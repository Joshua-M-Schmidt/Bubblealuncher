package source.nova.com.bubblelauncherfree.IconPackSettings;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.DataObj;

public class IconPackListAdapter extends ArrayAdapter<DataObj> {
    public IconPackListAdapter(Context context, ArrayList<DataObj> apps) {
        super(context, 0, apps);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        DataObj app = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.icon_pack_item, parent, false);
        }
        // Lookup view for data population

        TextView appName =  convertView.findViewById(R.id.app_name);

        ImageView appicon = convertView.findViewById(R.id.app_icon);
        // Populate the data into the template view using the data object
        appName.setText(app.name);
        appicon.setImageDrawable(app.drawable);

        // Return the completed view to render on screen
        return convertView;
    }
}


