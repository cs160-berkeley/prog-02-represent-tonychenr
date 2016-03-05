package com.example.tc.represent;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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

        imageView.setImageResource(getResources().getIdentifier(getPackageName() +":" + imagePath, null, null));
        nameView.setText(type + ": " + name);
        partyView.setText("Party: " + party);
        emailView.setText("Email: " + email);
        websiteView.setText("Website: " + website);
        endOfTermView.setText("End of Term: " + endOfTerm);
        bill1View.setText(bills[0]);
        bill2View.setText(bills[1]);
        comm1View.setText(committees[0]);
        comm2View.setText(committees[1]);
    }
}
