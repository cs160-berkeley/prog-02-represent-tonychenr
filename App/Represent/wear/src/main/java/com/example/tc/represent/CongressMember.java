package com.example.tc.represent;

import android.graphics.Bitmap;

/**
 * Created by TC on 3/1/2016.
 */
public class CongressMember {

    private String name;
    private String[] committees;
    private String email;
    private String endOfTerm;
    private String imagePath;
    private String party;
    private String[] sponsoredBills;
    private String tweet;
    private String type;
    private String website;
    private String demPresVote;
    private String repPresVote;
    private String county;
    private Bitmap imageBitmap;

    public CongressMember(String name, String[] committees, String email, String endOfTerm,
                          String imagePath, String party, String[] sponsoredBills, String tweet,
                          String type, String website, String demPresVote, String repPresVote,
                          String county, Bitmap imageBitmap) {
        this.name = name;
        this.party = party;
        this.type = type;
        this.county = county;
        this.demPresVote = demPresVote;
        this.repPresVote = repPresVote;
        this.imageBitmap = imageBitmap;
        this.committees = committees;
        this.email = email;
        this.endOfTerm = endOfTerm;
        this.imagePath = imagePath;
        this.sponsoredBills = sponsoredBills;
        this.tweet = tweet;
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public String getParty() {
        return party;
    }

    public String getType() {
        return type;
    }

    public String getDemPresVote() {
        return demPresVote;
    }

    public String getRepPresVote() {
        return repPresVote;
    }

    public String getCounty() {
        return county;
    }

    public Bitmap getImageBitmap() {
        return imageBitmap;
    }

    public void setImageBitmap(Bitmap image) {
        imageBitmap = image;
    }

    public String[] getCommittees() {
        return committees;
    }

    public String getEmail() {
        return email;
    }
    public String getEndOfTerm () {
        return endOfTerm;
    }

    public String getImagePath() {
        return imagePath;
    }

    public String[] getSponsoredBills() {
        return sponsoredBills;
    }

    public String getTweet() {
        return tweet;
    }

    public String getWebsite() {
        return website;
    }
}
