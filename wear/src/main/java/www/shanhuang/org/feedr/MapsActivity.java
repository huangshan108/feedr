package www.shanhuang.org.feedr;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends Activity implements
        OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    /**
     * Overlay that shows a short help text when first launched. It also provides an option to
     * exit the app.
     */
    private DismissOverlayView mDismissOverlay;

    /**
     * The map. It is initialized when the map has been fully loaded and is ready to be used.
     *
     * @see #onMapReady(GoogleMap)
     */
    private GoogleMap mMap;

    double TARGET_LAT;
    double TARGET_LOG ;

    double CURRENT_LAT;
    double CURRENT_LOG;

    String encoding;
    GoogleApiClient googleApiClient;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .build();
        }
        googleApiClient.connect();

        /**
         * Get current location
         */
        Intent creatorIntent = getIntent();
//        CURRENT_LAT = Double.parseDouble(creatorIntent.getStringExtra("lat"));
//        CURRENT_LOG = Double.parseDouble(creatorIntent.getStringExtra("lon"));

        String data = creatorIntent.getStringExtra("data");
        Log.e("data received", data);
        String[] parsed = data.split("_splitmeherepleasenow_");// the encoding can actually be one of my other splitters so im using this as the splitting point
        encoding = parsed[0];
        String[] points = parsed[1].split("_");

        CURRENT_LAT = new Double(points[0].split(":")[0]);
        CURRENT_LOG = new Double(points[0].split(":")[1]);
        TARGET_LAT = new Double(points[1].split(":")[0]);
        TARGET_LOG = new Double(points[1].split(":")[1]);


        Log.e("encoding", encoding);
        Log.e("curr_lat", CURRENT_LAT + "");
        Log.e("curr_log", CURRENT_LOG + "");
        Log.e("tar_lat", TARGET_LAT + "");
        Log.e("tar_log", TARGET_LOG + "");

        // Set the layout. It only contains a MapFragment and a DismissOverlay.
        setContentView(R.layout.activity_maps);

        // Retrieve the containers for the root of the layout and the map. Margins will need to be
        // set on them to account for the system window insets.
        final FrameLayout topFrameLayout = (FrameLayout) findViewById(R.id.root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) findViewById(R.id.map_container);

        // Set the system view insets on the containers when they become available.
        topFrameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Call through to super implementation and apply insets
                insets = topFrameLayout.onApplyWindowInsets(insets);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                // Add Wearable insets to FrameLayout container holding map as margins
                params.setMargins(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                mapFrameLayout.setLayoutParams(params);

                return insets;
            }
        });

        // Obtain the DismissOverlayView and display the introductory help text.
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.intro_text);
        mDismissOverlay.showIntroIfNecessary();

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment =
                (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Map is ready to be used.
        mMap = googleMap;

        // Set the long click listener as a way to exit the map.
        mMap.setOnMapLongClickListener(this);

        // Add a marker in Sydney, Australia and move the camera.
        LatLng target = new LatLng(TARGET_LAT, TARGET_LOG);
        mMap.addMarker(new MarkerOptions().position(target).title("Target"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        setUpMap();

        // encodedString should be received here.
//        String encodedString = "eqbfF|ufiVsAeSeJdAwAPCc@E_@IGG?kAPCi@m@sIC]";
//        Log.i("LIST", encodedString);
        List<LatLng> list = decodePoly(encoding);

        PolylineOptions options = new PolylineOptions().width(12).color(Color.parseColor("#00B3Fd")).geodesic(true);
        for (int z = 0; z < list.size() - 1; z++) {
            LatLng point = list.get(z);
            options.add(point);
        }
        mMap.addPolyline(options);
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Display the dismiss overlay with a button to exit this activity.
        mDismissOverlay.show();
    }

    private void setUpMap() {
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.setMyLocationEnabled(true);
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng((CURRENT_LAT + TARGET_LAT) / 2, (CURRENT_LOG + TARGET_LOG) / 2));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(13);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    // Create a Location Request and register as a listener when connected
    @Override
    public void onConnected(Bundle connectionHint) {

        // Create the LocationRequest object
        LocationRequest locationRequest = LocationRequest.create();
        // Use high accuracy
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 2 seconds
        locationRequest.setInterval(TimeUnit.SECONDS.toMillis(2));
        // Set the fastest update interval to 2 seconds
        locationRequest.setFastestInterval(TimeUnit.SECONDS.toMillis(2));
        // Set the minimum displacement
        locationRequest.setSmallestDisplacement(2);

        // Register listener using the LocationRequest object
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, (LocationListener) this);
    }

    // Disconnect from Google Play Services when the Activity stops
    @Override
    protected void onStop() {

        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, (LocationListener) this);
            googleApiClient.disconnect();
        }
        super.onStop();
    }

    public void onConnectionFailed(ConnectionResult connectionResult) { }

    public void onLocationChanged(Location location){

        CURRENT_LAT = location.getLatitude();
        CURRENT_LOG = location.getLongitude();
        Log.i("LAT", Double.toString(CURRENT_LAT));
        Log.i("LOG", Double.toString(CURRENT_LOG));
    }
}
