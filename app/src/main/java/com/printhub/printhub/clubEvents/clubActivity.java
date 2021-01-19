package com.printhub.printhub.clubEvents;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
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
    private List<EventsClass> blogList;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private mEventsAdapter blogRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_club);

        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore= FirebaseFirestore.getInstance();

        button=findViewById(R.id.fab);
        mEvents=findViewById(R.id.mEvents);
        blogList=new ArrayList<>();
        mEvents.setLayoutManager(new LinearLayoutManager(getApplicationContext()));


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),postEvent.class);
                startActivity(intent);
            }
        });
        Query firstQuery=firebaseFirestore.collection(cityName).document(collegeName).collection("collab").orderBy("timestamp", Query.Direction.DESCENDING);
        firstQuery.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent( QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {

                for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
                    if(doc.getType()==DocumentChange.Type.ADDED){
                        EventsClass blogPost=doc.getDocument().toObject(EventsClass.class);
                        blogList.add(blogPost);

                        blogRecyclerAdapter.notifyDataSetChanged();
                    }
                }

            }
        });
        blogRecyclerAdapter=new mEventsAdapter(blogList,this);
        mEvents.setAdapter(blogRecyclerAdapter);

    }
}