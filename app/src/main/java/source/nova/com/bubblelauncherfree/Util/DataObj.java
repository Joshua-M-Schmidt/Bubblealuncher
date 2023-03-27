package source.nova.com.bubblelauncherfree.Util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.R;

/**
 * Created by joshua on 16.10.16.
 */

public class DataObj {
    public String name;
    public String package_name;
    public int x;
    public int y;
    public Drawable drawable;
    public int visiblity;
    public String category;
    public boolean selected;

    public DataObj (String name, String package_name, int x, int y){
        this.name = name;
        this.package_name =  package_name;
        this.y = y;
        this.x = x;
    }

    public DataObj (String name, String package_name, int x, int y,String category){
        this.name = name;
        this.package_name =  package_name;
        this.y = y;
        this.x = x;
        this.category = category;
    }

    public DataObj (String name, String package_name, int x, int y, Drawable icon){
        this.name = name;
        this.package_name =  package_name;
        this.y = y;
        this.x = x;
        this.drawable = icon;
    }

    public DataObj (String name, String package_name, int x, int y, Drawable icon, int visiblity){
        this.name = name;
        this.package_name =  package_name;
        this.y = y;
        this.x = x;
        this.drawable = icon;
        this.visiblity = visiblity;
    }

    public void setDrawable(Context ctx){
        try {
            //Log.i("Debug the icon loading", appsInfo.get(i).package_name);
            Log.i("get icon debug",package_name);
            AppManager appManager = new AppManager(ctx);
            Bitmap icon = appManager.getAppIcon(package_name, Util.getDiam(ctx), ctx);
            //Bitmap icon = appManager.getAppIconLabel(appsInfo.get(i).package_name,appsInfo.get(i).iconRes,appsInfo.get(i).name,asize);
            drawable = new BitmapDrawable(ctx.getResources(), icon);
        } catch (NullPointerException e) {
            //todelete.add(appsInfo.get(i));

            drawable = new BitmapDrawable(ctx.getResources(),BitmapFactory.decodeResource(ctx.getResources(), R.drawable.placeholder));
            Log.i("app icon not found", "delte app from list");
        }
    }
}
