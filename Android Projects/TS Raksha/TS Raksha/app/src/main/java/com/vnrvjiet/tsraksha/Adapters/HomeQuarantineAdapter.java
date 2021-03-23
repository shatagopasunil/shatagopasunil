package com.vnrvjiet.tsraksha.Adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.chrisbanes.photoview.PhotoView;
import java.util.List;

public class HomeQuarantineAdapter extends PagerAdapter{
    private List<String> urls;
    private Context context;

    public HomeQuarantineAdapter(Context context,List<String> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }


    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        PhotoView photoView = new PhotoView(context);
        Glide.with(context).load(urls.get(position)).fitCenter().into(photoView);
        container.addView(photoView,0);
        return photoView;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((ImageView)object);
    }

}

