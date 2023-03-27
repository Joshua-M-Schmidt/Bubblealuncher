package source.nova.com.bubblelauncherfree.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;

import org.greenrobot.eventbus.EventBus;

import source.nova.com.bubblelauncherfree.Settings.SettingsActivity;
import source.nova.com.bubblelauncherfree.Settings.SettingsActivity2;
import source.nova.com.bubblelauncherfree.Util.Message;

/**
 * Created by joshua on 11.07.16.
 */
public class WallPaperChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        EventBus.getDefault().post(new Message(Message.WALLP_CHANGE,
                PreferenceManager.getDefaultSharedPreferences(context).getBoolean(SettingsActivity2.PREF_WALLPAPER_TOGGLE,false)));

    }
}
