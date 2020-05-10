package com.printhub.printhub.prodcutscategory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.Cart;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.IndividualProduct;
import com.printhub.printhub.NotificationActivity;
import com.printhub.printhub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import static com.printhub.printhub.MainnewActivity.cityName;
import static com.printhub.printhub.MainnewActivity.collegeName;



public class Stationary extends AppCompatActivity {


    //created for firebaseui android tutorial by Vamsi Tallapudi

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;
    private LottieAnimationView tv_no_item;
    Boolean isScrolling = false;
    int totalItems, scrolledOutItems;
    private LinearLayoutManager manager;
    Query query;

    //Getting reference to Firebase Database
   // FirebaseDatabase database = FirebaseDatabase.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentSnapshot lastDocumentSnapshot;


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

        LoadData();

        manager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(manager);
        MyAdapter myAdapter= new MyAdapter(mRecyclerView, Stationary.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>());
        mRecyclerView.setAdapter(myAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItems = manager.getItemCount();
                scrolledOutItems =manager.findLastVisibleItemPosition();
                if(isScrolling && scrolledOutItems+1>=totalItems){
                    isScrolling = false;
                    LoadData();
                }
            }
        });

    }

    private void LoadData() {
        if(lastDocumentSnapshot == null){
            query = db.collection(cityName).document(collegeName).collection("products").limit(10);
        }else{
            query = db.collection(cityName).document(collegeName).collection("products").startAfter(lastDocumentSnapshot).limit(10);
        }
        query.get().addOnSuccessListener(this,new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    lastDocumentSnapshot = documentSnapshot;
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    String productName = documentSnapshot.getString("productName");
                    String key = documentSnapshot.getId();
                    String price = documentSnapshot.getString("price");
                    String mrp = documentSnapshot.getString("mrp");
                    String discount = documentSnapshot.getString("discount");
                    String productImage = documentSnapshot.getString("productImage");
                    ((MyAdapter) mRecyclerView.getAdapter()).update(productName, key, price, mrp, discount, productImage);
                }
            }
        });
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

            TextView quantity,productName, price, mrp, discount;
            ImageView productImage;
            public ViewHolder(@NonNull final View itemView) {
                super(itemView);
                quantity = itemView.findViewById(R.id.quantityTextView);
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