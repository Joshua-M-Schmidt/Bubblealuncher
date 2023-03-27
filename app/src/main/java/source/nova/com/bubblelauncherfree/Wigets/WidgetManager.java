package source.nova.com.bubblelauncherfree.Wigets;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class WidgetManager {

    public static final String WIDGET_INFO_KEY = "widget_infos_key";

    private ArrayList<WidgetInfo> widgetInfos;
    private Context ctx;
    private SharedPreferences prefs;
    private Gson gson;

    public WidgetManager(Context ctx){
        this.ctx = ctx;

        gson = new Gson();
        prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public ArrayList<WidgetInfo> getWidgetInfos(){
        String widgetObjects = prefs.getString(WIDGET_INFO_KEY,"");
        if(!widgetObjects.isEmpty()){
            Type type = new TypeToken<ArrayList<WidgetInfo>>(){}.getType();
            ArrayList<WidgetInfo> widgetInfosFromJSON = gson.fromJson(widgetObjects,type);
            return widgetInfosFromJSON;
        }else{
            return new ArrayList<>();
        }
    }

    public void addWidgetInfo(WidgetInfo widgetInfo){
        ArrayList<WidgetInfo> infos = getWidgetInfos();
        infos.add(widgetInfo);
        saveWidgetInfoList(infos);
    }

    public void removeWidgetInfo(String id){
        ArrayList<WidgetInfo> infos = getWidgetInfos();
        WidgetInfo toRemove = null;
        for(WidgetInfo info : infos){
            if(info.getTag().equals(id)){
                toRemove = info;
            }
        }

        if(toRemove != null) {
            infos.remove(toRemove);
        }

        saveWidgetInfoList(infos);
    }

    public WidgetInfo getWidgetInfo(String tag){
        ArrayList<WidgetInfo> infos = getWidgetInfos();
        for(WidgetInfo wi : infos){
            if(wi.getTag().equals(tag)){
                return wi;
            }
        }

        return null;
    }

    private void saveWidgetInfoList(ArrayList<WidgetInfo> widgetInfos){
        Type type = new TypeToken<ArrayList<WidgetInfo>>(){}.getType();
        String widgetObjects = gson.toJson(widgetInfos,type);
        prefs.edit().putString(WIDGET_INFO_KEY,widgetObjects).apply();
    }

    public void updateWidgetPosition(String tag, int x, int y){
        ArrayList<WidgetInfo> infos = getWidgetInfos();
        for(WidgetInfo wi : infos){
            if(wi.getTag().equals(tag)){
                wi.setX(x);
                wi.setY(y);
            }
        }

        saveWidgetInfoList(infos);
    }

    public void updateWidgetSize(String tag, int x, int y){
        ArrayList<WidgetInfo> infos = getWidgetInfos();
        for(WidgetInfo wi : infos){
            if(wi.getTag().equals(tag)){
                wi.setW(x);
                wi.setH(y);
            }
        }

        saveWidgetInfoList(infos);
    }

}
