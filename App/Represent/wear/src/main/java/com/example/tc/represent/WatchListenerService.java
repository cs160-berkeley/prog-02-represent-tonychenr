package com.example.tc.represent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Wearable;
import com.google.android.gms.wearable.WearableListenerService;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class WatchListenerService extends WearableListenerService {
    // In PhoneToWatchService, we passed in a path, either "/FRED" or "/LEXY"
    // These paths serve to differentiate different phone-to-watch messages
    private static final String FRED_FEED = "/Fred";
    private static final String LEXY_FEED = "/Lexy";
    private static final String TAG = "WatchListener";
    private static final int TIMEOUT_MS = 500;
//    private GoogleApiClient mGoogleApiClient;

//    @Override
//    public void onCreate(){
//        super.onCreate();
//
////        mGoogleApiClient.connect();
//    }
    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d("T", "in WatchListenerService, got: " + messageEvent.getPath());
        super.onMessageReceived( messageEvent );
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        Log.d("T", "in WatchListenerService, got data");
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/data")) {

                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                DataMapItem item = DataMapItem.fromDataItem(event.getDataItem());
                DataMap dataMap = item.getDataMap();
                Asset memberAsset;
                Bitmap bitmap;
                ByteArrayOutputStream stream;
                byte[] bytes;
                for (String s : dataMap.keySet()) {
                    if (s.startsWith("image")) {
                        memberAsset = dataMap.getAsset(s);
                        bitmap = loadBitmapFromAsset(memberAsset);
                        stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 10, stream);
                        bytes = stream.toByteArray();
                        intent.putExtra(s, bytes);
                    } else if (s.startsWith("Member")) {
                        intent.putExtra(s, dataMap.getStringArrayList(s));
                    } else if (s.equals("date")) {
                        continue;
                    } else {
                        intent.putExtra(s, dataMap.getStringArray(s));
                    }
                }
                Log.d("T", "in WatchListenerService loading Congress member information");
                startActivity(intent);
            }
        }
    }

    public Bitmap loadBitmapFromAsset(Asset asset) {
        if (asset == null) {
            throw new IllegalArgumentException("Asset must be non-null");
        }
        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Wearable.API)
                .build();
        ConnectionResult result =
                mGoogleApiClient.blockingConnect(TIMEOUT_MS, TimeUnit.MILLISECONDS);
        if (!result.isSuccess()) {
            return null;
        }
        // convert asset into a file descriptor and block until it's ready
        InputStream assetInputStream = Wearable.DataApi.getFdForAsset(
                mGoogleApiClient, asset).await().getInputStream();
        mGoogleApiClient.disconnect();

        if (assetInputStream == null) {
            Log.w(TAG, "Requested an unknown Asset.");
            return null;
        }
        // decode the stream into a bitmap
        Log.d("T", "in WatchListenerService loadBitmapFromAsset finish");
        return BitmapFactory.decodeStream(assetInputStream);
    }
}
