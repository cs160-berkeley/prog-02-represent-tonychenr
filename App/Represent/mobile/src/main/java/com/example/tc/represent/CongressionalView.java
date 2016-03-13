package com.example.tc.represent;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import io.fabric.sdk.android.Fabric;

public class CongressionalView extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    CongressmanListAdapter congressAdapter;
    private List<CongressMember> congressmanList = new ArrayList<CongressMember>();
    private GoogleApiClient mGoogleApiClient;
    private String mLatitudeText;
    private String mLongitudeText;
    private Location mLastLocation;
    private String mZipCode;
    private String baseURL = "https://congress.api.sunlightfoundation.com";
    private String baseImageURL = "https://theunitedstates.io/images/congress/225x275/";
    private String baseGeocodingURL = "https://maps.googleapis.com/maps/api/geocode/json?";
    private static final String SUNLIGHT_KEY =  "";

    private static final String TWITTER_KEY = "";
    private static final String TWITTER_SECRET = "";
    private static final String GOOGLE_KEY = "";
    private String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.congress_list);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        String zipCode = getIntent().getExtras().getString("ZIP_CODE");
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void getCongressMembers(String zipCode) {
        mZipCode = zipCode;
        if (zipCode.equals("random")) {
            new GetCongressMembersTask().execute(zipCode);
        } else {
            StringBuffer url = new StringBuffer();
            url.append(baseURL).append("/legislators/locate?");

            if (zipCode.equals("current")) {
                url.append("latitude=").append(mLatitudeText);
                url.append("&longitude=").append(mLongitudeText);
            } else {
                url.append("zip=").append(zipCode);
            }
            url.append("&apikey=").append(SUNLIGHT_KEY);
            ListView congressListView = (ListView) findViewById(R.id.congress_list);
            congressListView.setAdapter(null);
            new GetCongressMembersTask().execute(url.toString());
        }
    }

    private void setTweets(final ArrayList<CongressMember> members) {
        TwitterCore.getInstance().logInGuest(new Callback<AppSession>() {
            @Override
            public void success(Result<AppSession> appSessionResult) {
                AppSession session = appSessionResult.data;
                TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient(session);
                for (final CongressMember c : members) {
                    twitterApiClient.getStatusesService().userTimeline(null, c.getTwitterID(), 1,
                            null, null, false, false, false, true, new Callback<List<Tweet>>() {

                                @Override
                                public void success(Result<List<Tweet>> result) {
                                    if (result.data.size() > 0) {
                                        c.setTweet(result.data.get(0));
                                    }
                                    for (CongressMember member : members) {
                                        if (member.getTweet() != null) {
                                            setListAdapter();
                                        }
                                    }
                                }

                                @Override
                                public void failure(TwitterException e) {
                                    e.printStackTrace();
                                }
                            });
                }
            }

            @Override
            public void failure(TwitterException e) {
                e.printStackTrace();
            }
        });
    }

    private void setListAdapter() {
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
            Log.d("T", "in CongressionalView sendWatchData " + member.get(9));
            sendIntent.putExtra("Member" + i, member);
            sendIntent.putExtra("COMMITTEES" + i, c.getCommittees());
            sendIntent.putExtra("BILLS" + i, c.getSponsoredBills());
        }
        startService(sendIntent);
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        if (mLastLocation == null) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                mLatitudeText = String.valueOf(mLastLocation.getLatitude());
                mLongitudeText = String.valueOf(mLastLocation.getLongitude());
            }
            getCongressMembers(getIntent().getExtras().getString("ZIP_CODE"));
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.i("CongV", "Connection suspended");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("CongV", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }
    // Reads an InputStream and converts it to a String.
    private String readIt(InputStream inputStream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line).append("\n");
        }
        if (buffer.length() == 0) {
            return null;
        }
        try {
            reader.close();
        } catch (final IOException e) {
            Log.e("get", "IOException - error closing error BufferedReader stream", e);
        }
        return buffer.toString();
    }

    public String[] getLatLongByURL(String requestURL) {
        URL url;
        InputStream inputStream;
        HttpURLConnection conn;
        String[] latlon = new String[2];
        try {
            url = new URL(requestURL);
            conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            inputStream = conn.getInputStream();
            if (inputStream == null) {
                return null;
            }

            String geocodingJsonStr = readIt(inputStream);
            JSONObject  jsonRootObject = new JSONObject(geocodingJsonStr);
            JSONArray results = jsonRootObject.optJSONArray("results");
            if (results.length() == 0) {
                return null;
            }
            JSONObject locationJson = results.getJSONObject(0).getJSONObject("geometry")
                    .getJSONObject("location");

            latlon[0] = String.valueOf(locationJson.getDouble("lat"));
            latlon[1] = String.valueOf(locationJson.getDouble("lng"));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return latlon;
    }

    private String loadJSONFromAsset(String path) throws IOException {
        String json = null;
        InputStream is = getAssets().open(path);
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");
        return json;

    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class GetCongressMembersTask extends AsyncTask<String, Void, ArrayList<CongressMember>> {
        private ProgressDialog dialog;
        private String mCounty = null;
        private String demPresVote = null;
        private String repPresVote = null;

        @Override
        protected void onPreExecute() {
            dialog = ProgressDialog.show(CongressionalView.this, " ", "Getting Congress members ...", true);
        }
        @Override
        protected ArrayList<CongressMember> doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            String url = urls[0];
            try {
                if (urls[0].equals("random")) {
                    try {
                        Random rand = new Random();
                        StringBuffer randomUrl = new StringBuffer();
                        JSONArray counties = new JSONArray(loadJSONFromAsset("election-county-2012-list.json"));
                        int randInt = rand.nextInt(counties.length());
                        JSONObject voteData = counties.getJSONObject(randInt);
                        String county = voteData.getString("county-name");
                        String state = voteData.getString("state-postal");
                        StringBuffer zipUrl = new StringBuffer();
                        zipUrl.append(baseGeocodingURL).append("address=");
                        zipUrl.append(county).append(",+").append(state);
                        zipUrl.append("&key=").append(GOOGLE_KEY);
                        String[] latlon = getLatLongByURL(zipUrl.toString());
                        if (latlon == null) {
                            return null;
                        }
                        mLatitudeText = latlon[0];
                        mLongitudeText = latlon[1];
                        mCounty = county + ", " + state;
                        demPresVote = String.valueOf(voteData.getDouble("obama-percentage"));
                        repPresVote = String.valueOf(voteData.getDouble("romney-percentage"));
                        Log.d("random", latlon[0] + "," + latlon[1]);
                        randomUrl.append(baseURL).append("/legislators/locate?");
                        randomUrl.append("latitude=").append(latlon[0]);
                        randomUrl.append("&longitude=").append(latlon[1]);
                        randomUrl.append("&apikey=").append(SUNLIGHT_KEY);
                        url = randomUrl.toString();

                        title = mCounty;
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }
                }
                ArrayList<CongressMember> members = getMembersFromURL(url);
                if (members.size() == 0) {
                    StringBuffer zipUrl = new StringBuffer();
                    zipUrl.append(baseGeocodingURL).append("address=").append(mZipCode);
                    zipUrl.append("&key=").append(GOOGLE_KEY);
                    String[] latlon = getLatLongByURL(zipUrl.toString());
                    if (latlon == null) {
                        return null;
                    }

                    StringBuffer newUrl = new StringBuffer();
                    newUrl.append(baseURL).append("/legislators/locate?");
                    newUrl.append("latitude=").append(latlon[0]);
                    newUrl.append("&longitude=").append(latlon[1]);
                    newUrl.append("&apikey=").append(SUNLIGHT_KEY);
                    members = getMembersFromURL(newUrl.toString());
                    if (members.size() == 0) {
                        return null;
                    }
                }
                setMemberImages(members);
                congressmanList = members;

                return members;
            } catch (IOException e) {
                Log.e("getCongressMembersTask", "Unable to retrieve web page. URL may be invalid.");
            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(ArrayList<CongressMember> members) {
            if (members == null || members.size() == 0) {
                Toast.makeText(CongressionalView.this, "Could not find Congress members at location",
                        Toast.LENGTH_SHORT).show();
            } else {
                setTweets(members);
                if (mCounty != null) {
                    for (CongressMember c : congressmanList) {
                        c.setDemPresVote(demPresVote);
                        c.setRepPresVote(repPresVote);
                        c.setCounty(mCounty);
                    }
                    setTitle(title);
                    sendWatchData();
                } else {
                    new GetVotingDataTask().execute(mZipCode);
                }
            }
            dialog.dismiss();
        }

        // Given a URL, establishes an HttpUrlConnection and retrieves
        // the web page content as a InputStream, which it returns as
        // a string.
        private ArrayList<CongressMember> getMembersFromURL(String myurl) throws IOException {
            InputStream inputStream = null;
            HttpURLConnection conn = null;

            try {
                URL url = new URL(myurl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                // Starts the query
                conn.connect();
                int response = conn.getResponseCode();
                inputStream = conn.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                // Convert the InputStream into a string
                String congressJsonStr = readIt(inputStream);
                try {
                    return getMembersFromJSON(congressJsonStr);
                } catch (JSONException e) {
                    Log.e("getCongressMembersTask","JSONException - error converting json");
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.e("getCongressMembersTask","IOException - error fetching JSON from inputStream");
                return null;
            } finally {
                if (conn != null) {
                    conn.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (final IOException e) {
                        Log.e("getCongressMembersTask", "IOException - error InputStream", e);
                    }
                }
            }

            return null;
        }

        private ArrayList<CongressMember> getMembersFromJSON(String json) throws JSONException, IOException {
            ArrayList<CongressMember> membersFromJson = new ArrayList<CongressMember>();

            JSONObject  jsonRootObject = new JSONObject(json);
            JSONArray membersJsonArray = jsonRootObject.optJSONArray("results");
            CongressMember member;
            String name;
            String email;
            String endOfTerm;
            String party;
            String twitterID;
            String type;
            String website;
            String bioguide_id;
            for (int i = 0; i < membersJsonArray.length(); i++) {
                JSONObject memberObject = membersJsonArray.getJSONObject(i);
                name = memberObject.getString("first_name") + " " + memberObject.getString("last_name");
                email = memberObject.getString("oc_email");
                endOfTerm = memberObject.getString("term_end");
                party = memberObject.getString("party");
                twitterID = memberObject.getString("twitter_id");
                type = memberObject.getString("title");
                website = memberObject.getString("website");
                bioguide_id = memberObject.getString("bioguide_id");
                member = new CongressMember(name, null, email, endOfTerm, null, party, null,
                        twitterID, type, website, null, null, null, bioguide_id);
                membersFromJson.add(member);
            }
            try {
                fillCommittees(membersFromJson);
                fillSponsoredBills(membersFromJson);
            } catch (IOException e) {
                Log.e("getMembersFromJSON", "IOException - error filling committees and bills");
            }

            return membersFromJson;
        }

        private void fillCommittees(ArrayList<CongressMember> members) throws IOException, JSONException {
            InputStream inputStream;
            HttpURLConnection conn;
            StringBuffer myurl;
            for (CongressMember member : members) {
                inputStream = null;
                conn = null;
                myurl = new StringBuffer();
                myurl.append(baseURL).append("/committees?");
                myurl.append("member_ids=").append(member.getBioguide_id());
                myurl.append("&apikey=" + SUNLIGHT_KEY);
                try {
                    URL url = new URL(myurl.toString());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    inputStream = conn.getInputStream();
                    if (inputStream == null) {
                        return;
                    }

                    // Convert the InputStream into a string
                    String committeesJsonStr = readIt(inputStream);
                    JSONObject  jsonRootObject = new JSONObject(committeesJsonStr);
                    JSONArray committeesJsonArray = jsonRootObject.optJSONArray("results");
                    String[] committees = new String[2];
                    for (int i = 0; i < 2; i++) {
                        JSONObject committeeObject = committeesJsonArray.getJSONObject(i);
                        committees[i] = committeeObject.getString("name");
                    }
                    member.setCommittees(committees);
                    // Makes sure that the InputStream is closed after the app is
                    // finished using it.
                } catch (IOException e) {
                    Log.e("fillCommitteesTask","IOException - error fetching JSON from inputStream");
                    return;
                } catch (JSONException e) {
                    Log.e("fillCommitteesTask","JSONException - error converting JSON");
                    return;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (final IOException e) {
                            Log.e("fillCommitteesTask", "IOException - error InputStream", e);
                        }
                    }
                }
            }
        }

        private void fillSponsoredBills(ArrayList<CongressMember> members) {
            InputStream inputStream;
            HttpURLConnection conn;
            StringBuffer myurl;
            for (CongressMember member : members) {
                inputStream = null;
                conn = null;
                myurl = new StringBuffer();
                myurl.append(baseURL).append("/bills?");
                myurl.append("sponsor_id=").append(member.getBioguide_id());
                myurl.append("&apikey=" + SUNLIGHT_KEY);
                try {
                    URL url = new URL(myurl.toString());
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);
                    // Starts the query
                    conn.connect();
                    inputStream = conn.getInputStream();
                    if (inputStream == null) {
                        return;
                    }

                    // Convert the InputStream into a string
                    String committeesJsonStr = readIt(inputStream);
                    JSONObject  jsonRootObject = new JSONObject(committeesJsonStr);
                    JSONArray committeesJsonArray = jsonRootObject.optJSONArray("results");
                    String[] bills = new String[2];
                    for (int i = 0; i < 2; i++) {
                        JSONObject billObject = committeesJsonArray.getJSONObject(i);
                        bills[i] = billObject.getString("introduced_on")
                                + System.getProperty("line.separator");
                        if (!billObject.getString("short_title").equals("null")) {
                            bills[i] += billObject.getString("short_title");
                        } else {
                            bills[i] += billObject.getString("official_title");
                        }

                    }
                    member.setSponsoredBills(bills);
                    // Makes sure that the InputStream is closed after the app is
                    // finished using it.
                } catch (IOException e) {
                    Log.e("fillSponsoredBillsTask","IOException - error fetching JSON from inputStream");
                    return;
                } catch (JSONException e) {
                    Log.e("fillSponsoredBillsTask","JSONException - error converting JSON");
                    return;
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                    if (inputStream != null) {
                        try {
                            inputStream.close();
                        } catch (final IOException e) {
                            Log.e("fillSponsoredBillsTask", "IOException - error InputStream", e);
                        }
                    }
                }
            }
        }

        private void setMemberImages(ArrayList<CongressMember> members) {
            StringBuffer url;
            for (CongressMember c : members) {
                url = new StringBuffer();
                url.append(baseImageURL).append(c.getBioguide_id()).append(".jpg");
                c.setImagePath(url.toString());
            }
        }


    }

    private class GetVotingDataTask extends AsyncTask<String, Void, String> {

        private String latitude;
        private String longitude;
        private String county;
        private String state;
        private String demPresVote;
        private String repPresVote;

        @Override
        protected String doInBackground(String... zipCode) {
            // params comes from the execute() call: params[0] is the url.

                if (zipCode[0].equals("current") || zipCode[0].equals("shake")) {
                    latitude = mLatitudeText;
                    longitude = mLongitudeText;
                } else {
                    StringBuffer zipUrl = new StringBuffer();
                    zipUrl.append(baseGeocodingURL).append("address=").append(zipCode[0]);
                    zipUrl.append("&key=").append(GOOGLE_KEY);
                    String[] latlon = getLatLongByURL(zipUrl.toString());
                    if (latlon == null) {
                        return null;
                    }
                    latitude = latlon[0];
                    longitude = latlon[1];
                }
                StringBuffer countyUrl = new StringBuffer();
                countyUrl.append(baseGeocodingURL).append("latlng=");
                countyUrl.append(latitude).append(",").append(longitude);
                countyUrl.append("&key=").append(GOOGLE_KEY);
                Log.d("GetVotingDataTask", countyUrl.toString());
                return setVotingData(countyUrl.toString());
        }

        @Override
        protected void onPostExecute(String result) {
            if (result == null) {
                Log.e("getVoteData", "Could not get voting data");
                Toast.makeText(CongressionalView.this, "Could not load voting data at location",
                        Toast.LENGTH_SHORT).show();
            } else {
                setTitle(title);
                sendWatchData();
            }

        }

        private String setVotingData(String voteUrl) {
            URL url;
            InputStream inputStream;
            HttpURLConnection conn;
            try {
                url = new URL(voteUrl);
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(15000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");
                conn.setDoOutput(true);
                int responseCode = conn.getResponseCode();
                inputStream = conn.getInputStream();
                if (inputStream == null) {
                    return null;
                }

                String geocodingJsonStr = readIt(inputStream);
                JSONObject  jsonRootObject = new JSONObject(geocodingJsonStr);
                JSONArray results = jsonRootObject.optJSONArray("results");
                JSONArray addressComponents = results.getJSONObject(0).getJSONArray("address_components");
                for (int i = 0; i < addressComponents.length(); i++) {
                    JSONObject component = addressComponents.getJSONObject(i);
                    if (component.getJSONArray("types").getString(0).equals("administrative_area_level_1")) {
                        state = component.getString("short_name");
                    } else if (component.getJSONArray("types").getString(0).equals("administrative_area_level_2")) {
                        county = component.getString("long_name");
                    }
                }
                JSONObject voteData = new JSONObject(loadJSONFromAsset("election-county-2012.json"));
                String countyState = county + ", " + state;
                title = countyState;
                JSONObject countyVote = voteData.getJSONObject(countyState);
                if (countyVote == null) {
                    return null;
                }

                demPresVote = String.valueOf(countyVote.getDouble("obama"));
                repPresVote = String.valueOf(countyVote.getDouble("romney"));
                for (CongressMember c : congressmanList) {
                    c.setDemPresVote(demPresVote);
                    c.setRepPresVote(repPresVote);
                    c.setCounty(countyState);
                }
                return "1";
            } catch (IOException e) {
                Log.e("setVotingData", "IOException - unable to load JSON from asset");
            } catch (JSONException e) {
                Log.e("setVotingData", "JSONException - unable to get value in JSON");
                e.printStackTrace();
            }
            return null;
        }
    }
}
