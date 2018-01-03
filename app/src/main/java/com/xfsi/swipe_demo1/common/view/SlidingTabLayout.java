package com.xfsi.swipe_demo1.common.view;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.TextView;

import com.xfsi.swipe_demo1.R;

/**
 * Created by local-kieu on 1/18/16.
 * main purposes: create scrollview with SlidingTabStrip linearLayout
 extends HorizontalScrollView
 1 interface, 3 constants, 6 fields, 3 constructors
 9 meths (6 pub,1 pro,2 priv); 1 override
 2 classes
 */
public class SlidingTabLayout extends HorizontalScrollView {

    /******** 1 interface 3 constants, 6 fields, 3 constructors */

    /*** control colors drawn in the tab layout. */
    public interface TabColorizer {
        int getIndicatorColor(int pos);
        int getDividerColor(int pos);
    }
    /*** 3 constants */
    private static final int TITLE_OFF_dips = 24;
    private static final int TV_PADDING_dips = 16;
    private static final int TV_TEXT_SIZE_sp = 12;
    /*** 6 fields */
    private int mTitleOff;
    private int mTVLayoutId;
    private int mTVTxId;

    private ViewPager mVPager;
    private ViewPager.OnPageChangeListener mVPPageChangeListener;
    private final SlidingTabStrip mTabStrip;
    /*** 3 constructors */
    public SlidingTabLayout(Context context) { this(context, null);}
    public SlidingTabLayout(Context context, AttributeSet attrs) { this(context, attrs, 0);}
    public SlidingTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        /* View.setHorizontalScrollBarEnabled(b)
				Define whether the horizontal scrollbar should be drawn or not. */
        setHorizontalScrollBarEnabled(false); // Disable the Scroll Bar

        /* ScrollView.setFillViewPort(b)	Indicates this HorizontalScrollView whether
				it should stretch its content width to fill the viewport or not */
        setFillViewport(true);  //	Make sure that the Tab Strips fills this View

        mTitleOff = (int) (TITLE_OFF_dips * getResources().getDisplayMetrics().density);
        mTabStrip = new SlidingTabStrip(context);
        addView(mTabStrip, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    /********** 9 meths (6 pub,1 pro,2 priv); 1 override */

    /*** 6 public meths */
    /*** 3 set meths - just call the same methods in SlidingTabStrip class to make them public */
    /* If you only require simple custmisation then you can use
    * {@link #setSelectedIndicatorColors(int...)} and {@link #setDividerColors(int...)} to achieve
    * similar effects. */
    public void setCustomTabColorizer(TabColorizer tabColorizer) {
        mTabStrip.setCustomTabColorizer(tabColorizer);
    }
    /* Sets the colors to be used for indicating the selected tab. These colors are treated as a
    * circular array. Providing one color will mean that all tabs are indicated with the same color. */
    public void setSelectedIndicatorColors(int... colors) {
        mTabStrip.setSelectedIndicatorColors(colors);
    }
    /* Sets the colors to be used for tab dividers. These colors are treated as a circular array.
     * Providing one color will mean that all tabs are indicated with the same color. */
    public void setDividerColors(int... colors) {
        mTabStrip.setDividerColors(colors);
    }
    /*** 2 meths - set global fields */
    /* Set the {@link ViewPager.OnPageChangeListener}. When using {@link SlidingTabLayout} you are
    * required to set any {@link ViewPager.OnPageChangeListener} through this method. This is so
    * that the layout can update it's scroll position correctly. */
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mVPPageChangeListener = listener;
    }
    // Set the custom layout to be inflated for the tab views.
    public void setCustomTabView(int layoutResId, int txId) {
        mTVLayoutId = layoutResId;
        mTVTxId = txId;
    }

    /* Sets the associated view pager. Note that the assumption here is that the pager content
    * (number of tabs and tab titles) does not change after this call has been made. */
    public void setViewPager(ViewPager vPager) {
        mTabStrip.removeAllViews();

        mVPager = vPager;
        if (vPager != null) {
            vPager.setOnPageChangeListener(new InternalViewPagerListener());
        }
    }
    /*** 1 protected method - Create a default view to be used for tabs.
     This is called if a custom tab view is not set.
     set properties for TextView - gravity,size,typeface,BG,Allcaps,paddings */
    protected TextView createDefaultTabView(Context context) {
        TextView tx = new TextView(context);
        tx.setGravity(Gravity.CENTER);
        tx.setTextSize(TypedValue.COMPLEX_UNIT_SP, TV_TEXT_SIZE_sp);
        tx.setTypeface(Typeface.DEFAULT_BOLD);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            /* If we're running on Honeycomb or newer, then we can use the Theme's
       	selectableItemBackground to ensure that the View has a pressed state
       	to hold resource data - outValue.(data,resourceId,density,string,type,...) */
            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            tx.setBackgroundResource(outValue.resourceId);
            // this doesn't change the color when scrolling, it's still blue
            //tx.setBackgroundResource(R.color.colorAccent);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            // If we're running on ICS or newer, enable all-caps to match the Action Bar tab style
            tx.setAllCaps(true);
        }
        int padding = (int) (TV_PADDING_dips * getResources().getDisplayMetrics().density);
        tx.setPadding(padding, padding, padding, padding);

        return tx;
    }

    /*** 2 private methods */

    /* uses in setViewPager(..)
	    PagerAdapter  ViewPager.getAdapter()	Retrieve the current adapter supplying pages
	    use data from the adapter to populate the tab strip. */
    private void populateTabStrip() {
        final PagerAdapter adapter = mVPager.getAdapter();
        final View.OnClickListener tabClickListener = new TabClickListener();

        for (int i=0; i < adapter.getCount(); i++) {
            View tabV = null;
            TextView tabTitleV = null;

            if (mTVLayoutId != 0) {
                // If there is a custom tab view layout id set, try and inflate it
                tabV = LayoutInflater.from(getContext()).inflate(mTVLayoutId, mTabStrip, false);
                tabTitleV = (TextView) tabV.findViewById(mTVTxId);
            }
            if (tabV == null) { tabV = createDefaultTabView(getContext()); }
            if (tabTitleV == null && TextView.class.isInstance(tabV)) {tabTitleV = (TextView)tabV; }

            mTabStrip.addView(tabV);
        }
    }
    /* use in onAttachedWindow() and other places */
    private void scrollToTab(int tabidx, int posOff) {
        final int tabSCCount = mTabStrip.getChildCount();
        if (tabSCCount == 0 || tabidx < 0 || tabidx >= tabSCCount) { return; }

        View selChild = mTabStrip.getChildAt(tabidx);
        if (selChild != null) {
            int targetScrollX = selChild.getLeft() + posOff;
            if (tabidx > 0 || posOff > 0) {
                // If we're not at the first child and are mid-scroll, make sure we obey the offset
                targetScrollX -= mTitleOff;
            }
            scrollTo(targetScrollX, 0);
        }
    }

    /*** 1 Override */
    @Override protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if (mVPager != null) { scrollToTab(mVPager.getCurrentItem(), 0);}
    }

    /********** 2 classes */

    private class InternalViewPagerListener implements ViewPager.OnPageChangeListener {
        private int mScrollState;

        @Override public void onPageScrolled(int pos, float posOff, int posOffPx) {
            int tabSCCount = mTabStrip.getChildCount();
            if ((tabSCCount == 0) || (pos < 0) || (pos >= tabSCCount)) { return;}

            mTabStrip.onViewPagerPageChanged(pos, posOff);

            View selTitle = mTabStrip.getChildAt(pos);
            int extraOff = (selTitle != null) ? (int) (posOff * selTitle.getWidth()) : 0;
            scrollTo(pos, extraOff);

            if (mVPPageChangeListener != null) {
                mVPPageChangeListener.onPageScrolled(pos, posOff, posOffPx);
            }
        }

        @Override public void onPageScrollStateChanged(int state) {
            mScrollState = state;
            if ( mVPPageChangeListener != null) {
                mVPPageChangeListener.onPageScrollStateChanged(state);
            }
        }

        @Override public void onPageSelected(int pos) {
            if (mScrollState == ViewPager.SCROLL_STATE_IDLE) {
                mTabStrip.onViewPagerPageChanged(pos, 0f);
                scrollToTab(pos, 0);
            }
            if (mVPPageChangeListener != null) {
                mVPPageChangeListener.onPageSelected(pos);
            }
        }
    }

    private class TabClickListener implements View.OnClickListener {
        @Override public void onClick(View v) {
            for (int i=0; i < mTabStrip.getChildCount(); i++) {
                if (v== mTabStrip.getChildAt(i)) {
                    mVPager.setCurrentItem(i);
                    return;
                }
            }
        }
    }
}
