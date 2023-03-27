package source.nova.com.bubblelauncherfree.Settings;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.SettingsActivities.BackgroundActivity;
import source.nova.com.bubblelauncherfree.SettingsActivities.BadgeSettingsActivity;
import source.nova.com.bubblelauncherfree.SettingsActivities.ClockActivity;
import source.nova.com.bubblelauncherfree.SettingsActivities.FolderSettingsActivity;
import source.nova.com.bubblelauncherfree.SettingsActivities.IconSettingsActivity;
import source.nova.com.bubblelauncherfree.SettingsActivities.LayoutActivity;
import source.nova.com.bubblelauncherfree.SettingsActivities.SearchbarActivity;

public class SettingsActivity2 extends AppCompatActivity {

    RelativeLayout iconSettings;
    RelativeLayout layoutSettings;
    RelativeLayout backgroundSettings;
    RelativeLayout searchbarSettings;
    RelativeLayout clockSettings;
    RelativeLayout folderSettings;
    RelativeLayout badgeSettings;

    public static final String PREF_WALLPAPER_TOGGLE = "wallpaper_key";
    public static final String PREF_STATUSBAR_TOGGLE = "statusbar_key";
    public static final String PREF_APPLABELS_TOGGLE = "applabels_key";

    private static final String PREMIUM_KEY ="premium_key";

    public static boolean checkPremium(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(PREMIUM_KEY,true);
        //return true;
    }

    public static void setPremium(Context ctx){
        SharedPreferences.Editor edit = PreferenceManager.getDefaultSharedPreferences(ctx).edit();
        edit.putBoolean(PREMIUM_KEY,true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        iconSettings = findViewById(R.id.icon_settings);
        iconSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), IconSettingsActivity.class);
                startActivity(settingIntent);
            }
        });

        layoutSettings = findViewById(R.id.layout_settings);
        layoutSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), LayoutActivity.class);
                startActivity(settingIntent);
            }
        });

        backgroundSettings = findViewById(R.id.background_settings);
        backgroundSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), BackgroundActivity.class);
                startActivity(settingIntent);
            }
        });

        searchbarSettings = findViewById(R.id.searchbar_settings);
        searchbarSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), SearchbarActivity.class);
                startActivity(settingIntent);
            }
        });

        clockSettings = findViewById(R.id.clock_settings);
        clockSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), ClockActivity.class);
                startActivity(settingIntent);
            }
        });

        folderSettings = findViewById(R.id.Folder_settings);
        folderSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), FolderSettingsActivity.class);
                startActivity(settingIntent);
            }
        });

        badgeSettings = findViewById(R.id.badge_settings);
        badgeSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), BadgeSettingsActivity.class);
                startActivity(settingIntent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent settingIntent = new Intent(this, MainActivity.class);
                startActivity(settingIntent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
