package com.printhub.printhub.clubEvents;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
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
import com.printhub.printhub.R;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class postEvent extends AppCompatActivity {
    private ImageView postImage;
    private EditText postDes,clubName;
    private Button postButton;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri=null;
    private ProgressBar progressBar;

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
        progressBar=findViewById(R.id.progress_bar);
        postImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });

        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String desc=postDes.getText().toString();
                if(null!=mImageUri && !TextUtils.isEmpty(desc)){
                    progressBar.setVisibility(View.VISIBLE);
                    String random= FieldValue.serverTimestamp().toString();
                    StorageReference filepath= storageReference.child("clubEventImages").child(random+".jpg");
                    filepath.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                             filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                 @Override
                                 public void onSuccess(Uri uri) {
                                     Map<String,Object> postMap=new HashMap<>();
                                     Uri url = uri;
                                     postMap.put("imageUrl",url.toString());
                                     postMap.put("description",desc);
                                     postMap.put("user_id",currUserId);
                                     postMap.put("timestamp",FieldValue.serverTimestamp());

                                     firebaseFirestore.collection(cityName).document(collegeName).collection("collab").add(postMap).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                         @Override
                                         public void onSuccess(DocumentReference documentReference) {
                                             Toast.makeText(postEvent.this, "Everthing works fine", Toast.LENGTH_LONG).show();
                                             progressBar.setVisibility(View.INVISIBLE);
                                             Intent intent=new Intent(getApplicationContext(),clubActivity.class);
                                             startActivity(intent);
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
                }
            }
        });

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
            Picasso.with(this).load(mImageUri).into(postImage);
        } else if (requestCode == PICK_IMAGE_REQUEST && data == null) {
            finish();
        }

    }
}