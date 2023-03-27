package source.nova.com.bubblelauncherfree.SettingsActivities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.CustomViews.AppView;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getFolderPoints;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.CACHE_ID;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getRandomApps;
import static source.nova.com.bubblelauncherfree.Util.Util.rasterToPixel;

public class LayoutActivity extends AppCompatActivity {

    public static final String BUBBLE_PADDING_KEY = "bubble_padding_key";
    SeekBar bubble_padding;

    public static final String BUBBLE_SIZE_KEY = "bubble_size";
    SeekBar bubble_size;

    public static final String BUBBLE_RASTER_STYLE = "raster_style";
    RelativeLayout raster_style;
    public static final String RASTER_STYLE_HONEYCOMB = "honeycomb";
    public static final String RASTER_STYLE_CHESSBOARD = "chessboard";

    public static final String HIDE_STATUS_BAR_KEY = "hide_status_bar_key";
    SwitchCompat hideStatusBar;

    RelativeLayout icon_wrapper;
    ArrayList<AppView> apps;
    ArrayList<Point> appPoints;
    int appNumber = 9;
    int asize;
    int padding;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);

        asize = Util.getDiam(getApplicationContext());
        padding = Util.getPadding(getApplicationContext());

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();

        initAppView();
        initBubbleSize();
        initBubblePadding();
        initRasterStyle();
        initHideStatusBar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private void initHideStatusBar(){
        hideStatusBar = findViewById(R.id.hide_status_bar);
        hideStatusBar.setChecked(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(HIDE_STATUS_BAR_KEY,false));
        hideStatusBar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(HIDE_STATUS_BAR_KEY,b);
                editor.commit();

            }
        });
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

    AlertDialog.Builder builder;

    public void initRasterStyle(){

        final String[] shapes = {RASTER_STYLE_HONEYCOMB,
                RASTER_STYLE_CHESSBOARD};

        builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a layout style");
        builder.setItems(shapes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(shapes[which]){
                    case RASTER_STYLE_HONEYCOMB:
                        editor.putString(BUBBLE_RASTER_STYLE,RASTER_STYLE_HONEYCOMB);
                        editor.commit();
                        resetApps();
                        break;
                    case RASTER_STYLE_CHESSBOARD:
                        editor.putString(BUBBLE_RASTER_STYLE,RASTER_STYLE_CHESSBOARD);
                        editor.commit();
                        resetApps();
                        break;
                    default:
                        editor.putString(BUBBLE_RASTER_STYLE,RASTER_STYLE_HONEYCOMB);
                        editor.commit();
                        resetApps();
                        break;
                }
                // the user clicked on colors[which]
            }
        });

        raster_style = findViewById(R.id.raster_style);
        raster_style.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.show();
            }
        });
    }

    AlertDialog.Builder builderShape;

    private void initBubblePadding(){
        bubble_padding = findViewById(R.id.bubble_padding);

        bubble_padding.setProgress( PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BUBBLE_PADDING_KEY,7));
        bubble_padding.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            // When Progress value changed.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
            }

            // Notification that the user has started a touch gesture.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // Notification that the user has finished a touch gesture
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                editor.putInt(BUBBLE_PADDING_KEY, progress);
                editor.commit();
                resetApps();
            }
        });
    }

    private void initAppView(){
        icon_wrapper = findViewById(R.id.icon_wrapper);
        appPoints = getFolderPoints(appNumber);
        apps = getRandomApps(appNumber,getApplicationContext(),asize);

        for (int n = 0; n < appNumber; n++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);

            Point pxPoint = rasterToPixel(appPoints.get(n), asize, padding,getApplicationContext());

            params.leftMargin = pxPoint.x;
            params.topMargin = pxPoint.y;

            apps.get(n).setLayoutParams(params);
            apps.get(n).setVisibility(View.VISIBLE);
            icon_wrapper.addView(apps.get(n));
        }
    }

    private void resetApps(){

        DiskLruImageCache cache = new DiskLruImageCache(getApplicationContext(),
                CACHE_ID,
                1024 * 1024 * 10 /* 10 mb */,
                Bitmap.CompressFormat.PNG,
                100);

        cache.clearCache();

        asize = Util.getDiam(getApplicationContext());
        padding = Util.getPadding(getApplicationContext());
        icon_wrapper.removeAllViews();

        appPoints = getFolderPoints(appNumber);
        apps = getRandomApps(appNumber,getApplicationContext(),asize);

        for (int n = 0; n < appNumber; n++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);

            Point pxPoint = rasterToPixel(appPoints.get(n), asize, padding,getApplicationContext());

            params.leftMargin = pxPoint.x;
            params.topMargin = pxPoint.y;

            apps.get(n).setLayoutParams(params);
            apps.get(n).setVisibility(View.VISIBLE);
            icon_wrapper.addView(apps.get(n));
        }
    }

    private void initBubbleSize(){
        bubble_size = findViewById(R.id.bubble_size);

        bubble_size.setProgress( PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BUBBLE_SIZE_KEY,60));
        bubble_size.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            // When Progress value changed.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
            }

            // Notification that the user has started a touch gesture.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // Notification that the user has finished a touch gesture
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                editor.putInt(BUBBLE_SIZE_KEY, progress);
                editor.commit();
                resetApps();
            }
        });
    }
}
