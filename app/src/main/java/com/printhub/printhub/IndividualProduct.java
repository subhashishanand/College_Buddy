package com.printhub.printhub;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.printhub.printhub.prodcutscategory.Stationary;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.MainnewActivity.firebaseUserId;

public class IndividualProduct extends AppCompatActivity {


    @BindView(R.id.productimage)
    ImageView productimage;
    @BindView(R.id.productname)
    TextView productname;
    @BindView(R.id.productprice)
    TextView productprice;
    @BindView(R.id.add_to_cart)
    TextView addToCart;
    @BindView(R.id.buy_now)
    TextView buyNow;
    @BindView(R.id.productdesc)
    TextView productdesc;
    @BindView(R.id.quantityProductPage)
    EditText quantityProductPage;
    @BindView(R.id.add_to_wishlist)
    LottieAnimationView addToWishlist;
    @BindView(R.id.custommessage)
    EditText custommessage;
    @BindView(R.id.mrp)
    TextView mrp;
    @BindView(R.id.discount)
    TextView discount;

    private String usermobile, useremail;

    private int quantity = 1;
    //private UserSession session;
    //private GenericProductModel model;
    String key;
    int price;
    DataSnapshot itemSnapshot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);
        ButterKnife.bind(this);

        mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //initialize();
        DatabaseReference mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        mDatabaseReference.child("products").child(getIntent().getStringExtra("key")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                key = dataSnapshot.getKey();
                itemSnapshot= dataSnapshot;
                price = Integer.parseInt(dataSnapshot.child("price").getValue().toString());
                productname.setText(dataSnapshot.child("ProductName").getValue().toString());
                productprice.setText("Rs. "+price);
                mrp.setText(dataSnapshot.child("mrp").getValue().toString());
                discount.setText(dataSnapshot.child("discount").getValue()+" %off");
                productdesc.setText(dataSnapshot.child("description").getValue().toString());
                Picasso.with(IndividualProduct.this).load(dataSnapshot.child("productImage").getValue().toString()).into(productimage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

//    private void initialize() {
//        model = (GenericProductModel) getIntent().getSerializableExtra("product");
//
//        productprice.setText("₹ " + Float.toString(model.getCardprice()));
//
//        productname.setText(model.getCardname());
//        productdesc.setText(model.getCarddiscription());
//        quantityProductPage.setText("1");
//        Picasso.with(IndividualProduct.this).load(model.getCardimage()).into(productimage);
//
//        //SharedPreference for Cart Value
//        session = new UserSession(getApplicationContext());
//
//        //validating session
//        session.isLoggedIn();
//        usermobile = session.getUserDetails().get(UserSession.KEY_MOBiLE);
//        useremail = session.getUserDetails().get(UserSession.KEY_EMAIL);
//
//        //setting textwatcher for no of items field
//        quantityProductPage.addTextChangedListener(productcount);
//
//        //get firebase instance
//        //initializing database reference
//        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//    }
//
    public void Notifications(View view) {
        startActivity(new Intent(IndividualProduct.this, NotificationActivity.class));
        finish();
    }
//
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
//
    public void shareProduct(View view) {
        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        String shareBody = "Found amazing " + productname.getText().toString() + "on Print Hub App";
        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
        startActivity(Intent.createChooser(sharingIntent, "Share via"));
    }
//
    public void similarProduct(View view) {
        startActivity(new Intent(IndividualProduct.this, Stationary.class));
        finish();
    }
//
//    private SingleProductModel getProductObject() {
//
//        return new SingleProductModel(model.getCardid(), Integer.parseInt(quantityProductPage.getText().toString()), useremail, usermobile, model.getCardname(), Float.toString(model.getCardprice()), model.getCardimage(), model.carddiscription,customheader.getText().toString(),custommessage.getText().toString());
//
//    }
//
    public void decrement(View view) {
        if (quantity > 1) {
            quantity--;
            quantityProductPage.setText(String.valueOf(quantity));
        }
    }
//
    public void increment(View view) {
        if (quantity < 500) {
            quantity++;
            quantityProductPage.setText(String.valueOf(quantity));
        } else {
            Toasty.error(IndividualProduct.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
        }
    }
//
//    //check that product count must not exceed 500
//    TextWatcher productcount = new TextWatcher() {
//        @Override
//        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//            //none
//        }
////
//        @Override
//        public void onTextChanged(CharSequence s, int start, int before, int count) {
//            if (quantityProductPage.getText().toString().equals("")) {
//                quantityProductPage.setText("0");
//            }
//        }
//
//        @Override
//        public void afterTextChanged(Editable s) {
//            //none
//            if (Integer.parseInt(quantityProductPage.getText().toString()) >= 500){
//                Toasty.error(IndividualProduct.this, "Product Count Must be less than 500", Toast.LENGTH_LONG).show();
//            }
//        }
//
//    };
//
    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
//
    public void addToCart(View view) {

//        if ( customheader.getText().toString().length() == 0 ||  custommessage.getText().toString().length() ==0 ){
//
//            Snackbar.make(view, "Header or Message Empty", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//        }else{
//
//            mDatabaseReference.child("cart").child(usermobile).push().setValue(getProductObject());
//            session.increaseCartValue();
//            Log.e("Cart Value IP", session.getCartValue() + " ");
//            Toasty.success(IndividualProduct.this, "Added to Cart", Toast.LENGTH_SHORT).show();
//        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("products").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref.child("cart").child(firebaseUserId).child(key).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ref.child("cart").child(firebaseUserId).child(key).child("quantity").setValue(quantity+"");
                                ref.child("cart").child(firebaseUserId).child(key).child("Cost").setValue(quantity*price+"");
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
            Toasty.success(IndividualProduct.this, "Added to Cart").show();

}

    public void addToWishList(View view) {

        addToWishlist.playAnimation();
//        mDatabaseReference.child("wishlist").child(usermobile).push().setValue(getProductObject());
//        session.increaseWishlistValue();
    }

    public void goToCart(View view) {

//        if ( customheader.getText().toString().length() == 0 ||  custommessage.getText().toString().length() ==0 ){
//
//            Snackbar.make(view, "Header or Message Empty", Snackbar.LENGTH_LONG)
//                    .setAction("Action", null).show();
//        }else {
//            mDatabaseReference.child("cart").child(usermobile).push().setValue(getProductObject());
//            session.increaseCartValue();
//            startActivity(new Intent(IndividualProduct.this, Cart.class));
//            finish();
//        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.child("products").child(key).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref.child("cart").child(firebaseUserId).child(key).setValue(dataSnapshot.getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        task.addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                ref.child("cart").child(firebaseUserId).child(key).child("quantity").setValue(quantity+"");
                                ref.child("cart").child(firebaseUserId).child(key).child("Cost").setValue(quantity*price+"").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        startActivity(new Intent(IndividualProduct.this, Cart.class));
                                        finish();
                                    }
                                });
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}