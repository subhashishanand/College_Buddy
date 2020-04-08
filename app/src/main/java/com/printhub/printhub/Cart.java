package com.printhub.printhub;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
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

import static com.printhub.printhub.MainnewActivity.firebaseUserId;

public class Cart extends AppCompatActivity {

    //to get user session data
    //private UserSession session;
    private HashMap<String,String> user;
    private String name,email,photo,mobile;
    private RecyclerView mRecyclerView;
    private StaggeredGridLayoutManager mLayoutManager;

    //Getting reference to Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference mDatabaseReference = database.getReference();
    private LottieAnimationView tv_no_item;
    private LinearLayout activitycartlist;
    private LottieAnimationView emptycart;

    //private ArrayList<SingleProductModel> cartcollect;
    private float totalcost=0;

    ChildEventListener childListener, printChildListener;
    DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference();
    public static MyAdapter myAdapter;
    String cost;

    ValueEventListener stockCheck;

    String quantity;
    boolean stock = true;

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

        //retrieve session values and display on listviews
       // getValues();

        //SharedPreference for Cart Value
       // session = new UserSession(getApplicationContext());

        //validating session
        //session.isLoggedIn();

        mRecyclerView = findViewById(R.id.recyclerview);
        tv_no_item = findViewById(R.id.tv_no_cards);
        activitycartlist = findViewById(R.id.activity_cart_list);
        //emptycart = findViewById(R.id.empty_cart);
        //cartcollect = new ArrayList<>();

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }

        //using staggered grid pattern in recyclerview
        mLayoutManager = new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(mLayoutManager);

        childListener=databaseReference.child("cart").child(firebaseUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                //actually called for indiv items at the database reference...
                if (tv_no_item.getVisibility() == View.VISIBLE) {
                    tv_no_item.setVisibility(View.GONE);
                }
                String cartKey = dataSnapshot.getKey();
                String quantity = dataSnapshot.child("quantity").getValue().toString();
                String cost = dataSnapshot.child("Cost").getValue().toString();
                totalcost = totalcost+Float.parseFloat(cost);
                String productName = dataSnapshot.child("ProductName").getValue().toString();
                String price = dataSnapshot.child("price").getValue().toString();
                String mrp = dataSnapshot.child("mrp").getValue().toString();
                String discount = dataSnapshot.child("discount").getValue().toString();
                String productImage = dataSnapshot.child("productImage").getValue().toString();
                ((MyAdapter) mRecyclerView.getAdapter()).update(productName, cartKey, price, mrp, discount, productImage, quantity,cost,"notUse");
                //productName=pdfName   cartKey=orderKey   price=startingPageNo   mrp==endPageNo  discount=doubleSided  productImage=custom quantity=copy cost,color

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
        printChildListener=databaseReference.child("printCart").child(firebaseUserId).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {
                if (tv_no_item.getVisibility() == View.VISIBLE) {
                    tv_no_item.setVisibility(View.GONE);
                }
                String fileName = dataSnapshot.child("fileName").getValue().toString();
                String key = dataSnapshot.getKey();
                String color = dataSnapshot.child("color").getValue().toString();
                String startingPageNo = dataSnapshot.child("startPageNo").getValue().toString();
                String endingPageNo = dataSnapshot.child("endPageNo").getValue().toString();
                String doubleSided = dataSnapshot.child("doubleSided").getValue().toString();
                String custom = dataSnapshot.child("custom").getValue().toString();
                String copy = dataSnapshot.child("Copy").getValue().toString();
                String cost = dataSnapshot.child("Cost").getValue().toString();
                totalcost = totalcost+Float.parseFloat(cost);

                ((MyAdapter) mRecyclerView.getAdapter()).update(fileName,key,startingPageNo,endingPageNo,doubleSided,custom,copy,cost,color);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @androidx.annotation.Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRecyclerView.setLayoutManager(new LinearLayoutManager(Cart.this));
        myAdapter= new MyAdapter(mRecyclerView, Cart.this, new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>(),new ArrayList<String>());
        mRecyclerView.setAdapter(myAdapter);

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
        if(new CheckInternetConnection(this).isInternetConnected()){
            Toasty.normal(Cart.this, "Calculating Total Cost").show();
            stock=true;
            for(int i =0; i<myAdapter.colors.size();i++){
                if(myAdapter.colors.get(i).equals("notUse")){
                    int finalI = i;
                    stockCheck=databaseReference.child("products").child(myAdapter.keys.get(i)).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            int intStock = Integer.parseInt(dataSnapshot.child("stock").getValue().toString());
                            databaseReference.child("products").child(myAdapter.keys.get(finalI)).removeEventListener(stockCheck);
                            if(intStock<Integer.parseInt(myAdapter.quantities.get(finalI))){
                                stock = false;
                                Toasty.normal(Cart.this, finalI+" th item is out of Stock");
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
            }
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(totalcost==0){
                        Toasty.error(Cart.this, "No items selected").show();
                    }else {
                        if(stock) {
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
            new CheckInternetConnection(this).checkConnection();
        }

    }
//
//    //viewHolder for our Firebase UI
//    public static class MovieViewHolder extends RecyclerView.ViewHolder{
//
//        TextView cardname;
//        ImageView cardimage;
//        TextView cardprice;
//        TextView cardcount;
//        ImageView carddelete;
//
//        View mView;
//        public MovieViewHolder(View v) {
//            super(v);
//            mView = v;
//            cardname = v.findViewById(R.id.cart_prtitle);
//            cardimage = v.findViewById(R.id.image_cartlist);
//            cardprice = v.findViewById(R.id.cart_prprice);
//            cardcount = v.findViewById(R.id.cart_prcount);
//            carddelete = v.findViewById(R.id.deletecard);
//        }
//    }


//    private void getValues() {
//
//        //create new session object by passing application context
//        session = new UserSession(getApplicationContext());
//
//        //validating session
//        session.isLoggedIn();
//
//        //get User details if logged in
//        user = session.getUserDetails();
//
//        name = user.get(UserSession.KEY_NAME);
//        email = user.get(UserSession.KEY_EMAIL);
//        mobile = user.get(UserSession.KEY_MOBiLE);
//        photo = user.get(UserSession.KEY_PHOTO);
//    }


    @Override
    protected void onPause() {
        databaseReference.child("cart").child(firebaseUserId).removeEventListener(childListener);
        databaseReference.child("printCart").child(firebaseUserId).removeEventListener(printChildListener);
        super.onPause();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void viewProfile(View view) {
        startActivity(new Intent(Cart.this,Profile.class));
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    public void Notifications(View view) {

        startActivity(new Intent(Cart.this,NotificationActivity.class));
        finish();
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
            if(!colors.get(position).equals("notUse")){
                holder.productName.setText(productNames.get(position));
                holder.cost.setText("Cost: "+costs.get(position));
                holder.cartPrCount.setText("Copies: "+quantities.get(position));
                holder.price.setText("custom: "+productImages.get(position)+"  Color: " + colors.get(position) +"\nfrom "+ prices.get(position)+" to "+ mrps.get(position)+ "   Double sided: "+ discounts.get(position));
                holder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference();
                        deleteRef.child("printCart").child(firebaseUserId).child(keys.get(position)).removeValue();
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
                        DatabaseReference deleteRef = FirebaseDatabase.getInstance().getReference();
                        deleteRef.child("cart").child(firebaseUserId).child(keys.get(position)).removeValue();
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
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        int position = recyclerView.getChildAdapterPosition(view);
//                        if(colors.get(position).equals("notUse")) {
//                            Intent intent = new Intent(Cart.this, IndividualProduct.class);
//                            intent.putExtra("key", keys.get(position));
//                            startActivity(intent);
//                        }
//                    }
//                });

            }
        }
    }

    public  void refersh(){
        startActivity(new Intent(Cart.this, Cart.class));
        finish();
    }

}
