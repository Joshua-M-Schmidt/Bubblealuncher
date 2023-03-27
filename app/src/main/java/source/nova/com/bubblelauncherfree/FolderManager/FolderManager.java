package source.nova.com.bubblelauncherfree.FolderManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;
import android.widget.RelativeLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.AppManager.DiskLruImageCache;
import source.nova.com.bubblelauncherfree.CustomViews.AppView;
import source.nova.com.bubblelauncherfree.CustomViews.FolderView;
import source.nova.com.bubblelauncherfree.Theme;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getAllApps;
import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getAppsFromDB;
import static source.nova.com.bubblelauncherfree.AppManager.AppManager.isAppInstalled;

/**
 * Created by joshua on 17.10.17.
 */

public class FolderManager {

    public static final boolean DEBUG_MODE = true;
    public static final String DEBUG_FOLDER_MANAGER = "debug_folder_manager";
    public static final String CACHE_ID = "folder_icons_cache";

    DiskLruImageCache cache;
    Context ctx;

    public FolderManager(Context ctx) {
        cache = new DiskLruImageCache(ctx,
                CACHE_ID,
                1024 * 1024 * 10 /* 10 mb */,
                Bitmap.CompressFormat.PNG,
                100);
        this.ctx = ctx;
    }

    // helper functions for folder generation usw...

    public static boolean folderIntersection(ArrayList<FolderView> folders, Point newPosition){

        if(DEBUG_MODE){
            Log.i(DEBUG_FOLDER_MANAGER,"FolderIntersection ***********************************");
        }

        for(int i = 0; i < folders.size(); i++){

            Point folderRasterPoint = folders.get(i).getPosition();
            Point appRasterPoint = newPosition;

            if(DEBUG_MODE){
                Log.i(DEBUG_FOLDER_MANAGER,"Folder Position: "+folderRasterPoint.x+" "+folderRasterPoint.y);
                Log.i(DEBUG_FOLDER_MANAGER,"New Position: "+appRasterPoint.x+" "+appRasterPoint.y);
            }
            if(folderRasterPoint.equals(appRasterPoint)){
                if(DEBUG_MODE){
                    Log.i(DEBUG_FOLDER_MANAGER,"Folder Position: "+folderRasterPoint.x+" "+folderRasterPoint.y);
                    Log.i(DEBUG_FOLDER_MANAGER,"New Position: "+appRasterPoint.x+" "+appRasterPoint.y);
                }
                return true;
            }
        }
        return false;
    }

    public static String getfolderIntersection(ArrayList<FolderView> folders, Point newPosition){
        for(int i = 0; i < folders.size(); i++){
            if(folders.get(i).getPosition().equals(newPosition)){
                return folders.get(i).getFolderName();
            }
        }
        return null;
    }

    public static String getUniqueFolderID(Context ctx){
        boolean hasUnique = false;
        String ret = "";

        while(!hasUnique){
            char[] chars = "abcdefghijklmnopqrstuvwxyz".toCharArray();
            StringBuilder sb = new StringBuilder();
            Random random = new Random();
            for (int i = 0; i < 20; i++) {
                char c = chars[random.nextInt(chars.length)];
                sb.append(c);
            }
            ret = sb.toString();
            hasUnique = !doesFolderExist(ret,ctx);
        }

        return ret;
    }

    // public operations used from the mainActivity

    public static ArrayList<AppView> getAppsInFolder(String folderName,Context ctx, int asize){

        AppManager appManager = new AppManager(ctx);

        Log.i("get apps for folder",folderName);

        ArrayList<AppView> ret = new ArrayList<>();

        FolderDataHelper helper = null;
        try{
            String[] apps = getAppsBelongingToFolder(folderName,getFolderDataHelper(ctx),ctx);

            for(int i = 0; i < apps.length; i++){
                ret.add(appManager.getAppViewByPackage(apps[i],ctx, asize));
            }

        }finally {
            if(helper != null){
                helper.close();
            }
        }

        return ret;
    }

    public static ArrayList<AppView> getRandomApps(int number, Context ctx, int asize){
        AppManager appManager = new AppManager(ctx);

        ArrayList<AppView> ret = new ArrayList<>();

        FolderDataHelper helper = null;
        try{
            ArrayList<DataObj> apps = getAllApps(ctx);
            if(apps.size() >= number){
                for(int i = 0; i < number; i++){
                    ret.add(appManager.getAppViewByPackage(apps.get(i).package_name,ctx, asize));
                }
            }else{
                for(int i = 0; i < apps.size(); i++){
                    ret.add(appManager.getAppViewByPackage(apps.get(i).package_name,ctx, asize));
                }
            }


        }finally {
            if(helper != null){
                helper.close();
            }
        }

        return ret;
    }

    public static ArrayList<DataObj> getAppsInFolder(String folderName, Context ctx){

        AppManager appManager = new AppManager(ctx);

        Log.i("get apps for folder",folderName);

        ArrayList<DataObj> ret = new ArrayList<>();

        FolderDataHelper helper = null;
        try{
            String[] apps = getAppsBelongingToFolder(folderName,getFolderDataHelper(ctx),ctx);

            for(int i = 0; i < apps.length; i++){
                ret.add(appManager.getDataObjFromPackage(apps[i],ctx,new Point(0,0)));
            }

        }finally {
            if(helper != null){
                helper.close();
            }
        }

        return ret;
    }

    public static ArrayList<String> getAppsInFolderAppPackage(String foldername, Context ctx){
        ArrayList<String> ret = new ArrayList<>();

        FolderDataHelper helper = null;
        try{
            String[] apps = getAppsBelongingToFolder(foldername,getFolderDataHelper(ctx),ctx);
            if(apps != null){
                for(int i = 0; i < apps.length; i++){
                    ret.add(apps[i]);
                }
            }else{
                return null;
            }


        }finally {
            if(helper != null){
                helper.close();
            }
        }

        return ret;
    }



    public static void updateFolderPosition(Point newPosition, Context ctx, String folderName){
        FolderDataHelper helper = null;

        try{
            helper = getFolderDataHelper(ctx);

            ContentValues cv = new ContentValues();

            Log.i("GetFoldersFromDB","x: "+newPosition.x +" y: "+newPosition.y);

            cv.put(FolderContract.Y_POSITION,newPosition.y);
            cv.put(FolderContract.X_POSITION,newPosition.x);

            helper.update(
                    FolderContract.TABLE_NAME,
                    cv,
                    FolderContract.FOLDER_NAME+" = ?",
                    new String[]{
                            folderName
                    }
            );

        }finally {
            if(helper != null)
                helper.close();
        }
    }

    public static void createFolder(String folderName, String appA, String appB, int x, int y, Context ctx){

        //change the belonging of the apps

        AppManager.updateFolder(folderName,ctx,appA);
        AppManager.updateFolder(folderName,ctx,appB);

        //create a new folder

        addFolderToDB(folderName,appA,appB,x,y,ctx);

        if(DEBUG_MODE){
            Log.i(DEBUG_FOLDER_MANAGER,"CreateFolder ***********************************");
            Log.i(DEBUG_FOLDER_MANAGER,"FolderName: "+folderName);
            Log.i(DEBUG_FOLDER_MANAGER,"AppA: "+appA);
            Log.i(DEBUG_FOLDER_MANAGER,"AppB: "+appB);
            Log.i(DEBUG_FOLDER_MANAGER,"X: "+x);
            Log.i(DEBUG_FOLDER_MANAGER,"Y: "+y);
        }
    }

    public static void deleteFolder(String folderName, Context ctx){
        FolderDataHelper helper = null;
        try{
            helper = getFolderDataHelper(ctx);
            removeFolderFromDB(folderName,helper);
        }finally {
            if(helper != null)
                helper.close();
        }

        if(DEBUG_MODE){
            Log.i(DEBUG_FOLDER_MANAGER,"deleteFolder ***********************************");
            Log.i(DEBUG_FOLDER_MANAGER,"FolderName: "+folderName);
        }
    }

    public static void addAppToFolder(String folderName, String appPackageName, Context ctx){
        DiskLruImageCache cache = new DiskLruImageCache(ctx,
                CACHE_ID,
                1024 * 1024 * 10 /* 10 mb */,
                Bitmap.CompressFormat.PNG,
                100);

        cache.removeFromCache(folderName);

        FolderDataHelper helper = null;
        try {

            // update folder database

            helper = getFolderDataHelper(ctx);
            addAppPackageToFolder(folderName, appPackageName, helper, ctx);

            // update app database

            AppManager.updateFolder(folderName,ctx,appPackageName);

        }finally {
            if(helper != null)
                helper.close();
        }

        if(DEBUG_MODE){
            Log.i(DEBUG_FOLDER_MANAGER,"Add App to Folder ***********************************");
            Log.i(DEBUG_FOLDER_MANAGER,"FolderName: "+folderName);
            Log.i(DEBUG_FOLDER_MANAGER,"AppPackageName: "+appPackageName);
        }
    }

    public static void changeFolderName(String folderName, String newName, Context ctx){
        FolderDataHelper helper = null;
        try {

            // update folder database

            helper = getFolderDataHelper(ctx);
            updateFolderName(folderName, newName, helper);

            // update app database

            AppManager.updateFolderName(folderName,ctx,newName);

        }finally {
            if(helper != null)
                helper.close();
        }

    }

    public static void removeAppFromFolder(String folderName, String appPackageName, Context ctx){

        DiskLruImageCache cache = new DiskLruImageCache(ctx,
                CACHE_ID,
                1024 * 1024 * 10 /* 10 mb */,
                Bitmap.CompressFormat.PNG,
                100);

        cache.removeFromCache(folderName);

        FolderDataHelper helper = null;
        try {

            // update folder database

            helper = getFolderDataHelper(ctx);
            removeAppPackageFromFolder(folderName, appPackageName, helper,ctx);

            // update app database

            AppManager.updateFolder(folderName,ctx,null);

        }finally {
            if(helper != null)
                helper.close();
        }

        if(DEBUG_MODE){
            Log.i(DEBUG_FOLDER_MANAGER,"Remove App from Folder ***********************************");
            Log.i(DEBUG_FOLDER_MANAGER,"FolderName: "+folderName);
            Log.i(DEBUG_FOLDER_MANAGER,"AppPackageName: "+appPackageName);
        }
    }

    public static ArrayList<FolderView> getFoldersFromDB(Context ctx){
        ArrayList<FolderView> ret = new ArrayList<>();

        FolderDataHelper helper = null;
        Cursor folders = null;
        try{
            helper = getFolderDataHelper(ctx);

            folders = helper.read(
                    FolderContract.TABLE_NAME,
                    new String[]{
                            FolderContract.FOLDER_NAME,
                            FolderContract.X_POSITION,
                            FolderContract.Y_POSITION,
                            FolderContract.APPS_INSIDE
                    },
                    "",
                    null,
                    ""
            );

            if(DEBUG_MODE){
                Log.i(DEBUG_FOLDER_MANAGER,"Cursor count: "+folders.getCount());
            }

            if(folders.moveToFirst()){
                while (!folders.isAfterLast()){
                    ret.add(getFolderViewFromCursor(folders,ctx));

                    Log.i("GetFoldersFromDB","x: "+folders.getInt(folders.getColumnIndex(FolderContract.X_POSITION))+" y: "+folders.getInt(folders.getColumnIndex(FolderContract.Y_POSITION)));

                    folders.moveToNext();
                }
            }

        }finally {
            if(helper != null)
                helper.close();
            if(folders != null)
                folders.close();
        }

        return ret;
    }

    public static FolderView getFolderViewFromDB(String name,Context ctx){
        FolderView ret = null;

        FolderDataHelper helper = null;
        Cursor folders = null;
        try{
            helper = getFolderDataHelper(ctx);

            folders = helper.read(
                    FolderContract.TABLE_NAME,
                    new String[]{
                            FolderContract.FOLDER_NAME,
                            FolderContract.X_POSITION,
                            FolderContract.Y_POSITION,
                            FolderContract.APPS_INSIDE
                    },
                    FolderContract.FOLDER_NAME+ " = ? ",
                    new String[]{name},
                    ""
            );

            if(DEBUG_MODE){
                Log.i(DEBUG_FOLDER_MANAGER,"Cursor count: "+folders.getCount());
            }

            if(folders.moveToFirst()){
                while (!folders.isAfterLast()){
                    ret = (getFolderViewFromCursor(folders,ctx));

                    Log.i("GetFoldersFromDB","x: "+folders.getInt(folders.getColumnIndex(FolderContract.X_POSITION))+" y: "+folders.getInt(folders.getColumnIndex(FolderContract.Y_POSITION)));

                    folders.moveToNext();
                }
            }

        }finally {
            if(helper != null)
                helper.close();
            if(folders != null)
                folders.close();
        }

        return ret;
    }

    public static FolderView getNewFolderView(Context ctx, String folderName, Point position, ArrayList<String> apps){

        int asize = Util.getDiam(ctx);
        int padding = Util.getPadding(ctx);

        Log.i("GetNewFolderView","x: "+position.x+" y: "+position.y);

        FolderView temp = new FolderView(
                ctx,
                folderName,
                position,
                apps
        );

        int w = 0;

        if(PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString("app_icon_style","circle").equals("hexagon") ||
                PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getString("app_icon_style","circle").equals("hexagon-outline")){
            w = (int)(2*Util.getHexOffsetX(asize/2));

        }

        temp.setImageBitmap(getHexFolderIcon(apps, asize,asize-w,ctx,folderName,false));

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize,asize);

        Point newRasterP = Util.rasterToPixel(position,asize,padding,ctx);

        params.leftMargin = newRasterP.x;
        params.topMargin = newRasterP.y;

        temp.setLayoutParams(params);


        return temp;
    }

    public static Bitmap getHexFolderIcon(ArrayList<String> appArray, int size,int width, Context ctx, String folderName, boolean force){
        DiskLruImageCache cache = new DiskLruImageCache(ctx,
                CACHE_ID,
                1024 * 1024 * 10 /* 10 mb */,
                Bitmap.CompressFormat.PNG,
                100);
        String id = folderName.replace(".","_").toLowerCase();
        Bitmap iconCache = cache.getBitmap(id);

        if(iconCache == null || force){
            Log.i("hello","generate folder icon");
            Bitmap icon = Bitmap.createBitmap(width, size, Bitmap.Config.ARGB_8888);
            Canvas iconCanvas = new Canvas(icon);
            int offset = 0;
            int yOffset = 0;

            if(size != width){
                offset = (int) (Util.getHexOffsetX(width/3));
                yOffset = (int) (Util.getHexOffsetY(width/3))/2;
            }

            AppManager appManager = new AppManager(ctx);

            ArrayList<Point> pattern = new ArrayList<>();

            if(PreferenceManager.getDefaultSharedPreferences(ctx).getString(Theme.RASTER_STYLE_KEY,Theme.RASTER_STYLE_HONEYCOMB).equals(Theme.RASTER_STYLE_CHESSBOARD)){
                pattern.add(new Point(0,0));
                pattern.add(new Point(1,0));
                pattern.add(new Point(2,0));
                pattern.add(new Point(0,1));
                pattern.add(new Point(1,1));
                pattern.add(new Point(2,1));
                pattern.add(new Point(0,2));
                pattern.add(new Point(1,2));
                pattern.add(new Point(2,2));
            }else{
                pattern.add(new Point(1,0));
                pattern.add(new Point(2,0));
                pattern.add(new Point(0,1));
                pattern.add(new Point(1,1));
                pattern.add(new Point(2,1));
                pattern.add(new Point(1,2));
                pattern.add(new Point(2,2));
            }

            int diam = width/3;
            int padding = Util.getPadding(ctx);
            diam -= padding;
            diam += padding/3;

            if(DEBUG_MODE){
                Log.i(DEBUG_FOLDER_MANAGER,"GetHexIcon ***********************************"+size);
                Log.i(DEBUG_FOLDER_MANAGER,"pattern size ***********************************"+pattern.size());
                Log.i(DEBUG_FOLDER_MANAGER,"apparry size ***********************************"+appArray.size());
            }

            for(int i = 0; i < appArray.size() && i < pattern.size(); i++){
                Bitmap tempIcon = Bitmap.createScaledBitmap(appManager.getAppIcon(appArray.get(i),diam,ctx), diam, diam, false);

                Point temppoint = getPixelPoint(pattern.get(i),padding,diam,ctx);
                temppoint.x += offset;
                temppoint.y += yOffset;
                iconCanvas.drawBitmap(tempIcon,temppoint.x,temppoint.y,null);
            }

            if(PreferenceManager.getDefaultSharedPreferences(ctx).getBoolean("display_folder_name",true)) {
                drawCenter(iconCanvas,folderName);
            }
            cache.put(id,icon);
            return icon;
        }else{
            Log.i("hello","folder from cache");
            return iconCache;
        }
    }

    // private operation used only internal

    private static void drawCenter(Canvas canvas, String text) {
        Rect canvasBounds = new Rect();
        canvas.getClipBounds(canvasBounds);
        Paint backPaint = new Paint();
        backPaint.setColor(Color.argb(150,0,0,0));
        backPaint.setFlags(Paint.ANTI_ALIAS_FLAG);
        /*RectF backrect = new RectF(
                0,
                (int)(canvasBounds.height()/3f*2-(canvasBounds.height()/6f)),
                canvasBounds.width(),
                (int)(canvasBounds.height()-(canvasBounds.height()/6f)));
        canvas.drawPath(composeRoundedRectPath(backrect,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f),backPaint);*/

        RectF backrect = new RectF(
                0,
                (int)(canvasBounds.height()/3f*2-(canvasBounds.height()/3f)),
                canvasBounds.width(),
                (int)(canvasBounds.height()-(canvasBounds.height()/3f)));
        //canvas.drawRect(backrect,backPaint);
        canvas.drawPath(composeRoundedRectPath(backrect,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f,
                canvasBounds.height()/6f),backPaint);

        //canvas.drawRect(0,canvasBounds.height()-(canvasBounds.height()/6f),canvasBounds.width(),canvasBounds.height()/3f*2-(canvasBounds.height()/6f),backPaint);

        Paint textPaint = new Paint();
        textPaint.setARGB(255, 255, 255, 255);
        textPaint.setTextSize(canvasBounds.height()/3f - canvasBounds.height()/9f);
        textPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

        Paint shadowPaint = new Paint();
        shadowPaint.setARGB(255, 255, 255, 255);
        shadowPaint.setTextSize(canvasBounds.height()/3f - canvasBounds.height()/9f);
        shadowPaint.setFlags(Paint.ANTI_ALIAS_FLAG);

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
        //float y = cHeight / 3f * 2f + r.height() / 2f - r.bottom;
        float y = cHeight / 2f + r.height() / 2f - r.bottom;

        if(text.length() > 8){
            canvas.drawText(text.substring(0,5)+"...", x, y, textPaint);
            shadowPaint.setShadowLayer(10, 0, 0, Color.BLACK);
            //canvas.drawText(text.substring(0,5)+"...", x, y, shadowPaint);
        }
        else{
            canvas.drawText(text, x, y, textPaint);
            shadowPaint.setShadowLayer(10, 0, 0, Color.BLACK);
            //canvas.drawText(text, x, y, shadowPaint);
        }

    }

    public static Path composeRoundedRectPath(RectF rect, float topLeftDiameter, float topRightDiameter,float bottomRightDiameter, float bottomLeftDiameter){
        Path path = new Path();
        topLeftDiameter = topLeftDiameter < 0 ? 0 : topLeftDiameter;
        topRightDiameter = topRightDiameter < 0 ? 0 : topRightDiameter;
        bottomLeftDiameter = bottomLeftDiameter < 0 ? 0 : bottomLeftDiameter;
        bottomRightDiameter = bottomRightDiameter < 0 ? 0 : bottomRightDiameter;

        path.moveTo(rect.left + topLeftDiameter/2 ,rect.top);
        path.lineTo(rect.right - topRightDiameter/2,rect.top);
        path.quadTo(rect.right, rect.top, rect.right, rect.top + topRightDiameter/2);
        path.lineTo(rect.right ,rect.bottom - bottomRightDiameter/2);
        path.quadTo(rect.right ,rect.bottom, rect.right - bottomRightDiameter/2, rect.bottom);
        path.lineTo(rect.left + bottomLeftDiameter/2,rect.bottom);
        path.quadTo(rect.left,rect.bottom,rect.left, rect.bottom - bottomLeftDiameter/2);
        path.lineTo(rect.left,rect.top + topLeftDiameter/2);
        path.quadTo(rect.left,rect.top, rect.left + topLeftDiameter/2, rect.top);
        path.close();

        return path;
    }

    public static boolean doesFolderExist(String folderName,Context ctx){
        Cursor folder = null;
        FolderDataHelper helper= null;
        try{
            helper = getFolderDataHelper(ctx);

            folder = helper.read(
                    FolderContract.TABLE_NAME,
                    new String[]{
                            FolderContract.FOLDER_NAME
                    },
                    FolderContract.FOLDER_NAME+ " = ? ",
                    new String[]{
                            folderName
                    },
                    ""
            );

            return folder.moveToFirst();

        }finally {
            if(folder != null)
                folder.close();
            if(helper != null)
                helper.close();
        }
    }

    private static final String TAG = "folder_point";

    private static Point getPixelPoint(Point rasterPoint, int padding, int diam, Context ctx){
        Log.i(TAG+" d","get folder point");
        Log.i(TAG+" x","diam "+diam+" padding "+padding);
        if(PreferenceManager.getDefaultSharedPreferences(ctx).getString("raster_style","honeycomb").equals("honeycomb")){

            int packoffset = diam - (int) Math.sqrt(Math.pow(diam,2)- Math.pow(.5*diam,2));

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("square") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("square-outline")) {
                packoffset = 0;
            }else if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                packoffset = (int) getHexOffsetY(diam/2);
            }

            int y = rasterPoint.y*(((diam+padding)-packoffset));

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                diam -= (2*getHexOffsetX(diam/2));
            }

            Log.i(TAG+"f","packoffset "+packoffset);

            if(rasterPoint.y % 2 == 0){

                //int x = rasterPoint.x*(diam+padding);
                int x = ((rasterPoint.x*(diam+padding)) - ((diam/2) +(padding/2)));

                Log.i(TAG+"000","x "+rasterPoint.x+" y "+rasterPoint.y);
                Log.i(TAG,"x "+x+" y "+y);

                return new Point(x,y+packoffset);
            }else{

                int x = (rasterPoint.x*(diam+padding));


                return new Point(x,y+packoffset);
            }
        }else{
            int y = rasterPoint.y*(diam+padding);

            int x = rasterPoint.x*(diam+padding);

            return new Point(x,y);
        }

        //return Util.rasterToPixel(rasterPoint,diam,padding,ctx);
    }

    private static double getHexOffsetX(int size){
        double x1 = size + size * Math.cos(((4*(360/6)-30)* Math.PI / 180f));
        Log.i("x offset",x1+" off");
        return x1;
    }

    private static double getHexOffsetY(int size){
        double y1 = size + size * Math.sin(((0*(360/6)-30)* Math.PI / 180f));
        return y1;
    }

    private static FolderView getFolderViewFromCursor(Cursor folders,Context ctx){

        int asize = Util.getDiam(ctx);
        int padding = Util.getPadding(ctx);

        Point pxPos = Util.rasterToPixel(new Point(
                folders.getInt(folders.getColumnIndex(FolderContract.X_POSITION)),
                folders.getInt(folders.getColumnIndex(FolderContract.Y_POSITION))
        ),asize,padding,ctx);

        FolderView temp = new FolderView(
                ctx,
                folders.getString(folders.getColumnIndex(FolderContract.FOLDER_NAME)),
                new Point(
                        folders.getInt(folders.getColumnIndex(FolderContract.X_POSITION)),
                        folders.getInt(folders.getColumnIndex(FolderContract.Y_POSITION))
                ),
                new ArrayList<>(
                        Arrays.asList(
                                convertStringToArray(
                                        folders.getString(
                                                folders.getColumnIndex(FolderContract.APPS_INSIDE)
                                        )
                                )
                        )
                )
        );

        int w = 0;

        if(PreferenceManager.getDefaultSharedPreferences(ctx)
                .getString("app_icon_style","circle").equals("hexagon") ||
                PreferenceManager.getDefaultSharedPreferences(ctx)
                        .getString("app_icon_style","circle").equals("hexagon-outline")){
            w = (int)(2*Util.getHexOffsetX(asize/2));

        }

        temp.setImageBitmap(getHexFolderIcon(new ArrayList<>(Arrays.asList(convertStringToArray(folders.getString(folders.getColumnIndex(FolderContract.APPS_INSIDE))))),
                asize,
                asize-w,
                ctx,folders.getString(folders.getColumnIndex(FolderContract.FOLDER_NAME)),false));


        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(asize, asize);

        Log.i("GetFolderViewFromCursor","x: "+folders.getInt(folders.getColumnIndex(FolderContract.X_POSITION))+" y: "+folders.getInt(folders.getColumnIndex(FolderContract.Y_POSITION)));

        params.leftMargin = pxPos.x;
        params.topMargin = pxPos.y;

        temp.setLayoutParams(params);

        return temp;
    }

    private static void removeAppPackageFromFolder(String folderName, String appPackageName, FolderDataHelper helper, Context ctx){
        String[] appsAlreadyInFolder = getAppsBelongingToFolder(folderName,helper,ctx);
        List<String> appList = new ArrayList<>(Arrays.asList(appsAlreadyInFolder));

        for(int i = 0; i < appList.size(); i++){
            if(appList.get(i).equals(appPackageName))
                appList.remove(i);
        }

        ContentValues cv = new ContentValues();
        cv.put(FolderContract.APPS_INSIDE,convertArrayToString(appList.toArray(new String[0])));

        helper.update(
                FolderContract.TABLE_NAME,
                cv,
                FolderContract.FOLDER_NAME+ " = ? ",
                new String[]{
                        folderName
                }
        );

    }

    private static void addAppPackageToFolder(String folderName, String appPackageName, FolderDataHelper helper, Context ctx){

        String[] appsAlreadyInFolder = getAppsBelongingToFolder(folderName,helper,ctx);

        if(DEBUG_MODE){
            Log.i(DEBUG_FOLDER_MANAGER,"AddPackageToFolder ***********************************");
        }

        List<String> newAppList = new ArrayList<>(Arrays.asList(appsAlreadyInFolder));
        newAppList.add(appPackageName);

        if(DEBUG_MODE){
            for(String s : appsAlreadyInFolder){
                Log.i(DEBUG_FOLDER_MANAGER,"old app: "+s);

            }
            Log.i(DEBUG_FOLDER_MANAGER,"new app: "+appPackageName);
            Log.i(DEBUG_FOLDER_MANAGER,"new app list: "+convertArrayToString(newAppList.toArray(new String[0])));
            Log.i(DEBUG_FOLDER_MANAGER,"add to folder: "+ folderName);

        }

        ContentValues cv = new ContentValues();
        cv.put(FolderContract.APPS_INSIDE,convertArrayToString(newAppList.toArray(new String[0])));

        helper.update(
                FolderContract.TABLE_NAME,
                cv,
                FolderContract.FOLDER_NAME+ " = ? ",
                new String[]{
                        folderName
                }
        );
    }

    private static void updateFolderName(String folderName, String newName, FolderDataHelper helper){

        ContentValues cv = new ContentValues();
        cv.put(FolderContract.FOLDER_NAME,newName);

        helper.update(
                FolderContract.TABLE_NAME,
                cv,
                FolderContract.FOLDER_NAME+ " = ? ",
                new String[]{
                        folderName
                }
        );
    }

    private static String[] getAppsBelongingToFolder(String folderName, FolderDataHelper helper, Context ctx){
        Cursor folder = null;

        try{
            folder = helper.read(
                    FolderContract.TABLE_NAME,
                    new String[]{
                            FolderContract.APPS_INSIDE
                    },
                    FolderContract.FOLDER_NAME+ " = ? ",
                    new String[]{
                            folderName
                    },
                    ""
            );

            if(folder.moveToFirst()){
                String[] appPackagesInFolder = convertStringToArray(folder.getString(folder.getColumnIndex(FolderContract.APPS_INSIDE)));
                ArrayList<String> appPackages = new ArrayList<>();
                for(String appPackage : appPackagesInFolder){
                    if(isAppInstalled(ctx,appPackage)){
                        appPackages.add(appPackage);
                    }
                }
                return appPackages.toArray(new String[appPackages.size()]);
            }else{
                return null;
            }

        }finally {
            if(folder != null)
                folder.close();
        }
    }

    private static void removeFolderFromDB(String folderName, FolderDataHelper helper){
        helper.delete(
                FolderContract.TABLE_NAME,
                FolderContract.FOLDER_NAME+ " = ? ",
                new String[]{
                        folderName
                }
        );
    }

    public static FolderDataHelper getFolderDataHelper(Context ctx){
        return new FolderDataHelper(
                ctx,
                FolderContract.TABLE_NAME,
                FolderContract.VERSION,
                FolderContract.CREATE,
                FolderContract.DELETE
        );
    }

    private static void addFolderToDB(String folderName, String appA, String appB, int x, int y, Context ctx){
        FolderDataHelper helper = null;
        try{
            helper = getFolderDataHelper(ctx);

            Log.i("Add Folder To DB","x: "+x+" y: "+y);

            ContentValues cv = new ContentValues();
            cv.put(FolderContract.FOLDER_NAME,folderName);
            cv.put(FolderContract.X_POSITION,x);
            cv.put(FolderContract.Y_POSITION,y);
            cv.put(FolderContract.APPS_INSIDE,convertArrayToString(new String[]{appA,appB}));

            helper.write(
                    FolderContract.TABLE_NAME,
                    cv,
                    "",
                    new String[]{
                            FolderContract.FOLDER_NAME,
                            FolderContract.X_POSITION,
                            FolderContract.Y_POSITION,
                            FolderContract.APPS_INSIDE
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

    public static void convertFolderPoints(Context ctx){
        ArrayList<FolderView> folders = getFoldersFromDB(ctx);
        for(FolderView folder : folders){
            updateFolderPosition(Util.pixelToRaster(folder.getPosition(),Util.getDiam(ctx),Util.getPadding(ctx),ctx),ctx,folder.folderName);
        }
    }

    private static String strSeparator = "__,__";

    private static String convertArrayToString(String[] array){
        String str = "";
        for (int i = 0;i<array.length; i++) {
            str = str+array[i];
            // Do not append comma at the end of last element
            if(i<array.length-1){
                str = str+strSeparator;
            }
        }
        return str;
    }

    public static String[] convertStringToArray(String str){
        String[] arr = str.split(strSeparator);
        return arr;
    }

    public static boolean isNameValid(String name,ArrayList<FolderView> folders){
        return (name != null && name != "") && !doesFolderExist(name, folders);
    }

    public static boolean doesFolderExist(String name, ArrayList<FolderView> folders){
        for(FolderView folder : folders){
            if(folder.getFolderName().equals(name))
                return true;
        }

        return false;
    }

}
