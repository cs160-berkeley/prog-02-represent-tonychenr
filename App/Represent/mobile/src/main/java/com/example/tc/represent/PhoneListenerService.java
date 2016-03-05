package com.example.tc.represent;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class PhoneListenerService extends WearableListenerService {

    //   WearableListenerServices don't need an iBinder or an onStartCommand: they just need an onMessageReceieved.
    private static final String TOAST = "/send_toast";
    private static final String TAG = "WatchListener";
    private static final int TIMEOUT_MS = 5000;

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in PhoneListenerService, got: " + messageEvent.getPath());
        if( messageEvent.getPath().equalsIgnoreCase(TOAST) ) {

            // Value contains the String we sent over in WatchToPhoneService, "good job"
            String value = new String(messageEvent.getData(), StandardCharsets.UTF_8);

            // Make a toast with the String
            Context context = getApplicationContext();
            int duration = Toast.LENGTH_SHORT;

            Toast toast = Toast.makeText(context, value, duration);
            toast.show();

            // so you may notice this crashes the phone because it's
            //''sending message to a Handler on a dead thread''... that's okay. but don't do this.
            // replace sending a toast with, like, starting a new activity or something.
            // who said skeleton code is untouchable? #breakCSconceptions

        } else {
            super.onMessageReceived( messageEvent );
        }

    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {

        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/detail")) {
                Intent intent = new Intent(this, DetailedView.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                DataMap dataMap = item.getDataMap();
                intent.putExtras(dataMap.toBundle());

                Log.d("T", "loading detailed view");
                startActivity(intent);
            } else if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/shake")) {
                Intent intent = new Intent(this, CongressionalView.class );
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                DataMap dataMap = item.getDataMap();
                intent.putExtra("ZIP_CODE", dataMap.getString("ZIP_CODE"));
                Log.d("T", "in PhoneListenerService loading new list of Congress members");
                startActivity(intent);
            }
        }
    }
}
