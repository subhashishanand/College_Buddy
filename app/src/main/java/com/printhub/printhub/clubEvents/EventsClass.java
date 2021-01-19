package com.printhub.printhub.clubEvents;

import android.widget.EditText;

import com.google.firebase.Timestamp;

import java.util.Date;

public class EventsClass {

    public String user_id,description,imageUrl;
    public Date timestamp;

    public EventsClass(){}

    public EventsClass(String user_id, String description, String imageUrl, Date timestamp) {
        this.user_id = user_id;
        this.description = description;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
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
