package www.shanhuang.org.feedr;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private LocationService locationService;
    private String latLong;
    private int WAIT_TIME = 5000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Opentable.getRestaurants("94704");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, LocationService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        Log.e("race", "racing");

        /** Handler is to wait for LocationService to connect and get the location.
         *  Then make the calls to get the location without causing an error.
         * **/

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // run after waiting  WAIT_TIME ms
                latLong = locationService.getLocation();
                Log.e("main loc", latLong);
            }
        }, WAIT_TIME);

    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(mConnection);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder binder) {
            LocationService.MyBinder b = (LocationService.MyBinder) binder;
            locationService = b.getService();
            Log.e("race", "race on!");
        }

        public void onServiceDisconnected(ComponentName className) {
            locationService = null;
        }
    };


}
