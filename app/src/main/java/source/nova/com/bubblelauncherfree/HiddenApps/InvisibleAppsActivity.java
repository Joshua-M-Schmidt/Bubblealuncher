package source.nova.com.bubblelauncherfree.HiddenApps;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.DataObj;

public class InvisibleAppsActivity extends AppCompatActivity {

    private ListView appList;
    private HiddenAppListAdapter adapter;
    private ArrayList<DataObj> apps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invisible_apps);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        apps = AppManager.getHiddenAppsFromDB(getApplicationContext());
        for(DataObj app : apps){
            app.setDrawable(getApplicationContext());
            app.visiblity = 0;
        }

        adapter = new HiddenAppListAdapter(getApplicationContext(), apps);

        appList = findViewById(R.id.app_list);
        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                DataObj app = adapter.getItem(i);
                if(app.visiblity == 0){
                    AppManager.setAppAtFreePosition(getApplicationContext(),app.package_name);
                    AppManager.unhideApp(app.package_name,getApplicationContext());
                    app.visiblity = 1;
                    ImageView hidden_symbol = view.findViewById(R.id.hidden_app);
                    hidden_symbol.setImageResource(R.drawable.visibility_white);
                }else if(app.visiblity == 1){
                    AppManager.hideApp(app.package_name,getApplicationContext());
                    app.visiblity = 0;
                    ImageView hidden_symbol = view.findViewById(R.id.hidden_app);
                    hidden_symbol.setImageResource(R.drawable.visibility_off_white);
                }
            }
        });
        appList.setAdapter(adapter);
        appList.setDividerHeight(0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(this, MainActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
