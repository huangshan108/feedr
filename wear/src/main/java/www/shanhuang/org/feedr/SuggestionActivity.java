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
import java.text.DecimalFormat;
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

//        Log.e("rest coord", restaurants.size() + "");
//        Iterator<Restaurant> it = restaurants.iterator();
//        while (it.hasNext()){
//            Restaurant r = it.next();
//            getDistance(r);
//        }

        // ---------------------
        fill();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }


    public void nextSuggestion(View view) {
        // get next information from mobile, then use that to build the next suggestion

        // TODO: get info from mobile then add to intent and start new activity
        Restaurant next = getNextRestaurant();


        TextView information = (TextView) findViewById(R.id.restaurant_info);
        ImageView image = (ImageView) findViewById(R.id.restaurant_image);
//        ImageView ratings = (ImageView) findViewById(R.id.restaurant_rating);
        String rating = next.getRating();
        String stars = "*";
        switch (rating) {
                case "1":
//                    ratings.setBackgroundResource(R.mipmap.star_1);
                    stars = "*";
                    break;
                case "1.5":
                case "2":
//                    ratings.setBackgroundResource(R.mipmap.star_2);
                    stars = "**";
                    break;
                case "2.5":
                case "3":
//                    ratings.setBackgroundResource(R.mipmap.star_3);
                    stars = "***";
                    break;
                case "3.5":
                case "4":
//                    ratings.setBackgroundResource(R.mipmap.star_4);
                    stars = "****";
                    break;
                case "4.5":
                default:
//                    ratings.setBackgroundResource(R.mipmap.star_5);
                    stars = "*****";
                    break;
        }

        DecimalFormat df = new DecimalFormat();
        df.applyPattern("##.##");
        double dist = getDistance(next) / 1000 / 1.6;

       information.setText(" " + next.getName() + " \n Price: " + next.getPrice() + " \n Distance: " + df.format(dist) + "mi \n Ratings: " + stars );
    }

    int index =0;

    // returns the next restaurant based on the preferences
    protected Restaurant getNextRestaurant() {
        // get preferences

        int cost_position = 4; // change to value loaded
        double distance_position = 5; // change to value loaded


//        for (Restaurant restaurant : restaurants) {
//            double distance = getDistance(restaurant);
//            int cost = new Integer(restaurant.getPrice());
//            if (cost <= cost_position && distance <= distance_position ) {
//                Log.e("filter", "in here");
//                restaurants.remove(restaurant);
//                return restaurant;
//            } else {
//                Log.e("filter", "not filtering correctly");
//            }
//        }
        Restaurant out = restaurants.get(index);
        index = (index+1)%3;
        return out;
        // if we get here, we reuse the restaurant list
//        restaurants = backup;
//        Collections.shuffle(backup);
//        getNextRestaurant();
//        return null;
    }

    protected void getRestaurantList() {
        JSONObject restsObj = new JSONObject();
        JSONArray restsArr = new JSONArray();
        restaurants = new ArrayList<Restaurant>();
        try {
            String data = "{\"total_entries\":3,\"per_page\":100,\"current_page\":1,\"restaurants\":[{\"id\":84985,\"name\":\"Tako Sushi\",\"address\":\"2379 Telegraph Avenue\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5106658000\",\"lat\":37.867274,\"lng\":-122.258646,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=84985\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=84985\",\"image_url\":\"http://s3-media3.fl.yelpcdn.com/bphoto/c2w2geSA0XBbE5MPsXT3fg/348s.jpg\"}, {\"id\":84986,\"name\":\"Cheese Board Pizza\",\"address\":\"1512 Shattuck Avenue\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94709\",\"country\":\"US\",\"phone\":\"5105493183\",\"lat\":37.879853,\"lng\":-122.269516,\"price\":3,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=84985\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=84985\",\"image_url\":\"http://www.berkeleyside.com/wp-content/uploads/2011/06/Pizza-from-Cheese-Board.jpg\"}, {\"id\":84987,\"name\":\"Gypsy's\",\"address\":\"2519 Durant Avenue\",\"city\":\"Berkeley\",\"state\":\"CA\",\"area\":\"San Francisco Bay Area\",\"postal_code\":\"94704\",\"country\":\"US\",\"phone\":\"5105484860\",\"lat\":37.868098,\"lng\":-122.258136,\"price\":2,\"reserve_url\":\"http://www.opentable.com/single.aspx?rid=84985\",\"mobile_reserve_url\":\"http://mobile.opentable.com/opentable/?restId=84985\",\"image_url\":\"http://image.zmenu.com/large/14460/20131206063723739974.jpg\"}]}";

//            restsObj = new JSONObject(json);
            restsObj = new JSONObject(data);
            restsArr = restsObj.getJSONArray("restaurants");
            for (int i = 0; i < restsArr.length(); i++) {
                JSONObject obj = (JSONObject) restsArr.get(i);
//                Log.d("zip is", obj.getString("postal_code"));
                Restaurant r = new Restaurant(obj);
                restaurants.add(r);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        backup = restaurants;
//        Collections.shuffle(restaurants);

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



    /** filling up initially **/

    private void fill() {
        Restaurant next = getNextRestaurant();


        TextView information = (TextView) findViewById(R.id.restaurant_info);
        ImageView image = (ImageView) findViewById(R.id.restaurant_image);
//        ImageView ratings = (ImageView) findViewById(R.id.restaurant_rating);
        String rating = next.getRating();
        String stars = "*";
        switch (rating) {
            case "1":
//                    ratings.setBackgroundResource(R.mipmap.star_1);
                stars = "*";
                break;
            case "1.5":
            case "2":
//                    ratings.setBackgroundResource(R.mipmap.star_2);
                stars = "**";
                break;
            case "2.5":
            case "3":
//                    ratings.setBackgroundResource(R.mipmap.star_3);
                stars = "***";
                break;
            case "3.5":
            case "4":
//                    ratings.setBackgroundResource(R.mipmap.star_4);
                stars = "****";
                break;
            case "4.5":
            default:
//                    ratings.setBackgroundResource(R.mipmap.star_5);
                stars = "*****";
                break;
        }

        DecimalFormat df = new DecimalFormat();
        df.applyPattern("##.##");
        double dist = getDistance(next) / 1000 / 1.6;

        information.setText(" " + next.getName() + " \n Price: " + next.getPrice() + " \n Distance: " + df.format(dist) + "mi \n Ratings: " + stars );
    }

}

