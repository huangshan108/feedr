package www.shanhuang.org.feedr;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;

public class PreferenceActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference);

        // TODO: pass on new preference settings to mobile

    }

    public void startSuggestion(View view) {

        // get next information from mobile, then use that to build the next suggestion
        // TODO: get info from mobile then add to intent and start new activity

        Intent suggestionIntent = new Intent(this, SuggestionActivity.class);
        suggestionIntent.putExtra("image", "img_1");
        startActivity(suggestionIntent);
    }

}
