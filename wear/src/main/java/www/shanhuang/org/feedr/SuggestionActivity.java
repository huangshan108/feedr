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
        // just for prog03:
        currImage = creatorIntent.getStringExtra("image");

        // for actual project; uncomment later
        // data = creatorIntent.getStringExtra("data");
        // String[] parsed = data.split("|");
        ImageButton ib = (ImageButton) findViewById(R.id.suggestion_1_button);
        int img = 0;
        Log.e("ckpt", "checkpoint 1");
        Context c = getApplicationContext();
        img = getDrawable(getApplicationContext(), "start");
//        switch (currImage) {
//            case "start":
//                img = getDrawable(getApplicationContext(), "start");
//                break;
//            case "img_1":
//                img = getDrawable(getApplicationContext(), "img_1");
//                break;
//            case "img_2":
//                img = getDrawable(getApplicationContext(), "img_2");
//                break;
//            case "img_3":
//                img = getDrawable(getApplicationContext(), "img_3");
//                break;
//        }
        ib.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View vew) {
                getMap();
                return true;
            }
        });
        Log.e("imgage", img + "");
        Log.e("ckpt", "checkpoint 2");

//        ib.setBackgroundResource(img);
        setContentView(R.layout.activity_suggestion);
    }


    public void nextSuggestion(View view) {
        // get next information from mobile, then use that to build the next suggestion

        // TODO: get info from mobile then add to intent and start new activity

        Intent suggestionIntent = new Intent(this, SuggestionActivity.class);

        /** set the next imagebutton src **/
        String next = "";
        switch (currImage) {
            case "start":
                next = "img_1";
                break;
            case "img_1":
                next = "img_2";
                break;
            case "img_2":
                next = "img_3";
                break;
            case "img_3":
                next = "start";
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

    }

}
