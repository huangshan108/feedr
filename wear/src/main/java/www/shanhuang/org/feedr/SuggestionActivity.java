package www.shanhuang.org.feedr;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SuggestionActivity extends Activity implements DataApi.DataListener {
    protected String zip, json;
    protected double currLat, currLon;
    private ArrayList<Restaurant> restaurants;
    GoogleApiClient mApiClient;
    Bitmap image;
    private ImageListenerService imageService;
    private int WAIT_TIME = 2 *1000;
    Restaurant targetRestaurant;

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

        ImageButton go = (ImageButton) findViewById(R.id.suggestion_1_button);
        ImageButton map = (ImageButton) findViewById(R.id.suggestion_2_button);

        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Map getting", "mapped");
                getMap(new Double(targetRestaurant.getLat()), new Double(targetRestaurant.getLng()), targetRestaurant.getName());
            }
        });
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Getting next suggestion", "got");
                nextSuggestion(view);
            }
        });

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

        fill();
    }



    @Override
    protected void onResume() {
        super.onResume();
        Intent imageIntent = new Intent(this, ImageListenerService.class);
        bindService(imageIntent, mConnection,
                Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }


    public void nextSuggestion(View view) {
        // get next information from mobile, then use that to build the next suggestion
        targetRestaurant = getNextRestaurant();


        getImage(targetRestaurant);
        TextView information = (TextView) findViewById(R.id.restaurant_info);
        ImageView image = (ImageView) findViewById(R.id.restaurant_image);
//        ImageView ratings = (ImageView) findViewById(R.id.restaurant_rating);
        String rating = targetRestaurant.getRating();
        String stars = "*";
        switch (rating) {

            case "1":
                stars = "*";
                break;
            case "1.5":
            case "2":
                stars = "**";
                break;
            case "2.5":
            case "3":
                stars = "***";
                break;
            case "3.5":
            case "4":
                stars = "****";
                break;
            case "4.5":
            default:
                stars = "*****";
                break;
        }

        DecimalFormat df = new DecimalFormat();
        df.applyPattern("##.##");
        double dist = getDistance(targetRestaurant) / 1000 / 1.6;

        information.setText(" " + targetRestaurant.getName() + " \n Price: " + targetRestaurant.getPrice() + " \n Distance: " + df.format(dist) + "mi \n Ratings: " + stars );
    }

    int index =0;

    // returns the next restaurant based on the preferences
    protected Restaurant getNextRestaurant() {
        // get next restaurant that meets the preference. if none met, then return whatever is available.
        SharedPreferences sharedPref = getSharedPreferences("PREFERENCES", Context.MODE_PRIVATE);

        double[] dist_array = {0.2, 0.5, 1, 2};

        int cost_filter = sharedPref.getInt("cost", 3);
        int dist_filter = sharedPref.getInt("distance", 3);

        Log.e("cost", "<" + cost_filter);
        Log.e("dist", "<" + dist_array[dist_filter]);
        for (Restaurant restaurant : restaurants) {
            double distance = getDistance(restaurant);
            int cost = new Integer(restaurant.getPrice());
            index = (index+1)%restaurants.size();
            if (cost <= cost_filter && distance <= dist_array[dist_filter] ) {
                return restaurant;
            }
        }

        Restaurant out = restaurants.get(index);
        index = (index+1)%restaurants.size();

        return out;
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
                Restaurant r = new Restaurant(obj);
                restaurants.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("size", restaurants.size()+ "");
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

        double output = 0;
        Location start = new Location("start");
        start.setLatitude(currLat);
        start.setLongitude(currLon);
        Location destination = new Location("restaurant");
        destination.setLatitude(new Double(targetRestaurant.getLat()));
        destination.setLongitude(new Double(targetRestaurant.getLng()));
        output = start.distanceTo(destination);
        return output;

    }

    protected void getImage(Restaurant targetRestaurant) {

        // image unable to retrieve; data transfer issue

//        WatchMessenger.sendMessage(mApiClient, "/image", targetRestaurant.getImageUrl());
//        final Handler handler = new Handler();
//        handler.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                // run after waiting  WAIT_TIME ms
//                try {
//                    String bitmap_string = imageService.getBitmap();
//                    image = BitmapFactory.decodeFile(bitmap_string);
//                    ImageView img = (ImageView) findViewById(R.id.restaurant_image);
//                    img.setImageBitmap(image);
////                    image = new BitmapFactory().decodeFile(imageService.getBitmap());
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

    /** filling up initially **/

    private void fill() {
        targetRestaurant = getNextRestaurant();

        getImage(targetRestaurant);
        TextView information = (TextView) findViewById(R.id.restaurant_info);
        ImageView image = (ImageView) findViewById(R.id.restaurant_image);
//        ImageView ratings = (ImageView) findViewById(R.id.restaurant_rating);
        String rating = targetRestaurant.getRating();
        String stars = "*";
        switch (rating) {
            case "1":
                stars = "*";
                break;
            case "1.5":
            case "2":
                stars = "**";
                break;
            case "2.5":
            case "3":
                stars = "***";
                break;
            case "3.5":
            case "4":
                stars = "****";
                break;
            case "4.5":
            default:
                stars = "*****";
                break;
        }

        DecimalFormat df = new DecimalFormat();
        df.applyPattern("##.##");
        double dist = getDistance(targetRestaurant) / 1000 / 1.6;

        information.setText(" " + targetRestaurant.getName() + " \n Price: " + targetRestaurant.getPrice() + " \n Distance: " + df.format(dist) + "mi \n Ratings: " + stars );
    }


    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            Log.e("conn", "conn");
            ImageListenerService.MyBinder b = (ImageListenerService.MyBinder) binder;
            imageService = b.getService();
            Log.e("oSC", "im bound");
        }

        public void onServiceDisconnected(ComponentName className) { imageService = null;
        }
    };
}

