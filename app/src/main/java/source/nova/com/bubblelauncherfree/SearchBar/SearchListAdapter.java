package source.nova.com.bubblelauncherfree.SearchBar;

import android.content.Context;
import android.graphics.Outline;
import androidx.core.content.ContextCompat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Util;

/**
 * Created by joshua on 12.04.18.
 */

public class SearchListAdapter extends ArrayAdapter<DataObj> {
    AppManager appManager;
    Context ctx;

    String selectedPackages[];

    public SearchListAdapter(Context context, ArrayList<DataObj> apps) {
        super(context, 0, apps);
        appManager = new AppManager(context);
        ctx = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        DataObj app = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.app_item, parent, false);
        }

        // Lookup view for data population

        TextView appName =  convertView.findViewById(R.id.app_name);

        ImageView appicon = convertView.findViewById(R.id.app_icon);
        // Populate the data into the template view using the data object
        appName.setText(app.name);
        if(app.drawable == null){
            appicon.setImageBitmap(appManager.getAppIcon(app.package_name,MainActivity.asize,ctx));
        }else{
            appicon.setImageDrawable(app.drawable);
        }

        if(app.selected){
            convertView.setBackground(ContextCompat.getDrawable(ctx, R.drawable.buy_button));
        }else{
            convertView.setBackground(null);
        }

        // Return the completed view to render on screen

        final int width = convertView.getWidth();
        final int height = convertView.getHeight();
        convertView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0,0,width,height, Util.getDiam(getContext())/5);
            }
        });
        //convertView.setClipToOutline(true);
        return convertView;
    }
}