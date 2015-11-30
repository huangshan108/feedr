package www.shanhuang.org.feedr;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
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
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CountDownLatch;

public class MobileListenerService extends WearableListenerService {
    private static final String GET_SUGGESTION = "/get_suggestion";
    private String openTableURL = "SOME URL HERE";
    private GoogleApiClient mApiClient;

    private static String MEAL_PLAN = "/meal_plan";
    private static String MAP = "/map";
    public MobileListenerService() {
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase(GET_SUGGESTION) ) {
            // TODO: get new suggestion from restaurant, then send the info back to the wear
            getSuggestions();
        } else {
            super.onMessageReceived( messageEvent );
        }

    }

    protected void getSuggestions() {
        final CountDownLatch latch = new CountDownLatch(1);

        new Thread(new Runnable() {
            @Override
            public void run() {
                URL myURL = null;
                HttpURLConnection myURLConnection = null;

                try {
                    /** Connect to openTable API and get the info **/
                    String pref_info = "some pref we load";
                    myURL = new URL(openTableURL + pref_info);
                    myURLConnection = (HttpURLConnection) myURL.openConnection();
                    myURLConnection.connect();

                    /** Parse the output and build a new JSONObject out of it**/
                    InputStream in = new BufferedInputStream(myURLConnection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    JSONObject jObj = new JSONObject(sb.toString());
                    JSONArray jArr = jObj.getJSONArray("features");

                    /** For each JSONObject, extract the data **/
                    for (int i = 0; i < jArr.length(); i++) {
                        JSONObject values = jArr.getJSONObject(jArr.length()-i-1);
                        String data_extracted = "";
                        // TODO: get the data out from the JSONObject, then send the message to watch

                        // as of right now, I am assuming our data will be in the format:
                        // "name|star_count|$_count|distance|image_url|other_info_\n_delimited"
                        // NOTE: i believe we have a 100 Byte limit per message so we might have to be clever about this
                        sendMessage(MEAL_PLAN, data_extracted);
                    }
                    latch.countDown();

                } catch (IOException e) {
                    Log.e("IOEXCEPTION", "" + e + "");
                } catch (JSONException j) {
                    Log.e("JSON EXCEPTION", "" + j + "");
                } finally {
                    if (myURLConnection != null) {
                        myURLConnection.disconnect();
                    }
                }
            }
        }).start();
        try {
            latch.await();
        } catch (InterruptedException ie) {
            Log.e("InterruptedException", "" + ie + "");
        }

    }

    protected void sendMessage(final String path, final String data) {
        mApiClient = new GoogleApiClient.Builder( this )
                .addApi(Wearable.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        /* Successfully connected */
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        /* Connection was interrupted */
                    }
                })
                .build();
        new Thread( new Runnable() {
            @Override
            public void run() {
                NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes( mApiClient ).await();

                for(Node node : nodes.getNodes()) {
                    MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                            mApiClient, node.getId(), path, data.getBytes() ).await();
                }
            }
        }).start();
    }

}
