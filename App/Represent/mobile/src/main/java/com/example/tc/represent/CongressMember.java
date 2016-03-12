package com.example.tc.represent;

import com.twitter.sdk.android.core.models.Tweet;

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
    private String twitterID;
    private String type;
    private String website;
    private String demPresVote;
    private String repPresVote;
    private String county;
    private String bioguide_id;
    private Tweet tweet;

    public CongressMember(String name, String[] committees, String email, String endOfTerm,
                          String imagePath, String party, String[] sponsoredBills, String twitterID,
                          String type, String website, String demPresVote, String repPresVote,
                          String county, String bioguide_id) {
        this.name = name;
        this.committees = committees;
        this.email = email;
        this.endOfTerm = endOfTerm;
        this.imagePath = imagePath;
        this.party = party;
        this.sponsoredBills = sponsoredBills;
        this.twitterID = twitterID;
        this.type = type;
        this.website = website;
        this.demPresVote = demPresVote;
        this.repPresVote = repPresVote;
        this.county = county;
        this.bioguide_id = bioguide_id;
        this.tweet = null;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public void setDemPresVote(String demPresVote) {
        this.demPresVote = demPresVote;
    }

    public void setRepPresVote(String repPresVote) {
        this.repPresVote = repPresVote;
    }

    public void setSponsoredBills(String[] sponsoredBills) {
        this.sponsoredBills = sponsoredBills;
    }

    public void setCommittees(String[] committees) {
        this.committees = committees;
    }

    public void setTweet(Tweet tweet) {
        this.tweet = tweet;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getName() {
        return name;
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

    public String getParty() {
        return party;
    }

    public String[] getSponsoredBills() {
        return sponsoredBills;
    }

    public String getTwitterID() {
        return twitterID;
    }

    public Tweet getTweet() {
        return tweet;
    }

    public String getType() {
        return type;
    }

    public String getWebsite() {
        return website;
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

    public String getBioguide_id() {
        return bioguide_id;
    }
}
