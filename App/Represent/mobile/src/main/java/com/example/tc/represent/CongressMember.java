package com.example.tc.represent;

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

    public CongressMember(String name, String[] committees, String email, String endOfTerm,
                          String imagePath, String party, String[] sponsoredBills, String tweet,
                          String type, String website, String demPresVote, String repPresVote, String county) {
        this.name = name;
        this.committees = committees;
        this.email = email;
        this.endOfTerm = endOfTerm;
        this.imagePath = imagePath;
        this.party = party;
        this.sponsoredBills = sponsoredBills;
        this.tweet = tweet;
        this.type = type;
        this.website = website;
        this.demPresVote = demPresVote;
        this.repPresVote = repPresVote;
        this.county = county;
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

    public String getTweet() {
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
}
