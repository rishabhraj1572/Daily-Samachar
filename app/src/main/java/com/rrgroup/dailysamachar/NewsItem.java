package com.rrgroup.dailysamachar;

public class NewsItem {
private String mDate,mHeadline,mImage,mDescription,mLink,mSrc;
private String mTitle,mTeamOne,mTeamTwo,mUpdate;

public NewsItem(String Date,String Headline,String Image,String Description,String Link,String Src){
    mDate=Date;
    mHeadline=Headline;
    mImage=Image;
    mDescription=Description;
    mLink=Link;
    mSrc=Src;
}

public NewsItem(String title,String update,String teamone,String teamtwo){
        mTitle=title;
        mTeamOne=teamone;
        mTeamTwo=teamtwo;
        mUpdate=update;
}

    public String getDate() {
        return mDate;
    }

    public String getDescription() {
        return mDescription;
    }

    public String getHeadline() {
        return mHeadline;
    }

    public String getImage() {
        return mImage;
    }

    public String getLink() {
        return mLink;
    }

    public String getSrc() {
        return mSrc;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getUpdate() {
        return mUpdate;
    }

    public String getTeamOne() {
        return mTeamOne;
    }

    public String getTeamTwo() {
        return mTeamTwo;
    }
};


