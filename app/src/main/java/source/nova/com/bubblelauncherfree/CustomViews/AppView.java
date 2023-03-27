package source.nova.com.bubblelauncherfree.CustomViews;

import android.content.Context;
import android.icu.util.LocaleData;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Random;

import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.Notification.Notification;
import source.nova.com.bubblelauncherfree.Notification.NotificationManager;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;


import static source.nova.com.bubblelauncherfree.AppManager.AppManager.updateAppPosition;
import static source.nova.com.bubblelauncherfree.MainActivity.asize;
import static source.nova.com.bubblelauncherfree.SettingsActivities.BadgeSettingsActivity.BADGE_BACKGROUND_COLOR;
import static source.nova.com.bubblelauncherfree.SettingsActivities.BadgeSettingsActivity.BADGE_MINIMAL_MODE;


/**
 * Created by joshua on 07.09.16.
 */
public class AppView extends RelativeLayout {

    private String appPackage;
    public String appName;
    private Float appScale;
    private Point position;
    public String category;

    private Random r = new Random();
    private RotateAnimation shake_anim;
    public Animation anim1;
    private Context context;
    public MaterialBadgeTextView badge;
    NotificationManager notificationManager;

    public AppView(Context context, String appPackage, String appName, Point position) {
        super(context);
        this.appPackage = appPackage;
        this.appName = appName;
        this.position = position;
        this.context = context;

        setLeft(position.y);
        setTop(position.x);

        shake_anim = new RotateAnimation(
                -2.5f,
                2.5f,
                asize/4, asize/4
        );
        shake_anim.setDuration(100);
        shake_anim.setStartOffset((long)r.nextInt(50));
        shake_anim.setRepeatCount(Animation.INFINITE);
        shake_anim.setRepeatMode(Animation.REVERSE);

        notificationManager = new NotificationManager(context);
        updateBadge();
    }

    private void addBadgeCounter(){
        int count = notificationManager.getNotificationCount(this.appPackage);
        badge = new MaterialBadgeTextView(context);
        addView(badge);
        badge.setBackgroundColor(PreferenceManager.getDefaultSharedPreferences(context).getInt(BADGE_BACKGROUND_COLOR,0xffffffff));
        if(PreferenceManager.getDefaultSharedPreferences(context).getBoolean(BADGE_MINIMAL_MODE,true)){
            badge.setHighLightMode();
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize/4, asize/4);
            params.topMargin = asize/20;
            params.leftMargin = asize/20;
            badge.setLayoutParams(params);
        }else{
            //badge.clearHighLightMode();
            badge.setBadgeCount(count);
        }
        badge.setAppPackage(getAppPackage());

    }

    public void updateBadge(){
        int count = notificationManager.getNotificationCount(this.appPackage);
        if(count > 0){
            if(badge == null){
                addBadgeCounter();
            }else{
                badge.setBadgeCount(count);
            }
        }else{
            return;
        }
    }

    public void setAppearAnim(ArrayList<Float> getScalePosition){
        this.setVisibility(VISIBLE);

        float x = 0.5f;
        float y = 0.5f;

        //Point p = getPosition(v);
        //String site = getSite(p,(int) (asize*v.getScaleX()));


        ArrayList<Float> pos = getScalePosition;

        anim1 = new ScaleAnimation(
                0f, getAppScale(), // Start and end values for the Y axis scaling
                0f, getAppScale() // Start and end values for the Y axis scaling
                , // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, pos.get(0), // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, pos.get(1)); // Pivot point of Y scaling

        anim1.setDuration(500);
        anim1.setFillAfter(true); // Needed to keep the result of the animation
    }

    public void setDisappearAnim(ArrayList<Float> getScalePosition){
        this.setVisibility(INVISIBLE);

        float x = 0.5f;
        float y = 0.5f;

        //Point p = getPosition(v);
        //String site = getSite(p,(int) (asize*v.getScaleX()));

        ArrayList<Float> pos = getScalePosition;

        anim1 = new ScaleAnimation(
                getAppScale(), 0f, // Start and end values for the Y axis scaling
                getAppScale(), 0f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, pos.get(0), // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, pos.get(1)); // Pivot point of Y scaling

        anim1.setDuration(100);
        anim1.setFillAfter(true); // Needed to keep the result of the animation
    }

    public void disappear(){
        this.startAnimation(anim1);
    }

    public void appear(){
        this.startAnimation(anim1);
    }

    public Point getPosition(){
        return this.position;
    }

    public void setPosition(Point position,Context ctx){
        updateAppPosition(position,getContext(),getAppPackage());

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);

        Point pxpoint = Util.rasterToPixel(position, asize,MainActivity.padding,ctx);

        params.leftMargin = pxpoint.x;
        params.topMargin = pxpoint.y;

        setLayoutParams(
                params
        );

        this.position = position;
    }

    public void shake(){
        this.startAnimation(shake_anim);
    }

    public void stopShake(){
        this.shake_anim.cancel();
    }

    public String getAppPackage() {
        return appPackage;
    }

    public String getAppName() {
        return appName;
    }

    public Float getAppScale() {
        return appScale;
    }

    public void setAppScale(Float appScale) {
        this.appScale = appScale;
    }
}
