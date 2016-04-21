package com.example.tc.represent;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.GridViewPager;
import android.support.wearable.view.WatchViewStub;
import android.util.Log;
import android.widget.TextView;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "";
    private static final String TWITTER_SECRET = "";


    private final int SHAKE_THRESHOLD = 3;
    private TextView mTextView;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private ShakeDetector mShakeDetector;
    private WatchGridPagerAdapter congressAdapter;
    private ArrayList<CongressMember> members = new ArrayList<CongressMember>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        initShaker();
        if (getIntent().getExtras() != null) {
            initMembers();
            Log.d("T", "in wearMainActivity initializing grid");

            final GridViewPager pager = (GridViewPager) findViewById(R.id.pager);
            congressAdapter = new WatchGridPagerAdapter(this, getFragmentManager(), members);
            pager.setAdapter(congressAdapter);
        }
    }

    private void initMembers() {
        Bundle extras = getIntent().getExtras();
        ArrayList<String> member;
        CongressMember newMember;
        int index;
        String[] committees;
        String[] sponsoredBills;
        // Add congress member information to list of members
        for (String s : extras.keySet()) {
            if (s.startsWith("Member")) {
                index = Integer.parseInt(s.substring(s.length() - 1));
                member = extras.getStringArrayList(s);
                committees = extras.getStringArray("COMMITTEES" + index);
                sponsoredBills = extras.getStringArray("BILLS" + index);
                newMember = new CongressMember(member.get(0), committees, member.get(1), member.get(2),
                        member.get(3), member.get(4), sponsoredBills, null,
                        member.get(5), member.get(6), member.get(7), member.get(8),
                        member.get(9), null);
                members.add(index, newMember);
            }
        }

        // Add respective image of congress member
        Bitmap bitmap;
        for (String s : extras.keySet()) {
            if (s.startsWith("image")) {
                newMember = members.get(Integer.parseInt(s.substring(s.length() - 1)));
                byte[] bytes = extras.getByteArray(s);
                bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                newMember.setImageBitmap(bitmap);
            }
        }
    }

    private void initShaker() {
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector();
        mShakeDetector.setOnShakeListener(new ShakeDetector.OnShakeListener() {

            public void onShake() {
                Log.d("T", "in wearMainActivity handleShakeEvent");
                Intent shakeIntent = new Intent(MainActivity.this, WatchToPhoneService.class);
                shakeIntent.putExtra("ZIP_CODE", "random");
                shakeIntent.putExtra("isShake", "True");
                startService(shakeIntent);
            }
        });
    }

    protected void onResume() {
        super.onResume();
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        super.onPause();
        mSensorManager.unregisterListener(mShakeDetector);
    }

}