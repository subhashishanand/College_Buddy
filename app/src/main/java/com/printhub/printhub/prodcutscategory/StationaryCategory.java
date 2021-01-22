package com.printhub.printhub.prodcutscategory;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.R;
import com.printhub.printhub.WebServices.WebViewActivity;
import com.printhub.printhub.sidebar.Profile;
import com.printhub.printhub.sidebar.Wishlist;

public class StationaryCategory extends AppCompatActivity {
    private ImageView notebook,pen,misc,sheets,stationary;
    private String link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stationary_category);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Stationary Category");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
}