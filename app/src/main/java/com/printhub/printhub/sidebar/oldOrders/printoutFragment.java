package com.printhub.printhub.sidebar.oldOrders;

import android.content.Context;
import android.graphics.Paint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.R;

import java.util.ArrayList;
import java.util.Date;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class printoutFragment extends Fragment {

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
        View v = inflater.inflate(R.layout.fragment_printout, container, false);
        mRecyclerView = v.findViewById(R.id.my_recycler_view);
        tv_no_item = v.findViewById(R.id.tv_no_cards);
        noData =  v.findViewById(R.id.no_data);
        frameLayout= v.findViewById(R.id.frame_container);
        //check Internet Connection
        new CheckInternetConnection(getContext()).checkConnection();


//
//        db.collection(cityName).document(collegeName).collection("printOrders").orderBy("orderedTime", Query.Direction.DESCENDING).get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
//            @Override
//            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                if(queryDocumentSnapshots.isEmpty()){
//                    if (tv_no_item.getVisibility() == View.VISIBLE) {
//                        tv_no_item.setVisibility(View.GONE);
//                    }
//                    noData.setVisibility(View.VISIBLE);
//                }
//                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
//                    if (tv_no_item.getVisibility() == View.VISIBLE) {
//                        tv_no_item.setVisibility(View.GONE);
//                    }
//                    if(documentSnapshot.getString("userId").equals(firebaseUserId)){
//                        String fileName = documentSnapshot.getString("fileName");
//                        String custom = documentSnapshot.getString("custom") ;
//                        String status = documentSnapshot.getString("status");
//                        String color = documentSnapshot.getString("color");
//                        String doubleSide = documentSnapshot.getString("doubleSided");
//                        String start = documentSnapshot.getString("startPageNo");
//                        String end = documentSnapshot.getString("endPageNo");
//                        String orderId = documentSnapshot.getString("orderId");
//                        String quantity = documentSnapshot.getString("copy");
//                        long milliseconds=documentSnapshot.getTimestamp("orderedTime").toDate().getTime();
//                        Date date= documentSnapshot.getTimestamp("orderedTime").toDate();
//                        ((PrintAdapter)mRecyclerView.getAdapter()).update(fileName,custom,status,color,doubleSide,start,end, orderId, quantity,date);
//                        //quantity-copies,
//
//                    }
//
//                }
//            }
//        });
        LoadData();
        manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);

        if (mRecyclerView != null) {
            //to enable optimization of recyclerview
            mRecyclerView.setHasFixedSize(true);
        }


        PrintAdapter myAdapter = new PrintAdapter(mRecyclerView, getContext(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(), new ArrayList<String>(),new ArrayList<Date>());
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
            query = db.collection(cityName).document(collegeName).collection("printOrders").orderBy("orderedTime", Query.Direction.DESCENDING).limit(10);
        }else{
            query = db.collection(cityName).document(collegeName).collection("printOrders").orderBy("orderedTime", Query.Direction.DESCENDING).startAfter(lastDocumentSnapshot).limit(10);
        }
        query.get().addOnSuccessListener(getActivity(), new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(lastDocumentSnapshot==null){
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    //noData.setVisibility(View.VISIBLE);
                }

                for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                    if (tv_no_item.getVisibility() == View.VISIBLE) {
                        tv_no_item.setVisibility(View.GONE);
                    }
                    lastDocumentSnapshot = documentSnapshot;
                    if (documentSnapshot.getString("userId").equals(firebaseUserId)) {
                        if (noData.getVisibility() == View.VISIBLE) {
                            noData.setVisibility(View.GONE);
                        }
                        if (frameLayout.getVisibility() == View.GONE) {
                            frameLayout.setVisibility(View.VISIBLE);
                        }
                        String fileName = documentSnapshot.getString("fileName");
                        String custom = documentSnapshot.getString("custom");
                        String status = documentSnapshot.getString("status");
                        String color = documentSnapshot.getString("color");
                        String doubleSide = documentSnapshot.getString("doubleSided");
                        String start = documentSnapshot.getString("startPageNo");
                        String end = documentSnapshot.getString("endPageNo");
                        String orderId = documentSnapshot.getString("orderId");
                        String quantity = documentSnapshot.getString("copy");
                        long milliseconds = documentSnapshot.getTimestamp("orderedTime").toDate().getTime();
                        Date date = documentSnapshot.getTimestamp("orderedTime").toDate();
                        ((PrintAdapter) mRecyclerView.getAdapter()).update(fileName, custom, status, color, doubleSide, start, end, orderId, quantity, date);

                    }

                    //quantity-copies,
                }

             }
        });
    }

    private class PrintAdapter extends RecyclerView.Adapter<PrintAdapter.ViewHolder> {
        RecyclerView recyclerView;
        Context context;
        ArrayList<String> fileNames=new ArrayList<>();
        ArrayList<String>  customs= new ArrayList<>();
        ArrayList<String>  statuses= new ArrayList<>();
        ArrayList<String> colors= new ArrayList<>();
        ArrayList<String> doubles= new ArrayList<>();
        ArrayList<String> starts= new ArrayList<>();
        ArrayList<String> ends= new ArrayList<>();
        ArrayList<String> orderIds= new ArrayList<>();
        ArrayList<String> quantities = new ArrayList<>();
        ArrayList<Date> returnDate= new ArrayList<>();


        public void update(String fileName, String custom, String status, String color, String doubleSide, String start, String end, String orderId, String quantity,Date date){
            fileNames.add(fileName);
            customs.add(custom);
            statuses.add(status);
            colors.add(color);
            doubles.add(doubleSide);
            starts.add(start);
            ends.add(end);
            orderIds.add(orderId);
            quantities.add(quantity);
            returnDate.add(date);
            notifyDataSetChanged();  //refershes the recyler view automatically...
        }

        public PrintAdapter(RecyclerView recyclerView, Context context, ArrayList<String> fileNames, ArrayList<String> customs, ArrayList<String> statuses, ArrayList<String> colors, ArrayList<String> doubles, ArrayList<String> starts, ArrayList<String> ends, ArrayList<String> orderIds, ArrayList<String> quantities,ArrayList<Date> returnDate) {
            this.recyclerView = recyclerView;
            this.context = context;
            this.fileNames = fileNames;
            this.customs = customs;
            this.statuses = statuses;
            this.colors = colors;
            this.doubles = doubles;
            this.starts = starts;
            this.ends = ends;
            this.orderIds =  orderIds;
            this.quantities = quantities;
            this.returnDate=returnDate;


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
            holder.fileName.setText(fileNames.get(position));
            holder.quantity.setText("Copy: " + quantities.get(position));
            holder.status.setText("Status: "+ statuses.get(position));
            holder.orderNo.setText(orderIds.get(position));
            holder.price.setText("custom: "+customs.get(position)+"  Color: " + colors.get(position) +"\nfrom "+ starts.get(position)+" to "+ ends.get(position)+ "   Double sided: "+ doubles.get(position));
            long milliseconds=returnDate.get(position).getTime();
            String dateString= DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
            holder.orderedDate.setText("Ordered On: "+dateString);
        }

        @Override
        public int getItemCount() {
            return fileNames.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView quantity, status, fileName, price, mrp, discount, orderNo,orderedDate;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                fileName=itemView.findViewById(R.id.productname);
                quantity = itemView.findViewById(R.id.quantityTextView);
                status = itemView.findViewById(R.id.statusTextView);
                price=itemView.findViewById(R.id.price);
                mrp= itemView.findViewById(R.id.mrp);
                mrp.setPaintFlags(mrp.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                discount= itemView.findViewById(R.id.discount);
                orderNo = itemView.findViewById(R.id.orderid);
                orderedDate = itemView.findViewById(R.id.orderDate);
            }
        }
    }

}
