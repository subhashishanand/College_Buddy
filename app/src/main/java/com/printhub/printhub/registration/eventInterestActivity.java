package com.printhub.printhub.registration;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.R;
import com.printhub.printhub.clubEvents.EventsClass;
import com.printhub.printhub.clubEvents.mEventsAdapter;
import com.printhub.printhub.collab.collabAdapter;
import com.printhub.printhub.collab.collabClass;

import java.util.ArrayList;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;

public class eventInterestActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    Boolean isScrolling = false;
    int totalItems, scrolledOutItems;
    private LinearLayoutManager manager;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DocumentSnapshot lastDocumentSnapshot=null;
    private mEventsAdapter mEventsAdapter;
    Query query;
    private LottieAnimationView tv_no_item,emptyBox;
    TextView empty_text;
    FrameLayout frameLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_interest);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        recyclerView = findViewById(R.id.collabview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        empty_text=findViewById(R.id.empty_text);
        emptyBox= findViewById(R.id.emptyBox);
        frameLayout = findViewById(R.id.framelayout);

        manager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        mEventsAdapter= new mEventsAdapter(new ArrayList<>(), eventInterestActivity.this, recyclerView);
        recyclerView.setAdapter(mEventsAdapter);
        if (recyclerView != null) {
            //to enable optimization of recyclerview
            recyclerView.setHasFixedSize(true);
        } recyclerView = findViewById(R.id.collabview);
        manager=new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(manager);
        if (recyclerView != null) {
            //to enable optimization of recyclerview
            recyclerView.setHasFixedSize(true);
        }

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


    private void loadData(){
        if (lastDocumentSnapshot == null) {
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("eventinterest").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        }else{
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("users").document(FirebaseAuth.getInstance().getCurrentUser().getUid()).collection("eventinterest").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastDocumentSnapshot).limit(10);
        }

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
//                    if (emptyBox.getVisibility() == View.GONE) {
//                        emptyBox.setVisibility(View.VISIBLE);
//                        empty_text.setVisibility(View.VISIBLE);
//                    }
                }
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    lastDocumentSnapshot = documentSnapshot;
                    String key =documentSnapshot.getId();
                    if(null!=key && !key.isEmpty()){
                        firebaseFirestore.collection(cityName).document(collegeName).collection("clubActivity").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if(task.getResult().exists()){
                                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                                        tv_no_item.setVisibility(View.GONE);
                                    }
                                    if(frameLayout.getVisibility()==View.GONE){
                                        frameLayout.setVisibility(View.VISIBLE);
                                    }
                                    if(emptyBox.getVisibility()==View.VISIBLE){
                                        emptyBox.setVisibility(View.GONE);
                                        empty_text.setVisibility(View.GONE);
                                    }
                                    EventsClass cc=task.getResult().toObject(EventsClass.class);
                                    ((mEventsAdapter)recyclerView.getAdapter()).update(cc);
                                }


                            }
                        });

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