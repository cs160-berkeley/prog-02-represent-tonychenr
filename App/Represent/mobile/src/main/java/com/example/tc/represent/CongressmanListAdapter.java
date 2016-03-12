package com.example.tc.represent;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.twitter.sdk.android.core.AppSession;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.tweetui.CompactTweetView;
import com.twitter.sdk.android.tweetui.LoadCallback;
import com.twitter.sdk.android.tweetui.TweetUtils;
import com.twitter.sdk.android.tweetui.TweetView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CongressmanListAdapter extends BaseAdapter {

	private Context context;
	private List<CongressMember> congressmen;
	private static LayoutInflater inflater = null;

	public CongressmanListAdapter (Context context, List<CongressMember> congressmen) {
		this.congressmen = congressmen;
		this.context = context;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
    public int getCount () {
        return congressmen.size();
    }

    @Override
    public Object getItem (int position) {
        return congressmen.get(position);
    }

    @Override
    public long getItemId (int position) {
        return position;
    }

    @Override
    public View getView (int position, View view, ViewGroup parent) {
    	CongressMember c = (CongressMember) getItem(position);

    	if (view == null) {
            view = inflater.inflate(R.layout.congressman_list_item, parent, false);
            ViewHolder holder = new ViewHolder(view);
            CompactTweetView tweetView = new CompactTweetView(context, c.getTweet());
            holder.layout_TweetContainer.addView(tweetView);
//            Log.d("listAdapter", c.getTweet().text);
        }

    	//now set the view elements to this congressman's information
        ImageView imageView = (ImageView) view.findViewById(R.id.congressImage);
    	TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView partyView = (TextView) view.findViewById(R.id.party);
        TextView emailView = (TextView) view.findViewById(R.id.email);
        TextView websiteView = (TextView) view.findViewById(R.id.website);
        GetImageTask imageTask = new GetImageTask();
        Bitmap image = null;
        try {
            image = imageTask.execute(c.getImagePath()).get();
            imageView.setImageBitmap(image);
        } catch (InterruptedException | ExecutionException e) {
            Log.d("getView", "Failed to get image");
        }
        if (c.getType().equals("Sen")) {
            nameView.setText("Senator " + c.getName());
        } else {
            nameView.setText("Rep. " + c.getName());
        }
        partyView.setText(c.getParty());
        emailView.setText(c.getEmail());
        websiteView.setText(c.getWebsite());
        return view;
    }

    class ViewHolder {
        FrameLayout layout_TweetContainer;
        public ViewHolder(View view) {
            layout_TweetContainer = (FrameLayout) view.findViewById(R.id.tweet_layout);
            view.setTag(this);
        }
    }

    private class GetImageTask extends AsyncTask<String,Void,Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            return getBitmapFromURL(params[0]);
        }

        private Bitmap getBitmapFromURL(String myurl) {
            try {
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
