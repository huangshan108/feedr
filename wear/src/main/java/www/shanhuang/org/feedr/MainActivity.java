package www.shanhuang.org.feedr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class MainActivity extends Activity {

    private TextView mTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.round_activity_main);

        /** ImageButton listener setup **/
        ImageButton home_button = (ImageButton) findViewById(R.id.home_button);
        boolean hb = home_button == null;
        Log.e("home button: ", hb + "");

        // click starts the suggestions
//        home_button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startSuggestion();
//            }
//        });

//         long click opens the preferences
//        home_button.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View vew) {
//                startPreference();
//                return true;
//            }
//        });
        startSuggestion();
    }

    public void startSuggestion() {

//        Intent suggestionIntent = new Intent(this, SuggestionActivity.class);
//        suggestionIntent.putExtra("image", "img_1");
//        startActivity(suggestionIntent);
        Intent WLS_intent = new Intent(this, WatchListenerService.class);
        WLS_intent.putExtra("note", "get_suggestion");
        startService(WLS_intent);

    }

    public void startPreference() {
        Intent preferenceIntent = new Intent(this, PreferenceActivity.class);
        startActivity(preferenceIntent);
    }



}
