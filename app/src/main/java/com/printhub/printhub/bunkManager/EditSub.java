package com.printhub.printhub.bunkManager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.printhub.printhub.R;

public class EditSub extends AppCompatActivity {

    TextView t;
    private ArrayList<Subjectlist> subjectArrayList;
    private RecyclerAdapter recyclerAdapter;
    String subname;
    int position;
    int attendance;
    int bunk;
    int extraatd = 0,lesatd = 0,extrabnk = 0,lessbnk = 0;
    float percent;
    DatabaseHelper db = new DatabaseHelper(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editsub);
        getComingIntent();
        subjectArrayList = new ArrayList<>();
        recyclerAdapter = new RecyclerAdapter(this,subjectArrayList);


        Button incatd = (Button) findViewById(R.id.incatd);
        Button decatd = (Button) findViewById(R.id.decatd);
        Button incbnk = (Button) findViewById(R.id.incbnk);
        Button decbnk = (Button) findViewById(R.id.decbnk);
        Button resetsub = (Button) findViewById(R.id.resetsub);
        Button deletesub = (Button) findViewById(R.id.deletesub);
        Button ok = (Button) findViewById(R.id.ok);

        incatd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extraatd++;
                calpercentage();
                setData();
            }
        });

        incbnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                extrabnk++;
                calpercentage();
                setData();
            }
        });

        decatd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (attendance + extraatd - lesatd <= 0){
                    Toast.makeText(getApplicationContext(),"Cannot decrease anymore !",Toast.LENGTH_SHORT).show();
                }
                else {
                    lesatd++;
                    calpercentage();
                    setData();
                }
            }
        });

        decbnk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bunk + extrabnk - lessbnk <= 0){
                    Toast.makeText(getApplicationContext(),"Cannot decrease anymore !",Toast.LENGTH_SHORT).show();
                }
                else {
                    lessbnk++;
                    calpercentage();
                    setData();
                }
            }
        });

        resetsub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.resetsub(subname);
                startActivities(new Intent[]{new  Intent(getApplicationContext(), BunkActivity.class)});
                finish();
            }
        });

        deletesub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.deletesub(subname);
                startActivities(new Intent[]{new  Intent(getApplicationContext(), BunkActivity.class)});
                finish();
            }
        });


        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(attendance + extraatd - lesatd + bunk + extrabnk - lessbnk  == 0 ){
                    db.resetsub(subname);
                    startActivities(new Intent[]{new Intent(getApplicationContext(), BunkActivity.class)});
                    finish();
                }
                else {
                    db.incatd(subname, extraatd);
                    db.incbnk(subname, extrabnk);
                    db.decattend(subname, lesatd);
                    db.decbunk(subname, lessbnk);
                    startActivities(new Intent[]{new Intent(getApplicationContext(), BunkActivity.class)});
                    finish();
                }
            }
        });


    }

    public void getComingIntent() //To get intent values from recycleradapter.java
    {
        subname = getIntent().getStringExtra("subname");
        percent = getIntent().getFloatExtra("percent",100);
        attendance= getIntent().getIntExtra("attendance",0);
        position = getIntent().getIntExtra("position",0);
        bunk = getIntent().getIntExtra("bunk",0);
        setData();
    }

    public void calpercentage(){
        percent = ((float)(attendance + extraatd - lesatd)*100)/(float) (attendance + extraatd - lesatd + bunk + extrabnk - lessbnk);
    }

    private void setData() ///To set data to text box
    {
        if(attendance + extraatd - lesatd + bunk + extrabnk - lessbnk  == 0 ){
            TextView a = (TextView)findViewById(R.id.percent);
            a.setText("100 %");
            t = (TextView) findViewById(R.id.subname);
            t.setText(subname);

            t = (TextView) findViewById(R.id.atd);
            t.setText(String.valueOf(attendance + extraatd - lesatd));

            t = (TextView) findViewById(R.id.bnk);
            t.setText(String.valueOf(bunk + extrabnk - lessbnk));

        }
        else
        {
            t = (TextView) findViewById(R.id.subname);
            t.setText(subname);

            t = (TextView) findViewById(R.id.percent);
            t.setText(String.format("%.2f", percent) + " %");

            t = (TextView) findViewById(R.id.atd);
            t.setText(String.valueOf(attendance + extraatd - lesatd));

            t = (TextView) findViewById(R.id.bnk);
            t.setText(String.valueOf(bunk + extrabnk - lessbnk));
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(EditSub.this, BunkActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

}