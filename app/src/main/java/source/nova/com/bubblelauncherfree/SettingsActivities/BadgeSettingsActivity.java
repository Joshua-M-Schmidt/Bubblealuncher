package source.nova.com.bubblelauncherfree.SettingsActivities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.CustomViews.AppView;
import source.nova.com.bubblelauncherfree.CustomViews.MaterialBadgeTextView;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;

import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getRandomApps;
import static source.nova.com.bubblelauncherfree.MainActivity.asize;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty.PERMISSIONS_NOTIFICATION_ACCESS;

public class BadgeSettingsActivity extends AppCompatActivity {

    AppView appView;
    private RelativeLayout appWrapper;

    private RelativeLayout editNotificationSettings;

    public static final String BADGE_BACKGROUND_COLOR = "badge_background_color";
    private RelativeLayout chooseBadgeColor;
    private AlphaTileView viewBadgeColor;

    public static final String BADGE_MINIMAL_MODE = "badge_minimal_mode_key";
    private SwitchCompat chooseBadgeMode;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_badge_settings);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();

        initAppView();
        initChooseColor();
        initMinimalModeSwitch();
        initEditPermission();
    }

    private void initAppView(){
        ArrayList<AppView> app = getRandomApps(1,getApplicationContext(),asize);
        appView = app.get(0);
        appView.badge = new MaterialBadgeTextView(getApplicationContext());
        appView.addView(appView.badge);
        appWrapper = findViewById(R.id.appView_wrapper);
        appWrapper.addView(appView);
        appView.badge.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(this).getInt(BADGE_BACKGROUND_COLOR,0xffffffff));
        if(PreferenceManager.getDefaultSharedPreferences(this).getBoolean(BADGE_MINIMAL_MODE,true)){
            appView.badge.setHighLightMode();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize/4, asize/4);
            params.topMargin = asize/20;
            params.leftMargin = asize/20;
            appView.badge.setLayoutParams(params);
        }else{
            //badge.clearHighLightMode();
            appView.badge.setBadgeCount(10);
        }

    }

    private void initMinimalModeSwitch(){
        chooseBadgeMode = findViewById(R.id.badge_minimal_mode);
        boolean show = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(BADGE_MINIMAL_MODE,true);
        chooseBadgeMode.setChecked(show);
        chooseBadgeMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(BADGE_MINIMAL_MODE,b);
                editor.commit();
                updateBadge();
            }
        });
    }

    private void initChooseColor(){
        viewBadgeColor = findViewById(R.id.alphaTileView);
        viewBadgeColor.setPaintColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BADGE_BACKGROUND_COLOR,0xffffffff));
        chooseBadgeColor = findViewById(R.id.back_color);
        chooseBadgeColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(BadgeSettingsActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Badge background color")
                        .setPositiveButton("OK",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        viewBadgeColor.setPaintColor(envelope.getColor());
                                        editor.putInt(BADGE_BACKGROUND_COLOR,envelope.getColor());
                                        editor.commit();
                                        updateBadge();
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .setPreferenceName(BADGE_BACKGROUND_COLOR)
                        .attachBrightnessSlideBar(false)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });
    }

    private void initEditPermission(){
        editNotificationSettings = findViewById(R.id.editBadgeSettings);
        editNotificationSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callGPSSettingIntent = new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS);
                startActivityForResult(callGPSSettingIntent,PERMISSIONS_NOTIFICATION_ACCESS);
            }
        });
    }

    private void updateBadge(){
        appView.badge.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BADGE_BACKGROUND_COLOR,0xffffffff));
        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(BADGE_MINIMAL_MODE,true)){
            appView.badge.setHighLightMode();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize/4, asize/4);
            params.topMargin = asize/20;
            params.leftMargin = asize/20;
            appView.badge.setLayoutParams(params);
        }else{
            //badge.clearHighLightMode();
            appView.badge.setBadgeCount(10);
        }
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