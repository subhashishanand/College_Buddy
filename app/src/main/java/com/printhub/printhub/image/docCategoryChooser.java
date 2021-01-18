package com.printhub.printhub.image;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.printhub.printhub.HomeScreen.MainnewActivity;
import com.printhub.printhub.R;
import com.printhub.printhub.pdf.pdfActivity;

public class docCategoryChooser extends AppCompatActivity {
    Button image_button, pdf_button;
    DatabaseReference mref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_category_chooser);
        image_button= findViewById(R.id.imageButton);
        pdf_button= findViewById(R.id.pdfButton);
        mref= FirebaseDatabase.getInstance().getReference();
        image_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(docCategoryChooser.this, MultipleImages.class);
                String uploadkey = mref.push().getKey();
                intent.putExtra("uploadkey",uploadkey);
                startActivity(intent);

            }
        });
        pdf_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(docCategoryChooser.this, pdfActivity.class);
                String uploadkey = mref.push().getKey();
                intent.putExtra("uploadkey",uploadkey);
                startActivity(intent);

            }
        });
    }
}