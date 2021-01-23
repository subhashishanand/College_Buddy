package com.printhub.printhub.globalEvents;

import java.util.Date;

public class GlobalEventClass {
   private String Name,description,locaton,imageUrl,activityDate,activityTime,link,eventid,eventTitle;
    private Date timestamp;

    public GlobalEventClass() {
    }

    public GlobalEventClass(String name, String description, String locaton, String imageUrl, String activityDate, String activityTime, String link, String eventid, String eventTitle, Date timestamp) {
        Name = name;
        this.description = description;
        this.locaton = locaton;
        this.imageUrl = imageUrl;
        this.activityDate = activityDate;
        this.activityTime = activityTime;
        this.link = link;
        this.eventid = eventid;
        this.eventTitle = eventTitle;
        this.timestamp = timestamp;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocaton() {
        return locaton;
    }

    public void setLocaton(String locaton) {
        this.locaton = locaton;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
