package com.example.tc.represent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class DetailedView extends AppCompatActivity {

    private String name;
    private String committees[];
    private String email;
    private String endOfTerm;
    private String imagePath;
    private String party;
    private String bills[];
    private String type;
    private String website;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_view);
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.congressman_list_item, null);
        Bundle extras = getIntent().getExtras();
        name = extras.getString("NAME");
        committees = extras.getStringArray("COMMITTEES");
        email = extras.getString("EMAIL");
        endOfTerm = extras.getString("ENDOFTERM");
        imagePath = extras.getString("IMAGE");
        party = extras.getString("PARTY");
        bills = extras.getStringArray("BILLS");
        type = extras.getString("TYPE");
        website = extras.getString("WEBSITE");

        fillValues();
    }

    private void fillValues() {
        ImageView imageView = (ImageView) findViewById(R.id.congressImage);
        TextView nameView = (TextView) findViewById(R.id.name);
        TextView partyView = (TextView) findViewById(R.id.party);
        TextView emailView = (TextView) findViewById(R.id.email);
        TextView websiteView = (TextView) findViewById(R.id.website);
        TextView endOfTermView = (TextView) findViewById(R.id.endofterm);
        TextView bill1View = (TextView) findViewById(R.id.bill1);
        TextView bill2View = (TextView) findViewById(R.id.bill2);
        TextView comm1View = (TextView) findViewById(R.id.committee1);
        TextView comm2View = (TextView) findViewById(R.id.committee2);
        GetImageTask imageTask = new GetImageTask();
        Bitmap image = null;
        try {
            image = imageTask.execute(imagePath).get();
            imageView.setImageBitmap(image);
        } catch (InterruptedException | ExecutionException e) {
            Log.d("getView", "Failed to get image");
        }
        if (type.equals("Sen")) {
            nameView.setText("Senator " + name);
        } else {
            nameView.setText("Representative " + name);
        }
        partyView.setText(party);
        emailView.setText(email);
        websiteView.setText(website);
        endOfTermView.setText(endOfTerm);
        bill1View.setText(bills[0]);
        bill2View.setText(bills[1]);
        comm1View.setText(committees[0]);
        comm2View.setText(committees[1]);
    }
    private class GetImageTask extends AsyncTask<String,Void,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromURL(params[0]);
        }

        private Bitmap getBitmapFromURL(String myurl) {
            try {
                Log.d("List Adapter", myurl);
                URL url = new URL(myurl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                Bitmap myBitmap = BitmapFactory.decodeStream(input);
                return myBitmap;
            } catch (Exception e) {
                Log.d("List Adapter", "Failed to get image");
                return null;
            }
        }
    }
}
