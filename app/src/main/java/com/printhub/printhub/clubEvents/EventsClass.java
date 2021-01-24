package com.printhub.printhub.clubEvents;

import android.widget.EditText;

import com.google.firebase.Timestamp;

import java.util.Date;

public class EventsClass {

    public String clubName,description,imageUrl,activityDate,activityTime,link,eventid,eventTitle,userid,status;
    public Date timestamp;

    public EventsClass(){}

    public EventsClass(String clubName, String description, String imageUrl, Date timestamp,String activityDate, String activityTime, String link,String eventid,String eventTitle,String userid,String status) {
        this.clubName= clubName;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.activityDate=activityDate;
        this.activityTime=activityTime;
        this.link=link;
        this.eventid= eventid;
        this.eventTitle= eventTitle;
        this.userid= userid;
        this.status= status;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getEventid() {
        return eventid;
    }

    public void setEventid(String eventid) {
        this.eventid = eventid;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
