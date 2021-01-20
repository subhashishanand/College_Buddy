package com.printhub.printhub.registration;

public class user {

    private String name;
    private String rollNumber;
    private String mobileNumber;
    private String collegeName;
    private String hostelName;
    private String cityName;
    private String token;
    private String imageLink;



    public user() {
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public user(String name, String rollNumber, String mobileNumber, String collegeName, String hostelName, String cityName, String token,String imageLink) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.mobileNumber = mobileNumber;
        this.collegeName = collegeName;
        this.hostelName = hostelName;
        this.cityName = cityName;
        this.imageLink=imageLink;
        this.token = token;
    }

    public String getrollNumber() {
        return rollNumber;
    }

    public void setrollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getCollegeName() {
        return collegeName;
    }

    public void setCollegeName(String collegeName) {
        this.collegeName = collegeName;
    }

    public String getHostelName() {
        return hostelName;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
    }

    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
