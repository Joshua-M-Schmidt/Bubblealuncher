package source.nova.com.bubblelauncherfree.Util;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;

public class DBCleaner {

    public static void checkForDuplicates(Context ctx){
        ArrayList<DataObj> apps = AppManager.getAllAppsFromDB(ctx);
        for(DataObj appA : apps){
            for(DataObj appB : apps){
                if(appA.package_name.equals(appB.package_name)){
                    Log.i("duplicate Found", appA.package_name);
                }
            }
        }
    }
}
