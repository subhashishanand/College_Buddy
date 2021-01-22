package com.printhub.printhub.clubEvents;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
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

import com.allyants.notifyme.NotifyMe;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
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

import io.grpc.internal.LogExceptionRunnable;

import static com.printhub.printhub.HomeScreen.MainnewActivity.cityName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.collegeName;
import static com.printhub.printhub.HomeScreen.MainnewActivity.firebaseUserId;

public class mEventsAdapter extends RecyclerView.Adapter<mEventsAdapter.ViewHolder> {

    List<EventsClass> blog_list;
    Context context;
    private FirebaseFirestore db;
    public mEventsAdapter( List<EventsClass> blog_list,Context context) {
        this.blog_list=blog_list;
        this.context= context;
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
        holder.setDescText(eventsClass.getDescription());
        holder.setName(eventsClass.clubName);
        holder.setTime(eventsClass.getActivityTime());
        holder.setDate(eventsClass.getActivityDate());
        Picasso.with(context).load(eventsClass.getImageUrl()).into(holder.clubEventPost);
        holder.setName(eventsClass.getClubName());

        holder.reminderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar=Calendar.getInstance();
                SimpleDateFormat simpleDateFormat= new SimpleDateFormat("dd/MM/yyyy hh:mm aa");
                try {
                    calendar.setTime(simpleDateFormat.parse(eventsClass.getActivityDate()+" "+eventsClass.getActivityTime()));
                    Log.e("Time",calendar.getTime().toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                NotifyMe notifyMe = new NotifyMe.Builder(context)
                        .title("Hey")
                        .content("Lets go")
                        .color(255,0,0,255)
                        .led_color(255,255,255,255)
                        .time(calendar)
                  //      .addAction(intent,"Snooze",false)
                        .key("test")
                    //    .addAction(new Intent(),"Dismiss",true,false)
                      //  .addAction(intent,"Done")
                        .large_icon(R.mipmap.ic_launcher_round)
                        .rrule("FREQ=MINUTELY;INTERVAL=5;COUNT=2")
                        .build();
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
        private TextView timeTextView, dateTextView;
        ImageView clubEventPost;
        private Button reminderButton;
        private TextView blogDate,authorName;
        public ViewHolder(@NonNull  View itemView) {

            super(itemView);
            mView=itemView;
            reminderButton=mView.findViewById(R.id.reminderButton);
            clubEventPost= mView.findViewById(R.id.clubEventPost);
            //descView=mView.findViewById(R.id.blog_desc);
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
