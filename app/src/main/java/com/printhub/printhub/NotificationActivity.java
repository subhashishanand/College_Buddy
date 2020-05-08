package com.printhub.printhub;

import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import com.airbnb.lottie.LottieAnimationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import butterknife.ButterKnife;

public class NotificationActivity extends AppCompatActivity {

    //Array list for storing all the notifications
    //private ArrayList<NotificationPojo> listofnotif;
    private ListView listView;
    private LottieAnimationView emptycart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        ButterKnife.bind(this);

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        listView = findViewById(R.id.listView);
        emptycart = findViewById(R.id.empty_notification);
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void markAsRead(View view) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }
}
