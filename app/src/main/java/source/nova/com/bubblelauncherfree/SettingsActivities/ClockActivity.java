package source.nova.com.bubblelauncherfree.SettingsActivities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Transformation;
import android.widget.AdapterView;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;

import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.Clock.Clock;
import source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Settings.FontPickerAdapter;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;
import source.nova.com.bubblelauncherfree.Util.FontItem;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

public class ClockActivity extends AppCompatActivity {

    RelativeLayout clockWrapper;
    Clock clock;

    public static final String SHOW_CLOCK_KEY = "show_clock";
    SwitchCompat showClock;

    public static final String CLOCKMINI = "mini 1x1";
    public static final String CLOCKSMALL = "small 1x2";
    public static final String CLOCKLARGE = "large 4x3";
    public static final String CLOCKSIZEKEY = "clock_sizes";
    RelativeLayout clockLayout;

    public static final String CLOCK_COLOR_KEY = "clock_color";
    AlphaTileView clockColorView;
    RelativeLayout clockColor;

    public static final String CLOCKSHADOWCOLORKEY= "clock_shadow_color";
    AlphaTileView shadowColorView;
    RelativeLayout clockShadow;

    public static final String CLOCK_SHADOW_RADIUS_KEY = "clock_shadow_radius";
    SeekBar shadowRadius;

    public static final String CLOCK_FONT_KEY = "clock_font";
    public static final String SET_CLOCK_FONT_KEY = "set_clock_font";
    RelativeLayout clockFont;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clock);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();

        initClockWrapper();
        initClock();


        showClock = findViewById(R.id.show_clock);
        boolean show = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(SHOW_CLOCK_KEY,true);
        showClock.setChecked(show);
        showClock.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(SHOW_CLOCK_KEY,b);
                editor.commit();
                setClock();
            }
        });

        clockLayout = findViewById(R.id.clock_layout);
        clockLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clockSizeChooser();
            }
        });

        clockColor = findViewById(R.id.back_color);
        clockColorView = findViewById(R.id.alphaTileView);
        clockColorView.setPaintColor(sp.getInt(CLOCK_COLOR_KEY,0xffffff));
        clockColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(ClockActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Background Color")
                        .setPositiveButton("OK",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        clockColorView.setPaintColor(envelope.getColor());
                                        editor.putInt(CLOCK_COLOR_KEY,envelope.getColor());
                                        editor.commit();
                                        setClock();
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });

        clockShadow = findViewById(R.id.clock_shadow_color);
        shadowColorView = findViewById(R.id.alphaTileView);
        shadowColorView.setPaintColor(sp.getInt(CLOCKSHADOWCOLORKEY,0xffffff));
        clockShadow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(ClockActivity.this, android.app.AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Background Color")
                        .setPositiveButton("OK",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        shadowColorView.setPaintColor(envelope.getColor());
                                        editor.putInt(CLOCKSHADOWCOLORKEY,envelope.getColor());
                                        editor.commit();
                                        setClock();
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });

        shadowRadius = findViewById(R.id.shadow_radius);

        shadowRadius.setProgress((int)(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getFloat(CLOCK_SHADOW_RADIUS_KEY,100)));
        shadowRadius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

                editor.putFloat(CLOCK_SHADOW_RADIUS_KEY, progress);
                editor.commit();
                setClock();
            }
        });
    }

    private void clockSizeChooser(){
        final String[] sizes = {CLOCKMINI, CLOCKSMALL, CLOCKLARGE};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pick a size");
        builder.setItems(sizes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(sizes[which]){
                    case CLOCKMINI:
                        editor.putString(CLOCKSIZEKEY,CLOCKMINI);
                        editor.commit();
                        setClock();
                        break;
                    case CLOCKSMALL:
                        editor.putString(CLOCKSIZEKEY,CLOCKSMALL);
                        editor.commit();
                        setClock();
                        break;
                    case CLOCKLARGE:
                        editor.putString(CLOCKSIZEKEY,CLOCKLARGE);
                        editor.commit();
                        setClock();
                        break;
                    default:
                        editor.putString(CLOCKSIZEKEY,CLOCKLARGE);
                        editor.commit();
                        setClock();
                        break;
                }
                // the user clicked on colors[which]
            }
        });
        builder.show();

        clockFont = findViewById(R.id.clock_font);
        clockFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });
    }

    private void initClock(){
        clock = new Clock(
                getApplicationContext(),
                Util.getDiam(getApplicationContext()),
                Util.getPadding(getApplicationContext()));
        clockWrapper.addView(clock);
    }

    ArrayList<FontItem> fonts;

    private void showDialog(){

        final Dialog dialog = new Dialog(this);

        fonts = new ArrayList<>();

        fonts.add(new FontItem("black street", R.font.black_street));
        fonts.add(new FontItem("editundo",R.font.editundo));
        fonts.add(new FontItem("edundot",R.font.edundot));
        fonts.add(new FontItem("edunline",R.font.edunline));
        fonts.add(new FontItem("kimberley",R.font.kimberley));

        View view = getLayoutInflater().inflate(R.layout.font_picker, null);

        ListView lv =  view.findViewById(R.id.custom_list);

        // Change MyActivity.this and myListOfItems to your own values
        FontPickerAdapter clad = new FontPickerAdapter(ClockActivity.this, fonts);

        lv.setAdapter(clad);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                editor.putInt(CLOCK_FONT_KEY,fonts.get(i).path);
                editor.commit();
                setClock();
                dialog.dismiss();
            }
        });

        dialog.setContentView(view);

        dialog.show();

    }

    private void setClock(){
        clockWrapper.removeAllViews();
        clock = new Clock(
                getApplicationContext(),
                Util.getDiam(getApplicationContext()),
                Util.getPadding(getApplicationContext()));
        clockWrapper.addView(clock);
        clockWrapper.invalidate();
    }

    private void initClockWrapper(){
        clockWrapper = findViewById(R.id.clock_wraper);

        /*if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wallpaper_key",false)){
            clockWrapper.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("background_color",0x000000));
        }else{
            clockWrapper.setBackground(null);
        }*/

        int h = Util.rasterToPixel(new Point(3,3),Util.getDiam(getApplicationContext()), Util.getPadding(getApplicationContext()), getApplicationContext()).y-Util.getPadding(getApplicationContext())*2;

        LinearLayout.LayoutParams clockWrapper_layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h);
        clockWrapper.setLayoutParams(clockWrapper_layout);
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
