package com.printhub.printhub.globalEvents;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
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
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import es.dmoral.toasty.Toasty;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class GlobalEventAdapter extends RecyclerView.Adapter<GlobalEventAdapter.ViewHolder> {
    List<GlobalEventClass> blog_list;
    Context context;
    RecyclerView recyclerView;
    private FirebaseFirestore db;

    public void update(GlobalEventClass globalEventClass){
        blog_list.add(globalEventClass);
        notifyDataSetChanged();
    }

    public GlobalEventAdapter( List<GlobalEventClass> blog_list,Context context,RecyclerView recyclerView) {
        this.blog_list=blog_list;
        this.context= context;
        this.recyclerView=recyclerView;
    }

    @Override
    public GlobalEventAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.club_event_cardlayout,parent,false);
        db= FirebaseFirestore.getInstance();
        return new GlobalEventAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GlobalEventAdapter.ViewHolder holder, int position) {
        holder.location.setVisibility(View.VISIBLE);
        GlobalEventClass globalEventsClass=blog_list.get(position);
        String postkey =  globalEventsClass.getEventid();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.setDescText(globalEventsClass.getDescription());
        holder.setName(globalEventsClass.getName());
        holder.setTime(globalEventsClass.getActivityTime());
        holder.setDate(globalEventsClass.getActivityDate());
        Picasso.with(context).load(blog_list.get(position).getImageUrl()).placeholder(R.drawable.collegebuddy).into(holder.EventPost);
        holder.setName(globalEventsClass.getName());
        holder.location.setText(globalEventsClass.getLocaton());
        holder.eventName.setText(globalEventsClass.getEventTitle());
        if(null!=globalEventsClass.getLink() &&!globalEventsClass.getLink().isEmpty()){
            holder.linkTextView.setVisibility(View.VISIBLE);
            holder.setLink(globalEventsClass.getLink());
            holder.linkTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(context,"Redirecting you to link",Toast.LENGTH_SHORT).show();
                    String link =  globalEventsClass.getLink();
                    Intent i = new Intent(context, WebViewActivity.class);
                    i.putExtra("Link",link );
                    context.startActivity(i);
                }
            });
        }

        //countlike
        db.collection("globalEvent").document(postkey).collection("Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
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
        db.collection("globalEvent").document(postkey).collection("Likes").document(userid).addSnapshotListener(new EventListener<DocumentSnapshot>() {
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
                GlobalEventClass globalEventsClass1 = new GlobalEventClass(globalEventsClass.getName(),globalEventsClass.getDescription(),globalEventsClass.getLocaton(),globalEventsClass.getImageUrl(), globalEventsClass.getActivityDate(),globalEventsClass.getActivityTime(),globalEventsClass.getLink(),globalEventsClass.getEventid(),globalEventsClass.getEventTitle(),globalEventsClass.getTimestamp());

                db.collection("globalEvent").document(postkey).collection("Likes").document(userid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(!task.getResult().exists()){
                            db.collection(cityName).document(collegeName).collection("users").document(userid).collection("eventinterest").document(postkey).set(globalEventsClass1).addOnSuccessListener(new OnSuccessListener<Void>() {
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
                            db.collection("globalEvent").document(postkey).collection("Likes").document(userid).set(likemap);
                        }else{
                            db.collection(cityName).document(collegeName).collection("users").document(userid).collection("eventinterest").document(postkey).delete();
                            db.collection("globalEvent").document(postkey).collection("Likes").document(userid).delete();
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
                    calendar.setTime(simpleDateFormat.parse(globalEventsClass.getActivityDate()+" "+globalEventsClass.getActivityTime()));
                    calendar.add(Calendar.MINUTE,-30);
                    Log.e("Time",calendar.getTime().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(context, GlobalActivity.class);
                intent.putExtra("test","I am a String");
                NotifyMe notifyMe = new NotifyMe.Builder(context)
                        .title(globalEventsClass.getEventTitle())
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
                holder.reminderButton.pauseAnimation();
                Toasty.success(context,"We will remind you", Toasty.LENGTH_SHORT).show();
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
        private TextView descView;
        private TextView timeTextView, dateTextView,linkTextView,text_action,location,eventName;
        ImageView EventPost,interest;
        private LottieAnimationView reminderButton;
        private TextView blogDate,authorName;
        public ViewHolder(@NonNull  View itemView) {

            super(itemView);
            mView=itemView;
            reminderButton=mView.findViewById(R.id.reminderButton);
            EventPost= mView.findViewById(R.id.clubEventPost);
            interest= mView.findViewById(R.id.saveClubEvent);
            text_action= mView.findViewById(R.id.text_action);
            linkTextView= mView.findViewById(R.id.linkTextView);
            location = mView.findViewById(R.id.location);
            eventName= mView.findViewById(R.id.eventName);
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
            blogDate.setText("posted on:"+date);
        }

        public void setName(String name){
            authorName=mView.findViewById(R.id.authorName);
            authorName.setText("Organiser Name: "+name);
        }
    }
}
