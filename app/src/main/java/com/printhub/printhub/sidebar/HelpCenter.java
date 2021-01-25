package com.printhub.printhub.sidebar;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.printhub.printhub.R;
import com.printhub.printhub.WebServices.WebViewActivity;

public class HelpCenter extends AppCompatActivity {

    private TextView email,insta,contact,contactAlt,contactAlt2,contactAlt3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_center);

        Toolbar toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        email=findViewById(R.id.email);
        insta=findViewById(R.id.insta);
        contact=findViewById(R.id.contact);
        contactAlt=findViewById(R.id.contactAlt);
        contactAlt2=findViewById(R.id.contactAlt2);
        contactAlt3=findViewById(R.id.contactAlt3);

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Forwarding you to our email", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[] {"collegebuddy.connect@gmail.com"});
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT,"Let's Connect");
                intent.putExtra(android.content.Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(intent,"Send"));

                /* Send it off to the Activity-Chooser */
            }
        });

        insta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(getApplicationContext(), WebViewActivity.class);
                Toast.makeText(getApplicationContext(), "Redirecting you to our insta handle", Toast.LENGTH_SHORT).show();
                intent.putExtra("Link","https://www.instagram.com/college_buddy_india?r=nametag");
                startActivity(intent);
            }
        });

        contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.CALL_PHONE},9);
                }else{
                    Toast.makeText(getApplicationContext(),"Hey lets connect",Toast.LENGTH_LONG).show();
                    String s= "tel:"+contact.getText();
                    Intent intent= new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(s));
                    startActivity(intent);
                }

            }
        });
        contactAlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.CALL_PHONE},9);
                }else{
                    String s= "tel:"+contactAlt.getText();
                    Intent intent= new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(s));
                    startActivity(intent);
                }

            }
        });
        contactAlt2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.CALL_PHONE},9);
                }else{
                    String s= "tel:"+contactAlt2.getText();
                    Intent intent= new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(s));
                    startActivity(intent);
                }

            }
        });
        contactAlt3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions((Activity) getApplicationContext(),new String[]{Manifest.permission.CALL_PHONE},9);
                }else{
                    String s= "tel:"+contactAlt3.getText();
                    Intent intent= new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse(s));
                    startActivity(intent);
                }

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
