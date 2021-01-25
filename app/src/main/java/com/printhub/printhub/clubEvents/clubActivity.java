package com.printhub.printhub.clubEvents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;


import com.airbnb.lottie.LottieAnimationView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.printhub.printhub.Cart;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;
import com.printhub.printhub.collab.collabActivity;

import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;

public class clubActivity extends AppCompatActivity {
    private FloatingActionButton button;
    private LinearLayoutManager manager;
    Boolean isScrolling = false;
    int totalItems, scrolledOutItems;
    private FirebaseFirestore firebaseFirestore;
    private mEventsAdapter blogRecyclerAdapter;
    private DocumentSnapshot lastDocumentSnapshot=null;
    Query query;

    private RecyclerView mRecyclerView;
    private LottieAnimationView tv_no_item;

        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            toolbar.setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        //new code
        new CheckInternetConnection(this).checkConnection();
        mRecyclerView = findViewById(R.id.my_recycler_view);
        tv_no_item = findViewById(R.id.tv_no_cards);

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }
                firebaseFirestore= FirebaseFirestore.getInstance();

        button=findViewById(R.id.fab);
        manager=new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        blogRecyclerAdapter= new mEventsAdapter(new ArrayList<>(),clubActivity.this, mRecyclerView);
        mRecyclerView.setAdapter(blogRecyclerAdapter);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),postEvent.class);
                startActivity(intent);
            }
        });
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
    private void tapview() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.fab), "Post a club event", "You club is organising a event and you want to spread the word! Well you can post it here and get going")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.colorAccent2)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.third))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        Toasty.success(clubActivity.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
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

    private void loadData(){
        if (lastDocumentSnapshot == null) {
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("clubActivity").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
        } else {
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("clubActivity").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastDocumentSnapshot).limit(5);
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
                        EventsClass blogPost = documentSnapshot.toObject(EventsClass.class);
                        ((mEventsAdapter)mRecyclerView.getAdapter()).update(blogPost);
                    }
                }
            }
        });
    }

    public void viewtour(View view){
        tapview();
    }

    public void homeclick(View view){
        startActivity(new Intent(clubActivity.this, MainnewActivity.class));
        finish();
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