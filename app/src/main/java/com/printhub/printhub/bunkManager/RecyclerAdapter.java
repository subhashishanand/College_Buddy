package com.printhub.printhub.bunkManager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.List;

import androidx.recyclerview.widget.RecyclerView;

import com.printhub.printhub.R;

public  class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder>  { //Implemented onclicklistener to get
    //context

    public static Context getApplicationContext;
    private List<Subjectlist> subjectlists;
    private Activity activity;
    RecyclerAdapter res;




    public RecyclerAdapter(Activity activity, List<Subjectlist> subjectlists) {
        this.subjectlists = subjectlists;
        this.activity = activity;
    }

    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup viewGroup, int viewType) {

        final DatabaseHelper db1 = new DatabaseHelper(viewGroup.getContext()); //database helper object here viewGroup.getContext

        //passes the context of clicked viewgroup

        //inflate your layout and pass it to view holder
        LayoutInflater inflater = activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.recyclesub, viewGroup, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        EditSub e = new EditSub();


        res = new RecyclerAdapter(this.activity,subjectlists); //object for recycleradapter

        Button attendbutton = (Button) view.findViewById(R.id.AttendButton); //click listener for attend button
        Button bunkbutton = (Button) view.findViewById(R.id.BunkButton); //click listener for bunk button
        attendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.AttendButton )  //to match the id of element clcked with attend button
                {
                    db1.attend(subjectlists.get(viewHolder.getAdapterPosition()).getSubname());
                }

                setRecyclerViewData(db1); //to update the data in subject array list
                // after updating in database

                notifyDataSetChanged();        //to notify the recyclerview that the
                //dataset has been changed and needs to
                // be refreshed
            }
        });


        bunkbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view.getId() == R.id.BunkButton )  //to match the id of element clcked with attend button
                {
                    db1.bunk(subjectlists.get(viewHolder.getAdapterPosition()).getSubname());

                }

                setRecyclerViewData(db1);

                notifyDataSetChanged();
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final RecyclerAdapter.ViewHolder viewHolder, int position) {

        int adapterPos = viewHolder.getAdapterPosition();
        if (adapterPos < 0) {
            position *= -1;
        }

        //setting data to view holder elements
        viewHolder.Subject.setText(subjectlists.get(position).getSubname());
        viewHolder.Percent.setText(String.valueOf(subjectlists.get(position).getPercent()));
        viewHolder.Attendance.setText(subjectlists.get(position).getAttendance()+"/"+(subjectlists.get(position).getAttendance()+subjectlists.get(position).getBunked()));

        //set on long click listener for each element
        viewHolder.container.setOnLongClickListener(onLongClickListener(position));



    }




    protected class ViewHolder extends RecyclerView.ViewHolder {
        private TextView Subject;
        private TextView Attendance;
        private TextView Percent;
        private View container;
        private Button attendbutton;
        private Button bunkbutton;

        public ViewHolder(View view) {
            super(view);
            Subject = (TextView) view.findViewById(R.id.subname);
            Attendance = (TextView) view.findViewById(R.id.attendance);
            Percent = (TextView) view.findViewById(R.id.percent);
            container = (View) view.findViewById(R.id.card);
            attendbutton = (Button) view.findViewById(R.id.AttendButton);
            bunkbutton = (Button) view.findViewById(R.id.BunkButton);
        }

    }

    private void setDataToView(TextView subname, TextView percent, TextView attendance, int position) {
        subname.setText(subjectlists.get(position).getSubname());
        percent.setText(String.valueOf(subjectlists.get(position).getPercent()));
        attendance.setText(subjectlists.get(position).getAttendance());
    }

    public void setRecyclerViewData(DatabaseHelper db) {  // Here we have declared same like mainactivity method with additional context as a parameter
        // because we need to collect  data from given viewholder and update subjectlist as soon as db is updated

        subjectlists.clear();

        //Adding DataBase data to the ArrayList to Display on RecyclerView
        Cursor res = db.getAllData();
        if(res.getCount() == 0){
            return;
        }
        while (res.moveToNext()){
            subjectlists.add(new Subjectlist(res.getString(0),Float.parseFloat(res.getString(4)),Integer.parseInt(res.getString(1)),Integer.parseInt(res.getString(2))));
        }

    }


    @Override
    public int getItemCount() {
        return (null != subjectlists ? subjectlists.size() : 0);
    }

    public View.OnLongClickListener onLongClickListener(final int position) {
        return new View.OnLongClickListener(){

            @Override
            public boolean onLongClick(View view) {
                DatabaseHelper db = new DatabaseHelper(view.getContext());
                Intent i = new Intent(view.getContext(), EditSub.class);
                i.putExtra("percent", subjectlists.get(position).getPercent());
                i.putExtra("attendance", subjectlists.get(position).getAttendance());
                i.putExtra("subname",subjectlists.get(position).getSubname());
                i.putExtra("postition",position);
                i.putExtra("bunk",subjectlists.get(position).getBunked());
                activity.startActivity(i);
                (activity).finish();
                return true;
            }

        };
    }



}