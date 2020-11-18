package com.printhub.printhub.sidebar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.mikepenz.crossfadedrawerlayout.view.CrossfadeDrawerLayout;
import com.mikepenz.materialdrawer.Drawer;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.NotificationActivity;
import com.printhub.printhub.R;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

public class Wishlist extends AppCompatActivity {

    private Drawer result;
    private CrossfadeDrawerLayout crossfadeDrawerLayout = null;

    //to get user session data
    //private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    private LottieAnimationView tv_no_item;
    private FrameLayout activitycartlist;
    private LottieAnimationView emptycart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cart");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        new CheckInternetConnection(this).checkConnection();

        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.frame_container);
        emptycart = findViewById(R.id.empty_cart);

            mRecyclerView.setHasFixedSize(true);
//        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewProfile(View view) {
        startActivity(new Intent(Wishlist.this, Profile.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

    }

    public void Notifications(View view) {

        startActivity(new Intent(Wishlist.this, NotificationActivity.class));
        finish();
    }
}
