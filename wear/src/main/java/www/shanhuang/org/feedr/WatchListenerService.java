package www.shanhuang.org.feedr;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WatchListenerService extends WearableListenerService {
    private static final String START_ACTIVITY = "/start_activity";
    private String new_data, old_data="";

    public void WatchListenerService() {}

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( START_ACTIVITY ) ) {

            new_data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            if (new_data.compareTo(old_data)!=0) {
                // make sure the new data we receive is not the same as the data we just received
                old_data = new_data;

                // TODO: do something with the message we received from mobile

            }

        } else {
            super.onMessageReceived( messageEvent );
        }

    }


}
