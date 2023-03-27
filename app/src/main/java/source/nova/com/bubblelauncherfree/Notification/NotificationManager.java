package source.nova.com.bubblelauncherfree.Notification;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import java.util.ArrayList;

public class NotificationManager {

    private Context ctx;

    public NotificationManager(Context ctx){
        this.ctx = ctx;
    }

    public boolean isAppInDB(String appPackage){
        NotificationDataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new NotificationDataHelper(
                    ctx,
                    NotificationContract.TABLE_NAME,
                    NotificationContract.VERSION,
                    NotificationContract.CREATE,
                    NotificationContract.DELETE);

            apps = helper.read(
                    NotificationContract.TABLE_NAME,
                    new String[]{
                            NotificationContract.APP_PACKAGE

                    },
                    NotificationContract.APP_PACKAGE + " = ? ",new String[]{appPackage},""
            );

            if(apps.moveToFirst()){
                return true;
            }else{
                return false;
            }

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }
    }

    public void printtDB(){
        ArrayList<Notification> all = getAllEntries();
        for(Notification not : all){
            Log.i("badge print",not.toString());
        }
    }

    public void addEntry(String appPackage){

        NotificationDataHelper helper = null;

        try{
            helper = new NotificationDataHelper(
                    ctx,
                    NotificationContract.TABLE_NAME,
                    NotificationContract.VERSION,
                    NotificationContract.CREATE,
                    NotificationContract.DELETE
            );

            ContentValues cv = new ContentValues();
            cv.put(NotificationContract.APP_PACKAGE,appPackage);
            cv.put(NotificationContract.NOTIFICATION_COUNT,1);

            helper.write(
                    NotificationContract.TABLE_NAME,
                    cv,
                    "",
                    new String[]{
                            NotificationContract.APP_PACKAGE,
                            NotificationContract.NOTIFICATION_COUNT
                    },
                    "",
                    new String[]{},
                    ""
            );

        }finally {
            if(helper != null)
                helper.close();
        }
    }

    public int getNotificationCount(String appPackage){
        NotificationDataHelper helper = null;
        Cursor notification = null;

        try{
            helper = new NotificationDataHelper(
                    ctx,
                    NotificationContract.TABLE_NAME,
                    NotificationContract.VERSION,
                    NotificationContract.CREATE,
                    NotificationContract.DELETE
            );


            notification = helper.read(
                    NotificationContract.TABLE_NAME,
                    new String[]{
                            NotificationContract.NOTIFICATION_COUNT
                    },
                    NotificationContract.APP_PACKAGE+" = ? ",
                    new String[]{appPackage},
                    ""
            );

            if(notification.moveToFirst()){
                return notification.getInt(notification.getColumnIndex(NotificationContract.NOTIFICATION_COUNT));
            }else{
                return 0;
            }

        }finally {
            if(helper != null)
                helper.close();
            if(notification != null)
                notification.close();
        }
    }

    public void updateEntry(String appPackage,Boolean increase){

        NotificationDataHelper helper = null;

        try{
            helper = new NotificationDataHelper(
                    ctx,
                    NotificationContract.TABLE_NAME,
                    NotificationContract.VERSION,
                    NotificationContract.CREATE,
                    NotificationContract.DELETE
            );


            if(isAppInDB(appPackage)){
                ContentValues cv = new ContentValues();
                if(increase){
                    cv.put(NotificationContract.NOTIFICATION_COUNT,getNotificationCount(appPackage)+1);
                }else{
                    int count = getNotificationCount(appPackage)-1;
                    if(count < 0)
                        cv.put(NotificationContract.NOTIFICATION_COUNT,0);
                    else
                        cv.put(NotificationContract.NOTIFICATION_COUNT,count);
                }
                helper.update(
                        NotificationContract.TABLE_NAME,
                        cv,
                        NotificationContract.APP_PACKAGE+" = ?",
                        new String[]{
                                appPackage
                        }
                );
            }else{
                addEntry(appPackage);
            }

        }finally {
            if(helper != null){
                helper.close();
            }
        }
    }

    public ArrayList<Notification> getAllEntries(){

        ArrayList<Notification> ret = new ArrayList<>();

        NotificationDataHelper helper = null;
        Cursor notification = null;

        try{
            helper = new NotificationDataHelper(
                    ctx,
                    NotificationContract.TABLE_NAME,
                    NotificationContract.VERSION,
                    NotificationContract.CREATE,
                    NotificationContract.DELETE
            );


            notification = helper.read(
                    NotificationContract.TABLE_NAME,
                    new String[]{
                            NotificationContract.NOTIFICATION_COUNT,
                            NotificationContract.APP_PACKAGE
                    },
                    "",
                    new String[]{},
                    ""
            );

            if(notification.moveToFirst()){
                while(!notification.isAfterLast()){
                    ret.add(new Notification(
                            notification.getString(notification.getColumnIndex(NotificationContract.APP_PACKAGE)),
                            notification.getInt(notification.getColumnIndex(NotificationContract.NOTIFICATION_COUNT))
                    ));

                    notification.moveToNext();
                }
            }

            return ret;

        }finally {
            if(helper != null)
                helper.close();
            if(notification != null)
                notification.close();
        }
    }

    public void removeNotifications(String appPackage){
        NotificationDataHelper helper = null;

        try{
            helper = new NotificationDataHelper(
                    ctx,
                    NotificationContract.TABLE_NAME,
                    NotificationContract.VERSION,
                    NotificationContract.CREATE,
                    NotificationContract.DELETE
            );

            if(isAppInDB(appPackage)){
                ContentValues cv = new ContentValues();
                cv.put(NotificationContract.NOTIFICATION_COUNT,0);
                helper.update(
                        NotificationContract.TABLE_NAME,
                        cv,
                        NotificationContract.APP_PACKAGE+" = ?",
                        new String[]{
                                appPackage
                        }
                );
            }

        }finally {
            if(helper != null){
                helper.close();
            }
        }
    }
}
