package com.github.gfx.android.tinypdfreader;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Arrays;

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
    }

    static int toFrame(int n) {
        return n * 1000 / 60;
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
            new LoadBitmapTask(pdfRenderer, photoView).execute(position);
        } else {
            int pageLeft = position * 2 - 1;
            int pageRight = pageLeft + 1;
            new LoadBitmapTask(pdfRenderer, photoView).execute(pageLeft, pageRight);
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

    private static class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {

        final PdfRenderer pdfRenderer;

        final ImageView targetView;

        public LoadBitmapTask(PdfRenderer pdfRenderer, ImageView targetView) {
            this.pdfRenderer = pdfRenderer;
            this.targetView = targetView;
            targetView.setAlpha(0.0f);
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            long startTime = System.currentTimeMillis();

            Bitmap bitmap = null;
            for (int i = 0; i < params.length; i++) {
                int position = params[i];
                try (PdfRenderer.Page page = pdfRenderer.openPage(position)) {
                    int w = page.getWidth() * 2;
                    int h = page.getHeight() * 2;

                    if (bitmap == null) {
                        bitmap = Bitmap.createBitmap(w * params.length, h, Bitmap.Config.ARGB_8888);
                    }
                    int offsetX = i * w;
                    Rect rect = new Rect(offsetX, 0, offsetX + w, h);
                    page.render(bitmap, rect, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                }
            }
            Log.d(TAG, "pages " + Arrays.toString(params) + " loaded in " + (System.currentTimeMillis() - startTime) + "ms");
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            targetView.setImageBitmap(bitmap);
            targetView.animate()
                    .alpha(1.0f)
                    .setDuration(toFrame(20))
                    .start();
        }
    }
}
