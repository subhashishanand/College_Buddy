package com.printhub.printhub.registration;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.printhub.printhub.CheckInternetConnection;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.printhub.printhub.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DetailActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    EditText nameText, rollNumberText, mobileNumberText, hostelNameText;
    Spinner citySpinner,collegeSpinner;
    String cityName=null,collegeName=null,userId;
    Button uploadButton;
    SharedPreferences detail = null,cityNameSharedPref,collegeNameSharedPref,userIdSharedPref;
    CircleImageView circularImage;
    private static  int GALLERY_REQUEST = 1;
    Uri imageUri = null;
    ProgressDialog progressDialog;
    private StorageReference mstorageref;
    String token;


    ArrayList cityArray = new ArrayList<String>();

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        new CheckInternetConnection(this).checkConnection();
        circularImage = findViewById(R.id.circularImage);
        rollNumberText = findViewById(R.id.rollNumberText);
        mobileNumberText = findViewById(R.id.mobileNumberText);
        nameText = findViewById(R.id.nameText);
        hostelNameText = findViewById(R.id.hostelNameText);
        citySpinner = findViewById(R.id.citySpinner);
        collegeSpinner = findViewById(R.id.collegeSpinner);
        uploadButton= findViewById(R.id.uploadButton);
        citySpinner.setOnItemSelectedListener(this);
        userId =FirebaseAuth.getInstance().getCurrentUser().getUid();
        mstorageref = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering");
        progressDialog.setCancelable(false);
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
        collegeNameSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);
        cityNameSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);
        userIdSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startadding();


            }
        });
        circularImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
                    Log.e("File Opener","File chooser is opened with crop activity");

                   CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1,1)
                            .start(DetailActivity.this);


                }else{
                    ActivityCompat.requestPermissions(DetailActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 56);



                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Result","On Activity Result is called");
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                imageUri = result.getUri();
                circularImage.setImageURI(imageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("Result","Error has occured in result");
            }
        }
        Log.e("Result","On Activity Result is completed");
    }

    private void startadding(){
        final String rollNumber =rollNumberText.getText().toString();
        final String mobileNumber = mobileNumberText.getText().toString();
        final String name = nameText.getText().toString();
        final String hostelName =  hostelNameText.getText().toString();
        if (!TextUtils.isEmpty(rollNumber) &&!TextUtils.isEmpty(mobileNumber)&&!TextUtils.isEmpty(collegeName) && !TextUtils.isEmpty(name) && !TextUtils.isEmpty(hostelName) && !TextUtils.isEmpty(cityName) && !TextUtils.isEmpty(name) ){
            if(mobileNumber.length()==10){
                progressDialog.show();
                if(null !=imageUri){
                    final StorageReference filepath = mstorageref.child("user images").child(imageUri.getLastPathSegment());
                    filepath.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    adddata(uri.toString(),rollNumber,mobileNumber,hostelName,name);

                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();


                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();

                        }
                    });
                }else{
                    String url="https://firebasestorage.googleapis.com/v0/b/myapplication-2ca64.appspot.com/o/avtarimage.jpg?alt=media&token=ab803ce5-92dd-4855-8c3c-1082128cfad1";
                    adddata(url,rollNumber,mobileNumber,hostelName,name);
                }



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


    public void adddata(String url,String rollNumber,String mobileNumber,String hostelName,String name ){
        Map<String, Object> collegeInfo = new HashMap<>();
        collegeInfo.put("cityName", cityName);
        collegeInfo.put("collegeName", collegeName);

        FirebaseMessaging.getInstance().getToken().addOnSuccessListener(new OnSuccessListener<String>() {
            @Override
            public void onSuccess(String s) {
                token=s;



        db.collection("users").document(userId).set(collegeInfo).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                user user1=new user(name ,rollNumber,mobileNumber,collegeName,hostelName,cityName,token,url);
                db.collection(cityName).document(collegeName).collection("users").document(userId).set(user1).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        progressDialog.dismiss();
                        Toasty.success(getApplicationContext(), "Successfully registered!!").show();
                        detail.edit().putBoolean("fillDetails", true).apply();
                        collegeNameSharedPref.edit().putString("collegeName",collegeName).apply();
                        cityNameSharedPref.edit().putString("cityName", cityName).apply();
                        userIdSharedPref.edit().putString("userId",userId).apply();
                        startActivity(new Intent(getApplicationContext(), MainnewActivity.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toasty.error(getApplicationContext(), "Failed!! Check connection and please try again").show();
                    }
                });
            }
        });

            }
        });

    }


    public void displayimage(){
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);

    }
}
