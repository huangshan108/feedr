package www.shanhuang.org.feedr;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.os.Handler;
import android.os.IBinder;
import android.support.wearable.view.GridViewPager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SuggestionActivity extends Activity implements DataApi.DataListener {
    String currImage;
    protected String zip, json;
    protected double currLat, currLon;
    private ArrayList<Restaurant> restaurants, backup;
    GoogleApiClient mApiClient;
    Bitmap image;
    private WatchListenerService wls;
    private int WAIT_TIME = 3 *1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent creatorIntent = getIntent();
        setContentView(R.layout.activity_suggestion);

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

        /**
         for actual project; uncomment later
         data = creatorIntent.getStringExtra("data");
         String[] parsed = data.split("|");
         **/
        // just for prog03:
        currImage = creatorIntent.getStringExtra("image");
        ImageButton go = (ImageButton) findViewById(R.id.suggestion_1_button);
        ImageButton map = (ImageButton) findViewById(R.id.suggestion_2_button);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Map getting", "mapped");
                //getMap();
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Getting next suggestion", "gott");
                nextSuggestion(view);
            }
        });



    //final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
    //pager.setAdapter(new SampleGridPagerAdapter(this, getFragmentManager()));

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
//                //getMap();
//                return true;
//            }
//        });


        String data = creatorIntent.getStringExtra("data");
        Log.i("data", data);
        int index =  data.indexOf("||");
        json = data.substring(index + 2);
        String location = data.substring(0,index);
        String[] parsed = location.split(":");
        zip = parsed[0];
        currLat = new Double(parsed[1]);
        currLon = new Double(parsed[2]);

        Log.e("json", json);
        getRestaurantList();
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
//        getImage(r);
//        getMap(new Double(r.getLat()), new Double(r.getLng()));

//        Log.i("getLat", r.getLat());
//        Log.i("getLng", r.getLng());
//        getMap(new Double(r.getLat()), new Double(r.getLng()), r.getName());

        // ---------------------
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, WatchListenerService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        /** Handler is to wait for LocationService to connect and get the location.
         *  Then make the calls to get the location without causing an error.
         * **/
        Log.e("bound", "service bound");
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // run after waiting  WAIT_TIME ms
//            }
//        }, WAIT_TIME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
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
        Restaurant next = getNextRestaurant();


        /**
         *
         * uncomment when new layout is done
        TextView information = (TextView) findViewById(R.id.restaurant_info);
        ImageView image = (ImageView) findViewById(R.id.restaurant_image);
        ImageView ratings = (ImageView) findViewById(R.id.ratings);
        String rating = next.getRating();
        switch (rating) {
            case "1":
                break;
                ratings.setBackground(R.mipmap.1_star);
            case "1.5":
            case "2":
                ratings.setBackground(R.mipmap.2_star);
                break;
            case "2.5":
            case "3":
                ratings.setBackground(R.mipmap.3_star);
                break;
            case "3.5":
            case "4":
                ratings.setBackground(R.mipmap.4_star);
                break;
            case "4.5":
            default:
                ratings.setBackground(R.mipmap.5_star);
                break;
        }
       information.setText(next.getName() + "\n Price: " + next.getPrice() + " Distance: " + getDistance(next) );
         **/
    }

    // returns the next restaurant based on the preferences
    protected Restaurant getNextRestaurant() {
        // get preferences

        int cost_position = 2; // change to value loaded
        double distance_position = 0.5; // change to value loaded


        for (Restaurant restaurant : restaurants) {
            double distance = getDistance(restaurant);
            int cost = new Integer(restaurant.getPrice());
            if (cost <= cost_position && distance <= distance_position ) {
                restaurants.remove(restaurant);
                return restaurant;
            }
        }

        // if we get here, we reuse the restaurant list
        restaurants = backup;
        Collections.shuffle(backup);
        getNextRestaurant();
        return null;
    }

    protected void getRestaurantList() {
        JSONObject restsObj = new JSONObject();
        JSONArray restsArr = new JSONArray();
        restaurants = new ArrayList<Restaurant>();
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
        backup = restaurants;
        Collections.shuffle(restaurants);

    }


    public static int getDrawable(Context context, String name) {

        // dynamically get image id
        Assert.assertNotNull(context);
        Assert.assertNotNull(name);

        return context.getResources().getIdentifier(name,
                "drawable", context.getPackageName());
    }

    public void getMap(double restaurantLat, double restaurantLng, String restaurantName) {
        /** Use the location information to get current location, and restaurant location
         *  and set those tto the GoogleMaps API to get directions to the restaurant
         **/
        // TODO: tell WatchListenerService to tell Mobile to get map info and then call MapActivity
//        Intent mapIntent = new Intent(this, MapsActivity.class);
//        mapIntent.putExtra("lat", currLat);
//        mapIntent.putExtra("lon", currLon);
//        startActivity(mapIntent);

        // connect the ApiClient, and send message
        GoogleApiClient mApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
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

        WatchMessenger.sendMessage(mApiClient, "/map", currLat+":"+currLon+"_"+restaurantLat+":"+restaurantLng+":"+restaurantName );
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

    protected void getImage(Restaurant targetRestaurant) {

//        WatchMessenger.sendMessage(mApiClient, "/image", targetRestaurant.getImageUrl());
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // run after waiting  WAIT_TIME ms
//                try {
//                    FileInputStream in = new FileInputStream("image.txt");
//                    StringBuilder s = new StringBuilder();
//                    int c;
//                    while ((c = in.read()) != -1) {
//                        s += c + "";
//                    }
//                    String bitmap_string = wls.getBitmap();
//                    image = BitmapFactory.decodeFile(bitmap_string);
//                    ImageView img = (ImageView) findViewById(R.id.img);
//                    img.setImageBitmap(image);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        }, WAIT_TIME);

    }

    /** All dynamically retrieving image code **/

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.e("data changed", "delta");
        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            DataItem item = event.getDataItem();
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/image")) {
                DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
                Asset profileAsset = dataMapItem.getDataMap().getAsset("restaurant image");
                image = loadBitmapFromAsset(profileAsset);
                Log.e("image", "image received");
                // Do something with the bitmap
            }
        }
    }


    public Bitmap loadBitmapFromAsset(Asset asset) {
        int TIMEOUT_MS = 10* 1000;
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        ConnectionResult result =
                mApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mApiClient, asset).await().getInputStream();
        mApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w("IMAGE Error", "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        return BitmapFactory.decodeStream(assetInputStream);
    }

    /** Binder stuff **/

    private ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            Log.e("conn", "service connected");
            WatchListenerService.MyBinder b = (WatchListenerService.MyBinder) binder;
            wls = b.getService();
        }

        public void onServiceDisconnected(ComponentName className) {
            wls = null;
        }
    };

}

