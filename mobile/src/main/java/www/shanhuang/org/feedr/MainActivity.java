package www.shanhuang.org.feedr;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;

import java.io.IOException;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private LocationService locationService;
    private String latLong, zip;
    private int WAIT_TIME = 10000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Intent MapIntent = new Intent(this, MapsActivity.class);
//        startActivity(MapIntent);

//        Opentable.getRestaurants("94704");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Intent intent= new Intent(this, LocationService.class);
        bindService(intent, mConnection,
                Context.BIND_AUTO_CREATE);
        /** Handler is to wait for LocationService to connect and get the location.
         *  Then make the calls to get the location without causing an error.
         * **/
        Log.e("bound", "service bound");
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // run after waiting  WAIT_TIME ms
                latLong = locationService.getLocation();
                double lat = new Double(latLong.split(":")[0]);
                double lon = new Double(latLong.split(":")[1]);
                zip = geocoder(lat, lon);
                Intent mobileListenerIntent = new Intent(getBaseContext() , MobileListenerService.class);
                mobileListenerIntent.putExtra("zip:latlong", zip+":"+latLong);
                mobileListenerIntent.putExtra("command", "start");
                startService(mobileListenerIntent);
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
        }

        public void onServiceDisconnected(ComponentName className) {
            locationService = null;
        }
    };

    protected String geocoder(double lat, double lon) {
        // convert the string from usgs to lat,long and use this info to inflate the map
        List<Address> geocodeMatches = null;
        try {
            geocodeMatches = new Geocoder(this).getFromLocation(lat, lon, 1);
        } catch (IOException e) {

        }
        if (geocodeMatches!=null) {
            return geocodeMatches.get(0).getPostalCode();
        }
        return "";
    }

}
