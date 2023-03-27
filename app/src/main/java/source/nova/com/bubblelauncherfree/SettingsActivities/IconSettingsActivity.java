package source.nova.com.bubblelauncherfree.SettingsActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Outline;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.CustomViews.AppView;
import source.nova.com.bubblelauncherfree.IconPackSettings.IconPackActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getFolderPoints;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getAppsInFolder;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getRandomApps;
import static source.nova.com.bubblelauncherfree.Util.Util.rasterToPixel;

public class IconSettingsActivity extends AppCompatActivity {

    RelativeLayout iconPackSettings;
    SwitchCompat cropIconsSwitch;
    public static final String BUBBLE_CROP = "crop_background_sircle";
    SeekBar saturation;
    public static final String BUBBLE_SATURATION = "bubble_saturation";
    SeekBar shadowSize;
    public static final String BUBBLE_SHADOW = "bubble_shadow";

    RelativeLayout icon_wrapper;
    ArrayList<AppView> apps;
    ArrayList<Point> appPoints;
    int appNumber = 9;
    int asize;
    int padding;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;


    /*public static final String BUBBLE_DIPLSAY_BACK_KEY = "disable_app_background";
    SwitchCompat display_symbol_background;

    public static final String BUBBLE_USE_CUSTOM_BACK = "custom_app_background";
    CheckBox use_custom_background;
    LinearLayout custom_background_layout;

    public static final String BUBBLE_BACK_COLOR_KEY = "app_background_color";
    LinearLayout app_background_color;
    ColorPicker colorPicker;

    public static final String BUBBLE_BACK_SHAPE_KEY = "app_icon_style";
    LinearLayout app_background_shape;
    public static final String BUBBLE_BACK_SHAPE_CIRCLE = "circle";
    public static final String BUBBLE_BACK_SHAPE_SQUARE = "square";
    public static final String BUBBLE_BACK_SHAPE_HEX = "hexagon";
    public static final String BUBBLE_BACK_SHAPE_CIRCLE_OUTLINE = "circle-outline";
    public static final String BUBBLE_BACK_SHAPE_SQUARE_OUTLINE = "square-outline";
    public static final String BUBBLE_BACK_SHAPE_HEX_OUTLINE = "hexagon-outline";


    public static final String BUBBLE_BACK_RATIO = "icon_background_ratio";
    LinearLayout background_ratio_layout;
    SeekBar background_ratio;
    TextView background_ratio_title;

    public static final String ICON_COLOR_SCHEME = "app_icon_color_theme";
    public static final String ICON_COLOR_SCHEME_PASTEL = "pastel";
    public static final String ICON_COLOR_SCHEME_GREYSCALE = "grayscale";
    public static final String ICON_COLOR_SCHEME_NEON = "neon (classic)";
    LinearLayout iconColorScheme;*/

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);
        setContentView(R.layout.activity_icon_settings);

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();

        iconPackSettings = findViewById(R.id.icon_pack_settings);
        iconPackSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settingIntent = new Intent(getApplicationContext(), IconPackActivity.class);
                startActivity(settingIntent);
            }
        });

        asize = Util.getDiam(getApplicationContext());
        padding = Util.getPadding(getApplicationContext());

        initAppView();
        initSaturation();
        initShadow();
        initCrop();
        /*initUseCustomBack();
        initBackgroundRatio();
        initColorScheme();
        initShapeChooser();
        initAppBackgroundColor();
        initDisplayBack();
        initRasterStyle();*/

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    boolean crop;

    private void initCrop(){
        cropIconsSwitch = findViewById(R.id.crop_bubble);
        crop = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(BUBBLE_CROP,true);
        cropIconsSwitch.setChecked(crop);
        cropIconsSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(BUBBLE_CROP,b);
                crop = b;
                editor.commit();

                DiskLruImageCache cache = new DiskLruImageCache(getApplicationContext(),
                        "app_icons_new",
                        1024 * 1024 * 10 /* 10 mb */,
                        Bitmap.CompressFormat.PNG,
                        100);

                cache.clearCache();
                resetApps();
            }
        });
    }

    public void initShadow(){
        shadowSize = findViewById(R.id.shadow_radius);

        shadowSize.setProgress((PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BUBBLE_SHADOW,100)));
        shadowSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

                editor.putInt(BUBBLE_SHADOW, progress);
                editor.commit();
                resetApps();
            }
        });
    }

    public void initSaturation(){
        saturation = findViewById(R.id.bubble_saturation);

        saturation.setProgress((int)(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getFloat(BUBBLE_SATURATION,1.0F))*100);
        saturation.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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

                DiskLruImageCache cache = new DiskLruImageCache(getApplicationContext(),
                        "app_icons_new",
                        1024 * 1024 * 10 /* 10 mb */,
                        Bitmap.CompressFormat.PNG,
                        100);

                cache.clearCache();

                editor.putFloat(BUBBLE_SATURATION, progress/100F);
                editor.commit();
                resetApps();
            }
        });
    }

    /*AlertDialog.Builder builderColorScheme;

    public void initColorScheme(){
        final String[] shapes = {ICON_COLOR_SCHEME_PASTEL,
                ICON_COLOR_SCHEME_GREYSCALE,
                ICON_COLOR_SCHEME_NEON};

        builderColorScheme = new AlertDialog.Builder(this);
        builderColorScheme.setTitle("Pick a color scheme");
        builderColorScheme.setItems(shapes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(shapes[which]){
                    case ICON_COLOR_SCHEME_PASTEL:
                        editor.putString(ICON_COLOR_SCHEME,ICON_COLOR_SCHEME_PASTEL);
                        editor.commit();
                        resetApps();
                        break;
                    case ICON_COLOR_SCHEME_GREYSCALE:
                        editor.putString(ICON_COLOR_SCHEME,ICON_COLOR_SCHEME_GREYSCALE);
                        editor.commit();
                        resetApps();
                        break;
                    case ICON_COLOR_SCHEME_NEON:
                        editor.putString(ICON_COLOR_SCHEME,ICON_COLOR_SCHEME_NEON);
                        editor.commit();
                        resetApps();
                        break;
                    default:
                        editor.putString(ICON_COLOR_SCHEME,ICON_COLOR_SCHEME_NEON);
                        editor.commit();
                        resetApps();
                        break;
                }
                // the user clicked on colors[which]
            }
        });

        iconColorScheme = findViewById(R.id.icon_color_scheme);
        iconColorScheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builderColorScheme.show();
            }
        });
    }

    public void initBackgroundRatio(){
        background_ratio_layout = findViewById(R.id.bubble_background_ration_layout);
        background_ratio_title = findViewById(R.id.bubble_back_ratio_title);
        background_ratio = findViewById(R.id.bubble_back_ratio);

        background_ratio.setProgress( PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BUBBLE_BACK_RATIO,7));
        background_ratio.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            int progress = 0;

            // When Progress value changed.
            @Override
            public void onProgressChanged(SeekBar seekBar, int progressValue, boolean fromUser) {
                progress = progressValue;
                background_ratio_title.setText("Bubble background ratio: " + progressValue + "/" + seekBar.getMax());

            }

            // Notification that the user has started a touch gesture.
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            // Notification that the user has finished a touch gesture
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                background_ratio_title.setText("Bubble background ratio: " + progress + "/" + seekBar.getMax());

                editor.putInt(BUBBLE_BACK_RATIO, progress);
                editor.commit();
                resetApps();
            }
        });
    }

    private void initShapeChooser(){

        final String[] shapes = {BUBBLE_BACK_SHAPE_CIRCLE,
                BUBBLE_BACK_SHAPE_CIRCLE_OUTLINE,
                BUBBLE_BACK_SHAPE_SQUARE_OUTLINE,
                BUBBLE_BACK_SHAPE_HEX_OUTLINE,
                BUBBLE_BACK_SHAPE_SQUARE,
                BUBBLE_BACK_SHAPE_HEX};

        builderShape = new AlertDialog.Builder(this);
        builderShape.setTitle("Pick a shape");
        builderShape.setItems(shapes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch(shapes[which]){
                    case BUBBLE_BACK_SHAPE_CIRCLE:
                        editor.putString(BUBBLE_BACK_SHAPE_KEY,BUBBLE_BACK_SHAPE_CIRCLE);
                        editor.commit();
                        resetApps();
                        break;
                    case BUBBLE_BACK_SHAPE_CIRCLE_OUTLINE:
                        editor.putString(BUBBLE_BACK_SHAPE_KEY,BUBBLE_BACK_SHAPE_CIRCLE_OUTLINE);
                        editor.commit();
                        resetApps();
                        break;
                    case BUBBLE_BACK_SHAPE_SQUARE_OUTLINE:
                        editor.putString(BUBBLE_BACK_SHAPE_KEY,BUBBLE_BACK_SHAPE_SQUARE_OUTLINE);
                        editor.commit();
                        resetApps();
                        break;
                    case BUBBLE_BACK_SHAPE_HEX_OUTLINE:
                        editor.putString(BUBBLE_BACK_SHAPE_KEY,BUBBLE_BACK_SHAPE_HEX_OUTLINE);
                        editor.commit();
                        resetApps();
                        break;
                    case BUBBLE_BACK_SHAPE_SQUARE:
                        editor.putString(BUBBLE_BACK_SHAPE_KEY,BUBBLE_BACK_SHAPE_SQUARE);
                        editor.commit();
                        resetApps();
                        break;
                    case BUBBLE_BACK_SHAPE_HEX:
                        editor.putString(BUBBLE_BACK_SHAPE_KEY,BUBBLE_BACK_SHAPE_HEX);
                        editor.commit();
                        resetApps();
                        break;
                    default:
                        editor.putString(BUBBLE_BACK_SHAPE_KEY,BUBBLE_BACK_SHAPE_CIRCLE);
                        editor.commit();
                        resetApps();
                        break;
                }
                // the user clicked on colors[which]
            }
        });

        app_background_shape = findViewById(R.id.custom_back_shape);
        app_background_shape.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builderShape.show();
            }
        });

    }

    private void initAppBackgroundColor(){
        app_background_color = findViewById(R.id.choose_symbol_back_color);
        app_background_color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int color = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BUBBLE_BACK_COLOR_KEY,0xffffffff);

                int red = Color.red(color);
                int green = Color.green(color);
                int blue = Color.blue(color);
                int alpha = Color.alpha(color);

                colorPicker = new ColorPicker(IconSettingsActivity.this,alpha, red, green, blue);

                colorPicker.setDialogButtonText("CONFIRM")    // The default text is "SUBMIT"
                        .setCloseOnBackPressed(false)         // The default value is true
                        .showButtonAsTransparent(true)        // The default value is false
                        .setCloseOnDialogButtonPressed(true); // The default value is true

                colorPicker.setCallback(new ColorPickerCallback() {
                    @Override
                    public void onColorChosen(@ColorInt int color, String hex, String hexNoAlpha) {

                        // Once the dialog's select button has been pressed, we
                        // can get the selected color and use it for the
                        // background of our view
                        editor.putInt(BUBBLE_BACK_COLOR_KEY,color);
                        editor.commit();
                        resetApps();
                    }


                    @Override
                    public void onColorChanged(@ColorInt int color, String hex, String hexNoAlpha) {

                        // when the device is rotated
                        colorPicker.setDialogButtonTextColor(color);
                    }
                });

                colorPicker.show();
            }
        });
    }

    boolean useCustomBack = false;

    private void initUseCustomBack(){
        use_custom_background = findViewById(R.id.use_custom_symbol_background);
        custom_background_layout = findViewById(R.id.use_custom_background_layout);
        useCustomBack = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(BUBBLE_USE_CUSTOM_BACK,false);
        use_custom_background.setChecked(useCustomBack);
        use_custom_background.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(BUBBLE_USE_CUSTOM_BACK,b);
                useCustomBack = b;
                editor.commit();
                displayBackDisable();
                resetApps();
            }
        });
    }

    boolean display_symbol_back = false;

    private void initDisplayBack(){


        display_symbol_background = findViewById(R.id.activate_symbol_background);
        display_symbol_back = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(BUBBLE_DIPLSAY_BACK_KEY,false);
        displayBackDisable();
        display_symbol_background.setChecked(display_symbol_back);
        display_symbol_background.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                editor.putBoolean(BUBBLE_DIPLSAY_BACK_KEY,b);
                editor.commit();
                display_symbol_back = b;
                displayBackDisable();
                resetApps();
            }
        });
    }

    private void displayBackDisable(){
        display_symbol_back = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(BUBBLE_DIPLSAY_BACK_KEY,false);
        useCustomBack = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(BUBBLE_USE_CUSTOM_BACK,false);

        if(display_symbol_back){
            app_background_shape.setVisibility(View.VISIBLE);
            background_ratio_layout.setVisibility(View.VISIBLE);
            custom_background_layout.setVisibility(View.VISIBLE);
            if(useCustomBack){
                app_background_color.setVisibility(View.VISIBLE);
                iconColorScheme.setVisibility(View.GONE);
            }else{
                iconColorScheme.setVisibility(View.VISIBLE);
                app_background_color.setVisibility(View.GONE);
            }
        }else{
            iconColorScheme.setVisibility(View.GONE);
            background_ratio_layout.setVisibility(View.GONE);
            app_background_shape.setVisibility(View.GONE);
            custom_background_layout.setVisibility(View.GONE);
            app_background_color.setVisibility(View.GONE);
        }

    }*/

    private void initAppView(){
        icon_wrapper = findViewById(R.id.icon_wrapper);
        appPoints = getFolderPoints(appNumber);
        apps = getRandomApps(appNumber,getApplicationContext(),asize);
        //icon_wrapper.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, asize*3));

        for (int n = 0; n < appNumber; n++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);

            Point pxPoint = rasterToPixel(appPoints.get(n), asize, padding,getApplicationContext());

            params.leftMargin = pxPoint.x;
            params.topMargin = pxPoint.y;

            for(AppView app : apps){
                app.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        //outline.setRoundRect(0,0,asize,asize,asize/2);
                        outline.setOval(10,10,asize-10,asize-10);
                    }
                });
                app.setElevation((PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BUBBLE_SHADOW,100)));
                app.setClipToOutline(false);
            }

            apps.get(n).setLayoutParams(params);
            apps.get(n).setVisibility(View.VISIBLE);
            icon_wrapper.addView(apps.get(n));
        }
    }

    private void resetApps(){
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

            for(AppView app : apps){
                app.setOutlineProvider(new ViewOutlineProvider() {
                    @Override
                    public void getOutline(View view, Outline outline) {
                        //outline.setRoundRect(0,0,asize,asize,asize/2);
                        outline.setOval(10,10,asize-10,asize-10);
                    }
                });
                app.setElevation((PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BUBBLE_SHADOW,100)));
                app.setClipToOutline(false);
            }
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
