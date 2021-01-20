package com.printhub.printhub.collab;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.printhub.printhub.R;


public class collabActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.collabview);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), collabPostActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}