package www.shanhuang.org.feedr;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class WatchListenerService extends WearableListenerService {

    private final String MEAL_PLAN = "/meal_plan";
    private final String MAP = "/map";
    private final String PREFERENCES = "/preferences";
    private final String PREFS_CHANGE = "/prefs_change";

    private GoogleApiClient mApiClient;

    @Override
    public void onCreate() {
        super.onCreate();
        //initialize the googleAPIClient for message passing

        mApiClient = new GoogleApiClient.Builder( this )
                .addApi( Wearable.API )
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                    }
                })
                .build();

    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // get the intent, if the string extra says get suggestion, use sendMessage to go get message
        String note = intent.getStringExtra("note");
        mApiClient.connect();
        Log.e("api connected", mApiClient.isConnecting() + "");
//        while (mApiClient.isConnecting()) {}
        Log.e("api connected", mApiClient.isConnected() + "");
        if (note.equals("get_suggestion")) {
            WatchMessenger.sendMessage(mApiClient, MEAL_PLAN, "");
        }
        return START_STICKY;
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String new_data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        if( messageEvent.getPath().equalsIgnoreCase( MEAL_PLAN ) ) {
            // TODO: do something with the message we received from mobile
            Log.e("data received", new_data);
            Intent SuggestionIntent = new Intent(this, SuggestionActivity.class);
//            SuggestionIntent.putExtra("data", new_data);
//            startActivity(SuggestionIntent);

        } else if (messageEvent.getPath().equalsIgnoreCase( MAP )){
            // TODO: do something with the message and start MapActivity
            Intent MapIntent = new Intent(this, MapActivity.class);
            MapIntent.putExtra("map", new_data);
            startActivity(MapIntent);

        } else if (messageEvent.getPath().equalsIgnoreCase(PREFERENCES)) {
            // TODO: update preferences
            Intent preferencesIntent = new Intent(this, PreferenceActivity.class);
            preferencesIntent.putExtra("prefs", new_data);
            startActivity(preferencesIntent);

        } else {
            super.onMessageReceived(messageEvent);
        }

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            DataItem item = event.getDataItem();
            if (item.getUri().getPath().equalsIgnoreCase(PREFS_CHANGE)) {
                DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                int cost = map.getInt("cost");
                int distance = map.getInt("distance");
            }
        }
    }
}
