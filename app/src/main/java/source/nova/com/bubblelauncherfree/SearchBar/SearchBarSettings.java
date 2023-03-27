package source.nova.com.bubblelauncherfree.SearchBar;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.annotation.ColorInt;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.cameron.materialcolorpicker.ColorPicker;
import com.cameron.materialcolorpicker.ColorPickerCallback;

import source.nova.com.bubblelauncherfree.SettingsActivities.BuyingActivity;
import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

public class SearchBarSettings extends AppCompatActivity {

    private SearchBar searchBar;
    private LinearLayout main_layout;
    private RelativeLayout searchBarWrapper;
    private LinearLayout premiumView;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    private RelativeLayout search_bar_background_color;

    private SeekBar search_bar_border_radius;
    private TextView search_bar_border_radius_title;

    private SeekBar search_bar_margin;
    private TextView search_bar_margin_title;

    private RelativeLayout search_bar_text_color;

    private RelativeLayout search_bar_layout;

    @Override
    public void onCreate(Bundle sis) {
        super.onCreate(sis);


        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();

        initSearchBarWrapper();
        initSearchBar();

        if(!MainActivity.IS_PREMIUM){
            premiumView.setVisibility(View.VISIBLE);
            premiumView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(SearchBarSettings.this,BuyingActivity.class));
                }
            });
        }
    }

    public static final String SEARCHBAR_LAYOUT_KEY = "searchbar_layout_key";
    public static final String SEARCHBAR_LAYOUT_SMALL = "small searchbar layout";
    public static final String SEARCHBAR_LAYOUT_BIG = "big searchbar layout";



    public static final String SEARCHBAR_TEXT_COLOR_KEY = "searchbar_text_color_key";



    public static final String SEARCHBAR_MARGIN_KEY = "searchbar_margin_key";


    public static final String SEARCHBAR_BORDER_RADIUS_KEY = "searchbar_border_radius_key";


    public static final String SEARCHBAR_BACKGROUND_COLOR = "search_bar_backgroudn_color";



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

    private void initSearchBar(){
        searchBar = new SearchBar(getApplicationContext());
        searchBarWrapper.addView(searchBar);
        searchBar.unfocus();
        MainActivity.hideKeyboard(SearchBarSettings.this);searchBar.unfocus();
        MainActivity.hideKeyboard(SearchBarSettings.this);
    }

    private void initSearchBarWrapper(){
        searchBarWrapper = findViewById(R.id.search_bar_wraper);

        if(!PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("wallpaper_key",false)){
            searchBarWrapper.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt("background_color",0x000000));
        }else{
            searchBarWrapper.setBackground(null);
        }

        int h = Util.rasterToPixel(new Point(3,3),Util.getDiam(getApplicationContext()), Util.getPadding(getApplicationContext()), getApplicationContext()).y-Util.getPadding(getApplicationContext())*2;

        LinearLayout.LayoutParams clockWrapper_layout = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,h);
        searchBarWrapper.setLayoutParams(clockWrapper_layout);
    }
}
