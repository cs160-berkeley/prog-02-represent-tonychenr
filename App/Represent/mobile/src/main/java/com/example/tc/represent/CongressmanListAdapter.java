package com.example.tc.represent;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

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

    	if (view == null) view = inflater.inflate(R.layout.congressman_list_item, null);

    	//now set the view elements to this congressman's information
        ImageView imageView = (ImageView) view.findViewById(R.id.congressImage);
    	TextView nameView = (TextView) view.findViewById(R.id.name);
        TextView partyView = (TextView) view.findViewById(R.id.party);
        TextView emailView = (TextView) view.findViewById(R.id.email);
        TextView websiteView = (TextView) view.findViewById(R.id.website);
        TextView tweetView = (TextView) view.findViewById(R.id.tweet);

        imageView.setImageResource(context.getResources().getIdentifier(context.getPackageName() +":" + c.getImagePath(), null, null));
        nameView.setText(c.getType() + ": " + c.getName());
        partyView.setText("Party: " + c.getParty());
        emailView.setText("Email: " + c.getEmail());
        websiteView.setText("Website: " + c.getWebsite());
        tweetView.setText("Latest tweet: " + c.getTweet());
    	//////////// etc ////////////
        return view;
    }
}