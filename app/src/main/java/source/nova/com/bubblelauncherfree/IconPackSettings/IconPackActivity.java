package source.nova.com.bubblelauncherfree.IconPackSettings;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.SettingsActivities.IconSettingsActivity;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Point;

public class IconPackActivity extends AppCompatActivity {


    public static final String ICON_PACK_KEY = "iconPacks";
    public static final String NO_ICON_PACK_PACKAGE = "iconPacks";

    private ListView iconList;
    private IconPackListAdapter adapter;
    private ArrayList<String> iconPacks;
    private ArrayList<DataObj> iconPacks_apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon_pack);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        iconPacks_apps = new ArrayList<>();

        DataObj app = new DataObj("no icon pack",NO_ICON_PACK_PACKAGE,0,0);

        iconPacks_apps.add(app);

        iconPacks = getInstalledIconPacks("com.gau.go.launcherex.theme",getApplicationContext());
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
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putString(ICON_PACK_KEY,app.package_name).apply();
                DiskLruImageCache cache;

                    cache = new DiskLruImageCache(getApplicationContext(),
                            "app_icons_new",
                            1024 * 1024 * 10 /* 10 mb */,
                            Bitmap.CompressFormat.PNG,
                            100);
                    cache.clearCache();

                startActivity(new Intent(getApplication(), IconSettingsActivity.class));
            }
        });

        iconList.setAdapter(adapter);
        iconList.setDividerHeight(0);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(this, IconSettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static ArrayList<String> getInstalledIconPacks(String filter, Context ctx)
    {
        ArrayList<String> packs = new ArrayList<>();

        List<ResolveInfo> infos = ctx.getPackageManager().queryIntentActivities(new Intent(filter), PackageManager.GET_META_DATA);
        if (infos != null)
        {
            for (int i = 0; i < infos.size(); i++)
            {
                ActivityInfo activity = infos.get(i).activityInfo;
                String packageName = activity.packageName;
                if (packageName != null)
                    packs.add(packageName);
            }
        }

        return packs;
    }

}
