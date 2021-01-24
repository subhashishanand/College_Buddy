package com.printhub.printhub.sidebar.aboutus;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.printhub.printhub.R;

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public SliderAdapter(Context context){
        this.context = context;

    }

    //Arrays
    public int[] slide_images = {
            R.drawable.circle_logo,
            R.drawable.circle_logo,
            R.drawable.circle_logo,
            R.drawable.circle_logo,
            R.drawable.circle_logo,

    };

    public String[] slide_headings = {
            "College Buddy",
            "What we do",
            "Our Inspiration",
            "Our Vision",
            "Team Description",
    };

    public String[] slide_descs= {
            "One stop solution for all your college needs, you name it we got it. We are an Ed-Tech oriented productivity app where students can collaborate together for solving problems. Get access to events happening in their campus and across the globe, and can save their time by getting their requirements delivered at their doorstep. ",
            "The array of features the we are proving right now are Collab, Club Activity, Global Events, Bunk Manager, Purchase Stationery, Get Printouts and access to most used applications like Zomato, Uber, twitter and a lot more " ,
            "We love to have time to chill with friends, getting social, exploring creative projects, learning with friends and most importantly we don't want to bore ourselves with repetitive tasks so we created college buddy. We will do all those tasks you hate and make your life a little simpler and a lot more fun!",
            "Our goal is to cater every need of college students so that they can focus on what matters the most and achieve their true potential",
            "We are just a bunch of students who took it upon themselves to improve College experience for everyone. Our team consist of Prahant, Vaibhav, Subhashish, Rahul and one more Vaibhav ;). We are driven towards our vision and will not stop until we make you college life better and more fun!",
    };

    @Override
    public int getCount() {
        return slide_headings.length;
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view ==  object;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide_layout, container, false);
        container.addView(view);
        ImageView slideImageView = view.findViewById(R.id.slide_image);
        TextView slideHeading = view.findViewById(R.id.slide_heading);
        TextView slideDescription = view.findViewById(R.id.slide_descs);

        slideImageView.setImageResource(slide_images[position]);
        slideHeading.setText(slide_headings[position]);
        slideDescription.setText(slide_descs[position]);

        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
