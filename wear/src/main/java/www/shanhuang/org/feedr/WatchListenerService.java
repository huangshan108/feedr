package www.shanhuang.org.feedr;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class WatchListenerService extends WearableListenerService {

    private final String MEAL_PLAN = "/meal_plan";
    private final String MAP = "/map";
    private String new_data, old_data="", old_map ="";

    public void WatchListenerService() {}

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase( MEAL_PLAN ) ) {

            new_data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            if (new_data.compareTo(old_data)!=0) {
                // make sure the new data we receive is not the same as the data we just received
                old_data = new_data;

                // TODO: do something with the message we received from mobile
                Intent SuggestionIntent = new Intent(this, SuggestionActivity.class);
                SuggestionIntent.putExtra("data", new_data);
                startActivity(SuggestionIntent);
            }

        } else if (messageEvent.getPath().equalsIgnoreCase( MAP )){
            new_data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            if (new_data.compareTo(old_map) != 0) {
                old_map = new_data;
                // TODO: do something with the message and start MapActivity
                Intent MapIntent = new Intent(this, MapActivity.class);
                MapIntent.putExtra("map", new_data);
                startActivity(MapIntent);
            }
        } else {

            super.onMessageReceived( messageEvent );
        }

    }


}
