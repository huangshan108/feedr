package www.shanhuang.org.feedr;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;

public class MobileListenerService extends WearableListenerService {
    private static final String GET_SUGGESTION = "/get_suggestion";

    public MobileListenerService() {
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        if( messageEvent.getPath().equalsIgnoreCase(GET_SUGGESTION) ) {
            // TODO: get new suggestion from restaurant, then send the info back to the wear
        } else {
            super.onMessageReceived( messageEvent );
        }

    }
}
