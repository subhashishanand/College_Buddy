package com.printhub.printhub.prodcutscategory;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.StaggeredGridLayoutManager;
//import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
//import com.beingdev.magicprint.Cart;
//import com.beingdev.magicprint.IndividualProduct;
//import com.beingdev.magicprint.NotificationActivity;
//import com.beingdev.magicprint.R;
//import com.beingdev.magicprint.models.GenericProductModel;
//import com.beingdev.magicprint.networksync.CheckInternetConnection;
//import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.printhub.printhub.Cart;
import com.printhub.printhub.IndividualProduct;
import com.printhub.printhub.NotificationActivity;
import com.printhub.printhub.R;
import com.printhub.printhub.networksync.CheckInternetConnection;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

/**
 * Created by kshitij on 22/1/18.
 */

public class Stationary extends AppCompatActivity {


    //created for firebaseui android tutorial by Vamsi Tallapudi

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private LottieAnimationView tv_no_item;

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cards);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        //Initializing our Recyclerview
        mRecyclerView = findViewById(R.id.my_recycler_view);
        tv_no_item = findViewById(R.id.tv_no_cards);


        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child("products").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (tv_no_item.getVisibility() == View.VISIBLE) {
                    tv_no_item.setVisibility(View.GONE);
                }
                //actually called for indiv items at the database reference...
                final String productName = dataSnapshot.child("ProductName").getValue().toString();
                final String key = dataSnapshot.getKey();
                String price = dataSnapshot.child("price").getValue().toString();
                String mrp = dataSnapshot.child("mrp").getValue().toString();
                String discount = dataSnapshot.child("discount").getValue().toString();
                String productImage = dataSnapshot.child("productImage").getValue().toString();

                ((MyAdapter) mRecyclerView.getAdapter()).update(productName, key, price, mrp, discount, productImage);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Stationary.this));
        MyAdapter myAdapter= new MyAdapter(mRecyclerView, Stationary.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>());
        mRecyclerView.setAdapter(myAdapter);
//
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        MyAdapter myAdapter= new MyAdapter(recyclerView, getActivity(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>());
//        recyclerView.setAdapter(myAdapter);

//        //using staggered grid pattern in recyclerview
//        mLayoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
//        mRecyclerView.setLayoutManager(mLayoutManager);

        //Say Hello to our new FirebaseUI android Element, i.e., FirebaseRecyclerAdapter
//        final FirebaseRecyclerAdapter<GenericProductModel, Cards.MovieViewHolder> adapter = new FirebaseRecyclerAdapter<GenericProductModel, Cards.MovieViewHolder>(
//                GenericProductModel.class,
//                R.layout.cards_cardview_layout,
//                Cards.MovieViewHolder.class,
//                //referencing the node where we want the database to store the data from our Object
//                mDatabaseReference.child("Products").child("Stationary").getRef()
//        ) {
//            @Override
//            protected void populateViewHolder(final Cards.MovieViewHolder viewHolder, final GenericProductModel model, final int position) {
//                if (tv_no_item.getVisibility() == View.VISIBLE) {
//                    tv_no_item.setVisibility(View.GONE);
//                }
//                viewHolder.cardname.setText(model.getCardname());
//                viewHolder.cardprice.setText("₹ " + Float.toString(model.getCardprice()));
//                Picasso.with(Stationary.this).load(model.getCardimage()).into(viewHolder.cardimage);
//
//                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Intent intent = new Intent(Stationary.this, IndividualProduct.class);
//                        intent.putExtra("product", getItem(position));
//                        startActivity(intent);
//                    }
//                });
//            }
//        };
//
//
//        mRecyclerView.setAdapter(adapter);

    }

    public void viewCart(View view) {
        startActivity(new Intent(Stationary.this, Cart.class));
        finish();
    }


//    //viewHolder for our Firebase UI
//    public static class MovieViewHolder extends RecyclerView.ViewHolder {
//
//        TextView cardname;
//        ImageView cardimage;
//        TextView cardprice;
//
//        View mView;
//
//        public MovieViewHolder(View v) {
//            super(v);
//            mView = v;
//            cardname = v.findViewById(R.id.cardcategory);
//            cardimage = v.findViewById(R.id.cardimage);
//            cardprice = v.findViewById(R.id.cardprice);
//        }
//    }

    public void Notifications(View view) {
        startActivity(new Intent(Stationary.this, NotificationActivity.class));
        finish();
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
        new CheckInternetConnection(this).checkConnection();
    }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        RecyclerView recyclerView;
        Context context;
        ArrayList<String> productNames=new ArrayList<>();
        ArrayList<String>  keys= new ArrayList<>();
        ArrayList<String> prices= new ArrayList<>();
        ArrayList<String> mrps= new ArrayList<>();
        ArrayList<String> discounts= new ArrayList<>();
        ArrayList<String> productImages= new ArrayList<>();





        public void update(String productName, String key, String price, String mrp, String discount, String productImage){
            productNames.add(productName);
            keys.add(key);
            prices.add(price);
            mrps.add(mrp);
            discounts.add(discount);
            productImages.add(productImage);
            notifyDataSetChanged();  //refershes the recyler view automatically...
        }


        public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> productNames, ArrayList<String> keys, ArrayList<String> prices, ArrayList<String> mrps, ArrayList<String> discounts,ArrayList<String> productImages) {
            this.recyclerView = recyclerView;
            this.context = context;
            this.productNames = productNames;
            this.keys = keys;
            this.prices = prices;
            this.mrps = mrps;
            this.discounts = discounts;
            this.productImages = productImages;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//to create a view for recycle view items
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.product_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            //initialise the elements of indiv items...
            holder.productName.setText(productNames.get(position));
            holder.price.setText("Rs. "+prices.get(position));
            holder.mrp.setText(mrps.get(position));
            holder.discount.setText(discounts.get(position)+"% off");
            Picasso.with(Stationary.this).load(productImages.get(position)).into(holder.productImage);
        }

        @Override
        public int getItemCount() {//return the no of items
            return productNames.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView productName, price, mrp, discount;
            ImageView productImage;
            public ViewHolder(@NonNull final View itemView) {
                super(itemView);
                productName=itemView.findViewById(R.id.productname);
                price=itemView.findViewById(R.id.price);
                mrp= itemView.findViewById(R.id.mrp);
                mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                discount= itemView.findViewById(R.id.discount);
                productImage = itemView.findViewById(R.id.productimage);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = recyclerView.getChildAdapterPosition(view);
                        Intent intent = new Intent(Stationary.this, IndividualProduct.class);
                        intent.putExtra("key", keys.get(position));
                        startActivity(intent);
                    }
                });
            }
        }
    }
}