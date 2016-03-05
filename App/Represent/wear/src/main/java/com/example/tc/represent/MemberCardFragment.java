package com.example.tc.represent;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.wearable.view.CardFragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.lang.reflect.Member;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class MemberCardFragment extends CardFragment {

    private CongressMember mMember;
    ImageButton imageBtn;
    TextView typeView;
    TextView nameView;

    public MemberCardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateContentView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_member_card, container, false);
        imageBtn = (ImageButton) view.findViewById(R.id.imageButton);
        typeView = (TextView) view.findViewById(R.id.typeText);
        nameView = (TextView) view.findViewById(R.id.nameText);

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent detailIntent = new Intent(getActivity(), WatchToPhoneService.class);
                detailIntent.putExtra("NAME", mMember.getName());
                detailIntent.putExtra("COMMITTEES", mMember.getCommittees());
                detailIntent.putExtra("EMAIL", mMember.getEmail());
                detailIntent.putExtra("ENDOFTERM", mMember.getEndOfTerm());
                detailIntent.putExtra("IMAGE", mMember.getImagePath());
                detailIntent.putExtra("PARTY", mMember.getParty());
                detailIntent.putExtra("BILLS", mMember.getSponsoredBills());
                detailIntent.putExtra("TYPE", mMember.getType());
                detailIntent.putExtra("WEBSITE", mMember.getWebsite());
                detailIntent.putExtra("isShake", "False");
                getActivity().startService(detailIntent);
            }
        });
        typeView.setText(mMember.getType().substring(0, 3));
        nameView.setText(mMember.getName());
        return view;
    }

    public void setCongressMember(CongressMember member) {
        mMember = member;
    }

}
