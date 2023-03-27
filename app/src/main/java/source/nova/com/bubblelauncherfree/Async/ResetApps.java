package source.nova.com.bubblelauncherfree.Async;

import android.content.Context;
import android.os.AsyncTask;

import java.util.ArrayList;

import source.nova.com.bubblelauncherfree.AppManager.AppManager;
import source.nova.com.bubblelauncherfree.CustomViews.FolderView;
import source.nova.com.bubblelauncherfree.FolderManager.FolderManager;
import source.nova.com.bubblelauncherfree.Util.DataObj;
import source.nova.com.bubblelauncherfree.Util.Point;

public class ResetApps extends AsyncTask<Void, Void, Boolean> {

    public ResetApps.OnAppsReset listener;

    public interface OnAppsReset {
        void onAppsReseted();
    }

    private Context ctx;

    public ResetApps(Context ctx, ResetApps.OnAppsReset listener) {
        this.ctx = ctx;
        this.listener = listener;
    }


    protected Boolean doInBackground(Void... Void) {
        backReset();
        return true;
    }


    protected void onPostExecute(Boolean result) {
        listener.onAppsReseted();
    }

    public void backReset() {
        ArrayList<DataObj> apps = AppManager.getAllAppsFromDB(ctx);
        ArrayList<FolderView> folders = FolderManager.getFoldersFromDB(ctx);
        for (DataObj app : apps) {
            AppManager.removeFolderAttrFromApp(ctx, app.package_name);
            AppManager.unhideApp(app.package_name,ctx);
            AppManager.addAppToHomeScreen(app.package_name,ctx);
        }

        for (FolderView folder : folders) {
            FolderManager.deleteFolder(folder.folderName, ctx);
        }

        ArrayList<Point> points = AppManager.getPoints(apps.size());

        for (int i = 0; i < points.size(); i++) {
            AppManager.updateAppPosition(points.get(i), ctx, apps.get(i).package_name);
        }
    }

}
