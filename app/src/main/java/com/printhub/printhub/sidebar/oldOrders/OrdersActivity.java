package com.printhub.printhub.sidebar.oldOrders;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.R;
//import com.printhub.printhub.cabsFragment;


public class OrdersActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_orders);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Cart");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        BottomNavigationView bottomNavigationView=findViewById(R.id.nagivation);
        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        if(savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,new printoutFragment()).commit();
        }
    }
    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener= new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
            Fragment selectedFragment = null;
            switch (menuItem.getItemId()) {
                case R.id.navigation_printouts:
                    selectedFragment = new printoutFragment();
                    break;
                case R.id.navigation_stationary:
                    selectedFragment = new stationaryFragment();
                    break;
//                case R.id.navigation_cabs:
//                    selectedFragment = new cabsFragment();
//                    break;
//                case R.id.navigation_old_books:
//                    selectedFragment = new oldBooksFragment();
//                    break;
            }
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout,selectedFragment).commit();
            return true;
        }
    };

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
