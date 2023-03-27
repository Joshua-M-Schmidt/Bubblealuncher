package source.nova.com.bubblelauncherfree.Clock;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cameron.materialcolorpicker.ColorPicker;
import com.cameron.materialcolorpicker.ColorPickerCallback;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.SettingsActivities.BuyingActivity;
import source.nova.com.bubblelauncherfree.Settings.FontPickerAdapter;
import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity;
import source.nova.com.bubblelauncherfree.Util.FontItem;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.SettingsActivities.ClockActivity.CLOCKLARGE;
import static source.nova.com.bubblelauncherfree.SettingsActivities.ClockActivity.CLOCKMINI;
import static source.nova.com.bubblelauncherfree.SettingsActivities.ClockActivity.CLOCKSIZEKEY;
import static source.nova.com.bubblelauncherfree.SettingsActivities.ClockActivity.CLOCKSMALL;

public class ClockSettingActivity extends AppCompatActivity {

    LinearLayout main_layout;
    RelativeLayout clockWrapper;
    Clock clock;
    LinearLayout premiumView;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private Button chooseClockLayout;

    // clock shadow

    public static final String CLOCKSHADOWKEY= "clock_shadow";
    public static final String CLOCKSHADOWCOLORKEY= "clock_shadow_color";
    public static final String CLOCK_SHADOW_RADIUS_KEY = "clock_shadow_radius";
    private CheckBox clockShadow;
    private Button clockShadowColor;
    private SeekBar clockShadowRadius;
    private TextView clockShadowRadiusText;

    // clock color

    public static final String CLOCK_COLOR_KEY = "clock_color";
    private Button clockColor;

    // clock font

    public static final String CLOCK_FONT_KEY = "clock_font";
    public static final String SET_CLOCK_FONT_KEY = "set_clock_font";
    private CheckBox setClockFont;
    private Button clockFont;

    // display clock

    public static final String SHOW_CLOCK_KEY = "show_clock";
    private CheckBox showClock;

    @Override
    public void onCreate(Bundle sis){
        super.onCreate(sis);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if(!MainActivity.IS_PREMIUM){
            chooseClockLayout.setEnabled(true);
            clockColor.setEnabled(true);
            clockShadow.setEnabled(false);
            clockShadowColor.setEnabled(false);
            clockShadowRadius.setEnabled(false);
            clockShadowRadiusText.setEnabled(false);
            setClockFont.setEnabled(false);
            clockFont.setEnabled(false);
            premiumView.setVisibility(View.VISIBLE);
            premiumView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(ClockSettingActivity.this,BuyingActivity.class));
                }
            });
        }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent settingIntent = new Intent(this, SettingsActivity.class);
                settingIntent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
                settingIntent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
                startActivity(settingIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
