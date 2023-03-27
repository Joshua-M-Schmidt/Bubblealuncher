package source.nova.com.bubblelauncherfree.Async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.Util.DataObj;

import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getAppsFromDevice1;
import static source.nova.com.bubblelauncherfree.AppManager.AppManager.getAppsSortByAlphabet;
import static source.nova.com.bubblelauncherfree.AppManager.AppManager.initAppPosition;
import static source.nova.com.bubblelauncherfree.AppManager.AppManager.writeAppsToDB;

public class FetchApps extends AsyncTask<Void, Void, Boolean> {

    public OnAppsFetched listener;
    public OnProgress pListener;

    public interface OnAppsFetched{
        void onAppsFetched();
    }

    public interface OnProgress{
        void onProgress(String message,boolean color);
    }

    private Context ctx;

    public FetchApps(Context ctx, OnAppsFetched listener, OnProgress pListener) {
        this.ctx = ctx;
        this.listener = listener;
        this.pListener = pListener;
    }

    protected Boolean doInBackground(Void... Void) {
        try{

            //this function is only used once during the first use
            //therefore the app data is feched from the device
            ArrayList<DataObj> toWrite = new ArrayList<>();

            pListener.onProgress("get apps from device",true);

            ArrayList<DataObj> sortedAppList = getAppsSortByAlphabet(getAppsFromDevice1(this.ctx));

            for(DataObj obj : sortedAppList){
                pListener.onProgress("... "+obj.name,false);

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            pListener.onProgress(sortedAppList.size()+" apps found",true);

            //add the sorted apps to the list with the settings view

            pListener.onProgress("save apps to database",true);

            toWrite.addAll(sortedAppList);

            writeAppsToDB(toWrite, this.ctx);

            // assigns a position to all apps in the DB

            pListener.onProgress("write app position",true);

            initAppPosition(this.ctx, toWrite);

            pListener.onProgress("loading done",true);

        }catch (Exception e){
            e.printStackTrace();
        }
        return true;
    }

    protected void onPostExecute(Boolean result) {
        listener.onAppsFetched();
    }
}

