package www.shanhuang.org.feedr;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.app.Activity;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.View;
import android.widget.ImageButton;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.TextView;

public class SuggestionActivity extends Activity {
    String currImage;
    String zip;
    double currLat, currLon;

    TextView gestureEvent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent creatorIntent = getIntent();
        setContentView(R.layout.activity_suggestion);
        /**
           for actual project; uncomment later
           data = creatorIntent.getStringExtra("data");
           String[] parsed = data.split("|");
        **/
        // just for prog03:
//        currImage = creatorIntent.getStringExtra("image");
//
//        ImageButton ib = (ImageButton) findViewById(R.id.suggestion_1_button);
//        boolean isnull = ib == null;
//        Log.d("image button is null", isnull + "");
//        // this is just for prog03
//        switch (currImage) {
//            case "img_1":
//                ib.setBackgroundResource(R.mipmap.restaurant1);
//                break;
//            case "img_2":
//                ib.setBackgroundResource(R.mipmap.restaurant2);
//                break;
//            case "img_3":
//                ib.setBackgroundResource(R.mipmap.restaurant3);
//                break;
//        }
//        ib.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View vew) {
//                getMap();
//                return true;
//            }
//        });

        String data = creatorIntent.getStringExtra("data");
        int index =  data.indexOf("||");
        String json = data.substring(index + 2);
        String location = data.substring(0,index);
        String[] parsed = location.split(":");
        zip = parsed[0];
        currLat = new Double(parsed[1]);
        currLon = new Double(parsed[2]);

        Log.e("json", json);
        JSONObject restsObj = new JSONObject();
        JSONArray restsArr = new JSONArray();
        ArrayList<Restaurant> restaurants = new ArrayList<Restaurant>();
        try {
            restsObj = new JSONObject(json);
            restsArr = restsObj.getJSONArray("restaurants");
            for (int i = 0; i < restsArr.length(); i++) {
                JSONObject obj = (JSONObject) restsArr.get(i);
//                Log.d("zip is", obj.getString("postal_code"));
                Restaurant r = new Restaurant((JSONObject) restsArr.get(i));
                restaurants.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Collections.shuffle(restaurants);
//        Restaurant rest = it.next();
//        double lat = new Double(rest.getLat());
//        double lon = new Double(rest.getLng());

        Log.e("rest coord", restaurants.size() + "");
        Iterator<Restaurant> it = restaurants.iterator();
        while (it.hasNext()){
            Restaurant r = it.next();
//            Log.d("restaurant name", r.getName() + " ");
//            Log.d("restaurant zip", r.getZip() + " ");
            getDistance(r);
        }

        Restaurant r = restaurants.get(0);
//        getMap(new Double(r.getLat()), new Double(r.getLng()));
        final TextView tv = (TextView) findViewById(R.id.info);
        tv.setOnTouchListener(new OnSwipeTouchListener(this) {
            @Override
            public void onSwipeLeft() {
                Log.e("Swipe", "left swipe detected");
                tv.setText("oh look a left swipe");
            }
            @Override
            public void onSwipeRight() {
                Log.e("Swipe", "right swipe detected");
                tv.setText("oh look a right swipe");

            }
        });
        // ---------------------
    }


    public void nextSuggestion(View view) {
        // get next information from mobile, then use that to build the next suggestion

        // TODO: get info from mobile then add to intent and start new activity

//        Intent suggestionIntent = new Intent(this, SuggestionActivity.class);
//
//        /** set the next imagebutton src **/
//        String next = "";
//        switch (currImage) {
//            case "img_1":
//                next = "img_2";
//                break;
//            case "img_2":
//                next = "img_3";
//                break;
//            case "img_3":
//                next = "img_1";
//                break;
//        }
//        suggestionIntent.putExtra("image", next);
//        startActivity(suggestionIntent);
    }

    public static int getDrawable(Context context, String name) {

        // dynamically get image id
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);

        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }

    public void getMap(double restaurantLat, double restaurantLng) {
        /** Use the location information to get current location, and restaurant location
         *  and set those tto the GoogleMaps API to get directions to the restaurant
         **/
        // TODO: tell WatchListenerService to tell Mobile to get map info and then call MapActivity
//        Intent mapIntent = new Intent(this, MapsActivity.class);
//        mapIntent.putExtra("lat", currLat);
//        mapIntent.putExtra("lon", currLon);
//        startActivity(mapIntent);

        // connect the ApiClient, and send message
        GoogleApiClient mApiClient = new GoogleApiClient.Builder( this )
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

        // tell mobile to get the directions to the restaurant, which then gets the directions and tells wear to launch MapsActivity
//        WatchMessenger.sendMessage(mApiClient, "/map", currLat+":"+currLon+"_"+restaurantLat+":"+restaurantLng );
    }

    public double getDistance(Restaurant targetRestaurant) {
//        Log.e("location", "current location: "+ currLat + ":" + currLon);
//        Log.e("location", "target location: "+ targetRestaurant.getLat() + ":" + targetRestaurant.getLng());

        double output = 0;
        Location start = new Location("start");
        start.setLatitude(currLat);
        start.setLongitude(currLon);
        Location destination = new Location("restaurant");
        destination.setLatitude(new Double(targetRestaurant.getLat()));
        destination.setLongitude(new Double(targetRestaurant.getLng()));
        output = start.distanceTo(destination);
//        Log.e("distance", output + "m");
        return output;

    }

    // gesture code

//    @Override
//    public boolean onTouchEvent(MotionEvent event) {
//        // TODO Auto-generated method stub
//        return gestureDetector.onTouchEvent(event);
//    }
//
//    SimpleOnGestureListener simpleOnGestureListener
//            = new SimpleOnGestureListener(){
//
//
//        @Override
//        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
//                               float velocityY) {
//            String swipe = "";
//            float sensitvity = 50;
//
//            gestureEvent.setText("onFling: \n" + e1.toString() + "\n" + e2.toString() +"\n"
//                    + "velocityX= " + String.valueOf(velocityX) + "\n"
//                    + "velocityY= " + String.valueOf(velocityY) + "\n");
//            // TODO Auto-generated method stub
//            if((e1.getX() - e2.getX()) > sensitvity){
//                swipe += "Swipe Left\n";
//            }else if((e2.getX() - e1.getX()) > sensitvity){
//                swipe += "Swipe Right\n";
//            }else{
//                swipe += "\n";
//            }
//
//            if((e1.getY() - e2.getY()) > sensitvity){
//                swipe += "Swipe Up\n";
//            }else if((e2.getY() - e1.getY()) > sensitvity){
//                swipe += "Swipe Down\n";
//            }else{
//                swipe += "\n";
//            }
//
//            gestureEvent.setText(swipe);
//
//            return super.onFling(e1, e2, velocityX, velocityY);
//        }
//    };
//
//    GestureDetector gestureDetector
//            = new GestureDetector(simpleOnGestureListener);

}


