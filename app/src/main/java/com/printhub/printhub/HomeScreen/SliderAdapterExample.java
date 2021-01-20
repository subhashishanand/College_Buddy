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

import java.util.ArrayList;

public class SliderAdapterExample extends SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private Context context;
    private int mCount;
    ArrayList<String> sliderImage=new ArrayList<>();
    ArrayList<String> sliderAbout=new ArrayList<>();

    public SliderAdapterExample(Context context,ArrayList<String> aboutUs, ArrayList<String> sliderImages) {
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
        try {
            Glide.with(viewHolder.itemView)
                    .load(sliderImage.get(position).toString().trim())
                    .fitCenter()
                    .into(viewHolder.imageViewBackground);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        viewHolder.textViewDescription.setText(sliderAbout.get(position).toString());
        viewHolder.textViewDescription.setTextSize(16);
        viewHolder.imageGifContainer.setVisibility(View.GONE);



        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "This is item in position " + position, Toast.LENGTH_SHORT).show();
            }
        });

//
//        switch (position) {
//            case 0:
//                viewHolder.textViewDescription.setText("This is slider item " + position);
//                viewHolder.textViewDescription.setTextSize(16);
//                viewHolder.textViewDescription.setTextColor(Color.WHITE);
//                viewHolder.imageGifContainer.setVisibility(View.GONE);
//                Glide.with(viewHolder.itemView)
//                        .load("https://images.pexels.com/photos/218983/pexels-photo-218983.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
//                        .fitCenter()
//                        .into(viewHolder.imageViewBackground);
//                break;
//            case 2:
//                viewHolder.textViewDescription.setText("This is slider item " + position);
//                viewHolder.textViewDescription.setTextSize(16);
//                viewHolder.textViewDescription.setTextColor(Color.WHITE);
//                viewHolder.imageGifContainer.setVisibility(View.GONE);
//                Glide.with(viewHolder.itemView)
//                        .load("https://images.pexels.com/photos/747964/pexels-photo-747964.jpeg?auto=compress&cs=tinysrgb&h=750&w=1260")
//                        .fitCenter()
//                        .into(viewHolder.imageViewBackground);
//                break;
//            case 4:
//                viewHolder.textViewDescription.setText("This is slider item " + position);
//                viewHolder.textViewDescription.setTextSize(16);
//                viewHolder.textViewDescription.setTextColor(Color.WHITE);
//                viewHolder.imageGifContainer.setVisibility(View.GONE);
//                Glide.with(viewHolder.itemView)
//                        .load("https://images.pexels.com/photos/929778/pexels-photo-929778.jpeg?auto=compress&cs=tinysrgb&dpr=2&h=750&w=1260")
//                        .fitCenter()
//                        .into(viewHolder.imageViewBackground);
//                break;
//            default:
//                viewHolder.textViewDescription.setTextSize(29);
//                viewHolder.textViewDescription.setTextColor(Color.WHITE);
//                viewHolder.textViewDescription.setText("Ohhhh! look at this!");
//                viewHolder.imageGifContainer.setVisibility(View.VISIBLE);
//                Glide.with(viewHolder.itemView)
//                        .load(R.drawable.file1)
//                        .fitCenter()
//                        .into(viewHolder.imageViewBackground);
//                Glide.with(viewHolder.itemView)
//                        .asGif()
//                        .load(R.drawable.file2)
//                        .into(viewHolder.imageGifContainer);
//                break;
//
//        }

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