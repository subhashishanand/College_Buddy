package com.printhub.printhub.sidebar.oldOrders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;

public class ReturnActivity extends AppCompatActivity {

    private FirebaseFirestore db=FirebaseFirestore.getInstance();
    String uid;
    TextView productName,productDescription,price;
    ImageView productImageView;
    MultiAutoCompleteTextView reasonEditText;
    Button returnButton;
    String replaceCount="";
    RadioGroup radioGroup;
    RadioButton radioButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_return);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        productName=findViewById(R.id.productName);
        productDescription=findViewById(R.id.description);
        price=findViewById(R.id.price);
        productImageView=findViewById(R.id.productImageView);
        reasonEditText=findViewById(R.id.reasonEditText);
        returnButton=findViewById(R.id.returnButton);


        uid=getIntent().getStringExtra("uid");
        db.collection(cityName).document(collegeName).collection("productOrders").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                productName.setText(documentSnapshot.getString("productName"));
                productDescription.setText(documentSnapshot.getString("description"));
                price.setText(documentSnapshot.getString("price"));
                int data= (int)Integer.parseInt(documentSnapshot.getString("replaceCount"))+1;
                replaceCount = String.valueOf(data);
                Picasso.with(getApplicationContext()).load(documentSnapshot.getString("productImage")).placeholder(R.drawable.drawerback).into(productImageView);
                long milliseconds=documentSnapshot.getTimestamp("orderedTime").toDate().getTime();
                String dateString= DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
               // orderTime.setText(dateString);
            }
        });
        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection(cityName).document(collegeName).collection("productOrders").document(uid).update("status","returnRequested","replaceCount",replaceCount).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toasty.success(ReturnActivity.this, "Return request submitted").show();
                        Intent intent=new Intent(getApplicationContext(), OrdersActivity.class);
                        startActivity(intent);
                    }
                });
            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}