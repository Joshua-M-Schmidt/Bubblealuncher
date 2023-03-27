package source.nova.com.bubblelauncherfree.StartConfigActivities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.R;
import android.text.Html;
import android.widget.Toast;

public class WelcomeActivty extends AppCompatActivity {

    private ViewPager viewpager;
    private LinearLayout liner;
    private WelcomePageAdapter myadapter;

    private TextView[] mdots;
    private Button next,back;

    private int mCureentPage;

    private int currentPosition = 0;

    public static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
    public static final int PERMISSIONS_NOTIFICATION_ACCESS = 200;
    public static final int PERMISSIONS_FILE_ACCESS = 300;
    public static final String KEY_APPS_LOADED = "key_apps_loaded";
    public static final String KEY_FIRST_USE = "first_use";

    public static final String FIRST_KEY = "first_key";
    public static final String APPS_LOADED_KEY = "apps_loaded_key";
    public static final String APPS_SELECTED_INITIALISATION_KEY = "apps_selected_intiialisation_key";
    public static final String KEY_APP_LAYOUT_SELECTION_KEY = "app_layout_selection_key";
    public static final String TUTORIAL_WATCHED_KEY = "tutoiral_watched_key";
    public static final String ICON_STYLE_SET_KEY = "icon_style_set_key";

    private SharedPreferences sp;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome_activty);

        // In Activity's onCreate() for instance
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        this.sp = PreferenceManager.getDefaultSharedPreferences(this);
        this.editor = sp.edit();

        viewpager=(ViewPager)findViewById(R.id.view_pager);
        liner=(LinearLayout)findViewById(R.id.dots);

        next=(Button)findViewById(R.id.nextBtn);
        back=(Button)findViewById(R.id.backBtn);

        next.setEnabled(true);
        back.setEnabled(false);
        back.setVisibility(View.INVISIBLE);

        next.setText("NEXT");
        back.setText("");


        myadapter=new WelcomePageAdapter(this,WelcomeActivty.this );
        myadapter.addOnProgressListener(progressListener);
        viewpager.setAdapter(myadapter);
        viewpager.addOnPageChangeListener(viewlistener);
        adddots(0);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(mCureentPage+1);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewpager.setCurrentItem(mCureentPage-1);
            }
        });
    }

    public void adddots(int i){

        mdots=new TextView[7];
        liner.removeAllViews();

        for (int x=0;x<mdots.length;x++){

            mdots[x]=new TextView(this);
            mdots[x].setText(Html.fromHtml("&#8226;"));
            mdots[x].setTextSize(35);
            mdots[x].setTextColor(getResources().getColor(R.color.colorGray));

            liner.addView(mdots[x]);
        }
        if (mdots.length>0){
            mdots[i].setTextColor(getResources().getColor(R.color.colorWhite));
        }
    }

    WelcomePageAdapter.OnProgressListener progressListener = new WelcomePageAdapter.OnProgressListener() {
        @Override
        public void OnProgress(int progress) {
            if (currentPosition==0){
                next.setEnabled(true);
                back.setEnabled(false);
                back.setVisibility(View.INVISIBLE);

                next.setText("NEXT");
                back.setText("");
            }
            else if(currentPosition==mdots.length-1){

                next.setEnabled(true);
                back.setEnabled(true);
                back.setVisibility(View.VISIBLE);

                next.setText("FINISH");
                back.setText("BACK");

            }
            else {
                next.setEnabled(true);
                back.setEnabled(true );
                back.setVisibility(View.VISIBLE);

                next.setText("NEXT");
                back.setText("BACK");
            }

        }
    };

    ViewPager.OnPageChangeListener viewlistener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            currentPosition = position;
            Log.i("viewpapger","scrolled");

        }

        @Override
        public void onPageSelected(int position) {
            currentPosition = position;
            if(position == mdots.length-1){
                Intent intent = new Intent(getApplicationContext(),  SelectAppsActivity.class);
                startActivity(intent);
            }
            adddots(position);
            mCureentPage = position;

            if (position==0){
                next.setEnabled(true);
                back.setEnabled(false);
                back.setVisibility(View.INVISIBLE);

                next.setText("NEXT");
                back.setText("");
            }
            else if(position==mdots.length-1){

                next.setEnabled(true);
                back.setEnabled(true);
                back.setVisibility(View.VISIBLE);

                next.setText("FINISH");
                back.setText("BACK");

            }
            else {
                next.setEnabled(true);
                back.setEnabled(true );
                back.setVisibility(View.VISIBLE);

                next.setText("NEXT");
                back.setText("BACK");
            }

            Log.i("position", position+1 + "  "+ myadapter.progress);

            if(position+1 == myadapter.progress){
                next.setText("");
                next.setEnabled(false);
            }

            if(position == 6){
                myadapter.notifyDataSetChanged();
            }

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PERMISSIONS_NOTIFICATION_ACCESS){
            if (NotificationManagerCompat.getEnabledListenerPackages(this).contains(getPackageName())) {        //ask for permission
                //myadapter.progress += 1;
                //myadapter.listener.OnProgress(myadapter.progress);
                //myadapter.notifyDataSetChanged();
            }
            if(resultCode == RESULT_OK){
                Log.i("permission", "it worked?");
            }
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if(requestCode == 12){
            Log.i("hey", "permision granted");
        }
        if (requestCode == PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission is granted
                myadapter.progress += 2;
                myadapter.listener.OnProgress(myadapter.progress);
                myadapter.notifyDataSetChanged();
            }
        }else if(requestCode == PERMISSIONS_FILE_ACCESS){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                myadapter.progress += 1;
                myadapter.listener.OnProgress(myadapter.progress);
                myadapter.notifyDataSetChanged();
            }
        }
    }
}
