package source.nova.com.bubblelauncherfree.Async;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.CustomViews.FolderView;
import source.nova.com.bubblelauncherfree.FolderManager.FolderManager;
import source.nova.com.bubblelauncherfree.MainActivity;
import source.nova.com.bubblelauncherfree.Theme;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Point;
import source.nova.com.bubblelauncherfree.Util.Util;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getAllAppsFromDB;
import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getDialerPackageName;
import static source.nova.com.bubblelauncherfree.AppManager.AppManager.updateAppPosition;
import static source.nova.com.bubblelauncherfree.FolderManager.FolderManager.updateFolderPosition;
import static source.nova.com.bubblelauncherfree.MainActivity.asize;

public class SortApps extends AsyncTask<Void, Void, Boolean> {

    public OnAppsSort listener;

    public interface OnAppsSort{
        void onAppsSorted();
    }

    private Context ctx;

    public SortApps(Context ctx, OnAppsSort listener) {
        this.ctx = ctx;
        this.listener = listener;
    }


    protected Boolean doInBackground(Void... Void) {
        sortAllApps();
        return true;
    }

    protected void onPostExecute(Boolean result) {
        listener.onAppsSorted();
    }

    String TAG = "debugSort";

    private void sortAllApps(){
        ArrayList<DataObj> apps = AppManager.getAppsFromDB(ctx);
        ArrayList<FolderView> folders = FolderManager.getFoldersFromDB(ctx);

        ArrayList<Point> pattern;
        String p = PreferenceManager.getDefaultSharedPreferences(ctx).getString(Theme.PATTERN_TAG,Theme.PATTERN_BIG_ISLAND);
        Log.i(TAG,p+" pp");
        switch (p){
            case Theme.PATTERN_BIG_ISLAND:
                pattern = AppManager.getBigPatternIsland();
                break;
            case Theme.PATTERN_BIG_RECTANGLE:
                pattern = AppManager.getBigPatternRectangle();
                break;
            case Theme.PATTERN_SCATTERED:
                pattern = AppManager.getScatteredPattern();
                break;
            case Theme.PATTERN_TWO_ISLANDS:
                pattern = AppManager.getTwoIslandPattern();
                break;
            default:
                pattern = AppManager.getBigPatternIsland();
                break;
        }

        ArrayList<Point> points = AppManager.shiftGorup(pattern,new Point(3,3));
        Random r = new Random();

        Point center;
        if(points.size() > 12){
            center = points.get(12);
        }else{
            center = points.get(7);
        }

        Util.centerPointInScreen(ctx,center);

        Log.i(TAG,"apps size "+apps.size());
        Log.i(TAG, "folder size "+folders.size());
        Log.i(TAG, "points size "+points.size());

        for(FolderView folder : folders){
            int n = r.nextInt(points.size());
            setFolderPosition(folder.folderName,points.get(n));
            points.remove(n);
        }

        Log.i(TAG, "after folder size "+points.size());
        Log.i(TAG,"after app size "+apps.size());

        for(DataObj app : apps){
            Log.i(TAG, "add"+app.package_name+ " random "+r.nextInt(points.size()));
            int n = r.nextInt(points.size());
            setAppPosition(app.package_name,points.get(n));
            AppManager.addAppToHomeScreen(app.package_name,ctx);
            points.remove(n);
        }

        Log.i(TAG, "points1 size "+points.size());


        ArrayList<DataObj> appsF = AppManager.getAppsNotOnHomescreen(ctx);



        for(int i = 0; i < points.size(); i++){
            setAppPosition(appsF.get(i).package_name,points.get(i));
            AppManager.addAppToHomeScreen(appsF.get(i).package_name,ctx);
        }

        int counter2 = 0;

        /*while(points.size()-1 > 0){
            Log.i(TAG, "points2 size "+points.size());
            Log.i(TAG, "apps not on homescreen "+appsF.size());
            Log.i("repapp1",counter2+ " counter2");
            Log.i("repapp1",appsF.get(counter2).package_name);
            Log.i("repapp1",points.size() +" size");
            Log.i("repapp1",points.get(counter2).toString() +" countt");
            setAppPosition(appsF.get(counter2).package_name,points.get(counter2));
            AppManager.addAppToHomeScreen(appsF.get(counter2).package_name,ctx);
            Log.i(TAG, "add"+appsF.get(counter2).package_name+ " random "+r.nextInt(points.size()));
            points.remove(counter2);
            counter2++;
        }*/


        Log.i(TAG, "points3 size "+points.size());
    }

    private void setAppPosition(String app, Point newPosition) {
        Log.i("set_app_pos",app+" "+newPosition.toString());
        updateAppPosition(newPosition,ctx,app);
    }

    private void setFolderPosition(String folder, Point newPosition) {
        Log.i("set_fold_pos",folder+" "+newPosition.toString());
        updateFolderPosition(newPosition,ctx,folder);
    }

    public void backReset(){
        ArrayList<DataObj> apps = AppManager.getAllAppsFromDB(ctx);
        ArrayList<FolderView> folders = FolderManager.getFoldersFromDB(ctx);
        for(DataObj app : apps){
            AppManager.removeFolderAttrFromApp(ctx,app.package_name);
        }

        for(FolderView folder : folders){
            FolderManager.deleteFolder(folder.folderName,ctx);
        }

        ArrayList<Point> points = AppManager.getPoints(apps.size());

        for(int i = 0; i < points.size(); i++){
            AppManager.updateAppPosition(points.get(i),ctx,apps.get(i).package_name);
        }
    }

    public static ArrayList<DataObj> getNotContainingSublist(ArrayList<DataObj> apps, ArrayList<String> packages) {
        ArrayList<DataObj> retlist = new ArrayList<>();
        retlist.addAll(apps);
        for(DataObj app : apps){
            for(String str : packages){
                if(app.package_name.equals(str)){
                    retlist.remove(app);
                }
            }
        }

        Log.i("Group_sort","Sublist "+retlist.size()+" old "+apps.size());

        return retlist;
    }


    public static ArrayList<String> defaultProgramms(Context ctx){
        ArrayList<String> defaultPackages = new ArrayList<>();

        // default browser
        Intent browserIntent = new Intent("android.intent.action.VIEW", Uri.parse("http://"));
        ResolveInfo resolveInfo = ctx.getPackageManager().resolveActivity(browserIntent, PackageManager.MATCH_DEFAULT_ONLY);
        defaultPackages.add(resolveInfo.activityInfo.packageName);

        // default caller
        defaultPackages.add(getDialerPackageName(ctx));

        // camera
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        ResolveInfo resolveInfoCam = ctx.getPackageManager().resolveActivity(cameraIntent,PackageManager.MATCH_DEFAULT_ONLY);
        defaultPackages.add(resolveInfoCam.activityInfo.packageName);

        // gallery
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(android.content.Intent.ACTION_VIEW);
        galleryIntent.setType("image/*");
        galleryIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ResolveInfo resolveInfoGal = ctx.getPackageManager().resolveActivity(galleryIntent,PackageManager.MATCH_DEFAULT_ONLY);
        defaultPackages.add(resolveInfoGal.activityInfo.packageName);

        // music
        Intent musicIntent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC);
        ResolveInfo resolveInfoMusic = ctx.getPackageManager().resolveActivity(musicIntent,PackageManager.MATCH_DEFAULT_ONLY);
        defaultPackages.add(resolveInfoMusic.activityInfo.packageName);

        ArrayList<String> commonlyUsed = new ArrayList<>();
        commonlyUsed.add("com.instagram.android");
        commonlyUsed.add("com.google.android.gm");
        commonlyUsed.add("com.android.vending");
        commonlyUsed.add("com.spotify.music");
        commonlyUsed.add("com.snapchat.android");
        commonlyUsed.add("com.google.android.youtube");
        commonlyUsed.add("com.android.contacts");
        commonlyUsed.add("com.whatsapp");

        for(String app : commonlyUsed){
            if(defaultPackages.size() < 9){
                if(AppManager.isAppInDB(ctx,app)){
                    defaultPackages.add(app);
                }
            }
        }

        return defaultPackages;
    }


    public void createFolderByDataObj(ArrayList<DataObj> addApps,String name){

        if(addApps.size() > 1){
            Point p = AppManager.getFreePositionAll1(getAllAppsFromDB(ctx),FolderManager.getFoldersFromDB(ctx));
            FolderManager.createFolder(name,addApps.get(0).package_name,addApps.get(1).package_name,p.x,p.y,ctx);

            FolderView newFolder = FolderManager.getNewFolderView(
                    ctx,
                    name,
                    p,
                    new ArrayList<>(Arrays.asList(new String[]{
                            addApps.get(0).package_name,
                            addApps.get(1).package_name
                    }))
            );


            addApps.remove(0);
            addApps.remove(0);

            int w = 0;

            if(PreferenceManager.getDefaultSharedPreferences(ctx)
                    .getString("app_icon_style","circle").equals("hexagon") ||
                    PreferenceManager.getDefaultSharedPreferences(ctx)
                            .getString("app_icon_style","circle").equals("hexagon-outline")){
                w = (int)(2*Util.getHexOffsetX(MainActivity.asize/2));

            }

            for(DataObj app : addApps){
                FolderManager.addAppToFolder(name,app.package_name,ctx);
                ArrayList<String> newlist = newFolder.getAppsContained();
                newlist.add(app.package_name);
                newFolder.setImageBitmap(FolderManager.getHexFolderIcon(newlist, asize,asize-w, ctx,newFolder.getFolderName(),false));
            }
        }
    }
}

