package com.printhub.printhub;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

public class SplashActivity extends AppCompatActivity {

    private static int SPLASH_TIME_OUT = 3000;


    //to get user session data
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SharedPreferences detail= null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        detail = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);

        TextView appname= findViewById(R.id.appname);
        appname.setTypeface(typeface);

        YoYo.with(Techniques.Bounce)
                .duration(7000)
                .playOn(findViewById(R.id.logo));

        YoYo.with(Techniques.FadeInUp)
                .duration(5000)
                .playOn(findViewById(R.id.appname));

            new Handler().postDelayed(new Runnable() {

            /*
             * Showing splash screen with a timer. This will be useful when you
             * want to show case your app logo / company
             */

                @Override
                public void run() {
                    // This method will be executed once the timer is over
                    // Start your app main activity
                    if(user==null){
                    startActivity(new Intent(SplashActivity.this, WelcomeActivity.class));
                    finish();}
                    else{
                        if (!detail.getBoolean("fillDetail", false)) {
                            Intent detailFill = new Intent(SplashActivity.this,DetailActivity.class);
                            startActivity(detailFill);
                            finish();}
                        else{
                        startActivity(new Intent(SplashActivity.this, MainnewActivity.class));
                        finish();}
                    }
                }
            }, SPLASH_TIME_OUT);
        }
    }