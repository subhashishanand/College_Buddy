package com.printhub.printhub.clubEvents;

import android.widget.EditText;

import com.google.firebase.Timestamp;

import java.util.Date;

public class EventsClass {

    public String clubName,description,imageUrl,activityDate,activityTime;
    public Date timestamp;

    public EventsClass(){}

    public EventsClass(String clubName, String description, String imageUrl, Date timestamp,String activityDate, String activityTime) {
        this.clubName= clubName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.activityDate=activityDate;
        this.activityTime=activityTime;
    }

    public String getClubName() {
        return clubName;
    }

    public void setClubName(String clubName) {
        this.clubName = clubName;
    }

    public String getActivityDate() {
        return activityDate;
    }

    public void setActivityDate(String activityDate) {
        this.activityDate = activityDate;
    }

    public String getActivityTime() {
        return activityTime;
    }

    public void setActivityTime(String activityTime) {
        this.activityTime = activityTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
