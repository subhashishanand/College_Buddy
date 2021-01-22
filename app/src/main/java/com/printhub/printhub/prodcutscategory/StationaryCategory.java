package com.printhub.printhub.prodcutscategory;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.printhub.printhub.R;
import com.printhub.printhub.WebServices.WebViewActivity;

public class StationaryCategory extends AppCompatActivity {
    private Button notebook,pen,misc,sheets,stationary;
    private String link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationary_category);
        misc=findViewById(R.id.misc);
        notebook=findViewById(R.id.notebook);
        pen=findViewById(R.id.pen);
        stationary=findViewById(R.id.stationary);
        sheets=findViewById(R.id.sheets);

        notebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "notebook";
                Intent i = new Intent(getApplicationContext(), Stationary.class);
                i.putExtra("Link", link);
                startActivity(i);
            }
        });

        pen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "pen";
                Intent i = new Intent(getApplicationContext(), Stationary.class);
                i.putExtra("Link", link);
                startActivity(i);

            }
        });

        stationary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "stationary";
                Intent i = new Intent(getApplicationContext(), Stationary.class);
                i.putExtra("Link", link);
                startActivity(i);

            }
        });

        sheets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "sheets";
                Intent i = new Intent(getApplicationContext(), Stationary.class);
                i.putExtra("Link", link);
                startActivity(i);
            }
        });

        misc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "misc";
                Intent i = new Intent(getApplicationContext(), Stationary.class);
                i.putExtra("Link", link);
                startActivity(i);

            }
        });

    }
}