package com.printhub.printhub.sidebar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.printhub.printhub.Cart;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.registration.DetailActivity;
import com.printhub.printhub.NotificationActivity;
import com.printhub.printhub.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class Profile extends AppCompatActivity {

    private TextView name, mobileNo, detail;
    private TextView updateDetails;
    private LinearLayout addressview;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

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

        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId).get().addOnSuccessListener(this,new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.getString("name")+"\n"+documentSnapshot.getString("rollNumber"));
                mobileNo.setText(documentSnapshot.getString("mobileNumber"));
                detail.setText(documentSnapshot.getString("hostelName")+"\n"+collegeName+", "+cityName);
            }
        });

    }

    private void initialize() {

        addressview = findViewById(R.id.addressview);
        detail = findViewById(R.id.detailview);
        mobileNo=findViewById(R.id.mobileview);
        name =findViewById(R.id.nameText);
        updateDetails=findViewById(R.id.updatedetails);

        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, DetailActivity.class));
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



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewCart(View view) {
        startActivity(new Intent(Profile.this, Cart.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection

    }

    public void Notifications(View view) {
        startActivity(new Intent(Profile.this, NotificationActivity.class));
        finish();
    }
}
