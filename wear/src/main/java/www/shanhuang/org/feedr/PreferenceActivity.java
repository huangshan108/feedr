package www.shanhuang.org.feedr;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;

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

        Intent WLS_intent = new Intent(this, WatchListenerService.class);
        WLS_intent.putExtra("note", "get_suggestion");
        startService(WLS_intent);
    }

    public void onPreferencesButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.dist_radioButton1:
                if (checked)
                    break;
            case R.id.dist_radioButton2:
                if (checked)
                    break;
            case R.id.dist_radioButton3:
                if (checked)
                    break;
            case R.id.dist_radioButton4:
                if (checked)
                    break;
            case R.id.cost_radioButton1:
                if (checked)
                    break;
            case R.id.cost_radioButton2:
                if (checked)
                    break;
            case R.id.cost_radioButton3:
                if (checked)
                    break;
            case R.id.cost_radioButton4:
                if (checked)
                    break;
        }
    }

}
