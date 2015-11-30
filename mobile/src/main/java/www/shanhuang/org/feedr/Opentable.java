package www.shanhuang.org.feedr;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Shan on 11/29/15.
 * This class uses Opentable restaurtant API to get restaurant data
 * from third party Opentable API, which can be found at:
 * https://opentable.herokuapp.com/
 */
public class Opentable {

    static String API_END_POINT = "http://opentable.herokuapp.com/api/restaurants";
    static Thread apiThread;

    /**
     * Find restaurant be zip code
     * @param zip US zip code
     * @return JSON String
     */
    public static String getRestaurants(final String zip) {
        final StringBuilder res = new StringBuilder();
        apiThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    final URL api = new URL(API_END_POINT + "?zip=" + zip);
                    Log.i("API endpoint URL", api.toString());
                    HttpURLConnection connection = (HttpURLConnection) api.openConnection();
                    InputStream in = new BufferedInputStream(connection.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder sb = new StringBuilder();
                    res.append(reader.readLine());
                    connection.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        apiThread.start();
        try {
            apiThread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.i("Restaurant Data", res.toString());
        return res.toString();
    }
}
