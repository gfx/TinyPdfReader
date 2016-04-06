package com.github.gfx.android.tinypdfreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import uk.co.senab.photoview.PhotoView;

public class PdfPagerAdapter extends PagerAdapter {

    final Context context;

    final PdfRenderer pdfRenderer;

    public PdfPagerAdapter(Context context, PdfRenderer pdfRenderer) {
        this.context = context;
        this.pdfRenderer = pdfRenderer;
    }

    @Override
    public int getCount() {
        return pdfRenderer.getPageCount();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        long t0 = System.currentTimeMillis();

        PhotoView view = new PhotoView(context);

        try(PdfRenderer.Page page = pdfRenderer.openPage(position)) {
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            );
            view.setLayoutParams(params);

            Bitmap bitmap = Bitmap.createBitmap(page.getWidth() * 2, page.getHeight() * 2, Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            view.setImageBitmap(bitmap);
        }

        container.addView(view);

        Log.d("PdfPagerAdapter", "instantiateItem: " + position + " in " + (System.currentTimeMillis() - t0) + "ms");
        return view;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }
}
