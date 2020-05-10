package com.printhub.printhub;

public class user {

    private String name;
    private String rollNumber;
    private String mobileNumber;
    private String collegeName;
    private String hostelName;
    private String cityName;


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

    public user(String name, String rollNumber, String mobileNumber, String collegeName, String hostelName, String cityName) {
        this.name = name;
        this.rollNumber = rollNumber;
        this.mobileNumber = mobileNumber;
        this.collegeName = collegeName;
        this.hostelName = hostelName;
        this.cityName = cityName;
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

    public void setHostelName(String hostelName) {
        this.hostelName = hostelName;
    }
}
