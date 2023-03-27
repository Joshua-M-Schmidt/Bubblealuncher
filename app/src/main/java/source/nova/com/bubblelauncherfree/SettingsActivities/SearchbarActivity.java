package source.nova.com.bubblelauncherfree.SettingsActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Outline;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.SearchBar.SearchBar;
import source.nova.com.bubblelauncherfree.SearchBar.SearchBarSettings;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

public class SearchbarActivity extends AppCompatActivity {

    private SearchBar searchBar;
    private LinearLayout searchBarWrapper;

    public static final String SEARCHBAR_BORDER_RADIUS_KEY = "searchbar_border_radius_key";
    private SeekBar search_bar_border_radius;

    public static final String SEARCHBAR_TEXT_COLOR_KEY = "searchbar_text_color_key";
    private RelativeLayout search_bar_text_color;
    private AlphaTileView search_bar_text_color_view;

    public static final String SEARCHBAR_BACKGROUND_COLOR = "search_bar_backgroudn_color";
    private RelativeLayout search_bar_back_color;
    private AlphaTileView search_bar_back_color_view;

    public static final String DRAGPIN_COLOR = "drag_pin_color";
    public static final String DRAGPIN_WIDTH = "drag_pin_width";
    private SeekBar drag_pin_width;
    public static final String DRAGPIN_HEIGHT = "drag_pin_height";
    private SeekBar drag_pin_height;
    public static final String DRAGPIN_RADIUS = "drag_pin_radius";
    private SeekBar drag_pin_radius;

    private RelativeLayout dragPinColor;
    private AlphaTileView dragPinColorView;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private View dragPin;
    int dragPinHeight = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searchbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();

        initSearchBarWrapper();
        initSearchBar();

        drag_pin_radius = findViewById(R.id.drag_pin_border_radius);
        drag_pin_radius.setProgress((int) PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_RADIUS,20));
        drag_pin_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

                editor.putInt(DRAGPIN_RADIUS, progress);
                editor.commit();
                initSearchBar();
            }
        });

        drag_pin_height = findViewById(R.id.drag_pin_height);
        drag_pin_height.setProgress((int) PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_HEIGHT,20));
        drag_pin_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

                editor.putInt(DRAGPIN_HEIGHT, progress);
                editor.commit();
                initSearchBar();
            }
        });

        drag_pin_width = findViewById(R.id.drag_pin_width);
        drag_pin_width.setProgress((int) PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_WIDTH,60));
        drag_pin_width.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

                editor.putInt(DRAGPIN_WIDTH, progress);
                editor.commit();
                initSearchBar();
            }
        });


        dragPinColorView = findViewById(R.id.alphaTileView_dragpin);
        dragPinColorView.setPaintColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_COLOR,0xffffffff));
        dragPinColor = findViewById(R.id.drag_pin_color);
        dragPinColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(SearchbarActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Text Color")
                        .setPositiveButton("OK",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        dragPinColorView.setPaintColor(envelope.getColor());
                                        editor.putInt(DRAGPIN_COLOR,envelope.getColor());
                                        editor.commit();
                                        initSearchBar();
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .setPreferenceName(DRAGPIN_COLOR)
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });

        search_bar_border_radius = findViewById(R.id.search_bar_border_radius);
        search_bar_border_radius.setProgress((int) PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getFloat(SEARCHBAR_BORDER_RADIUS_KEY,60));
        search_bar_border_radius.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

                editor.putFloat(SEARCHBAR_BORDER_RADIUS_KEY, progress);
                editor.commit();
                initSearchBar();
            }
        });

        search_bar_text_color_view = findViewById(R.id.alphaTileView_text);
        search_bar_text_color_view.setPaintColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(SEARCHBAR_TEXT_COLOR_KEY,0xffffffff));
        search_bar_text_color = findViewById(R.id.text_color);
        search_bar_text_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(SearchbarActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Text Color")
                        .setPositiveButton("OK",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        search_bar_text_color_view.setPaintColor(envelope.getColor());
                                        editor.putInt(SEARCHBAR_TEXT_COLOR_KEY,envelope.getColor());
                                        editor.commit();
                                        initSearchBar();
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .setPreferenceName(SEARCHBAR_TEXT_COLOR_KEY)
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });

        search_bar_back_color_view = findViewById(R.id.alphaTileView);
        search_bar_back_color_view.setPaintColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(SEARCHBAR_BACKGROUND_COLOR,0xffffffff));
        search_bar_back_color = findViewById(R.id.back_color);
        search_bar_back_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(SearchbarActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Text Color")
                        .setPositiveButton("OK",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        search_bar_back_color_view.setPaintColor(envelope.getColor());
                                        editor.putInt(SEARCHBAR_BACKGROUND_COLOR,envelope.getColor());
                                        editor.commit();
                                        initSearchBar();
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .setPreferenceName(SEARCHBAR_BACKGROUND_COLOR)
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });
    }

    private void initSearchBar(){
        searchBarWrapper.removeAllViews();
        searchBar = new SearchBar(getApplicationContext());
        searchBar.setCollapsed();
        searchBar.setClipToPadding(false);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,(int)Util.dipToPixels(getApplicationContext(),50));
        searchBar.setLayoutParams(params);
        searchBar.setVisibility(View.VISIBLE);
        searchBar.setAlpha(1f);

        Log.i("serachbar", searchBar.isShown() +" is shown?");

        dragPin = new View(getApplicationContext());
        searchBarWrapper.addView(dragPin);

        LinearLayout.LayoutParams paramsPin = new LinearLayout.LayoutParams(
                (int)Util.dipToPixels(getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_WIDTH,80)),
                (int)Util.dipToPixels(getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_HEIGHT,20)));
        paramsPin.setMargins((int)Util.dipToPixels(getApplicationContext(),10),
                (int)Util.dipToPixels(getApplicationContext(),10),
                (int)Util.dipToPixels(getApplicationContext(),10),
                (int)Util.dipToPixels(getApplicationContext(),20));

        dragPin.setLayoutParams(paramsPin);
        dragPin.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0, 0, dragPin.getWidth(), dragPin.getHeight(),
                        (int)Util.dipToPixels(getApplicationContext(),(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_RADIUS,20))));
            }
        });
        dragPin.setClipToOutline(true);
        dragPin.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(SearchbarActivity.DRAGPIN_COLOR,0x00000066));
        searchBarWrapper.addView(searchBar);
    }

    private void initSearchBarWrapper(){
        searchBarWrapper = findViewById(R.id.search_bar_wrapper);
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
