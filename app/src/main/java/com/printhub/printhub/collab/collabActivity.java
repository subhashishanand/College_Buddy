package com.printhub.printhub.collab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsListView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.R;
import com.printhub.printhub.clubEvents.EventsClass;
import com.printhub.printhub.clubEvents.mEventsAdapter;

import java.util.ArrayList;
import java.util.List;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;


public class collabActivity extends AppCompatActivity {
    FloatingActionButton fab;
    RecyclerView recyclerView;
    private List<collabClass> collabList=new ArrayList<>();
    Boolean isScrolling = false;
    int totalItems, scrolledOutItems;
    private LinearLayoutManager manager;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private DocumentSnapshot lastDocumentSnapshot=null;
    private collabAdapter collabAdapter;
    Query query;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab);
        fab = findViewById(R.id.fab);
        recyclerView = findViewById(R.id.collabview);
        manager=new LinearLayoutManager(getApplicationContext());
       recyclerView.setLayoutManager(manager);
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

    private void loadData(){
        if (lastDocumentSnapshot == null) {
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("collab").orderBy("timestamp", Query.Direction.DESCENDING).limit(5);
        } else {
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("collab").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastDocumentSnapshot).limit(5);
        }
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    lastDocumentSnapshot = documentSnapshot;
                    collabClass collabPost=documentSnapshot.toObject(collabClass.class);
                    collabList.add(collabPost);
                    collabAdapter.notifyDataSetChanged();
                }
            }
        });
        collabAdapter=new collabAdapter(collabList,this);
        recyclerView.setAdapter(collabAdapter);

    }
}