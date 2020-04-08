package com.printhub.printhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;
import com.whygraphics.multilineradiogroup.MultiLineRadioGroup;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.MainnewActivity.firebaseUserId;

public class OrderDetails extends AppCompatActivity {

    @BindView(R.id.delivery_date)
    TextView deliveryDate;
    @BindView(R.id.delivery_message)
    TextView deliveryMessageTextView;
    @BindView(R.id.no_of_items)
    TextView noOfItems;
    @BindView(R.id.total_amount)
    TextView totalAmount;
    @BindView(R.id.main_activity_multi_line_radio_group)
    MultiLineRadioGroup mainActivityMultiLineRadioGroup;

    //private ArrayList<SingleProductModel> cartcollect;
    private String payment_mode="UPI",order_reference_id;
    private String placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no;
    //private UserSession session;
    private HashMap<String,String> user;
    private DatabaseReference mDatabaseReference;
    private String currdatetime;
    DatabaseReference reference;

    float timer;
    ValueEventListener itemCart,printCart;
    ProgressDialog progressDialog;

    final int ActivityRequestCode= 1441;

    String finalkey, time;

    TransactionDetails transactionId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);



        noOfItems.setText(Cart.myAdapter.productNames.size()+"");
        totalAmount.setText(getIntent().getStringExtra("totalPrice"));



        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        progressDialog = new ProgressDialog(OrderDetails.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setTitle("Payment processing...");
        progressDialog.setProgress(0);
        progressDialog.setCancelable(false);

        reference=FirebaseDatabase.getInstance().getReference();
        reference.child("upi").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                float FirebaseTime = Float.parseFloat(dataSnapshot.child("eveningtime").getValue().toString());
                Date currentTime = Calendar.getInstance().getTime();
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH.mm");
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                timer= Float.parseFloat((timeFormat.format(currentTime)));
                if(timer>FirebaseTime){
                    Calendar c = Calendar.getInstance();
                    c.add(Calendar.DATE, 1);
                    Date nextDate = c.getTime();
                    deliveryDate.setText(dateFormat.format(nextDate));
                }else{
                    deliveryDate.setText(dateFormat.format(currentTime));
                }
                String deliveryMessage= dataSnapshot.child("delivery").getValue().toString();
                deliveryMessageTextView.setText(deliveryMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

//
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
//
//        //SharedPreference for Cart Value
//        //session = new UserSession(getApplicationContext());
//
//        //validating session
//        //session.isLoggedIn();
//
//        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
//
//        productdetails();

        mainActivityMultiLineRadioGroup.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(ViewGroup group, RadioButton button) {
                payment_mode=button.getText().toString();
            }
        });

    }

//    private void productdetails() {
//
//        Bundle bundle = getIntent().getExtras();
//
//        //setting total price
//        totalAmount.setText(bundle.get("totalprice").toString());
//
//        //setting number of products
//        noOfItems.setText(bundle.get("totalproducts").toString());
//
//        //cartcollect = (ArrayList<SingleProductModel>) bundle.get("cartproducts");
//
//        //delivery date
//        SimpleDateFormat formattedDate = new SimpleDateFormat("dd/MM/yyyy");
//        Calendar c = Calendar.getInstance();
//        c.add(Calendar.DATE, 7);  // number of days to add
//        String tomorrow = (formattedDate.format(c.getTime()));
//        deliveryDate.setText(tomorrow);
//
//        mainActivityMultiLineRadioGroup.setOnCheckedChangeListener(new MultiLineRadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(ViewGroup group, RadioButton button) {
//                payment_mode=button.getText().toString();
//            }
//        });
//
//        //user = session.getUserDetails();
//
////        placed_user_name=user.get(UserSession.KEY_NAME);
////        getPlaced_user_email=user.get(UserSession.KEY_EMAIL);
////        getPlaced_user_mobile_no=user.get(UserSession.KEY_MOBiLE);
//
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy-HH-mm");
//        currdatetime = sdf.format(new Date());
//    }
//
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
//
    public void PlaceOrder(View view) {

        if(payment_mode.equals("UPI")){
            parentShifting();
            //newPayment();
            //payment();
        }else{
            Toasty.normal(OrderDetails.this, "Select Another Payment Method").show();
        }
//
////        if (validateFields(view)) {
////
////            order_reference_id = getordernumber();
////
////            //adding user details to the database under orders table
////            mDatabaseReference.child("orders").child(getPlaced_user_mobile_no).child(currdatetime).push().setValue(getProductObject());
////
////            //adding products to the order
////            for(SingleProductModel model:cartcollect){
////                mDatabaseReference.child("orders").child(getPlaced_user_mobile_no).child(currdatetime).child("items").push().setValue(model);
////            }
////
////            mDatabaseReference.child("cart").child(getPlaced_user_mobile_no).removeValue();
////            session.setCartValue(0);
////
//            Intent intent = new Intent(OrderDetails.this, OrderPlaced.class);
//////            intent.putExtra("orderid",order_reference_id);
//            startActivity(intent);
//            finish();
////        }
    }

//    private  void newPayment() {
//    }

    private void payment() {
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(OrderDetails.this)
                .setPayeeVpa("7979757341@paytm")
                .setPayeeName("subhashish anand")
                .setTransactionId(String.valueOf(System.currentTimeMillis()))
                .setTransactionRefId(String.valueOf(System.currentTimeMillis()))
                .setDescription("check")
                .setAmount(String.valueOf(getIntent().getStringExtra("totalPrice")))
                .build();
        easyUpiPayment.startPayment();

        easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
            @Override
            public void onTransactionCompleted(TransactionDetails transactionDetails) {

                Log.d("TransactionDetails", transactionDetails.toString());
                transactionId = transactionDetails;
                transactionDetails.getStatus();
            }

            @Override
            public void onTransactionSuccess() {
                easyUpiPayment.detachListener();
                progressDialog.dismiss();
                Toast.makeText(OrderDetails.this, "Success", Toast.LENGTH_SHORT).show();
                // parentShifting();
                finish();
            }

            @Override
            public void onTransactionSubmitted() {

            }

            @Override
            public void onTransactionFailed() {
                easyUpiPayment.detachListener();
                Toast.makeText(OrderDetails.this, "Failed", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onTransactionCancelled() {
                easyUpiPayment.detachListener();
                Toast.makeText(OrderDetails.this, "Cancelled", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
            }

            @Override
            public void onAppNotFound() {
                easyUpiPayment.detachListener();
                Toast.makeText(OrderDetails.this, "No App Found", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void parentShifting() {
        Date newTime = Calendar.getInstance().getTime();
        SimpleDateFormat newFormat = new SimpleDateFormat("dd_MM_yyyy@HH_mm_ss");
        SimpleDateFormat saveFor = new SimpleDateFormat("HH:mm:ss dd/MM/yyyy");
        time = saveFor.format(newTime);
        String todayDate = newFormat.format(newTime);
        String key=reference.push().getKey();
        finalkey = todayDate+key;
         itemCart= reference.child("cart").child(firebaseUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    reference.child("updateOrder").child(finalkey).setValue(dataSnapshot.getValue());
                    reference.child("updateOrder").child(finalkey).child("user").setValue(firebaseUserId);
                    reference.child("updateOrder").child(finalkey).child("time").setValue(time);
                    reference.child("updateOrder").child(finalkey).child("totalCost").setValue(String.valueOf(getIntent().getStringExtra("totalPrice")));
                    reference.child("updateOrder").child(finalkey).child("status").setValue("Not delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            reference.child("cart").child(firebaseUserId).removeEventListener(itemCart);
                            reference.child("cart").child(firebaseUserId).removeValue();
                        }
                    });
                }else {
                    reference.child("cart").child(firebaseUserId).removeEventListener(itemCart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        printCart=reference.child("printCart").child(firebaseUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChildren()) {
                    reference.child("updatePrintOrder").child(finalkey).setValue(dataSnapshot.getValue());
                    reference.child("updatePrintOrder").child(finalkey).child("user").setValue(firebaseUserId);
                    reference.child("updatePrintOrder").child(finalkey).child("time").setValue(time);
                    reference.child("updatePrintOrder").child(finalkey).child("totalCost").setValue(String.valueOf(getIntent().getStringExtra("totalPrice")));
                    reference.child("updatePrintOrder").child(finalkey).child("status").setValue("Not delivered").addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            reference.child("printCart").child(firebaseUserId).removeEventListener(printCart);
                            reference.child("printCart").child(firebaseUserId).removeValue();
                        }
                    });
                }else {
                    reference.child("printCart").child(firebaseUserId).removeEventListener(printCart);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        Intent intent = new Intent(OrderDetails.this, OrderPlaced.class);
        intent.putExtra("orderid",finalkey);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    //
//    private boolean validateFields(View view) {
//
//        if (ordername.getText().toString().length() == 0 || orderemail.getText().toString().length() == 0 || ordernumber.getText().toString().length() == 0 || orderaddress.getText().toString().length() == 0 ||
//                orderpincode.getText().toString().length() == 0) {
//            Snackbar.make(view, "Kindly Fill all the fields", Snackbar.LENGTH_SHORT)
//                    .setAction("Action", null).show();
//            return false;
//        } else if (orderemail.getText().toString().length() < 4 || orderemail.getText().toString().length() > 30) {
//            orderemail.setError("Email Must consist of 4 to 30 characters");
//            return false;
//        } else if (!orderemail.getText().toString().matches("^[A-za-z0-9.@]+")) {
//            orderemail.setError("Only . and @ characters allowed");
//            return false;
//        } else if (!orderemail.getText().toString().contains("@") || !orderemail.getText().toString().contains(".")) {
//            orderemail.setError("Email must contain @ and .");
//            return false;
//        } else if (ordernumber.getText().toString().length() < 4 || ordernumber.getText().toString().length() > 12) {
//            ordernumber.setError("Number Must consist of 10 characters");
//            return false;
//        } else if (orderpincode.getText().toString().length() < 6 || ordernumber.getText().toString().length() > 8){
//            orderpincode.setError("Pincode must be of 6 digits");
//            return false;
//        }
//
//        return true;
//    }
//
////    public PlacedOrderModel getProductObject() {
////        return new PlacedOrderModel(order_reference_id,noOfItems.getText().toString(),totalAmount.getText().toString(),deliveryDate.getText().toString(),payment_mode,ordername.getText().toString(),orderemail.getText().toString(),ordernumber.getText().toString(),orderaddress.getText().toString(),orderpincode.getText().toString(),placed_user_name,getPlaced_user_email,getPlaced_user_mobile_no);
////    }
//
//    public String getordernumber() {
//
//        return currdatetime.replaceAll("-","");
//    }
}
