package source.nova.com.bubblelauncherfree.StartConfigActivities;

import android.content.Context;
import android.graphics.Outline;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.SettingsActivities.ClockActivity;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

/**
 * Created by joshua on 12.04.18.
 */

public class LayoutGridAdapter extends ArrayAdapter<Layout> {

    public static String[] getLayoutNames(){
        return new String[]{"Minimal Clock", "Small Groups", "Sandwich","Clock Islands"};
    }

    public static Layout getLayout(String name){

        ArrayList<Point> minimal_clock_points = new ArrayList<>();
        minimal_clock_points.add(new Point(3,8));
        minimal_clock_points.add(new Point(4,8));
        minimal_clock_points.add(new Point (4,9));
        minimal_clock_points.add(new Point (2,9));
        minimal_clock_points.add(new Point (2,7));
        minimal_clock_points.add(new Point (2,8));
        minimal_clock_points.add(new Point(3,10));
        minimal_clock_points.add(new Point (3,7));
        minimal_clock_points.add(new Point (3,6));
        minimal_clock_points.add(new Point (5,8));
        minimal_clock_points.add(new Point (4,7));
        minimal_clock_points.add(new Point (3,9));
        minimal_clock_points.add(new Point (4,6));
        minimal_clock_points.add(new Point(4,10));
        minimal_clock_points.add(new Point  (5,6));
        minimal_clock_points.add(new Point  (5,9));
        minimal_clock_points.add(new Point (6,8) );
        minimal_clock_points.add(new Point  (5,7));
        minimal_clock_points.add(new Point (5,10));

        ArrayList<Point> sandwich_layout = new ArrayList<>();
        sandwich_layout.add(new Point (3,8));
        sandwich_layout.add(new Point (4,8));
        sandwich_layout.add(new Point (4,9));
        sandwich_layout.add(new Point (2,9));
        sandwich_layout.add(new Point (3,2));
        sandwich_layout.add(new Point (2,3));
        sandwich_layout.add(new Point (2,8));
        sandwich_layout.add(new Point (3,3));
        sandwich_layout.add(new Point(3,10));
        sandwich_layout.add(new Point (5,8));
        sandwich_layout.add(new Point (4,3));
        sandwich_layout.add(new Point (3,9));
        sandwich_layout.add(new Point(4,10));
        sandwich_layout.add(new Point (4,2));
        sandwich_layout.add(new Point (5,2));
        sandwich_layout.add(new Point (5,9));
        sandwich_layout.add(new Point(5,10));
        sandwich_layout.add(new Point (3,4));
        sandwich_layout.add(new Point (6,8));
        sandwich_layout.add(new Point (2,4));
        sandwich_layout.add(new Point (4,4));
        sandwich_layout.add(new Point (5,3));
        sandwich_layout.add(new Point (6,4));
        sandwich_layout.add(new Point (5,4));


        ArrayList<Point> clock_islands = new ArrayList<>();
        clock_islands.add(new Point (3,10));
        clock_islands.add(new Point  (4,8));
        clock_islands.add(new Point  (4,9));
        clock_islands.add(new Point  (1,6));
        clock_islands.add(new Point  (2,7));
        clock_islands.add(new Point  (2,8));
        clock_islands.add(new Point  (1,7));
        clock_islands.add(new Point  (1,8));
        clock_islands.add(new Point (1,5));
        clock_islands.add(new Point  (7,7));
        clock_islands.add(new Point  (2,6));
        clock_islands.add(new Point  (3,9));
        clock_islands.add(new Point (7,3));
        clock_islands.add(new Point (4,10));
        clock_islands.add(new Point  (6,3));
        clock_islands.add(new Point  (6,8));
        clock_islands.add(new Point (7,8));
        clock_islands.add(new Point  (7,4));
        clock_islands.add(new Point  (6,9));
        clock_islands.add(new Point  (8,4));
        clock_islands.add(new Point  (6,7));
        clock_islands.add(new Point  (7,2));

        ArrayList<Point> islands = new ArrayList<>();

        islands.add(new Point(3,10));
        islands.add(new Point (4,8));
        islands.add(new Point (4,9));
        islands.add(new Point (5,6));
        islands.add(new Point (3,5));
        islands.add(new Point (4,4));
        islands.add(new Point (4,5));
        islands.add(new Point (4,6));
        islands.add(new Point (1,5));
        islands.add(new Point (7,9));
        islands.add(new Point (2,6));
        islands.add(new Point (3,9));
        islands.add(new Point (7,3));
        islands.add(new Point(4,10));
        islands.add(new Point (6,3));
        islands.add(new Point (6,8));
        islands.add(new Point (7,8));
        islands.add(new Point (7,4));
        islands.add(new Point (5,4));
        islands.add(new Point (6,9));
        islands.add(new Point (6,7));

        ArrayList<Layout> l = new ArrayList<>();
        l.add(new Layout(
                "Minimal Clock",
                minimal_clock_points,
                R.drawable.ic_minimal_clock,
                true,
                new Point(2,1),
                ClockActivity.CLOCKLARGE
        ));

        l.add(new Layout(
                "Small Groups",
                islands,
                R.drawable.ic_islands,
                true,
                new Point(0,5),
                ClockActivity.CLOCKLARGE
        ));

        l.add(new Layout(
                "Sandwich",
                sandwich_layout,
                R.drawable.ic_sandwich_layout,
                false,
                new Point(2,5),
                ClockActivity.CLOCKLARGE
        ));

        l.add(new Layout(
                "Clock Islands",
                clock_islands,
                R.drawable.ic_clock_islands,
                true,
                new Point(2,3),
                ClockActivity.CLOCKLARGE
        ));

        for(Layout layout : l){
            if(layout.getName().equals(name)){
                return layout;
            }
        }

        return l.get(0);
    }

    Context ctx;

    public LayoutGridAdapter(Context context, ArrayList<Layout> layouts) {
        super(context, 0, layouts);
        ctx = context;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Layout layout = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.layout_item, parent, false);
        }
        // Lookup view for data population

        TextView appName =  convertView.findViewById(R.id.app_name);

        ImageView appicon = convertView.findViewById(R.id.app_icon);
        // Populate the data into the template view using the data object
        appName.setText(layout.getName());

        appicon.setImageResource(layout.getTitleImage());

        Layout applayout = getLayout(PreferenceManager.getDefaultSharedPreferences(ctx).getString(WelcomeActivty.KEY_APP_LAYOUT_SELECTION_KEY,""));

        if(applayout != null){
            Log.i("layoutname","layout.getname: "+layout.getName());
            Log.i("appLayout", "applayout:"+ applayout.getName());
            if(applayout.getName() == layout.getName()){

                layout.selected = true;
            }
        }


        if(layout.selected){
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