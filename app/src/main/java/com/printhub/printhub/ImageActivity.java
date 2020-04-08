package com.printhub.printhub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import es.dmoral.toasty.Toasty;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;
import com.squareup.picasso.Picasso;

import static com.printhub.printhub.MainnewActivity.firebaseUserId;

public class ImageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private static final int PICK_IMAGE_REQUEST = 12;
    private TextView cost;

    private double costValue=2.00;
    String[] perPage;


    private TextView mButtonChooseImage, addToCart;
    private ImageView buyNow;
    private EditText noOfPrints;
    private ImageView mImageView;
    private ProgressBar mProgressBar;
    ProgressDialog progressDialog;
    Switch color,poster;


    int copies=1;

    private Uri mImageUri;
    Spinner custom;
    String customString;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;

    String rollNo,colorPrint="No",posterPrint="No";

    private StorageTask mUploadTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
        mButtonChooseImage = findViewById(R.id.choose_image);
        addToCart = findViewById(R.id.add_to_cart);
        noOfPrints = findViewById(R.id.noOfPrint);
        buyNow = findViewById(R.id.buy_now);
        mImageView = findViewById(R.id.image_view);
        cost= findViewById(R.id.cost);
        buyNow = findViewById(R.id.buy_now);
        color = findViewById(R.id.color_photo);
        poster = findViewById(R.id.poster);
        custom = findViewById(R.id.custom);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this,R.array.Print, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        custom.setAdapter(adapter);
        custom.setOnItemSelectedListener(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("PDF prints");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();

        if(ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            openFileChooser();
        }else{
            ActivityCompat.requestPermissions(ImageActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
        }

        mButtonChooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(ImageActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
                    openFileChooser();
                }else{
                    ActivityCompat.requestPermissions(ImageActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
                }
            }
        });

        poster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(posterPrint.equals("No")){
                    posterPrint = "Yes";
                }else{
                    posterPrint = "No";
                }
                updateCost();
            }
        });

        color.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorPrint.equals("No")){
                    colorPrint = "Yes";
                }else{
                    colorPrint = "No";
                }
                updateCost();
            }
        });

        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    if (mImageUri != null) {
                        uploadFile();
                    } else {
                        openFileChooser();
                    }
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ImageActivity.this, Cart.class));
                finish();
            }
        });

        noOfPrints.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!noOfPrints.getText().toString().equals("")){
                    int copy = Integer.valueOf(noOfPrints.getText().toString().trim());
                    if(copy!=0){
                        copies = copy;
                    }else{
                        Toast.makeText(ImageActivity.this, "Enter correct copies",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    copies=1;
                    Toasty.normal(ImageActivity.this,"default Copies is 1", Toast.LENGTH_SHORT).show();
                }
                updateCost();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            openFileChooser();
        }else{
            Toast.makeText(ImageActivity.this,"Please provide permission",Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null) {
            mImageUri = data.getData();

            Picasso.with(this).load(mImageUri).into(mImageView);
            noOfPrints.setText("1");
            updateCost();
        }else if(requestCode== PICK_IMAGE_REQUEST && data==null){
            finish();
        }

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

    private void uploadFile() {
        if(isConnectionAvailable(this)) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("User").child(firebaseUserId);
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    rollNo = dataSnapshot.child("rollNumber").getValue().toString();

                    progressDialog = new ProgressDialog(ImageActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setTitle("Uploading file...");
                    progressDialog.setProgress(0);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    if (mImageUri != null) {
                        final String fileName1 = System.currentTimeMillis() + "@" + rollNo;
                        final String type = "." + getFileExtension(mImageUri);
                        final StorageReference fileReference = mStorageRef.child(fileName1
                                + type);

                        mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                        String type = "."+ getFileExtension(mImageUri);  //type
                                        //Store the url in realtime database.
                                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();        //return the path to root

                                        reference.child("printCart").child(firebaseUserId).child(fileName1).child("doubleSided").setValue("No");
                                        reference.child("printCart").child(firebaseUserId).child(fileName1).child("custom").setValue(customString);
                                        if(posterPrint.equals("Yes") && colorPrint.equals("Yes")) {
                                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("color").setValue("Color Poster");
                                        }else if (posterPrint.equals("Yes") && colorPrint.equals("No")){
                                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("color").setValue("B&W Poster");
                                        }else{
                                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("color").setValue(colorPrint);
                                        }
                                        reference.child("printCart").child(firebaseUserId).child(fileName1).child("startPageNo").setValue(1+"");
                                        reference.child("printCart").child(firebaseUserId).child(fileName1).child("endPageNo").setValue(1+"");
                                        reference.child("printCart").child(firebaseUserId).child(fileName1).child("fileName").setValue(mImageUri.getLastPathSegment());
                                        reference.child("printCart").child(firebaseUserId).child(fileName1).child("Cost").setValue(costValue+"");
                                        reference.child("printCart").child(firebaseUserId).child(fileName1).child("Copy").setValue(copies+"");
                                        reference.child("printCart").child(firebaseUserId).child(fileName1).child("type").setValue(type).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progressDialog.dismiss();
                                                if (task.isSuccessful()) {
                                                    Toasty.success(ImageActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                                    poster.setChecked(false);
                                                    posterPrint = "No";
                                                    color.setChecked(false);
                                                    colorPrint ="No";
                                                    Toasty.success(ImageActivity.this,"File added to your cart").show();
                                                } else {
                                                    Toasty.error(ImageActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();
                                                }

                                            }
                                        });
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        progressDialog.dismiss();
                                        Toasty.error(ImageActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                                        //Track the progress of our upload
                                        double currentProgress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                        progressDialog.setProgress((int) currentProgress);
                                    }
                                });
                    } else {
                        Toast.makeText(ImageActivity.this, "No file selected", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(ImageActivity.this, "Check your connection",Toast.LENGTH_SHORT).show();
        }

    }

//    private void goMainActivity(){
//        Intent intent= new Intent(ImageActivity.this, MainnewActivity.class);
//        startActivity(intent);
//        finish();
//
//    }

    private  void updateCost(){
        int page =  copies/Integer.parseInt(perPage[0]);
        int rem = copies%Integer.parseInt(perPage[0]);
        if(rem!=0) {
            page = page+1;
        }
        if(posterPrint.equals("Yes")) {
            if (colorPrint.equals("Yes")) {
                costValue = page * 10.00;
                cost.setText("Cost: " + costValue);
            } else {
                costValue = page * 5.00;
                cost.setText("Cost: " + costValue);
            }
        }else{
            if (colorPrint.equals("Yes")) {
                costValue = page * 5.00;
                cost.setText("Cost: " + costValue);
            } else {
                costValue = page * 2.00;
                cost.setText("Cost: " + costValue);
            }
        }
    }

//    private void updateTotalCost(){
//        totalCostValue = totalCostValue+costValue;
//    }

//    private void payment(){
//        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
//                .with(ImageActivity.this)
//                .setPayeeVpa("7979757341@paytm")
//                .setPayeeName("Subhashish Anand")
//                .setTransactionId(String.valueOf(System.currentTimeMillis()))
//                .setTransactionRefId(String.valueOf(System.currentTimeMillis()))
//                .setDescription("check")
//                .setAmount(String.valueOf(totalCostValue))
//                .build();
//        easyUpiPayment.startPayment();
//
//        easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
//            @Override
//            public void onTransactionCompleted(TransactionDetails transactionDetails) {
//
//                Log.d("TransactionDetails", transactionDetails.toString());
//            }
//
//            @Override
//            public void onTransactionSuccess() {
//                easyUpiPayment.detachListener();
//                Toast.makeText(ImageActivity.this, "Success", Toast.LENGTH_SHORT).show();
//                parentShifting();
//                goMainActivity();
//            }
//
//            @Override
//            public void onTransactionSubmitted() {
//                easyUpiPayment.detachListener();
//                Toast.makeText(ImageActivity.this, "Pending | Submitted", Toast.LENGTH_SHORT).show();
//                clearData();
//            }
//
//            @Override
//            public void onTransactionFailed() {
//                easyUpiPayment.detachListener();
//                Toast.makeText(ImageActivity.this, "Failed", Toast.LENGTH_SHORT).show();
//                clearData();
//            }
//
//            @Override
//            public void onTransactionCancelled() {
//                easyUpiPayment.detachListener();
//                Toast.makeText(ImageActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
//                clearData();
//
//            }
//
//            @Override
//            public void onAppNotFound() {
//                easyUpiPayment.detachListener();
//                Toast.makeText(ImageActivity.this, "No App Found", Toast.LENGTH_SHORT).show();
//                clearData();
//            }
//        });
//    }

//    private void alertBox(){
//        AlertDialog.Builder builder = new AlertDialog.Builder(ImageActivity.this);
//        builder.setMessage("Total Cost: "+totalCostValue).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                payment();
//            }
//        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialogInterface, int i) {
//                clearData();
//            }
//        }).setCancelable(false);
//        AlertDialog alertDialog=builder.create();
//        alertDialog.show();
//    }

//    private void clearData(){
//        mImageUri=null;
//        cost.setText("Cost: ...");
//        mImageView.setImageDrawable(null);
//    }


//    private void parentShifting(){
//        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("orders").child("Payment pending").child(getIntent().getStringExtra("uploadkey"));
//        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("orders").child("pending").child(getIntent().getStringExtra("uploadkey"));
//        reference.orderByChild("user").equalTo(firebaseUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                ref.setValue(dataSnapshot.getValue());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });

//    }
    @Override
    public void onBackPressed() {
        startActivity(new Intent(ImageActivity.this, MainnewActivity.class));
        super.onBackPressed();
    }


    public void Notifications(View view) {
        startActivity(new Intent(ImageActivity.this,NotificationActivity.class));
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        customString = parent.getSelectedItem().toString();
        perPage=customString.split(" ");
        updateCost();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
