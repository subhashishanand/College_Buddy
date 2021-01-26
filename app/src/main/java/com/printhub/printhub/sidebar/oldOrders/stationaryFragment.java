package com.printhub.printhub.sidebar.oldOrders;


import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Switch;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;


public class stationaryFragment extends Fragment {

    private RecyclerView mRecyclerView;
    private LottieAnimationView tv_no_item;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    int totalItems, scrolledOutItems;
    private LinearLayoutManager manager;
    Query query;
    private DocumentSnapshot lastDocumentSnapshot;
    Boolean isScrolling = false;
    LottieAnimationView noData;
    FrameLayout frameLayout;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_stationary, container, false);
        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        tv_no_item = v.findViewById(R.id.tv_no_cards);
        noData =  v.findViewById(R.id.no_data);
        frameLayout =v.findViewById(R.id.frame_container);
        //check Internet Connection
        new CheckInternetConnection(getContext()).checkConnection();

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }

        LoadData();
        manager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(manager);
        OrderAdapter myAdapter= new OrderAdapter(mRecyclerView, getContext(),new ArrayList<String>(),new ArrayList<String>() , new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<Date>(),new ArrayList<String>(),new ArrayList<String>());
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
        return v;
    }

    private void LoadData() {
        if(lastDocumentSnapshot == null){
            query = db.collection(cityName).document(collegeName).collection("productOrders").orderBy("orderedTime", Query.Direction.DESCENDING).limit(10);
        }else{
            query = db.collection(cityName).document(collegeName).collection("productOrders").orderBy("orderedTime", Query.Direction.DESCENDING).startAfter(lastDocumentSnapshot).limit(10);
        }
        query.get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(queryDocumentSnapshots.isEmpty()){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                  // noData.setVisibility(View.VISIBLE);
                }else {
                    for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        lastDocumentSnapshot = documentSnapshot;
                        if (tv_no_item.getVisibility() == View.VISIBLE) {
                            tv_no_item.setVisibility(View.GONE);
                        }
                        if (documentSnapshot.getString("userId").equals(firebaseUserId)) {
                            if (noData.getVisibility() == View.VISIBLE) {
                                noData.setVisibility(View.GONE);
                            }
                            if (frameLayout.getVisibility() == View.GONE) {
                                frameLayout.setVisibility(View.VISIBLE);
                            }

                            String productName = documentSnapshot.getString("productName");
                            String quantity = documentSnapshot.getString("quantity");
                            String status = documentSnapshot.getString("status");
                            String price = documentSnapshot.getString("price");
                            String mrp = documentSnapshot.getString("mrp");
                            String discount = documentSnapshot.getString("discount");
                            String productImage = documentSnapshot.getString("productImage");
                            String orderId = documentSnapshot.getString("orderId");
                            String couponSaving = documentSnapshot.getString("couponSaving");
                            String replaceCount = documentSnapshot.getString("replaceCount");
                            long milliseconds = documentSnapshot.getTimestamp("orderedTime").toDate().getTime();
                            Date date = documentSnapshot.getTimestamp("orderedTime").toDate();
                            String key = documentSnapshot.getId();
                            ((OrderAdapter) mRecyclerView.getAdapter()).update(productName, quantity, status, price, mrp, discount, productImage, orderId, key, date, couponSaving, replaceCount);
                        }
                        //quantity-copies,
                    }
                    if (noData.getVisibility() == View.VISIBLE) {
                        noData.setVisibility(View.GONE);
                    }

                }
            }
        });
    }

    private class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ViewHolder>{
        RecyclerView recyclerView;
        Context context;
        ArrayList<String> productNames=new ArrayList<>();
        ArrayList<String>  quantities= new ArrayList<>();
        ArrayList<String>  statuses= new ArrayList<>();
        ArrayList<String> prices= new ArrayList<>();
        ArrayList<String> mrps= new ArrayList<>();
        ArrayList<String> discounts= new ArrayList<>();
        ArrayList<String> productImages= new ArrayList<>();
        ArrayList<String> orderIds= new ArrayList<>();
        ArrayList<String> keys= new ArrayList<>();
        ArrayList<Date> returnDate= new ArrayList<>();
        ArrayList<String> couponSavings =new ArrayList<>();
        ArrayList<String> replaceCounts = new ArrayList<>();


        public void update(String productName, String quantity, String status, String price, String mrp, String discount, String productImage, String orderId, String key,Date date,String couponSaving,String replaceCount){
            productNames.add(productName);
            quantities.add(quantity);
            statuses.add(status);
            prices.add(price);
            mrps.add(mrp);
            discounts.add(discount);
            productImages.add(productImage);
            orderIds.add(orderId);
            keys.add(key);
            returnDate.add(date);
            couponSavings.add(couponSaving);
            replaceCounts.add(replaceCount);
            notifyDataSetChanged();  //refershes the recyler view automatically...

        }

        public OrderAdapter(RecyclerView recyclerView, Context context, ArrayList<String> productNames, ArrayList<String> quantities, ArrayList<String> statuses, ArrayList<String> prices, ArrayList<String> mrps, ArrayList<String> discounts,ArrayList<String> productImages, ArrayList<String> orderIds, ArrayList<String> keys,ArrayList<Date> returnDate,ArrayList<String> couponSavings,ArrayList<String> replaceCounts) {
            this.recyclerView = recyclerView;
            this.context = context;
            this.productNames = productNames;
            this.quantities = quantities;
            this.statuses = statuses;
            this.prices = prices;
            this.mrps = mrps;
            this.discounts = discounts;
            this.productImages = productImages;
            this.orderIds =  orderIds;
            this.keys=keys;
            this.returnDate=returnDate;
            this.couponSavings = couponSavings;
            this.replaceCounts= replaceCounts;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.order_product_item, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (noData.getVisibility() == View.VISIBLE) {
                noData.setVisibility(View.GONE);
            }
            if (frameLayout.getVisibility() == View.GONE) {
                frameLayout.setVisibility(View.VISIBLE);
            }
            holder.productName.setText(productNames.get(position));
            holder.quantity.setText("Quantity: " + quantities.get(position));
            holder.status.setText("Status: "+ statuses.get(position));
            int shownprice = Integer.parseInt(prices.get(position))-Integer.parseInt(couponSavings.get(position));
            holder.price.setText("Rs. "+shownprice+"");
            holder.mrp.setText(mrps.get(position));
            holder.discount.setText(discounts.get(position)+"% off");
            holder.orderNo.setText(orderIds.get(position));
            Picasso.with(context).load(productImages.get(position)).placeholder(R.drawable.drawerback).into(holder.productImage);
            Calendar cal = Calendar.getInstance();
            Date today=cal.getTime();
            cal.setTime(returnDate.get(position));
            cal.add(Calendar.DATE,3);
            Date returnLimit=cal.getTime();
           long milliseconds=returnDate.get(position).getTime();
            String dateString= DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
            holder.orderedDate.setText("Ordered On: "+dateString);
            int replacecount = Integer.parseInt(replaceCounts.get(position));
            if(statuses.get(position).equals("Delivered") && (returnLimit.compareTo(today)>0)){
                holder.returnOption.setVisibility(View.VISIBLE);
                holder.returnOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        PopupMenu popupMenu= new PopupMenu(context,holder.returnOption);
                        popupMenu.inflate(R.menu.order_menu);
                        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem item) {
                                switch(item.getItemId()){
                                    case R.id.item1:
                                        callReplace(keys.get(position),replacecount) ;
                                        break;
                                    case R.id.item2:
                                        callReturn(keys.get(position));
                                        break;
                                    default:
                                        break;
                                }
                                return false;
                            }
                        });
                        popupMenu.show();

                    }
                });


            }

        }

        @Override
        public int getItemCount() {
            return productNames.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView quantity, status, productName, price, mrp, discount, orderNo,returnOption,orderedDate;
            ImageView productImage;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                productName=itemView.findViewById(R.id.productname);
                quantity = itemView.findViewById(R.id.quantityTextView);
                status = itemView.findViewById(R.id.statusTextView);
                price=itemView.findViewById(R.id.price);
                mrp= itemView.findViewById(R.id.mrp);
                mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                discount= itemView.findViewById(R.id.discount);
                productImage = itemView.findViewById(R.id.productimage);
                orderNo = itemView.findViewById(R.id.orderid);
                returnOption=itemView.findViewById(R.id.returnoption);
                orderedDate = itemView.findViewById(R.id.orderDate);
            }
        }
        private void callReplace(String uid,int replaceCount){
            if(replaceCount<2){
                Intent intent=new Intent(getContext(), ReplacementActivity.class);
                intent.putExtra("uid",uid);
                startActivity(intent);
            }else{
                Intent intent=new Intent(getContext(), replaceLapsed.class);
                startActivity(intent);
            }

        }
        private void callReturn(String uid){
            Intent intent =new Intent(getContext(), ReturnActivity.class);
            intent.putExtra("uid",uid);
            startActivity(intent);
        }
    }
}
