package source.nova.com.bubblelauncherfree.Util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.Iterator;
import java.util.List;

public class FetchCategory extends AsyncTask<Void, Void, Void> {

    public final static String GOOGLE_URL = "https://play.google.com/store/apps/details?id=";
    public static final String ERROR = "error";

    private final String TAG = FetchCategory.class.getSimpleName();
    private PackageManager pm;

    private Context ctx;

    public FetchCategory(Context ctx){
        this.ctx = ctx;
    }

    @Override
    protected Void doInBackground(Void... errors) {
        String category;
        pm = ctx.getPackageManager();
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        Iterator<ApplicationInfo> iterator = packages.iterator();
        while (iterator.hasNext()) {
            ApplicationInfo packageInfo = iterator.next();
            String query_url = GOOGLE_URL + packageInfo.packageName;
            Log.i(TAG, query_url);
            category = getCategory(query_url);
            Log.i(TAG, packageInfo.packageName + " "+ category);
            // store category or do something else
        }
        return null;
    }


    private String getCategory(String query_url) {
        if (false) {
            //manage connectivity lost
            return ERROR;
        } else {
            try {
                Document doc = Jsoup.connect(query_url).get();
                Element link = doc.select("span[itemprop=genre]").first();
                return link.text();
            } catch (Exception e) {
                return ERROR;
            }
        }
    }
}
