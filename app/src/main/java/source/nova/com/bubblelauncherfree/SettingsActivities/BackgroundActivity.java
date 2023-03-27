package source.nova.com.bubblelauncherfree.SettingsActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.SwitchCompat;

import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.skydoves.colorpickerview.AlphaTileView;
import com.skydoves.colorpickerview.ColorEnvelope;
import com.skydoves.colorpickerview.ColorPickerDialog;
import com.skydoves.colorpickerview.listeners.ColorEnvelopeListener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;

public class BackgroundActivity extends AppCompatActivity {

    public static final String PREF_WALLPAPER_TOGGLE = "wallpaper_key";
    public static final String PREF_BACKGROUND_COLOR = "background_color";
    public static final String PREF_BACKGROUND_SCROLLABLE = "background_scrollable";


    AlphaTileView tileView;
    RelativeLayout chooseWallpaper;
    SwitchCompat scrollableWallpaper;
    SwitchCompat useSolidColor;
    RelativeLayout chooseColor;

    Context ctx;

    boolean useSolidColorB;
    boolean scrollableWallpaperB;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_background);


        ctx = this;

        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();

        scrollableWallpaperB = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PREF_BACKGROUND_SCROLLABLE,false);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        tileView = findViewById(R.id.alphaTileView);
        tileView.setPaintColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(PREF_BACKGROUND_COLOR,0x000000));

        chooseWallpaper = findViewById(R.id.wallpaper);
        chooseWallpaper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(scrollableWallpaperB){
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    intent.putExtra("crop", "true");
                    intent.putExtra("scale", true);
                    intent.putExtra("outputX", 256);
                    intent.putExtra("outputY", 256);
                    intent.putExtra("aspectX", 1);
                    intent.putExtra("aspectY", 1);
                    intent.putExtra("return-data", true);
                    startActivityForResult(intent, 17);
                }else{
                    Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                    startActivity(Intent.createChooser(intent, "Select Wallpaper"));
                }

            }
        });

        useSolidColorB = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PREF_WALLPAPER_TOGGLE,false);
        useSolidColor = findViewById(R.id.use_solid_color);
        useSolidColor.setChecked(useSolidColorB);
        useSolidColor.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                useSolidColorB = b;
                editor.putBoolean(PREF_WALLPAPER_TOGGLE,b);
                editor.commit();
                hideIrrelvantSettings();
            }
        });

        scrollableWallpaper = findViewById(R.id.scroll_wallpaper);
        scrollableWallpaper.setChecked(scrollableWallpaperB);
        scrollableWallpaper.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ContextWrapper cw = new ContextWrapper(getApplicationContext());
                File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                File f=new File(directory, "profile.jpg");


                if(b){
                    if(f.exists()){
                        scrollableWallpaperB = true;
                        editor.putBoolean(PREF_BACKGROUND_SCROLLABLE,true);
                        editor.commit();
                    }else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                        builder.setMessage(R.string.pref_scrollable_back_dialog)
                                .setPositiveButton(R.string.pref_scrollable_back_dialog_choose, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        Intent intent = new Intent(Intent.ACTION_PICK,
                                                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                                        intent.setType("image/*");
                                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                                        intent.putExtra("crop", "true");
                                        intent.putExtra("scale", true);
                                        intent.putExtra("outputX", 256);
                                        intent.putExtra("outputY", 256);
                                        intent.putExtra("aspectX", 1);
                                        intent.putExtra("aspectY", 1);
                                        intent.putExtra("return-data", true);
                                        startActivityForResult(intent, 17);
                                    }
                                })
                                .setNegativeButton(R.string.pref_scrollable_back_dialog_no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        scrollableWallpaper.setChecked(false);
                                        dialog.dismiss();
                                    }
                                });

                        builder.show();
                    }
                }else{
                    scrollableWallpaperB = false;
                    editor.putBoolean(PREF_BACKGROUND_SCROLLABLE,false);
                    editor.commit();
                }

            }
        });

        chooseColor = findViewById(R.id.back_color);
        chooseColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ColorPickerDialog.Builder(BackgroundActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_DARK)
                        .setTitle("Background Color")
                        .setPositiveButton("OK",
                                new ColorEnvelopeListener() {
                                    @Override
                                    public void onColorSelected(ColorEnvelope envelope, boolean fromUser) {
                                        tileView.setPaintColor(envelope.getColor());
                                        editor.putInt(PREF_BACKGROUND_COLOR,envelope.getColor());
                                        editor.commit();
                                    }
                                })
                        .setNegativeButton("CANCEL",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                })
                        .setPreferenceName(PREF_BACKGROUND_COLOR)
                        .attachAlphaSlideBar(false)
                        .attachBrightnessSlideBar(true)  // default is true. If false, do not show the BrightnessSlideBar.
                        .show();
            }
        });

        hideIrrelvantSettings();

    }

    private void hideIrrelvantSettings(){
        if(useSolidColorB){
            chooseWallpaper.setVisibility(View.GONE);
            findViewById(R.id.scrollable_wallpaper_container).setVisibility(View.GONE);
            chooseColor.setVisibility(View.VISIBLE);
        }else{
            chooseColor.setVisibility(View.INVISIBLE);
            chooseWallpaper.setVisibility(View.VISIBLE);
            findViewById(R.id.scrollable_wallpaper_container).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("hello_back","something happeens "+requestCode);
        if (requestCode == 17) {
            Log.d("hello_back","back image is here");
            if(data == null){
                return;
            }
            Uri uri = data.getData();

            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                saveToInternalStorage(bitmap);
                scrollableWallpaperB = true;
                editor.putBoolean(PREF_BACKGROUND_SCROLLABLE,true);
                editor.commit();
            } catch (IOException e) {
                e.printStackTrace();
            }


            Log.d("save","back image");


            //Now you can do whatever you want with your inpustream, save it as file, upload to a server, decode a bitmap...
        }
    }

    private String saveToInternalStorage(Bitmap bitmapImage){
        ContextWrapper cw = new ContextWrapper(getApplicationContext());
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath=new File(directory,"profile.jpg");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
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
