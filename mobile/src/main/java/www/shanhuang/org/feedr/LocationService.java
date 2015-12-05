package www.shanhuang.org.feedr;

import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.Wearable;
import android.app.Service;

import java.util.ArrayList;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final IBinder mBinder = new MyBinder();

    private GoogleApiClient mGoogleApiClient;
    public static String TAG = "GPSActivity";
    public static int UPDATE_INTERVAL_MS = 30* 1000;
    public static int FASTEST_INTERVAL_MS = 30 * 1000;
    protected Location location;
    private boolean sent = false;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.e("LocationService", "service started -- in onCreate");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        mGoogleApiClient.connect();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(ConnectionResult connResult) {}

    @Override
    public void onConnected(Bundle bundle) {
        Log.e("connected", "connected");
        // Build a request for continual location updates
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_INTERVAL_MS);

        // Send request for location updates
        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient,
                        locationRequest, this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.getStatus().isSuccess()) {
                            Log.d(TAG, "Successfully requested");
                        } else {
                            Log.e(TAG, status.getStatusMessage());
                        }
                    }
                });

        location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        while (location == null) {
            locationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL_MS)
                    .setFastestInterval(FASTEST_INTERVAL_MS);

            // Send request for location updates
            LocationServices.FusedLocationApi
                    .requestLocationUpdates(mGoogleApiClient,
                            locationRequest, this)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status status) {
                            if (status.getStatus().isSuccess()) {
                                Log.d(TAG, "Successfully requested");
                            } else {
                                Log.e(TAG, status.getStatusMessage());
                            }
                        }
                    });
            location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        Double lat = location.getLatitude();
        Double lon = location.getLongitude();
        String loc = lat + ":" + lon;
        Log.e("location: ", loc);

    }


    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public IBinder onBind(Intent intent) { return mBinder; }

    public class MyBinder extends Binder {
        LocationService getService() {
            return LocationService.this;
        }

        String getLocation() {
            if (location != null) {
                Double lat = location.getLatitude();
                Double lon = location.getLongitude();
                String loc = lat + ":" + lon;
                return loc;
            }
            return "not ready";
        }
    }

    String getLocation() {
        if (location != null) {
            Double lat = location.getLatitude();
            Double lon = location.getLongitude();
            String loc = lat + ":" + lon;
            return loc;
        }
        return "not ready";
    }
}