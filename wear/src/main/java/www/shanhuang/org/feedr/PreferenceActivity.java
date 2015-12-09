package www.shanhuang.org.feedr;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;

public class PreferenceActivity extends Activity {

    protected int cost_filter;
    protected int distance_filter;

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
        SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.dist_radioButton1:
                if (checked) editor.putInt("distance", 1);
                break;
            case R.id.dist_radioButton2:
                if (checked) editor.putInt("distance", 2);
                break;
            case R.id.dist_radioButton3:
                if (checked) editor.putInt("distance", 3);
                break;
            case R.id.dist_radioButton4:
                if (checked) editor.putInt("distance", 4);
                break;
            case R.id.cost_radioButton1:
                if (checked) editor.putInt("cost", 1);
                break;
            case R.id.cost_radioButton2:
                if (checked) editor.putInt("cost", 2);
                break;
            case R.id.cost_radioButton3:
                if (checked) editor.putInt("cost", 3);
                break;
            case R.id.cost_radioButton4:
                if (checked) editor.putInt("cost", 4);
                break;
        }
    }

}
