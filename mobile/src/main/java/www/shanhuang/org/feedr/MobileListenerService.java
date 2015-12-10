package www.shanhuang.org.feedr;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

public class MobileListenerService extends WearableListenerService {
    private static final String GET_SUGGESTION = "/get_suggestion";
    private GoogleApiClient mApiClient;

    private static String MEAL_PLAN = "/meal_plan";
    private static String MAP = "/map";
    private static String IMAGE = "/image";
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

        String command = intent.getStringExtra("command");
        if (command.equalsIgnoreCase("start")){
            String values = intent.getStringExtra("zip:latlong");
            String[] parsed = values.split(":");
            zip = parsed[0];
            latLong = parsed[1] + ":" + parsed[2];
            sendMessage(MEAL_PLAN,values + "||" + Opentable.getRestaurants(zip));
        } else if (command.equalsIgnoreCase("map")) {
            String encoding = intent.getStringExtra("encoding");
            String location_data = intent.getStringExtra("location_data");
            sendMessage(MAP, encoding + "_splitmeherepleasenow_" + location_data);
        }
        return START_STICKY;
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.e("message received", "got it");
        if( messageEvent.getPath().equalsIgnoreCase(MEAL_PLAN) ) {
            // TODO: get new suggestion from restaurant, then send the info back to the wear

            String data = Opentable.getRestaurants(zip);
            sendMessage(MEAL_PLAN, zip+":"+latLong+"||"+data);

        } else if (messageEvent.getPath().equalsIgnoreCase(PREFS_CHANGE)) {
            // TODO: received preferences change from watch, save preferences to persist
            savePreferences(messageEvent);
        } else if (messageEvent.getPath().equalsIgnoreCase(MAP)) {

            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra("locations", new String(messageEvent.getData()));
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mapIntent);
        }else if (messageEvent.getPath().equalsIgnoreCase(IMAGE)) {
            String url = new String(messageEvent.getData(), StandardCharsets.UTF_8);
            Log.e("request received", url);
            new LoadImage().execute(url);

            int WAIT_TIME = 3000;
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Asset bitmap_image = createAssetFromBitmap(bitmap);
                    PutDataRequest request = PutDataRequest.create("/image_2");
                    request.putAsset("restaurant image", bitmap_image);
                    Wearable.DataApi.putDataItem(mApiClient, request);
                    Log.e("running", "handler running");
                }
            }, WAIT_TIME);
        }
        else {
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
//        Log.d("phone api", mApiClient.isConnected()+"");
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();
                Log.d("phone api", mApiClient.isConnected()+"");
                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, data.getBytes() ).await();
                }
            }
        }).start();
    }

    private static Asset createAssetFromBitmap(Bitmap bitmap) {
        final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
        return Asset.createFromBytes(byteStream.toByteArray());
    }

    Bitmap bitmap;

    private class LoadImage extends AsyncTask<String, String, Bitmap> {

        protected Bitmap doInBackground(String... args) {
            try {
                Log.e("image found", "wohoo");
                bitmap = BitmapFactory.decodeStream((InputStream) new URL(args[0]).getContent());

            } catch (Exception e) {
                Log.e("exception", e + "");
                e.printStackTrace();
            }
            return bitmap;
        }

        protected void onPostExecute(Bitmap image) {

            if(image != null){
                // send back the image
                sendMessage(IMAGE, bitmap.toString());
            }else{
                Log.e("image error", "unable to fetch image");


            }
        }
    }
}
