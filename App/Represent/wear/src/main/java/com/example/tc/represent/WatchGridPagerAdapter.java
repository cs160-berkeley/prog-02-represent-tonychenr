package com.example.tc.represent;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.wearable.view.CardFragment;
import android.support.wearable.view.FragmentGridPagerAdapter;
import android.support.wearable.view.GridPagerAdapter;
import android.util.Log;

import java.lang.reflect.Member;
import java.util.List;

/**
 * Created by TC on 3/2/2016.
 */
public class WatchGridPagerAdapter extends FragmentGridPagerAdapter {

    private final Context context;
    private List mRows;
    private final List<CongressMember> members;

    public WatchGridPagerAdapter(Context context, FragmentManager fm, List<CongressMember> members) {
        super(fm);
        this.context = context;
        this.members = members;
    }
    @Override
    public int getColumnCount(int i) {
        if (i == getRowCount() - 1 || members.get(i).getType().equals("Senator")) {
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public int getRowCount() {
        return members.size() + 1;
    }

    // Obtain the UI fragment at the specified position
    @Override
    public Fragment getFragment(int row, int column) {
        String title;
        String text;
        CardFragment fragment;

        if (row == getRowCount() - 1) {
            title = "Shake Function";
            text = "Shake to randomize location";
            fragment = CardFragment.create(title, text);
            return fragment;
        }

        CongressMember member = members.get(row);
        if (column == 0) {
            fragment = new MemberCardFragment();
            ((MemberCardFragment) fragment).setCongressMember(member);
        } else {
            title = member.getCounty();
            text = "Obama: " + member.getDemPresVote() + "\n"
                    + "Romney: " + member.getRepPresVote();
            fragment = CardFragment.create(title, text);
        }

        return fragment;
    }

    // Obtain the background image for the specific page
    @Override
    public Drawable getBackgroundForPage(int row, int column) {
        if(row == getRowCount() - 1) {
            // Place image at specified position
            return context.getResources().getDrawable(R.drawable.us_map, null);
        } else {
            return new BitmapDrawable(context.getResources(), members.get(row).getImageBitmap());
        }
    }
}
