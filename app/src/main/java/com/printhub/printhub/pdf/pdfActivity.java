package com.printhub.printhub.pdf;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import es.dmoral.toasty.Toasty;

import android.Manifest;
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
import android.view.MotionEvent;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getkeepsafe.taptargetview.TapTarget;
import com.getkeepsafe.taptargetview.TapTargetSequence;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.listener.OnLoadCompleteListener;
import com.github.barteksc.pdfviewer.listener.OnPageChangeListener;
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.itextpdf.text.pdf.PdfReader;
import com.printhub.printhub.Cart;

import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;
import com.printhub.printhub.collab.collabActivity;
import com.printhub.printhub.image.MultipleImages;
import com.shockwave.pdfium.PdfDocument;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


public class pdfActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener , OnPageChangeListener, OnLoadCompleteListener,
        OnPageErrorListener {

    TextView notification,noOfPages,cost;
    Switch switch1,color;

    String collegeName, cityName;
    SharedPreferences cityNameSharedPref,collegeNameSharedPref;

    private double costValue=2;
    Spinner custom;
    String customString;
    String[] perPage;
    private Boolean isRepeating;
    FirebaseStorage storage;         //used for uploading files .. Ex: pdf
    FirebaseDatabase database;
    LockableScrollView parentScroll;
    LinearLayout lowerLinearLayout;
    FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
    String firebaseUid;

    ProgressDialog progressDialog;

    Uri pdfUri;                      //uri are actually URLs that are meant for local storage
    String upiIdEt,name, note, rollNo, doubleSided="No",colorPrint="No";
    int page;
    int noPages=0;

    int startPageNo;
    int endPageNo;

    EditText noOfPrint, startingPage, endingPage;
    int copies=1;

    TextView chooseAnotherFile;
    TextView add_to_cart;
    ImageView buyNow;

    PDFView pdfView;
    Integer pageNumber = 0;
    String pdfFileName;

    double colorRate=0,doubleSidedPrice=0,singleSidedPrice=0,pageLimit=0,pageLimitDiscount=0;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("PDF prints");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

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
        pdfView = findViewById(R.id.pdfView);
        parentScroll=findViewById(R.id.scrollbar);
        lowerLinearLayout=findViewById(R.id.lowerLinearLayout);
        ArrayAdapter<CharSequence>adapter=ArrayAdapter.createFromResource(this,R.array.Print, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        custom.setAdapter(adapter);
        custom.setOnItemSelectedListener(this);
        firebaseUid=firebaseAuth.getUid();

        collegeNameSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);
        cityNameSharedPref = getSharedPreferences("com.printhub.printhub", MODE_PRIVATE);
        collegeName=collegeNameSharedPref.getString("collegeName","");
        cityName = cityNameSharedPref.getString("cityName", "");

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

       pdfView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                parentScroll.setScrollingEnabled(false);
                pdfView.jumpTo(pdfView.getCurrentPage());
                return false;
            }
        });

        lowerLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                parentScroll.setScrollingEnabled(true);
                return false;
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
                    if(pageNo<= noPages && pageNo!=0){
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
                    if(pageNo<= noPages && pageNo!=0){
                        endPageNo = pageNo;
                    }else{
                        endingPage.setText(noPages+"");
                        endPageNo = noPages;
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

                    progressDialog = new ProgressDialog(pdfActivity.this);
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                    progressDialog.setTitle("Uploading file...");
                    progressDialog.setProgress(0);
                    progressDialog.setCancelable(false);
                    progressDialog.show();

                    String key = UUID.randomUUID().toString();

                    final String fileName = key +".pdf";
                    final String fileName1 = key ;
                    StorageReference storageReference = storage.getReference();       //returns root path
                    storageReference.child(fileName).putFile(pdfUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String type = "."+ getFileExtension(pdfUri);  //type
                            Map<String, Object> pdfDescription = new HashMap<>();
                            pdfDescription.put("userId", firebaseUid);
                            pdfDescription.put("doubleSided", doubleSided);
                            pdfDescription.put("customString", customString);
                            pdfDescription.put("color", colorPrint);
                            pdfDescription.put("startPageNo", startPageNo+"");
                            pdfDescription.put("endPageNo", endPageNo+"");
                            pdfDescription.put("fileName", pdfUri.getLastPathSegment());
                            pdfDescription.put("cost", costValue+"");
                            pdfDescription.put("copy", copies+"");
                            pdfDescription.put("type",type);
                            pdfDescription.put("retriveName", fileName1);
                            db.collection(cityName).document(collegeName).collection("users").document(firebaseUid)
                                    .collection("printCart").document(fileName1).set(pdfDescription).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    progressDialog.dismiss();
                                    Toast.makeText(pdfActivity.this, "File successfully uploaded", Toast.LENGTH_SHORT).show();
                                    switch1.setChecked(false);
                                    color.setChecked(false);
                                    colorPrint ="No";
                                    doubleSided = "No";
                                    Toasty.success(pdfActivity.this,"File added to your cart").show();
                                    startActivity(new Intent(pdfActivity.this,Cart.class));
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    progressDialog.dismiss();
                                    Toasty.error(pdfActivity.this, "file not uploaded", Toast.LENGTH_SHORT).show();
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
            pdfFileName= pdfUri.getLastPathSegment();
            notification.setText("Document: "+pdfFileName);
            try {
                PdfReader document = new PdfReader(pdfActivity.this.getContentResolver().openInputStream(pdfUri));
                noPages = document.getNumberOfPages();
                displayFromUri(pdfUri);
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
                //pdfActivity.this.finish();
            }
        }else if(requestCode== 86 && data==null){
            finish();
        }

    }

    private void displayFromUri(Uri pdfUri) {
        pdfView.fromUri(pdfUri)
                .defaultPage(pageNumber)
                .onPageChange(this)
                .enableAnnotationRendering(true)
                .onLoad(this)
                .scrollHandle(new DefaultScrollHandle(this))
                .spacing(10) // in dp
                .onPageError(this)
                .load();
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
        page = ((endPageNo - startPageNo + 1)* copies)/Integer.parseInt(perPage[0]);
        int rem = ((endPageNo - startPageNo + 1)* copies)%Integer.parseInt(perPage[0]);
        if(rem!=0) {
            page = page+1;
        }
        if(colorRate==0 || doubleSidedPrice==0 || singleSidedPrice==0){
            db.collection(cityName).document(collegeName).collection("printingPrice").document("printPrice").get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    colorRate=documentSnapshot.getDouble("color");
                    doubleSidedPrice=documentSnapshot.getDouble("doubleSided");
                    singleSidedPrice=documentSnapshot.getDouble("singleSided");
                    pageLimit=documentSnapshot.getDouble("pageLimit");
                    pageLimitDiscount=documentSnapshot.getDouble("pageLimitDiscount");
                    if (colorPrint.equals("Yes")) {
                        costValue = page * colorRate;
                    } else {
                        if (doubleSided.equals("No")) {
                            costValue = page * singleSidedPrice;
                        } else {
                            if (page > 1) {
                                costValue = page * doubleSidedPrice;
                            } else {
                                costValue = page * singleSidedPrice;
                            }
                        }
                    }
                    if(page>((int)pageLimit)){
                        costValue=costValue-((costValue*pageLimitDiscount)/100);
                    }
                    cost.setText("Cost: " + costValue);
                }
            });
        }else {
            if (colorPrint.equals("Yes")) {
                costValue = page * colorRate;
            } else {
                if (doubleSided.equals("No")) {
                    costValue = page * singleSidedPrice;                } else {
                    if (page > 1) {
                            costValue = page * doubleSidedPrice;
                    } else {
                        costValue = page * singleSidedPrice;
                    }
                }
            }
            if(page>((int)pageLimit)){
                costValue=costValue-((costValue*pageLimitDiscount)/100);
            }
            cost.setText("Cost: " + costValue);
        }

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

    @Override
    public void loadComplete(int nbPages) {
        PdfDocument.Meta meta = pdfView.getDocumentMeta();
        printBookmarksTree(pdfView.getTableOfContents(), "-");

    }

    public void printBookmarksTree(List<PdfDocument.Bookmark> tree, String sep) {
        for (PdfDocument.Bookmark b : tree) {

            if (b.hasChildren()) {
                printBookmarksTree(b.getChildren(), sep + "-");
            }
        }
    }

    @Override
    public void onPageChanged(int page, int pageCount) {
        pageNumber = page;
        setTitle(String.format("%s %s / %s", pdfFileName, page + 1, pageCount));

    }

    private void tapview() {

        new TapTargetSequence(this)
                .targets(
                        TapTarget.forView(findViewById(R.id.tour), "Scroll Pdf", "Use the Scrollbar on the right side to navigate through the pdf")
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
                        TapTarget.forView(findViewById(R.id.printFrom), "Print Detail", "Select the part of the pdf that you want to print")
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
                        TapTarget.forView(findViewById(R.id.noOfPrint), "No of Copies", "Enter the number of copies")
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
                        Toasty.success(pdfActivity.this, " You are ready to go !", Toast.LENGTH_SHORT).show();
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


    @Override
    public void onPageError(int page, Throwable t) {

    }

    public void viewtour(View view){
        tapview();
    }

    public void homeclick(View view){
        startActivity(new Intent(pdfActivity.this, MainnewActivity.class));
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
