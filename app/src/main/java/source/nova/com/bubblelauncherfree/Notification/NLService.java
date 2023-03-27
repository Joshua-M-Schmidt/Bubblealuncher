package source.nova.com.bubblelauncherfree.Notification;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;

import java.util.Calendar;
import java.util.concurrent.Semaphore;

public class NLService extends NotificationListenerService {

    NotificationManager manager;

    SharedPreferences prefs;
    SharedPreferences.Editor editor;

    @Override
    public void onCreate() {
        super.onCreate();

        prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = prefs.edit();

        manager = new NotificationManager(getApplicationContext());
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {
        if ((sbn.getNotification().flags & android.app.Notification.FLAG_GROUP_SUMMARY) != 0) {
            //Ignore the notification
            return;
        }


        if ((sbn.getNotification().flags & Notification.FLAG_NO_CLEAR) != 0) {
            //Ignore the notification
            return;
        }

        if(sbn.isClearable() && !sbn.isOngoing()){

            // update db

            manager.updateEntry(sbn.getPackageName(),true);

            // alert maincativitiy

            Intent i = new  Intent("com.source.nova.NOTIFICATION_LISTENER_EXAMPLE");
            i.putExtra("notification_event",sbn.getPackageName());
            i.putExtra("removed",false);
            sendBroadcast(i);
        }else{

            // No badge for unclearable notification

        }
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {

        // update db

        manager.updateEntry(sbn.getPackageName(),false);

        // alert mainactivity

        Intent i = new  Intent("com.source.nova.NOTIFICATION_LISTENER_EXAMPLE");
        i.putExtra("notification_event",sbn.getPackageName());
        i.putExtra("removed",true);
        sendBroadcast(i);
    }

}
