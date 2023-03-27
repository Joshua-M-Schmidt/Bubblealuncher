package source.nova.com.bubblelauncherfree;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.app.WallpaperManager;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Outline;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.collection.LruCache;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GestureDetectorCompat;
import androidx.appcompat.app.AppCompatActivity;

import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.DisplayCutout;
import android.view.DragEvent;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.view.ViewOutlineProvider;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.ScrollableViewHelper;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import hotchemi.android.rate.AppRate;
import hotchemi.android.rate.OnClickButtonListener;
import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.Clock.Clock;
import source.nova.com.bubblelauncherfree.Clock.ClockSettingActivity;
import source.nova.com.bubblelauncherfree.CustomViews.AppView;
import source.nova.com.bubblelauncherfree.CustomViews.FolderView;
import source.nova.com.bubblelauncherfree.CustomViews.HorizontalListView;
import source.nova.com.bubblelauncherfree.CustomViews.MaterialBadgeTextView;
import source.nova.com.bubblelauncherfree.CustomViews.TwoDScrollView;
import source.nova.com.bubblelauncherfree.FolderManager.FolderManager;
import source.nova.com.bubblelauncherfree.Notification.NLService;
import source.nova.com.bubblelauncherfree.Notification.Notification;
import source.nova.com.bubblelauncherfree.Notification.NotificationManager;
import source.nova.com.bubblelauncherfree.Receiver.AppChangeReceiver;
import source.nova.com.bubblelauncherfree.Receiver.AppInstallReceiver;
import source.nova.com.bubblelauncherfree.SearchBar.ContactListAdapter;
import source.nova.com.bubblelauncherfree.SearchBar.SearchBar;
import source.nova.com.bubblelauncherfree.SearchBar.SearchListAdapter;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;
import source.nova.com.bubblelauncherfree.SettingsActivities.BuyingActivity;
import source.nova.com.bubblelauncherfree.SettingsActivities.SearchbarActivity;
import source.nova.com.bubblelauncherfree.StartConfigActivities.WelcomeActivty;
import source.nova.com.bubblelauncherfree.Util.AppSaveInfo;
import source.nova.com.bubblelauncherfree.Util.Contacts;
import source.nova.com.bubblelauncherfree.Util.DBCleaner;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Message;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;
import source.nova.com.bubblelauncherfree.Wigets.WidgetHost;
import source.nova.com.bubblelauncherfree.Wigets.WidgetInfo;
import source.nova.com.bubblelauncherfree.Wigets.WidgetManager;
import source.nova.com.bubblelauncherfree.Wigets.WidgetView;
import source.nova.com.bubblelauncherfree.Async.SearchBarData;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.*;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.changeFolderName;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.deleteFolder;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.folderIntersection;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getAppsInFolder;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getAppsInFolderAppPackage;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.getfolderIntersection;
import static source.nova.com.bubblelauncherfree.Settings.SettingsActivity2.checkPremium;
import static source.nova.com.bubblelauncherfree.SettingsActivities.BackgroundActivity.PREF_BACKGROUND_COLOR;
import static source.nova.com.bubblelauncherfree.SettingsActivities.BackgroundActivity.PREF_BACKGROUND_SCROLLABLE;
import static source.nova.com.bubblelauncherfree.SettingsActivities.BackgroundActivity.PREF_WALLPAPER_TOGGLE;
import static source.nova.com.bubblelauncherfree.SettingsActivities.IconSettingsActivity.BUBBLE_SHADOW;
import static source.nova.com.bubblelauncherfree.SettingsActivities.LayoutActivity.HIDE_STATUS_BAR_KEY;
import static source.nova.com.bubblelauncherfree.SettingsActivities.SearchbarActivity.DRAGPIN_HEIGHT;
import static source.nova.com.bubblelauncherfree.SettingsActivities.SearchbarActivity.DRAGPIN_RADIUS;
import static source.nova.com.bubblelauncherfree.SettingsActivities.SearchbarActivity.DRAGPIN_WIDTH;
import static source.nova.com.bubblelauncherfree.Util.Util.rasterToPixel;
import static source.nova.com.bubblelauncherfree.Util.Util.rasterToPixelArea;
import static source.nova.com.bubblelauncherfree.Wigets.WidgetManager.WIDGET_INFO_KEY;

public class MainActivity extends AppCompatActivity{
    // ui
    private ImageButton searchButton;
    private FloatingActionButton widgetSizeButton;
    private FloatingActionButton settingsButton;
    private FloatingActionButton visibilityButton;
    private FloatingActionButton deleteButton;
    private FloatingActionButton designButton;
    private FloatingActionButton premiumButton;
    private FloatingActionButton anchorButton;
    private FloatingActionButton updateButton;
    private FloatingActionButton removeFromHomeScreen;
    private FloatingActionButton add_widget_button;
    private FloatingActionButton closeSettingsMode;

    private EditText searchText;
    private TextView searchTextView;
    private SearchBar searchBar;
    private SlidingUpPanelLayout slidingUpPanelLayout;
    private ScrollView scrollSearch;

    public static final String SETTINGS_KEY = "settings";

    private float mx, my;

    private RelativeLayout layout;
    private ImageView background_image;
    private TwoDScrollView scroller;

    //folder stuff
    private RelativeLayout folderOverlay;
    public boolean folderOpen = false;
    public ArrayList<AppView> currentlyInFolderOverlay = null;
    public String currentFolder = null;

    private ArrayList<AppView> apps;
    private ArrayList<FolderView> folders;
    private ArrayList<DataObj> appsInfo;

    float movedx = 0;
    float movedy = 0;

    //system parameter
    private static int width;
    private static int height;

    //runtime variables
    private String inEditModeAppPackage = "";
    private boolean dragging = false;
    private boolean scaled;
    private boolean autoscrolling = false;

    //parameter
    public static int asize;
    public static int padding;
    private boolean showlabel = false;
    private boolean settingsMode = false;

    private ArrayList<WidgetView> editableViews;

    public static boolean DEBUG_APP_POSITIONS = true;

    public static boolean IS_PREMIUM = true;

    private AppManager appManager;

    //app receiver
    AppInstallReceiver appInstallReceiver = new AppInstallReceiver();
    AppChangeReceiver appChangeReceiver = new AppChangeReceiver();

    private GestureDetectorCompat mDetector;

    private LruCache<String, Bitmap> mMemoryCache;

    private HorizontalScrollView premiumScroller;

    @Subscribe
    public void onEventMainThread(Message m) {
        switch (m.type) {
            case Message.APP_INSTALL:
                Log.i("App", "installed " + m.valuestr);
                installedApp(m.valuestr);
                break;
            case Message.APP_DEINSTALL:
                Log.i("App", "deinstalled " + m.valuestr);
                deleteApp(m.valuestr);
                unsetAppInEdit();
                break;
            case Message.STATUS_BAR_CHANGE:
                initWindow();
                break;
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private GridView appGrid;
    private LinearLayout searchResultWrapper;
    private HorizontalListView appList;
    private HorizontalListView contactList;
    private SearchListAdapter adapter;
    private ContactListAdapter contactsAdapter;
    private TextView appsListHeadline;
    private TextView contactListHeadline;

    DiskLruImageCache cache;

    SearchBarData contactsTask;
    ArrayList<DataObj> appsSearch;
    ArrayList<Contacts.ContactInfo> contactSearch;

    NotificationManager notificationManager;
    ArrayList<MaterialBadgeTextView> badges;
    RelativeLayout dragView;
    View dragPin;
    int dragPinHeight = 100;

    boolean shouldExecuteOnResume;

    public class NestedScrollableViewHelper extends ScrollableViewHelper {
        public int getScrollableViewScrollPosition(View scrollableView, boolean isSlidingUp) {
            if (appGrid instanceof GridView) {
                if(isSlidingUp){
                    return appGrid.getScrollY();
                } else {
                    GridView nsv = appGrid;
                    View child = nsv.getChildAt(0);
                    return (child.getBottom() - (nsv.getHeight() + nsv.getScrollY()));
                }
            } else {
                return 0;
            }
        }
    }

    RelativeLayout all_layout;

    @Override
    protected void onStart() {
        Log.i("lifecycle", "onstart");
        if(_appWidgetHost != null) {
            _appWidgetHost.startListening();
            //initWidgets();
            //initClockOjbects();
        }
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("lifecycle", "onresume");
        if(!firstUse) {
            if(shouldExecuteOnResume){
                // Your onResume Code Here
                initWidgets();
                initClockOjbects();
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                dragPin.setAlpha(1f);
                searchBar.setAlpha(0f);
            } else{
                shouldExecuteOnResume = true;
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(!firstUse){
            _appWidgetHost.stopListening();
            EventBus.getDefault().unregister(this);
            unregisterReceiver(appInstallReceiver);
            unregisterReceiver(appChangeReceiver);
            unregisterReceiver(nReceiver);
        }
    }

    boolean firstUse = true;

    private FloatingActionButton printThemeButton;
    public static final boolean DEBUG = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i("lifecycle", "oncreate");
        shouldExecuteOnResume = false;

        // check if the first time app is used

        firstUse = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(WelcomeActivty.KEY_FIRST_USE,true);

        if(firstUse){ // if not start the welcome activity
            Intent intent = new Intent(this,  WelcomeActivty.class);
            startActivity(intent);
        }else {

            setContentView(R.layout.activity_main);

            slidingUpPanelLayout = findViewById(R.id.sliding_layout);
            slidingUpPanelLayout.setTouchEnabled(true);
            slidingUpPanelLayout.setScrollableViewHelper(new NestedScrollableViewHelper());
            slidingUpPanelLayout.setNestedScrollingEnabled(true);

            all_layout = findViewById(R.id.all_container);

            widgetSizeButton = findViewById(R.id.widget_size);

            searchBar = new SearchBar(getApplicationContext());

            RelativeLayout searchbarwrapper = findViewById(R.id.search_bar_wraper);
            searchbarwrapper.addView(searchBar);

            dragPin = findViewById(R.id.dragPin);
            slidingUpPanelLayout.setDragView(dragView);
            contactList = findViewById(R.id.contact_list);
            appList = findViewById(R.id.app_list);
            appGrid = findViewById(R.id.app_grid);
            slidingUpPanelLayout.setScrollableView(appGrid);
            searchResultWrapper = findViewById(R.id.search_result_wraper);

            RelativeLayout.LayoutParams paramsPin = new RelativeLayout.LayoutParams(
                    (int)Util.dipToPixels(getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_WIDTH,60)),
                    (int)(Util.dipToPixels(getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_HEIGHT,20))));
            paramsPin.addRule(RelativeLayout.ABOVE, R.id.search_bar_wraper);
            paramsPin.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
            dragPin.setLayoutParams(paramsPin);

            dragPin.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, dragPin.getWidth(), dragPin.getHeight(), (int)Util.dipToPixels(getApplicationContext(),PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(DRAGPIN_RADIUS,20)));
                }
            });
            dragPin.setClipToOutline(true);

            dragPin.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(SearchbarActivity.DRAGPIN_COLOR,0xffffff66));

            dragPinHeight = (int) Util.dipToPixels(getApplicationContext(), 20);
            appsListHeadline = findViewById(R.id.apps_search_headline);
            contactListHeadline = findViewById(R.id.contactlistheadline);
            initMemoryCache();
            contactsTask = new SearchBarData(getApplicationContext(), new SearchBarData.OnContactsLoaded() {
                @Override
                public void onContactsLoaded(ArrayList<Contacts.ContactInfo> contacts, ArrayList<DataObj> appss) {
                    contactSearch = contacts;
                    appsSearch = appss;
                    initSearchView();
                }
            });

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && checkSelfPermission(Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS}, PERMISSIONS_REQUEST_READ_CONTACTS);
                //After this point you wait for callback in onRequestPermissionsResult(int, String[], int[]) overriden method
            } else {
                contactsTask.execute();
            }


            cache = new DiskLruImageCache(getApplicationContext(),
                    "app_icons_new",
                    1024 * 1024 * 10 /* 10 mb */,
                    Bitmap.CompressFormat.PNG,
                    100);

            appManager = new AppManager(getApplicationContext());

            asize = Util.getDiam(getApplicationContext());

            padding = Util.getPadding(getApplicationContext());

            // read all the appdata on the phone and write it to the DB
            // assigns every app an position on the screen in px
            // this happens only if the app is used the first time

            DBCleaner.checkForDuplicates(this);

            // initialize the system that asks the user for a app review
            initAppRate();

            // initialize that the status bar and button bar are not shown
            // this makes the app basically fullscreen
            initWindow();

            // initialize the receivers for the event that an app is added removed or changed
            initPackageEvents();

            // this sets the width and height attributes to the real device width and height in px
            initWdithHeight();

            // reads the preferences if the labels should be shown or not
            checkIfShowLabels();

            // scrolls the view to a position that the margin of 2*asize is not
            // visible when the app is opened
            initScrollViews();

            // initialize the wrapper view of the icons
            initlayout();

            initAppViewList();

            // assign a drag and drop listener to the layout view to move the
            // app icons around an position them
            initDragAndDropListener();


            initDeleteButton();

            editableViews = new ArrayList<>();

            initDesignButton();

            initFolderLabel();

            initSettingsButton();

            initVisibilityButton();

            initRemoveFromHomeScreen();

            if (!IS_PREMIUM) {
                initPremiumButton();
            }

            initAnchorButton();

            initUpdateButton();

            initStartPosition();

            layout.setPadding(
                    0,
                    0,
                    asize * 2,
                    asize * 2);
            if (!settingsMode) {
                Log.i("call checkScale", "from: " + "oncreate");
                checkScale();
            }


            initFloatingActionButtonList();

            initSettingsScrollView();

            initAddWidgetButton();

            initCloseSettingsMode();

            initNavBar();

            nReceiver = new NotificationReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("com.source.nova.NOTIFICATION_LISTENER_EXAMPLE");
            registerReceiver(nReceiver, filter);

            initWidgets();

            initClockOjbects();

            if (DEBUG) {
                initPrintThemeButton();
            }

            Intent serivceIntent = new Intent(getApplicationContext(), NLService.class);
            startService(serivceIntent);

            premiumScroller = findViewById(R.id.premium_scroller);
            findViewById(R.id.premium_scroller_layout).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),  BuyingActivity.class);
                    startActivity(intent);
                }
            });

        }
    }

    boolean display_clock;
    Clock clock;

    private void initClockOjbects(){
        display_clock = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(ClockSettingActivity.SHOW_CLOCK_KEY,false);

        if(display_clock){
            if(clock != null){

                if(clock.getParent() == null){
                    //clock = new Clock(getApplicationContext(),asize,padding);
                    clock.setTag("___clock");
                    layout.addView(clock);
                    //editableViews.add(clock);

                    Point newPosition = Clock.getClockPosition(getApplicationContext());

                    Point size3 = Clock.getClockSize(getApplicationContext());
                    RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size3.x,size3.y);
                    Point pxp = rasterToPixel(newPosition,asize,padding,getApplicationContext());
                    clock.setPadding(0,0,0,0);
                    params.leftMargin = pxp.x;
                    params.topMargin = pxp.y+padding;
                    clock.setLayoutParams(
                            params
                    );

                    //clock.setBackgroundColor(0xff22ff22);

                    clock.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            if (!settingsMode)
                                toggleSettingsMode();
                            if(widgetNotTouchable) {
                                //return true;
                            }
                            inEditModeAppPackage = "___clock";
                            setViewInEdit(clock);

                            return false;
                        }
                    });
                }

            }else{
                clock = new Clock(getApplicationContext(),asize,padding);
                clock.setTag("___clock");
                layout.addView(clock);
                //editableViews.add(clock);

                Point newPosition = Clock.getClockPosition(getApplicationContext());

                Point size3 = Clock.getClockSize(getApplicationContext());
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size3.x,size3.y);
                Point pxp = rasterToPixel(newPosition,asize,padding,getApplicationContext());
                clock.setPadding(0,0,0,0);
                params.leftMargin = pxp.x;
                params.topMargin = pxp.y+padding;
                clock.setLayoutParams(
                        params
                );

                //clock.setBackgroundColor(0xff22ff22);

                clock.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        if (!settingsMode)
                            toggleSettingsMode();
                        if(widgetNotTouchable) {
                            //return true;
                        }
                        inEditModeAppPackage = "___clock";
                        setViewInEdit(clock);

                        return false;
                    }
                });

                clock.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(widgetNotTouchable){
                            return;
                        }
                    }
                });
            }


        }
    }


    public void setWidgetsUnclickable(boolean set){
        for(View v : editableViews){
            if(set){
                v.setEnabled(false);
                v.setFocusable(false);
                v.setClickable(false);
                v.setLongClickable(false);
            }else{
                v.setEnabled(true);
                v.setFocusable(true);
                v.setClickable(true);
                v.setLongClickable(true);
            }
        }
    }

    public void initPrintThemeButton(){
        printThemeButton = findViewById(R.id.print_button);
        printThemeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(AppView app : apps){
                    Log.i("____app poistion", app.getPosition().toString());
                }

                if(display_clock){
                    Log.i("____clock position", clock.getClockPosition(getApplicationContext()).toString());
                    Log.i("____clock size", clock.getClockSizeString());
                    Log.i("____show clock", display_clock+"");
                }


                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Log.i("____start postion", new Point(preferences.getInt(X_START_KEY,0),preferences.getInt(Y_START_KEY,0)).toString());
            }
        });
    }

    public void initCloseSettingsMode(){
        closeSettingsMode = findViewById(R.id.close_settingsmode);
        closeSettingsMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(settingsMode)
                    toggleSettingsMode();
            }
        });
    }

    public void initAddWidgetButton(){
        add_widget_button = findViewById(R.id.add_widget_button);
        add_widget_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickWidget();
            }
        });
        add_widget_button.hide();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_PICK_APPWIDGET) {
                configureWidget(data);
            } else if (requestCode == REQUEST_CREATE_APPWIDGET) {
                createWidget(data.getExtras().getInt("appWidgetId", -1));
            }
        } else if (resultCode == RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra("appWidgetId", -1);
            if (appWidgetId != -1) {
                _appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }



    public static WidgetHost _appWidgetHost;
    public static AppWidgetManager _appWidgetManager;
    ArrayList<WidgetInfo> widgetInfos;
    WidgetManager widgetManager;
    private static final int APPWIDGET_HOST_ID = 0x9345;
    private static final int REQUEST_CREATE_APPWIDGET = 0x9344;
    public static final int REQUEST_PICK_APPWIDGET = 0x2678;

    public void initWidgets(){

        for(View v : editableViews){
            Log.i("initialized views", v.getTag()+" widget ");
        }

        _appWidgetManager = AppWidgetManager.getInstance(this);
        _appWidgetHost = new WidgetHost(getApplicationContext(), APPWIDGET_HOST_ID);
        _appWidgetHost.startListening();

        widgetManager = new WidgetManager(getApplicationContext());
        widgetInfos = widgetManager.getWidgetInfos();

        String widgetObjects = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString(WIDGET_INFO_KEY,"");

        Log.i("widgetObjects",widgetObjects+" ");

        if(widgetInfos != null){
            for(WidgetInfo wi : widgetInfos){
                createWidget(Integer.parseInt(wi.getTag()));
            }
        }
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt("appWidgetId", -1);
        AppWidgetProviderInfo appWidgetInfo = _appWidgetManager.getAppWidgetInfo(appWidgetId);
        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent("android.appwidget.action.APPWIDGET_CONFIGURE");
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra("appWidgetId", appWidgetId);
            startActivityForResult(intent, REQUEST_CREATE_APPWIDGET);
        } else {
            createWidget(extras.getInt("appWidgetId", -1));
        }
    }

    private static void updateWidgetOption(int id) {
        Bundle newOps = new Bundle();
        newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, 3 * 150);
        newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH, 3 * 150);
        newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 4 * 150);
        newOps.putInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT, 4 * 150);
        _appWidgetManager.updateAppWidgetOptions(id, newOps);
    }

    private void createWidget(final int appWidgetId) {
        Log.i("adding widgets","adding widgets "+appWidgetId);
        for(View v : editableViews){
            if(v.getTag().equals(appWidgetId)){
                return;
            }
        }

        AppWidgetProviderInfo appWidgetInfo = _appWidgetManager.getAppWidgetInfo(appWidgetId);

        WidgetView widgetView = (WidgetView) _appWidgetHost.createView(getApplicationContext(), appWidgetId, appWidgetInfo);

        widgetView.setAppWidget(appWidgetId, appWidgetInfo);

        widgetView.post(new Runnable() {
            @Override
            public void run() {
                updateWidgetOption(appWidgetId);
            }
        });

        WidgetInfo widgetInfo = widgetManager.getWidgetInfo(String.valueOf(appWidgetId));
        if(widgetInfo == null){
            widgetInfo = new WidgetInfo(2,2,String.valueOf(appWidgetId),4,3);
            widgetManager.addWidgetInfo(widgetInfo);
        }

        final Point widgetSize = rasterToPixelArea(new Point(widgetInfo.getW(),widgetInfo.getH()),asize,padding,getApplicationContext());
        RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(widgetSize.x,widgetSize.y);
        Point widgetPos = rasterToPixel(new Point(widgetInfo.getX(),widgetInfo.getY()),asize,padding,getApplicationContext());

        p.leftMargin = widgetPos.x;
        p.topMargin = widgetPos.y;

        widgetView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                setViewInEdit(v);
                return false;
            }
        });

        widgetView.setPadding(0,0,0,0);
        widgetView.setTag(appWidgetId);
        widgetView.setLayoutParams(p);

        /*widgetView.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0,0,widgetSize.x,widgetSize.y,asize/3);
            }
        });
        widgetView.setClipToOutline(true);*/

        layout.addView(widgetView);
        editableViews.add(widgetView);
        Log.i("add","to editableViews");
    }


    private void pickWidget() {
        int appWidgetId = _appWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent("android.appwidget.action.APPWIDGET_PICK");
        pickIntent.putExtra("appWidgetId", appWidgetId);
        startActivityForResult(pickIntent, REQUEST_PICK_APPWIDGET);
    }


    void addEmptyData(Intent pickIntent) {
        ArrayList customInfo = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_INFO, customInfo);
        ArrayList customExtras = new ArrayList();
        pickIntent.putParcelableArrayListExtra(AppWidgetManager.EXTRA_CUSTOM_EXTRAS, customExtras);
    }

    int cutout_bottom;
    int cutout_left;
    int cutout_right;

    public void initDisplayCutout(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            DisplayCutout displayCutout = getWindow().getDecorView().getRootWindowInsets().getDisplayCutout();
            cutout_bottom = displayCutout.getBoundingRects().get(0).bottom;
            cutout_left = displayCutout.getBoundingRects().get(0).left;
            cutout_right = displayCutout.getBoundingRects().get(0).right;

            Log.i("cutout_position","bottom "+cutout_bottom);
            Log.i("cutout_position","left "+cutout_left);
            Log.i("cutout_position","right "+cutout_right);
        }
    }

    public boolean isAppInCutout(Point app_position){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if(app_position.x >= cutout_left && app_position.x < cutout_right && app_position.y < cutout_bottom){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public int get_dist_to_cutout(Point app_position){
        Point left_cutout = new Point(cutout_left,cutout_bottom/2);
        Point right_cutout = new Point(cutout_right,cutout_bottom/2);
        Point bottom_cutout = new Point(cutout_left+((cutout_right-cutout_left)/2),cutout_bottom);

        int left_dist = left_cutout.distance(app_position);
        int right_dist = right_cutout.distance(app_position);
        int bottom_dist = bottom_cutout.distance(app_position);

        int smallest_cutout_dist = 0;

        if(left_dist <= right_dist && left_dist <= bottom_dist){
            smallest_cutout_dist = left_dist;
        }

        if(right_dist <= left_dist && right_dist <= bottom_dist){
            smallest_cutout_dist = right_dist;
        }

        if(bottom_dist <= left_dist && bottom_dist <= right_dist){
            smallest_cutout_dist = bottom_dist;
        }

        return smallest_cutout_dist;
    }

    private NotificationReceiver nReceiver;

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String temp = intent.getExtras().getString("notification_event");
            boolean removed = intent.getExtras().getBoolean("removed");
            if(temp == null){
                Log.i("badge hello there","its empty");
            }else{
                Log.i("badge hello there",temp +" remove: "+removed);
                for(AppView app: apps){
                    if(app.getAppPackage().equals(temp)){
                        if(removed) {
                            if(app.badge != null)
                                app.badge.setBadgeCount(0);
                            notificationManager.removeNotifications(app.getAppPackage());
                        }else {
                            app.updateBadge();
                        }
                    }
                }
            }


        }
    }

    ArrayList<Notification> notifications;

    public void initBadges(){
        Log.i("badges","init");
        notificationManager = new NotificationManager(getApplicationContext());
        badges = new ArrayList<>();
        notifications = notificationManager.getAllEntries();
    }

    public String getFolderFromApp(String appPackage){
        String ret = "";
        for(FolderView f : folders){
            ArrayList<String> a = FolderManager.getAppsInFolderAppPackage(f.getFolderName(),getApplicationContext());
            for(String app : a){
                if(app.equals(appPackage)){
                    return f.folderName;
                }
            }
        }
        return ret;
    }

    public void addToApp(MaterialBadgeTextView view, String appPackage){
        for(AppView app : apps){
            if(appPackage.equals(app.getAppPackage())){
                Log.i("badge pos",app.getPosition().toString());
                RelativeLayout.LayoutParams p = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                Point appRPOs = rasterToPixel(app.getPosition(),asize,padding,getApplicationContext());
                Point appPos = new Point(0,0);
                p.leftMargin = appPos.x;
                p.topMargin = appPos.y;
                app.addView(view);
            }
        }
    }

    Picasso p;

    public void initMemoryCache(){
        close_icon = ( R.drawable.baseline_close_24px );
        search_icon = ( R.drawable.baseline_search_24px );
        final String searchBar_style = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getString("search_bar_style","new");
        if(searchBar_style.equals("classic")){
            close_icon = ( R.drawable.close );
            search_icon = ( R.drawable.search_icon );
        }
        //p = Picasso.get();
        //p.load(search_icon).into(searchButton);
        //p.load(close_icon);
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
                contactsTask.execute();
            } else {
                Toast.makeText(this, "Until you grant the permission, we canot display the names", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;

    ImageButton googleSearchButton;
    ImageButton youtubeSearchButton;
    ImageButton duckduckgoSearchButton;
    LinearLayout searchbackground;

    public int getpixels(int dp){

        //Resources r = boardContext.getResources();
        //float px = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpis, r.getDisplayMetrics());

        final float scale = this.getResources().getDisplayMetrics().density;
        int px = (int) (dp * scale + 0.5f);



        return px;

    }

    public static void openKeypad(final Context context, final View v) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager inputManager =   (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.showSoftInput(v, InputMethodManager.SHOW_IMPLICIT);
                Log.e("openKeypad", "Inside Handler");
            }},10);
    }

    int close_icon;
    int search_icon;

    boolean closed = false;
    Handler myHandler = new Handler();

    private void initSearchView(){

        contactList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,final int position, long id) {
                hideKeyboard(MainActivity.this);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                Contacts.ContactInfo contact = contactsAdapter.getItem(position);
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_URI, String.valueOf(contact.getID()));
                    intent.setData(uri);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        appGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long id) {
                hideKeyboard(MainActivity.this);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                DataObj app = adapter.getItem(i);
                try {
                    Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(app.package_name);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });


        appGrid.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("panel state", "disableing touch");
                slidingUpPanelLayout.setTouchEnabled(false);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);

                        //slidingUpPanelLayout.cancelDragAndDrop();
                    }
                });


                settingsButton.show();
                anchorButton.show();
                //updateButton.setVisibility(View.VISIBLE);

                // don't show until provlem with widgets is solved
                if(IS_PREMIUM){
                    //add_widget_button.show();
                }

                if(!checkPremium(getApplicationContext())){
                    if(!IS_PREMIUM){
                        premiumButton.show();
                    }
                }

                settingsMode = true;

                for (int i = 0; i < apps.size(); i++) {
                    apps.get(i).setAppScale(1f);
                    apps.get(i).shake();
                }

                for (int i = 0; i < folders.size(); i++) {
                    folders.get(i).setAppScale(1f);
                    folders.get(i).shake();
                }

                AppView app = getAppView(adapter.getItem(position));

                hideKeyboard(MainActivity.this);

                ClipData data = ClipData.newPlainText(app.getAppPackage(), "");
                data.addItem(new ClipData.Item(app.getAppPackage()));
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                shadowBuilder.getView().setAlpha(1f);

                dragging = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, app, 0);
                } else {
                    view.startDrag(data, shadowBuilder, app, 0);
                }

                return false;
            }
        });

        appList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
                hideKeyboard(MainActivity.this);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                DataObj app = adapter.getItem(i);
                try {
                    Intent intent = getApplicationContext().getPackageManager().getLaunchIntentForPackage(app.package_name);
                    startActivity(intent);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        appList.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                hideKeyboard(MainActivity.this);
                return false;
            }
        });

        appList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                settingsButton.show();
                anchorButton.show();
                //updateButton.setVisibility(View.VISIBLE);
                if(IS_PREMIUM) { // dont show until problem with widgets is solved
                    //add_widget_button.show();
                }

                if(!checkPremium(getApplicationContext())){
                    if(!IS_PREMIUM){
                        premiumButton.show();
                    }
                }

                settingsMode = true;

                for (int i = 0; i < apps.size(); i++) {
                    apps.get(i).setAppScale(1f);
                    apps.get(i).shake();
                }

                for (int i = 0; i < folders.size(); i++) {
                    folders.get(i).setAppScale(1f);
                    folders.get(i).shake();
                }

                AppView app = getAppView(adapter.getItem(position));

                hideKeyboard(MainActivity.this);
                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);

                ClipData data = ClipData.newPlainText(app.getAppPackage(), "");
                data.addItem(new ClipData.Item(app.getAppPackage()));
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        view);
                shadowBuilder.getView().setAlpha(1f);

                dragging = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    view.startDragAndDrop(data, shadowBuilder, app, 0);
                } else {
                    view.startDrag(data, shadowBuilder, app, 0);
                }


                return false;
            }
        });

        googleSearchButton = findViewById(R.id.googleSearch);
        youtubeSearchButton = findViewById(R.id.youtubeSearch);
        duckduckgoSearchButton = findViewById(R.id.duckduckgoSearch);

        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                searchBar.setCollapsed();
            }
        });

        searchBar.setOnSearchBarListener(new SearchBar.SearchBarListener() {
            @Override
            public void onTextChanged(String text) {

                if(!text.isEmpty()){
                    appGrid.setVisibility(View.GONE);
                    searchResultWrapper.setVisibility(View.VISIBLE);
                }else{
                    appGrid.setVisibility(View.VISIBLE);
                    searchResultWrapper.setVisibility(View.GONE);
                }

                ArrayList<DataObj> subList = new ArrayList<>();

                for (int i = 0; i < appsSearch.size(); i++) {
                    Log.i(appsSearch.get(i).name, appsSearch.get(i).name.indexOf(text) + "");
                    if (appsSearch.get(i).name.toLowerCase().indexOf(text.toLowerCase()) != -1) {
                        subList.add(appsSearch.get(i));
                    }
                }

                if (subList.size() == 0) {
                    appList.setVisibility(View.GONE);
                    appsListHeadline.setVisibility(View.GONE);
                } else {
                    appList.setVisibility(View.VISIBLE);
                    appsListHeadline.setVisibility(View.VISIBLE);
                }

                adapter = new SearchListAdapter(getApplicationContext(), subList);
                appList.setAdapter(adapter);
                appGrid.setAdapter(adapter);



                // contacts

                ArrayList<Contacts.ContactInfo> contactSublist = new ArrayList<>();
                if (contactSearch != null) {
                    for (int i = 0; i < contactSearch.size(); i++) {
                        Log.i(contactSearch.get(i).getName(), contactSearch.get(i).getName().indexOf(text) + "");
                        if (contactSearch.get(i).getName().toLowerCase().indexOf(text.toLowerCase()) != -1) {
                            contactSublist.add(contactSearch.get(i));
                        }
                    }

                    if (contactSublist.size() == 0) {
                        contactList.setVisibility(View.GONE);
                        contactListHeadline.setVisibility(View.GONE);
                    } else {
                        contactList.setVisibility(View.VISIBLE);
                        contactListHeadline.setVisibility(View.VISIBLE);
                    }

                    contactsAdapter = new ContactListAdapter(getApplicationContext(), contactSublist);
                    contactList.setAdapter(contactsAdapter);

                }

                setSearchButton(text);
            }

            @Override
            public void onButtonClicked() {
                if(slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.COLLAPSED||
                        slidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.HIDDEN) {
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
                    appGrid.setVisibility(View.VISIBLE);
                    searchResultWrapper.setVisibility(View.GONE);
                }else{
                    slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                    hideKeyboard(MainActivity.this);
                }
            }

            @Override
            public void onTextFocus(){
                return;

             }
        });



        slidingUpPanelLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
                if(slideOffset >= 0.5f){
                    if(slideOffset > 0.95f){
                        searchBar.setAlpha(1f);
                        dragPin.setAlpha(0f);
                    }else{
                        searchBar.setAlpha(((slideOffset-0.5f)/0.5f));
                    }

                }

                if(slideOffset <= 0.5f){
                    if(slideOffset < 0.01){
                        dragPin.setAlpha(1f);
                        searchBar.setAlpha(0f);
                    }else{
                        dragPin.setAlpha(1f-((slideOffset)/0.5f));
                    }

                }

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {
                Log.i("panel state"," prev state "+previousState.name()+ " new state "+newState.name());
                if(newState == SlidingUpPanelLayout.PanelState.COLLAPSED){
                    hideKeyboard(MainActivity.this);
                    hasdone = false;
                }else if (newState == SlidingUpPanelLayout.PanelState.EXPANDED){
                    searchBar.searchText.requestFocus();
                    openKeypad(getApplicationContext(),searchBar.searchText);
                }else if (newState == SlidingUpPanelLayout.PanelState.HIDDEN){
                    hideKeyboard(MainActivity.this);
                }
            }
        });

    }

    float slideOffsetOld = 0;
    boolean hasdone = false;

    private void setSearchButton(final String str){
        googleSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String escapedQuery = null;
                try {
                    escapedQuery = URLEncoder.encode(str, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.parse("http://www.google.com/#q=" + escapedQuery);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        youtubeSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String escapedQuery = null;
                try {
                    escapedQuery = URLEncoder.encode(str, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.parse("http://www.youtube.com/results?search_query=" + escapedQuery);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        duckduckgoSearchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String escapedQuery = null;
                try {
                    escapedQuery = URLEncoder.encode(str, "UTF-8");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                Uri uri = Uri.parse("http://duckduckgo.com/?q=" + escapedQuery);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
    }

    private void openApp(String package_string){

        if (!settingsMode) {
            notificationManager.removeNotifications(package_string);
            for(AppView app: apps){
                if(app.getAppPackage().equals(package_string)){
                    if(app.badge != null)
                        app.badge.setBadgeCount(0);
                }
            }

            Intent intent = getPackageManager().getLaunchIntentForPackage(package_string);

            if (intent != null) {
                Log.i("package to launch", package_string);
                startActivity(intent);//null pointer check in case package name was not found
            }else{
                removeAppFromLayout(package_string);
                removeAppFromHomeScreen(package_string,this);
                hideApp(package_string,this);
                Toast.makeText(this,"Removed unopenable app from homescreen",Toast.LENGTH_SHORT).show();
                Log.i("package to launch", "not found");
            }
        }else{
            AppView appView = getAppViewFromPackage(apps,package_string);
            setAppInEdit(appView);
        }
    }

    private void initNavBar(){
        View decorView = getWindow().getDecorView();
        decorView.setOnSystemUiVisibilityChangeListener
                (new View.OnSystemUiVisibilityChangeListener() {
                    @Override
                    public void onSystemUiVisibilityChange(int visibility) {
                        if ((visibility & View.SYSTEM_UI_FLAG_HIDE_NAVIGATION) == 0) {
                            // TODO: The navigation bar is visible. Make any desired
                            // adjustments to your UI, such as showing the action bar or
                            // other navigational controls.
                            //searchButton.setVisibility(View.INVISIBLE);
                        } else {
                            // TODO: The navigation bar is NOT visible. Make any desired
                            // adjustments to your UI, such as hiding the action bar or
                            // other navigational controls.

                            //searchButton.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private ArrayList<String> defaultProgramms(){
        ArrayList<String> defaultPackages = new ArrayList<>();

        // default browser
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        ResolveInfo resolveInfo = getPackageManager().resolveActivity(browserIntent,PackageManager.MATCH_DEFAULT_ONLY);
        defaultPackages.add(resolveInfo.activityInfo.packageName);

        // default caller
        defaultPackages.add(getDialerPackageName(getApplicationContext()));

        // camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ResolveInfo resolveInfoCam = getPackageManager().resolveActivity(cameraIntent,PackageManager.MATCH_DEFAULT_ONLY);
        defaultPackages.add(resolveInfoCam.activityInfo.packageName);

        // gallery
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(android.content.Intent.ACTION_VIEW);
        galleryIntent.setType("image/*");
        galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ResolveInfo resolveInfoGal = getPackageManager().resolveActivity(galleryIntent,PackageManager.MATCH_DEFAULT_ONLY);
        defaultPackages.add(resolveInfoGal.activityInfo.packageName);

        // music
        Intent musicIntent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC);
        ResolveInfo resolveInfoMusic = getPackageManager().resolveActivity(musicIntent,PackageManager.MATCH_DEFAULT_ONLY);
        defaultPackages.add(resolveInfoMusic.activityInfo.packageName);

        ArrayList<String> commonlyUsed = new ArrayList<>();

        commonlyUsed.add("com.android.vending");
        commonlyUsed.add("com.instagram.android");
        commonlyUsed.add("com.spotify.music");
        commonlyUsed.add("com.snapchat.android");
        commonlyUsed.add("com.google.android.youtube");
        commonlyUsed.add("com.whatsapp");
        commonlyUsed.add("com.android.vending");

        for(String app : commonlyUsed){
            if(defaultPackages.size() < 9){
                defaultPackages.add(app);
            }
        }

        return defaultPackages;
    }

    public void writePointOnStorage(String name, Point p){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt(name+"x", p.x);
        editor.putInt(name+"y", p.y);
        editor.commit();
    }

    public Point getPointFromStorage(String name){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        int x = sharedPref.getInt(name+"x", -1);
        int y = sharedPref.getInt(name+"y", -1);
        return new Point(x,y);
    }

    public void createLetterFolder(){
        ArrayList<ArrayList<AppView>> alphaGroupApps = getAlphabetGroups(apps);
        for(ArrayList<AppView> apps : alphaGroupApps){
            createFolderByAppView(apps,apps.get(0).appName.substring(0,1));
        }
    }

    private static ArrayList<ArrayList<AppView>> getAlphabetGroups(ArrayList<AppView> apps){
        ArrayList<ArrayList<AppView>> alphabetGroups = new ArrayList<>();

        char alphabet[] = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

        for(char letter : alphabet){
            ArrayList<AppView> singleLetterApps = new ArrayList<>();
            for(int i = 0; i < apps.size(); i++){
                if(apps.get(i).getAppName().toLowerCase().charAt(0) == letter){
                    Log.i(letter+" ",apps.get(i).getAppName());
                    singleLetterApps.add(apps.get(i));
                }
            }

            if(singleLetterApps.size() > 0){
                alphabetGroups.add(singleLetterApps);
            }
        }


        for(int i = 0; i < apps.size(); i++){
            ArrayList<AppView> singleLetterApps = new ArrayList<>();
            for(char letter : alphabet){
                if(apps.get(i).getAppName().toLowerCase().charAt(0) != letter){
                    singleLetterApps.add(apps.get(i));
                }
            }

        }

        return alphabetGroups;
    }

    public void printfApplist(ArrayList<AppView> apps,String befo){
        for(AppView app : apps){
            Log.i("printli"+befo,app.getAppPackage());
        }
    }

    public void createFolderByAppView(ArrayList<AppView> addApps,String name){

        if(addApps.size() > 1){
            Point p = AppManager.getFreePositionAll(apps,folders);
            FolderManager.createFolder(name,addApps.get(0).getAppPackage(),addApps.get(1).getAppPackage(),p.x,p.y,MainActivity.this);

            final FolderView newFolder = FolderManager.getNewFolderView(
                    getApplicationContext(),
                    name,
                    p,
                    new ArrayList<>(Arrays.asList(new String[]{
                            addApps.get(0).getAppPackage(),
                            addApps.get(1).getAppPackage()
                    }))
            );


            newFolder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFolder(newFolder);
                }
            });

            Log.i("App apps","before");
            printfApplist(addApps,"before");


            removeAppFromLayout(addApps.get(0));
            removeAppFromLayout(addApps.get(1));

            Log.i("layout", "add new folder");
            layout.addView(newFolder);

            folders.add(newFolder);

            addApps.remove(0);
            addApps.remove(0);

            Log.i("App apps","after");
            printfApplist(addApps,"after");

            int w = 0;

            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

            }

            for(AppView app : addApps){
                removeAppFromLayout(app);
                FolderManager.addAppToFolder(name,app.getAppPackage(),this);

                ArrayList<String> newlist = newFolder.getAppsContained();
                newlist.add(app.getAppPackage());
                newFolder.setImageBitmap(FolderManager.getHexFolderIcon(newlist, asize,asize -w, getApplicationContext(),newFolder.getFolderName(),false));
            }
        }
    }

    public void addAppsToFolder(AppView app, String name){

        int w = 0;

        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("app_icon_style","circle").equals("hexagon") ||
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString("app_icon_style","circle").equals("hexagon-outline")){
            w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

        }

        for(FolderView newFolder : folders){
            if(newFolder.folderName.equals(name)){
                removeAppFromLayout(app);
                FolderManager.addAppToFolder(name,app.getAppPackage(),this);

                ArrayList<String> newlist = newFolder.getAppsContained();
                newlist.add(app.getAppPackage());
                newFolder.setImageBitmap(FolderManager.getHexFolderIcon(newlist, asize,asize-w, getApplicationContext(),newFolder.getFolderName(),false));
            }
        }

    }

    public void removeAppFromFolder(String folder, String app){
        FolderView currentFolderView = null;

        for(FolderView f : folders){
            if(f.folderName.equals(folder)){
                currentFolderView = f;
            }
        }

        if(currentFolderView != null){

            AppView currentApp = appManager.getAppViewByPackage(
                    app,
                    getApplicationContext(),asize);

            final String package_string = currentApp.getAppPackage();

            currentApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApp(package_string);
                }
            });

            currentApp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    AppView appView = getAppViewFromPackage(apps,package_string);
                    setAppInEdit(appView);
                    appView.setVisibility(View.INVISIBLE);

                    // create drag object

                    ClipData data = ClipData.newPlainText(appView.getAppPackage(), "");
                    data.addItem(new ClipData.Item(appView.getAppPackage()));
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            appView);
                    shadowBuilder.getView().setAlpha(1f);

                    dragging = true;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        appView.startDragAndDrop(data, shadowBuilder, appView, 0);
                    } else {
                        appView.startDrag(data, shadowBuilder,appView, 0);
                    }

                    if(!settingsMode)
                        toggleSettingsMode();

                    return true;
                }
            });

            // remove app from old folder

            removeFolderAttrFromApp(getApplicationContext(), currentApp.getAppPackage());
            FolderManager.removeAppFromFolder(currentFolderView.getFolderName(), currentApp.getAppPackage(), getApplicationContext());
            currentFolderView.appsContained.remove(currentApp.getAppPackage());

            apps.add(currentApp);
            appsInfo.add(AppManager.getDataObjFromPackage(currentApp.getAppPackage(), getApplicationContext(), currentFolderView.getPosition()));
            layout.addView(currentApp);
            setAppPosition(currentApp,AppManager.getFreePosition(getApplicationContext()));


            int w = 0;

            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

            }

            // set new image for folder

            currentFolderView.setImageBitmap(
                    FolderManager.getHexFolderIcon(
                            getAppsInFolderAppPackage(
                                    currentFolderView.getFolderName(),
                                    getApplicationContext()),
                            asize,
                            asize-w,
                            getApplicationContext(),
                            currentFolderView.getFolderName(),
                            false
                    )
            );

            // check if there is only one item left in the folder

            if (currentFolderView.getAppsContained().size() == 1) {

                // get last appview in folder

                AppView lastApp = appManager.getAppViewByPackage(
                        currentFolderView.getAppsContained().get(0),
                        getApplicationContext(),asize);

                final String package_string_current = lastApp.getAppPackage();

                lastApp.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        openApp(package_string);
                    }
                });

                lastApp.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {

                        AppView appView = getAppViewFromPackage(apps,package_string_current);
                        setAppInEdit(appView);
                        appView.setVisibility(View.INVISIBLE);

                        // create drag object

                        ClipData data = ClipData.newPlainText(appView.getAppPackage(), "");
                        data.addItem(new ClipData.Item(appView.getAppPackage()));
                        View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                                appView);
                        shadowBuilder.getView().setAlpha(1f);

                        dragging = true;

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            appView.startDragAndDrop(data, shadowBuilder, appView, 0);
                        } else {
                            appView.startDrag(data, shadowBuilder,appView, 0);
                        }

                        if(!settingsMode)
                            toggleSettingsMode();

                        return true;
                    }
                });

                // add app to layout

                setAppPosition(lastApp, currentFolderView.getPosition());

                lastApp.setPosition(currentFolderView.getPosition(),getApplicationContext());
                apps.add(lastApp);
                lastApp.shake();
                appsInfo.add(AppManager.getDataObjFromPackage(lastApp.getAppPackage(), getApplicationContext(), currentFolderView.getPosition()));
                layout.addView(lastApp);
                unsetAppInEdit();
                deleteButton.hide();

                // remove folder from app

                removeFolderAttrFromApp(getApplicationContext(), lastApp.getAppPackage());

                // remove the folder

                FolderManager.removeAppFromFolder(currentFolderView.getFolderName(), lastApp.getAppPackage(), getApplicationContext());
                deleteFolder(currentFolderView.getFolderName(), getApplicationContext());

                // remove folder from layout

                folderOverlay.removeAllViews();
                currentFolderView.stopShake();
                layout.removeView(currentFolderView);
            }
        }


    }

    public void removeAllAppsFromFolders(){
        for(int m = 0; m < folders.size(); m++){
            for(int i = 0; i < folders.get(m).appsContained.size()-1; i++){
                removeAppFromFolder(folders.get(m).folderName,folders.get(m).appsContained.get(i));
            }
        }
        folders.clear();
    }

    public void createFolderCategories(){

        ArrayList<AppView> allApps = new ArrayList<>();
        allApps.addAll(getNotContainingSublist(apps,defaultProgramms()));

        Log.i("Create folder"," folder function"+ apps.size());

        ArrayList<AppView> addApps = new ArrayList<>();

        for(AppView app : allApps){
            Log.i("app packages",app.getAppPackage());
            if(app.category != null){
                if(app.category.equals("Art & Design") ||
                        app.category.equals("Comics") ||
                        app.category.equals("Education") ||
                        app.category.equals("News & Magazines") ||
                        app.category.equals("Books & Reference")){
                    Log.i("Media",app.getAppPackage());
                    addApps.add(app);
                }
            }
        }

        for(AppView app : addApps){
            allApps.remove(app);
        }

        createFolderByAppView(addApps,"Media");
        addApps.clear();

        for(AppView app : allApps){
            Log.i("app packages",app.getAppPackage());
            if(app.category != null){
                if(app.category.equals("Education")){
                    Log.i("Education",app.getAppPackage());
                    addApps.add(app);
                }
            }
        }

        for(AppView app : addApps){
            allApps.remove(app);
        }

        createFolderByAppView(addApps,"Education");
        addApps.clear();

        for(AppView app : allApps){
            Log.i("app packages",app.getAppPackage());
            if(app.category != null){
                if(app.category.equals("Productivity") ||
                        app.category.equals("Lifestyle") ||
                        app.category.equals("Finance") ||
                        app.category.equals("Dating") ||
                        app.category.equals("Health & Fitness") ||
                        app.category.equals("Beauty")){
                    Log.i("Lifestyle",app.getAppPackage());
                    addApps.add(app);
                }
            }
        }


        createFolderByAppView(addApps,"Lifestyle");
        addApps.clear();

        for(AppView app : allApps){
            Log.i("app packages",app.getAppPackage());
            if(app.category != null){
                if(app.category.equals("Communication") ||
                        app.category.equals("Business")){
                    Log.i("Communication",app.getAppPackage());
                    addApps.add(app);
                }
            }
        }


        createFolderByAppView(addApps,"Communication");
        addApps.clear();

        for(AppView app : allApps){
            Log.i("app packages",app.getAppPackage());
            if(app.category != null){
                if(app.category.equals("Arcade") ||
                    app.category.equals("Cards") ||
                    app.category.equals("Casual") ||
                    app.category.equals("Racing") ||
                    app.category.equals("Sport Games") ||
                    app.category.equals("Action") ||
                    app.category.equals("Adventure") ||
                    app.category.equals("Casino") ||
                    app.category.equals("Board") ||
                    app.category.equals("Educational") ||
                    app.category.equals("Music Games") ||
                    app.category.equals("Role Playing") ||
                    app.category.equals("Simulation") ||
                    app.category.equals("Strategy") ||
                    app.category.equals("Trivia") ||
                    app.category.equals("Word Games") ||
                    app.category.equals("Puzzle")){
                    Log.i("Communication",app.getAppPackage());
                    addApps.add(app);
                }
            }
        }


        createFolderByAppView(addApps,"Games");
        addApps.clear();

        for(AppView app : allApps){
            Log.i("app packages",app.getAppPackage());
            if(app.category != null){
                if(app.category.equals("Entertainment") ||  app.category.equals("Shopping")){
                    Log.i("Entertainment",app.getAppPackage());
                    addApps.add(app);
                }
            }
        }


        createFolderByAppView(addApps,"Entertainment");
        addApps.clear();

        for(AppView app : allApps){
            Log.i("app packages",app.getAppPackage());
            if(app.category != null){
                if(app.category.equals("Maps & Navigation") ||
                        app.category.equals("Personalization") ||
                        app.category.equals("Tools") ||
                        app.category.equals("OTHERS") ||
                        app.category.equals("OTHER") ||
                        app.category.equals("Maps & Navigation") ||
                        app.category == null ||
                        app.category.equals("Weather")){
                    Log.i("Tools",app.getAppPackage());
                    addApps.add(app);
                }
            }
        }

        createFolderByAppView(addApps,"Tools");
        addApps.clear();

        for(AppView app : allApps){
            if(app.getAppPackage().startsWith("com.google") ){
                Log.i("google app",app.getAppPackage());
                addApps.add(app);
            }
        }



        createFolderByAppView(addApps,"google");
        addApps.clear();

        for(AppView app : allApps){
            if(app.getAppPackage().startsWith("com.android")){
                Log.i("android app",app.getAppPackage());
                addApps.add(app);
            }
        }

        createFolderByAppView(addApps,"System");
        addApps.clear();
    }

    public void createCommunicationFolder(){
        String folderName = "Communication";
        ArrayList<AppView> addApps = new ArrayList<>();
        ArrayList<String> comPermissions = new ArrayList<>();
        comPermissions.add("android.permission.READ_PROFILE");
        comPermissions.add("android.permission.READ_CONTACTS");
        comPermissions.add("android.permission.MANAGE_ACCOUNTS");
        for(AppView app : apps){
            Log.i("app packages",app.getAppPackage());
            if(arePermissionsInApp(app.getAppPackage(),comPermissions)){
                addApps.add(app);
            }
        }

        createFolderByAppView(addApps,folderName);
    }

    public void createOtherFolder(){
        String folderName = "other";
        ArrayList<AppView> addApps = new ArrayList<>();
        ArrayList<String> comPermissions = new ArrayList<>();
        comPermissions.add("android.permission.CHANGE_WIFI_STATE");
        for(AppView app : apps){
            Log.i("app packages",app.getAppPackage());
            if(arePermissionsInApp(app.getAppPackage(),comPermissions)){
                addApps.add(app);
            }
        }

        createFolderByAppView(addApps,folderName);
    }

    public boolean arePermissionsInApp(String appPackage, ArrayList<String> reqPermissions){
        ArrayList<String> appPermissions = getPermissionForPackage(appPackage);
        for (String reqPermission : reqPermissions){
            if(!isOnePermissionInList(appPermissions,reqPermission)){
                return false;
            }
        }
        return true;
    }

    private boolean isOnePermissionInList(ArrayList<String> appPermissions, String reqPermissions){
        for(String appPermission : appPermissions){
            if(appPermission.equals(reqPermissions)){
                return true;
            }
        }

        return false;
    }

    public void initUpdateButton(){
        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);
                progressDialog.setMessage("Search for new apps");
                progressDialog.show();
                ArrayList<DataObj> appsFromDevice = AppManager.getAppsFromDevice1(getApplicationContext());

                for(DataObj apps : appsFromDevice){
                    Log.i("is",apps.package_name +" in db? "+ isAppInDB(getApplicationContext(),apps.package_name));
                    if(!isAppInDB(getApplicationContext(),apps.package_name)){
                        installedApp(apps.package_name);
                    }
                }

                progressDialog.dismiss();
            }
        });
    }

    HorizontalScrollView settingsScrollView;

    private void initSettingsScrollView(){
        settingsScrollView = findViewById(R.id.settings_scrollview);
        /*
        settingsScrollView.setOnTouchListener(new View.OnTouchListener() {
            private ViewTreeObserver observer;

            float curX, curY;

            MotionEvent.PointerCoords coords1 = new MotionEvent.PointerCoords();
            MotionEvent.PointerCoords coords2 = new MotionEvent.PointerCoords();

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                    case MotionEvent.ACTION_DOWN:
                        event.getPointerCoords(0, coords1);

                        if (event.getPointerCount() == 2) {
                            scroll = false;
                            event.getPointerCoords(1, coords2);
                            dist = spaceBetweentPoints(coords1, coords2);
                        }

                        if (scroll) {

                            movedx = event.getX();
                            movedy = event.getY();
                            mx = event.getX();
                            my = event.getY();
                        }
                        break;
                    case MotionEvent.ACTION_MOVE:

                        if (scroll) {

                            //scrolling stuff
                            curX = event.getX();
                            curY = event.getY();
                            settingsScrollView.scrollBy((int) (mx - curX), (int) (my - curY));
                            mx = curX;
                            my = curY;
                        }

                        scaleSettingsButtons();
                        break;
                }
                return false;
            }
        });*/
    }

    ArrayList<FloatingActionButton> settingsButtonList;

    public void initFloatingActionButtonList(){
        settingsButtonList = new ArrayList<>();
        settingsButtonList.add(settingsButton);
        settingsButtonList.add(anchorButton);
        settingsButtonList.add(deleteButton);
        if(!IS_PREMIUM){
            settingsButtonList.add(premiumButton);
        }
        settingsButtonList.add(visibilityButton);
        settingsButtonList.add(removeFromHomeScreen);
        settingsButtonList.add(add_widget_button);
    }

    public static String X_START_KEY = "x_pos_start_key";
    public static String Y_START_KEY = "y_post_start_key";

    private void initStartPosition(){
        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final int y = preferences.getInt(Y_START_KEY,asize * 2);
        final int x = preferences.getInt(X_START_KEY,asize * 2);

        Log.i(Y_START_KEY,x+ " x "+y+" y ");

        scroller.post(new Runnable() {
            public void run() {
                Log.i(Y_START_KEY,y+" y ");
                scroller.scrollTo(x, y);
            }
        });

    }

    private void initAnchorButton(){
        anchorButton = findViewById(R.id.anchor_button);
        anchorButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int x = scroller.getScrollX();
                int y = scroller.getScrollY();

                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt(X_START_KEY,x);
                editor.putInt(Y_START_KEY,y);
                editor.commit();
                Toast.makeText(getApplicationContext(),"Position saved",Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initPremiumButton(){
        premiumButton = findViewById(R.id.premium_button);
        premiumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,BuyingActivity.class));
            }
        });
    }

    private void initSettingsButton() {
        settingsButton = findViewById(R.id.settings_button);
        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSettings();
            }
        });
        settingsButton.hide();
    }

    private void initVisibilityButton(){
        visibilityButton = findViewById(R.id.visibility_button);
        visibilityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.hideApp(inEditModeAppPackage,getApplicationContext());
                removeAppFromLayout(inEditModeAppPackage);
            }
        });
    }

    private void initRemoveFromHomeScreen(){
        removeFromHomeScreen = findViewById(R.id.remove_homescreen);
        removeFromHomeScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppManager.removeAppFromHomeScreen(inEditModeAppPackage,getApplicationContext());
                removeAppFromLayout(inEditModeAppPackage);
            }
        });
    }

    private TextView folderLabel;

    private void initFolderLabel() {
        folderLabel = findViewById(R.id.folder_label);
    }


    private void setFolderPosition(FolderView folder, Point newPosition) {
        try {
            folder.setPosition(newPosition,getApplicationContext());

            if (!settingsMode) {
                Log.i("call checkScale","from: "+"setfolderposition");
                checkScale();
            }
        } finally {
            layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    layout.removeOnLayoutChangeListener(this);
                    if (!settingsMode) {
                        Log.i("call checkScale","from: "+"addonlayoutchangelistener");
                        checkScale();
                    }
                }
            });
        }
    }

    public void initDesignButton() {
        designButton = findViewById(R.id.design_button);
        designButton.hide();
        designButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (FolderView folder : folders) {
                    if (folder.getFolderName().equals(inEditModeAppPackage)) {
                        editFolderName(folder);
                    }
                }
            }
        });
    }

    public void initDeleteButton() {
        deleteButton = findViewById(R.id.delete_button);
        deleteButton.hide();
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(View view : editableViews){
                    if(String.valueOf(view.getTag()).equals(String.valueOf(inEditModeAppPackage))){
                        removeWidget(getViewFromTag(inEditModeAppPackage));
                        return;
                    }
                }

                Log.i("uninstall",inEditModeAppPackage);
                Uri packageUri = Uri.parse("package:" + inEditModeAppPackage);
                Intent uninstallIntent =
                        new Intent(Intent.ACTION_DELETE, packageUri);
                startActivity(uninstallIntent);

            }
        });
    }

    public void removeWidget(View hostView) {
        _appWidgetHost.deleteAppWidgetId(Integer.parseInt(String.valueOf(hostView.getTag())));
        widgetManager.removeWidgetInfo(String.valueOf(hostView.getTag()));
        layout.removeView(hostView);
        editableViews.remove(hostView);
        widgetSizeButton.hide();
    }

    private FolderView getCurentFolderView() {
        Log.i("currentFolder",currentFolder);
        FolderView folderView = null;
        for (int i = 0; i < folders.size(); i++) {
            if (folders.get(i).getFolderName().equals(currentFolder)) {
                folderView = folders.get(i);
            }
        }

        return folderView;
    }

    private boolean isDragObjectClock(ClipDescription cl){
        if(cl.getLabel().equals("___clock"))
            return true;
        else
            return false;
    }

    private boolean isViewIntersectingOtherView(DragEvent event){
        View v = getViewFromTag(inEditModeAppPackage);
        Point dropPoint = new Point((int)event.getX(),(int)event.getY());
        dropPoint.y -= v.getHeight()/2;
        dropPoint.x -= v.getWidth()/2;
        Point rasterPoint = Util.rasterToPixel(Util.pixelToRaster(dropPoint, asize, padding,getApplicationContext()),asize,padding,getApplicationContext());
        Log.i("____clock drop",rasterPoint.toString()+" ");
        Rect clockRect = new Rect(
                rasterPoint.x,
                rasterPoint.y,
                rasterPoint.x+v.getWidth(),
                rasterPoint.y+v.getHeight());
        Log.i("____clockrect",clockRect.toString());

        for(AppView app : apps){
            if((new Rect(
                    app.getLeft(),
                    app.getTop(),
                    app.getRight(),
                    app.getBottom()-padding
            )).intersect(clockRect)){
                Log.i("____clock inter","app "+app.appName);
                return true;
            }
        }

        for(FolderView folder : folders){
            if((new Rect(
                    folder.getLeft(),
                    folder.getTop(),
                    folder.getRight(),
                    folder.getBottom()-padding
            )).intersect(clockRect)){
                Log.i("____clock inter","app "+folder.folderName);
                return true;
            }
        }
        return false;
    };


    private void handleDragFromFolderToAnotherApp(final FolderView currentFolderView,final Point rasterPoint,final AppView currentApp){
        // ask for the name of the folder

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom));
        builder.setTitle("Choose the folder name");

        final EditText input = new EditText(MainActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String t = input.getText().toString().toLowerCase();
                // check if the folder name is valid

                if (FolderManager.isNameValid(t, folders) &&
                        !t.equals("") &&
                        t != null) {

                    hideKeyboard(MainActivity.this);

                    // create new folderview

                    FolderView newFolder = FolderManager.getNewFolderView(
                            getApplicationContext(),
                            t.toString(),
                            rasterPoint,
                            new ArrayList<>(Arrays.asList(new String[]{
                                    getAppIntersection(apps, rasterPoint),
                                    currentApp.getAppPackage()
                            }))
                    );

                    folders.add(newFolder);
                    newFolder.shake();
                    layout.addView(newFolder);

                    // ceate new folder in db

                    FolderManager.createFolder(
                            t,
                            getAppIntersection(apps, rasterPoint),
                            currentApp.getAppPackage(),
                            rasterPoint.x,
                            rasterPoint.y,
                            MainActivity.this);

                    // remove intersecting app

                    removeAppFromLayout(getAppViewIntersection(apps, rasterPoint));

                    // remove app from old folder

                    removeFolderAttrFromApp(getApplicationContext(), currentApp.getAppPackage());
                    FolderManager.removeAppFromFolder(currentFolderView.getFolderName(), currentApp.getAppPackage(), getApplicationContext());
                    currentFolderView.appsContained.remove(currentApp.getAppPackage());

                    int w = 0;

                    if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("app_icon_style","circle").equals("hexagon") ||
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .getString("app_icon_style","circle").equals("hexagon-outline")){
                        w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

                    }

                    // set new image for folder

                    currentFolderView.setImageBitmap(
                            FolderManager.getHexFolderIcon(
                                    getAppsInFolderAppPackage(
                                            currentFolderView.getFolderName(),
                                            getApplicationContext()),
                                    asize,asize-w,
                                    getApplicationContext(),
                                    currentFolderView.getFolderName(),
                                    false
                            )
                    );

                    // check if there is only one item left in the folder

                    if (currentFolderView.getAppsContained().size() == 1) {

                        // get last appview in folder

                        AppView lastApp = appManager.getAppViewByPackage(
                                currentFolderView.getAppsContained().get(0),
                                getApplicationContext(),asize);

                        // add app to layout

                        setAppPosition(lastApp, currentFolderView.getPosition());

                        lastApp.setPosition(currentFolderView.getPosition(),getApplicationContext());
                        apps.add(lastApp);
                        lastApp.shake();
                        appsInfo.add(AppManager.getDataObjFromPackage(lastApp.getAppPackage(), getApplicationContext(), currentFolderView.getPosition()));
                        layout.addView(lastApp);
                        unsetAppInEdit();
                        deleteButton.hide();

                        // remove folder from app

                        removeFolderAttrFromApp(getApplicationContext(), lastApp.getAppPackage());

                        // remove the folder

                        FolderManager.removeAppFromFolder(currentFolderView.getFolderName(), lastApp.getAppPackage(), getApplicationContext());
                        deleteFolder(currentFolderView.getFolderName(), getApplicationContext());

                        // remove folder from layout

                        folderOverlay.removeAllViews();
                        currentFolderView.stopShake();
                        layout.removeView(currentFolderView);
                        folders.remove(currentFolderView);
                    }

                    // deliver toast message it the input is not valid

                } else {
                    if (t == null || t.equals("")) {
                        Toast.makeText(getApplicationContext(), "Choose a name", Toast.LENGTH_SHORT).show();
                    } else if (FolderManager.doesFolderExist(t, folders)) {
                        Toast.makeText(getApplicationContext(), "The name does already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        // app is intersecting folder

    }

    private void handleDragFromFolderToAnotherFolder(FolderView currentFolderView, Point rasterPoint, AppView currentApp){
        // check if the folder is itself

        if (getfolderIntersection(folders, rasterPoint).equals(currentFolderView.getFolderName())) {

            int w = 0;

            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

            }

            // restore the old folder icon

            currentFolderView.setImageBitmap(
                    FolderManager.getHexFolderIcon(
                            getAppsInFolderAppPackage(
                                    currentFolderView.getFolderName(),
                                    getApplicationContext()),
                            asize,asize-w,
                            getApplicationContext(),
                            currentFolderView.getFolderName(),
                            false
                    )
            );

            // the folder is not itself

        } else {

            // remove app from old folder

            removeFolderAttrFromApp(getApplicationContext(), currentApp.getAppPackage());
            FolderManager.removeAppFromFolder(currentFolderView.getFolderName(), currentApp.getAppPackage(), getApplicationContext());

            currentFolderView.appsContained.remove(currentApp.getAppPackage());

            int w = 0;

            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

            }

            // set new image for folder

            currentFolderView.setImageBitmap(
                    FolderManager.getHexFolderIcon(
                            getAppsInFolderAppPackage(
                                    currentFolderView.getFolderName(),
                                    getApplicationContext()),
                            asize,asize-w,
                            getApplicationContext(),
                            currentFolderView.getFolderName(),
                            false
                    )
            );

            // check if there is only one item left in the folder

            if (currentFolderView.getAppsContained().size() == 1) {

                // get last appview in folder

                AppView lastApp = appManager.getAppViewByPackage(
                        currentFolderView.getAppsContained().get(0),
                        getApplicationContext(),asize);

                // add app to layout

                setAppPosition(lastApp, currentFolderView.getPosition());

                lastApp.setPosition(currentFolderView.getPosition(),getApplicationContext());
                apps.add(lastApp);
                lastApp.shake();
                appsInfo.add(AppManager.getDataObjFromPackage(lastApp.getAppPackage(), getApplicationContext(), currentFolderView.getPosition()));
                layout.addView(lastApp);
                unsetAppInEdit();
                deleteButton.hide();

                // remove folder from app

                removeFolderAttrFromApp(getApplicationContext(), lastApp.getAppPackage());

                // remove the folder

                FolderManager.removeAppFromFolder(currentFolderView.getFolderName(), lastApp.getAppPackage(), getApplicationContext());

                deleteFolder(currentFolderView.getFolderName(), getApplicationContext());

                // remove folder from layout

                folderOverlay.removeAllViews();
                currentFolderView.stopShake();
                layout.removeView(currentFolderView);
                folders.remove(currentFolderView);
            }

            // get intersecting folder

            for (FolderView intersectingFolder : folders) {
                if (getfolderIntersection(folders, rasterPoint).equals(intersectingFolder.folderName)) {

                    unsetFolderInEdit();
                    unsetAppInEdit();

                    // add the app to the other older

                    FolderManager.addAppToFolder(intersectingFolder.getFolderName(), currentApp.getAppPackage(), getApplicationContext());

                    // set new image for folder

                    intersectingFolder.setImageBitmap(
                            FolderManager.getHexFolderIcon(
                                    getAppsInFolderAppPackage(
                                            intersectingFolder.getFolderName(),
                                            getApplicationContext()),
                                    asize,asize-w,
                                    getApplicationContext(),
                                    intersectingFolder.getFolderName(),
                                    false
                            )
                    );
                }
            }
        }
    }

    private void removeAppFromFolder(AppView app){
        for(FolderView folderView : folders){
            for(DataObj dataObj : getAppsInFolder(folderView.getFolderName(),getApplicationContext())){
                if(dataObj.package_name.equals(app.getAppPackage())){
                    // set new image for folder

                    removeAppFromFolder(folderView.folderName,dataObj.package_name);

                    int w = getWidth();

                    folderView.setImageBitmap(
                            FolderManager.getHexFolderIcon(
                                    getAppsInFolderAppPackage(
                                            folderView.getFolderName(),
                                            getApplicationContext()),
                                    asize,asize-w,
                                    getApplicationContext(),
                                    folderView.getFolderName(),
                                    false
                            )
                    );

                    if (folderView.getAppsContained().size() == 1) {

                        // get last appview in folder

                        AppView lastApp = appManager.getAppViewByPackage(
                                folderView.getAppsContained().get(0),
                                getApplicationContext(),asize);

                        // add app to layout

                        setAppPosition(lastApp, folderView.getPosition());

                        lastApp.setPosition(folderView.getPosition(),getApplicationContext());
                        apps.add(lastApp);
                        lastApp.shake();
                        appsInfo.add(AppManager.getDataObjFromPackage(lastApp.getAppPackage(), getApplicationContext(), folderView
                                .getPosition()));
                        layout.addView(lastApp);
                        unsetAppInEdit();
                        deleteButton.hide();

                        // remove folder from app

                        removeFolderAttrFromApp(getApplicationContext(), lastApp.getAppPackage());

                        // remove the folder

                        FolderManager.removeAppFromFolder(folderView.getFolderName(), lastApp.getAppPackage(), getApplicationContext());

                        deleteFolder(folderView.getFolderName(), getApplicationContext());

                        // remove folder from layout

                        folderOverlay.removeAllViews();
                        folderView.stopShake();
                        layout.removeView(folderView);
                        folders.remove(folderView);
                    }
                }
            }
        }
    }

    private void handleRemoveAppFromFolder(final AppView currentApp, FolderView currentFolderView, Point rasterPoint){

        removeFolderAttrFromApp(getApplicationContext(), currentApp.getAppPackage());
        FolderManager.removeAppFromFolder(currentFolderView.getFolderName(), currentApp.getAppPackage(), getApplicationContext());

        currentFolderView.appsContained.remove(currentApp.getAppPackage());

        int w = getWidth();

        // set new image for folder

        currentFolderView.setImageBitmap(
                FolderManager.getHexFolderIcon(
                        getAppsInFolderAppPackage(
                                currentFolderView.getFolderName(),
                                getApplicationContext()),
                        asize,asize-w,
                        getApplicationContext(),
                        currentFolderView.getFolderName(),
                        false
                )
        );

        // add app to layout

        setAppPosition(currentApp, rasterPoint);

        currentApp.setPosition(rasterPoint,getApplicationContext());
        apps.add(currentApp);
        final String pk = currentApp.getAppPackage();
        currentApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp(pk);
            }
        });
        currentApp.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                setAppInEdit(currentApp);
                currentApp.setVisibility(View.INVISIBLE);

                // create drag object

                ClipData data = ClipData.newPlainText(currentApp.getAppPackage(), "");
                data.addItem(new ClipData.Item(currentApp.getAppPackage()));
                View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                        currentApp);
                shadowBuilder.getView().setAlpha(1f);

                dragging = true;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    currentApp.startDragAndDrop(data, shadowBuilder, currentApp, 0);
                } else {
                    currentApp.startDrag(data, shadowBuilder,currentApp, 0);
                }

                if(!settingsMode)
                    toggleSettingsMode();

                return true;
            }
        });

        currentApp.shake();
        appsInfo.add(AppManager.getDataObjFromPackage(currentApp.getAppPackage(), getApplicationContext(), rasterPoint));
        layout.addView(currentApp);
        unsetAppInEdit();
        deleteButton.hide();

        // check if there is only one item left in the folder

        if (currentFolderView.getAppsContained().size() == 1) {

            // get last appview in folder

            AppView lastApp = appManager.getAppViewByPackage(
                    currentFolderView.getAppsContained().get(0),
                    getApplicationContext(),asize);

            // add app to layout

            setAppPosition(lastApp, currentFolderView.getPosition());

            lastApp.setPosition(currentFolderView.getPosition(),getApplicationContext());
            apps.add(lastApp);
            final String pk1 = lastApp.getAppPackage();
            lastApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApp(pk1);
                }
            });
            lastApp.shake();
            appsInfo.add(AppManager.getDataObjFromPackage(lastApp.getAppPackage(), getApplicationContext(), currentFolderView
                    .getPosition()));
            layout.addView(lastApp);
            unsetAppInEdit();
            deleteButton.hide();
            // remove folder from app

            removeFolderAttrFromApp(getApplicationContext(), lastApp.getAppPackage());

            // remove the folder

            FolderManager.removeAppFromFolder(currentFolderView.getFolderName(), lastApp.getAppPackage(), getApplicationContext());

            deleteFolder(currentFolderView.getFolderName(), getApplicationContext());

            // remove folder from layout

            folderOverlay.removeAllViews();
            currentFolderView.stopShake();
            layout.removeView(currentFolderView);
            folders.remove(currentFolderView);
        }
    }

    private void handleDragFromNotVisibleToAnotherApp(final Point rasterPoint,final AppView currentApp){
        // ask for the name of the folder

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom));
        builder.setTitle("Choose the folder name");

        final EditText input = new EditText(MainActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String t = input.getText().toString().toLowerCase();

                // check if the folder name is valid

                if (FolderManager.isNameValid(t, folders) &&
                        !t.equals("") &&
                        t != null) {

                    hideKeyboard(MainActivity.this);

                    // create new folderview

                    FolderView newFolder = FolderManager.getNewFolderView(
                            getApplicationContext(),
                            t,
                            rasterPoint,
                            new ArrayList<>(Arrays.asList(new String[]{
                                    getAppIntersection(apps, rasterPoint),
                                    currentApp.getAppPackage()
                            }))
                    );

                    folders.add(newFolder);
                    newFolder.shake();
                    layout.addView(newFolder);

                    // ceate new folder in db

                    FolderManager.createFolder(
                            t,
                            getAppIntersection(apps, rasterPoint),
                            currentApp.getAppPackage(),
                            rasterPoint.x,
                            rasterPoint.y,
                            MainActivity.this);

                    removeAppFromLayout(getAppIntersection(apps,rasterPoint));

                    AppManager.addAppToHomeScreen(currentApp.getAppPackage(),getApplicationContext());

                    // deliver toast message it the input is not valid

                } else {
                    if (t == null || t.equals("")) {
                        Toast.makeText(getApplicationContext(), "Choose a name", Toast.LENGTH_SHORT).show();
                    } else if (FolderManager.doesFolderExist(t, folders)) {
                        Toast.makeText(getApplicationContext(), "The name does already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

        // app is intersecting folder

    }

    private void handleDragFromNotVisibleToAnotherFolder( Point rasterPoint, AppView currentApp){
        // remove app from old folder

        AppManager.addAppToHomeScreen(currentApp.getAppPackage(),getApplicationContext());

        int w = 0;

        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("app_icon_style","circle").equals("hexagon") ||
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString("app_icon_style","circle").equals("hexagon-outline")){
            w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

        }

        // get intersecting folder

        for (FolderView intersectingFolder : folders) {
            if (getfolderIntersection(folders, rasterPoint).equals(intersectingFolder.folderName)) {

                unsetFolderInEdit();
                unsetAppInEdit();

                // add the app to the other older

                FolderManager.addAppToFolder(intersectingFolder.getFolderName(), currentApp.getAppPackage(), getApplicationContext());

                // set new image for folder

                intersectingFolder.setImageBitmap(
                        FolderManager.getHexFolderIcon(
                                getAppsInFolderAppPackage(
                                        intersectingFolder.getFolderName(),
                                        getApplicationContext()),
                                asize,asize-w,
                                getApplicationContext(),
                                intersectingFolder.getFolderName(),
                                false
                        )
                );
            }
        }


    }

    private void handleRemoveAppFromNotVisible(AppView currentApp, Point rasterPoint){
        AppManager.addAppToHomeScreen(currentApp.getAppPackage(),getApplicationContext());

        // add app to layout

        setAppPosition(currentApp, rasterPoint);

        currentApp.setPosition(rasterPoint,getApplicationContext());
        apps.add(currentApp);
        final String pk = currentApp.getAppPackage();
        currentApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openApp(pk);
            }
        });
        currentApp.shake();
        appsInfo.add(AppManager.getDataObjFromPackage(currentApp.getAppPackage(), getApplicationContext(), rasterPoint));
        layout.addView(currentApp);
        unsetAppInEdit();
        deleteButton.hide();

    }

    private int getWidth(){
        int w  = 0;

        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getString("app_icon_style","circle").equals("hexagon") ||
                PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                        .getString("app_icon_style","circle").equals("hexagon-outline")){
            w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

        }

        return w;
    }

    private void handleDragAppToApp(final Point rasterPoint, final AppView app){
        if (!getAppIntersection(apps, rasterPoint).equals(app.getAppPackage())) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom));
            builder.setTitle("Choose the folder name");

            final EditText input = new EditText(MainActivity.this);

            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String t = input.getText().toString().toLowerCase();

                    if (FolderManager.isNameValid(t, folders) &&
                            !t.equals("") &&
                            t != null) {
                        final FolderView newFolder = FolderManager.getNewFolderView(
                                getApplicationContext(),
                                t,
                                rasterPoint,
                                new ArrayList<>(Arrays.asList(new String[]{
                                        getAppIntersection(apps, rasterPoint),
                                        app.getAppPackage()
                                }))
                        );

                        newFolder.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                openFolder(newFolder);
                            }
                        });


                        FolderManager.createFolder(
                                t,
                                getAppIntersection(apps, rasterPoint),
                                app.getAppPackage(),
                                rasterPoint.x,
                                rasterPoint.y,
                                MainActivity.this);

                        deleteButton.hide();

                        removeAppFromLayout(getAppViewIntersection(apps, rasterPoint));
                        removeAppFromLayout(app);

                        Log.i("layout", "add new folder");
                        layout.addView(newFolder);

                        folders.add(newFolder);

                        newFolder.shake();
                    } else {
                        if (t == null || t.equals("")) {
                            Toast.makeText(getApplicationContext(), "Choose a name", Toast.LENGTH_SHORT).show();
                        } else if (FolderManager.doesFolderExist(t, folders)) {
                            Toast.makeText(getApplicationContext(), "The name does already exist", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();

        }
    }

    private void handleDragAppToFolder(final Point rasterPoint, final AppView app){

        int w = getWidth();

        FolderManager.addAppToFolder(FolderManager.getfolderIntersection(folders, rasterPoint), app.getAppPackage(), getApplicationContext());
        for (int j = 0; j < folders.size(); j++) {
            if (folders.get(j).getFolderName().equals(FolderManager.getfolderIntersection(folders, rasterPoint))) {
                ArrayList<String> newlist = folders.get(j).getAppsContained();
                newlist.add(app.getAppPackage());
                folders.get(j).setImageBitmap(FolderManager.getHexFolderIcon(newlist, asize,asize-w, getApplicationContext(), folders.get(j).getFolderName(),false));
            }
        }

        removeAppFromLayout(app);
        deleteButton.hide();
    }

    private void handleDragApp(final Point rasterPoint, final AppView app){
        setAppPosition(app, rasterPoint);

        unsetAppInEdit();
    }

    private boolean handleDragFolder(ClipDescription cl, DragEvent event){
        for (int i = 0; i < folders.size(); i++) {
            FolderView folder = folders.get(i);
            if (folder.getFolderName().equals(cl.getLabel())) {
                Point rasterPoint = Util.pixelToRaster(new Point((int) (event.getX()),
                        (int) (event.getY())), asize, padding,getApplicationContext());


                if (appIntersection(apps, rasterPoint)) {

                } else if (folderIntersection(folders, rasterPoint)) {

                } else {

                    setFolderPosition(folders.get(i), rasterPoint);

                    unsetFolderInEdit();
                }

                return true;
            }
        }

        return false;
    }

    private void handleDragView(DragEvent event){
        Log.i("drag_view","ineditmodeapppackage: "+inEditModeAppPackage);
        Point dropPoint = new Point((int)event.getX(),(int)event.getY());
        View v = getViewFromTag(inEditModeAppPackage);
        if(inEditModeAppPackage.equals("___clock")){
            v = clock;
        }
        dropPoint.y -= v.getHeight()/2;
        dropPoint.x -= v.getWidth()/2;
        Point rasterPoint = Util.pixelToRaster(dropPoint, asize, padding,getApplicationContext());
        Log.i(inEditModeAppPackage,"raster point"+ rasterPoint.toString());
        writePointOnStorage(inEditModeAppPackage,rasterPoint);
        setViewPosition(inEditModeAppPackage,rasterPoint);
    }

    View dropShadow;

    private void initDragAndDropListener() {

        /*dropShadow = new View(this);
        layout.addView(dropShadow);
        dropShadow.setVisibility(View.VISIBLE);
        dropShadow.setBackgroundColor(Color.WHITE);
        dropShadow.setAlpha(0.4f);*/


        layout.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View view, DragEvent event) {

                switch (event.getAction()) {
                    case DragEvent.ACTION_DRAG_STARTED:
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
                            }
                        });



                        Log.i("drag_start", event.getX() + " " + event.getY());
                        Log.i("drag_start", event.getClipDescription().getLabel() + " ");
                        ClipDescription cl1 = event.getClipDescription();
                        for (AppView app : apps) {
                            if (app.getAppPackage().equals(cl1.getLabel())) {
                                app.setVisibility(View.INVISIBLE);
                            }
                        }

                        break;

                    case DragEvent.ACTION_DRAG_LOCATION:

                        Point touchPosition = getTouchPositionFromDragEvent(view, event);

                        /*Point pr = Util.pixelToRaster(new Point((int) (event.getX()),
                                (int) (event.getY())), asize, padding,getApplicationContext());

                        Log.i("dropshadow",pr.toString());

                        Point rp = Util.rasterToPixel(pr,asize,padding,getApplicationContext());

                        Log.i("dropshadow",rp.toString());

                        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);
                        params.leftMargin = rp.x;
                        params.topMargin = rp.y;
                        dropShadow.setLayoutParams(
                                params
                        );*/

                        autoScroll(touchPosition);
                        break;

                    case DragEvent.ACTION_DROP:

                        slidingUpPanelLayout.setTouchEnabled(true);

                        //dropShadow.setVisibility(View.GONE);

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
                            }
                        });


                        Log.i("drag drop", event.getX() + " " + event.getY());

                        if (mHandler != null) {
                            mHandler.removeCallbacks(scroll_animation);
                            autoscrolling = false;
                        }

                        ClipDescription cl = event.getClipDescription();

                        // app is dragged out of folder

                        if (folderOpen) {

                            // get current folder

                            final FolderView currentFolderView = getCurentFolderView();

                            // get all apps in current folder

                            ArrayList<AppView> appsInCurrentFolder = FolderManager.getAppsInFolder(currentFolderView.getFolderName(), getApplicationContext(),asize);

                            // get current app

                            closeFolderFast();

                            for (int n = 0; n < appsInCurrentFolder.size(); n++) {
                                if (appsInCurrentFolder.get(n).getAppPackage().equals(String.valueOf(cl.getLabel()))) {
                                    final AppView currentApp = appsInCurrentFolder.get(n);

                                    final Point rasterPoint = Util.pixelToRaster(new Point((int) (event.getX()),
                                            (int) (event.getY())), asize, padding,getApplicationContext());

                                    // app is intersecting another app

                                    if (appIntersection(apps, rasterPoint)) {

                                        handleDragFromFolderToAnotherApp(currentFolderView,rasterPoint,currentApp);

                                    } else if (folderIntersection(folders, rasterPoint)) {

                                        handleDragFromFolderToAnotherFolder(currentFolderView,rasterPoint,currentApp);

                                        // app is not intersecting anything

                                    } else {

                                        // remove app from folder

                                        handleRemoveAppFromFolder(currentApp,currentFolderView,rasterPoint);

                                    }
                                }
                            }

                            break;
                        }

                        // if the drag object is a folder

                       if(handleDragFolder(cl, event)){
                           return true;
                       }

                        // if the drag object is an APP

                        for (int i = 0; i < apps.size(); i++) {
                            final AppView app = apps.get(i);
                            if (app.getAppPackage().equals(cl.getLabel())) {

                                final Point rasterPoint = Util.pixelToRaster(new Point((int) (event.getX()),
                                        (int) (event.getY())), asize, padding,getApplicationContext());

                                // If two apps intersect create new folder

                                if (appIntersection(apps, rasterPoint)) {

                                    handleDragAppToApp(rasterPoint,app);

                                    // if app and folder intersect than add app to folder

                                } else if (folderIntersection(folders, rasterPoint)) {

                                    handleDragAppToFolder(rasterPoint, app);

                                } else {

                                    handleDragApp(rasterPoint, app);

                                    // if the app has to be repositioned
                                }

                                dragging = false;
                                app.setVisibility(View.VISIBLE);

                                return true;
                            }
                        }

                        for (int i = 0; i < appsSearch.size(); i++) {
                            if (appsSearch.get(i).package_name.equals(cl.getLabel())) {
                                final AppView currentApp = getAppView(appsSearch.get(i));

                                final Point rasterPoint = Util.pixelToRaster(new Point((int) (event.getX()),
                                        (int) (event.getY())), asize, padding,getApplicationContext());

                                removeAppFromFolder(currentApp);

                                if (appIntersection(apps, rasterPoint)) {

                                    handleDragFromNotVisibleToAnotherApp(rasterPoint,currentApp);

                                } else if (folderIntersection(folders, rasterPoint)) {

                                    handleDragFromNotVisibleToAnotherFolder(rasterPoint,currentApp);

                                } else {
                                    handleRemoveAppFromNotVisible(currentApp,rasterPoint);

                                }

                                return true;
                            }
                        }

                        // if drag object is the view

                        handleDragView(event);

                        break;
                    default:
                        break;
                }
                return true;
            }
        });
    }

    private void folderNameChooser(final FolderView folder) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(MainActivity.this, R.style.AlertDialogCustom));
        builder.setTitle("Choose the folder name");

        final EditText input = new EditText(MainActivity.this);

        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String t = input.getText().toString().toLowerCase();
                if (FolderManager.isNameValid(t, folders)) {

                    int w = 0;

                    if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("app_icon_style","circle").equals("hexagon") ||
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .getString("app_icon_style","circle").equals("hexagon-outline")){
                        w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

                    }

                    hideKeyboard(MainActivity.this);

                    // update folder name
                    changeFolderName(folder.getFolderName(), t, getApplicationContext());
                    folder.folderName = t;
                    folder.setImageBitmap(FolderManager.getHexFolderIcon(getAppsInFolderAppPackage(folder.getFolderName(), getApplicationContext()), asize,asize-w, getApplicationContext(), folder.getFolderName(),false));
                    folderLabel.setText(t);
                } else {
                    if (t == null || t.equals("")) {
                        Toast.makeText(getApplicationContext(), "Choose a name", Toast.LENGTH_SHORT).show();
                    } else if (FolderManager.doesFolderExist(t, folders)) {
                        Toast.makeText(getApplicationContext(), "The name does already exist", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void removeAppFromLayout( final String appPackage) {
        unsetAppInEdit();
        for(int i = 0; i < apps.size(); i++){
            AppView app = apps.get(i);
            if(app.getAppPackage().equals(appPackage)){
                for (int j = 0; j < appsInfo.size(); j++) {
                    if (app.getAppPackage().equals(appsInfo.get(j).package_name)) {
                        appsInfo.remove(j);
                    }
                }
                apps.remove(app);
                app.stopShake();
                app.clearAnimation();
                app.setVisibility(View.GONE);
                layout.post(new Runnable() {
                    public void run() {
                        for(AppView app : apps){
                            if(app.getAppPackage().equals(appPackage)){
                                Log.i("layout", "remove app: " + app.getAppPackage());
                                layout.removeView(app);
                            }
                        }
                    }
                });
                app.destroyDrawingCache();
                layout.invalidate();
            }
        }
    }

    private void removeAppFromLayout(final AppView app) {
        for (int i = 0; i < appsInfo.size(); i++) {
            if (app.getAppPackage().equals(appsInfo.get(i).package_name)) {
                appsInfo.remove(i);
            }
        }
        apps.remove(app);
        app.stopShake();
        app.clearAnimation();
        app.setVisibility(View.GONE);
        layout.post(new Runnable() {
            public void run() {
                Log.i("layout", "remove app: " + app.getAppPackage());
                layout.removeView(app);
            }
        });
        app.destroyDrawingCache();
        layout.invalidate();
    }

    private void setViewPosition(String tag,Point newPosition){
        Log.i("view position",newPosition.toString());

        Point pxp = rasterToPixel(newPosition,asize,padding,getApplicationContext());
        Point size3;

        if(tag.equals("___clock")){
            size3 = Clock.getClockSize(getApplicationContext());
            Clock.setClockPosition(newPosition,getApplicationContext());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size3.x,size3.y);
            clock.setPadding(0,0,0,0);
            params.leftMargin = pxp.x;
            params.topMargin = pxp.y+padding;
            clock.setLayoutParams(
                    params
            );

            //unsetViewInEdit();
        }else{
            View v = getViewFromTag(tag);
            Log.i("asdf","this is called before");
            widgetSizeButton.show();

            WidgetInfo widgetInfo = widgetManager.getWidgetInfo(tag);

            widgetManager.updateWidgetPosition(tag,newPosition.x,newPosition.y);


            size3 = Util.rasterToPixelArea(new Point(widgetInfo.getW(),widgetInfo.getH()),asize, padding, getApplicationContext());


            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(size3.x,size3.y);
            v.setPadding(0,0,0,0);
            params.leftMargin = pxp.x;
            params.topMargin = pxp.y+padding;
            v.setLayoutParams(
                    params
            );

            RelativeLayout.LayoutParams widgetSizeButtonParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            widgetSizeButtonParams.leftMargin = (pxp.x+size3.x)-asize/4;
            widgetSizeButtonParams.topMargin = (pxp.y+padding+size3.y)-asize/4;
            widgetSizeButton.setLayoutParams(widgetSizeButtonParams);
        }



        //unsetViewInEdit();
    }

    private void setViewSize(String tag,Point newSize){
        View v = getViewFromTag(tag);

        Log.i("view position",newSize.toString());

        WidgetInfo widgetInfo = widgetManager.getWidgetInfo(tag);

        final Point position = rasterToPixel(new Point(widgetInfo.getX(),widgetInfo.getY()),asize,padding,getApplicationContext());
        final Point pxp = rasterToPixelArea(newSize,asize,padding,getApplicationContext());

        /*v.setOutlineProvider(new ViewOutlineProvider() {
            @Override
            public void getOutline(View view, Outline outline) {
                outline.setRoundRect(0,0,pxp.x,pxp.y,asize/3);
            }
        });
        v.setClipToOutline(true);*/

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(pxp.x,pxp.y);
        v.setPadding(0,0,0,0);
        params.leftMargin = position.x;
        params.topMargin = position.y+padding;
        v.setLayoutParams(
                params
        );
    }


    public View getViewFromTag(String tag){
        Log.i("getview",tag);
        for(View v : editableViews){
            if(String.valueOf(v.getTag()).equals(tag)){
                return v;
            }
        }
        return null;
    }

    private void setAppPosition( AppView app, Point newPosition) {
        Log.i("newapppos", app.getAppPackage()+" "+newPosition.toString());

        try {

            app.setPosition(newPosition,getApplicationContext());


            if (!settingsMode) {
                Log.i("call checkScale","from: "+"setappposition");
                checkScale();
            }
        } finally {
            layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
                @Override
                public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                    layout.removeOnLayoutChangeListener(this);
                    if (!settingsMode) {
                        Log.i("call checkScale","from: "+"addonlayoutchangelistener");
                        checkScale();
                    }
                }
            });
        }
    }


    private void initAppViewList() {

        // init the data list for folder and apps

        convertDB(getApplicationContext());

        apps = new ArrayList<>();
        appsInfo = new ArrayList<>();
        folders = new ArrayList<>();

        appsInfo.addAll(getAppsFromDB(getApplicationContext()));

        apps.addAll(getAppViewApps());
        folders.addAll(FolderManager.getFoldersFromDB(getApplicationContext()));

        for(final FolderView folder : folders){
            folder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openFolder(folder);
                }
            });

            folder.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    Log.e("folder","foldr in settings mode");

                    setFolderInEdit(folder);

                    folder.setVisibility(View.INVISIBLE);

                    // create drag object

                    ClipData data = ClipData.newPlainText(folder.getFolderName(), "");
                    data.addItem(new ClipData.Item(folder.getFolderName()));
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            folder);
                    shadowBuilder.getView().setAlpha(1f);

                    dragging = true;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        folder.startDragAndDrop(data, shadowBuilder, folder, 0);
                    } else {
                        folder.startDrag(data, shadowBuilder,folder, 0);
                    }

                    if(!settingsMode)
                        toggleSettingsMode();

                    return true;
                }
            });
        }

        for(AppView app : apps){
            app.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    //outline.setRoundRect(0,0,asize,asize,asize/2);
                    outline.setOval(10,10,asize-10,asize-10);
                }
            });
            app.setElevation((PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(BUBBLE_SHADOW,10)));
            app.setClipToOutline(false);
        }

        Log.i("apps size",apps.size() +" size");

        addImagesToView(apps);
        addFoldersToView(folders);
        initBadges();

        final SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        final int y = preferences.getInt(Y_START_KEY,asize * 2);
        final int x = preferences.getInt(X_START_KEY,asize * 2);

        scroller.scrollTo(x,y);

        addImagesToAppsInfo();

        Log.i("is dialer"," "+AppManager.isPackageInList(AppManager.getDialerPackageName(getApplicationContext()),appsInfo));
    }

    public void groupApps(ArrayList<String> appGroup){
        int size = AppManager.getSquareSize(appGroup.size());
        Point groupSize = new Point(size,size);
        Log.i("Group size",groupSize.toString());
        //Point freePos = getFreeGroupPos(groupSize,getNotContainingSublist(apps,appGroup),folders);
        Point freePos = new Point(2,2);
        Log.i("Group pos",freePos.toString());
        ArrayList<Point> groupPoints = offsetPoints(AppManager.getPoints(appGroup.size()),freePos);
        int counter = 0;
        for(int i = 0; i < apps.size(); i++){
            for(int n = 0; n < appGroup.size(); n++){
                if(apps.get(i).getAppPackage().equals(appGroup.get(n)) && counter < groupPoints.size()){
                    setAppPosition(apps.get(i),groupPoints.get(counter));
                    Log.i("Group",groupPoints.get(counter).toString());
                    counter++;
                }
            }

        }
    }

    public void groupFolders(ArrayList<String> folderNames){
        int size = AppManager.getSquareSize(folderNames.size());
        Point groupSize = new Point(size,size);
        Log.i("Group size",groupSize.toString());
        //Point freePos = getFreeGroupPos(groupSize,apps,getNotContainingSublistf(folders,folderNames));
        Point freePos = new Point(2,5);
        Log.i("Group pos",freePos.toString());
        ArrayList<Point> groupPoints = offsetPoints(AppManager.getPoints(folderNames.size()),freePos);
        int counter = 0;
        for(int i = 0; i < folders.size(); i++){
            for(int n = 0; n < folderNames.size(); n++){
                if(folders.get(i).folderName.equals(folderNames.get(n)) && counter < groupPoints.size()){
                    setFolderPosition(folders.get(i),groupPoints.get(counter));
                    Log.i("Group",groupPoints.get(counter).toString());
                    counter++;
                }
            }

        }
    }

    public ArrayList<Point> offsetPoints(ArrayList<Point> points,Point offset){
        for(int i = 0; i < points.size(); i++){
            points.get(i).y += offset.y;
            points.get(i).x += offset.x;
        }
        return points;
    }

    public AppView getAppViewFromPackage(ArrayList<AppView> apps, String packageName){
        AppView ret = null;
        for(AppView app : apps){
            if(app.getAppPackage().equals(packageName)){
                ret = app;
            }
        }
        return ret;
    }

    public ArrayList<AppView> getAppViewGroupFromPackages(ArrayList<AppView> apps, ArrayList<String> packageNames){
        ArrayList<AppView> retGroup = null;
        for(String p : packageNames){
            AppView app = getAppViewFromPackage(apps, p);
            if(app != null){
                retGroup.add(app);
            }
        }
        return retGroup;
    }

    public ArrayList<AppView> getNotContainingSublist(ArrayList<AppView> apps, ArrayList<String> packages) {
        ArrayList<AppView> retlist = new ArrayList<>();
        retlist.addAll(apps);
        for(AppView app : apps){
            for(String str : packages){
                if(app.getAppPackage().equals(str)){
                    retlist.remove(app);
                }
            }
        }

        Log.i("Group","Sublist "+retlist.size()+" old "+apps.size());

        return retlist;
    }

    public ArrayList<FolderView> getNotContainingSublistf(ArrayList<FolderView> apps, ArrayList<String> packages) {
        ArrayList<FolderView> retlist = new ArrayList<>();
        retlist.addAll(apps);
        for(FolderView app : apps){
            for(String str : packages){
                if(app.folderName.equals(str)){
                    retlist.remove(app);
                }
            }
        }

        Log.i("Group","Sublist "+retlist.size()+" old "+apps.size());

        return retlist;
    }

    public ArrayList<AppView> getSublist(ArrayList<AppView> apps){
        ArrayList<AppView> sublist = new ArrayList<>();
        for(AppView app : apps){
            if(!app.getAppPackage().startsWith("com.android") && !app.getAppPackage().startsWith("com.google")){
                sublist.add(app);
            }
        }
        Log.i("sublist opt",apps.size() +" transformed to: "+sublist.size());

        return sublist;
    }

    public boolean isAppUserInstalled(String appPackage){
        ApplicationInfo appInfo;
        int flags = PackageManager.GET_META_DATA |
                PackageManager.GET_SHARED_LIBRARY_FILES |
                PackageManager.GET_UNINSTALLED_PACKAGES;
        try {
            appInfo = getPackageManager().getApplicationInfo(appPackage,flags);
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                return false;
            } else {
                return true;
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
    }

    private ArrayList<String> getPermissionForPackage(String packageName){
        ArrayList<String> retlist = new ArrayList<>();
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName,PackageManager.GET_PERMISSIONS);
            String[] requestedPermissions = packageInfo.requestedPermissions;
            if(requestedPermissions != null){
                for(String s : requestedPermissions){
                    retlist.add(s);
                }
            }else{
                Log.i("Permission","Array is null");
            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return retlist;
    }

    private void addImagesToAppsInfo() {
        for (int i = 0; i < appsInfo.size(); i++) {
            appsInfo.get(i).drawable = apps.get(i).getBackground();
        }
    }

    private void checkIfShowLabels() {
        showlabel = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SettingsActivity2.PREF_APPLABELS_TOGGLE, false);
    }

    float pivotx;
    float pivoty;

    private void initScrollViews() {
        scroller = findViewById(R.id.two_d_scroller);
        scroller.setScrollChangeListner(new TwoDScrollView.ScrollChangeListener() {
            @Override
            public void onScrollChanged(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                if(!settingsMode)
                    checkScale();
            }
        });


        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PREF_BACKGROUND_SCROLLABLE,false)){
            scroller.setBackgroundColor(Color.BLACK);
        }

        scroller.setLongPressListener(new TwoDScrollView.LongPressListener() {
            @Override
            public void onLongPress() {
                anchorButton.show();
                if(IS_PREMIUM){ // don't show until problem with widgets is solved
                    //add_widget_button.show();
                }
                toggleSettingsMode();
            }
        });

        scroller.setZoomListener(new TwoDScrollView.ZoomListener() {
            @Override
            public void onZoom(float scale, ScaleGestureDetector detector) {
                /*layout.setScaleX(scale);
                layout.setScaleY(scale);
                int focusX = (int )detector.getFocusX();
                int focusY = (int) detector.getFocusY();
                Log.i("getFocus "+scale, "widtg "+layout.getWidth()+
                        " height "+layout.getHeight()+" scroller x "+scroller.getScrollX()+
                        " "+scroller.getScrollY()+" focus "+focusX+" "+focusY+" span "+detector.getCurrentSpanX()+" "+detector.getCurrentSpanY());



                //layout.setPivotX(detector.getFocusX());
                //layout.setPivotY(detector.getFocusY());

                pivotx = detector.getFocusX()+scroller.getScrollX();
                pivoty = detector.getFocusY()+scroller.getScrollY();


                //layout.setPivotX(pivotx);
                //layout.setPivotY(pivotx);
                Log.i("getFocus",(focusX+scroller.getScrollX())+" "+(focusY+scroller.getScrollY()));
                //checkScale();*/
            }

            @Override
            public void onZoomStart(ScaleGestureDetector detector){

            }
        });




    }

    ImageView image;

    private void initlayout() {
        layout = findViewById(R.id.wrapper);
        image = findViewById(R.id.background_image_view);
        layout.setClipToPadding(false);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
        layout.setLayoutParams(layoutParams);

        /*layout.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE |
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);*/
        boolean wallp_toggle = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PREF_WALLPAPER_TOGGLE,false);

        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 12);
            return;
        }


        if(wallp_toggle){
            layout.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getInt(PREF_BACKGROUND_COLOR,0x000000));
        }else{
            if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(PREF_BACKGROUND_SCROLLABLE,false)){

                //layout.setBackground(wallpaperDrawable);

                try {
                    ContextWrapper cw = new ContextWrapper(getApplicationContext());
                    File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
                    File f=new File(directory, "profile.jpg");
                    Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));

                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            500,500);
                    image.setLayoutParams(lp);
                    image.setImageBitmap(b);
                    image.setScaleType(ImageView.ScaleType.CENTER_CROP);
                }
                catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                }


            }else{
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
                        WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
                layout.setBackground(null);
                layout.setBackgroundColor(getResources().getColor(android.R.color.transparent));
            }
        }


        layout.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View view, int i, int i1, int i2, int i3, int i4, int i5, int i6, int i7) {
                Log.i("tree", "changed");
                //updatePoints();
                layout.setPadding(
                        0,
                        0,
                        asize*2,
                        asize*2);
                FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                        layout.getWidth(),layout.getHeight());
                image.setLayoutParams(lp);
                if (!settingsMode) {
                    Log.i("call checkScale","from: "+"init layoutt");
                    checkScale();
                }
            }
        });



        //background_image = findViewById(R.id.background_image);


        //FrameLayout.LayoutParams slayoutParams = new FrameLayout.LayoutParams(layout.getWidth(),layout.getHeight());
        //FrameLayout.LayoutParams slayoutParams = new FrameLayout.LayoutParams(hScroll.getWidth(),vScroll.getHeight());

        folderOverlay = findViewById(R.id.overlay_folder);
        //folderOverlay.setLayoutParams(slayoutParams);
        currentlyInFolderOverlay = new ArrayList<>();

        folderOverlay.setVisibility(View.GONE);

        layout.addOnAttachStateChangeListener(new View.OnAttachStateChangeListener() {
            @Override
            public void onViewAttachedToWindow(View v) {
                initDisplayCutout();
            }

            @Override
            public void onViewDetachedFromWindow(View v) {
            }
        });

    }

    private void initPackageEvents() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter.addDataScheme("package");
        registerReceiver(appChangeReceiver, intentFilter);

        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter1.addDataScheme("package");
        registerReceiver(appInstallReceiver, intentFilter1);

        EventBus.getDefault().register(this);
    }

    private void initWindow() {

        /*
        if (PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(SearchBarSettings.PREF_STATUSBAR_TOGGLE, false)) {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        //getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);


        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER,
        //        WindowManager.LayoutParams.FLAG_SHOW_WALLPAPER);
 */

        if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(HIDE_STATUS_BAR_KEY,false)){
            View decorView = getWindow().getDecorView();
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }else{
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }


    }

    private void initWdithHeight() {
        Display display = getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(size);
        } else {
            display.getSize(size);
        }
        width = size.x;
        height = size.y;
    }

    private void initAppRate() {
        AppRate.with(this)
                .setInstallDays(1) // default 10, 0 means install day.
                .setLaunchTimes(5) // default 10
                .setRemindInterval(2) // default 1
                .setShowLaterButton(true) // default true
                .setDebug(false) // default false
                .setOnClickButtonListener(new OnClickButtonListener() { // callback listener.
                    @Override
                    public void onClickButton(int which) {
                        Log.d(MainActivity.class.getName(), Integer.toString(which));
                    }
                })
                .monitor();

        // Show a dialog if meets conditions
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //Log.i("new config","hallo");
        //updatePoints();
        //checkScale();
        Log.i("call checkScale","from: "+"onConfigurationChanged");
        // updateApps();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(!firstUse){
            initWindow();
            if (!settingsMode) {
                Log.i("call checkScale","from: "+"onWindowFocusChanged");
                checkScale();
            }
        }
    }

    public void updateApps() {
        try {
            if (settingsMode) {
                toggleSettingsMode();
            }

            layout.removeAllViews();

            initWindow();

            initWdithHeight();

            //setupLabel(getSharedPreferences(MainActivity.SETTINGS_KEY, Context.MODE_PRIVATE)
            //        .getBoolean(SettingsFragment.LABEL_KEY,false));

            initAppViewList();

            initClockOjbects();
            initWidgets();

        } finally {
            layout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                    for (AppView app : apps) {
                        int[] a = new int[2];
                        app.getLocationOnScreen(a);
                        //Log.i("app name", app.appName+" ");
                    }
                    if (!settingsMode) {
                        Log.i("call checkScale","from: "+"update apps");
                        checkScale();
                    }
                }
            });
        }
    }

    public boolean hasLaunchIntent(final String appPackage){
        PackageManager packageManager = getPackageManager();

        Intent intent = getPackageManager().getLaunchIntentForPackage(appPackage);

        if (intent != null) {
            Log.d("openable","app is openable");
            return true;
            //startActivity(intent);
        } else {
            Log.d("openable", "No Intent available to handle action");
            return false;
        }
    }

    public void installedApp(final String appPackage) {

        if(!hasLaunchIntent(appPackage))
            return;

        installApp(getApplicationContext(), appPackage, apps);

        if(!isAppVisible(this,appPackage))
            return;

        DataObj appData = getAppFromDB(this,appPackage);

        String folder = isAppInFolder(this,appPackage);
        Log.i("apps_in_folder", "foldername: "+folder);
        if(!folder.equals("")){
            for(FolderView f : folders){
                if(f.folderName.equals(folder)){
                    int w = 0;

                    if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("app_icon_style","circle").equals("hexagon") ||
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .getString("app_icon_style","circle").equals("hexagon-outline")){
                        w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

                    }

                    FolderView currentFolderView = f;

                    ArrayList<String> appsInfFolderForIcon = getAppsInFolderAppPackage(
                            currentFolderView.getFolderName(),
                            getApplicationContext());

                    for(String str : appsInfFolderForIcon){
                        Log.i("apps_in_folder"," now in folder: "+str);
                    }

                    currentFolderView.setImageBitmap(
                            FolderManager.getHexFolderIcon(
                                    appsInfFolderForIcon,
                                    asize,
                                    asize-w,
                                    getApplicationContext(),
                                    currentFolderView.getFolderName(),
                                    true
                            )
                    );
                }
            }
        }else{
            final AppView currentApp = getAppView(appData);

            // add app to layout

            setAppPosition(currentApp, currentApp.getPosition());

            currentApp.setPosition(currentApp.getPosition(),getApplicationContext());
            apps.add(currentApp);
            currentApp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApp(appPackage);
                }
            });
            currentApp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    setAppInEdit(currentApp);
                    currentApp.setVisibility(View.INVISIBLE);

                    // create drag object

                    ClipData data = ClipData.newPlainText(currentApp.getAppPackage(), "");
                    data.addItem(new ClipData.Item(currentApp.getAppPackage()));
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            currentApp);
                    shadowBuilder.getView().setAlpha(1f);

                    dragging = true;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        currentApp.startDragAndDrop(data, shadowBuilder, currentApp, 0);
                    } else {
                        currentApp.startDrag(data, shadowBuilder,currentApp, 0);
                    }

                    if(!settingsMode)
                        toggleSettingsMode();

                    return true;
                }
            });

            currentApp.shake();
            appsInfo.add(appData);
            layout.addView(currentApp);
        }
    }

    final String DEBUG_APPONPHONE = "appOnPhone";

    public static String LAST_DEINSTALLED_APP_CLASS = "last_deinstalled_app_class";

    public void updatePoints() {
        Log.i("getAppsFromDB", "updatePoints");
        appsInfo = getAppsFromDB(getApplicationContext());
        //appsPositions = getPoints(appsInfo.size() + 1, padding, padding, padding, asize);
        for (int i = 0; i < apps.size(); i++) {
            apps.get(i).setPosition(new Point(appsInfo.get(i).x, appsInfo.get(i).y),getApplicationContext());
            apps.get(i).getLocationOnScreen(new int[2]);
            if (!settingsMode)
                scaleNonText(apps.get(i), getScaleOfApp(apps.get(i)));
        }
        layout.setPadding(0, 0, asize*2, asize*20);
        if (!settingsMode) {
            Log.i("call checkScale","from: "+"updateapps");
            checkScale();
        }
    }

    public AppView getAppView(DataObj obj){
        AppView temp = new AppView(getApplicationContext(),
                obj.package_name,
                obj.name,
                new Point(obj.x, obj.y));

        try {

            Log.i("Debug the icon loading", obj.package_name);
            Bitmap icon = appManager.getAppIcon(obj.package_name,asize, getApplicationContext());

            //Bitmap icon = appManager.getAppIconLabel(appsInfo.get(i).package_name,appsInfo.get(i).iconRes,appsInfo.get(i).name,asize);
            temp.setBackground(new BitmapDrawable(getResources(),icon));
        } catch (NullPointerException e) {
            temp.setBackgroundResource(R.drawable.placeholder);
        }

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);

        temp.setLayoutParams(
                params
        );

        return temp;
    }

    public ArrayList<AppView> getAppViewApps() {

        ArrayList<AppView> retlist = new ArrayList<>();

        //ArrayList<DataObj> todelete = new ArrayList<>();

        for (int i = 0; i < appsInfo.size(); i++) {

            final String package_string =  appsInfo.get(i).package_name;

            AppView temp = new AppView(getApplicationContext(),
                    appsInfo.get(i).package_name,
                    appsInfo.get(i).name,
                    new Point(appsInfo.get(i).x, appsInfo.get(i).y));

            try {

                //Log.i("Debug the icon loading", appsInfo.get(i).package_name);
                Bitmap icon = appManager.getAppIcon(appsInfo.get(i).package_name,asize, getApplicationContext());
                //Bitmap icon = appManager.getAppIconLabel(appsInfo.get(i).package_name,appsInfo.get(i).iconRes,appsInfo.get(i).name,asize);

                temp.setBackground(new BitmapDrawable(getResources(),icon));

            } catch (NullPointerException e) {
                temp.setBackgroundResource(R.drawable.placeholder);
            }

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);

            temp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApp(package_string);
                    notificationManager.removeNotifications(package_string);
                }
            });

            temp.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if(!settingsMode)
                        toggleSettingsMode();

                    AppView appView = getAppViewFromPackage(apps,package_string);
                    setAppInEdit(appView);
                    appView.setVisibility(View.INVISIBLE);

                    // create drag object

                    ClipData data = ClipData.newPlainText(appView.getAppPackage(), "");
                    data.addItem(new ClipData.Item(appView.getAppPackage()));
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            appView);
                    shadowBuilder.getView().setAlpha(1f);

                    dragging = true;

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        Log.i("panel state"," start drag and drop");
                        appView.startDragAndDrop(data, shadowBuilder, appView, 0);
                    } else {
                        Log.i("panel state"," start drag and drop");
                        appView.startDrag(data, shadowBuilder,appView, 0);
                    }

                    return true;
                }
            });

            Point pxpoint = rasterToPixel(new Point(appsInfo.get(i).x, appsInfo.get(i).y), asize, padding,getApplicationContext());

            Log.i("rasterpoint app", pxpoint.toString());
            Log.i("rasterpoint px app", new Point(appsInfo.get(i).x, appsInfo.get(i).y).toString());

            params.leftMargin = pxpoint.x;
            params.topMargin = pxpoint.y;

            temp.setLayoutParams(
                    params
            );

            retlist.add(temp);
        }

        return retlist;
    }

    public static float interpolatePivot(int lenght, int position) {
        if (position == 0) {
            position = 1;
        }

        lenght = (int) (lenght - (2f * (asize)));

        float ret = 1f - (position / (lenght * 1f));

        if (ret > 1f) {
            return 1f;
        } else if (ret < 0f) {
            return 0f;
        } else {
            return ret;
        }
    }

    public ArrayList<Float> getScalePosition(AppView v) {

        float x = 0.5f;
        float y = 0.5f;

        Point p = getPosition(v);
        String site = getSite(p, (int) (asize * v.getScaleX()));

        switch (site) {
            case "left":
                x = 1;
                y = interpolatePivot(height, p.y);
                break;
            case "right":
                x = 0;
                y = interpolatePivot(height, p.y);
                break;
            case "top":
                y = 1;
                x = interpolatePivot(width, p.x);
                break;
            case "bottom":
                y = 0;
                x = interpolatePivot(width, p.x);
                break;
            default:
                break;
        }

        ArrayList<Float> retlist = new ArrayList<>();
        retlist.add(x);
        retlist.add(y);
        return retlist;
    }

    public void scaleNonTextView(View v, float scale) {

        float x = 0.5f;
        float y = 0.5f;

        Point p = getPosition(v);
        String site = getSite(p, (int) (asize * v.getScaleX()));

        switch (site) {
            case "left":
                x = 1;
                y = interpolatePivot(height, p.y);
                break;
            case "right":
                x = 0;
                y = interpolatePivot(height, p.y);
                break;
            case "top":
                y = 1;
                x = interpolatePivot(width, p.x);
                break;
            case "bottom":
                y = 0;
                x = interpolatePivot(width, p.x);
                break;
            default:
                break;
        }

        //x = .5f;
        //y = .5f;

        Animation anim = new ScaleAnimation(
                scale, scale, // Start and end values for the Y axis scaling
                scale, scale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, x, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, y); // Pivot point of Y scaling

        anim.setDuration(0);
        anim.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(anim);
    }

    // todo: fit for any view

    public void scaleNonView(View v, float scale) {

        float x = 0.5f;
        float y = 0.5f;

        Point p = getPosition(v);
        String site = getSite(p, (int) (asize * v.getScaleX()));

        switch (site) {
            case "left":
                x = 1;
                y = interpolatePivot(height, p.y);
                break;
            case "right":
                x = 0;
                y = interpolatePivot(height, p.y);
                break;
            case "top":
                y = 1;
                x = interpolatePivot(width, p.x);
                break;
            case "bottom":
                y = 0;
                x = interpolatePivot(width, p.x);
                break;
            default:
                break;
        }

        //x = .5f;
        //y = .5f;

        Animation anim = new ScaleAnimation(
                scale, scale, // Start and end values for the Y axis scaling
                scale, scale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling

        anim.setDuration(0);
        anim.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(anim);
    }

    public void scaleNonText(AppView v, float scale) {
        v.setAppScale(scale);

        float x = 0.5f;
        float y = 0.5f;

        Point p = getPosition(v);
        String site = getSite(p, (int) (asize * v.getScaleX()));

        switch (site) {
            case "left":
                x = 1;
                y = interpolatePivot(height, p.y);
                break;
            case "right":
                x = 0;
                y = interpolatePivot(height, p.y);
                break;
            case "top":
                y = 1;
                x = interpolatePivot(width, p.x);
                break;
            case "bottom":
                y = 0;
                x = interpolatePivot(width, p.x);
                break;
            default:
                break;
        }

        //x = .5f;
        //y = .5f;

        Animation anim = new ScaleAnimation(
                scale, scale, // Start and end values for the Y axis scaling
                scale, scale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, x, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, y); // Pivot point of Y scaling

        anim.setDuration(0);
        anim.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(anim);
    }

    public void scaleNonText(FolderView v, float scale) {
        v.setAppScale(scale);

        float x = 0.5f;
        float y = 0.5f;

        Point p = getPosition(v);
        String site = getSite(p, (int) (asize * v.getScaleX()));

        switch (site) {
            case "left":
                x = 1;
                y = interpolatePivot(height, p.y);
                break;
            case "right":
                x = 0;
                y = interpolatePivot(height, p.y);
                break;
            case "top":
                y = 1;
                x = interpolatePivot(width, p.x);
                break;
            case "bottom":
                y = 0;
                x = interpolatePivot(width, p.x);
                break;
            default:
                break;
        }

        //x = .5f;
        //y = .5f;

        Animation anim = new ScaleAnimation(
                scale, scale, // Start and end values for the Y axis scaling
                scale, scale, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, x, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, y); // Pivot point of Y scaling

        anim.setDuration(0);
        anim.setFillAfter(true); // Needed to keep the result of the animation
        v.startAnimation(anim);
    }

    public void scaleSettingsButtons(){
        for( FloatingActionButton floatingActionButton: settingsButtonList){
            scaleNonTextView(floatingActionButton,getScaleOfApp(floatingActionButton));
        }
    }

    public Point getSize(RelativeLayout layout) {
        int x = 0;
        int y = 0;
        for (int i = 0; i < layout.getChildCount(); i++) {
            View v = layout.getChildAt(i);
            if(v.getMeasuredWidth()+v.getX() > x){
                x =  v.getMeasuredWidth()+ (int) v.getX();
            }
            if(v.getMeasuredHeight()+v.getY() > y){
                y =  v.getMeasuredHeight()+ (int) v.getY();
            }
        }
        return new Point(x,y);
    }

    public void checkScale() {

        int x = scroller.getScrollX();
        int y = scroller.getScrollY();
        Rect windowRect = new Rect();

        layout.getWindowVisibleDisplayFrame(windowRect);

        final int hWindow = windowRect.height();
        final int wWindow = windowRect.width();

        //Log.i("scroll x",x+" border "+(x+wWindow)+" "+((x+wWindow) > 2000));

        final Point size = getSize(layout);

        if (scaled) {

            for (AppView app : apps) {
                scaleNonText(app, 1);
            }

            for (FolderView folder : folders) {
                scaleNonText(folder, 1);
            }

            for (MaterialBadgeTextView badge : badges){
                scaleNonTextView(badge, 1);
                badge.bringToFront();
            }
        }

        if (!scaled) {
            for (AppView app : apps) {
                app.getLocationOnScreen(new int[2]);
                scaleNonText(app, getScaleOfApp(app));
            }

            // todo: scale widgets

            if(editableViews != null){
                for (View view : editableViews) {
                view.getLocationOnScreen(new int[2]);
                scaleNonView(view, getScaleOfView(view));
                }
                if(display_clock){
                    scaleNonView(clock, getScaleOfView(clock));
                }
            }





            for (FolderView folder : folders) {
                folder.getLocationOnScreen(new int[2]);
                scaleNonText(folder, getScaleOfFolder(folder));
            }

            if(badges != null){
                for (MaterialBadgeTextView badge : badges){
                    badge.getLocationOnScreen(new int[2]);
                    scaleNonTextView(badge, getScaleOfApp(badge));
                    badge.bringToFront();
                }
            }

            if (folderOpen) {
                for (AppView app : currentlyInFolderOverlay) {
                    //if(app.anim.hasEnded())
                    scaleNonText(app, getScaleOfApp(app));
                }
            }
        }
    }

    public float getScaleOfFolder(FolderView app) {
        if (isViewInside(app, (int) (asize * app.getScaleX()))) {
            if (isInsideBound(app, (int) (asize * app.getScaleX()))) {
                return 1f;
            } else {
                return interpolate(getPosition(app), (int) (asize * app.getScaleX()));
            }
        } else {
            return 0f;
        }
    }

    public float getScaleOfView(View view) {

        int viewWidth = view.getWidth();
        int viewHeight = view.getHeight();

        // height for screen height
        // width for screen width




        Point positionOnScreen = getPosition(view);

        int pad = asize/4;
        int width = this.width - (pad*2);
        int height = this.height - (pad*2);
        positionOnScreen.x = positionOnScreen.x - pad;
        positionOnScreen.y = positionOnScreen.y - pad;

        //Log.i("position", positionOnScreen.x+" " +viewWidth+" "+width);

        if(positionOnScreen.x < 0 || positionOnScreen.x + viewWidth > width || positionOnScreen.y < 0 || positionOnScreen.y + viewHeight > height){

            int rightDiff = (int)(width - (positionOnScreen.x + (viewWidth/2f)));
            int leftDiff = positionOnScreen.x + (viewWidth/2);

            int bottomDiff = (int)(height - (positionOnScreen.y + (viewHeight/2f)));
            int topDiff = positionOnScreen.y + (viewHeight/2);

            if(leftDiff < 0)
                leftDiff = 0;

            if(rightDiff < 0)
                rightDiff = 0;

            if(bottomDiff < 0)
                bottomDiff = 0;

            if(topDiff < 0)
                topDiff = 0;

           // view.setBackgroundColor(Color.argb(150, 255, 5, 10));

            float horizontalScale = 1f;

            if(leftDiff < rightDiff){
                horizontalScale = (float)leftDiff / (viewWidth/2f);
            }else{
                horizontalScale = (float)rightDiff / (viewWidth/2f);
            }

            float verticalScale = 1f;

            if(topDiff < bottomDiff){
                verticalScale = (float)topDiff / (viewHeight/2f);
            }else{
                verticalScale = (float)bottomDiff / (viewHeight/2f);
            }

            if(verticalScale < horizontalScale){
                return verticalScale;
            }else{
                return horizontalScale;
            }


        }else{
            //view.setBackgroundColor(Color.argb(150, 10, 255, 10));
        }


        return 1f;
    }

    public float getScaleOfApp(View app) {

        if (isViewInside(app, (int) (app.getWidth() * app.getScaleX())) /* && !isAppInCutout(getPosition(app))*/) {
            if (isInsideBound(app, (int) (app.getHeight() * app.getScaleX())) /* && !isInsideBoundCutout(app,(int) (app.getHeight() * app.getScaleX()))*/) {
                return 1f;
            } else {
                return interpolate(getPosition(app), (int) (app.getHeight() * app.getScaleX()));
            }
        } else {
            return 0f;
        }
    }

    private int mInterval = (1000 / 60);
    private Point fingerPosition;
    private Handler mHandler;

    public void autoScroll(Point point) {
        fingerPosition = point;
        if (fingerInBounds(point)) {
            autoscrolling = false;
            if (mHandler != null) {
                mHandler.removeCallbacks(scroll_animation);
            }
        } else {
            mHandler = new Handler();
            if (autoscrolling == false)
                scroll_animation.run();
        }

    }

    public boolean fingerInBounds(Point position) {
        if (position.x > asize &&
                position.x < width - asize &&
                position.y > asize &&
                position.y < height - asize) {
            return true;
        } else {
            return false;
        }
    }

    Runnable scroll_animation = new Runnable() {
        @Override
        public void run() {
            autoscrolling = true;
            scrollBy(fingerPosition);
            mHandler.postDelayed(scroll_animation, mInterval);
        }
    };

    private void scrollBy(Point fingerPosition) {
        if (fingerPosition.x < asize) {
            scroller.smoothScrollBy(- 30, 0);
        } else if (fingerPosition.x > (width - asize)) {
            scroller.smoothScrollBy( 30, 0);
        } else if (fingerPosition.y < asize) {
            scroller.smoothScrollBy(0,  - 30);
        } else if (fingerPosition.y > (height - asize)) {
            scroller.smoothScrollBy(0,  30);
        } else {
            if (mHandler != null) {
                mHandler.removeCallbacks(scroll_animation);
                autoscrolling = false;
            }
        }
    }

    private void scroll(Point fingerPosition) {
        if (fingerPosition.x < asize) {
            scroller.scrollTo(scroller.getScrollX() - 20, 0);
        } else if (fingerPosition.x > (width - asize)) {
            scroller.scrollTo(scroller.getScrollX() + 20, 0);
        } else if (fingerPosition.y < asize) {
            scroller.scrollTo(0, scroller.getScrollY() - 20);
        } else if (fingerPosition.y > (height - asize)) {
            scroller.scrollTo(0, scroller.getScrollY() + 20);
        } else {
            if (mHandler != null) {
                mHandler.removeCallbacks(scroll_animation);
                autoscrolling = false;
            }
        }
    }

    public Point getTouchPositionFromDragEvent(View item, DragEvent event) {
        Rect rItem = new Rect();
        item.getGlobalVisibleRect(rItem);
        return new Point(rItem.left + Math.round(event.getX()) - scroller.getScrollX(), rItem.top + Math.round(event.getY()) - scroller.getScrollY());
    }

    public int getSmalestMargin(Point app_position, int app_size) {

        int cutout_bottom;
        int cutout_left;
        int cutout_right;

        int left = app_position.x + (app_size / 2);
        int right = (width - app_position.x - (app_size / 2));
        int top = app_position.y + (app_size / 2);
        int bottom = (height - app_position.y) - (app_size / 2);

        int smallest_margin;

        if (left <= right && left <= top && left <= bottom)
            smallest_margin =  2 * left + app_size;
        else if (right <= left && right <= top && right <= bottom)
            smallest_margin =   2 * right + app_size;
        else if (top <= right && top <= left && top <= bottom)
            smallest_margin =   2 * top + app_size;
        else if (bottom <= right && bottom <= top && bottom <= left)
            smallest_margin =   2 * bottom + app_size;
        else
            smallest_margin =  0;

        return smallest_margin;
    }

    public static String getSite(Point p, int s) {
        int left = p.x + (s / 2);
        int right = (width - p.x - (s / 2));
        int top = p.y + (s / 2);
        int bottom = (height - p.y) - (s / 2);

        if (left <= right && left <= top && left <= bottom)
            return "left";
        else if (right <= left && right <= top && right <= bottom)
            return "right";
        else if (top <= right && top <= left && top <= bottom)
            return "top";
        else if (bottom <= right && bottom <= top && bottom <= left)
            return "bottom";
        else
            return null;
    }

    private void toggleSettingsMode() {

        if (settingsMode) {
            closeSettingsMode.hide();
            printThemeButton.hide();

            settingsButton.hide();
            designButton.hide();
            deleteButton.hide();
            add_widget_button.hide();
            if(!IS_PREMIUM){
                premiumButton.hide();
            }
            anchorButton.hide();
            updateButton.hide();
            settingsMode = false;

            if(display_clock){
                clock.stopShake();
            }

            for (int i = 0; i < apps.size(); i++) {
                apps.get(i).stopShake();
            }

            for (int i = 0; i < folders.size(); i++) {
                folders.get(i).stopShake();
            }

            for (WidgetView v : editableViews) {
                v.stopShake();
            }

            if (!settingsMode) {
                checkScale();
            }
            unsetAppInEdit();
            unsetViewInEdit();

            premiumScroller.setVisibility(View.GONE);

        } else {

            settingsButton.show();
            if(!IS_PREMIUM)
                premiumScroller.setVisibility(View.VISIBLE);


            if(!checkPremium(getApplicationContext())){
                if(!IS_PREMIUM){
                    //
                }
            }

            settingsMode = true;

            for (int i = 0; i < apps.size(); i++) {
                apps.get(i).setAppScale(1f);
                apps.get(i).shake();
            }

            for (WidgetView v : editableViews) {
                v.shake();
            }

            if(display_clock){
                clock.shake();
            }

            for (int i = 0; i < folders.size(); i++) {
                folders.get(i).setAppScale(1f);
                folders.get(i).shake();
            }



        }
    }

    int dist = 0;
    boolean scroll = true;
    boolean widgetNotTouchable = true;

    private int spaceBetweentPoints(MotionEvent.PointerCoords p1,
                                    MotionEvent.PointerCoords p2) {
        double w = p1.x - p2.x;
        double h = p1.y - p2.y;

        int a = (int) Math.sqrt(Math.pow(w, 2) + Math.pow(h, 2));

        return a;
    }


    public class MyInterpolator implements Interpolator {
        public MyInterpolator() {
        }

        public float getInterpolation(float t) {
            return 0.9f * t;
        }
    }

    public void intersectWithOpenFolder(MotionEvent event) {
        for (int i = 0; i < currentlyInFolderOverlay.size(); i++) {
            Rect outRect = new Rect((int) (currentlyInFolderOverlay.get(i).getLeft()),
                    (int) (currentlyInFolderOverlay.get(i).getTop()),
                    (int) (currentlyInFolderOverlay.get(i).getRight()),
                    (int) (currentlyInFolderOverlay.get(i).getBottom()));

            if (outRect.contains((int) event.getX() + scroller.getScrollX(), (int) (event.getY()) + scroller.getScrollY())) {

                if (!currentlyInFolderOverlay.get(i).getAppName().equals(currentlyInFolderOverlay.get(i).getAppPackage())) {

                    if (settingsMode) {
                        unsetAppInEdit();
                        setAppInEdit(currentlyInFolderOverlay.get(i));
                    } else {
                        folderOverlay.removeAllViews();
                        try {
                            Intent intent = getPackageManager().getLaunchIntentForPackage(currentlyInFolderOverlay.get(i).getAppPackage());
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                } else {
                    //start settings
                    if (currentlyInFolderOverlay.get(i).getAppPackage().equals(SETTINGS_KEY)) {

                        if (!settingsMode) {
                            //showSettings(getSupportFragmentManager());
                        } else {
                            setAppInEdit(currentlyInFolderOverlay.get(i));
                        }
                    }
                }
            }
        }
    }

    public void closeFolder() {

        //folderOverlay.setOnClickListener(null);
        folderOverlay.setClickable(true);



        designButton.hide();

        for (int i = 0; i < currentlyInFolderOverlay.size(); i++) {
            for(MaterialBadgeTextView badge : badges){
                if(badge.getAppPackage().equals(currentlyInFolderOverlay.get(i).getAppPackage())){
                    badge.setVisibility(View.GONE);
                }
            }
            currentlyInFolderOverlay.get(i).setDisappearAnim(getScalePosition(currentlyInFolderOverlay.get(i)));

            currentlyInFolderOverlay.get(i).disappear();
        }

        int x = scroller.getScrollX();
        int y = scroller.getScrollY();
        Rect windowRect = new Rect();

        layout.getWindowVisibleDisplayFrame(windowRect);

        final int hWindow = windowRect.height();
        final int wWindow = windowRect.width();

        final Point size = getSize(layout);
        if((x+wWindow) > size.x +asize*2){
            scroller.post(new Runnable() {
                public void run() {
                    scroller.smoothScrollTo(size.x-wWindow+asize*2, 0);
                }
            });
        }



        if((y+hWindow) > size.y + asize*2){
            scroller.post(new Runnable() {
                public void run() {
                    scroller.smoothScrollTo(size.y-hWindow+asize*2, 0);
                }
            });
        }


        AlphaAnimation animation1 = new AlphaAnimation(1.0f, 0.0f);
        animation1.setDuration(100);
        animation1.setStartOffset(0);
        animation1.setFillAfter(true);
        folderLabel.startAnimation(animation1);

        ValueAnimator colorAnim = ObjectAnimator.ofInt(folderOverlay, "backgroundColor", Color.argb(180, 0, 0, 0), Color.TRANSPARENT);
        colorAnim.setDuration(100);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                folderOpen = false;
                currentFolder = null;
                folderOverlay.setVisibility(View.GONE);
                currentlyInFolderOverlay.clear();
                folderOverlay.removeAllViews();
            }
        }, 100);
    }


    public void closeFolderFast(){
        designButton.hide();
        folderOpen = false;
        currentFolder = null;
        folderOverlay.setVisibility(View.GONE);
        currentlyInFolderOverlay.clear();
        folderOverlay.removeAllViews();
    }


    public void openFolder(FolderView folder) {
        folderOpen = true;
        folderOverlay.setVisibility(View.VISIBLE);

        folderOverlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFolder();
            }
        });

        folderOverlay.bringToFront();

        Point folderPos = rasterToPixel(folder.getPosition(), asize, padding,getApplicationContext());

        ArrayList<AppView> appsInFolder = getAppsInFolder(folder.getFolderName(), getApplicationContext(),asize);

        currentFolder = folder.getFolderName();

        ArrayList<Point> points = getFolderPoints(appsInFolder.size());

        for (int n = 0; n < appsInFolder.size(); n++) {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);

            Point pxPoint = rasterToPixel(points.get(n), asize, padding,getApplicationContext());

            params.leftMargin = pxPoint.x + folderPos.x;
            params.topMargin = pxPoint.y + folderPos.y;

            appsInFolder.get(n).setLayoutParams(params);
            appsInFolder.get(n).setVisibility(View.VISIBLE);
            folderOverlay.addView(appsInFolder.get(n));

            final String package_string = appsInFolder.get(n).getAppPackage();

            appsInFolder.get(n).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openApp(package_string);
                }
            });

            currentlyInFolderOverlay.add(appsInFolder.get(n));
        }

        folderLabel = new TextView(getApplicationContext());
        folderLabel.setTextColor(Color.WHITE);
        folderOverlay.addView(folderLabel);

        folderOverlay.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                for (AppView app : currentlyInFolderOverlay) {
                    int[] a = new int[2];
                    app.getLocationOnScreen(a);
                }
                if (!settingsMode) {
                    checkScale();
                }
            }
        });

        for (final AppView app : currentlyInFolderOverlay) {
            scaleNonText(app,getScaleOfApp(app));
            app.setAppearAnim(getScalePosition(app));
            app.appear();


            app.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    folderOverlay.setVisibility(View.GONE);

                    // create drag obj

                    ClipData data = ClipData.newPlainText(app.getAppPackage(), "");
                    data.addItem(new ClipData.Item(app.getAppPackage()));
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                            app);
                    shadowBuilder.getView().setAlpha(1f);


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        app.startDragAndDrop(data, shadowBuilder, app, 0);
                    } else {
                        app.startDrag(data, shadowBuilder, app, 0);
                    }


                    dragging = true;

                    deleteButton.hide();

                    if(!settingsMode)
                        toggleSettingsMode();

                    return true;
                }
            });

            /*AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
            animation1.setDuration(500);
            animation1.setStartOffset(0);
            animation1.setFillAfter(true);
            app.startAnimation(animation1);*/
        }

        RelativeLayout.LayoutParams layouttextParams = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layouttextParams.leftMargin = folderPos.x;
        layouttextParams.topMargin = folderPos.y - (int) (asize * 1.5f);
        folderLabel.setLayoutParams(layouttextParams);
        folderLabel.setText(folder.getFolderName());
        folderLabel.setTextSize(TypedValue.COMPLEX_UNIT_PX, asize);
        AlphaAnimation animation1 = new AlphaAnimation(0.0f, 1.0f);
        animation1.setDuration(500);
        animation1.setStartOffset(0);
        animation1.setFillAfter(true);

        folderLabel.startAnimation(animation1);
        ValueAnimator colorAnim = ObjectAnimator.ofInt(folderOverlay, "backgroundColor", Color.TRANSPARENT, Color.argb(180, 0, 0, 0));
        colorAnim.setDuration(500);
        colorAnim.setEvaluator(new ArgbEvaluator());
        colorAnim.start();
    }


    float vx;
    float vy;
    float friction = 0.9f;
    private Handler handler = new Handler();

    public void velocityScroll(float nvx, float nvy) {
        vx = nvx;
        vy = nvy;

        //handler.postDelayed(runnable, 0);
        //handler.postDelayed(runnable, 0);


        if (nvx > 120)
            nvx = 120;

        if (nvx < -120)
            nvx = -120;

        if (nvy > 120)
            nvy = 120;

        if (nvy < -120)
            nvy = -120;

        ValueAnimator animation = ValueAnimator.ofFloat(nvx, 0);
        animation.setDuration(4 * (int) (Math.abs(nvx)));
        animation.setInterpolator(new MyInterpolator());
        animation.start();
        animation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scroller.scrollBy((int) ((float) valueAnimator.getAnimatedValue()), 0);
                if (!settingsMode) {
                    checkScale();
                }
            }
        });

        ValueAnimator animationy = ValueAnimator.ofFloat(nvy, 0);
        animationy.setDuration(4 * (int) (Math.abs(nvy)));
        animationy.setInterpolator(new MyInterpolator());
        animationy.start();
        animationy.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scroller.scrollBy(0, (int) ((float) valueAnimator.getAnimatedValue()));
                if (!settingsMode) {
                    checkScale();
                }
            }
        });

    }

    public boolean vcalc() {
        vx = vx * friction;
        vy = vy * friction;
        scroller.scrollBy((int) vx, 0);
        scroller.scrollBy(0, (int) vy);
        checkScale();
        if (vx < 0.1 && vy < 0.1) {
            return false;
        } else {
            return true;
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (vcalc())
                handler.postDelayed(this, 1000 / 60);
        }
    };

    public void scrollToPosition(int x, int y) {

        // scroll automatic to the desired position and check the scale of the apps

        Long animationDuration = 1000L;

        // init the path for the animation

        int xStart = scroller.getScrollX();
        int xEnd = x;
        int yStart = scroller.getScrollY();
        int yEnd = y;

        // animate horizontal movement

        ValueAnimator x_animator = ValueAnimator.ofInt(xStart, xEnd);
        x_animator.setDuration(animationDuration);
        x_animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scroller.scrollTo((int) valueAnimator.getAnimatedValue(), scroller.getScrollX());
                checkScale();

            }
        });
        x_animator.start();

        // animate vertical movement

        ValueAnimator y_animator = ValueAnimator.ofInt(yStart, yEnd);
        y_animator.setDuration(animationDuration);
        y_animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                scroller.scrollTo(scroller.getScrollX(), (int) valueAnimator.getAnimatedValue());
                checkScale();
            }
        });
        y_animator.start();
    }

    public void setViewInEdit(View view){

        inEditModeAppPackage = String.valueOf(view.getTag());

        if(!settingsMode)
            toggleSettingsMode();

        unsetAppInEdit();
        unsetFolderInEdit();
        unsetViewInEdit();
        Log.i("set view in edit",inEditModeAppPackage+"package");
        if(inEditModeAppPackage != "___clock"){
            deleteButton.show();
            inEditModeAppPackage = String.valueOf(view.getTag());
            Log.i("asdf","or this is called before");
            widgetSizeButton.show();
            widgetSizeButton.bringToFront();
            widgetSizeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Widget Size");

                    // Set up the input
                    // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text

                    LinearLayout layout = new LinearLayout(getApplicationContext());
                    layout.setOrientation(LinearLayout.VERTICAL);

                    // Add a TextView here for the "Title" label, as noted in the comments
                    final EditText w = new EditText(getApplicationContext());
                    w.setHint("Width");
                    layout.addView(w); // Notice this is an add method

                    // Add another TextView here for the "Description" label
                    final EditText h = new EditText(getApplicationContext());
                    h.setHint("height");
                    layout.addView(h); // Another add method

                    builder.setView(layout); // Again this is a set metho



                    // Set up the buttons
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            int inputw;
                            int inputh;

                            inputw = Integer.parseInt(w.getText().toString());
                            inputh = Integer.parseInt(h.getText().toString());

                            for(View view : editableViews){
                                if(String.valueOf(view.getTag()).equals(String.valueOf(inEditModeAppPackage))){
                                    widgetManager.updateWidgetSize(inEditModeAppPackage,inputw,inputh);

                                    setViewSize(inEditModeAppPackage,new Point(inputw,inputh));
                                }
                            }

                            hideKeyboard(MainActivity.this);
                        }
                    });
                    builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });

                    builder.show();

                }
            });

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
            params.leftMargin = view.getRight()-asize/4;
            params.topMargin = view.getBottom()-asize/4;
            widgetSizeButton.setLayoutParams(params);

            view.setBackgroundColor(Color.argb(200, 255, 255, 255));

            ClipData data = ClipData.newPlainText(String.valueOf(view.getTag()), "");
            data.addItem(new ClipData.Item(String.valueOf(view.getTag())));
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    view);
            shadowBuilder.getView().setAlpha(1f);

            dragging = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.startDragAndDrop(data, shadowBuilder, view, 0);
            } else {
                view.startDrag(data, shadowBuilder, view, 0);
            }
        }else{

            clock.setBackgroundColor(Color.argb(200, 255, 255, 255));
            deleteButton.hide();
            widgetSizeButton.hide();


            ClipData data = ClipData.newPlainText(String.valueOf(view.getTag()), "");
            data.addItem(new ClipData.Item(String.valueOf(view.getTag())));
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(
                    clock);
            shadowBuilder.getView().setAlpha(1f);

            dragging = true;

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                clock.startDragAndDrop(data, shadowBuilder, clock, 0);
            } else {
                clock.startDrag(data, shadowBuilder, clock, 0);
            }

        }
    }

    public void unsetViewInEdit(){
        if(clock != null){
            clock.setBackgroundColor(Color.argb(0, 255, 255, 255));
        }
        unsetAppInEdit();
        unsetFolderInEdit();
        //inEditModeAppPackage = "";
        for (View v : editableViews) {
            if(v != null){
                v.setBackgroundColor(Color.argb(0, 255, 255, 255));
            }
        }
        widgetSizeButton.hide();
        deleteButton.hide();
    }

    public void setAppInEdit( AppView app) {
        Log.i("setapp",app.getAppPackage()+" "+app.category);
        unsetAppInEdit();
        unsetFolderInEdit();
        unsetViewInEdit();
        app.getBackground().setColorFilter(new PorterDuffColorFilter(Color.GRAY, PorterDuff.Mode.MULTIPLY));
        inEditModeAppPackage = app.getAppPackage();
        deleteButton.setBackground(null);
        deleteButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        deleteButton.show();
        removeFromHomeScreen.show();
        //visibilityButton.setVisibility(View.VISIBLE);
        if(!anchorButton.isOrWillBeHidden())
            anchorButton.hide();
        if(!add_widget_button.isOrWillBeHidden())
            add_widget_button.hide();
        if(!designButton.isOrWillBeHidden())
            designButton.hide();
    }

    public void setFolderInEdit(FolderView folder) {
        unsetFolderInEdit();
        unsetViewInEdit();
        folder.setColorFilter(Color.argb(200, 255, 255, 255));
        String folderName = folder.getFolderName();
        inEditModeAppPackage = folderName;
        designButton.setBackground(null);
        designButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
        designButton.show();
        deleteButton.hide();
        //visibilityButton.setVisibility(View.GONE);
    }

    public void editFolderName(FolderView folder) {
        folderNameChooser(folder);
    }


    public void unsetFolderInEdit() {

        for (AppView app : apps) {
            app.getBackground().clearColorFilter();
        }

        for (FolderView folder : folders) {
            folder.getDrawable().clearColorFilter();
        }

        designButton.hide();
        //inEditModeAppPackage = "";
    }

    public void unsetAppInEdit() {
        for (AppView app : apps) {
            app.getBackground().clearColorFilter();
        }

        for (FolderView folder : folders) {
            folder.getDrawable().clearColorFilter();
        }

        deleteButton.hide();
        //visibilityButton.setVisibility(View.GONE);
        removeFromHomeScreen.hide();
        //inEditModeAppPackage = "";
    }


    public void deleteApp(String packageName) {
        String folder = isAppInFolder(this,packageName);
        Log.i("apps_in_folder", "foldername: "+folder);

        deinstallApp(getApplication(),packageName);
        unsetAppInEdit();

        if(!folder.equals("")){
            for(FolderView f : folders){
                if(f.folderName.equals(folder)){
                    int w = 0;

                    if(PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                            .getString("app_icon_style","circle").equals("hexagon") ||
                            PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                                    .getString("app_icon_style","circle").equals("hexagon-outline")){
                        w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

                    }


                    ArrayList<String> appsInfFolderForIcon = getAppsInFolderAppPackage(
                            f.getFolderName(),
                            getApplicationContext());

                    for(String str : appsInfFolderForIcon){
                        Log.i("apps_in_folder"," now in folder: "+str);
                    }

                    f.setImageBitmap(
                            FolderManager.getHexFolderIcon(
                                    appsInfFolderForIcon,
                                    asize,
                                    asize-w,
                                    getApplicationContext(),
                                    f.getFolderName(),
                                    true
                            )
                    );
                }
            }
        }

        for(final AppView app : apps){
            if(app.getAppPackage().equals(packageName)){
                removeAppFromLayout(app);
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Integer.parseInt(android.os.Build.VERSION.SDK) > 5
                && keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Log.d("CDA", "onKeyDown Called");
            if (settingsMode)
                onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        Log.d("CDA", "onBackPressed Called");
        if (settingsMode)
            toggleSettingsMode();

    }


    private void showSettings() {
        Intent settingIntent = new Intent(this, SettingsActivity2.class);
        //settingIntent.putExtra(SettingsActivity.EXTRA_SHOW_FRAGMENT, SettingsActivity.GeneralPreferenceFragment.class.getName());
        //settingIntent.putExtra(SettingsActivity.EXTRA_NO_HEADERS, true);
        startActivity(settingIntent);
    }

    public float interpolate(Point p, int s) {
        float value = (getSmalestMargin(p, s)) / (3f * s);
        //Log.i("Smallest margin"," "+getSmalestMargin(p,s));
        if (value < 0) {
            return 0;
        } else {
            if (value > 1f) {
                return 1f;
            } else {
                return value;
            }
        }
    }

    public boolean isInsideBoundCutout(View v, int s) {
        Point app_position = getPosition(v);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            if(app_position.x >= (cutout_left-(s*2f)) && app_position.x < (cutout_right+(s*2f)) && app_position.y < (cutout_bottom+(s*2f))){
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    public boolean isInsideBound(View v, int s) {
        Point p = getPosition(v);
        return p.x > s &&
                p.x < width - s * 2f &&
                p.y > s &&
                p.y < height - s * 2f;
    }

    private boolean isViewInside(View v, int s) {
        Point p = getPosition(v);
        return p.x > -s &&
                p.x < width &&
                p.y > -s &&
                p.y < height;
    }

    private Point getPosition(View myView) {
        int p[] = new int[2];
        myView.getLocationOnScreen(p);
        return new Point(p[0], p[1]);
    }

    public void addImagesToView(ArrayList<AppView> toadd) {
        for (AppView img : toadd) {
            Log.i("layout", "add app");
            layout.addView(img);
        }

        Log.i("call checkScale","from: "+"add images to veiw");
        checkScale();
    }

    public void addFoldersToView(ArrayList<FolderView> toadd) {
        for (FolderView view : toadd) {
            Log.i("layout", "add folder");

            layout.addView(view);
        }
        Log.i("call checkScale","from: "+"addfolders to view");
        checkScale();
    }
}
