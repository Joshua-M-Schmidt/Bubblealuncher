package source.nova.com.bubblelauncherfree.StartConfigActivities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;

import androidx.core.content.ContextCompat;
import androidx.core.view.MenuItemCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.SearchView;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.Clock.Clock;
import source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity;
import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.SearchBar.SearchListAdapter;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getAllApps;
import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getAppsFromDB;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.LayoutGridAdapter.getLayout;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty.KEY_APP_LAYOUT_SELECTION_KEY;
import static source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty.KEY_FIRST_USE;

public class SelectAppsActivity extends AppCompatActivity {

    private GridView appGrid;
    private ArrayList<DataObj> appsInfo;
    private AppManager appManager;
    private ArrayList<DataObj> selectedApps;
    private SearchListAdapter adapter;
    private Button next;
    private TextView count;
    private int chooseNumber = 7;
    private Layout layout;

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    public static final String APPS_SELECTED_INITIALISATION_KEY = "apps_selected_intiialisation_key";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_apps);

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sp.edit();

        layout = getLayout(sp.getString(KEY_APP_LAYOUT_SELECTION_KEY,""));
        if(layout != null) {
            Log.i("set clock","what is happening "+ layout.isShowClock());
            chooseNumber = layout.getAppPositions().size();
            editor.putBoolean(ClockSettingActivity.SHOW_CLOCK_KEY,layout.isShowClock());
            editor.commit();
        }else{
            editor.putBoolean(KEY_FIRST_USE,true);
            editor.commit();
            Intent intent = new Intent(getApplicationContext(),  WelcomeActivty.class);
            startActivity(intent);
        }

        appManager = new AppManager(getApplicationContext());
        appsInfo = new ArrayList<>();
        selectedApps = new ArrayList<>();
        appsInfo.addAll(getAllApps(getApplicationContext()));

        next = findViewById(R.id.next);
        count = findViewById(R.id.count);

        appGrid = findViewById(R.id.app_grid);

        for(DataObj app : appsInfo){
            app.drawable = new BitmapDrawable(appManager.getAppIconNonCache(app.package_name, Util.getDiam(getApplicationContext()),getApplicationContext()));
        }

        adapter = new SearchListAdapter(getApplicationContext(), appsInfo);
        appGrid.setAdapter(adapter);

        updateCount();

        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                if(true) {
                    DataObj app = adapter.getItem(i);
                    if (app.selected) {
                        app.selected = false;
                        view.setBackground(null);
                        selectedApps.remove(app);
                    } else {
                        if(selectedApps.size() < chooseNumber){
                            app.selected = true;
                            view.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.buy_button));
                            selectedApps.add(app);
                        }

                    }

                    Log.i("apps selected", selectedApps.size() + " ");
                }
                updateCount();

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                editor.putString(APPS_SELECTED_INITIALISATION_KEY,getPackageStrings());
                editor.apply();

                ArrayList<DataObj> apps = getAppsFromDB(getApplicationContext());

                for(DataObj app : apps){
                   AppManager.removeAppFromHomeScreen(app.package_name,getApplicationContext());
                }

                for(DataObj app : selectedApps){
                    AppManager.addAppToHomeScreen(app.package_name,getApplicationContext());
                }

                for(int i = 0; i < selectedApps.size(); i++){
                    AppManager.writeAppPosition(selectedApps.get(i).package_name,layout.getAppPositions().get(i).x,layout.getAppPositions().get(i).y,getApplicationContext());
                }

                Clock.setClockPosition(layout.getClockPosition(),getApplicationContext());

                editor.putBoolean(KEY_FIRST_USE,false);
                editor.commit();

                next.setAlpha(0.2f);
                next.setEnabled(false);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                //go to next activity
            }
        });

    }

    private String getPackageStrings(){
        ArrayList<String> list = new ArrayList<>();

        for(DataObj app : selectedApps){
            list.add(app.package_name);
        }

        Gson gson = new Gson();

        gson.toJson(list);

        String json = gson.toJson(list);

        return json;
    }

    private void updateCount(){

        count.setText("At least "+chooseNumber+" ("+selectedApps.size()+")");

        if(selectedApps.size() >= chooseNumber){
            next.setAlpha(1f);
            next.setEnabled(true);
        }else{
            next.setAlpha(0.2f);
            next.setEnabled(false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_select_activity, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchView.clearFocus();
             /*   if(list.contains(query)){
                    adapter.getFilter().filter(query);
                }else{
                    Toast.makeText(MainActivity.this, "No Match found",Toast.LENGTH_LONG).show();
                }*/
                return false;

            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String text = newText;

                ArrayList<DataObj> subList = new ArrayList<>();

                for (int i = 0; i < appsInfo.size(); i++) {
                    Log.i(appsInfo.get(i).name, appsInfo.get(i).name.indexOf(text) + "");
                    if (appsInfo.get(i).name.toLowerCase().indexOf(text.toLowerCase()) != -1) {
                        subList.add(appsInfo.get(i));
                    }
                }

                adapter = new SearchListAdapter(getApplicationContext(), subList);
                appGrid.setAdapter(adapter);
                return false;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }
}
