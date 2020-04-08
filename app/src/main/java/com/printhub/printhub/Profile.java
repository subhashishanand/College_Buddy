package com.printhub.printhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.printhub.printhub.MainnewActivity.firebaseUserId;

public class Profile extends AppCompatActivity {

    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;
    private TextView mobileNo;

    private TextView rollNo, hostelName;
    private CircleImageView primage;
    private TextView updateDetails;
    private LinearLayout addressview;


    //to get user session data
    //private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    //private SliderLayout sliderShow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initialize();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUserId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                rollNo.setText(dataSnapshot.child("rollNumber").getValue().toString());
                mobileNo.setText(dataSnapshot.child("mobileNumber").getValue().toString());
                hostelName.setText(dataSnapshot.child("hostelName").getValue().toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        //retrieve session values and display on listviews
        //getValues();

        //ImageSLider
        //inflateImageSlider();

    }

    private void initialize() {

        addressview = findViewById(R.id.addressview);
        primage=findViewById(R.id.profilepic);
        hostelName = findViewById(R.id.emailview);
        mobileNo=findViewById(R.id.mobileview);
        rollNo=findViewById(R.id.Roll_No);
        updateDetails=findViewById(R.id.updatedetails);

        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this,DetailActivity.class));
                finish();
            }
        });

        addressview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                startActivity(new Intent(Profile.this,Wishlist.class));
            }
        });
    }


//    private void getValues() {
//
//        //create new session object by passing application context
//        session = new UserSession(getApplicationContext());
//
//        //validating session
//        session.isLoggedIn();
//
//        //get User details if logged in
//        user = session.getUserDetails();
//
//        name=user.get(UserSession.KEY_NAME);
//        email=user.get(UserSession.KEY_EMAIL);
//        mobile=user.get(UserSession.KEY_MOBiLE);
//        photo=user.get(UserSession.KEY_PHOTO);
//
//        //setting values
//        tvemail.setText(email);
//        tvphone.setText(mobile);
//        namebutton.setText(name);
//
//        Picasso.with(Profile.this).load(photo).into(primage);
//
//
//    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewCart(View view) {
        startActivity(new Intent(Profile.this,Cart.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection

    }

    public void Notifications(View view) {
        startActivity(new Intent(Profile.this,NotificationActivity.class));
        finish();
    }

//    @Override
//    protected void onStop() {
//        sliderShow.stopAutoCycle();
//        super.onStop();
//
//    }
}
