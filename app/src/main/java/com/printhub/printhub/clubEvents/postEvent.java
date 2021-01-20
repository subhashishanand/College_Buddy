package com.printhub.printhub.clubEvents;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.paytm.pgsdk.easypay.actions.CustomProgressDialog;
import com.printhub.printhub.R;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import es.dmoral.toasty.Toasty;
import id.zelory.compressor.Compressor;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class postEvent extends AppCompatActivity {
    private ImageView postImage;
    private EditText postDes,clubName, chooseDateTextView, chooseTimeTextView;
    private Button postButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri=null;
    private Bitmap compressedImageFile;
    private ProgressDialog progressDialog;
    Calendar myCalendar = Calendar.getInstance();

    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private String currUserId;
    private FirebaseFirestore firebaseFirestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_event);


        storageReference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseAuth=FirebaseAuth.getInstance();
        currUserId=firebaseAuth.getCurrentUser().getUid();
        clubName=findViewById(R.id.clubName);
        postImage=findViewById(R.id.newPostImage);
        postDes=findViewById(R.id.newPostDes);
        postButton=findViewById(R.id.postEvent);
        chooseDateTextView=findViewById(R.id.choose_date);
        chooseTimeTextView=findViewById(R.id.choose_time);

        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Uploading Event...");
        progressDialog.setCancelable(false);

        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel();
            }
        };

        chooseTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(postEvent.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {

                        String time = selectedHour + ":" + selectedMinute;

                        SimpleDateFormat fmt = new SimpleDateFormat("HH:mm");
                        Date date = null;
                        try {
                            date = fmt.parse(time );
                        } catch (ParseException e) {

                            e.printStackTrace();
                        }

                        SimpleDateFormat fmtOut = new SimpleDateFormat("hh:mm aa");

                        String formattedTime=fmtOut.format(date);
                        chooseTimeTextView.setText(formattedTime);

                    }
                }, hour, minute, false);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();
            }
        });

        chooseDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(postEvent.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });





        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc=postDes.getText().toString();
                if(null!=mImageUri && !TextUtils.isEmpty(desc) && !TextUtils.isEmpty(clubName.getText()) && !TextUtils.isEmpty(chooseDateTextView.getText()) && !TextUtils.isEmpty(chooseTimeTextView.getText())){
                    progressDialog.show();
                    String random= FieldValue.serverTimestamp().toString();
                    StorageReference filepath= storageReference.child("clubEventImages").child(random+".jpg");
                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

//                            //Compressing Image File
//                            File newImageFile=new File(mImageUri.getPath());
//                            try {
//                                compressedImageFile = new Compressor(postEvent.this)
//                                        .setQuality(75)
//                                        .compressToBitmap(newImageFile);
//                            } catch (IOException e) {
//                                Log.e("Error ","The compress functionality is not working correctly");
//                            }
//
//
//                            ByteArrayOutputStream baos=new ByteArrayOutputStream();
//                            compressedImageFile.compress(Bitmap.CompressFormat.JPEG,100,baos);
//                            byte[] thumbData=baos.toByteArray();
//
//                            UploadTask uploadTask=storageReference.child("clubEventImages").child(random+".thumbjpg").putBytes(thumbData);
//
//                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//
//                                }
//                            }).addOnFailureListener(new OnFailureListener() {
//                                @Override
//                                public void onFailure(@NonNull  Exception e) {
//
//                                }
//                            });

                             filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     Map<String,Object> postMap=new HashMap<>();
                                     Uri url = uri;
                                     postMap.put("imageUrl",url.toString());
                                     postMap.put("description",desc);
                                     postMap.put("timestamp",FieldValue.serverTimestamp());
                                     postMap.put("clubName",clubName.getText().toString());
                                     postMap.put("activityDate",chooseDateTextView.getText().toString());
                                     postMap.put("activityTime", chooseTimeTextView.getText().toString());

                                     firebaseFirestore.collection(cityName).document(collegeName).collection("clubActivity").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                         @Override
                                         public void onSuccess(DocumentReference documentReference) {
                                             progressDialog.dismiss();
                                             Intent intent=new Intent(getApplicationContext(),clubActivity.class);
                                             startActivity(intent);
                                             finish();
                                         }
                                     });
                                 }
                             }).addOnFailureListener(new OnFailureListener() {
                                 @Override
                                 public void onFailure(@NonNull Exception e) {
                                     Toast.makeText(postEvent.this, "Image is Added to storage but uri cant be featched", Toast.LENGTH_LONG).show();
                                 }
                             });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(postEvent.this, "Image is not added to the storage", Toast.LENGTH_LONG).show();
                        }
                    });
                }else{
                    Toasty.error(postEvent.this, "Fill all arguments",Toasty.LENGTH_LONG).show();
                }
            }
        });

        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e("File Open","FileOpener is started");
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(2,1)
                        .start(postEvent.this);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e("Result","On Activity Result is called");
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                mImageUri = result.getUri();
                postImage.setImageURI(mImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Log.e("Result","Error has occured in result");
            }
        }
        Log.e("Result","On Activity Result is completed");

    }

    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.e("Result","On Activity Result is called");
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                mImageUri = result.getUri();
//                postImage.setImageURI(mImageUri);
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Log.e("Result","Error has occured in result");
//            }
//        }
//        Log.e("Result","On Activity Result is completed");
//    }



//    private void openFileChooser() {
//        Intent intent = new Intent();
//        intent.setType("image/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(intent, PICK_IMAGE_REQUEST);
//    }



    private void updateLabel() {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.ENGLISH);

        chooseDateTextView.setText(sdf.format(myCalendar.getTime()));
    }
}