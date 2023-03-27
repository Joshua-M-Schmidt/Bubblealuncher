package source.nova.com.bubblelauncherfree.AppManager;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.AdaptiveIconDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.annotation.RequiresApi;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import source.nova.com.bubblelauncherfree.CustomViews.AppView;
import source.nova.com.bubblelauncherfree.CustomViews.FolderView;
import source.nova.com.bubblelauncherfree.FolderManager.FolderManager;
import source.nova.com.bubblelauncherfree.IconPackSettings.IconPackActivity;
import source.nova.com.bubblelauncherfree.SettingsActivities.OrderSlide;
import source.nova.com.bubblelauncherfree.R;
import source.nova.com.bubblelauncherfree.Util.AppSaveInfo;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.IconPackManager;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.composeRoundedRectPath;
import static source.nova.com.bubblelauncherfree.SettingsActivities.IconSettingsActivity.BUBBLE_CROP;
import static source.nova.com.bubblelauncherfree.SettingsActivities.IconSettingsActivity.BUBBLE_SATURATION;

/**
 * Created by joshua on 07.09.16.
 */
public class AppManager {

    private Context context;

    DiskLruImageCache cache;

    public AppManager(Context ctx){
        this.context = ctx;
        cache = new DiskLruImageCache(ctx,
                "app_icons_new",
                1024 * 1024 * 10 /* 10 mb */,
                Bitmap.CompressFormat.PNG,
                100);
    }

    public static void removeFolderAttrFromApp(Context ctx, String appPackageName){
        DataHelper helper = null;

        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE
            );

            ContentValues cv = new ContentValues();
            cv.putNull(AppContract.BELONGS_TO_FOLDER);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+" = ?",
                    new String[]{
                            appPackageName
                    }
            );
        }finally {
            if (helper != null)
                helper.close();
        }
    }

    public static void updateAppPosition(Point newPosition, Context ctx, String appPackageName){
        DataHelper helper = null;

        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE
            );

            Log.i("UpdateAppPosition","x: "+newPosition.x+" y: "+newPosition.y);

            ContentValues cv = new ContentValues();
            cv.put(AppContract.Y_POSITION,newPosition.y);
            cv.put(AppContract.X_POSITION,newPosition.x);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+" = ?",
                    new String[]{
                            appPackageName
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
        }
    }

    public static void updateFolderName(String folder, Context ctx, String newName){
        DataHelper helper = null;
        Cursor read = null;

        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE
            );

            read = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_PACKAGE
                    },
                    AppContract.BELONGS_TO_FOLDER+" = ?",
                    new String[]{
                            folder
                    },
                    ""
            );

            if(read.moveToFirst()){
                while(!read.isAfterLast()){
                    ContentValues cv = new ContentValues();
                    cv.put(AppContract.BELONGS_TO_FOLDER,newName);

                    helper.update(
                            AppContract.TABLE_NAME,
                            cv,
                            AppContract.APP_PACKAGE+" = ?",
                            new String[]{
                                    read.getString(read.getColumnIndex(AppContract.APP_PACKAGE))
                            }
                    );
                    read.moveToNext();
                }
            }



        }finally {
            if(helper != null)
                helper.close();
            if(read != null)
                read.close();
        }
    }

    public static void updateFolder(String folder, Context ctx, String appPackageName){
        DataHelper helper = null;

        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE
            );

            ContentValues cv = new ContentValues();
            cv.put(AppContract.BELONGS_TO_FOLDER,folder);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+" = ?",
                    new String[]{
                            appPackageName
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
        }
    }

    public AppView getAppViewByPackage(String appPackage,Context ctx, int asize){
        DataObj obj = getDataObjFromPackage(appPackage,ctx,new Point(0,0));

        AppView ret = new AppView(ctx,
                appPackage,
                obj.name,
                new Point(obj.x,obj.y));

        try{
            //Log.i("Debug the icon loading", appsInfo.get(i).package_name);
            Log.i("get icon debug",obj.package_name);
            Bitmap icon = getAppIcon(obj.package_name, asize, ctx);
            ret.setBackground(new BitmapDrawable(ctx.getResources(),icon));
        }catch (NullPointerException e){
            //todelete.add(appsInfo.get(i));

            ret.setBackgroundResource(R.drawable.placeholder);
            Log.i("app icon not found", "delte app from list");
        }

        return ret;
    }

    public static void initAppPosition(Context ctx,ArrayList<DataObj> apps){

        //ArrayList<Point> appPositions = getPointsGroups(
        //        apps.size());



        //ArrayList<Point> appPositions = getAlphabetgroupsPositioned(apps);

        ArrayList<Point> patternIsland = new ArrayList<>();
        patternIsland.add(new Point(1,0));
        patternIsland.add(new Point(2,0));
        patternIsland.add(new Point(0,1));
        patternIsland.add(new Point(1,1));
        patternIsland.add(new Point(2,1));
        patternIsland.add(new Point(1,2));
        patternIsland.add(new Point(2,2));

        ArrayList<Point> patternSmallPyramid = new ArrayList<>();
        patternSmallPyramid.add(new Point(1,0));
        patternSmallPyramid.add(new Point(0,1));
        patternSmallPyramid.add(new Point(1,1));
        patternSmallPyramid.add(new Point(0,2));
        patternSmallPyramid.add(new Point(1,2));
        patternSmallPyramid.add(new Point(2,2));

        ArrayList<Point> patternBigPyramid = new ArrayList<>();
        patternBigPyramid.add(new Point(2,0));
        patternBigPyramid.add(new Point(1,1));
        patternBigPyramid.add(new Point(2,1));
        patternBigPyramid.add(new Point(1,2));
        patternBigPyramid.add(new Point(3,2));
        patternBigPyramid.add(new Point(0,3));
        patternBigPyramid.add(new Point(3,3));
        patternBigPyramid.add(new Point(1,5));
        patternBigPyramid.add(new Point(2,5));
        patternBigPyramid.add(new Point(1,4));
        patternBigPyramid.add(new Point(3,4));

        ArrayList<Point> patternRaute = new ArrayList<>();
        patternRaute.add(new Point(1,0));
        patternRaute.add(new Point(0,1));
        patternRaute.add(new Point(1,1));
        patternRaute.add(new Point(0,2));
        patternRaute.add(new Point(2,2));
        patternRaute.add(new Point(0,3));
        patternRaute.add(new Point(1,3));

        ArrayList<Point> appPositions;

        switch (PreferenceManager.getDefaultSharedPreferences(ctx).getString(OrderSlide.PATTERN,OrderSlide.PATTERN_CLASSIC)){
            case OrderSlide.PATTERN_CLASSIC:
                appPositions = getPoints(apps.size());
                break;
            default:
                appPositions = getPoints(apps.size());
                break;
        }

        //ArrayList<Point> appPositions = getPointsFromPattern(apps.size(),patternSmallPyramid, true);
        //ArrayList<Point> appPositions = getPointsFromPattern(apps.size(),patternBigPyramid, false);



        Log.i("initAppPosition size",apps.size()+" ");

        for(int i = 0; i < apps.size(); i++){

            Log.i("initAppPosition","x: "+appPositions.get(i).x+" y: "+appPositions.get(i).y);

            writeAppPosition(
                    apps.get(i).package_name,
                    appPositions.get(i).x,
                    appPositions.get(i).y,
                    ctx);
        }
    }

    public static ArrayList<Point> getTwoIslandPattern(){
        ArrayList<Point> pattern = new ArrayList<>();
        pattern.add(new Point(0,8));
        pattern.add(new Point(1,8));
        pattern.add(new Point(3,8));
        pattern.add(new Point(4,8));

        pattern.add(new Point(0,1));
        pattern.add(new Point(1,1));
        pattern.add(new Point(3,1));
        pattern.add(new Point(4,1));
        pattern.add(new Point(3,5));

        pattern.add(new Point(1,2));
        pattern.add(new Point(3,2));
        pattern.add(new Point(4,2));
        pattern.add(new Point(3,4));//center
        pattern.add(new Point(6,2));

        pattern.add(new Point(5,3));
        pattern.add(new Point(6,3));
        pattern.add(new Point(7,3));

        pattern.add(new Point(2,4));

        pattern.add(new Point(5,4));

        pattern.add(new Point(0,5));
        pattern.add(new Point(2,5));
        pattern.add(new Point(6,5));

        pattern.add(new Point(0,6));
        pattern.add(new Point(1,6));
        pattern.add(new Point(3,6));
        pattern.add(new Point(5,6));

        pattern.add(new Point(0,7));
        pattern.add(new Point(1,7));
        pattern.add(new Point(4,7));
        pattern.add(new Point(5,7));


        return pattern;
    }

    public static ArrayList<Point> getScatteredPattern(){
        ArrayList<Point> pattern = new ArrayList<>();

        pattern.add(new Point(0,1));
        pattern.add(new Point(1,1));
        pattern.add(new Point(2,1));

        pattern.add(new Point(4,2));

        pattern.add(new Point(0,3));
        pattern.add(new Point(1,3));
        pattern.add(new Point(3,3));
        pattern.add(new Point(4,3));
        pattern.add(new Point(5,3));

        pattern.add(new Point(0,4));
        pattern.add(new Point(1,4));
        pattern.add(new Point(2,4));
        pattern.add(new Point(3,4));

        pattern.add(new Point(0,5));
        pattern.add(new Point(1,5));
        pattern.add(new Point(3,5));
        pattern.add(new Point(4,5));
        pattern.add(new Point(5,5));

        pattern.add(new Point(1,7));
        pattern.add(new Point(2,7));
        pattern.add(new Point(3,7));

        pattern.add(new Point(1,8));
        pattern.add(new Point(2,8));
        pattern.add(new Point(3,8));
        pattern.add(new Point(4,8));
        return pattern;
    }

    public static ArrayList<Point> getBigPatternRectangle(){
        ArrayList<Point> patternRect = new ArrayList<>();
        patternRect.add(new Point(0,0));
        patternRect.add(new Point(1,0));
        patternRect.add(new Point(2,0));
        patternRect.add(new Point(3,0));

        patternRect.add(new Point(0,1));
        patternRect.add(new Point(1,1));
        patternRect.add(new Point(2,1));
        patternRect.add(new Point(3,1));

        patternRect.add(new Point(0,2));
        patternRect.add(new Point(1,2));
        patternRect.add(new Point(2,2));
        patternRect.add(new Point(3,2));

        patternRect.add(new Point(0,3));
        patternRect.add(new Point(1,3));
        patternRect.add(new Point(2,3));
        patternRect.add(new Point(3,3));

        patternRect.add(new Point(0,4));
        patternRect.add(new Point(1,4));
        patternRect.add(new Point(2,4));
        patternRect.add(new Point(3,4));

        return patternRect;
    }

    public static ArrayList<Point> getBigPatternIsland(){
        ArrayList<Point> patternIsland = new ArrayList<>();
        patternIsland.add(new Point(1,0));
        patternIsland.add(new Point(2,0));
        patternIsland.add(new Point(3,0));
        patternIsland.add(new Point(1,1));
        patternIsland.add(new Point(2,1));
        patternIsland.add(new Point(3,1));
        patternIsland.add(new Point(4,1));
        patternIsland.add(new Point(0,2));
        patternIsland.add(new Point(1,2));
        patternIsland.add(new Point(2,2));
        patternIsland.add(new Point(3,2));
        patternIsland.add(new Point(4,2));
        patternIsland.add(new Point(1,3));
        patternIsland.add(new Point(2,3));
        patternIsland.add(new Point(3,3));
        patternIsland.add(new Point(4,3));
        patternIsland.add(new Point(1,4));
        patternIsland.add(new Point(2,4));
        patternIsland.add(new Point(3,4));
        return patternIsland;
    }

    public static boolean isAppInDB(Context ctx, String packageName){

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_PACKAGE

                    },
                    AppContract.APP_PACKAGE + " = ? ",new String[]{packageName},""
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

    public static String isAppInFolder(Context ctx, String appPackage){

        Log.i("Version",AppContract.VERSION+" ");

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_PACKAGE,
                            AppContract.BELONGS_TO_FOLDER
                    },
                    AppContract.APP_PACKAGE+ " = ? ",new String[]{appPackage},""
            );

            if(apps.moveToFirst()){
                while(!apps.isAfterLast()){
                    if(apps.getString(apps.getColumnIndex(AppContract.BELONGS_TO_FOLDER)) != null){
                        return apps.getString(apps.getColumnIndex(AppContract.BELONGS_TO_FOLDER));
                    }
                    apps.moveToNext();
                }
            }
        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }

        return "";
    }

    public static void writeAppPosition(String packageName,int x, int y, Context ctx){
        DataHelper helper = null;
        Cursor apps = null;

        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE
            );

            Log.i("writeAppPosition","x: "+x+" y: "+y);

            ContentValues cv = new ContentValues();
            cv.put(AppContract.Y_POSITION,y);
            cv.put(AppContract.X_POSITION,x);
            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+" = ?",
                    new String[]{
                            packageName
                    }
            );

        }finally {
            if(helper != null){
                helper.close();
            }

            if(apps != null){
                apps.close();
            }
        }
    }

    public static boolean appIntersection(ArrayList<AppView> apps, Point newPosition){

        for(int i = 0; i < apps.size(); i++){
            if(apps.get(i).getPosition().equals(newPosition)){
                return true;
            }
        }
        return false;
    }

    public static AppView getAppViewIntersection(ArrayList<AppView> apps, Point newPosition){

        Log.i("GetAppViewIntersection","x: "+newPosition.x+" y: "+newPosition.y);

        for(int i = 0; i < apps.size(); i++){
            if(apps.get(i).getPosition().equals(newPosition)){
                Log.i("inters",apps.get(i).getPosition().x+" "+apps.get(i).getPosition()+" "+apps.get(i).getAppPackage());
                Log.i("inters",newPosition.x+" "+newPosition.y);
                return apps.get(i);
            }
        }
        return null;
    }

    public static String getAppIntersection(ArrayList<AppView> apps, Point newPosition){

        Log.i("getAppIntersection","x: "+newPosition.x+" y: "+newPosition.y);

        for(int i = 0; i < apps.size(); i++){
            if(apps.get(i).getPosition().equals(newPosition)){
                return apps.get(i).getAppPackage();
            }
        }
        return null;
    }

    public static DataObj installApp(Context ctx, String appPackage, ArrayList<AppView> appList){
        Point freePoint = getFreePosition(appList);
        DataObj obj = getDataObjFromPackage(appPackage,ctx,freePoint);

        // is app marked as deinstall in db?
        if(isAppInDB(ctx,appPackage)){
            ContentValues cv = new ContentValues();
            cv.put(AppContract.DEINSTALLED,0);

            DataHelper helper = null;
            Cursor apps = null;
            try{
                helper = new DataHelper(
                        ctx,
                        AppContract.TABLE_NAME,
                        AppContract.VERSION,
                        AppContract.CREATE,
                        AppContract.DELETE);

                helper.update(
                        AppContract.TABLE_NAME,
                        cv,
                        AppContract.APP_PACKAGE+ " = ? ",
                        new String[]{
                                appPackage
                        }
                );

            }finally {
                if(helper != null)
                    helper.close();
                if(apps != null)
                    apps.close();
            }

            Log.i("App RE-installed",obj.package_name+" x "+obj.x+" y "+obj.y);
        }else{
            DataHelper helper = null;
            try{
                helper = new DataHelper(
                        ctx,
                        AppContract.TABLE_NAME,
                        AppContract.VERSION,
                        AppContract.CREATE,
                        AppContract.DELETE);

                ContentValues cv = new ContentValues();
                cv.put(AppContract.APP_NAME, obj.name);
                cv.put(AppContract.APP_PACKAGE, obj.package_name);
                cv.put(AppContract.X_POSITION, obj.x);
                cv.put(AppContract.Y_POSITION, obj.y);
                helper.write(
                        AppContract.TABLE_NAME,
                        cv,
                        "",
                        new String[]{
                                AppContract.APP_NAME,
                                AppContract.APP_PACKAGE,
                                AppContract.X_POSITION,
                                AppContract.Y_POSITION
                        },
                        AppContract.APP_PACKAGE+" = ? ",
                        new String[]{
                                obj.package_name
                        },
                        ""
                );

            }finally {
                if(helper != null)
                    helper.close();
            }

            Log.i("App installed",obj.package_name+" x "+obj.x+" y "+obj.y);
        }





        return obj;
    }

    public static boolean isAppInstalled(Context ctx, String packageName){

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_PACKAGE

                    },
                    AppContract.APP_PACKAGE + " = ? "+ " and "+
                    AppContract.DEINSTALLED + " = ? ",new String[]{packageName,"0"},""
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

    public static boolean isAppVisible(Context ctx, String packageName){

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_PACKAGE
                    },
                    AppContract.APP_PACKAGE + " = ? "+ " and "+
                            AppContract.SHOW_APP + " = ? ",new String[]{packageName,"1"},""
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

    public static void deinstallApp(Context ctx, String appPackage){
        ContentValues cv = new ContentValues();
        cv.put(AppContract.DEINSTALLED,1);

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+ " = ? ",
                    new String[]{
                            appPackage
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }
    }

    public static void updatePosition(Context ctx, String appPackage, int x, int y){
        DataHelper helper = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE
            );

            Log.i("updatePosition","x: "+x+" y: "+y);

            ContentValues cv = new ContentValues();
            cv.put(AppContract.X_POSITION, x);
            cv.put(AppContract.Y_POSITION, y);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+" = ? ",
                    new String[]{
                            appPackage
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
        }
    }

    public static void setAppAtFreePosition(Context ctx, String appPackage){
        Point freePosition = getFreePosition(ctx);
        DataHelper helper = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE
            );

            Log.i("updatePosition","x: "+freePosition.x+" y: "+freePosition.y);

            ContentValues cv = new ContentValues();
            cv.put(AppContract.X_POSITION, freePosition.x);
            cv.put(AppContract.Y_POSITION, freePosition.y);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+" = ? ",
                    new String[]{
                            appPackage
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
        }
    }


    public static Point getFreePosition(ArrayList<AppView> AppList){
        Point border = getBorders(AppList);
        return new Point(1,border.y+1);
    }

    public static Point getFreePositionAll(ArrayList<AppView> AppList,ArrayList<FolderView> folders){
        Point border = getBorders(AppList);
        Point border1 = getBordersF(folders);
        Point ret = new Point(0,0);

        if(border.y > border1.y){
            ret.y = border.y +1;
        }else{
            ret.y = border1.y+1;
        }

        for(int row = 2; row < ret.x; row++){
            for(int col = 2; col < ret.y; col++){
                Log.i("is free", row+","+col+" "+isPositionFree(new Point(row,col),AppList,folders));
                if(isPositionFree(new Point(row,col),AppList,folders)){
                    return new Point(row,col);
                }
            }
        }
        return ret;
    }

    public static Point getFreePositionAll1(ArrayList<DataObj> AppList,ArrayList<FolderView> folders){
        Point border = getBordersD(AppList);
        Point border1 = getBordersF(folders);
        Point ret = new Point(0,0);

        if(border.y > border1.y){
            ret.y = border.y +1;
        }else{
            ret.y = border1.y+1;
        }

        for(int row = 2; row < ret.x; row++){
            for(int col = 2; col < ret.y; col++){
                Log.i("is free", row+","+col+" "+isPositionFreeD(new Point(row,col),AppList,folders));
                if(isPositionFreeD(new Point(row,col),AppList,folders)){
                    return new Point(row,col);
                }
            }
        }
        return ret;
    }

    public static boolean isPositionFree(Point position,ArrayList<AppView> apps, ArrayList<FolderView> folders){
        for(AppView app : apps){
            if(app.getPosition().x == position.x && app.getPosition().y == position.y){
                return false;
            }
        }
        for(FolderView folder : folders){
            if(folder.getPosition().x == position.x && folder.getPosition().y == position.y){
                return false;
            }
        }
        return true;
    }

    public static void hasDBCategorys(){

    }

    public static void setAppCategory(Context ctx,String appPackage, String category){
        DataHelper helper = null;

        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE
            );

            ContentValues cv = new ContentValues();
            cv.put(AppContract.APP_CATEGORY,category);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+" = ?",
                    new String[]{
                            appPackage
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
        }
    }

    public static boolean isPositionFreeD(Point position,ArrayList<DataObj> apps, ArrayList<FolderView> folders){
        for(DataObj app : apps){
            if(app.x == position.x && app.y == position.y){
                return false;
            }
        }
        for(FolderView folder : folders){
            if(folder.getPosition().x == position.x && folder.getPosition().y == position.y){
                return false;
            }
        }
        return true;
    }


    public static Point getFreeGroupPos(Point groupSize,ArrayList<AppView> apps, ArrayList<FolderView> folders ){
        Point border = getBorders(apps);
        Point border1 = getBordersF(folders);
        Point ret = new Point(0,0);



        if(border.y > border1.y){
            ret.y = border.y +1;
        }else{
            ret.y = border1.y+1;
        }

        if(border.x > border1.x){
            ret.x = border.x +1;
        }else{
            ret.x = border1.x+1;
        }

        Log.i("GroupPos",ret.toString());

        for(int row = 0; row < ret.x; row++){
            for(int col = 0; col < ret.y; col++){

                boolean posClear = true;

                Log.i("GroupPos", row+","+col+" "+isPositionFree(new Point(row,col),apps,folders));
                if(isPositionFree(new Point(row,col),apps,folders)){
                    for(int gRow = 0; row < groupSize.x; gRow++){
                        for(int gCol = 0; col < groupSize.y; gCol++){
                            Log.i("GroupPos", row+gRow+","+col+gCol+" "+isPositionFree(new Point(row,col),apps,folders));
                            if(!isPositionFree(new Point(row,col),apps,folders)){
                                posClear = false;
                            }
                        }
                    }
                }

                if(posClear){
                    Log.i("GroupPos", "free");
                    return new Point(row,col);
                }
            }
        }

        return ret;
    }

    public static Point getFreePosition(Context ctx){

        ArrayList<DataObj> appList = getAllAppsFromDB(ctx);

        int x = 0;
        int y = 0;

        for(int i = 0; i < appList.size(); i++){
            Point temp = new Point(appList.get(i).x,appList.get(i).y);
            if(temp.x > x)
                x = temp.x;
            if(temp.y > y)
                y = temp.y;
        }

        return new Point(1,y+1);
    }

    public static Point getBorders(ArrayList<AppView> AppList){
        int x = 0;
        int y = 0;

        for(int i = 0; i < AppList.size(); i++){
            Point temp = AppList.get(i).getPosition();
            if(temp.x > x)
                x = temp.x;
            if(temp.y > y)
                y = temp.y;
        }

        return new Point(x,y);
    }

    public static Point getBordersD(ArrayList<DataObj> AppList){
        int x = 0;
        int y = 0;

        for(int i = 0; i < AppList.size(); i++){
            Point temp = new Point(AppList.get(i).x,AppList.get(i).y);
            if(temp.x > x)
                x = temp.x;
            if(temp.y > y)
                y = temp.y;
        }

        return new Point(x,y);
    }

    public static Point getBordersFromGroup(ArrayList<Point> AppList){
        int x = 0;
        int y = 0;

        for(int i = 0; i < AppList.size(); i++){
            Point temp = AppList.get(i);
            if(temp.x > x)
                x = temp.x;
            if(temp.y > y)
                y = temp.y;
        }

        return new Point(x+1,y+1);
    }

    public static Point getBordersF(ArrayList<FolderView> AppList){
        int x = 0;
        int y = 0;

        for(int i = 0; i < AppList.size(); i++){
            Point temp = AppList.get(i).getPosition();
            if(temp.x > x)
                x = temp.x;
            if(temp.y > y)
                y = temp.y;
        }

        return new Point(x,y);
    }


    public static DataObj getDataObjFromPackage(String appPackage, Context ctx, Point position){
        String appName = "";
        try {
            appName = (String) ctx.getPackageManager()
                    .getApplicationLabel(ctx.getPackageManager()
                            .getApplicationInfo(appPackage,PackageManager.GET_META_DATA));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return new DataObj(appName,appPackage, position.x, position.y);
    }

    public static void writeAppsToDB(ArrayList<DataObj> toWrite, Context ctx){
        DataHelper helper = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            for(DataObj obj : toWrite){
                ContentValues cv = new ContentValues();
                cv.put(AppContract.APP_NAME, obj.name);
                cv.put(AppContract.APP_PACKAGE, obj.package_name);
                cv.put(AppContract.X_POSITION,obj.x);
                cv.put(AppContract.Y_POSITION,obj.y);
                helper.write(
                        AppContract.TABLE_NAME,
                        cv,
                        "",
                        new String[]{
                                AppContract.APP_NAME,
                                AppContract.APP_PACKAGE,
                                AppContract.APP_ICON_RES,
                                AppContract.X_POSITION,
                                AppContract.Y_POSITION
                        },
                        AppContract.APP_PACKAGE+" = ? ",
                        new String[]{
                                obj.package_name
                        },
                        ""
                );
            }

        }finally {
            if(helper != null)
                helper.close();
        }
    }

    public static ArrayList<DataObj> getAppsFromDevice1(Context ctx){
        ArrayList<DataObj> ret = new ArrayList<>();

        Intent i = new Intent(Intent.ACTION_MAIN, null);
        i.addCategory(Intent.CATEGORY_LAUNCHER);

        // Set the intent flags
        i.setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK|
                        Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED
        );

        List<ResolveInfo> appInfoList = null;
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            appInfoList = ctx
                    .getPackageManager()
                    .queryIntentActivities(i,PackageManager.ResolveInfoFlags.of(PackageManager.MATCH_ALL));
        }else{
           appInfoList = ctx
                    .getPackageManager()
                    .queryIntentActivities(i,0);
        }


        Log.i("list info", appInfoList.size()+"");

        for(ResolveInfo appInfo : appInfoList){
            Log.d("got app", appInfo.activityInfo.packageName);
            //if((appInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)

            //Drawable icon = ctx.getPackageManager().getApplicationIcon(appInfo.resolvePackageName);
            if(!String.valueOf(appInfo.loadLabel(ctx.getPackageManager())).startsWith("BubbleLauncher")){
                ret.add(new DataObj(
                        String.valueOf(appInfo.loadLabel(ctx.getPackageManager())),
                        appInfo.activityInfo.packageName,
                        0,
                        0
                ));
            }

        }

        return ret;
    }

    public static String getDialerPackageName(Context ctx){
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:123456789"));
        ResolveInfo resolveInfo = ctx.getPackageManager().resolveActivity(i, 0);
        String result = "";
        if (resolveInfo != null) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null) {
                if ("android".equals(activityInfo.packageName)) {
                    // no default activity.. choose first
                    List<ResolveInfo> resolveInfos = ctx.getPackageManager().queryIntentActivities(i, 0);
                    for (ResolveInfo rInfo : resolveInfos) {
                        result = rInfo.activityInfo.applicationInfo.packageName;
                        break;
                    }

                } else {
                    ApplicationInfo appInfo = activityInfo.applicationInfo;

                    if (appInfo != null) {
                        result = appInfo.packageName;
                    }
                }
            }
        }
        return result;
    }

    public static DataObj getDialerObj(Context ctx){
        Intent i = new Intent(Intent.ACTION_DIAL);
        i.setData(Uri.parse("tel:123456789"));
        ResolveInfo resolveInfo = ctx.getPackageManager().resolveActivity(i, 0);
        DataObj result = null;
        if (resolveInfo != null) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null) {
                if ("android".equals(activityInfo.packageName)) {
                    // no default activity.. choose first
                    List<ResolveInfo> resolveInfos = ctx.getPackageManager().queryIntentActivities(i, 0);
                    for (ResolveInfo rInfo : resolveInfos) {
                        result = new DataObj(rInfo.activityInfo.applicationInfo.packageName,
                                rInfo.activityInfo.applicationInfo.name,0,0);
                        break;
                    }
                } else {
                    ApplicationInfo appInfo = activityInfo.applicationInfo;

                    if (appInfo != null) {
                        result = new DataObj(appInfo.packageName,
                                appInfo.name,0,0);
                    }
                }
            }
        }

        return result;
    }

    public static ArrayList<Point> shiftGorup(ArrayList<Point> points, Point p){
        for(int i = 0; i < points.size(); i++){
            points.get(i).x += p.x;
            points.get(i).y += p.y;
        }

        return points;
    }

    public static void hideApp(String appPackage, Context ctx){
        ContentValues cv = new ContentValues();
        cv.put(AppContract.SHOW_APP,0);

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+ " = ? ",
                    new String[]{
                            appPackage
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }
    }

    public static void removeAppFromHomeScreen(String appPackage, Context ctx){
        ContentValues cv = new ContentValues();
        cv.put(AppContract.SHOW_APP,0);

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+ " = ? ",
                    new String[]{
                            appPackage
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }
    }

    public static void addAppToHomeScreen(String appPackage, Context ctx){
        ContentValues cv = new ContentValues();
        cv.put(AppContract.SHOW_APP,1);

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+ " = ? ",
                    new String[]{
                            appPackage
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }
    }

    public static void unhideApp(String appPackage, Context ctx){
        ContentValues cv = new ContentValues();
        cv.put(AppContract.SHOW_APP,1);

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            helper.update(
                    AppContract.TABLE_NAME,
                    cv,
                    AppContract.APP_PACKAGE+ " = ? ",
                    new String[]{
                            appPackage
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }
    }



    public static ArrayList<DataObj> getHiddenAppsFromDB(Context ctx){

        ArrayList<DataObj> ret = new ArrayList<>();

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_NAME,
                            AppContract.APP_PACKAGE,
                            AppContract.APP_ICON_RES,
                            AppContract.X_POSITION,
                            AppContract.Y_POSITION,
                            AppContract.BELONGS_TO_FOLDER,
                            AppContract.SHOW_APP,
                            AppContract.APP_CATEGORY
                    },
                    "",null,""
            );

            if(apps.moveToFirst()){
                while(!apps.isAfterLast()){
                    if(apps.getString(apps.getColumnIndex(AppContract.BELONGS_TO_FOLDER)) == null){
                        if(!apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)).equals("settings")){

                            // check if the app is set invisible

                            if(apps.getInt(apps.getColumnIndex((AppContract.SHOW_APP))) == 0){

                                Log.i("getAppsFromDB","x: "+apps.getInt(apps.getColumnIndex(AppContract.X_POSITION))+" y: "+apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)));

                                ret.add(new DataObj(
                                        apps.getString(apps.getColumnIndex(AppContract.APP_NAME)),
                                        apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)),
                                        apps.getInt(apps.getColumnIndex(AppContract.X_POSITION)),
                                        apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)),
                                        apps.getString(apps.getColumnIndex(AppContract.APP_CATEGORY))
                                ));
                            }
                        }else{
                            deinstallApp(ctx,"settings");
                        }
                    }

                    apps.moveToNext();
                }
            }

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }

        return getAppsSortByAlphabet(ret);
    }

    public static ArrayList<DataObj> getAllApps(Context ctx){
        ArrayList<DataObj> ret = new ArrayList<>();

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_NAME,
                            AppContract.APP_PACKAGE,
                            AppContract.APP_ICON_RES,
                            AppContract.X_POSITION,
                            AppContract.Y_POSITION,
                            AppContract.BELONGS_TO_FOLDER,
                            AppContract.SHOW_APP,
                            AppContract.APP_ON_HOMESCREEN,
                            AppContract.APP_CATEGORY,
                            AppContract.DEINSTALLED
                    },
                    "",null,""
            );


            if(apps.moveToFirst()){
                while(!apps.isAfterLast()){
                    if(!apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)).equals("settings") &&
                            apps.getInt(apps.getColumnIndex(AppContract.DEINSTALLED)) == 0){

                        // check if the app is set invisible

                        Log.i("getAppsFromDB","x: "+apps.getInt(apps.getColumnIndex(AppContract.X_POSITION))+" y: "+apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)));

                        ret.add(new DataObj(
                                apps.getString(apps.getColumnIndex(AppContract.APP_NAME)),
                                apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)),
                                apps.getInt(apps.getColumnIndex(AppContract.X_POSITION)),
                                apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)),
                                apps.getString(apps.getColumnIndex(AppContract.APP_CATEGORY))
                        ));
                    }else{
                        deinstallApp(ctx,"settings");
                    }

                    apps.moveToNext();
                }
            }

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }

        return getAppsSortByAlphabet(ret);
    }

    public static ArrayList<DataObj> getAppsNotOnHomescreen(Context ctx){

        ArrayList<DataObj> ret = new ArrayList<>();

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_NAME,
                            AppContract.APP_PACKAGE,
                            AppContract.APP_ICON_RES,
                            AppContract.X_POSITION,
                            AppContract.Y_POSITION,
                            AppContract.BELONGS_TO_FOLDER,
                            AppContract.SHOW_APP,
                            AppContract.APP_ON_HOMESCREEN,
                            AppContract.APP_CATEGORY,
                            AppContract.DEINSTALLED
                    },
                    "",null,""
            );


            if(apps.moveToFirst()){
                while(!apps.isAfterLast()){
                    if(apps.getString(apps.getColumnIndex(AppContract.BELONGS_TO_FOLDER)) == null){
                        if(!apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)).equals("settings")){

                            // check if the app is set invisible

                            if(apps.getInt(apps.getColumnIndex((AppContract.SHOW_APP))) == 0 &&
                                    apps.getInt(apps.getColumnIndex((AppContract.DEINSTALLED))) == 0){

                                Log.i("getAppsFromDB","x: "+apps.getInt(apps.getColumnIndex(AppContract.X_POSITION))+" y: "+apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)));

                                ret.add(new DataObj(
                                        apps.getString(apps.getColumnIndex(AppContract.APP_NAME)),
                                        apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)),
                                        apps.getInt(apps.getColumnIndex(AppContract.X_POSITION)),
                                        apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)),
                                        apps.getString(apps.getColumnIndex(AppContract.APP_CATEGORY))
                                ));
                            }
                        }else{
                            deinstallApp(ctx,"settings");
                        }
                    }

                    apps.moveToNext();
                }
            }

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }

        return getAppsSortByAlphabet(ret);
    }

    public static ArrayList<DataObj> getAppsFromDB(Context ctx){

        ArrayList<DataObj> ret = new ArrayList<>();

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_NAME,
                            AppContract.APP_PACKAGE,
                            AppContract.APP_ICON_RES,
                            AppContract.X_POSITION,
                            AppContract.Y_POSITION,
                            AppContract.BELONGS_TO_FOLDER,
                            AppContract.SHOW_APP,
                            AppContract.APP_CATEGORY,
                            AppContract.APP_ON_HOMESCREEN,
                            AppContract.DEINSTALLED
                    },
                    "",null,""
            );

            if(apps.moveToFirst()){
                while(!apps.isAfterLast()){
                    if(apps.getString(apps.getColumnIndex(AppContract.BELONGS_TO_FOLDER)) == null){
                        if(!apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)).equals("settings")){

                            // check if the app is set invisible

                            if(apps.getInt(apps.getColumnIndex((AppContract.SHOW_APP))) != 0){
                                if(apps.getInt(apps.getColumnIndex(AppContract.APP_ON_HOMESCREEN)) != 0 &&
                                        apps.getInt(apps.getColumnIndex(AppContract.DEINSTALLED)) == 0){
                                    Log.i("getAppsFromDB","x: "+apps.getInt(apps.getColumnIndex(AppContract.X_POSITION))+" y: "+apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)));
                                    ret.add(new DataObj(
                                            apps.getString(apps.getColumnIndex(AppContract.APP_NAME)),
                                            apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)),
                                            apps.getInt(apps.getColumnIndex(AppContract.X_POSITION)),
                                            apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)),
                                            apps.getString(apps.getColumnIndex(AppContract.APP_CATEGORY))
                                    ));
                                }

                            }
                        }else{
                             deinstallApp(ctx,"settings");
                        }
                    }

                    apps.moveToNext();
                }
            }

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }

        return getAppsSortByAlphabet(ret);
    }

    public static boolean isPackageInList(String appPackage, ArrayList<DataObj> apps){
        for(DataObj app : apps){
            if(app.package_name.equals(appPackage)){
                return true;
            }
        }
        return false;
    }


    public static boolean isPackageInList1(String appPackage, ArrayList<AppView> apps){
        for(AppView app : apps){
            if(app.getAppPackage().equals(appPackage)){
                return true;
            }
        }
        return false;
    }

    public static void convertDB(Context ctx){

        Log.i("convert db","convert it all!!!");

        boolean settingsObj = false;

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_NAME,
                            AppContract.APP_PACKAGE,
                            AppContract.APP_ICON_RES,
                            AppContract.X_POSITION,
                            AppContract.Y_POSITION,
                            AppContract.BELONGS_TO_FOLDER
                    },
                    "",null,""
            );


            if(apps.moveToFirst()){
                while(!apps.isAfterLast()){
                    if(apps.getString(apps.getColumnIndex(AppContract.BELONGS_TO_FOLDER)) == null){
                        if(apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)).equals("settings")){
                            if(!settingsObj){
                                settingsObj = true;
                                deinstallApp(ctx,"settings");
                                apps.moveToFirst();
                            }
                        }
                        if(settingsObj){
                            updateAppPosition(Util.pixelToRaster(new Point(
                                    apps.getInt(apps.getColumnIndex(AppContract.X_POSITION)),
                                    apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION))
                            ),Util.getDiam(ctx),Util.getPadding(ctx),ctx),ctx,apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)));
                        }
                    }
                    apps.moveToNext();
                }
            }

            if(settingsObj){
                FolderManager.convertFolderPoints(ctx);
            }

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }

    }

    public static DataObj getAppFromDB(Context ctx, String packageName){

        ArrayList<DataObj> ret = new ArrayList<>();

        Log.i("Version",AppContract.VERSION+" ");

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_NAME,
                            AppContract.APP_PACKAGE,
                            AppContract.APP_ICON_RES,
                            AppContract.X_POSITION,
                            AppContract.Y_POSITION,
                            AppContract.BELONGS_TO_FOLDER,
                            AppContract.APP_CATEGORY,
                            AppContract.DEINSTALLED
                    },
                    AppContract.APP_PACKAGE+" = ?",
                    new String[]{
                            packageName
                    },""
            );

            if(apps.moveToFirst()){
                while(!apps.isAfterLast()){
                    ret.add(new DataObj(
                            apps.getString(apps.getColumnIndex(AppContract.APP_NAME)),
                            apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)),
                            apps.getInt(apps.getColumnIndex(AppContract.X_POSITION)),
                            apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)),
                            apps.getString(apps.getColumnIndex(AppContract.APP_CATEGORY))
                    ));


                    apps.moveToNext();
                }
            }

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }

        return ret.get(0);
    }

    public static ArrayList<DataObj> getAllAppsFromDB(Context ctx){

        ArrayList<DataObj> ret = new ArrayList<>();

        Log.i("Version",AppContract.VERSION+" ");

        DataHelper helper = null;
        Cursor apps = null;
        try{
            helper = new DataHelper(
                    ctx,
                    AppContract.TABLE_NAME,
                    AppContract.VERSION,
                    AppContract.CREATE,
                    AppContract.DELETE);

            apps = helper.read(
                    AppContract.TABLE_NAME,
                    new String[]{
                            AppContract.APP_NAME,
                            AppContract.APP_PACKAGE,
                            AppContract.APP_ICON_RES,
                            AppContract.X_POSITION,
                            AppContract.Y_POSITION,
                            AppContract.BELONGS_TO_FOLDER,
                            AppContract.APP_CATEGORY,
                            AppContract.DEINSTALLED
                    },
                    "",null,""
            );

            if(apps.moveToFirst()){
                while(!apps.isAfterLast()){
                    if(apps.getInt(apps.getColumnIndex(AppContract.DEINSTALLED)) == 0){
                        ret.add(new DataObj(
                                apps.getString(apps.getColumnIndex(AppContract.APP_NAME)),
                                apps.getString(apps.getColumnIndex(AppContract.APP_PACKAGE)),
                                apps.getInt(apps.getColumnIndex(AppContract.X_POSITION)),
                                apps.getInt(apps.getColumnIndex(AppContract.Y_POSITION)),
                                apps.getString(apps.getColumnIndex(AppContract.APP_CATEGORY))
                        ));
                    }


                    apps.moveToNext();
                }
            }

        }finally {
            if(helper != null)
                helper.close();
            if(apps != null)
                apps.close();
        }

        return getAppsSortByAlphabet(ret);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Bitmap getAppIcon(PackageManager mPackageManager, String packageName, Context ctx) {

        Log.i("app icon"," get if from here");

        try {
            Drawable drawable = mPackageManager.getApplicationIcon(packageName);

            if (drawable instanceof BitmapDrawable) {
                return ((BitmapDrawable) drawable).getBitmap();
            } else if (drawable instanceof AdaptiveIconDrawable) {
                Drawable backgroundDr = null;

                backgroundDr = ((AdaptiveIconDrawable) drawable).getBackground();

                Drawable foregroundDr = ((AdaptiveIconDrawable) drawable).getForeground();

                Drawable[] drr = new Drawable[2];
                drr[0] = backgroundDr;
                drr[1] = foregroundDr;

                if(drr[0] != null){
                    drr[0] = new BitmapDrawable(ctx.getResources(),getCroppedBitmap(drawableToBitmap(drr[0]),ctx));
                }else{
                    Log.i("get icon", "no background available");
                }

                LayerDrawable layerDrawable = new LayerDrawable(drr);

                int width = layerDrawable.getIntrinsicWidth();
                int height = layerDrawable.getIntrinsicHeight();

                Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                Canvas canvas = new Canvas(bitmap);


                Paint p = new Paint();
                p.setColor(Color.WHITE);
                p.setAlpha(100);

                canvas.drawRect(0,0,canvas.getWidth(), canvas.getHeight(),p);


                layerDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                layerDrawable.draw(canvas);



                return bitmap;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static Bitmap updateSat(Bitmap src, float settingSat) {

        int w = src.getWidth();
        int h = src.getHeight();

        Bitmap bitmapResult =
                Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvasResult = new Canvas(bitmapResult);
        Paint paint = new Paint();
        ColorMatrix colorMatrix = new ColorMatrix();
        colorMatrix.setSaturation(settingSat);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(colorMatrix);
        paint.setColorFilter(filter);
        canvasResult.drawBitmap(src, 0, 0, paint);

        return bitmapResult;
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, Context ctx) {
        if(PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean(BUBBLE_CROP,true)){

            final int width = bitmap.getWidth();
            final int height = bitmap.getHeight();
            final Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            final Path path = new Path();
            path.addCircle(
                    (float)(width / 2)
                    , (float)(height / 2)
                    , (float) Math.min(width, (height / 2.2))
                    , Path.Direction.CCW);

            final Canvas canvas = new Canvas(outputBitmap);
            canvas.clipPath(path);
            canvas.drawBitmap(bitmap, 0, 0, null);

            return updateSat(outputBitmap,(PreferenceManager.getDefaultSharedPreferences(ctx).getFloat(BUBBLE_SATURATION,1.0F)));
        }else{
            return updateSat(bitmap,(PreferenceManager.getDefaultSharedPreferences(ctx).getFloat(BUBBLE_SATURATION,1.0F)));
        }
    }

    private static Bitmap getIconByPackage(String appPackage, Context ctx) throws IOException{
        Drawable appicon = null;
        Log.i("package",appPackage);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            appicon = new BitmapDrawable(ctx.getResources(),getAppIcon(ctx.getPackageManager(), appPackage,ctx));
        }else{
            try {
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    appicon = new BitmapDrawable(ctx.getResources(),getAppIcon(ctx.getPackageManager(),appPackage,ctx));
                }else{
                    appicon = ctx.getPackageManager().getApplicationLogo(appPackage);
                }


                if(appicon == null){
                    appicon = ctx.getPackageManager().getApplicationIcon(appPackage);
                }

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }

        Bitmap bitmap;

        if (appicon instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) appicon;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(appicon.getIntrinsicWidth() <= 0 || appicon.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(appicon.getIntrinsicWidth(), appicon.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        return bitmap;
    }

    public static final String big_image = "_big";
    public static final String small_image = "_small";

    public Bitmap getAppIcon( String packageName , int size, Context ctx){

        Bitmap iconCache = cache.getBitmap(packageName.replace(".","_").toLowerCase());
        //Bitmap iconCache = null;
        if(iconCache == null){
            Log.i("hello","this is the diskchacke generating");
            String appName = getDataObjFromPackage(packageName,ctx,new Point(0,0)).name;

            try {

               /* Log.i("get app icon",packageName + " size: "+ size);
                float percentage = PreferenceManager.getDefaultSharedPreferences(ctx).getInt("icon_background_ratio",70)/100f;
                if(percentage < 0.1f){
                    percentage = 0.1f;
                }
                if( size < 20){
                    size = 20;
                }
                int iconsize = (int)(size*percentage);
                int xypos = (size-iconsize)/2;*/

                Bitmap icon = null;

                //Drawable iconDrawable = ctx.getPackageManager().getApplicationIcon(packageName);
                Drawable iconDrawable = null;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    iconDrawable = new BitmapDrawable(ctx.getResources(),getAppIcon(ctx.getPackageManager(),packageName,ctx));
                }else{
                    iconDrawable = ctx.getPackageManager().getApplicationLogo(packageName);
                }


                String icon_pack_string = PreferenceManager.getDefaultSharedPreferences(ctx).getString("iconPacks","iconPacks");

                if(!icon_pack_string.equals(IconPackActivity.NO_ICON_PACK_PACKAGE)) {
                    IconPackManager.IconPack iconPack = new IconPackManager.IconPack(ctx, icon_pack_string);
                    iconPack.load();
                    icon = iconPack.getIconForPackage(packageName, drawableToBitmap(iconDrawable));
                }
                if(icon == null){
                    icon = drawableToBitmap(iconDrawable);
                }

 /*
               if(!PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getBoolean("disable_app_background", true)){
                    xypos = 0;
                    icon = icon.createScaledBitmap(icon,size,size,true);
                }else{
                    icon = icon.createScaledBitmap(icon,iconsize,iconsize,true);
                }

                Bitmap page = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(page);

                c.drawBitmap(getBackgroundShape(size,ctx,icon),0,0,null);
                c.drawBitmap(icon,xypos,xypos,null);*/

                cache.put(packageName.replace(".","_").toLowerCase(), getCroppedBitmap(icon,ctx));

                return getCroppedBitmap(icon,ctx);

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return getPlaceholderIcon(appName,size,ctx);
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                return getPlaceholderIcon(appName,size,ctx);
            }
        }else{
            return iconCache;
        }
    }

    public Bitmap getAppIconNonCache( String packageName , int size, Context ctx){

            Log.i("hello","none chace icon generated be carefully");

            String appName = getDataObjFromPackage(packageName,ctx,new Point(0,0)).name;

            try {

                Bitmap icon;
                Log.d("get_icon","++++"+packageName+"++++");
                Drawable iconDrawable = ctx.getPackageManager().getApplicationIcon(packageName);
                icon = drawableToBitmap(iconDrawable);
                icon = icon.createScaledBitmap(icon,size,size,true);
                return getCroppedBitmap(icon,ctx);

                /*Log.i("get app icon",packageName + " size: "+ size);
                float percentage = PreferenceManager.getDefaultSharedPreferences(ctx).getInt("icon_background_ratio",70)/100f;
                if(percentage < 0.1f){
                    percentage = 0.1f;
                }
                if( size < 20){
                    size = 20;
                }
                int iconsize = (int)(size*percentage);
                int xypos = (size-iconsize)/2;



               if(!PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getBoolean("disable_app_background", true)){
                    xypos = 0;
                    icon = icon.createScaledBitmap(icon,size,size,true);
                }else{
                    icon = icon.createScaledBitmap(icon,iconsize,iconsize,true);
                }

                Bitmap page = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
                Canvas c = new Canvas(page);

                c.drawBitmap(getBackgroundShape(size,ctx,icon),0,0,null);
                c.drawBitmap(icon,xypos,xypos,null);

                return page;*/

            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                return getPlaceholderIcon(appName,size,ctx);
            } catch (IllegalArgumentException e){
                e.printStackTrace();
                return getPlaceholderIcon(appName,size,ctx);
            }

    }

    private static Paint getIconBackgroundPaint(Bitmap icon, Context ctx){
        Paint backgroundColor = new Paint();
        backgroundColor.setAntiAlias(true);
        backgroundColor.setColor(Util.getDominantColor(icon,ctx));
        return backgroundColor;
    }

    private static Bitmap getBackgroundShape(int size, Context ctx,Bitmap icon){
        Bitmap page = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Paint backgroundColor = getIconBackgroundPaint(icon,ctx);
        int radius = size/2;

        Canvas c = new Canvas(page);
        c.drawColor(0, PorterDuff.Mode.CLEAR);

        if(PreferenceManager.getDefaultSharedPreferences(ctx)
                .getBoolean("disable_app_background", true)){
            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getBoolean("custom_app_background",false)){
                backgroundColor.setColor(PreferenceManager.getDefaultSharedPreferences(ctx).getInt("app_background_color",0xffffff));
            }

            Log.i("custom back",PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle"));

            switch (PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle")){
                case "circle":
                    c.drawCircle(radius,radius,radius, backgroundColor);
                    break;
                case "hexagon":
                    c.drawPath(createHexagon(radius,radius,radius),backgroundColor);
                    break;
                case "square":
                    c.drawRect(0,0,radius*2,radius*2, backgroundColor);
                    break;
                case "circle-outline":
                    backgroundColor.setStrokeWidth(radius/10);
                    backgroundColor.setStyle(Paint.Style.STROKE);
                    c.drawCircle(radius,radius,radius-2.5f, backgroundColor);
                    break;
                case "hexagon-outline":
                    backgroundColor.setStrokeWidth(radius/10);
                    backgroundColor.setStyle(Paint.Style.STROKE);
                    c.drawPath(createHexagon(radius-2.5f,radius,radius),backgroundColor);
                    break;
                case "square-outline":
                    backgroundColor.setStrokeWidth(radius/10);
                    backgroundColor.setStyle(Paint.Style.STROKE);
                    c.drawRect(0,0,radius*2,radius*2, backgroundColor);
                    break;
            }
        }

        return page;
    }

    private static Bitmap getIconByUri(String appPackage, Uri uri,Context ctx) throws IOException {
        //InputStream input = ctx.getContentResolver().openInputStream(uri);

        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inPreferredConfig= Bitmap.Config.ARGB_8888;//optional
        //BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        //input.close();
        if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
            return null;

        int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight : onlyBoundsOptions.outWidth;

        double ratio = (originalSize > 100) ? (originalSize / 100) : 1.0;

        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
        bitmapOptions.inPreferredConfig= Bitmap.Config.ARGB_8888;//optional
        //input = ctx.getContentResolver().openInputStream(uri);
        Bitmap bitmap = getIconByPackage(appPackage,ctx);
        //input.close();

        if(bitmap == null){
            bitmap = BitmapFactory.decodeResource(ctx.getResources(),
                    R.drawable.placeholder);
        }

        return bitmap;
    }

    private static int getPowerOfTwoForSampleRatio(double ratio){
        int k = Integer.highestOneBit((int) Math.floor(ratio));
        if(k==0) return 1;
        else return k;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;

        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static final int SIDES = 6;

    public static Path createHexagon(float size, int centerX, int centerY) {

        Path hex = new Path();
        double x1 = centerX + size * Math.cos(((0*(360/SIDES)-30)* Math.PI / 180f));
        double y1 = centerY + size * Math.sin(((0*(360/SIDES)-30)* Math.PI / 180f));
        hex.moveTo((float)x1,(float)y1);


        for(int i = 0; i < SIDES; i++){
            double x = centerX + size * Math.cos(((i*(360/SIDES)-30)* Math.PI / 180f));
            double y = centerY + size * Math.sin(((i*(360/SIDES)-30)* Math.PI / 180f));
            hex.lineTo((float)x,(float)y);
        }


        hex.close();
        return hex;
    }

    public static Bitmap getPlaceholderIcon(String appName, int size, Context ctx){
        Bitmap icon = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas iconCanvas = new Canvas(icon);

        Drawable d = ctx.getResources().getDrawable(R.drawable.placeholder);
        d.setBounds(0, 0, size, size);
        d.draw(iconCanvas);

        drawCenter(iconCanvas,appName);

        return icon;
    }

    private static void drawCenter(Canvas canvas, String text) {
        Rect canvasBounds = new Rect();
        canvas.getClipBounds(canvasBounds);
        Paint backPaint = new Paint();
        backPaint.setColor(Color.argb(200,30,30,30));
        RectF backrect = new RectF(
                0,
                (int)(canvasBounds.height()/3f*2-(canvasBounds.height()/6f)),
                canvasBounds.width(),
                (int)(canvasBounds.height()-(canvasBounds.height()/6f)));
        canvas.drawPath(composeRoundedRectPath(backrect,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f),backPaint);
        //canvas.drawRect(0,canvasBounds.height()-(canvasBounds.height()/6f),canvasBounds.width(),canvasBounds.height()/3f*2-(canvasBounds.height()/6f),backPaint);

        Paint textPaint = new Paint();
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextSize(canvasBounds.height()/3f - canvasBounds.height()/9f);
        Rect r = new Rect();
        canvas.getClipBounds(r);
        int cHeight = r.height();
        int cWidth = r.width();
        textPaint.setTextAlign(Paint.Align.LEFT);

        if(text.length() > 8)
            textPaint.getTextBounds(text.substring(0,5)+"...", 0, 8, r);
        else
            textPaint.getTextBounds(text, 0, text.length(), r);


        float x = cWidth / 2f - r.width() / 2f - r.left;
        float y = cHeight / 3f * 2f + r.height() / 2f - r.bottom;
        if(text.length() > 8)
            canvas.drawText(text.substring(0,5)+"...", x, y, textPaint);
        else
            canvas.drawText(text, x, y, textPaint);
    }

    private static Uri getIconPath(String packageName, String iconRes){
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE+"://"+packageName+"/"+iconRes);
    }

    public static ArrayList<DataObj> getAppsSortByAlphabet(ArrayList<DataObj> apps){
        Collections.sort(apps, new Comparator<DataObj>() {
            @Override
            public int compare(final DataObj object1, final DataObj object2) {
                return object1.name.compareTo(object2.name);
            }
        } );
        return apps;
    }

    public static ArrayList<Point> getAlphabetgroupsPositioned(ArrayList<DataObj> apps){
        ArrayList<DataObj> retlist = new ArrayList<>();
        ArrayList<ArrayList<DataObj>> alphabetGroups = getAlphabetGroups(apps);

        // init the first group and add it to the screen

        ArrayList<Point> tempPoints = getFolderPoints(alphabetGroups.get(0).size());

        for(int i = 0; i < alphabetGroups.get(0).size(); i++){

            alphabetGroups.get(0).get(i).x = tempPoints.get(i).x;
            alphabetGroups.get(0).get(i).y = tempPoints.get(i).y;

            retlist.add(alphabetGroups.get(0).get(i));
        }

        alphabetGroups.remove(0);

        // now iterate through all the other groups and add them to the first one

        for(ArrayList<DataObj> group : alphabetGroups){

            // init the positions of the group

           tempPoints = getFolderPoints(group.size());
            for(int i = 0; i < group.size(); i++){
                group.get(i).x = tempPoints.get(i).x;
                group.get(i).y = tempPoints.get(i).y;
            }

            Point groupDimension = getGroupDimension(group);
            Point listDimension = getGroupDimension(retlist);

            Point nextFreePoint = getNextFreePoint(retlist,listDimension);

            for(int i = 0; i < group.size(); i++){
                group.get(i).x += nextFreePoint.x;
                group.get(i).y += nextFreePoint.y;

                retlist.add(group.get(i));
            }
        }

        ArrayList<Point> ret = new ArrayList<>();

        for(DataObj app : retlist){
            ret.add(new Point(app.x,app.y));
        }

        return ret;
    }

    private static Point getNextFreePoint(ArrayList<DataObj> apps,Point listDimension){
        Point ret = new Point(0,0);

        printGrid(apps,listDimension);

        int smalestRowPoint = listDimension.x;
        int smalestCollumnPoint = listDimension.y;

        for(int row = 0; row < listDimension.y; row++){
            int tempLastRow = getLastPointInRow(apps,row);
            if(tempLastRow < smalestRowPoint){
                smalestRowPoint = tempLastRow;
            }
        }

        return new Point(listDimension.x+2,listDimension.y+2);
    }

    private static void printGrid(ArrayList<DataObj> apps, Point listDimension){
        Log.i("Grid","--------------------");
        for(int row = 0; row < listDimension.y+1; row++){
            String rowStr = "";
            for(int column = 0; column < listDimension.x+1; column++){
                for(DataObj app : apps){
                    if(app.x == column && app.y == row){
                        rowStr += "X";
                    }
                }
            }

            Log.i(row+"|",rowStr);
        }
        Log.i("Grid END","--------------------");
    }

    private static int getLastPointInColumn(ArrayList<DataObj> group, int column){
        int y = 0;

        for(DataObj app : group){
            if(app.x == column){
                if(app.y > y){
                    y = app.y;
                }
            }
        }

        return y;
    }

    private static int getLastPointInRow(ArrayList<DataObj> group, int row){
        int x = 0;

        for(DataObj app : group){
            if(app.y == row){
                if(app.x > x){
                    x = app.x;
                }
            }
        }

        return x;
    }

    private static Point getGroupDimension(ArrayList<DataObj> group){

        int x = 0;
        int y = 0;

        for(int i = 0; i < group.size(); i++){
            Point temp = new Point(group.get(i).x,group.get(i).y);
            if(temp.x > x)
                x = temp.x;
            if(temp.y > y)
                y = temp.y;
        }

        return new Point(x,y);
    }


    private static ArrayList<ArrayList<DataObj>> getAlphabetGroups(ArrayList<DataObj> apps){
        ArrayList<ArrayList<DataObj>> alphabetGroups = new ArrayList<>();

        char alphabet[] = {'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};

        for(char letter : alphabet){
            ArrayList<DataObj> singleLetterApps = new ArrayList<>();
            for(int i = 0; i < apps.size(); i++){
                if(apps.get(i).name.toLowerCase().charAt(0) == letter){
                    Log.i(letter+" ",apps.get(i).name);
                    singleLetterApps.add(apps.get(i));
                }
            }

            if(singleLetterApps.size() > 0){
                alphabetGroups.add(singleLetterApps);
            }
        }


        for(int i = 0; i < apps.size(); i++){
            ArrayList<DataObj> singleLetterApps = new ArrayList<>();
            for(char letter : alphabet){
                if(apps.get(i).name.toLowerCase().charAt(0) != letter){
                    singleLetterApps.add(apps.get(i));
                }
            }

        }



        return alphabetGroups;
    }

    public static int getSquareSize(int number){
        return (int) Math.ceil(Math.sqrt(number));
    }

    // get points that are ordered in a quadratic shape

    public static ArrayList<Point> getPoints(int number){
        ArrayList<Point> retlist = new ArrayList<>();

        int size = (int) Math.ceil(Math.sqrt(number));

        int offset = 0;
        int counter = 0;


        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                if(counter < number){

                    //int packoffset = diam - (int) Math.sqrt(Math.pow(diam,2)- Math.pow(.5*diam,2));

                    //int x = layoutPadding + startx + offset + (row*diam) + ((row-1)*padding);
                    int x = row+2;
                    //int y = layoutPadding -(2*packoffset) + (col*diam) + ((col)*padding)-(col*packoffset);
                    int y = col+2;

                    Log.i("GetPoints","x: "+x+" y: "+y);

                    retlist.add(new Point(x,y));

                    counter++;
                }
            }
        }
        return retlist;
    }

    public static ArrayList<Point> getPointsNull(int number){
        ArrayList<Point> retlist = new ArrayList<>();

        int size = (int) Math.ceil(Math.sqrt(number));

        int offset = 0;
        int counter = 0;


        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                if(counter < number){

                    //int packoffset = diam - (int) Math.sqrt(Math.pow(diam,2)- Math.pow(.5*diam,2));

                    //int x = layoutPadding + startx + offset + (row*diam) + ((row-1)*padding);
                    int x = row;
                    //int y = layoutPadding -(2*packoffset) + (col*diam) + ((col)*padding)-(col*packoffset);
                    int y = col;

                    Log.i("GetPoints","x: "+x+" y: "+y);

                    retlist.add(new Point(x,y));

                    counter++;
                }
            }
        }
        return retlist;
    }

    public static ArrayList<Point> getPointsFromPattern(int number,ArrayList<Point> pattern, boolean padding){
        ArrayList<Point> retlist = new ArrayList<>();
        int patternsize = pattern.size();
        Log.i("pattern size", patternsize+" ");
        int counter = 0;
        int islandCount = (int) Math.ceil(number/patternsize)+1;

        Log.i("number of apps",number+ " ");
        Log.i("number of island",islandCount+" ");

        ArrayList<Point> tempP = getPointsNull(islandCount);

        Point boundaries = getBordersFromGroup(pattern);
        Log.i("pattern bound", boundaries.toString());
        for(Point p : tempP){
            Log.i("temp points",p.toString());
            for(Point pp : pattern){
                if(counter <= number){
                    counter++;
                    if(padding){
                        retlist.add(new Point(pp.x+(p.x*(boundaries.x+1))+2,pp.y+(p.y*(boundaries.y+1))+2));
                        Log.i("added",new Point(pp.x+(p.x*(boundaries.x+1)),pp.y+(p.y*(boundaries.y+1))).toString()+" ");
                    }else{
                        retlist.add(new Point(pp.x+(p.x*(boundaries.x))+2,pp.y+(p.y*(boundaries.y))+2));
                        Log.i("added",new Point(pp.x+(p.x*(boundaries.x)),pp.y+(p.y*(boundaries.y))).toString()+" ");
                    }
                }
            }
        }

        return retlist;
    }

    public static ArrayList<Point> getPointsGroups(int number){
        ArrayList<Point> retlist = new ArrayList<>();

        int size = (int) Math.ceil(Math.sqrt(number));

        int offset = 0;
        int counter = 0;

        int offsetcol = 0;

        for(int row = 0; row < size; row++){

            if(row%3 == 0){
                offset++;
            }

            for(int col = 0; col < size; col++){

                if(col%3 == 0){
                    offsetcol = col/3;
                }

                if(counter < number){

                    //int packoffset = diam - (int) Math.sqrt(Math.pow(diam,2)- Math.pow(.5*diam,2));

                    //int x = layoutPadding + startx + offset + (row*diam) + ((row-1)*padding);
                    int x = row+2+offset;
                    //int y = layoutPadding -(2*packoffset) + (col*diam) + ((col)*padding)-(col*packoffset);
                    int y = col+2+offsetcol;

                    Log.i("GetPoints","x: "+x+" y: "+y);

                    retlist.add(new Point(x,y));

                    counter++;
                }
            }
        }
        return retlist;
    }

    public static ArrayList<Point> getFolderPoints(int number){
        ArrayList<Point> retlist = new ArrayList<>();

        int size = (int) Math.ceil(Math.sqrt(number));

        int counter = 0;

        for(int row = 0; row < size; row++){
            for(int col = 0; col < size; col++){
                if(counter < number){

                    int x = row;
                    int y = col;

                    retlist.add(new Point(x,y));

                    counter++;
                }
            }
        }
        return retlist;
    }
}
