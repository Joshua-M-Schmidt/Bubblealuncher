package source.nova.com.bubblelauncherfree.Async;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.Util.DataObj;

public class FetchCategories extends AsyncTask<Void, Void, Boolean> {

    public OnCategoriesFetched listener;

    public interface OnCategoriesFetched{
        void onCategoriesFetched(ArrayList<DataObj> apps);
    }

    private Context ctx;
    private ArrayList<DataObj> apps;

    public FetchCategories(Context ctx, ArrayList<DataObj> apps, OnCategoriesFetched listener) {
        this.ctx = ctx;
        this.listener = listener;
        this.apps = apps;
    }


    protected Boolean doInBackground(Void... Void) {
        try{
            for(int i = 0; i < apps.size(); i++){

                ApplicationInfo applications = ctx.getPackageManager().getApplicationInfo(apps.get(i).package_name,0);

                if ((applications.flags & ApplicationInfo.FLAG_SYSTEM) == 1)
                {
                    //This is System application
                    apps.get(i).category = "OTHER";
                    AppManager.setAppCategory(ctx,apps.get(i).package_name, "OTHER");
                }
                else
                {
                    String category = parseAndExtractCategory(applications.packageName);
                    apps.get(i).category = category;
                    AppManager.setAppCategory(ctx,apps.get(i).package_name, category);
                    Log.i("category "+i+" of "+apps.size(),applications.packageName+" "+parseAndExtractCategory(applications.packageName));

                }

            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    protected void onPostExecute(Boolean result) {
        listener.onCategoriesFetched(apps);
    }

    //Main URL for each app on Play Store
    public static final String APP_URL = "https://play.google.com/store/apps/details?id=";


    public static final String CATEGORY_STRING = "category/";

    public static String parseAndExtractCategory(String packageName) {

        //You can pass hl={language_code} for get category in some other langauage also other than English.
        //String url = APP_URL + packageName + "&hl=" + appContext.getString(R.string.app_lang);

        String url = APP_URL + packageName + "&hl=en"; //{https://play.google.com/store/apps/details?id=com.example.app&hl=en}
        String appCategoryType = null;
        String appName = null;

        try {

            if (true) {
                Document doc;
                try {
                    doc = Jsoup.connect(url).get();

                    if (doc != null) {
                        //Extract category String from a <anchor> tag value directly.
                        //NOTE: its return sub category text, for apps with multiple sub category.
                        //Comment this method {METHOD_1}, if you wish to extract category by href value.
                        Element CATEGORY_SUB_CATEGORY = doc.select("a[itemprop=genre]").first();
                        if (CATEGORY_SUB_CATEGORY != null) {
                            appCategoryType = CATEGORY_SUB_CATEGORY.text();
                        }
                        // Use below code only if you wist to extract category by href value.
                        //Its return parent or Main Category Text for all app.
                        //Comment this method {METHOD_2}, If you wihs to extract category from a<anchor> value.
                        if (appCategoryType == null || appCategoryType.length() < 1) {
                            Elements text = doc.select("a[itemprop=genre]");

                            if (text != null) {
                                if (appCategoryType == null || appCategoryType.length() < 2) {
                                    String href = text.attr("abs:href");
                                    if (href != null && href.length() > 4 && href.contains(CATEGORY_STRING)) {
                                        appCategoryType = getCategoryTypeByHref(href);
                                    }
                                }
                            }
                        }

                        if (appCategoryType != null && appCategoryType.length() > 1) {
                            /**
                             * Ger formatted category String by removing special character.
                             */
                            appCategoryType = replaceSpecialCharacter(appCategoryType);
                        }
                    }
                } catch (IOException e) {
                    //appCategoryType = appContext.getString(R.string.category_others);
                    appCategoryType = "OTHERS";
                    e.printStackTrace();
                }
            } else {
                //appCategoryType = appContext.getString(R.string.category_others);
                appCategoryType = "OTHERS";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appCategoryType;
    }

    public static final int cat_size = 9;

    public static final String CATEGORY_GAME_STRING = "GAME_";

    public static String getCategoryTypeByHref(String href) {
        String appCategoryType = null;
        try {
            appCategoryType = href.substring((href.indexOf(CATEGORY_STRING) + cat_size), href.length());
            if (appCategoryType != null && appCategoryType.length() > 1) {
                if (appCategoryType.contains(CATEGORY_GAME_STRING)) {
                    //appCategoryType = appContext.getString(R.string.games);
                    appCategoryType = "GAMES";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appCategoryType;
    }

    /**
     * @param appCategoryType
     * @return: formatted String
     */
    public static String replaceSpecialCharacter(String appCategoryType) {
        try {
            //Find and Replace '&amp;' with '&' in category Text
            if (appCategoryType.contains("&amp;")) {
                appCategoryType = appCategoryType.replace("&amp;", " & ");
            }

            //Find and Replace '_AND_' with ' & ' in category Text
            if (appCategoryType.contains("_AND_")) {
                appCategoryType = appCategoryType.replace("_AND_", " & ");
            }

            //Find and Replace '_' with ' ' <space> in category Text
            if (appCategoryType.contains("_")) {
                appCategoryType = appCategoryType.replace("_", " ");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return appCategoryType;
    }
}

