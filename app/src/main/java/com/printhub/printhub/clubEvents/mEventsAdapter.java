package com.printhub.printhub.clubEvents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.allyants.notifyme.NotifyMe;
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
import com.printhub.printhub.R;
import com.printhub.printhub.WebServices.WebViewActivity;
import com.printhub.printhub.bunkManager.Subjectlist;
import com.printhub.printhub.collab.collabActivity;
import com.printhub.printhub.collab.collabClass;
import com.printhub.printhub.collab.collabPostActivity;
import com.printhub.printhub.prodcutscategory.Stationary;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.security.auth.login.LoginException;

import es.dmoral.toasty.Toasty;
import io.grpc.internal.LogExceptionRunnable;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;
import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class mEventsAdapter extends RecyclerView.Adapter<mEventsAdapter.ViewHolder> {

    List<EventsClass> blog_list;
    Context context;
    RecyclerView recyclerView;
    private FirebaseFirestore db;


    public void update(EventsClass eventsClass){
        blog_list.add(eventsClass);
        notifyDataSetChanged();
    }

    public mEventsAdapter( List<EventsClass> blog_list,Context context,RecyclerView recyclerView) {
        this.blog_list=blog_list;
        this.context= context;
        this.recyclerView=recyclerView;
    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull  ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.club_event_cardlayout,parent,false);
        db= FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        EventsClass eventsClass=blog_list.get(position);
        String postkey =  eventsClass.getEventid();
        String postuser_id= eventsClass.getUserid();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.setDescText(eventsClass.getDescription());
        holder.setTime(eventsClass.getActivityTime());
        holder.setDate(eventsClass.getActivityDate());
        holder.eventName.setText(eventsClass.getEventTitle());
        Picasso.with(context).load(blog_list.get(position).getImageUrl()).placeholder(R.drawable.collab).into(holder.clubEventPost);
        holder.setName("Club Name: "+eventsClass.getClubName());
        if(null!=eventsClass.getLink() &&!eventsClass.getLink().isEmpty()){
            holder.linkTextView.setVisibility(View.VISIBLE);
            holder.linkView.setVisibility(View.VISIBLE);
            holder.setLink(eventsClass.getLink());
            holder.linkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"Redirecting you to link",Toast.LENGTH_SHORT).show();
                    String link =  eventsClass.getLink();
                    Intent i = new Intent(context, WebViewActivity.class);
                    i.putExtra("Link",link );
                    context.startActivity(i);
                }
            });

        }

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


        //countlike
        db.collection(cityName).document(collegeName).collection("clubActivity").document(postkey).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot queryDocumentSnapshots, FirebaseFirestoreException e) {
                if(!queryDocumentSnapshots.isEmpty()){
                    int count = queryDocumentSnapshots.size();
                    holder.text_action.setText(count+" Interested");
                }else{
                    holder.text_action.setText(0 +" Interested");
                }

            }
        });
        //getlike
        db.collection(cityName).document(collegeName).collection("clubActivity").document(postkey).collection("Likes").document(userid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(DocumentSnapshot documentSnapshot,FirebaseFirestoreException e) {
                if(documentSnapshot.exists()){
                    holder.interest.setImageDrawable(context.getDrawable(R.drawable.like_red));
                }else{
                    holder.interest.setImageDrawable(context.getDrawable(R.drawable.like_grey));

                }

            }
        });



        holder.interest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //holder.interest.playAnimation();
                EventsClass eventsClass1 = new EventsClass(eventsClass.getClubName(),eventsClass.getDescription(),eventsClass.getImageUrl(),eventsClass.getTimestamp(), eventsClass.getActivityDate(),eventsClass.getActivityTime(),eventsClass.getLink(),eventsClass.getEventid(),eventsClass.getEventTitle(),eventsClass.getUserid(),eventsClass.getStatus());

                db.collection(cityName).document(collegeName).collection("clubActivity").document(postkey).collection("Likes").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            db.collection(cityName).document(collegeName).collection("users").document(userid).collection("eventinterest").document(postkey).set(eventsClass1).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                            db.collection(cityName).document(collegeName).collection("clubActivity").document(postkey).collection("Likes").document(userid).set(likemap);
                        }else{
                            db.collection(cityName).document(collegeName).collection("users").document(userid).collection("eventinterest").document(postkey).delete();
                            db.collection(cityName).document(collegeName).collection("clubActivity").document(postkey).collection("Likes").document(userid).delete();
                        }

                    }
                });


            }
        });



        holder.reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.reminderButton.playAnimation();
                Calendar calendar=Calendar.getInstance();
                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                try {
                    calendar.setTime(simpleDateFormat.parse(eventsClass.getActivityDate()+" "+eventsClass.getActivityTime()));
                    calendar.add(Calendar.MINUTE,-30);
                    Log.e("Time",calendar.getTime().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context,clubActivity.class);
                intent.putExtra("test","I am a String");
                    NotifyMe notifyMe = new NotifyMe.Builder(context)
                            .title(eventsClass.clubName)
                            .content("Lets get going")
                            .color(255, 0, 0, 255)
                            .led_color(255, 255, 255, 255)
                            .time(calendar)
                            .addAction(intent, "Snooze", false)
                            .key("test")
                            .addAction(new Intent(), "Dismiss", true, false)
                            .large_icon(R.mipmap.ic_launcher_round)
                            .rrule("FREQ=MINUTELY;INTERVAL=10;COUNT=2")
                            .build();
                    Toasty.success(context,"We will remind you", Toasty.LENGTH_SHORT).show();
                    holder.reminderButton.pauseAnimation();
            }
        });

        long milliseconds=blog_list.get(position).getTimestamp().getTime();
        String dateString= DateFormat.format("dd/MM/yyyy",new Date(milliseconds)).toString();
        holder.setPostingTime(dateString);

    }
    @Override
    public int getItemCount() {
        return blog_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private View mView;
        private TextView descView,eventName;
        private TextView timeTextView, dateTextView,linkTextView,text_action,linkView,menuOtion;
        ImageView clubEventPost,interest;
        private TextView blogDate,authorName;
        LottieAnimationView reminderButton;
        public ViewHolder(@NonNull  View itemView) {

            super(itemView);
            mView=itemView;
            eventName= mView.findViewById(R.id.eventName);
            reminderButton=mView.findViewById(R.id.reminderButton);
            clubEventPost= mView.findViewById(R.id.clubEventPost);
            interest= mView.findViewById(R.id.saveClubEvent);
            text_action= mView.findViewById(R.id.text_action);
            linkTextView= mView.findViewById(R.id.linkTextView);
            linkView = mView.findViewById(R.id.linkview);
            menuOtion= mView.findViewById(R.id.menuoption);
            //descView=mView.findViewById(R.id.blog_desc);
        }

        public void setLink(String link){
            linkTextView=mView.findViewById(R.id.linkTextView);
            linkTextView.setText(link);
        }

        public void setDate(String date){
            dateTextView=mView.findViewById(R.id.clubEventDate);
            dateTextView.setText("Date: "+date);
        }

        public void setTime(String time){
            timeTextView=mView.findViewById(R.id.clubEventTime);
            timeTextView.setText("Time: "+time);
        }

        public void setDescText(String descText){
            descView=mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }
        public void setPostingTime(String date){
            blogDate=mView.findViewById(R.id.blogdate);
            blogDate.setText("Posted On:"+date);
        }

        public void setName(String name){
            authorName=mView.findViewById(R.id.authorName);
            authorName.setText(name);
        }
    }

    private void callUpdate(String postKey,String userid){
        Intent intent = new Intent(context, postEvent.class);
        intent.putExtra("postKey", postKey);
        context.startActivity(intent);
        //((Activity)context).finish();

    }

    private void CallDelete(String postKey,String userid){
        db.collection(cityName).document(collegeName).collection("clubActivity").document(postKey).collection("Likes").document(userid).delete();
        db.collection(cityName).document(collegeName).collection("clubActivity").document(postKey).delete();
        context.startActivity(new Intent(context, clubActivity.class));
        ((Activity)context).finish();



    }

}
