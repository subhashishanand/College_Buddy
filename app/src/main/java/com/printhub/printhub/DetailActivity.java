package com.printhub.printhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.DeadObjectException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText nameText, rollNumberText, mobileNumberText, hostelNameText;
    Spinner citySpinner,collegeSpinner;
    String cityName=null,collegeName=null,userId;
    Button uploadButton;
    SharedPreferences detail = null;

    ArrayList cityArray = new ArrayList<String>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        new CheckInternetConnection(this).checkConnection();
        rollNumberText = findViewById(R.id.rollNumberText);
        mobileNumberText = findViewById(R.id.mobileNumberText);
        nameText = findViewById(R.id.nameText);
        hostelNameText = findViewById(R.id.hostelNameText);
        citySpinner = findViewById(R.id.citySpinner);
        collegeSpinner = findViewById(R.id.collegeSpinner);
        uploadButton= findViewById(R.id.uploadButton);
        citySpinner.setOnItemSelectedListener(this);
        userId =FirebaseAuth.getInstance().getCurrentUser().getUid();
        collegeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                collegeName = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        gettingCityNames();

        detail = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);

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
        final String name = nameText.getText().toString();
        final String hostelName =  hostelNameText.getText().toString();
        if (!TextUtils.isEmpty(rollNumber) &&!TextUtils.isEmpty(mobileNumber)&&!TextUtils.isEmpty(collegeName) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(hostelName) && !TextUtils.isEmpty(cityName) ){
            if(mobileNumber.length()==10){
                Map<String, Object> collegeInfo = new HashMap<>();
                collegeInfo.put("cityName", cityName);
                collegeInfo.put("collegeName", collegeName);

                db.collection("users").document(userId).set(collegeInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        user user1=new user(name ,rollNumber,mobileNumber,collegeName,hostelName,cityName);
                        db.collection(cityName).document(collegeName).collection("users").document(userId).set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toasty.success(getApplicationContext(), "Successfully registered!!").show();
                                detail.edit().putBoolean("fillDetails", true).commit();
                                startActivity(new Intent(getApplicationContext(),MainnewActivity.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toasty.error(getApplicationContext(), "Failed!! Check connection and please try again").show();
                            }
                        });
                    }
                });
            }else{
                Toasty.error(this, "Enter correct mobile no").show();
            }

        }else{
            Toasty.error(this,"all fields are mandatory", Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        cityName = parent.getSelectedItem().toString();
        if(cityName!=null){
            db.collection("City").document(cityName).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    Map map =documentSnapshot.getData();
                    ArrayList collegeArray = new ArrayList<String>();
                    for(Object entry: map.values()){
                        collegeArray.add(entry);
                    }
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, collegeArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    collegeSpinner.setAdapter(adapter);
                }
            });
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    public void gettingCityNames(){
        db.collection("City").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for(QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots){
                    cityArray.add(documentSnapshot.getId());
                    ArrayAdapter<CharSequence> adapter = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item, cityArray);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(adapter);
                }
            }
        });
    }
}