package com.example.tc.represent;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {

    private Button mlocationBtn;
    private Button mSubmitBtn;
    private EditText mZipCode;
    private static final String TWITTER_KEY = "VCSVFO3keOSfv9SRvt413kjXJ";
    private static final String TWITTER_SECRET = "djT1SURXvJdtIbF6o3sbGu9NzhQ4ttjEDKWWyomlxmsWIdNy8H";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mlocationBtn = (Button) findViewById(R.id.currLocMainBtn);
        mSubmitBtn = (Button) findViewById(R.id.submitBtn);
        mZipCode = (EditText) findViewById(R.id.editZipMain);

        mlocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent listIntent = new Intent(MainActivity.this, CongressionalView.class);
                listIntent.putExtra("ZIP_CODE", "current");
                startActivity(listIntent);
            }
        });

        mSubmitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mZipCode.getText().toString().trim().length() > 0) {
                    Intent listIntent = new Intent(MainActivity.this, CongressionalView.class);
                    listIntent.putExtra("ZIP_CODE", mZipCode.getText().toString());
                    startActivity(listIntent);
                }
            }
        });
    }

}
