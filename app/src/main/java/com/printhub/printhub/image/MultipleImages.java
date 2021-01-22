package com.printhub.printhub.image;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import es.dmoral.toasty.Toasty;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.printhub.printhub.R;
import com.printhub.printhub.pdf.pdfActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class MultipleImages extends AppCompatActivity {

    private ImageSwitcher imageIs;
    private Button previousBtn,nextBtn,selectImageBtn,checkout;
    private EditText editCopies;
    private TextView totalCost;
    private Switch color,poster;
    private Spinner custom;

    private ArrayList<Uri> imageUris;
    private ArrayList<Integer> noOfCopies;
    private ArrayList<Boolean> colorPrint;
    private ArrayList<Boolean> posterPrint;
    private ArrayList<Integer> eachCost;

    private StorageReference mStorageRef;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    String customString="1 in 1 page";
    private StorageTask mUploadTask;
    private double costValue=2;


    private static final int PICK_IMAGES_CODE=0;
    int copies = 1;
    String  colorPrintEnabled = "No",posterEnabled="No";

    //position of selected image
    int position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_images);
        imageIs=findViewById(R.id.imageIs);
        previousBtn=findViewById(R.id.previousBtn);
        nextBtn=findViewById(R.id.nextBtn);
        selectImageBtn=findViewById(R.id.pickImagesBtn);
        editCopies=findViewById(R.id.editCopies);
        color=findViewById(R.id.color_photo);
        poster=findViewById(R.id.poster);
        totalCost=findViewById(R.id.totalCost);
        checkout=findViewById(R.id.checkout);
        custom=findViewById(R.id.custom);

        //init list
        imageUris=new ArrayList<>();
        noOfCopies=new ArrayList<>();
        colorPrint=new ArrayList<>();
        posterPrint=new ArrayList<>();
        eachCost=new ArrayList<>();



        mStorageRef = FirebaseStorage.getInstance().getReference();

        if(ContextCompat.checkSelfPermission(MultipleImages.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            pickImagesIntent();
        }else{
            ActivityCompat.requestPermissions(MultipleImages.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
        }

        // Setup image Switcher
        imageIs.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView imageView=new ImageView(getApplicationContext());
                return imageView;
            }
        });

        checkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadImages();
            }
        });

        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUris.size()==0){
                    Toast.makeText(MultipleImages.this, "No file selected", Toast.LENGTH_SHORT).show();
                }else {
                    if (colorPrintEnabled.equals("No")) {
                        colorPrintEnabled = "Yes";
                        colorPrint.set(position, true);
                        sumTotal(noOfCopies,colorPrint,posterPrint,totalCost);
                    } else {
                        colorPrintEnabled = "No";
                        colorPrint.set(position, false);
                        sumTotal(noOfCopies,colorPrint,posterPrint,totalCost);
                    }
                }

            }
        });

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUris.size()==0){
                    Toast.makeText(MultipleImages.this, "No file selected", Toast.LENGTH_SHORT).show();
                }else {
                    if (posterEnabled.equals("No")) {
                        posterEnabled = "Yes";
                        posterPrint.set(position, true);
                        sumTotal(noOfCopies,colorPrint,posterPrint,totalCost);
                    } else {
                        posterEnabled = "No";
                        posterPrint.set(position, false);
                        sumTotal(noOfCopies,colorPrint,posterPrint,totalCost);
                    }
                }

            }
        });

        editCopies.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!editCopies.getText().toString().equals("")) {
                    int copy = Integer.valueOf(editCopies.getText().toString().trim());
                    if (copy != 0) {

                        copies = copy;
                        noOfCopies.set(position,copies);
                    } else {
                        Toast.makeText(MultipleImages.this, "Enter correct copies", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    copies = 1;
                    Toast.makeText(MultipleImages.this, "default Copies is 1", Toast.LENGTH_SHORT).show();
                }
                sumTotal(noOfCopies,colorPrint,posterPrint,totalCost);
            }
        });

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImagesIntent();
            }
        });

        previousBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position>0){
                    position--;
                    imageIs.setImageURI(imageUris.get(position));
                    editCopies.setText(noOfCopies.get(position).toString());
                    color.setChecked(colorPrint.get(position));
                    poster.setChecked(posterPrint.get(position));
                }
                else{
                    Toast.makeText(MultipleImages.this, "No Previous Images..", Toast.LENGTH_SHORT).show();
                }
            }
        });

        nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position<imageUris.size()-1){
                    position++;
                    imageIs.setImageURI(imageUris.get(position));
                    editCopies.setText(noOfCopies.get(position).toString());
                    color.setChecked(colorPrint.get(position));
                    poster.setChecked(posterPrint.get(position));
                }
                else{
                    Toast.makeText(MultipleImages.this, "No More Images... ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void pickImagesIntent(){
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Images"),PICK_IMAGES_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            pickImagesIntent();
        }else{
            Toast.makeText(MultipleImages.this,"Please provide permission",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==PICK_IMAGES_CODE && resultCode ==RESULT_OK){
            if(data!=null) {
                Toasty.success(this, "image loaded").show();
                if (data.getClipData() != null) {
                    // Picked Multiple Images
                    int cout = data.getClipData().getItemCount(); // no of images picked;
                    for (int i = 0; i < cout; i++) {
                        //Get Image Uri at every Position
                        Uri imageUri = data.getClipData().getItemAt(i).getUri();

                        //Populating Arrays
                        imageUris.add(imageUri);// Added to array
                        noOfCopies.add(1);
                        colorPrint.add(false);
                        posterPrint.add(false);
                        eachCost.add(2);

                    }

                    //Setting first image to image switcher
                    imageIs.setImageURI(imageUris.get(0));

                    //Setting No of copies in edit text
                    editCopies.setText(noOfCopies.get(0).toString());

                    //Setting Switches
                    color.setChecked(colorPrint.get(0));
                    poster.setChecked(posterPrint.get(0));
                    sumTotal(noOfCopies,colorPrint,posterPrint,totalCost);

                    //Setting Position
                    position = 0;

                } else {
                    //picked single image
                    Uri imageUri = data.getData();
                    //Populating Arrays
                    imageUris.add(imageUri);
                    noOfCopies.add(1);
                    colorPrint.add(false);
                    posterPrint.add(false);
                    eachCost.add(2);

                    imageIs.setImageURI(imageUris.get(0));
                    editCopies.setText(noOfCopies.get(0).toString());
                    color.setChecked(colorPrint.get(0));
                    poster.setChecked(posterPrint.get(0));
                    position = 0;
                    sumTotal(noOfCopies,colorPrint,posterPrint,totalCost);
                }
            }else{
                Toasty.error(this, "no image selected").show();
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    //Total Cost Calculation
    public static void sumTotal(List<Integer> copies,List<Boolean> color,List<Boolean> poster,TextView tv) {
        double sum=0;
        for (int i = 0; i < copies.size(); i++) {
            if(color.get(i) && poster.get(i)){
                sum+=copies.get(i)*20;
            }else if(color.get(i)){
                sum+=copies.get(i)*10;
            }else if(poster.get(i)){
                sum+=copies.get(i)*12;
            }else{
                sum+=copies.get(i)*2;
            }
        }
        tv.setText(sum+"");
    }
    public static boolean isConnectionAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            return netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable();
        }
        return false;
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadImages() {
        if(imageUris.size()==0){
            Toast.makeText(MultipleImages.this, "No file selected", Toast.LENGTH_SHORT).show();
            return;
        }
        ProgressDialog progressDialog = new ProgressDialog(MultipleImages.this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setCancelable(false);
        progressDialog.setTitle("("+0+"/"+imageUris.size()+") Uploading file...");
        progressDialog.setProgress(0);
        progressDialog.show();
        for (int j = 0; j < imageUris.size(); j++) {
            if (isConnectionAvailable(this)) {
                if (imageUris.get(j) != null) {
                    final String fileName1 = UUID.randomUUID().toString();
                    final String type = "." + getFileExtension(imageUris.get(j));
                    final StorageReference fileReference = mStorageRef.child(fileName1
                            + type);

                    int finalJ = j;
                    mUploadTask = fileReference.putFile(imageUris.get(j)).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String type = "." + getFileExtension(imageUris.get(finalJ));  //type
                            Map<String, Object> imageDescription = new HashMap<>();
                            imageDescription.put("userId", firebaseUserId);
                            imageDescription.put("doubleSided", "No");
                            imageDescription.put("custom", customString);
                            if (posterPrint.get(finalJ).equals(1) && colorPrint.get(finalJ).equals(0)) {
                                imageDescription.put("color", "Color poster");
                            } else if (posterPrint.get(finalJ).equals(1) && colorPrint.get(finalJ).equals(0)) {
                                imageDescription.put("color", "B&W poster");
                            } else {
                                imageDescription.put("color", "No");
                            }
                            imageDescription.put("startPageNo", 1 + "");
                            imageDescription.put("endPageNo", 1 + "");
                            imageDescription.put("fileName", imageUris.get(finalJ).getLastPathSegment());
                            imageDescription.put("cost", eachCost.get(finalJ)+"");
                            imageDescription.put("copy", noOfCopies.get(finalJ) + "");
                            imageDescription.put("type", type);
                            imageDescription.put("retriveName", fileName1);

                            db.collection(cityName).document(collegeName).collection("users").document(firebaseUserId)
                                    .collection("printCart").document(fileName1).set(imageDescription).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    if(finalJ==(imageUris.size()-1)){
                                        progressDialog.dismiss();
                                        Toasty.success(MultipleImages.this, "File added to your cart").show();
                                    }else{
                                        progressDialog.setTitle("("+finalJ+"/"+imageUris.size()+") Uploading file...");
                                        progressDialog.setProgress(0);
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(finalJ==(imageUris.size()-1)){
                                        progressDialog.dismiss();
                                        Toasty.error(MultipleImages.this, finalJ+1+" file not uploaded", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toasty.error(MultipleImages.this, finalJ+1+" file not uploaded", Toast.LENGTH_SHORT).show();
                                        progressDialog.setTitle("("+finalJ+"/"+imageUris.size()+") Uploading file...");
                                        progressDialog.setProgress(0);
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    if(finalJ==(imageUris.size()-1)){
                                        progressDialog.dismiss();
                                        Toasty.error(MultipleImages.this, finalJ+1+" file not uploaded", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toasty.error(MultipleImages.this, finalJ+1+" file not uploaded", Toast.LENGTH_SHORT).show();
                                        progressDialog.setTitle("("+finalJ+"/"+imageUris.size()+") Uploading file...");
                                        progressDialog.setProgress(0);
                                    }
                                }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double currentProgress = (100.00 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                            progressDialog.setProgress((int) currentProgress);
                        }
                    });
                }
            } else {
                progressDialog.dismiss();
                Toast.makeText(MultipleImages.this, "Check your connection", Toast.LENGTH_SHORT).show();
                break;
            }
        }
    }
}