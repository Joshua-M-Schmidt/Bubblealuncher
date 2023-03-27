package source.nova.com.bubblelauncherfree.Async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.Util.Contacts;
import source.nova.com.bubblelauncherfree.Util.DataObj;

public class SearchBarData extends AsyncTask<Void, Void, Boolean> {

    public SearchBarData.OnContactsLoaded listener;

    public interface OnContactsLoaded {
        void onContactsLoaded(ArrayList<Contacts.ContactInfo> contacts, ArrayList<DataObj> apps);
    }

    private Context ctx;
    AppManager appManager;

    public SearchBarData(Context ctx, SearchBarData.OnContactsLoaded listener) {
        this.ctx = ctx;
        this.listener = listener;
        appManager = new AppManager(ctx);
    }

    protected void onPreExecute() {

    }

    protected Boolean doInBackground(Void... Void) {
        ArrayList<Contacts.ContactInfo> contacts = Contacts.getAll(ctx);
        ArrayList<DataObj> apps = appManager.getAllAppsFromDB(ctx);
        listener.onContactsLoaded(contacts,apps);
        return true;
    }


    protected void onPostExecute(Boolean result) {

    }
}
