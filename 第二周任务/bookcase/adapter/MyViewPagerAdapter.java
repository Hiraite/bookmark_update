package com.eebbk.onlineexercise.bookcase.adapter;

import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class MyViewPagerAdapter extends PagerAdapter {

    private List<View> mListViews;

    public MyViewPagerAdapter(List<View> listViews) {
        this.mListViews = listViews;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        System.out.println("destroyItem position = " + position);
        ((ViewPager) container).removeView(mListViews.get(position));
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        System.out.println("instantiateItem position = " + position);
        ((ViewPager) container).addView(mListViews.get(position), 0);
        return mListViews.get(position);
    }

    @Override
    public int getCount() {
        return mListViews.size();
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }
}
