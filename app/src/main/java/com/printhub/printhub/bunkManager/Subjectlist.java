package com.printhub.printhub.bunkManager;

public class Subjectlist {

    private String subname;
    private float percent;
    private int attendance;
    private int bunked;
    private int total;



    public Subjectlist(String subname,float percent,int attendance,int bunked ){
        this.subname = subname;
        this.percent = percent;
        this.attendance = attendance;
        this.bunked = bunked;
    }

    public String getSubname(){
        return subname;
    }

    public float getPercent(){
        return percent;
    }

    public int getAttendance(){
        return attendance;
    }

    public  int getBunked(){return bunked;}


}