package com.printhub.printhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.prodcutscategory.Stationary;
import com.squareup.picasso.Picasso;

import java.util.Map;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

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
    ImageView addToWishlist;
    @BindView(R.id.custommessage)
    EditText custommessage;
    @BindView(R.id.mrp)
    TextView mrp;
    @BindView(R.id.discount)
    TextView discount;
    LinearLayout cartLayout;
    ScrollView scrollView;

    private int quantity = 1;
    String key;
    double price;
    //DataSnapshot itemSnapshot;
    DocumentSnapshot finalDocumentSnapshot;
    private LottieAnimationView tv_no_item,cart_loader;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference documentReference = db.collection(cityName).document(collegeName);
    TextView stockTextView;
    ProgressDialog progressDialog;

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
        tv_no_item = findViewById(R.id.tv_no_cards);
        cart_loader= findViewById(R.id.cart_loader);
        cartLayout=findViewById(R.id.cartlayout);
        scrollView =findViewById(R.id.scrollbar);

        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Please Wait");
        progressDialog.setCancelable(false);



        //initialize();
        documentReference.collection("products")
                .document(getIntent().getStringExtra("key")).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (tv_no_item.getVisibility() == View.VISIBLE) {
                    tv_no_item.setVisibility(View.GONE);
                }
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
                cartLayout.setVisibility(View.VISIBLE);
                scrollView.setVisibility(View.VISIBLE);
            }
        });
//        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
//                .collection("Wishlist").document(key).addSnapshotListener(new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent( DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
//                if(documentSnapshot.exists()){
//                    addToWishlist.playAnimation();
//                }
//            }
//        });

        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                .collection("Wishlist").get().addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    if(documentSnapshot.getId().equals(getIntent().getStringExtra("key"))){
                        addToWishlist.setImageDrawable(getApplicationContext().getDrawable(R.drawable.like_red));
                    }

                }
            }
        });

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
        String shareBody = "Found this amazing " + productname.getText().toString() + " on CollegeBuddy application. Check it out " +"https://play.google.com/store/apps/details?id=com.printhub.signup";
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
        progressDialog.show();
        Map<String, Object> map = finalDocumentSnapshot.getData();
        map.put("quantity",quantity+"");
        map.put("cost",quantity*price+"");
        map.put("productId", key);
        map.put("userId",firebaseUserId);
        documentReference.collection("users").document(firebaseUserId)
                .collection("productCart").document(key).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.dismiss();
                Toasty.success(IndividualProduct.this, "Added to cart").show();
            }
        });
}

    public void addToWishList(View view) {

//        addToWishlist.playAnimation();
//        Map<String, Object> map = finalDocumentSnapshot.getData();
//        map.put("price",price+"");
//        map.put("productId", key);
//        map.put("userId",firebaseUserId);
//        documentReference.collection("users").document(firebaseUserId)
//                .collection("Wishlist").document(key).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void aVoid) {
//                Toasty.success(IndividualProduct.this, "Added to Wishlist").show();
//            }
//        });

        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                .collection("Wishlist").document(key).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(!task.getResult().exists()){
                    addToWishlist.setImageDrawable(getApplicationContext().getDrawable(R.drawable.like_red));
                    Map<String, Object> map = finalDocumentSnapshot.getData();
                    map.put("price",price+"");
                    map.put("productId", key);
                    map.put("userId",firebaseUserId);
                    documentReference.collection("users").document(firebaseUserId)
                            .collection("Wishlist").document(key).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toasty.success(IndividualProduct.this, "Added to Wishlist").show();
                        }
                    });

                }else{
                    Toasty.success(IndividualProduct.this, "Deleted from Wishlist").show();
                    addToWishlist.setImageDrawable(getApplicationContext().getDrawable(R.drawable.like_grey));
                    documentReference.collection("users").document(firebaseUserId)
                            .collection("Wishlist").document(key).delete();
                }

            }
        });


    }

    public void goToCart(View view) {
        progressDialog.show();
        Map<String, Object> map = finalDocumentSnapshot.getData();
        map.put("quantity",quantity+"");
        map.put("cost",quantity*price+"");
        map.put("productId",key);
        map.put("userId",firebaseUserId);
        documentReference.collection("users").document(firebaseUserId)
                .collection("productCart").document(key).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                progressDialog.show();
                startActivity(new Intent(IndividualProduct.this, Cart.class));
                finish();
            }
        });
    }
}