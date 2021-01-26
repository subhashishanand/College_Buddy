package com.printhub.printhub.WebServices;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;
import com.printhub.printhub.collab.collabActivity;

import es.dmoral.toasty.Toasty;

public class MainActivityWeb extends AppCompatActivity {
    private ImageView zomato,dominos,ola,redbus,udemy,twitter;
    private String link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_web2);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        zomato=findViewById(R.id.zomato);
        dominos=findViewById(R.id.dominos);
        ola=findViewById(R.id.ola);
        redbus=findViewById(R.id.redbus);
        udemy=findViewById(R.id.udemy);
        twitter=findViewById(R.id.twitter);



        zomato.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "https://www.zomato.com/india";
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                i.putExtra("Link", link);
                startActivity(i);
            }
        });

        dominos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                link = "https://www.dominos.co.in/";
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                i.putExtra("Link", link);
                startActivity(i);


            }
        });

        ola.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "https://book.olacabs.com/";
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                i.putExtra("Link", link);
                startActivity(i);

            }
        });

        redbus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "https://www.redbus.in/";
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                i.putExtra("Link", link);
                startActivity(i);

            }
        });

        udemy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "https://www.udemy.com/";
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
                i.putExtra("Link", link);
                startActivity(i);

            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                link = "https://twitter.com/?lang=en";
                Intent i = new Intent(getApplicationContext(), WebViewActivity.class);
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
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void tapview() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.zomato), "Web Service", "Just click on the baner and enjoy the services")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.colorAccent2)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.first))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        Toasty.success(MainActivityWeb.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }


    public void viewtour(View view){
        tapview();
    }

    public void homeclick(View view){
        startActivity(new Intent(MainActivityWeb.this,MainnewActivity.class));
        finish();
    }

}