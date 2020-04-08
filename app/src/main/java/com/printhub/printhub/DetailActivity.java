package com.printhub.printhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.printhub.printhub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText rollNumberText, mobileNumberText, collegeNameText ;
    Spinner spinner;
    String hostelName;
    Button uploadButton;
    DatabaseReference userDatabase;
    SharedPreferences detail = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        new CheckInternetConnection(this).checkConnection();
        rollNumberText = findViewById(R.id.rollNumberText);
        mobileNumberText = findViewById(R.id.mobileNumberText);
        collegeNameText = findViewById(R.id.collegeNameText);
        spinner = findViewById(R.id.hostel);
        uploadButton= findViewById(R.id.uploadButton);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.Hostel,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        detail = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);

        userDatabase = FirebaseDatabase.getInstance().getReference("User").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startadding();


            }
        });


    }

    private void startadding(){
        final String rollNumber =rollNumberText.getText().toString();
        final String mobileNumber = mobileNumberText.getText().toString();
        final String collegeName = collegeNameText.getText().toString();
        //final String hostelName =  hostelText.getText().toString();
        if (!TextUtils.isEmpty(rollNumber) &&!TextUtils.isEmpty(mobileNumber)&&!TextUtils.isEmpty(collegeName)  ){
            user user1=new user(rollNumber,mobileNumber,collegeName,hostelName);
            userDatabase.setValue(user1).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        detail.edit().putBoolean("fillDetail", true).commit();
                        startActivity(new Intent(getApplicationContext(),MainnewActivity.class));
                        Toasty.success(DetailActivity.this, "Details Saved",Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toasty.error(DetailActivity.this, "Check Internet connection and Try again",Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }else{
            Toasty.error(this,"all fields are mandatory", Toast.LENGTH_SHORT).show();
        }


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        hostelName = parent.getSelectedItem().toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}