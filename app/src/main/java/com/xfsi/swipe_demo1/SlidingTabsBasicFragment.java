package com.xfsi.swipe_demo1;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xfsi.swipe_demo1.common.logger.Log;
import com.xfsi.swipe_demo1.common.view.SlidingTabLayout;

/**
 * Created by local-kieu on 1/20/16.
 * extends Fragment
 * Main purpose: inflate fragment_sample, then find ViewPager, STLayout,
 *      then set adapter to view pager, set view pager to STLayout.
 * - 2 fields - SlidingTabLayout, ViewPager
 * - 2 @Override - onCreateView, onViewCreate
 * - 1 class - PagerAdapter - 5 @Override meths
 *      getCount, isViewFromObject, getPageTitle, destroyItem
 *      instantiateItem
 *      - inflate pager_item view, add to container, find textview in pager_item view, and set text
 */
public class SlidingTabsBasicFragment extends Fragment {
    static final String LOG_TAG = "SlidingTabsBasicFragment";

    /******** - 2 fields */
    private SlidingTabLayout mSTLayout;
    private ViewPager mViewPager;

    // inflate the fragment_sample
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle icicle) {
        return inflater.inflate(R.layout.fragment_sample, container, false);
    }

    // find ViewPager, STLayout, set adapter to viewpager, set View pager to STLayout
    @Override public void onViewCreated(View v, Bundle icicle) {
        mViewPager = (ViewPager) v.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());
        SlidingTabLayout mSTLayout = (SlidingTabLayout) v.findViewById(R.id.sliding_tabs);
        mSTLayout.setViewPager(mViewPager);
    }

    /********** - 1 class - PagerAdapter - 5 @Override meths
     *      getCount, isViewFromObject, getPageTitle, instantiateItem, destroyItem
     */
    class SamplePagerAdapter extends PagerAdapter {
        @Override public int getCount() { return 10;}
        @Override public boolean isViewFromObject( View v, Object o) { return v==o; }
        @Override public CharSequence getPageTitle(int pos) { return "Item " + (pos+1);}
        @Override public void destroyItem(ViewGroup container, int pos, Object obj) {
            container.removeView((View) obj);
            Log.i(LOG_TAG, "destroyItem() [position: " + pos + "]");
        }

        // inflate pager_item view, add to container, find textview in pager_item view, and settext
        @Override public Object instantiateItem(ViewGroup container, int pos) {
            View v = getActivity().getLayoutInflater().inflate(R.layout.pager_item, container, false);
            container.addView(v);
            TextView tx = (TextView)  v.findViewById(R.id.item_title);
            tx.setText(String.valueOf(pos +1));

            Log.i (LOG_TAG, "InstantiateItem() [position: " + pos + "]");
            return v;
        }
    }
}
