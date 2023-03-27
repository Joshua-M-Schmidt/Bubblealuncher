package source.nova.com.bubblelauncherfree.StartConfigActivities;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.PagerAdapter;
import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.Async.FetchApps;
import source.nova.com.bubblelauncherfree.CustomViews.MaterialBadgeTextView;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.StartConfigActivities.LayoutGridAdapter.getLayout;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.LayoutGridAdapter.getLayoutNames;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty.KEY_APPS_LOADED;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty.KEY_APP_LAYOUT_SELECTION_KEY;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty.PERMISSIONS_FILE_ACCESS;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty.PERMISSIONS_NOTIFICATION_ACCESS;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty.PERMISSIONS_REQUEST_READ_CONTACTS;

public class WelcomePageAdapter extends PagerAdapter {

    Context context;
    LayoutInflater inflater;

    public int progress = 2;

    public OnProgressListener listener;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public interface OnProgressListener{
        void OnProgress(int progress);
    }

    public WelcomePageAdapter(Context context, Activity activity){
        this.activity = activity;
        this.context = context;
        this.sp = PreferenceManager.getDefaultSharedPreferences(context);
        this.editor = sp.edit();

        initProgress();
    }

    private void initProgress(){
        if(sp.getBoolean(KEY_APPS_LOADED,false)){
            progress++;
            notifyDataSetChanged();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED) {
            progress+=2;
            notifyDataSetChanged();
        }

        if (NotificationManagerCompat.getEnabledListenerPackages(context).contains(context.getPackageName())) {        //ask for permission
            progress++;
            notifyDataSetChanged();
        }

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            progress++;
            notifyDataSetChanged();
        }

        if(sp.getString(KEY_APP_LAYOUT_SELECTION_KEY,"") != ""){
            progress++;
            notifyDataSetChanged();
        }

    }

    @Override
    public int getCount() {
        return progress;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    public void addOnProgressListener(OnProgressListener listener){
        this.listener = listener;
    }

    private MaterialBadgeTextView badge;
    private Handler mHandler;
    private int i;
    private TextView appLoading;
    private ProgressBar appLoadingBar;
    private Activity activity;

    private String selected;
    private GridView layoutGrid;
    private LayoutGridAdapter layoutAdapter;

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.i("instantiateItem","position "+position);
        View view = null;

        switch (position) {
            case 0:
                inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.slide_welcome, container, false);

                container.addView(view);

                break;
            case 1:
                inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.slide_load_data, container, false);
                appLoading = view.findViewById(R.id.app_loading_text);
                appLoading.setMovementMethod(new ScrollingMovementMethod());
                appLoadingBar = view.findViewById(R.id.app_progress);
                container.addView(view);

                if(sp.getBoolean(KEY_APPS_LOADED,false)){
                    appLoading.append("all apps fetched"+"\n");
                    appLoadingBar.setIndeterminate(false);
                    appLoadingBar.setMax(100);
                    appLoadingBar.setProgress(100);
                }else{
                    FetchApps getAppsFromDBTask = new FetchApps(context, new FetchApps.OnAppsFetched() {
                        @Override
                        public void onAppsFetched() {
                            appLoading.append("all apps fetched"+"\n");
                            Log.i("fetch apps", "all apps fetched");

                            appLoadingBar.setIndeterminate(false);
                            appLoadingBar.setMax(100);
                            appLoadingBar.setProgress(100);

                            editor.putBoolean(KEY_APPS_LOADED,true);
                            editor.commit();
                            progress();
                        }
                    }, new FetchApps.OnProgress() {
                        @Override
                        public void onProgress(String message, boolean color) {

                            appLoading.append(message+"\n");

                            while (appLoading.canScrollVertically(1)) {
                                appLoading.scrollBy(0, (int) Util.dipToPixels(context,15f));
                            }

                            Log.i("fetch apps", message);
                        }
                    });

                    getAppsFromDBTask.execute();
                }

                break;
            case 2:
                inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.slide_contact_permission, container, false);
                Button permit = view.findViewById(R.id.permit_contacts);

                permit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                            return;
                        }
                    }
                });

                container.addView(view);

                break;
            case 3:
                inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.slide_notifications, container, false);

                mHandler = new Handler();

                Runnable mUpdate = new Runnable() {
                    public void run() {

                        badge.setBadgeCount(i);

                        i++; // incrementing the value
                        if(i > 10)
                            i = 0;

                        mHandler.postDelayed(this, 500);
                    }
                };

                mHandler.post(mUpdate); //call this to run

                badge = view.findViewById(R.id.badge);
                badge.clearHighLightMode();
                badge.setBadgeCount(10);

                Button permitBadge = view.findViewById(R.id.permit_notifications);

                permitBadge.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent callGPSSettingIntent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                        activity.startActivityForResult(callGPSSettingIntent,PERMISSIONS_NOTIFICATION_ACCESS);
                    }
                });

                container.addView(view);

                break;
            case 4:
                inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.slide_file_access, container, false);
                container.addView(view);

                Button permitFiles = view.findViewById(R.id.permit_files);

                permitFiles.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ActivityCompat.requestPermissions(activity,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSIONS_FILE_ACCESS);
                    }
                });

                break;
            case 5:
                inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.slide_choose_layout, container, false);
                layoutGrid = view.findViewById(R.id.app_grid);
                ArrayList<Layout> layouts = new ArrayList<>();


                String[] layoutNames = getLayoutNames();

                for(String name : layoutNames){
                    layouts.add(getLayout(name));
                }

                layoutAdapter = new LayoutGridAdapter(context,layouts);
                layoutGrid.setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
                layoutGrid.setAdapter(layoutAdapter);

                layoutGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                        if(true) {
                            Layout layout = layoutAdapter.getItem(i);
                            Log.i("selected name","name: "+layout.getName());
                            if (layout.selected) {
                               // do nothing
                            } else {
                                layout.selected = true;
                                view.setBackground(ContextCompat.getDrawable(context, R.drawable.buy_button));

                                selected = layout.getName();
                                editor.putString(KEY_APP_LAYOUT_SELECTION_KEY,selected);
                                editor.apply();

                                for(int j=0; j<layoutGrid.getChildCount(); j++) {
                                    if(!selected.equals(layoutAdapter.getItem(j).getName())){
                                        View v = layoutGrid.getChildAt(j);
                                        Layout l = layoutAdapter.getItem(j);
                                        l.selected = false;
                                        v.setBackground(null);
                                    }
                                    // do stuff with child view
                                }

                                progress=7;
                                notifyDataSetChanged();
                                if(listener != null){
                                    listener.OnProgress(progress);
                                }


                            }

                        }
                    }
                });

                container.addView(view);

                break;
            case 6:
                inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.slide_fin, container, false);
                container.addView(view);
                break;
            default:
                break;
        }
        return view;
    }

    private void progress(){
        progress++;
        notifyDataSetChanged();
        if(listener != null){
            listener.OnProgress(progress);
        }
    }
}
