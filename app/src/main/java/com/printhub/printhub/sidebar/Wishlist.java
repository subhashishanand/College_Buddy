package com.printhub.printhub.sidebar;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.IndividualProduct;
import com.printhub.printhub.OrderDetails;
import com.printhub.printhub.R;
import com.printhub.printhub.prodcutscategory.Stationary;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class Wishlist extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;


    private LottieAnimationView tv_no_item;
    // private LinearLayout activitycartlist;
    private LottieAnimationView emptycart;

    private float totalcost=0;

    public static MyAdapter myAdapter;

    boolean stock = true;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wishlist);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Wishlist");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        //activitycartlist = findViewById(R.id.activity_cart_list);
        emptycart = findViewById(R.id.empty_cart);
        //cartcollect = new ArrayList<>();

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }

        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                .collection("Wishlist").get().addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    emptycart.setVisibility(View.VISIBLE);
                }
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    emptycart.setVisibility(View.GONE);
                    String cartKey =documentSnapshot.getId();
                    String quantity ="1";
                    String cost =documentSnapshot.getString("price");
                    totalcost = totalcost + Float.parseFloat(cost);
                    String productName = documentSnapshot.getString("productName");
                    String price = documentSnapshot.getString("price");
                    String mrp = documentSnapshot.getString("mrp");
                    String discount = documentSnapshot.getString("discount");
                    String productImage = documentSnapshot.getString("productImage");
                    ((MyAdapter) mRecyclerView.getAdapter()).update(productName, cartKey, price, mrp, discount, productImage, quantity,cost,"notUse");
                }
            }
        });
//



        mRecyclerView.setLayoutManager(new LinearLayoutManager(Wishlist.this));
        myAdapter= new MyAdapter(mRecyclerView, Wishlist.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
        mRecyclerView.setAdapter(myAdapter);


    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewProfile(View view) {
        startActivity(new Intent(Wishlist.this, Profile.class));
        finish();
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
        ArrayList<String> quantities= new ArrayList<>();
        ArrayList<String> costs= new ArrayList<>();
        ArrayList<String> colors= new ArrayList<>();






        public void update(String productName, String key, String price, String mrp, String discount, String productImage, String quantity,String cost,String color){
            productNames.add(productName);
            keys.add(key);
            prices.add(price);
            mrps.add(mrp);
            discounts.add(discount);
            productImages.add(productImage);
            quantities.add(quantity);
            costs.add(cost);
            colors.add(color);
            notifyDataSetChanged();  //refershes the recyler view automatically...
        }


        public MyAdapter(RecyclerView recyclerView, Context context, ArrayList<String> productNames, ArrayList<String> keys, ArrayList<String> prices, ArrayList<String> mrps, ArrayList<String> discounts,ArrayList<String> productImages,ArrayList<String> quantities,ArrayList<String> costs,ArrayList<String> colors) {
            this.recyclerView = recyclerView;
            this.context = context;
            this.productNames = productNames;
            this.keys = keys;
            this.prices = prices;
            this.mrps = mrps;
            this.discounts = discounts;
            this.productImages = productImages;
            this.quantities = quantities;
            this.costs = costs;
            this.colors = colors;
        }

        @NonNull
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//to create a view for recycle view items
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new MyAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyAdapter.ViewHolder holder, int position) {
            if(colors.get(position).equals("notUse")){
                holder.productName.setText(productNames.get(position));
                holder.price.setText("Rs. "+prices.get(position)+"  ");
                holder.mrp.setText("MRP: "+mrps.get(position));
                holder.discount.setText(discounts.get(position)+"% off");
                holder.cartPrCount.setText("Qty: "+quantities.get(position));
                holder.cost.setText("Cost: "+costs.get(position));
                Picasso.with(Wishlist.this).load(productImages.get(position)).into(holder.productImage);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                                .collection("Wishlist").document(keys.get(position)).delete();
                        refersh();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {//return the no of items
            return productNames.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder{

            TextView productName, price, mrp, discount,cartPrCount,cost ;
            ImageView productImage,delete;
            public ViewHolder(@NonNull final View itemView) {
                super(itemView);
                productName=itemView.findViewById(R.id.productname);
                price=itemView.findViewById(R.id.price);
                mrp= itemView.findViewById(R.id.mrp);
                mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                discount= itemView.findViewById(R.id.discount);
                productImage = itemView.findViewById(R.id.productimage);
                cartPrCount = itemView.findViewById(R.id.cart_prcount);
                cost= itemView.findViewById(R.id.cost);
                delete = itemView.findViewById(R.id.delete);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int position = recyclerView.getChildAdapterPosition(view);
                        Intent intent = new Intent(Wishlist.this, IndividualProduct.class);
                        intent.putExtra("key", keys.get(position));
                        startActivity(intent);
                    }
                });
            }
        }
    }

    public  void refersh(){
        startActivity(new Intent(Wishlist.this, Wishlist.class));
        finish();
    }

}