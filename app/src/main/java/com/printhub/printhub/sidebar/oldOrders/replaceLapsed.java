package com.printhub.printhub.sidebar.oldOrders;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.printhub.printhub.R;

public class replaceLapsed extends AppCompatActivity {
    private Button orderButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_replace_lapsed);
        orderButton=findViewById(R.id.orderBtn);

        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(),OrdersActivity.class);
                startActivity(intent);
            }
        });
    }
}