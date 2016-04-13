package com.github.gfx.android.tinypdfreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.support.v4.view.PagerAdapter;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class PdfPagerAdapter extends PagerAdapter {

    static final String TAG = PdfPagerAdapter.class.getSimpleName();

    final Context context;

    final PdfRenderer pdfRenderer;

    private PhotoViewAttacher.OnViewTapListener onViewTapListener;

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

        PhotoView imageView = new PhotoView(context);
        imageView.setOnViewTapListener(onViewTapListener);
        imageView.setZoomTransitionDuration(200);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        imageView.setLayoutParams(params);
        container.addView(imageView);

        new LoadBitmapTask(pdfRenderer, imageView).execute(position);

        Log.d(TAG, "instantiateItem: " + position + " in " + (System.currentTimeMillis() - t0) + "ms");
        return imageView;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
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
        }

        @Override
        protected Bitmap doInBackground(Integer... params) {
            int position = params[0];

            try(PdfRenderer.Page page = pdfRenderer.openPage(position)) {
                Bitmap bitmap = Bitmap.createBitmap(page.getWidth() * 2, page.getHeight() * 2, Bitmap.Config.ARGB_8888);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                return bitmap;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            targetView.setImageBitmap(bitmap);
        }
    }
}
