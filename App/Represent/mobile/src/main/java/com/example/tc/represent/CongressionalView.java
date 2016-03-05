package com.example.tc.represent;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class CongressionalView extends AppCompatActivity {

    CongressmanListAdapter congressAdapter;
    private List<CongressMember> congressmanList = new ArrayList<CongressMember>();
    private static HashMap<String, CongressMember[]> fakeData = new HashMap<String, CongressMember[]>();
    static {
        fakeData.put("94704", new CongressMember[]
                {
                        new CongressMember("BLee1", new String[] {"c1", "c2"}, "@.gov", "2016",
                                "drawable/barbaralee", "Democrat", new String[] {"b1", "b2"}, "twitL",
                                "Representative", "L.gov", "75", "25", "co1"),
                        new CongressMember("BLee2", new String[] {"c1", "c2"}, "@.gov", "2016",
                                "drawable/barbaralee", "Democrat", new String[] {"b1", "b2"}, "twitL",
                                "Representative", "L.gov", "75", "25", "co2"),
                        new CongressMember("BLee3", new String[] {"c1", "c2"}, "@.gov", "2016",
                                "drawable/barbaralee", "Democrat", new String[] {"b1", "b2"}, "twitL",
                                "Senator", "L.gov", "75", "25", "co3"),
                });
        fakeData.put("94720", new CongressMember[]
                {
                        new CongressMember("BLee4", new String[] {"c1", "c2"}, "@.gov", "2016",
                                "drawable/barbaralee", "Democrat", new String[] {"b1", "b2"}, "twitL",
                                "Representative", "L.gov", "75", "25", "co4"),
                        new CongressMember("BLee5", new String[] {"c1", "c2"}, "@.gov", "2016",
                                "drawable/barbaralee", "Democrat", new String[] {"b1", "b2"}, "twitL",
                                "Representative", "L.gov", "75", "25", "co5"),
                        new CongressMember("BLee6", new String[] {"c1", "c2"}, "@.gov", "2016",
                                "drawable/barbaralee", "Democrat", new String[] {"b1", "b2"}, "twitL",
                                "Senator", "L.gov", "75", "25", "co6"),
                });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congress_list);
        String zipCode = getIntent().getExtras().getString("ZIP_CODE");

        getCongressMembers(zipCode);
        ListView congressListView = (ListView) findViewById(R.id.congress_list);
        congressAdapter = new CongressmanListAdapter(CongressionalView.this, congressmanList);
        congressListView.setAdapter(congressAdapter);

        congressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View v, int position, long id) {
                // ListView Clicked item value
                CongressMember c = (CongressMember) congressAdapter.getItem(position);

                Intent detailIntent = new Intent(CongressionalView.this, DetailedView.class);
                detailIntent.putExtra("NAME", c.getName());
                detailIntent.putExtra("COMMITTEES", c.getCommittees());
                detailIntent.putExtra("EMAIL", c.getEmail());
                detailIntent.putExtra("ENDOFTERM", c.getEndOfTerm());
                detailIntent.putExtra("IMAGE", c.getImagePath());
                detailIntent.putExtra("PARTY", c.getParty());
                detailIntent.putExtra("BILLS", c.getSponsoredBills());
                detailIntent.putExtra("TYPE", c.getType());
                detailIntent.putExtra("WEBSITE", c.getWebsite());
                startActivity(detailIntent);
            }
        });
        sendWatchData();
    }

    private void getCongressMembers(String zipCode) {
        CongressMember[] members;
        if (zipCode.equals("0")) {
           members = fakeData.get("94704");
        } else {
            members = fakeData.get(zipCode);
        }
        for (CongressMember c : members) {
            congressmanList.add(c);
        }
    }

    private void sendWatchData() {
        Intent sendIntent = new Intent(CongressionalView.this, PhoneToWatchService.class);
        CongressMember c;
        ArrayList<String> member;
        for (int i=0; i < congressmanList.size(); i++) {
            c = congressmanList.get(i);
            member = new ArrayList<String>();
            member.add(c.getName());
            member.add(c.getEmail());
            member.add(c.getEndOfTerm());
            member.add(c.getImagePath());
            member.add(c.getParty());
            member.add(c.getType());
            member.add(c.getWebsite());
            member.add(c.getDemPresVote());
            member.add(c.getRepPresVote());
            member.add(c.getCounty());

            sendIntent.putExtra("Member" + i, member);
            sendIntent.putExtra("COMMITTEES" + i, c.getCommittees());
            sendIntent.putExtra("BILLS" + i, c.getSponsoredBills());
        }
        Log.d("T", "in CongressionalView sendWatchData, listsize=" + congressmanList.size());
        startService(sendIntent);
    }
}
