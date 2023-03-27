package source.nova.com.bubblelauncherfree.Async;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.Util.DataObj;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getDialerPackageName;

public class CreateFolder extends AsyncTask<Void, Void, Boolean> {

    public CreateFolder.OnFoldersCreate listener;

    public interface OnFoldersCreate{
        void onFoldersCreated();
    }

    private Context ctx;

    public CreateFolder(Context ctx, CreateFolder.OnFoldersCreate listener) {
        this.ctx = ctx;
        this.listener = listener;
    }



    protected Boolean doInBackground(Void... Void) {
        createFolderCategories();
        return true;
    }

    protected void onPostExecute(Boolean result) {
        listener.onFoldersCreated();
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

        Log.i("Group_folder","Sublist "+retlist.size()+" old "+apps.size());

        return retlist;
    }

    public void createFolderCategories(){
        ArrayList<DataObj> allApps = new ArrayList<>();
        allApps.addAll(getNotContainingSublist(AppManager.getAppsFromDB(ctx),defaultProgramms(ctx)));

        Log.i("Create folder"," folder function"+ allApps.size());

        ArrayList<DataObj> addApps = new ArrayList<>();

        for(DataObj app : allApps){
            Log.i("app_packages",app.package_name);
            if(app.category != null){
                if(app.category.equals("Art & Design") ||
                        app.category.equals("Comics") ||
                        app.category.equals("Entertainment") ||
                        app.category.equals("Sports") ||
                        app.category.equals("News & Magazines") ||
                        app.category.equals("Music & Audio") ||
                        app.category.equals("Auto & Vehicles") ||
                        app.category.equals("News & Magazines") ||
                        app.category.equals("Books & Reference")){
                    Log.i("media",app.package_name);
                    addApps.add(app);
                }
            }
        }

        createFolderByDataObj(addApps,"media");
        addApps.clear();

        for(DataObj app : allApps){
            Log.i("app packages",app.package_name);
            if(app.category != null){
                if(app.category.equals("Education")){
                    Log.i("edu",app.package_name);
                    addApps.add(app);
                }
            }
        }


        createFolderByDataObj(addApps,"edu");
        addApps.clear();

        for(DataObj app : allApps){
            Log.i("app packages",app.package_name);
            if(app.category != null){
                if(app.category.equals("Productivity") ||
                        app.category.equals("Lifestyle") ||
                        app.category.equals("Food & Drink") ||
                        app.category.equals("Finance") ||
                        app.category.equals("Medical") ||
                        app.category.equals("Parenting") ||
                        app.category.equals("Travel & Local") ||
                        app.category.equals("Dating") ||
                        app.category.equals("Health & Fitness") ||
                        app.category.equals("Beauty")){
                    Log.i("Personal",app.package_name);
                    addApps.add(app);
                }
            }
        }


        createFolderByDataObj(addApps,"personal");
        addApps.clear();

        for(DataObj app : allApps){
            Log.i("app packages",app.package_name);
            if(app.category != null){
                if(app.category.equals("Communication") ||
                        app.category.equals("Events") ||
                        app.category.equals("Social") ||
                        app.category.equals("Business")){
                    Log.i("com",app.package_name);
                    addApps.add(app);
                }
            }
        }


        createFolderByDataObj(addApps,"com");
        addApps.clear();

        for(DataObj app : allApps){
            Log.i("app packages",app.package_name);
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
                    Log.i("Communication",app.package_name);
                    addApps.add(app);
                }
            }
        }

        createFolderByDataObj(addApps,"games");
        addApps.clear();

        for(DataObj app : allApps){
            Log.i("app packages",app.package_name);
            if(app.category != null){
                if(app.category.equals("Entertainment")
                        ||  app.category.equals("Shopping")){
                    Log.i("Entertainment",app.package_name);
                    addApps.add(app);
                }
            }
        }

        createFolderByDataObj(addApps,"internet");
        addApps.clear();

        for(DataObj app : allApps){
            Log.i("app packages",app.package_name+" cat "+ app.category);
            if(app.category != null){
                if(app.category.equals("Maps & Navigation") ||
                        app.category.equals("Tools") ||
                        app.category.equals("House & Home") ||
                        app.category.equals("Photography") ||
                        app.category.equals("Video Players & Editors") ||
                        app.category.equals("Libraries & Demo") ||
                        app.category.equals("OTHERS") ||
                        app.category.equals("OTHER") ||
                        app.category.equals("Weather")){
                    Log.i("Tools",app.package_name);
                    addApps.add(app);
                }
            }
        }

        createFolderByDataObj(addApps,"tools");
        addApps.clear();

        for(DataObj app : allApps){
            if(app.package_name.startsWith("com.google") ){
                Log.i("google app",app.package_name);
                addApps.add(app);
            }
        }

        createFolderByDataObj(addApps,"google");
        addApps.clear();

        for(DataObj app : allApps){
            if(app.category != null){
                if(app.category.equals("Personalization")){
                    Log.i("android app",app.package_name);
                    addApps.add(app);
                }
            }
            if(app.package_name.startsWith("com.android")){
                Log.i("android app",app.package_name);
                addApps.add(app);
            }
        }

        createFolderByDataObj(addApps,"system");
        addApps.clear();
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

        for(DataObj app : addApps){
            AppManager.removeAppFromHomeScreen(app.package_name,ctx);
        }

        /*
        Log.i("debug","create folder name "+name);

        int asize = Util.getDiam(ctx);

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
                newFolder.setImageBitmap(FolderManager.getHexFolderIcon(newlist, asize,asize-w, ctx,newFolder.getFolderName()));
            }
        }*/
    }
}

