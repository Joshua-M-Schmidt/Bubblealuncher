package source.nova.com.bubblelauncherfree.SettingsActivities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;
import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.CustomViews.FolderView;
import source.nova.com.bubblelauncherfree.FolderManager.FolderManager;
import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Theme;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;
import source.nova.com.bubblelauncherfree.Async.CreateFolder;
import source.nova.com.bubblelauncherfree.Async.FetchCategories;
import source.nova.com.bubblelauncherfree.Async.ResetApps;
import source.nova.com.bubblelauncherfree.Async.SortApps;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getAllAppsFromDB;
import static source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity.SHOW_CLOCK_KEY;

public class ResetActivity extends AppCompatActivity{

    Button classicButton;
    Button theme1;
    Button theme2;
    Button theme3;
    Button theme4;
    Button theme5;
    Button theme;
    private ProgressDialog dialog;

    TextView premium_label_1;
    TextView premium_label_2;
    TextView premium_label_3;
    TextView premium_label_4;
    TextView premium_label_5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset);

        premium_label_1 = findViewById(R.id.premium_label_1);
        premium_label_2 = findViewById(R.id.premium_label_2);
        premium_label_3 = findViewById(R.id.premium_label_3);
        premium_label_4 = findViewById(R.id.premium_label_4);
        premium_label_5 = findViewById(R.id.premium_label_5);

        if(MainActivity.IS_PREMIUM){
            premium_label_1.setVisibility(View.GONE);
            premium_label_2.setVisibility(View.GONE);
            premium_label_3.setVisibility(View.GONE);
            premium_label_4.setVisibility(View.GONE);
            premium_label_5.setVisibility(View.GONE);
        }

        theme = findViewById(R.id.theme);
        theme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                applyTheme(Theme.getClassicTheme());
            }
        });

        classicButton = findViewById(R.id.classic_button);
        classicButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Theme.getClassicTheme().apply(getApplicationContext());
                SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = pref.edit();
                editor.putBoolean(SHOW_CLOCK_KEY,false);
                editor.commit();
                reset();
            }
        });



        theme1 = findViewById(R.id.theme1);
        theme1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.IS_PREMIUM)
                    applyTheme(Theme.getTheme1());
                else
                    openPremiumActivity();
            }
        });

        theme2 = findViewById(R.id.theme2);
        theme2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.IS_PREMIUM)
                    applyTheme(Theme.getTheme2());
                else
                    openPremiumActivity();
            }
        });

        theme3 = findViewById(R.id.theme3);
        theme3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.IS_PREMIUM)
                    applyTheme(Theme.getTheme3());
                else
                    openPremiumActivity();
            }
        });

        theme4 = findViewById(R.id.theme4);
        theme4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.IS_PREMIUM)
                    applyTheme(Theme.getTheme4());
                else
                    openPremiumActivity();
            }
        });

        theme5 = findViewById(R.id.theme5);
        theme5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.IS_PREMIUM)
                    applyTheme(Theme.getTheme5());
                else
                    openPremiumActivity();
            }
        });

        dialog = new ProgressDialog(ResetActivity.this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    public void openPremiumActivity(){
        startActivity(new Intent(getApplicationContext(),BuyingActivity.class));
    }

    public void applyTheme(Theme theme){
        if(Util.isNetworkAvailable(getApplicationContext())){
            theme.apply(getApplicationContext());
            ResetApps resetTask = new ResetApps(ResetActivity.this, new ResetApps.OnAppsReset() {
                @Override
                public void onAppsReseted() {
                    getAppCategories();
                }
            });
            resetTask.execute();
        }else{
            showNetworkNotAvailableWarning();
        }

    }

    private void showNetworkNotAvailableWarning(){
        androidx.appcompat.app.AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new androidx.appcompat.app.AlertDialog.Builder(ResetActivity.this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new androidx.appcompat.app.AlertDialog.Builder(ResetActivity.this);
        }
        builder.setTitle("No Internet Connection")
                .setMessage("The sorting works only with an active internet connection")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        // continue with delete
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void getAppCategories(){
        ArrayList<String> standartApps = SortApps.defaultProgramms(getApplicationContext());
        ArrayList<DataObj> sublist = SortApps.getNotContainingSublist(ResetActivity.getSublist(getAllAppsFromDB(getApplicationContext())),standartApps);
        FetchCategories catTask = new FetchCategories(ResetActivity.this, sublist, new FetchCategories.OnCategoriesFetched() {
            @Override
            public void onCategoriesFetched(ArrayList<DataObj> appsCat) {
                createCategoryFolders();
            }
        });
        catTask.execute();
    }

    private void createCategoryFolders(){
        CreateFolder folderTask = new CreateFolder(ResetActivity.this, new CreateFolder.OnFoldersCreate() {
            @Override
            public void onFoldersCreated() {
                sortAppsByCategorie();
            }
        });
        folderTask.execute();
    }

    private void startMainActivity(){
        startActivity(new Intent(this,MainActivity.class));
    }

    private void sortAppsByCategorie() {
        SortApps sortTask = new SortApps(ResetActivity.this, new SortApps.OnAppsSort() {
            @Override
            public void onAppsSorted() {
                startMainActivity();
            }
        });
        sortTask.execute();
    }

    private void sortApps(){
        SortApps sortTask = new SortApps(ResetActivity.this, new SortApps.OnAppsSort() {
            @Override
            public void onAppsSorted() {

            }
        });
        sortTask.execute();
    }

    public static ArrayList<DataObj> getSublist(ArrayList<DataObj> apps){
        ArrayList<DataObj> sublist = new ArrayList<>();
        for(DataObj app : apps){
            if(!app.package_name.startsWith("com.android") && !app.package_name.startsWith("com.google") && app.category == null){
                sublist.add(app);
            }
        }
        Log.i("sublist opt",apps.size() +" transformed to: "+sublist.size());

        return sublist;
    }

    public void reset(){
        dialog.setMessage("Reset Apps");
        dialog.show();
        Thread reset = new Thread(){
            @Override
            public void run(){
                backReset();
                dialog.dismiss();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        };
        reset.start();
    }

    public void  backReset(){
        ArrayList<DataObj> apps = AppManager.getAllAppsFromDB(getApplicationContext());
        ArrayList<FolderView> folders = FolderManager.getFoldersFromDB(getApplicationContext());
        for(DataObj app : apps){
            AppManager.removeFolderAttrFromApp(getApplicationContext(),app.package_name);
        }

        for(FolderView folder : folders){
            FolderManager.deleteFolder(folder.folderName,getApplicationContext());
        }

        ArrayList<Point> points = AppManager.getPoints(apps.size());

        for(int i = 0; i < points.size(); i++){
            AppManager.updateAppPosition(points.get(i),getApplicationContext(),apps.get(i).package_name);
        }
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