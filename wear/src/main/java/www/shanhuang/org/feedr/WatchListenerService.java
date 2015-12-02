package www.shanhuang.org.feedr;

import android.content.Intent;

import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class WatchListenerService extends WearableListenerService {

    private final String MEAL_PLAN = "/meal_plan";
    private final String MAP = "/map";
    private final String PREFERENCES = "/preferences";
    private final String PREFS_CHANGE = "/prefs_change";
    private String old_data, old_map;

    public void WatchListenerService() {}

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        String new_data = new String(messageEvent.getData(), StandardCharsets.UTF_8);
        if( messageEvent.getPath().equalsIgnoreCase( MEAL_PLAN ) ) {

            if (new_data.compareTo(old_data)!=0) {
                // make sure the new data we receive is not the same as the data we just received
                old_data = new_data;

                // TODO: do something with the message we received from mobile
                Intent SuggestionIntent = new Intent(this, SuggestionActivity.class);
                SuggestionIntent.putExtra("data", new_data);
                startActivity(SuggestionIntent);
            }

        } else if (messageEvent.getPath().equalsIgnoreCase( MAP )){
            if (new_data.compareTo(old_map) != 0) {
                old_map = new_data;
                // TODO: do something with the message and start MapActivity
                Intent MapIntent = new Intent(this, MapActivity.class);
                MapIntent.putExtra("map", new_data);
                startActivity(MapIntent);
            }
        } else if (messageEvent.getPath().equalsIgnoreCase(PREFERENCES)) {
            if (new_data.compareTo(old_map) != 0) {
                old_map = new_data;
                // TODO: update preferences
                Intent preferencesIntent = new Intent(this, PreferenceActivity.class);
                preferencesIntent.putExtra("prefs", new_data);
                startActivity(preferencesIntent);
            }
        } else {
            super.onMessageReceived( messageEvent );
        }

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        super.onDataChanged(dataEvents);

        final List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
        for (DataEvent event : events) {
            DataItem item = event.getDataItem();
            if (item.getUri().getPath().equalsIgnoreCase(PREFS_CHANGE)) {
                DataMap map = DataMapItem.fromDataItem(item).getDataMap();
                int cost = map.getInt("cost");
                int distance = map.getInt("distance");
            }
        }
    }
}
