package com.printhub.printhub.globalEvents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.R;
import com.printhub.printhub.clubEvents.EventsClass;
import com.printhub.printhub.clubEvents.clubActivity;
import com.printhub.printhub.clubEvents.mEventsAdapter;
import com.printhub.printhub.clubEvents.postEvent;

import java.util.ArrayList;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;

public class GlobalActivity extends AppCompatActivity {


    private LinearLayoutManager manager;
    Boolean isScrolling = false;
    int totalItems, scrolledOutItems;
    private FirebaseFirestore firebaseFirestore;
    private  GlobalEventAdapter globalEventAdapter;
    private DocumentSnapshot lastDocumentSnapshot=null;
    Query query;

    private RecyclerView mRecyclerView;
    private LottieAnimationView tv_no_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_global);
        //new code
        new CheckInternetConnection(this).checkConnection();
        mRecyclerView = findViewById(R.id.my_recycler_view);
        tv_no_item = findViewById(R.id.tv_no_cards);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
        firebaseFirestore= FirebaseFirestore.getInstance();


        manager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        globalEventAdapter= new GlobalEventAdapter(new ArrayList<>(), GlobalActivity.this, mRecyclerView);
        mRecyclerView.setAdapter(globalEventAdapter);

        loadData();
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItems = manager.getItemCount();
                scrolledOutItems = manager.findLastVisibleItemPosition();
                if (isScrolling && scrolledOutItems + 1 >= totalItems) {
                    isScrolling = false;
                    loadData();
                }
            }
        });

    }

    private void loadData(){
        if (lastDocumentSnapshot == null) {
            query = firebaseFirestore.collection("globalEvent").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        } else {
            query = firebaseFirestore.collection("globalEvent").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastDocumentSnapshot).limit(10);
        }
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    if(!documentSnapshot.exists()){

                    }
                    if(documentSnapshot!=null) {
                        lastDocumentSnapshot = documentSnapshot;
                        GlobalEventClass global = documentSnapshot.toObject(GlobalEventClass.class);
                        ((GlobalEventAdapter)mRecyclerView.getAdapter()).update(global);
                    }
                }
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
}