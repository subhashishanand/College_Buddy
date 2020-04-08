package com.printhub.printhub;

public class user {

    private String rollNumber;
    private String mobileNumber;
    private String collegeName;
    private String hostelName;


    public user() {
    }

    public user(String rollNumber, String mobileNumber, String collegeName, String hostelName) {
        this.rollNumber = rollNumber;
        this.mobileNumber = mobileNumber;
        this.collegeName = collegeName;
        this.hostelName = hostelName;
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
