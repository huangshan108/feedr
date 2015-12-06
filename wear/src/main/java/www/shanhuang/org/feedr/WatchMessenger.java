package www.shanhuang.org.feedr;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

/**
 * Created by Gang Hoon on 11/29/2015.
 */
public class WatchMessenger {

    public static void sendMessage(final GoogleApiClient mApiClient, final String path, final String data) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("starting message", "starting msg");
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        NodeApi.GetConnectedNodesResult nodes = Wearable.NodeApi.getConnectedNodes(mApiClient).await();
                        Log.e("connected nodes", nodes.getNodes().size() + "");
                        for (Node node : nodes.getNodes()) {
                            MessageApi.SendMessageResult result = Wearable.MessageApi.sendMessage(
                                    mApiClient, node.getId(), path, data.getBytes()).await();
                        }
                    }
                }).start();
            }
        }).start();
    }
}
