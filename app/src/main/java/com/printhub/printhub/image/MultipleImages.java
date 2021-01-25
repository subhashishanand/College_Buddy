package com.printhub.printhub.image;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import es.dmoral.toasty.Toasty;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.printhub.printhub.Cart;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;
import com.printhub.printhub.collab.collabActivity;
import com.printhub.printhub.pdf.pdfActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class MultipleImages extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageSwitcher imageIs;
    private Button previousBtn,nextBtn,selectImageBtn,checkout,getTotalCost,deleteImages;
    private EditText editCopies;
    private TextView totalCost;
    private Switch color,poster;
    private Spinner custom;
    private boolean isCheckoutOut=true;
    private ArrayList<Uri> imageUris;
    private ArrayList<Integer> noOfCopies;
    private ArrayList<Boolean> colorPrint;
    private ArrayList<Boolean> posterPrint;
    private ArrayList<Double> eachCost;

    //To Save the config
    private ArrayList<String> config;
    private ArrayList<Integer> configNo;
    String customString="1 in 1 page";
    String collegeName, cityName;
    SharedPreferences cityNameSharedPref,collegeNameSharedPref;
    String[] perPage;
    // private ArrayList<Integer> spinnerConfig;

    double colorPosterRate=20,blackPosterRate=12,colorRate=10,singleSidedPrice=2;

    private StorageReference mStorageRef;
    private FirebaseFirestore db;
    private StorageTask mUploadTask;
    double sum=0;


    private static final int PICK_IMAGES_CODE=0;
    int copies = 1;
    Boolean colorPrintEnable=false,posterEnable=false;

    //position of selected image
    int position=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiple_images);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
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
        getTotalCost=findViewById(R.id.getTotalCost);
        deleteImages=findViewById(R.id.deleteImages);


        collegeNameSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);
        cityNameSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);
        collegeName=collegeNameSharedPref.getString("collegeName","");
        cityName = cityNameSharedPref.getString("cityName", "");

        //init list
        imageUris=new ArrayList<>();
        noOfCopies=new ArrayList<>();
        colorPrint=new ArrayList<>();
        posterPrint=new ArrayList<>();
        eachCost=new ArrayList<>();
        config=new ArrayList<>();
        configNo=new ArrayList<>();
        db=FirebaseFirestore.getInstance();
        //spinnerConfig=new ArrayList<>();
        Animation in = AnimationUtils.loadAnimation(this,android.R.anim.slide_in_left);
        // Animation out = AnimationUtils.loadAnimation(this,android.R.anim.slide_out_right);
        imageIs.setInAnimation(in);
        // imageIs.setOutAnimation(out);
        //spinner populate
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.Print, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        custom.setAdapter(adapter);
        custom.setOnItemSelectedListener(MultipleImages.this);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        db.collection(cityName).document(collegeName).collection("printingPrice").document("printPrice").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                colorRate=documentSnapshot.getDouble("color");
                singleSidedPrice=documentSnapshot.getDouble("singleSided");
                colorPosterRate=documentSnapshot.getDouble("colorPoster");
                blackPosterRate=documentSnapshot.getDouble("blackPoster");
            }
        });

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
                if(isCheckoutOut) {
                    Log.e("config", config.get(0));
                    sumTotal();
                    uploadImages();
                }else{
                    Toast.makeText(getApplicationContext(),"Please Add another Item to checkOut Again",Toast.LENGTH_LONG).show();
                }
            }
        });

        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUris.size()==0){
                    Toast.makeText(MultipleImages.this, "No file selected", Toast.LENGTH_SHORT).show();
                }else {
                    if (colorPrintEnable) {
                        colorPrintEnable = false;
                        colorPrint.set(position, false);
                    } else {
                        colorPrintEnable = true;
                        colorPrint.set(position, true);              }
                }

            }
        });


        custom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                Log.e("error 205",i+" "+custom.getSelectedItem().toString());
                if(position<config.size()){
                    configNo.set(position,i);
                    config.set(position,custom.getSelectedItem().toString());
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });




        getTotalCost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sumTotal();
            }
        });

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageUris.size()==0){
                    Toast.makeText(MultipleImages.this, "No file selected", Toast.LENGTH_SHORT).show();
                }else {
                    if (posterEnable) {
                        posterEnable = false;
                        posterPrint.set(position, false);
                    } else {
                        posterEnable = true;
                        posterPrint.set(position, true);
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
            }
        });

        selectImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isCheckoutOut){
                    isCheckoutOut=true;
                }
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
                    custom.setSelection(configNo.get(position));

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
                    custom.setSelection(configNo.get(position));
                }
                else{
                    Toast.makeText(MultipleImages.this, "No More Images... ", Toast.LENGTH_SHORT).show();
                }
            }
        });

        deleteImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if(noOfCopies.size()==1){
                    imageUris.remove(position);
                    noOfCopies.remove(position);
                    eachCost.remove(position);
                    colorPrint.remove(position);
                    posterPrint.remove(position);
                    config.remove(position);
                    configNo.remove(position);
                    pickImagesIntent();


                }
                else if(position==(noOfCopies.size()-1)){
                    imageUris.remove(position);
                    noOfCopies.remove(position);
                    eachCost.remove(position);
                    colorPrint.remove(position);
                    posterPrint.remove(position);
                    config.remove(position);
                    configNo.remove(position);
                    position--;
                    imageIs.setImageURI(imageUris.get(position));
                    editCopies.setText(noOfCopies.get(position).toString());
                    color.setChecked(colorPrint.get(position));
                    poster.setChecked(posterPrint.get(position));

                }else {
                    imageUris.remove(position);
                    noOfCopies.remove(position);
                    eachCost.remove(position);
                    colorPrint.remove(position);
                    posterPrint.remove(position);
                    config.remove(position);
                    configNo.remove(position);
                    imageIs.setImageURI(imageUris.get(position));
                    editCopies.setText(noOfCopies.get(position).toString());
                    color.setChecked(colorPrint.get(position));
                    poster.setChecked(posterPrint.get(position));
                }
//                for (int i = position; i < noOfCopies.size()-1; i++) {
//
//                    noOfCopies.set(position,noOfCopies.get(position+1));
//                    if(colorPrint.get(i) && posterPrint.get(i)){
//                        eachCost.set(i,noOfCopies.get(i)*colorPosterRate);
//                        sum+=eachCost.get(i);
//                    }else if(colorPrint.get(i)){
//                        eachCost.set(i,noOfCopies.get(i)*colorRate);
//                        sum+=eachCost.get(i);
//                    }else if(posterPrint.get(i)){
//                        eachCost.set(i,noOfCopies.get(i)*blackPosterRate);
//                        sum+=eachCost.get(i);
//                    }else{
//                        eachCost.set(i,noOfCopies.get(i)*singleSidedPrice);
//                        sum+=eachCost.get(i);
//                    }
//                }
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
                        eachCost.add(singleSidedPrice);
                        config.add(customString);
                        configNo.add(0);


                    }


                    //Checking Size
                    Log.e("size", String.valueOf(config.size()));

                    //Setting first image to image switcher
                    imageIs.setImageURI(imageUris.get(0));

                    //Setting No of copies in edit text
                    editCopies.setText(noOfCopies.get(0).toString());

                    //Setting Switches
                    color.setChecked(colorPrint.get(0));
                    poster.setChecked(posterPrint.get(0));

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
                    eachCost.add(singleSidedPrice);
                    config.add(customString);
                    configNo.add(0);

                    imageIs.setImageURI(imageUris.get(0));
                    editCopies.setText(noOfCopies.get(0).toString());
                    color.setChecked(colorPrint.get(0));
                    poster.setChecked(posterPrint.get(0));
                    position = 0;
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
    }    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }


    //Total Cost Calculation
    void sumTotal() {
        sum=0;
        for (int i = 0; i < noOfCopies.size(); i++) {
            String noPages[]=config.get(i).split(" ");
            int pagesTemp=noOfCopies.get(i)/Integer.parseInt(noPages[0]);
            int rem = (noOfCopies.get(i))%Integer.parseInt(noPages[0]);
            if(rem!=0) {
                pagesTemp++;
            }
            if(colorPrint.get(i) && posterPrint.get(i)){
                eachCost.set(i,(pagesTemp*colorPosterRate));
                sum+=eachCost.get(i);
            }else if(colorPrint.get(i)){
                eachCost.set(i,pagesTemp*colorRate);
                sum+=eachCost.get(i);
            }else if(posterPrint.get(i)){
                eachCost.set(i,pagesTemp*blackPosterRate);
                sum+=eachCost.get(i);
            }else{
                eachCost.set(i,pagesTemp*singleSidedPrice);
                sum+=eachCost.get(i);
            }
        }
        totalCost.setText(sum+"");
    }


    //    @Override
//    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//        String text = custom.getSelectedItem().toString();
//        config.set(position,text);
//        Log.e("pos",config.get(position));
//
//    }
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
                            imageDescription.put("custom", config.get(finalJ));
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
                                        Intent intent = new Intent(getApplicationContext(), Cart.class);
                                        startActivity(intent);
                                        finish();
                                    }else{
                                        progressDialog.setTitle("("+finalJ+"/"+imageUris.size()+") Uploading file...");
                                        progressDialog.setProgress(0);
                                    }
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


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
    private void tapview() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.pickImagesBtn), "Add Images", "Adding more image from folder")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.colorAccent2)
                                .drawShadow(true)                   // Whether to draw a drop shadow or not
                                .cancelable(true)                  // Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.first),
                        TapTarget.forView(findViewById(R.id.getTotalCost), "Total Cost", "To get total cost click on the button")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.colorAccent2)
                                .drawShadow(true)
                                .cancelable(true)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.second),
                        TapTarget.forView(findViewById(R.id.checkout), "Checkout button", "Printout document added to your cart")
                                .targetCircleColor(R.color.colorAccent)
                                .titleTextColor(R.color.colorAccent)
                                .titleTextSize(25)
                                .descriptionTextSize(15)
                                .descriptionTextColor(R.color.colorAccent2)
                                .drawShadow(true)
                                .cancelable(true)// Whether tapping outside the outer circle dismisses the view
                                .tintTarget(true)
                                .transparentTarget(true)
                                .outerCircleColor(R.color.third))
                .listener(new TapTargetSequence.Listener() {
                    // This listener will tell us when interesting(tm) events happen in regards
                    // to the sequence
                    @Override
                    public void onSequenceFinish() {
                        Toasty.success(MultipleImages.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSequenceStep(TapTarget lastTarget, boolean targetClicked) {

                    }

                    @Override
                    public void onSequenceCanceled(TapTarget lastTarget) {
                        // Boo
                    }
                }).start();

    }

    public void viewtour(View view){
        tapview();
    }

    public void homeclick(View view){
        startActivity(new Intent(MultipleImages.this,MainnewActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

}
