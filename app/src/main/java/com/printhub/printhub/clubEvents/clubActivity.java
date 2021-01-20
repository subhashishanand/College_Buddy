package com.printhub.printhub.clubEvents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;


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
import com.printhub.printhub.R;

import java.util.ArrayList;
import java.util.List;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;

public class clubActivity extends AppCompatActivity {
    private FloatingActionButton button;
    private RecyclerView mEvents;
    private List<EventsClass> blogList=new ArrayList<>();
    private StorageReference storageReference;
    private LinearLayoutManager manager;
    Boolean isScrolling = false;
    int totalItems, scrolledOutItems;
    private FirebaseFirestore firebaseFirestore;
    private mEventsAdapter blogRecyclerAdapter;
    private DocumentSnapshot lastDocumentSnapshot=null;
    Query query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore= FirebaseFirestore.getInstance();

        button=findViewById(R.id.fab);
        mEvents=findViewById(R.id.mEvents);
        manager=new LinearLayoutManager(getApplicationContext());
        mEvents.setLayoutManager(manager);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),postEvent.class);
                startActivity(intent);
            }
        });
        loadData();
        mEvents.addOnScrollListener(new RecyclerView.OnScrollListener() {
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
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("clubActivity").orderBy("timestamp", Query.Direction.DESCENDING).limit(10);
        } else {
            query = firebaseFirestore.collection(cityName).document(collegeName).collection("clubActivity").orderBy("timestamp", Query.Direction.DESCENDING).startAfter(lastDocumentSnapshot).limit(10);
        }
        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    lastDocumentSnapshot = documentSnapshot;
                    EventsClass blogPost=documentSnapshot.toObject(EventsClass.class);
                    blogList.add(blogPost);
                    blogRecyclerAdapter.notifyDataSetChanged();
                }
            }
        });
        blogRecyclerAdapter=new mEventsAdapter(blogList,this);
        mEvents.setAdapter(blogRecyclerAdapter);
    }
}