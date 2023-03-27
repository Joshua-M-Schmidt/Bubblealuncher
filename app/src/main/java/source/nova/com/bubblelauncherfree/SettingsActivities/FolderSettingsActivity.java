package source.nova.com.bubblelauncherfree.SettingsActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.Clock.Clock;
import source.nova.com.bubblelauncherfree.CustomViews.AppView;
import source.nova.com.bubblelauncherfree.CustomViews.FolderView;
import source.nova.com.bubblelauncherfree.FolderManager.FolderContract;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.CACHE_ID;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.convertStringToArray;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getHexFolderIcon;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getRandomApps;
import static source.nova.com.bubblelauncherfree.MainActivity.asize;

public class FolderSettingsActivity extends AppCompatActivity {

    public static final String SHOW_FOLDER_LABEL_KEY = "display_folder_name";
    SwitchCompat showLabel;

    RelativeLayout folderWrapper;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_folder_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();

        showLabel = findViewById(R.id.show_label);
        showLabel.setChecked(sp.getBoolean(SHOW_FOLDER_LABEL_KEY,true));
        showLabel.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(SHOW_FOLDER_LABEL_KEY,b);
                editor.commit();
                setFolder();
            }
        });

        initFolderWrapper();
        setFolder();
    }

    private ArrayList<String> appPackages(ArrayList<AppView> apps){
        ArrayList<String> retarr = new ArrayList<>();

        for(AppView app : apps) {
            retarr.add(app.getAppPackage());
        }

        return retarr;
    }

    private void setFolder(){
        folderWrapper.removeAllViews();
        ArrayList<String> apps = appPackages(getRandomApps(7,getApplicationContext(),asize));
        DiskLruImageCache cache = new DiskLruImageCache(getApplicationContext(),
                CACHE_ID,
                1024 * 1024 * 10 /* 10 mb */,
                Bitmap.CompressFormat.PNG,
                100);

        cache.clearCache();

        FolderView folder = new FolderView(
                getApplicationContext(),
               "folder",
                new Point(
                        0,
                        0
                ),
                apps
        );

        int w = 0;

        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("app_icon_style","circle").equals("hexagon") ||
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString("app_icon_style","circle").equals("hexagon-outline")){
            w = (int)(2*Util.getHexOffsetX(asize/2));

        }

        folder.setImageBitmap(getHexFolderIcon(apps,
                asize,
                asize-w,
                getApplicationContext(),"folder",false));
        folderWrapper.addView(folder);
        folderWrapper.invalidate();
    }

    private void initFolderWrapper(){
        folderWrapper = findViewById(R.id.clock_wraper);

        /*if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wallpaper_key",false)){
            clockWrapper.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("background_color",0x000000));
        }else{
            clockWrapper.setBackground(null);
        }*/

        int h = Util.rasterToPixel(new Point(3,3),Util.getDiam(getApplicationContext()), Util.getPadding(getApplicationContext()), getApplicationContext()).y-Util.getPadding(getApplicationContext())*2;

        LinearLayout.LayoutParams clockWrapper_layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h);
        folderWrapper.setLayoutParams(clockWrapper_layout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent settingIntent = new Intent(this, SettingsActivity2.class);
                startActivity(settingIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}