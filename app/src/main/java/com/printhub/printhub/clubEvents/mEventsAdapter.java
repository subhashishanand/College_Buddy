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
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.allyants.notifyme.NotifyMe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.printhub.printhub.R;
import com.printhub.printhub.bunkManager.Subjectlist;
import com.printhub.printhub.prodcutscategory.Stationary;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.security.auth.login.LoginException;

import es.dmoral.toasty.Toasty;
import io.grpc.internal.LogExceptionRunnable;

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
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.club_events_cardlayout,parent,false);
        db= FirebaseFirestore.getInstance();
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull  ViewHolder holder, int position) {
        EventsClass eventsClass=blog_list.get(position);
        String postkey =  eventsClass.getEventid();
        String userid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        holder.setDescText(eventsClass.getDescription());
        holder.setName(eventsClass.clubName);
        holder.setTime(eventsClass.getActivityTime());
        holder.setDate(eventsClass.getActivityDate());
        Picasso.with(context).load(blog_list.get(position).getImageUrl()).into(holder.clubEventPost);
        holder.setName(eventsClass.getClubName());
        if(null!=eventsClass.getLink() &&!eventsClass.getLink().isEmpty()){
            holder.linkTextView.setVisibility(View.VISIBLE);
            holder.setLink(eventsClass.getLink());
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



        holder.reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        private MultiAutoCompleteTextView descView;
        private TextView timeTextView, dateTextView,linkTextView,text_action;
        ImageView clubEventPost,interest;
        private Button reminderButton;
        private TextView blogDate,authorName;
        public ViewHolder(@NonNull  View itemView) {

            super(itemView);
            mView=itemView;
            reminderButton=mView.findViewById(R.id.reminderButton);
            clubEventPost= mView.findViewById(R.id.clubEventPost);
            interest= mView.findViewById(R.id.saveClubEvent);
            text_action= mView.findViewById(R.id.text_action);
            //descView=mView.findViewById(R.id.blog_desc);
        }

        public void setLink(String link){
            linkTextView=mView.findViewById(R.id.linkTextView);
            linkTextView.setText(link);
        }

        public void setDate(String date){
            dateTextView=mView.findViewById(R.id.clubEventDate);
            dateTextView.setText(date);
        }

        public void setTime(String time){
            timeTextView=mView.findViewById(R.id.clubEventTime);
            timeTextView.setText(time);
        }

        public void setDescText(String descText){
            descView=mView.findViewById(R.id.blog_desc);
            descView.setText(descText);
        }
        public void setPostingTime(String date){
            blogDate=mView.findViewById(R.id.blogdate);
            blogDate.setText("posted date:"+date);
        }

        public void setName(String name){
            authorName=mView.findViewById(R.id.authorName);
            authorName.setText(name);
        }
    }
}
