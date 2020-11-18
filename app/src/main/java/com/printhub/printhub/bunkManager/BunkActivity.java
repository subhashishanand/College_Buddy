package com.printhub.printhub.bunkManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;

import java.util.ArrayList;

public class BunkActivity extends AppCompatActivity {

    private ArrayList<Subjectlist> subjectArrayList;
    public RecyclerAdapter recyclerAdapter;
    DatabaseHelper db = new DatabaseHelper(this); //Object for database helper class


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        subjectArrayList = new ArrayList<>();
        FloatingActionButton fab = findViewById(R.id.fab);
        RecyclerView recyclerView = findViewById(R.id.scrollableview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(linearLayoutManager);

        setRecyclerViewData(); //To set Data for elements of recycler view

        recyclerAdapter = new RecyclerAdapter(this, subjectArrayList);
        recyclerView.setAdapter(recyclerAdapter);

        fab.setOnClickListener(onAddingListner()); // action for floating action button

    }


    private void setRecyclerViewData() {

        //Adding DataBase data to the ArrayList to Display on RecyclerView
        Cursor res = db.getAllData();
        if(res.getCount() == 0){
            return;
        }
        while (res.moveToNext()){
            subjectArrayList.add(new Subjectlist(res.getString(0),Float.parseFloat(res.getString(4)),Integer.parseInt(res.getString(1)),Integer.parseInt(res.getString(2))));
        }

    }




    private View.OnClickListener onAddingListner(){ //To add new subject...
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                final Dialog dialog = new Dialog(BunkActivity.this);
                dialog.setContentView(R.layout.addsubject); //To specify the layout of dialogue
                dialog.setTitle("Add New Subject");
                dialog.setCancelable(false); /// cannot dismiss if touched else where

                ////////// setting layout for component
                EditText subname = (EditText) dialog.findViewById(R.id.name);
                Button savebtn = (Button) dialog.findViewById(R.id.btn_ok);
                Button cancelbtn = (Button) dialog.findViewById(R.id.btn_cancel);

                //////////// handling event for 2 buttons/////////////////
                savebtn.setOnClickListener(onConfirmListener(subname,dialog));
                cancelbtn.setOnClickListener(onCancelListener(dialog));

                dialog.show();
            }
        };

    }

    private View.OnClickListener onConfirmListener(final EditText subname, final Dialog dialog) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Subjectlist newsub = new Subjectlist(subname.getText().toString().trim(), 0,0,0);

                //adding new object to database
                db.createsub(subname.getText().toString().trim(),0,0,0,0);


                //adding new object to arraylist
                subjectArrayList.add(newsub);

                //notify data set changed in RecyclerView adapter
                recyclerAdapter.notifyDataSetChanged();
                //.notifyDataSetChanged();


                Toast.makeText(getBaseContext(), "New subject added !" , Toast.LENGTH_SHORT ).show(); //to toast a data

                //close dialog after all
                dialog.dismiss();
            }
        };
    }

    private View.OnClickListener onCancelListener(final Dialog dialog){
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        };

    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(BunkActivity.this, MainnewActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

}
