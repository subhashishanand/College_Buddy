package com.printhub.printhub.HomeScreen;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.GenericTransitionOptions;
import com.bumptech.glide.Glide;
import com.printhub.printhub.R;
import com.smarteist.autoimageslider.SliderViewAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private Context context;
    private int mCount;
    ArrayList<String> sliderImage=new ArrayList<>();
    ArrayList<String> sliderAbout=new ArrayList<>();


    public SliderAdapterExample(Context context){
        this.context=context;
    }


    public SliderAdapterExample(Context context, ArrayList<String> aboutUs, ArrayList<String> sliderImages) {
        this.context = context;
        this.sliderAbout=aboutUs;
        this.sliderImage= sliderImages;
    }

    public void setCount(int count) {
        this.mCount = count;
    }

    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_slider_layout_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(SliderAdapterVH viewHolder, final int position) {
        Picasso.with(context.getApplicationContext()).load(sliderImage.get(position).toString().trim()).placeholder(R.drawable.collegebuddy).into(viewHolder.imageViewBackground);
        viewHolder.textViewDescription.setText(sliderAbout.get(position).toString());
        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.imageGifContainer.setVisibility(View.GONE);

    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return sliderImage.size();
    }

    class SliderAdapterVH extends SliderViewAdapter.ViewHolder {

        View itemView;
        ImageView imageViewBackground;
        ImageView imageGifContainer;
        TextView textViewDescription;

        public SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.iv_auto_image_slider);
            imageGifContainer = itemView.findViewById(R.id.iv_gif_container);
            textViewDescription = itemView.findViewById(R.id.tv_auto_image_slider);
            this.itemView = itemView;
        }
    }


}