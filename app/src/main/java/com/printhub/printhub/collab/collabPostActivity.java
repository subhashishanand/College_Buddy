package com.printhub.printhub.collab;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;
import com.printhub.printhub.clubEvents.clubActivity;
import com.printhub.printhub.clubEvents.postEvent;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.bunkManager.RecyclerAdapter.getApplicationContext;

public class collabPostActivity extends AppCompatActivity {
    EditText domainField,descField,mobileText,whatsappText,linkedinText,githubText;
    Button postButton;
    private FirebaseFirestore firebaseFirestore;
    ProgressDialog progressDialog;
    String postkey;


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
        progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Uploading Post");
        progressDialog.setCancelable(false);
        postkey= getIntent().getStringExtra("postKey");
        if(null!=postkey && !postkey.isEmpty()){
            updatePost();
        }else{
            postButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String advKey = UUID.randomUUID().toString().substring(0,16);
                    postData(advKey);
                }
            });
        }


    }
    public void postData(String advKey){
        progressDialog.show();
        String domain = domainField.getText().toString();
        String description = descField.getText().toString();
        String mobileno = mobileText.getText().toString();
        boolean isBad=false;
        ArrayList<String> badWords=new ArrayList<>();
        badWords.add("sex");
        badWords.add("choot");
        badWords.add("chut");
        badWords.add("porn");
        badWords.add("boobs");
        badWords.add("dick");
        badWords.add("laude");
        badWords.add("bsdk");
        badWords.add("chod");
        badWords.add("pornhub");
        badWords.add("mia khalifa");
        badWords.add("bhosdk");
        badWords.add("fuddi");
        badWords.add("lode");
        badWords.add("bhosada");
        badWords.add("bund");
        badWords.add("tatte");
        badWords.add("goote");
        badWords.add("bitch");
        badWords.add("cunt");
        badWords.add("muth");

        for(int i=0;i<badWords.size();i++){
            if(description.contains(badWords.get(i)) || domain.contains(badWords.get(i))){
                isBad=true;
            }
        }
        String whatsAppno= whatsappText.getText().toString();
        String githubId= githubText.getText().toString();
        String linkedinId=  linkedinText.getText().toString();
        if(!TextUtils.isEmpty(domain) && !TextUtils.isEmpty(description) ){
            if(!isBad) {
                if (!TextUtils.isEmpty(mobileno) || !TextUtils.isEmpty(whatsAppno)) {
                    if (mobileno.length() == 10 || whatsAppno.length() == 10) {
                        Map<String, Object> postMap = new HashMap<>();
                        postMap.put("domain", domainField.getText().toString());
                        postMap.put("description", descField.getText().toString());
                        if (!mobileno.isEmpty()) {
                            postMap.put("mobileNo", mobileno);
                        }
                        if (!whatsAppno.isEmpty()) {
                            postMap.put("whatsApp", whatsAppno);
                        }
                        if (!githubId.isEmpty()) {
                            if (githubId.contains("github.com/")) {
                                postMap.put("githubId", githubId);
                            } else {
                                Toast.makeText(this, "Github Id you added is wrong you can edit it in post section", Toast.LENGTH_SHORT).show();
                            }
                        }
                        if (!linkedinId.isEmpty()) {
                            postMap.put("linkedinId", linkedinId);
                        }
                        postMap.put("status", "unverfied");
                        postMap.put("timestamp", FieldValue.serverTimestamp());
                        postMap.put("userid", FirebaseAuth.getInstance().getCurrentUser().getUid());
                        postMap.put("postkey", advKey);

                        firebaseFirestore.collection(cityName).document(collegeName).collection("collab").document(advKey).set(postMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                progressDialog.dismiss();
                                Toast.makeText(collabPostActivity.this, "Your Post Is Uploaded", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), collabActivity.class);
                                startActivity(intent);
                                finish();

                            }
                        });


                    } else {
                        Toast.makeText(this, "Please enter Correct Mobile Number", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }


                } else {
                    Toast.makeText(this, "Please enter Mobile Number or Whatsapp Number", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }else{
                Toast.makeText(this,"There is profanity in your description please enter the correct description",Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        }else{
            Toast.makeText(this, "Please enter Doamin And Description Of Project", Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }


    }

    private void updatePost(){
        postButton.setText("Update");
        firebaseFirestore.collection(cityName).document(collegeName).collection("collab").document(postkey).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                descField.setText(documentSnapshot.getString("description"));
                domainField.setText(documentSnapshot.getString("domain"));
                if(null!=documentSnapshot.getString("mobileNo")){
                    mobileText.setText(documentSnapshot.getString("mobileNo"));
                }
                if(null!=documentSnapshot.getString("whatsApp")){
                    whatsappText.setText(documentSnapshot.getString("whatsApp"));
                }
                if(null!=documentSnapshot.getString("githubId")){
                    githubText.setText(documentSnapshot.getString("githubId"));
                }
                if(null!=documentSnapshot.getString("linkedinId")){
                    linkedinText.setText(documentSnapshot.getString("linkedinId"));
                }

            }
        });
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postData(postkey);
            }
        });

    }

}