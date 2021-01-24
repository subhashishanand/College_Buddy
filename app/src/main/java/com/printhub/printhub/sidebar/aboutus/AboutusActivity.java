package com.printhub.printhub.sidebar.aboutus;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.printhub.printhub.R;
import com.printhub.printhub.sidebar.aboutus.SliderAdapter;

public class AboutusActivity extends AppCompatActivity {

    private ViewPager sViewPager;
    private LinearLayout dotsLayout;
    private TextView dots[];

    SliderAdapter sliderAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_aboutus);
        sViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        dotsLayout = (LinearLayout) findViewById(R.id.dotsLayout);

        sliderAdapter = new SliderAdapter(this);
        sViewPager.setAdapter(sliderAdapter);
        sViewPager.addOnPageChangeListener(viewPagerPageChangeListener);
        addBottomDots(0);

    }

    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    public void addDotIndicator(){
        dots = new TextView[5];
        for (int i=0; i<dots.length; i++){
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8266;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.backgroundcolor));

            dotsLayout.addView(dots[i]);
        }
    }

    private void addBottomDots(int currentPage) {
        dots = new TextView[5];
        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.backgroundcolor));  // dot_inactive
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0)
            dots[currentPage].setTextColor(getResources().getColor(R.color.pink)); // dot_active
    }
}
