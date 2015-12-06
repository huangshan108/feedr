package www.shanhuang.org.feedr;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MobileListenerService extends WearableListenerService {
    private static final String GET_SUGGESTION = "/get_suggestion";
    private GoogleApiClient mApiClient;

    private static String MEAL_PLAN = "/meal_plan";
    private static String MAP = "/map";
    private final String PREFS_CHANGE = "/prefs_change";

    private String zip, latLong;

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
        mApiClient.connect();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("start", "starting service");
        String values = intent.getStringExtra("zip:latlong");
        String[] parsed = values.split(":");
        zip = parsed[0];
        latLong = parsed[1] + ":" + parsed[2];
        Log.e("ls zip", zip);
        Log.e("ls latlong", latLong);

//        /** Connect the API **/
//        mApiClient = new GoogleApiClient.Builder( this )
//                .addApi(Wearable.API)
//                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//                    @Override
//                    public void onConnected(Bundle connectionHint) {
//                        /* Successfully connected */
//                    }
//
//                    @Override
//                    public void onConnectionSuspended(int cause) {
//                        /* Connection was interrupted */
//                    }
//                })
//                .build();

        sendMessage(MEAL_PLAN,Opentable.getRestaurants(zip));
        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e("message received", "got it");
        if( messageEvent.getPath().equalsIgnoreCase(MEAL_PLAN) ) {
            // TODO: get new suggestion from restaurant, then send the info back to the wear

            String data = Opentable.getRestaurants(zip);
            sendMessage(MEAL_PLAN, data);

        } else if (messageEvent.getPath().equalsIgnoreCase(PREFS_CHANGE)) {
            // TODO: received preferences change from watch, save preferences to persist
            savePreferences(messageEvent);
        } else {
            super.onMessageReceived( messageEvent );
        }
    }


    /*
     * Gets saved preferences and sends them to the watch.
     */
    protected void syncPreferences() {
        SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getApplicationContext());

        PutDataMapRequest putRequest = PutDataMapRequest.create(PREFS_CHANGE);
        DataMap map = putRequest.getDataMap();
        map.putInt("cost", prefs.getInt("cost", 1));
        map.putInt("distance", prefs.getInt("distance", 1));
        Wearable.DataApi.putDataItem(mApiClient, putRequest.asPutDataRequest());
    }

    protected void savePreferences(MessageEvent messageEvent) {
        // TODO: save new preferences received
    }

    protected void sendMessage(final String path, final String data) {
        new Thread( new Runnable() {
            @Override
            public void run() {
                Log.e("connected nodes","waiting");
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await(10, TimeUnit.SECONDS);

                Log.e("connected nodes", nodes.getNodes().size() + "");
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, data.getBytes() ).await();
                }
            }
        }).start();
    }

}
