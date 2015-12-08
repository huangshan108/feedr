package www.shanhuang.org.feedr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

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

public class SuggestionActivity extends Activity {
    String currImage;
    String zip;
    double currLat, currLon;

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
                Log.d("zip is", obj.getString("postal_code"));
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
            Log.d("restaurant zip", r.getZip() + " ");
        }




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

    public void getMap() {
        /** Use the location information to get current location, and restaurant location
         *  and set those tto the GoogleMaps API to get directions to the restaurant
         **/
        // TODO: tell WatchListenerService to tell Mobile to get map info and then call MapActivity
        Intent MapIntent = new Intent(this, MapsActivity.class);
        startActivity(MapIntent);
    }

}
