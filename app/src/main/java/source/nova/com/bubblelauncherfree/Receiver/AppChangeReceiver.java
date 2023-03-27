package source.nova.com.bubblelauncherfree.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import source.nova.com.bubblelauncherfree.Util.Message;

/**
 * Created by joshua on 07.07.16.
 */
public class AppChangeReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("not eventbus","app deinstalled");
        EventBus.getDefault().post(new Message(Message.APP_DEINSTALL,
                intent.getData().toString().replace("package:","")));
    }
}
