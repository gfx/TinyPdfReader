package com.github.gfx.android.tinypdfreader;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

public class PdfViewPager extends ViewPager {

    public PdfViewPager(Context context) {
        super(context);
    }

    public PdfViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    // to workaround ViewGroup+PhotoView problems
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        try {
            return super.onInterceptTouchEvent(ev);
        } catch (IllegalArgumentException e) {
            Log.w("PdfViewPager", "onInterceptTouchEvent", e);
            return false;
        }
    }
}
