package www.shanhuang.org.feedr;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;

public class ImageListenerService extends WearableListenerService {

    private static String MAP = "/map";
    private static String IMAGE = "/image";
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
        return START_STICKY;
    }


    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String new_data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        if (messageEvent.getPath().equalsIgnoreCase(IMAGE)) {
            Log.e("ils", "its here!");
            Log.e("the data", new_data);
            bitmap_string = new_data;

        } else {
            super.onMessageReceived(messageEvent);
        }

    }

    /** binder so Suggestion activity can get images directly from this service **/

    public class MyBinder extends Binder {
        ImageListenerService getService() {
            return ImageListenerService.this;
        }

    }

    public String getBitmap() {return bitmap_string;}

}
