package com.printhub.printhub.bunkManager;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper{
    public static final String database_name = "Attendance_manager.db";
    public static final String table_name = "AttendanceList";
    public static final String col_1 = "sname";
    public static final String col_2 = "att_lec";
    public static final String col_3 = "bunk_lec";
    public static final String col_4 = "total_lec";
    public static final String col_5 = "percent";

    public DatabaseHelper(Context context) {
        super(context, database_name,null, 1);
    }




    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table "+ table_name + "(sname varchar(20) primary key ,att_lec int , bunk_lec int , total_lec Int , percent float)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + table_name);
        onCreate(db);
    }

    public boolean createsub(String subname,int att_lec, int bunk_lec , int total_lec  , float percent ){ //TO create row for each subject
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_1,subname);
        contentValues.put(col_2,att_lec);
        contentValues.put(col_3,bunk_lec);
        contentValues.put(col_4,total_lec);
        contentValues.put(col_5,percent);
        long result = db.insert(table_name,null,contentValues);
        return result != -1;
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + table_name,null);
        return res;
    }

    public void attend(String subname){  //to attend
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET " + col_2 + " = " + col_2  + " + 1 " + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        updatetotal(subname);
        updatepercent(subname);
    }

    public void bunk(String subname){ //to bunk
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET " + col_3 + " = " +  col_3 + " + 1 " + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        updatetotal(subname);
        updatepercent(subname);
    }


    public void updatepercent(String subname){ //to percent
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET " + col_5 + " = " + " ( " + col_2   + " * 100 " +  " )" + " / " + col_4   + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
    }


    public void updatetotal(String subname){ //to total
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET " + col_4 + " = " +  col_2  + " + " + col_3  + " WHERE " + col_1  + " = " + "'" + subname + "'" ;
        db.execSQL(query);
    }

    public void decattend(String subname,int atd){  //to attend
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET " + col_2 + " = " + col_2  + " - " + atd + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        updatetotal(subname);
        updatepercent(subname);
    }

    public void decbunk(String subname,int atd){ //to bunk
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET " + col_3 + " = " +  col_3 + " - " + atd + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        updatetotal(subname);
        updatepercent(subname);
    }

    public void resetsub(String subname){ //to bunk
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET "  + col_2 + " = 0 " + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        query = "UPDATE " + table_name + " SET " + col_3 +  " = 0 " + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        query = "UPDATE " + table_name + " SET " + col_4 +  " = 0 " + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        query = "UPDATE " + table_name + " SET " + col_5 +  " = 100 " + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);

    }

    public void deletesub(String subname){ //to bunk
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + table_name + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
    }


    public void incatd(String subname,int atd){  //to attend
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET " + col_2 + " = " + atd + " + " + col_2   + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        updatetotal(subname);
        updatepercent(subname);
    }

    public void incbnk(String subname,int atd){ //to bunk
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE " + table_name + " SET " + col_3 + " = " +  col_3 + " + " + atd + " WHERE " + col_1  + " = " +  "'" + subname + "'" ;
        db.execSQL(query);
        updatetotal(subname);
        updatepercent(subname);
    }
}