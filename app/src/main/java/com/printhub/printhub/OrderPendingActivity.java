package com.printhub.printhub;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class OrderPendingActivity extends AppCompatActivity {

    String orderid;
    TextView orderIdVew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_pending);
        orderIdVew=findViewById(R.id.pendingOrderId);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
        initialize();

    }
    private void initialize() {
        orderid = getIntent().getStringExtra("orderid");
        orderIdVew.setText(orderid);
    }
    public void finishActivity(View view) {
        finish();
    }
}