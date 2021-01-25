package com.printhub.printhub.collab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;
import com.printhub.printhub.clubEvents.EventsClass;
import com.printhub.printhub.clubEvents.clubActivity;
import com.printhub.printhub.clubEvents.mEventsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;
import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;


public class collabActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    Boolean isScrolling = false;
    int totalItems, scrolledOutItems;
    private LinearLayoutManager manager;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DocumentSnapshot lastDocumentSnapshot=null;
    private collabAdapter collabAdapter;
    Query query;
    private LottieAnimationView tv_no_item;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        fab = findViewById(R.id.fab);
        tv_no_item = findViewById(R.id.tv_no_cards);


        new CheckInternetConnection(this).checkConnection();
        recyclerView = findViewById(R.id.collabview);
        manager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        collabAdapter= new collabAdapter(new ArrayList<>(), collabActivity.this, recyclerView);
        recyclerView.setAdapter(collabAdapter);
        if (recyclerView != null) {
            //to enable optimization of recyclerview
            recyclerView.setHasFixedSize(true);
        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), collabPostActivity.class);
                startActivity(intent);
            }
        });
        loadData();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
                        TapTarget.forView(findViewById(R.id.fab), "Add a Collab", " Add a collab request to find your ideal project partner ")
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
                        Toasty.success(collabActivity.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
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
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("collab").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        } else {
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("collab").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastDocumentSnapshot).limit(10);
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
                    lastDocumentSnapshot = documentSnapshot;
                    collabClass cc=documentSnapshot.toObject(collabClass.class);
                    ((collabAdapter)recyclerView.getAdapter()).update(cc);
                }
            }
        });

    }

    public void viewtour(View view){
        tapview();
    }

    public void homeclick(View view){
        startActivity(new Intent(collabActivity.this,MainnewActivity.class));
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