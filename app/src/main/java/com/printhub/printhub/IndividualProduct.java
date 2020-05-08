package com.printhub.printhub;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.printhub.printhub.prodcutscategory.Stationary;
import com.squareup.picasso.Picasso;

import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.MainnewActivity.cityName;
import static com.printhub.printhub.MainnewActivity.collegeName;
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

    private int quantity = 1;
    String key;
    double price;
    //DataSnapshot itemSnapshot;
    DocumentSnapshot finalDocumentSnapshot;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference = db.collection(cityName).document(collegeName);
    TextView stockTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_product);
        ButterKnife.bind(this);

        mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar);
        stockTextView = findViewById(R.id.stockTextView);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //initialize();
        documentReference.collection("products")
                .document(getIntent().getStringExtra("key")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                key = documentSnapshot.getId();
                stockTextView.setText(documentSnapshot.getString("stock"));
                finalDocumentSnapshot = documentSnapshot;
                price = Double.parseDouble(documentSnapshot.getString("price"));
                productname.setText(documentSnapshot.getString("productName"));
                productprice.setText("Rs. "+price);
                discount.setText(documentSnapshot.getString("discount")+" %off");
                mrp.setText(documentSnapshot.getString("mrp"));
                productdesc.setText(documentSnapshot.getString("description"));
                Picasso.with(IndividualProduct.this).load(documentSnapshot.getString("productImage")).into(productimage);
            }
        });
    }


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

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
//
    public void addToCart(View view) {


        Map<String, Object> map = finalDocumentSnapshot.getData();
        map.put("quantity",quantity+"");
        map.put("cost",quantity*price+"");
        map.put("productId", key);
        map.put("userId",firebaseUserId);
        documentReference.collection("users").document(firebaseUserId)
                .collection("productCart").document(key).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toasty.success(IndividualProduct.this, "Added to cart").show();
            }
        });
}

    public void addToWishList(View view) {

        addToWishlist.playAnimation();
    }

    public void goToCart(View view) {
        Map<String, Object> map = finalDocumentSnapshot.getData();
        map.put("quantity",quantity+"");
        map.put("cost",quantity*price+"");
        map.put("productId",key);
        map.put("userId",firebaseUserId);
        documentReference.collection("users").document(firebaseUserId)
                .collection("productCart").document(key).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                startActivity(new Intent(IndividualProduct.this, Cart.class));
                finish();

            }
        });
    }
}