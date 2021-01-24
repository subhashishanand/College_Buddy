package com.printhub.printhub;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.sidebar.Profile;
import com.printhub.printhub.sidebar.Wishlist;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

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

public class Cart extends AppCompatActivity {


    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;


    private LottieAnimationView tv_no_item;
    // private LinearLayout activitycartlist;

    private float totalcost=0;

    public static MyAdapter myAdapter;
    boolean stock = true;
    private LottieAnimationView emptytext;
    ProgressDialog progressDialog;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cart");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        progressDialog =new ProgressDialog(this);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);

        //retrieve session values and display on listviews
        // getValues();

        //SharedPreference for Cart Value
        // session = new UserSession(getApplicationContext());

        //validating session
        //session.isLoggedIn();

        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        //activitycartlist = findViewById(R.id.activity_cart_list);
        emptytext = findViewById(R.id.emptyBox);
        //cartcollect = new ArrayList<>();

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }

        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Cart.this));
        myAdapter= new MyAdapter(mRecyclerView, Cart.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
        mRecyclerView.setAdapter(myAdapter);

        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                .collection("productCart").get().addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    emptytext.setVisibility(View.VISIBLE);
                }else{
                    for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                        if (tv_no_item.getVisibility() == View.VISIBLE) {
                            tv_no_item.setVisibility(View.GONE);
                        }
                        String cartKey =documentSnapshot.getId();
                        String quantity =documentSnapshot.getString("quantity");
                        String cost =documentSnapshot.getString("cost");
                        totalcost = totalcost + Float.parseFloat(cost);
                        db.collection(cityName).document(collegeName).collection("products").document(cartKey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                String productName = documentSnapshot.getString("productName");
                                String price = documentSnapshot.getString("price");
                                String mrp = documentSnapshot.getString("mrp");
                                String discount = documentSnapshot.getString("discount");
                                String productImage = documentSnapshot.getString("productImage");
                                ((MyAdapter) mRecyclerView.getAdapter()).update(productName, cartKey, price, mrp, discount, productImage, quantity,cost,"notUse");
                            }
                        });
                    }
                }



            }
        });
//

        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                .collection("printCart").get().addOnSuccessListener(this, new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    emptytext.setVisibility(View.VISIBLE);
                }else {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        if (tv_no_item.getVisibility() == View.VISIBLE) {
                            tv_no_item.setVisibility(View.GONE);
                        }
                        String fileName = documentSnapshot.getString("fileName");
                        String key = documentSnapshot.getId();
                        String color = documentSnapshot.getString("color");
                        String startingPageNo = documentSnapshot.getString("startPageNo");
                        String endingPageNo = documentSnapshot.getString("endPageNo");
                        String doubleSided = documentSnapshot.getString("doubleSided");
                        String custom = documentSnapshot.getString("custom");
                        String copy = documentSnapshot.getString("copy");
                        String cost = documentSnapshot.getString("cost");
                        totalcost = totalcost + Float.parseFloat(cost);
                        ((MyAdapter) mRecyclerView.getAdapter()).update(fileName, key, startingPageNo, endingPageNo, doubleSided, custom, copy, cost, color);
                    }

                }
            }
        });

//        if(session.getCartValue()>0) {
//            populateRecyclerView();
//        }else if(session.getCartValue() == 0)  {
//            tv_no_item.setVisibility(View.GONE);
//            activitycartlist.setVisibility(View.GONE);
//            emptycart.setVisibility(View.VISIBLE);
//        }
    }

//    private void populateRecyclerView() {
//
//        //Say Hello to our new FirebaseUI android Element, i.e., FirebaseRecyclerAdapter
//        final FirebaseRecyclerAdapter<SingleProductModel,MovieViewHolder> adapter = new FirebaseRecyclerAdapter<SingleProductModel, MovieViewHolder>(
//                SingleProductModel.class,
//                R.layout.cart_item_layout,
//                MovieViewHolder.class,
//                //referencing the node where we want the database to store the data from our Object
//                mDatabaseReference.child("cart").child(mobile).getRef()
//        ) {
//            @Override
//            protected void populateViewHolder(final MovieViewHolder viewHolder, final SingleProductModel model, final int position) {
//                if(tv_no_item.getVisibility()== View.VISIBLE){
//                    tv_no_item.setVisibility(View.GONE);
//                }
//                viewHolder.cardname.setText(model.getPrname());
//                viewHolder.cardprice.setText("₹ "+model.getPrprice());
//                viewHolder.cardcount.setText("Quantity : "+model.getNo_of_items());
//                Picasso.with(Cart.this).load(model.getPrimage()).into(viewHolder.cardimage);
//
//                totalcost += model.getNo_of_items()*Float.parseFloat(model.getPrprice());
//                totalproducts += model.getNo_of_items();
//                cartcollect.add(model);
//
//                viewHolder.carddelete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        Toast.makeText(Cart.this,getItem(position).getPrname(),Toast.LENGTH_SHORT).show();
//                        getRef(position).removeValue();
//                        session.decreaseCartValue();
//                        startActivity(new Intent(Cart.this,Cart.class));
//                        finish();
//                    }
//                });
//            }
//        };
//        mRecyclerView.setAdapter(adapter);
//    }

    public void checkout(View view) {
        progressDialog.show();
        if(new CheckInternetConnection(this).isInternetConnected()){
            Toasty.normal(Cart.this, "Calculating Total Cost").show();
            stock=true;
            for(int i =0; i<myAdapter.colors.size();i++){
                if(myAdapter.colors.get(i).equals("notUse")){
                    int finalI = i;
                    db.collection(cityName).document(collegeName).collection("products")
                            .document(myAdapter.keys.get(i)).get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            progressDialog.dismiss();
                            if(!documentSnapshot.getString("stock").equals("available")){
                                stock = false;
                                Toasty.error(Cart.this, "Item no "+finalI+" not present at selected amount" );
                            }
                        }
                    });
//                    stockCheck=databaseReference.child("products").child(myAdapter.keys.get(i)).addValueEventListener(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                            int intStock = Integer.parseInt(dataSnapshot.child("stock").getValue().toString());
//                            databaseReference.child("products").child(myAdapter.keys.get(finalI)).removeEventListener(stockCheck);
//                            if(intStock<Integer.parseInt(myAdapter.quantities.get(finalI))){
//                                stock = false;
//                                Toasty.normal(Cart.this, finalI+" th item is out of Stock");
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError databaseError) {
//
//                        }
//                    });
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(totalcost==0){
                        progressDialog.dismiss();
                        Toasty.error(Cart.this, "No items selected").show();
                    }else {
                        progressDialog.dismiss();
                        if(stock) {
                            progressDialog.dismiss();
                            Intent intent = new Intent(Cart.this, OrderDetails.class);
                            intent.putExtra("totalPrice", Float.toString(totalcost));
                            //intent.putExtra("cartproducts",cartcollect);
                            startActivity(intent);
                            finish();
                        }
                    }
                }
            },1000);
        }else{
            progressDialog.dismiss();
            new CheckInternetConnection(this).checkConnection();
        }

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
        startActivity(new Intent(Cart.this, Profile.class));
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {//to create a view for recycle view items
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (emptytext.getVisibility() == View.VISIBLE) {
                emptytext.setVisibility(View.GONE);
            }
            if(!colors.get(position).equals("notUse")){
                holder.productName.setText(productNames.get(position));
                holder.cost.setText("Cost: "+costs.get(position));
                holder.cartPrCount.setText("Copies: "+quantities.get(position));
                holder.price.setText("custom: "+productImages.get(position)+"  Color: " + colors.get(position) +"\nfrom "+ prices.get(position)+" to "+ mrps.get(position)+ "   Double sided: "+ discounts.get(position));
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                                .collection("printCart").document(keys.get(position)).delete();
                        refersh();
                    }
                });
            }else{
                holder.productName.setText(productNames.get(position));
                holder.price.setText("Rs. "+prices.get(position)+"  ");
                holder.mrp.setText("MRP: "+mrps.get(position));
                holder.discount.setText(discounts.get(position)+"% off");
                holder.cartPrCount.setText("Qty: "+quantities.get(position));
                holder.cost.setText("Cost: "+costs.get(position));
                Picasso.with(Cart.this).load(productImages.get(position)).into(holder.productImage);
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                                .collection("productCart").document(keys.get(position)).delete();
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
            }
        }
    }

    public  void refersh(){
        startActivity(new Intent(Cart.this, Cart.class));
        finish();
    }

}
