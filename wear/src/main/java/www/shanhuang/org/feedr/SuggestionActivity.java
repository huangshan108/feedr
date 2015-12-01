package www.shanhuang.org.feedr;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import junit.framework.Assert;

public class SuggestionActivity extends Activity {
    String currImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent creatorIntent = getIntent();
        setContentView(R.layout.activity_suggestion);
        /**
           for actual project; uncomment later
           data = creatorIntent.getStringExtra("data");
           String[] parsed = data.split("|");
        **/
        // just for prog03:
        currImage = creatorIntent.getStringExtra("image");

        ImageButton ib = (ImageButton) findViewById(R.id.suggestion_1_button);
        boolean isnull = ib == null;
        Log.d("image button is null", isnull + "");
        // this is just for prog03
        switch (currImage) {
            case "img_1":
                ib.setBackgroundResource(R.mipmap.restaurant1);
                break;
            case "img_2":
                ib.setBackgroundResource(R.mipmap.restaurant2);
                break;
            case "img_3":
                ib.setBackgroundResource(R.mipmap.restaurant3);
                break;
        }
        ib.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View vew) {
                getMap();
                return true;
            }
        });

        // ---------------------
    }


    public void nextSuggestion(View view) {
        // get next information from mobile, then use that to build the next suggestion

        // TODO: get info from mobile then add to intent and start new activity

        Intent suggestionIntent = new Intent(this, SuggestionActivity.class);

        /** set the next imagebutton src **/
        String next = "";
        switch (currImage) {
            case "img_1":
                next = "img_2";
                break;
            case "img_2":
                next = "img_3";
                break;
            case "img_3":
                next = "img_1";
                break;
        }
        suggestionIntent.putExtra("image", next);
        startActivity(suggestionIntent);
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
        Intent MapIntent = new Intent(this, MapActivity.class);
        startActivity(MapIntent);
    }

}
