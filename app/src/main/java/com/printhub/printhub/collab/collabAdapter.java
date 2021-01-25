package com.printhub.printhub.collab;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.Cart;
import com.printhub.printhub.IndividualProduct;
import com.printhub.printhub.R;
import com.printhub.printhub.WebServices.WebViewActivity;
import com.printhub.printhub.clubEvents.EventsClass;
import com.printhub.printhub.pdf.pdfActivity;
import com.squareup.picasso.Picasso;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

import static com.firebase.ui.auth.AuthUI.canHandleIntent;
import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;


public class collabAdapter extends RecyclerView.Adapter<collabAdapter.ViewHolder> {
//
    private List<collabClass> collab_list;
    private Context context;
    private FirebaseFirestore db;
    private RecyclerView recyclerView;

    public void update(collabClass cc){
        collab_list.add(cc);
        notifyDataSetChanged();
    }
    public collabAdapter( List<collabClass> collab_list,Context context,RecyclerView recyclerView) {
        this.collab_list=collab_list;
        this.context= context;
        this.recyclerView=recyclerView;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.collab_cardlayout,parent,false);
        db= FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
       // String desc_data=blog_list.get(position).getDescription();
       // holder.setDescText(desc_data);
        String postkey =  collab_list.get(position).getPostkey();
        holder.description.setText(collab_list.get(position).getDescription());
        holder.domain.setText("Domain:"+" "+collab_list.get(position).getDomain());
        String postuser_id=collab_list.get(position).getUserid();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection(cityName).document(collegeName).collection("users").document(postuser_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    String userName=task.getResult().getString("name");
                    String imageurl = task.getResult().getString("imageLink").toString().trim();
                    holder.username.setText(userName);
                    Picasso.with(context).load(imageurl).placeholder(R.drawable.avtarimage).into(holder.userimage);
                }
            }
        });
        //countlike
        db.collection(cityName).document(collegeName).collection("collab").document(postkey).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots,FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    int count = queryDocumentSnapshots.size();
                    holder.text_action.setText(count+" Interested");
                }else{
                    holder.text_action.setText(0 +" Interested");
                }

            }
        });
        if(userid.equals(postuser_id)){
            holder.menuOtion.setVisibility(View.VISIBLE);
            holder.menuOtion.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popupMenu= new PopupMenu(context,holder.menuOtion);
                    popupMenu.inflate(R.menu.updatepost);
                    popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch(item.getItemId()){
                                case R.id.item1:
                                    callUpdate(postkey,userid) ;
                                    break;
                                case R.id.item2:
                                    CallDelete(postkey,userid);
                                    break;
                                default:
                                    break;
                            }
                            return false;
                        }
                    });
                    popupMenu.show();
                }
            });
        }else{
            holder.menuOtion.setVisibility(View.GONE);
        }

        //getlike
        db.collection(cityName).document(collegeName).collection("collab").document(postkey).collection("Likes").document(userid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot,FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    holder.interest.setImageDrawable(context.getDrawable(R.drawable.like_red));
                }else{
                    holder.interest.setImageDrawable(context.getDrawable(R.drawable.like_grey));

                }

            }
        });

        String gitlink=collab_list.get(position).getGithubId();
        if( null!=gitlink && !TextUtils.isEmpty(gitlink)){
            holder.github.setVisibility(View.VISIBLE);
            holder.github.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"Redirecting you to Github",Toast.LENGTH_SHORT).show();
                    String link=collab_list.get(position).getGithubId();
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("Link", link);
                    context.startActivity(intent);

                }
            });
        }
        String whatsappno=collab_list.get(position).getWhatsApp();
        if(null!=whatsappno && !TextUtils.isEmpty(whatsappno)) {
            String whatsapplink = "https://wa.me/+91" + whatsappno;
            holder.whatsapp.setVisibility(View.VISIBLE);
            holder.whatsapp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"Redirecting you to WhatsApp",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(context, WebViewActivity.class);
                    intent.putExtra("Link", whatsapplink);
                    context.startActivity(intent);

                }
            });

        }
            String number= collab_list.get(position).getMobileNo();
            if(null!=number && !TextUtils.isEmpty(number)){
                holder.call.setVisibility(View.VISIBLE);
                holder.call.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(context,"Redirecting you to Call",Toast.LENGTH_SHORT).show();
                        if(ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions((Activity) context,new String[]{Manifest.permission.CALL_PHONE},9);
                        }else{
                            String s= "tel:"+number;
                            Intent intent= new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse(s));
                            context.startActivity(intent);
                        }

                    }
                });
            }




        holder.interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 //holder.interest.playAnimation();
                collabClass collabClass = new collabClass(collab_list.get(position).getDomain(),collab_list.get(position).getDescription(),collab_list.get(position).getMobileNo(),collab_list.get(position).getWhatsApp(),collab_list.get(position).getGithubId(),collab_list.get(position).getLinkedinId(),collab_list.get(position).getStatus(),collab_list.get(position).getUserid(),collab_list.get(position).getTimestamp(),postkey);
                Map<String,Object> interestmap = new HashMap<>();
                interestmap.put("postkey", postkey);

                db.collection(cityName).document(collegeName).collection("collab").document(postkey).collection("Likes").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            db.collection(cityName).document(collegeName).collection("users").document(userid).collection("interest").document(postkey).set(collabClass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toasty.success(context, "Added to Interest").show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toasty.success(context, "Failed Try Again").show();
                                }
                            });
                            Map<String,Object> likemap = new HashMap<>();
                            likemap.put("timestamp", FieldValue.serverTimestamp());
                            db.collection(cityName).document(collegeName).collection("collab").document(postkey).collection("Likes").document(userid).set(likemap);
                        }else{
                            db.collection(cityName).document(collegeName).collection("users").document(userid).collection("interest").document(postkey).delete();
                            db.collection(cityName).document(collegeName).collection("collab").document(postkey).collection("Likes").document(userid).delete();
                        }

                    }
                });


            }
        });

        long milliseconds=collab_list.get(position).getTimestamp().getTime();
        String dateString= DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
        holder.postdate.setText(dateString);


    }

    @Override
    public int getItemCount() {
        return collab_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        CircleImageView userimage;
        ImageView github,linkedin,whatsapp,call;
        ImageView interest;
        private TextView username,postdate,domain,description,text_action,menuOtion;
        public ViewHolder(@NonNull  View itemView) {

            super(itemView);
            mView=itemView;
            userimage= mView.findViewById(R.id.userimage);
            github= mView.findViewById(R.id.github);
            linkedin= mView.findViewById(R.id.linkedin);
            whatsapp= mView.findViewById(R.id.whatsapp);
            call= mView.findViewById(R.id.call);
            interest= mView.findViewById(R.id.interest);
            username= mView.findViewById(R.id.username);
            postdate= mView.findViewById(R.id.postdate);
            domain= mView.findViewById(R.id.domain);
            description= mView.findViewById(R.id.description);
            text_action = mView.findViewById(R.id.text_action);
            menuOtion= mView.findViewById(R.id.menuoption);



            //descView=mView.findViewById(R.id.blog_desc);
        }

//        public void setDescText(String descText){
//            descView=mView.findViewById(R.id.blog_desc);
//            descView.setText(descText);
//        }

    }

    private void callUpdate(String postKey,String userid){
        Intent intent = new Intent(context, collabPostActivity.class);
        intent.putExtra("postKey", postKey);
        context.startActivity(intent);
        //((Activity)context).finish();

    }

    private void CallDelete(String postKey,String userid){
        db.collection(cityName).document(collegeName).collection("collab").document(postKey).collection("Likes").document(userid).delete();
        db.collection(cityName).document(collegeName).collection("collab").document(postKey).delete();
        context.startActivity(new Intent(context, collabActivity.class));
        ((Activity)context).finish();

    }



}
