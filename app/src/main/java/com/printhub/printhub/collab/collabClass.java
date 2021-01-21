package com.printhub.printhub.collab;

import java.util.Date;

public class collabClass {
    public String domain,description,mobileNo,whatsApp,githubId,linkedinId,status,userid,postkey;
    public Date timestamp;
    public collabClass(){

    }

    public collabClass(String domain, String description, String mobileNo, String whatsApp, String githubId, String linkedinId, String status, String userid, Date timestamp,String postkey) {
        this.domain = domain;
        this.description = description;
        this.mobileNo = mobileNo;
        this.whatsApp = whatsApp;
        this.githubId = githubId;
        this.linkedinId = linkedinId;
        this.status = status;
        this.userid = userid;
        this.timestamp = timestamp;
        this.postkey= postkey;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getWhatsApp() {
        return whatsApp;
    }

    public void setWhatsApp(String whatsApp) {
        this.whatsApp = whatsApp;
    }

    public String getGithubId() {
        return githubId;
    }

    public void setGithubId(String githubId) {
        this.githubId = githubId;
    }

    public String getLinkedinId() {
        return linkedinId;
    }

    public void setLinkedinId(String linkedinId) {
        this.linkedinId = linkedinId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getPostkey() {
        return postkey;
    }

    public void setPostkey(String postkey) {
        this.postkey = postkey;
    }
}
