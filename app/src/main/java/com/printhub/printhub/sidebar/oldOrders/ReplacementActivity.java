package com.printhub.printhub.sidebar.oldOrders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.printhub.printhub.R;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;

public class ReplacementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    String uid;
    TextView productName,productDescription,mrp,discount,price,orderId,orderTime,quantity,orderStatus;
    ImageView productImageView;
    EditText reasonEditText;
    Button replaceButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replacement);
        productName=findViewById(R.id.productName);
        productDescription=findViewById(R.id.description);
        mrp=findViewById(R.id.mrp);
        discount=findViewById(R.id.discount);
        price=findViewById(R.id.price);
        orderId=findViewById(R.id.orderId);
        orderTime=findViewById(R.id.orderTime);
        quantity=findViewById(R.id.quantity);
        orderStatus=findViewById(R.id.orderStatus);
        productImageView=findViewById(R.id.productImageView);
        reasonEditText=findViewById(R.id.reasonEditText);
        replaceButton=findViewById(R.id.replaceButton);

        uid=getIntent().getStringExtra("uid");
        Log.e("subhu",uid);
        db=FirebaseFirestore.getInstance();
        db.collection(cityName).document(collegeName).collection("productOrders").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                productName.setText(documentSnapshot.getString("productName"));
                productDescription.setText(documentSnapshot.getString("description"));
                mrp.setText(documentSnapshot.getString("mrp"));
                discount.setText(documentSnapshot.getString("discount"));
                price.setText(documentSnapshot.getString("price"));
                Picasso.with(ReplacementActivity.this).load(documentSnapshot.getString("productImage"));
                orderId.setText(documentSnapshot.getString("orderId"));
                Timestamp timestamp=documentSnapshot.getTimestamp("orderedTime");
                Date date =new Date();
                date.setTime(timestamp.getNanoseconds());
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm aa  dd/MM/yyyy");
                orderTime.setText(simpleDateFormat.format(date));
                quantity.setText(documentSnapshot.getString("quantity"));
                orderStatus.setText(documentSnapshot.getString("status"));
            }
        });
        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection(cityName).document(collegeName).collection("productOrders").document(uid).update("status","replaceRequested").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        orderStatus.setText("Replace requested");
                        Toasty.success(ReplacementActivity.this, "Replace request submitted").show();
                    }
                });
            }
        });
    }
}