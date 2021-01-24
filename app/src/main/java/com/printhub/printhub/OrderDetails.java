package com.printhub.printhub;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.printhub.printhub.payment.JSONParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.BindView;
import butterknife.ButterKnife;
import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.Cart.myAdapter;
import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class OrderDetails extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    @BindView(R.id.delivery_date)
    TextView deliveryDate;
    @BindView(R.id.no_of_items)
    TextView noOfItems;
    @BindView(R.id.total_amount)
    TextView totalAmount;


    ProgressDialog progressDialog;

    String custid="", orderId="", mid="",amount;
    String status = "";
    Random random = new Random();
    Button submit;
    EditText couponText;
    String discountprice;
    int couponSaving=0;
    TextView payable_amount,discount_amount;
    LottieAnimationView checkout_loader;



    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    //This is the test commit
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);
        ButterKnife.bind(this);

        noOfItems.setText(myAdapter.productNames.size()+"");
        amount = getIntent().getStringExtra("totalPrice");
        discountprice = getIntent().getStringExtra("totalPrice");
        totalAmount.setText(amount);
        payable_amount = findViewById(R.id.payable_amount);
        discount_amount = findViewById(R.id.discount_amount);
        discount_amount.setText("0.00");
        payable_amount.setText(discountprice);
        deliveryDate.setText("Within 24 hr");

        mid = "bzwPmq36716808918329"; /// your marchant id
        custid = firebaseUserId;
        submit= findViewById(R.id.submit);
        couponText= findViewById(R.id.couponText);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                couponVerify();
            }
        });



        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

    }

    private void couponVerify(){
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Applying Coupon");
        progressDialog.setCancelable(false);
        progressDialog.show();
        String couponCode = couponText.getText().toString();
        if(null!=couponCode && !couponCode.isEmpty()){
            //couponText.setEnabled(false);
            db.collection(cityName).document(collegeName).collection("coupon").document(couponCode).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if(documentSnapshot.exists()){
                        progressDialog.dismiss();

                       submit.setText("Applied");
                      int discountper= Integer.parseInt(documentSnapshot.getString("discount"));
                        Log.e("coupon",discountprice);
                       Double actualamt= Double.parseDouble(amount);
                       int discountamt=  (int)((actualamt*discountper)/100);
                       if(discountamt>Integer.parseInt(documentSnapshot.getString("maxDiscount"))){
                           discountamt=Integer.parseInt(documentSnapshot.getString("maxDiscount"));
                       }
                       discountprice=actualamt-(double)discountamt+"";
                       discount_amount.setText(discountamt+"");
                       payable_amount.setText(discountprice);
                       couponSaving=discountamt;

                         Toasty.success(OrderDetails.this, "Your Discount Coupon is applied").show();

                    }else{
                        progressDialog.dismiss();
                        couponText.setText("");
                        couponText.setEnabled(true);
                        submit.setText("Apply");
                        discountprice = amount;
                        discount_amount.setText("0.00");
                        payable_amount.setText(discountprice);
                        couponSaving=0;
                        Toasty.error(OrderDetails.this, "Enter Valid Coupon Code").show();
                    }

                }
            });



        }else{
            progressDialog.dismiss();
            discountprice = amount;
            submit.setText("Apply");
            discount_amount.setText("0.00");
            payable_amount.setText(discountprice);
            couponSaving=0;
            Toasty.error(OrderDetails.this, "Enter Coupon Code").show();
        }
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
//
    public void PlaceOrder(View view) {
        orderId = String.format("%04d", random.nextInt(10000));
            payment();
    }


    private void parentShifting() {
        for (int i = 0; i < myAdapter.colors.size(); i++) {
            int finalI = i;
            if (myAdapter.colors.get(i).equals("notUse")) {
                db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId).collection("productCart")
                        .document(myAdapter.keys.get(i)).get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String uid= UUID.randomUUID().toString().substring(0,16);
                        Map<String, Object> map = documentSnapshot.getData();
                        String tempPrice=documentSnapshot.getString("price");
                        int tPrice=Integer.parseInt(tempPrice);
                        int productSaving= (int) ((couponSaving*tPrice)/Double.parseDouble(amount));
                        map.put("orderedTime", FieldValue.serverTimestamp());
                        map.put("orderId", orderId);
                        map.put("status", status);
                        map.put("uid",uid);
                        map.put("couponSaving",productSaving+"");
                        map.put("replaceCount",0+"");
                        db.collection(cityName).document(collegeName).collection("productOrders").document(uid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(status.equals("Order received")) {
                                    db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId).collection("productCart")
                                            .document(myAdapter.keys.get(finalI)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (myAdapter.colors.size() == (finalI + 1)) {
                                                Intent intent = new Intent(OrderDetails.this, OrderPlaced.class);
                                                intent.putExtra("orderid", orderId);
                                                progressDialog.dismiss();
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }else {
                                    if (myAdapter.colors.size() == (finalI + 1)) {
                                        Intent intent = new Intent(OrderDetails.this, OrderPendingActivity.class);
                                        intent.putExtra("orderid", orderId);
                                        progressDialog.dismiss();
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        });
                    }
                });
            } else {
                db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId).collection("printCart")
                        .document(myAdapter.keys.get(i)).get().addOnSuccessListener(this, new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        String uid= UUID.randomUUID().toString().substring(0,16);
                        Map<String, Object> map = documentSnapshot.getData();
                        map.put("orderedTime", FieldValue.serverTimestamp());
                        map.put("orderId", orderId);
                        map.put("status", status);
                        map.put("uid",uid);
                        db.collection(cityName).document(collegeName).collection("printOrders").document(uid).set(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                if(status.equals("Order received")) {
                                    db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId).collection("printCart")
                                            .document(myAdapter.keys.get(finalI)).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            if (myAdapter.colors.size() == (finalI + 1)) {
                                                Intent intent = new Intent(OrderDetails.this, OrderPlaced.class);
                                                intent.putExtra("orderid", orderId);
                                                progressDialog.dismiss();
                                                startActivity(intent);
                                                finish();
                                            }
                                        }
                                    });
                                }else {
                                    if (myAdapter.colors.size() == (finalI + 1)) {
                                        Intent intent = new Intent(OrderDetails.this, OrderPendingActivity.class);
                                        intent.putExtra("orderid", orderId);
                                        progressDialog.show();
                                        startActivity(intent);
                                        finish();
                                    }
                                }
                            }
                        });
                    }
                });
            }
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void payment(){
        sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
        dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {

        private ProgressDialog dialog = new ProgressDialog(OrderDetails.this);

        //private String orderId , mid, custid, amt;
        String url ="https://collegebuddy39.000webhostapp.com/paytm/generateChecksum.php";
        String varifyurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;
        String CHECKSUMHASH ="";

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(OrderDetails.this);
            String param=
                    "MID="+mid+
                            "&ORDER_ID=" + orderId+
                            "&CUST_ID="+custid+
                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+discountprice+"&WEBSITE=DEFAULT"+
                            "&CALLBACK_URL="+ varifyurl+"&INDUSTRY_TYPE_ID=Retail";

            JSONObject jsonObject = jsonParser.makeHttpRequest(url,"POST",param);
            // yaha per checksum ke saht order id or status receive hoga..
            Log.e("CheckSum result >>",jsonObject.toString());
            if(jsonObject != null){
                Log.e("CheckSum result >>",jsonObject.toString());
                try {

                    CHECKSUMHASH=jsonObject.has("CHECKSUMHASH")?jsonObject.getString("CHECKSUMHASH"):"";
                    Log.e("CheckSum result >>",CHECKSUMHASH);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ","  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }

            // PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            PaytmPGService Service = PaytmPGService.getProductionService();

            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custid);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", discountprice);
            paramMap.put("WEBSITE", "DEFAULT");
            paramMap.put("CALLBACK_URL" ,varifyurl);
            //paramMap.put( "EMAIL" , "abc@gmail.com");   // no need
            // paramMap.put( "MOBILE_NO" , "9144040888");  // no need
            paramMap.put("CHECKSUMHASH" ,CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");

            PaytmOrder Order = new PaytmOrder(paramMap);
            Log.e("checksum ", "param "+ paramMap.toString());
            Service.initialize(Order,null);
            // start payment service call here
            Service.startPaymentTransaction(OrderDetails.this, true, true,
                    OrderDetails.this  );
        }

    }


    @Override
    public void onTransactionResponse(Bundle bundle) {
        String sat = bundle.getString("STATUS");
        if(sat.equals("TXN_SUCCESS")){
            status = "Order received";
            progressDialog = new ProgressDialog(OrderDetails.this);
            progressDialog.setMessage("Payment processing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            parentShifting();
        }else if(sat.equals("PENDING")){
            status = "Payment pending";
            progressDialog = new ProgressDialog(OrderDetails.this);
            progressDialog.setMessage("Payment processing...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            parentShifting();
        }
        Log.e("abcdefg ", status);
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(this, "network not available", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void clientAuthenticationFailed(String s) {
        Toast.makeText(this, " ui fail respon  "+ s, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void someUIErrorOccurred(String s) {
        Log.e("checksum ", " ui fail respon  "+ s );
        Toast.makeText(this, " ui fail respon  "+ s, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onErrorLoadingWebPage(int i, String s, String s1) {
        Log.e("checksum ", " error loading pagerespon true "+ s + "  s1 " + s1);
        Toast.makeText(this, " error loading pagerespon true "+ s + "  s1 " + s1, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.e("checksum ", " cancel call back respon  " );
        Toast.makeText(this,  " cancel call back respon  ", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onTransactionCancel(String s, Bundle bundle) {
        Log.e("checksum ", "  transaction cancel " );
        Toast.makeText(this,  "  transaction cancel ", Toast.LENGTH_SHORT).show();
    }

}
