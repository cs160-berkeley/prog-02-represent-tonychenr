package com.example.tc.represent;


import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class VoteFragment extends Fragment {

    private String mCountyState;
    private String mDemPresVote;
    private String mRepPresVote;
    TextView countyView;
    TextView demVoteView;
    TextView repVoteView;

    public VoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vote_card, container, false);
        countyView = (TextView) view.findViewById(R.id.countyStateText);
        demVoteView = (TextView) view.findViewById(R.id.demText);
        repVoteView = (TextView) view.findViewById(R.id.repText);

        countyView.setText(mCountyState);
        demVoteView.setText(mDemPresVote + "%");
        repVoteView.setText(mRepPresVote + "%");
        return view;
    }

    public void setVoteData(String countyState, String demPresVote, String RepPresVote) {
        mCountyState = countyState;
        mDemPresVote = demPresVote;
        mRepPresVote = RepPresVote;
    }

}
