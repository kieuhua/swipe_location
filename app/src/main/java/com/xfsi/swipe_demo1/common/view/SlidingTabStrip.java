package com.xfsi.swipe_demo1.common.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by local-kieu on 1/18/16.
 * 2.2 view/SlidingTabStrip.java
 the main purpose is to override View.onDraw()
 drawRect thick at selected child
 drawRect thin to all
 drawLine between child

 extends LinearLayout
 7 constants, 9 primitive fields, 2 compound fields
 2 constructors(1 long), 6 private meths
 1 Override View.onDraw()
 1 class (implement TabColorizer)
 */
public class SlidingTabStrip extends LinearLayout {
    /* BB=BOTTOM_BORDER(thick, color)
	SI=SELECTED_INDICATOR(thick, color)
	D=DIVIDER(thick, color, height)
	T=thick, P=paint, C=color, H= height, Off=offset */

    /*** 7 Constants for BB(2), SI(2), D(3) */
    private static final int DBBT_dips = 2;
    private static final byte DBBC_alpha = 0x26;
    private static final int SIT_dips = 8;
    //private static final int DSI_color = 0xFF33B5E5;
    private static final int DSI_color = 0xFF33B000;
    private static final int DDT_dips = 1;
    private static final byte DDC_alpha = 0x20;
    private static final float DD_height = 0.5f;

    /*** 9 primitives fields(7 finals) */
    private final int mBBT;
    private final Paint mBBP;
    private final int mDBBC;
    private final int mSIT;
    private final Paint mSIP;
    private final Paint mDP;
    private final float mDH;

    private int mSelPos;
    private float mSelOff;

    /*** 2 compound fields( 1 final) */
    private SlidingTabLayout.TabColorizer mCTColorizer;
    private final SimpleTabColorier mDTColorizer;


    /************* 2 constructors(1 long), 6 private meths */

    /*** 2 constructors */
    SlidingTabStrip(Context context) { this(context, null);}

    /** initiaLize - noDraw, density, typeface, themeForegroundColor
     set DefaultTabColorier - Indicator, divider
     set Bottom Border, sel-indicator, divider for thickness, paint, color
     */
    SlidingTabStrip(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false); //bcs you draw the view yourself
        final float density = getResources().getDisplayMetrics().density;

        // get the theme foreground color, so we can set it on BB, DD
        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.colorForeground, outValue, true);
        final int themeFGC = outValue.data;

        mDBBC = setColorAlpha(themeFGC, DBBC_alpha);
        mDTColorizer = new SimpleTabColorier();
        mDTColorizer.setIndicatorColors(DSI_color);
        mDTColorizer.setDividerColors(setColorAlpha(themeFGC, DDC_alpha));

        mBBT = (int) (DBBT_dips * density);
        mBBP = new Paint();
        mBBP.setColor(mDBBC);

        mSIT = (int) (SIT_dips * density);
        mSIP = new Paint();

        mDH = DD_height;
        mDP = new Paint();
        mDP.setStrokeWidth((int) (DDT_dips * density));
    }

    /*** 6 private methods - intialize fields */
    void setCustomTabColorizer(SlidingTabLayout.TabColorizer cTColorizer) {
        mCTColorizer = cTColorizer;
        invalidate();
    }
    void setSelectedIndicatorColors(int... colors) {
        // Make sure that the custom colorizer is removed
        mCTColorizer = null;
        mDTColorizer.setIndicatorColors(colors);
        invalidate();
    }
    void setDividerColors(int... colors) {
        // Make sure that the custom colorizer is removed
        mCTColorizer = null;
        mDTColorizer.setDividerColors(colors);
        invalidate();
    }
    void onViewPagerPageChanged(int pos, float posOff) {
        mSelPos = pos;
        mSelOff = posOff;
        invalidate();
    }
    private static int setColorAlpha(int c, byte alpha) {
        return Color.argb(alpha, Color.red(c), Color.green(c), Color.blue(c));
    }

    private static int blendColors(int c1, int c2, float ratio) {
        final float inverseRation = 1f - ratio;
        float r = (Color.red(c1) * ratio) + (Color.red(c2) * inverseRation);
        float g = (Color.green(c1) * ratio) + (Color.green(c2) * inverseRation);
        float b = (Color.blue(c1) * ratio) + (Color.blue(c2) * inverseRation);
        return Color.rgb((int)r, (int)g, (int)b);
    }

    /*********** 1 Override View.onDraw() */
    @Override protected void onDraw(Canvas canvas) {
        final int h = getHeight();  // tab strip height
        final int cCount = getChildCount();
        // the most value is tab strip height
        final int dividerHPx = (int) (Math.min(Math.max(0f, mDH), 1f) * h);
        final SlidingTabLayout.TabColorizer tColorizer = mCTColorizer != null ? mCTColorizer : mDTColorizer;

        // Thick colored underline below the current selection
        if (cCount > 0) {
            View selTitle = getChildAt(mSelPos);
            int l = selTitle.getLeft();
            int r = selTitle.getRight();
            int c = tColorizer.getIndicatorColor(mSelPos);

            // the selected tab inside strip, so mix neighbor's color, and draw partway
            if (mSelOff> 0f && mSelPos < (getChildCount() -1)) {
                // mix neighbor's color
                int nextC = tColorizer.getIndicatorColor(mSelPos + 1);
                if (c != nextC) {
                    c = blendColors(nextC, c, mSelOff);
                }

                // Draw the selection partway between the tabs
                View nextT = getChildAt(mSelPos +1);
                l = (int) (mSelOff * nextT.getLeft() + (1.0f - mSelOff) * l);
                r = (int) (mSelOff * nextT.getRight() + (1.0f - mSelOff) * r);
            }
            mSIP.setColor(c);
            canvas.drawRect(l, h - mSIT, r, h, mSIP);
        }
    }

    /*********** 1 class (implement TabColorizer) */
    private static class SimpleTabColorier implements SlidingTabLayout.TabColorizer {
        private int[] mIColors;
        private int[] mDColors;

        @Override public final int getIndicatorColor(int pos) {
            return mIColors[pos % mIColors.length];
        }
        @Override public final int getDividerColor(int pos) {
            return mDColors[pos % mDColors.length];
        }
        void setIndicatorColors(int... colors) {
            mIColors = colors;
        }
        void setDividerColors(int... colors) {
            mDColors = colors;
        }
    }
}
