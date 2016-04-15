package com.github.gfx.android.tinypdfreader;

import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.pdf.PdfRenderer;
import android.os.AsyncTask;
import android.widget.ImageView;

import hugo.weaving.DebugLog;

public class LoadBitmapTask extends AsyncTask<Integer, Void, Bitmap> {

    final PdfRenderer pdfRenderer;

    final ImageView targetView;

    public LoadBitmapTask(PdfRenderer pdfRenderer, ImageView targetView) {
        this.pdfRenderer = pdfRenderer;
        this.targetView = targetView;
        targetView.setAlpha(0.0f);
    }

    @DebugLog
    @Override
    protected Bitmap doInBackground(Integer... positions) {
        Bitmap bitmap = null;
        for (int i = 0; i < positions.length; i++) {
            int position = positions[i];
            try (PdfRenderer.Page page = pdfRenderer.openPage(position)) {
                int w = page.getWidth() * 2;
                int h = page.getHeight() * 2;

                if (bitmap == null) {
                    bitmap = Bitmap.createBitmap(w * positions.length, h, Bitmap.Config.ARGB_8888);
                }
                int offsetX = i * w;
                Rect dest = new Rect(offsetX, 0, offsetX + w, h);
                page.render(bitmap, dest, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            }
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        targetView.setImageBitmap(bitmap);
        targetView.animate().alpha(1.0f)
                .setDuration(PdfPagerAdapter.toFrame(20))
                .start();
    }
}
