package com.printhub.printhub;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import butterknife.BindView;
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
import android.os.SystemClock;
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
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.pdf.PdfReader;
import com.shreyaspatil.EasyUpiPayment.EasyUpiPayment;
import com.shreyaspatil.EasyUpiPayment.listener.PaymentStatusListener;
import com.shreyaspatil.EasyUpiPayment.model.TransactionDetails;

import java.io.IOException;
import java.util.ArrayList;

import static com.printhub.printhub.MainnewActivity.firebaseUserId;

public class pdfActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    TextView notification,noOfPages,cost;
    Switch switch1,color;

    private double costValue=2;
    Spinner custom;
    String customString;
    String[] perPage;

    FirebaseStorage storage;         //used for uploading files .. Ex: pdf
    FirebaseDatabase database;

    ProgressDialog progressDialog;

    Uri pdfUri;                      //uri are actually URLs that are meant for local storage
    String upiIdEt,name, note, rollNo, doubleSided="No",colorPrint="No";
    int noPages=0;

    int startPageNo;
    int endPageNo;

    EditText noOfPrint, startingPage, endingPage;
    int copies=1;

    TextView chooseAnotherFile;
    TextView add_to_cart;
    ImageView buyNow;
    ImageView notifica;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("PDF prints");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        notifica= findViewById(R.id.notific);
        buyNow = findViewById(R.id.buy_now);
        notification = findViewById(R.id.notification);
        noOfPages=findViewById(R.id.noOfPages);
        cost=findViewById(R.id.cost);
        noOfPrint=findViewById(R.id.noOfPrint);
        startingPage = findViewById(R.id.startPageNo);
        endingPage = findViewById(R.id.endPageNo);
        switch1 = findViewById(R.id.switch1);
        color = findViewById(R.id.color_photo);
        chooseAnotherFile =findViewById(R.id.choose_file);
        add_to_cart = findViewById(R.id.add_to_cart);
        custom = findViewById(R.id.custom);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.Print, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        custom.setAdapter(adapter);
        custom.setOnItemSelectedListener(this);

         storage=FirebaseStorage.getInstance();             //Return an object of firebase storage
        database=FirebaseDatabase.getInstance();           //Return an object of firebase database


        if(ContextCompat.checkSelfPermission(pdfActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) {
            selectPdf();
        }else{
            ActivityCompat.requestPermissions(pdfActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},9);
        }

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
        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(doubleSided.equals("No")){
                    doubleSided = "Yes";
                }else{
                    doubleSided= "No";
                }
                updateCost();
            }
        });

        notifica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(pdfActivity.this,NotificationActivity.class));
                finish();
            }
        });

        chooseAnotherFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectPdf();
            }
        });

        buyNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(pdfActivity.this, Cart.class));
                finish();
            }
        });

        add_to_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(startPageNo<=endPageNo & startPageNo>=1 & startPageNo<=noPages & endPageNo<=noPages ){
                    if(pdfUri!=null) {
                        uploadFile(pdfUri);
                    }else {
                        Toasty.error(pdfActivity.this, "Please select a pdf").show();
                    }
                }else{
                    Toasty.error(pdfActivity.this, "Enter correct starting and ending page no", Toast.LENGTH_SHORT).show();
                }
            }
        });

//        addFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addFileClick=true;
//                if(startPageNo<=endPageNo){
//                    if(pdfUri!=null) {
//                        uploadFile(pdfUri);
//                    }else {
//                        selectPdf();
//                    }
//                }else{
//                    Toast.makeText(pdfActivity.this, "Enter correct starting and ending page no",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        deleteFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                selectPdf();
//            }
//        });

//        pay.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                addFileClick = false;
//                if(isConnectionAvailable(pdfActivity.this)) {
//                    if(pdfUri!=null) {
//
//                        if (startPageNo <= endPageNo & startPageNo != 0 & startPageNo <= noPages & endPageNo <= noPages & endPageNo != 0) {
//                            uploadFile(pdfUri);
//
//                        } else {
//                            Toast.makeText(pdfActivity.this, "Enter correct starting and ending page no", Toast.LENGTH_SHORT).show();
//                        }
//                    }else {
//                        updateTotalCost();
//                        alertBox();
//                    }
//
//                }else{
//                    Toast.makeText(pdfActivity.this, "Check your connection",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

        startingPage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {

                if(!startingPage.getText().toString().equals("")){
                    int pageNo = Integer.valueOf(startingPage.getText().toString().trim());
                    if(pageNo<= noPages & pageNo!=0){
                        startPageNo = pageNo;
                    }else{
                        startingPage.setText("1");
                        startPageNo=1;
                        Toasty.error(pdfActivity.this, "Enter correct starting page no",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    startPageNo = 1;
                    Toasty.normal(pdfActivity.this, "Default starting page no is 1",Toast.LENGTH_SHORT).show();
                }
                updateCost();
            }
        });

        endingPage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!endingPage.getText().toString().equals("")){
                    int pageNo = Integer.valueOf(endingPage.getText().toString().trim());
                    if(pageNo<= noPages & pageNo!=0){
                        endPageNo = pageNo;
                    }else{
                        endingPage.setText(pageNo+"");
                        endPageNo = pageNo;
                        Toast.makeText(pdfActivity.this, "Enter correct ending page no",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    endPageNo = noPages;
                    Toasty.normal(pdfActivity.this,"Deafault end Page no is "+endPageNo,Toast.LENGTH_SHORT).show();
                }
                updateCost();
            }
        });

        noOfPrint.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!noOfPrint.getText().toString().equals("")){
                    int copy = Integer.valueOf(noOfPrint.getText().toString().trim());
                    if(copy!=0){
                        copies = copy;
                    }else{
                        noOfPrint.setText("1");
                        copies = 1;
                        Toast.makeText(pdfActivity.this, "Enter correct copies",Toast.LENGTH_SHORT).show();
                    }
                }else{
                    copies=1;
                    Toasty.normal(pdfActivity.this,"default Copies is 1", Toast.LENGTH_SHORT).show();
                }
                updateCost();
            }
        });
    }



    public void uploadFile(final Uri pdfUri) {
        if(isConnectionAvailable(this)) {
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
            ref.child("User").child(firebaseUserId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    rollNo = dataSnapshot.child("rollNumber").getValue().toString();

                    progressDialog = new ProgressDialog(pdfActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setTitle("Uploading file...");
                    progressDialog.setProgress(0);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String key = ref.push().getKey();

                    final String fileName = key +"@"+ rollNo+ ".pdf";
                    final String fileName1 = key+"@"+rollNo ;
                    StorageReference storageReference = storage.getReference();       //returns root path
                    storageReference.child(fileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            String type = "."+ getFileExtension(pdfUri);  //type
                            //Store the url in realtime database.
                            DatabaseReference reference = database.getReference();        //return the path to root

                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("doubleSided").setValue(doubleSided);
                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("custom").setValue(customString);
                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("color").setValue(colorPrint);
                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("startPageNo").setValue(startPageNo+"");
                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("endPageNo").setValue(endPageNo+"");
                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("fileName").setValue(pdfUri.getLastPathSegment());
                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("Cost").setValue(costValue+"");
                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("Copy").setValue(copies+"");
                            reference.child("printCart").child(firebaseUserId).child(fileName1).child("type").setValue(type).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        Toast.makeText(pdfActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                        switch1.setChecked(false);
                                        color.setChecked(false);
                                        colorPrint ="No";
                                        doubleSided = "No";
                                        Toasty.success(pdfActivity.this,"File added to your cart").show();
                                    } else {
                                        Toasty.error(pdfActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toasty.error(pdfActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {

                            //Track the progress of our upload
                            double currentProgress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setProgress((int) currentProgress);
                        }
                    });
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            Toast.makeText(pdfActivity.this, "Check your connection",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 9 && grantResults[0]== PackageManager.PERMISSION_GRANTED){
            selectPdf();
        }else{
            Toast.makeText(pdfActivity.this,"Please provide permission",Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void selectPdf() {
        //to offer user to select a file using file manager
        //we will be using an Intent
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction((Intent.ACTION_GET_CONTENT));   //to fetch files
        startActivityForResult(intent,86);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Whether user has selected a file or not(ex: pdf)


        if (requestCode== 86 && resultCode==RESULT_OK && data!=null){
            pdfUri = data.getData();   //return the uri of selected file.
            notification.setText("Document: "+data.getData().getLastPathSegment());
            try {
                PdfReader document = new PdfReader(pdfActivity.this.getContentResolver().openInputStream(pdfUri));
                noPages = document.getNumberOfPages();
                startPageNo=1;
                endPageNo = noPages;
                noOfPages.setText("No of pages: "+noPages);
                startingPage.setText("1");
                endingPage.setText(noPages+"");
                noOfPrint.setText("1");
                updateCost();
                //updateTotalCost();
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(pdfActivity.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.i("A Error",e.getMessage());
                pdfActivity.this.finish();
            }
        }else if(requestCode== 86 && data==null){
            finish();
        }

    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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

    private void updateCost(){
        int page = ((endPageNo - startPageNo + 1)* copies)/Integer.parseInt(perPage[0]);
        int rem = ((endPageNo - startPageNo + 1)* copies)%Integer.parseInt(perPage[0]);
        if(rem!=0) {
            page = page+1;
        }
        if(colorPrint.equals("Yes")){
            costValue = page * 5.00;
            cost.setText("Cost: " + costValue);
        }else{
            if(doubleSided.equals("No")) {
                costValue = page  * 2.00;
                cost.setText("Cost: " + costValue);
            }else{
                if(page>1) {
                    if((page<=30)){
                        costValue = page* 1.50;
                        cost.setText("Cost: " + costValue);
                    }else{
                        costValue = page* 1.00;
                        cost.setText("Cost: " + costValue);
                    }
                }else{
                    costValue = page* 2.00;
                    cost.setText("Cost: " + costValue);
                }
            }
        }

    }

    private void payment(){
        final EasyUpiPayment easyUpiPayment = new EasyUpiPayment.Builder()
                .with(pdfActivity.this)
                .setPayeeVpa("7979757341@paytm")
                .setPayeeName("subhashish anand")
                .setTransactionId(String.valueOf(System.currentTimeMillis()))
                .setTransactionRefId(String.valueOf(System.currentTimeMillis()))
                .setDescription("check")
                //.setAmount(String.valueOf(totalCostValue))
                .build();
        easyUpiPayment.startPayment();

        easyUpiPayment.setPaymentStatusListener(new PaymentStatusListener() {
            @Override
            public void onTransactionCompleted(TransactionDetails transactionDetails) {

                Log.d("TransactionDetails", transactionDetails.toString());
            }

            @Override
            public void onTransactionSuccess() {
                easyUpiPayment.detachListener();
                Toast.makeText(pdfActivity.this, "Success", Toast.LENGTH_SHORT).show();
                parentShifting();
                finish();
            }

            @Override
            public void onTransactionSubmitted() {
                Toast.makeText(pdfActivity.this, "Pending | Submitted", Toast.LENGTH_SHORT).show();
                clearData();
            }

            @Override
            public void onTransactionFailed() {
                easyUpiPayment.detachListener();
                Toast.makeText(pdfActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                clearData();
            }

            @Override
            public void onTransactionCancelled() {
                easyUpiPayment.detachListener();
                Toast.makeText(pdfActivity.this, "Cancelled", Toast.LENGTH_SHORT).show();
                clearData();

            }

            @Override
            public void onAppNotFound() {
                easyUpiPayment.detachListener();
                Toast.makeText(pdfActivity.this, "No App Found", Toast.LENGTH_SHORT).show();
                clearData();
            }
        });
    }

//    private void alertBox(){
//     AlertDialog.Builder builder = new AlertDialog.Builder(pdfActivity.this);
//     builder.setMessage("Total Cost: "+totalCostValue).setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//         @Override
//         public void onClick(DialogInterface dialogInterface, int i) {
//             payment();
//         }
//     }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//         @Override
//         public void onClick(DialogInterface dialogInterface, int i) {
//             clearData();
//         }
//     }).setCancelable(false);
//     AlertDialog alertDialog=builder.create();
//     alertDialog.show();
//    }

    private void clearData(){
        pdfUri=null;
        notification.setText("No document selected");
        noOfPages.setText("No of pages: ...");
        cost.setText("Cost: ...");
    }


//    private void updateTotalCost(){
//        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("orders").child("Payment pending").child(getIntent().getStringExtra("uploadkey"));
//        reference.orderByChild("user").equalTo(firebaseUserId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                totalCostValue = 0;
//                for(DataSnapshot datas: dataSnapshot.getChildren()) {
//                    double value = Double.valueOf(datas.child("Cost").getValue().toString().trim());
//                    totalCostValue = totalCostValue + value;
//                    totalCost.setText("Previous total cost: "+totalCostValue);
//                }
//
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }

    private void parentShifting(){
        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("orders").child("Payment pending").child(getIntent().getStringExtra("uploadkey"));
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("orders").child("pending").child(getIntent().getStringExtra("uploadkey"));
        reference.orderByChild("user").equalTo(firebaseUserId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ref.setValue(dataSnapshot.getValue());
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        customString = parent.getSelectedItem().toString();
        perPage=customString.split(" ");
        updateCost();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

}
