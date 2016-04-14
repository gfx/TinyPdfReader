package com.github.gfx.android.tinypdfreader;

import com.github.gfx.android.tinypdfreader.downloader.UrlLoaderFragment;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements UrlLoaderFragment.ResultListener {

    static final Uri uri = Uri.parse("http://www.jssec.org/dl/android_securecoding.pdf");
    //static final Uri uri = Uri.parse("https://github.com/googlesamples/android-PdfRendererBasic/raw/master/Application/src/main/assets/sample.pdf");

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            UrlLoaderFragment.newInstance(uri)
                    .show(getSupportFragmentManager(), R.id.content);
        }
    }

    @UiThread
    @Override
    public void onLoadFailure(IOException exception) {
        Log.wtf(TAG, exception);
    }

    @UiThread
    @Override
    public void onLoadSuccess(Response response, File pdfFile) {
        showPdf(pdfFile);
    }

    @UiThread
    void showPdf(File pdfFile) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content, PdfViewerFragment.newInstance(pdfFile))
                .commit();
    }
}
