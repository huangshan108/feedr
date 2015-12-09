package www.shanhuang.org.feedr;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WatchListenerService extends WearableListenerService {

    private static String MEAL_PLAN = "/meal_plan";
    private static String MAP = "/map";
    private static String IMAGE = "/image";
    private final String PREFS_CHANGE = "/prefs_change";
    private final String PREFERENCES = "/preferences";

    private GoogleApiClient mApiClient;
    protected String bitmap_string;

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
        // get the intent, if the string extra says get suggestion, use sendMessage to go get message

        String note = intent.getStringExtra("note");
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
//            Log.e("data received", new_data);
            Intent SuggestionIntent = new Intent(this, SuggestionActivity.class);
            SuggestionIntent.putExtra("data", new_data);
            SuggestionIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(SuggestionIntent);

        } else if (messageEvent.getPath().equalsIgnoreCase( MAP )){
            // TODO: do something with the message and start MapActivity
            Intent mapIntent = new Intent(this, MapsActivity.class);
            mapIntent.putExtra("data", new_data);
            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.i("start map", "start map");
            startActivity(mapIntent);

        } else if (messageEvent.getPath().equalsIgnoreCase(PREFERENCES)) {
            // TODO: update preferences
            Intent preferencesIntent = new Intent(this, PreferenceActivity.class);
            preferencesIntent.putExtra("prefs", new_data);
            startActivity(preferencesIntent);

        } else if (messageEvent.getPath().equalsIgnoreCase(IMAGE)) {
            Log.e("wls", "its here instead");
            Log.e("the data", new_data);
            bitmap_string = new_data;

            FileOutputStream out;
            try {
                out = new FileOutputStream("image.txt");
                out.write(bitmap_string.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }

        } else {
            super.onMessageReceived(messageEvent);
        }

    }

    /** binder so Suggestion activity can get images directly from this service **/

    public class MyBinder extends Binder {
        WatchListenerService getService() {
            return WatchListenerService.this;
        }

    }

    public String getBitmap() {return bitmap_string;}

}
