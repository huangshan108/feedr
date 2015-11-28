package www.shanhuang.org.feedr;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;

public class MapActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Intent creatorIntent = getIntent();

        /** TODO: get the dat out from the intent, then set the map to where you are and start
         *  TODO: the directions to the restaurant location
         **/
    }

}
