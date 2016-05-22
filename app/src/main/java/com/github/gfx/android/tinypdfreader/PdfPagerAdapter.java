package com.github.gfx.android.tinypdfreader;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.pdf.PdfRenderer;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PdfPagerAdapter extends PagerAdapter {

    static final String TAG = PdfPagerAdapter.class.getSimpleName();

    final Context context;

    final PdfRenderer pdfRenderer;

    final int count;

    final boolean portrait;

    private PhotoViewAttacher.OnViewTapListener onViewTapListener;

    public PdfPagerAdapter(Context context, PdfRenderer pdfRenderer) {
        this.context = context;
        this.pdfRenderer = pdfRenderer;
        this.portrait = isPortrait();
        if (portrait) {
            this.count = pdfRenderer.getPageCount();
        } else {
            this.count = (int) Math.ceil(pdfRenderer.getPageCount() / 2.0f);
        }
        Log.d(TAG, "portrait=" + portrait);
    }

    boolean isPortrait() {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        long t0 = System.currentTimeMillis();

        PhotoView photoView = new PhotoView(context);
        photoView.setOnViewTapListener(onViewTapListener);

        if (portrait || position == 0) {
            new RenderBitmapTask(pdfRenderer, photoView).execute(position);
        } else {
            int pageLeft = position * 2 - 1;
            int pageRight = pageLeft + 1;
            new RenderBitmapTask(pdfRenderer, photoView).execute(pageLeft, pageRight);
        }
        container.addView(photoView);

        Log.d(TAG, "instantiateItem: " + position + " in " + (System.currentTimeMillis() - t0) + "ms");
        return photoView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    public void setOnViewTapListener(PhotoViewAttacher.OnViewTapListener onViewTapListener) {
        this.onViewTapListener = onViewTapListener;
    }

}
