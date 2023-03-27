package source.nova.com.bubblelauncherfree.SettingsActivities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.IconPackSettings.IconPackActivity;
import source.nova.com.bubblelauncherfree.IconPackSettings.IconPackListAdapter;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Point;

import static source.nova.com.bubblelauncherfree.IconPackSettings.IconPackActivity.ICON_PACK_KEY;
import static source.nova.com.bubblelauncherfree.IconPackSettings.IconPackActivity.NO_ICON_PACK_PACKAGE;

public class SelectIconPack extends AppCompatActivity {

    private ListView iconList;
    private IconPackListAdapter adapter;
    private ArrayList<String> iconPacks;
    private ArrayList<DataObj> iconPacks_apps;
    private Button next;
    private Button search;
    private Button round;
    private Button applewatch;
    private CardView recommend;

    private String selectedPackage = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_icon_pack);

        iconPacks_apps = new ArrayList<>();

        search = findViewById(R.id.search);
        round = findViewById(R.id.round);
        applewatch = findViewById(R.id.applewatch);
        recommend = findViewById(R.id.recommend);

        if(iconPacks_apps.size() < 3){
            recommend.setVisibility(View.VISIBLE);
            search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=icon pack go launcher")));
                    }catch (android.content.ActivityNotFoundException afe){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=icon pack go launcher")));
                    }
                }
            });

            round.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=icon pack go launcher")));
                    }catch (android.content.ActivityNotFoundException afe){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=icon pack go launcher")));
                    }
                }
            });

            applewatch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    try{
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=icon pack go launcher")));
                    }catch (android.content.ActivityNotFoundException afe){
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/search?q=icon pack go launcher")));
                    }
                }
            });
        }else{
            recommend.setVisibility(View.GONE);
        }

        next = findViewById(R.id.next);

        DataObj app = new DataObj("no icon pack",NO_ICON_PACK_PACKAGE,0,0);

        app.drawable = getDrawable(R.drawable.blur_white);

        updateNext();

        iconPacks_apps.add(app);

        iconPacks = IconPackActivity.getInstalledIconPacks("com.gau.go.launcherex.theme",getApplicationContext());
        for(String iconPackPackage : iconPacks){
            DataObj temp = AppManager.getDataObjFromPackage(iconPackPackage,getApplicationContext(),new Point(0,0));
            temp.setDrawable(getApplicationContext());
            iconPacks_apps.add(temp);
        }

        adapter = new IconPackListAdapter(getApplicationContext(), iconPacks_apps);

        iconList = findViewById(R.id.icon_pack_list);
        iconList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataObj app = adapter.getItem(i);
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString("iconPacks",app.package_name).apply();


                if(true) {
                    if (app.selected) {
                        app.selected = false;
                        view.setBackgroundColor(Color.TRANSPARENT);
                        selectedPackage = "";
                    } else {
                        app.selected = true;
                        view.setBackgroundColor(Color.GREEN);
                        selectedPackage = app.package_name;
                    }

                    Log.i("apps selected", selectedPackage + " ");
                }

                updateNext();
                //startActivity(new Intent(getApplication(), MainActivity.class));
            }
        });

        iconList.setAdapter(adapter);
        iconList.setDividerHeight(0);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString(ICON_PACK_KEY, selectedPackage);
                editor.commit();

                next.setAlpha(0.2f);
                next.setEnabled(false);

                DiskLruImageCache cache;

                cache = new DiskLruImageCache(getApplicationContext(),
                        "app_icons_new",
                        1024 * 1024 * 10 /* 10 mb */,
                        Bitmap.CompressFormat.PNG,
                        100);
                cache.clearCache();

            }
        });
    }

    public static boolean isSet(Context ctx){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(ctx);
        if(sharedPref.getString(ICON_PACK_KEY,"").isEmpty()){
            return false;
        }else {
            return true;
        }
    }


    private void updateNext(){

        if(!selectedPackage.isEmpty()){
            next.setAlpha(1f);
            next.setEnabled(true);
        }else{
            next.setAlpha(0.2f);
            next.setEnabled(false);
        }
    }
}
