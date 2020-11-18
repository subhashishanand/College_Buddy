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
            R.drawable.group_b,
            R.drawable.subhu,
            R.drawable.group_b,
            R.drawable.group_b,
            R.drawable.group_b,
            R.drawable.group_b,
            R.drawable.group_b

    };

    public String[] slide_headings = {
            "PRINT HUB",
            "Prashant Yadav",
            "Subhashish Anand",
            "Vaibhav Singh",
            "Rahul Raj",
            "Vaibhav Deep",
            "Subham Priyadarshi"
    };

    public String[] slide_descs= {
            "Set to false to disable downscaling the image to fit into the content aread.",
        "Set to false to disable downscaling the image to fit into the content aread.",
        "Set to false to disable downscaling the image to fit into the content aread.",
            "Set to false to disable downscaling the image to fit into the content aread.",
            "Set to false to disable downscaling the image to fit into the content aread.",
            "Set to false to disable downscaling the image to fit into the content aread.",
            "Set to false to disable downscaling the image to fit into the content aread."
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
