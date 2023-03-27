package source.nova.com.bubblelauncherfree.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import source.nova.com.bubblelauncherfree.Util.Message;

/**
 * Created by joshua on 11.07.16.
 */
public class AppInstallReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("not eventbus","app installed");
        EventBus.getDefault().post(new Message(Message.APP_INSTALL,
                intent.getData().toString().replace("package:","")));

    }
}
