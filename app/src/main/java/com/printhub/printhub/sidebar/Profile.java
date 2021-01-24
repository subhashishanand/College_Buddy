package com.printhub.printhub.sidebar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.printhub.printhub.Cart;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.registration.DetailActivity;

import com.printhub.printhub.R;
import com.printhub.printhub.registration.eventInterestActivity;
import com.printhub.printhub.registration.interestActivity;
import com.printhub.printhub.sidebar.oldOrders.OrdersActivity;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class Profile extends AppCompatActivity {

    private TextView name, mobileNo, detail,rollnumber;
    private ImageView updateDetails;
    private LinearLayout addressview,eventInterest,orderSection;
    CircleImageView userImage;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        new CheckInternetConnection(this).checkConnection();

        initialize();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId).get().addOnSuccessListener(this,new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                name.setText(documentSnapshot.getString("name"));
                rollnumber.setText(documentSnapshot.getString("rollNumber"));
                mobileNo.setText(documentSnapshot.getString("mobileNumber"));
                detail.setText(documentSnapshot.getString("hostelName")+"\n"+collegeName+", "+cityName);
                Picasso.with(getApplicationContext()).load(documentSnapshot.getString("imageLink")).placeholder(R.drawable.avtarimage).into(userImage);
            }
        });

    }

    private void initialize() {

        addressview = findViewById(R.id.addressview);
        detail = findViewById(R.id.detailview);
        mobileNo=findViewById(R.id.mobileview);
        name =findViewById(R.id.nameText);
        updateDetails=findViewById(R.id.updatedetails);
        userImage = findViewById(R.id.userPic);
        rollnumber = findViewById(R.id.rollNumberTextview);
        eventInterest = findViewById(R.id.eventInterest);
        orderSection =findViewById(R.id.orderSection);

        eventInterest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, eventInterestActivity.class));
            }
        });


        updateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, DetailActivity.class));
                //finish();
            }
        });

        addressview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               startActivity(new Intent(Profile.this, interestActivity.class));
            }
        });

        orderSection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Profile.this, OrdersActivity.class));
            }
        });
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }



    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection

    }

}
