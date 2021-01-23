package com.printhub.printhub.sidebar.oldOrders;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ReplacementActivity extends AppCompatActivity {

    private FirebaseFirestore db;
    String uid;
    TextView productName,productDescription,price;
    ImageView productImageView;
    MultiAutoCompleteTextView reasonEditText;
    Button replaceButton;
    Button returnButton;
    String replaceCount="";
    RadioGroup radioGroup;
    RadioButton radioButton;
    ProgressDialog progressDialog;
    int radioid =-1;
    LottieAnimationView tv_no_cards ;
    ScrollView scrollView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replacement);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setTitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(this);
        scrollView= findViewById(R.id.scrollbar);
        progressDialog.setMessage("Submitting Return Request");
        progressDialog.setCancelable(false);
        productName=findViewById(R.id.productName);
        productDescription=findViewById(R.id.description);
        price=findViewById(R.id.price);
        productImageView=findViewById(R.id.productImageView);
        reasonEditText=findViewById(R.id.reasonEditText);
        replaceButton=findViewById(R.id.replaceButton);
        returnButton=findViewById(R.id.returnButton);
        radioGroup = findViewById(R.id.radiogroup);
        tv_no_cards= findViewById(R.id.tv_no_cards);
        uid=getIntent().getStringExtra("uid");
        db=FirebaseFirestore.getInstance();
        db.collection(cityName).document(collegeName).collection("productOrders").document(uid).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (tv_no_cards.getVisibility() == View.VISIBLE) {
                    tv_no_cards.setVisibility(View.GONE);
                }
                scrollView.setVisibility(View.VISIBLE);
                productName.setText(documentSnapshot.getString("productName"));
                productDescription.setText(documentSnapshot.getString("description"));
                int shownprice = Integer.parseInt(documentSnapshot.getString("price"))-Integer.parseInt(documentSnapshot.getString("couponSaving"));
                price.setText("Ordered Amount:"+String.valueOf(shownprice)+"");
                int data= (int)Integer.parseInt(documentSnapshot.getString("replaceCount"))+1;
                replaceCount = String.valueOf(data);
                Picasso.with(ReplacementActivity.this).load(documentSnapshot.getString("productImage")).placeholder(R.drawable.drawerback).into(productImageView);
                long milliseconds=documentSnapshot.getTimestamp("orderedTime").toDate().getTime();
                String dateString= DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
               // orderTime.setText(dateString);
            }
        });
        replaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                db.collection(cityName).document(collegeName).collection("productOrders").document(uid).update("status","replaceRequested").addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                      submit();
                    }
                });
            }
        });
    }


    public void submit(){
        progressDialog.show();
        radioid = radioGroup.getCheckedRadioButtonId();
        if(radioid!=-1){
            radioButton=findViewById(radioid);
            String description = reasonEditText.getText().toString().trim();
            String reason  = radioButton.getText().toString();
            if(!TextUtils.isEmpty(description) && null!=reason && !TextUtils.isEmpty(reason)){
                db.collection(cityName).document(collegeName).collection("productOrders").document(uid).update("status","replaceRequested","replaceCount",replaceCount,"replaceDescription",description,"replaceReason",reason).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        progressDialog.dismiss();
                        Toasty.success(ReplacementActivity.this, "Replace request submitted.").show();
                        Intent intent=new Intent(getApplicationContext(), OrdersActivity.class);
                        startActivity(intent);
                    }
                });

            }else{
                Toasty.error(ReplacementActivity.this, "Enter all the Fields").show();
                progressDialog.dismiss();

            }

        }else{
            Toasty.error(ReplacementActivity.this, "Enter all the Fields").show();
            progressDialog.dismiss();

        }


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}