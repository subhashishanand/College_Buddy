package com.printhub.printhub.collab;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.printhub.printhub.R;
import com.printhub.printhub.clubEvents.clubActivity;
import com.printhub.printhub.clubEvents.postEvent;

import java.util.HashMap;
import java.util.Map;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;

public class collabPostActivity extends AppCompatActivity {
    EditText domainField,descField,mobileText,whatsappText,linkedinText,githubText;
    Button postButton;
    private FirebaseFirestore firebaseFirestore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_collab_post);
        domainField= findViewById(R.id.domainField);
        descField = findViewById(R.id.descField);
        mobileText = findViewById(R.id.mobileText);
        whatsappText = findViewById(R.id.whatsappText);
        linkedinText = findViewById(R.id.linkedinText);
        githubText =findViewById(R.id.githubText);
        postButton = findViewById(R.id.postButton);
        firebaseFirestore=FirebaseFirestore.getInstance();
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postData();
            }
        });

    }
    public void postData(){
        String domain = domainField.getText().toString();
        String description = descField.getText().toString();
        String mobileno = mobileText.getText().toString();

        String whatsAppno= whatsappText.getText().toString();
        String githubId= githubText.getText().toString();
        String linkedinId=  linkedinText.getText().toString();
        if(!TextUtils.isEmpty(domain) && !TextUtils.isEmpty(description)){
            if(!TextUtils.isEmpty(mobileno) || !TextUtils.isEmpty(whatsAppno)){
                if(mobileno.length()!=10 || whatsAppno.length()!=10){
                    Toast.makeText(this, "Please enter Correct Mobile Number", Toast.LENGTH_SHORT).show();
                }else{
                    Map<String,Object> postMap=new HashMap<>();
                    postMap.put("domain",domainField.getText().toString());
                    postMap.put("description",descField.getText().toString());
                    if(!mobileno.isEmpty()){
                        postMap.put("mobileNo",mobileno);
                    }
                    if(!whatsAppno.isEmpty()){
                        postMap.put("whatsApp",whatsAppno);
                    }
                    if(!githubId.isEmpty()){
                        postMap.put("githubId",githubId);
                    }
                    if(!linkedinId.isEmpty()){
                        postMap.put("linkedinId",linkedinId);
                    }
                    postMap.put("status","unverfied");
                    postMap.put("timestamp", FieldValue.serverTimestamp());
                    postMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    firebaseFirestore.collection(cityName).document(collegeName).collection("collab").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            Toast.makeText(collabPostActivity.this, "Your Post Is Activated within 12 hours", Toast.LENGTH_LONG).show();
//                        Intent intent=new Intent(getApplicationContext(), clubActivity.class);
//                        startActivity(intent);
                            finish();
                        }
                    });

                }


            }else{
                Toast.makeText(this, "Please enter Mobile Number or Whatsapp Number", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Please enter Doamin And Description Of Project", Toast.LENGTH_SHORT).show();
        }


    }
}