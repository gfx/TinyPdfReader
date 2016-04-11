package com.github.gfx.android.tinypdfreader;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements UrlLoaderFragment.ResultListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    File pdfFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri uri = Uri.parse("http://www.jssec.org/dl/android_securecoding.pdf");
        //Uri uri = Uri.parse("https://github.com/googlesamples/android-PdfRendererBasic/raw/master/Application/src/main/assets/sample.pdf");
        pdfFile = new File(getCacheDir(), "file.pdf");

        if (!pdfFile.exists()) {
            UrlLoaderFragment urlLoaderFragment = UrlLoaderFragment.newInstance(uri, pdfFile);
            urlLoaderFragment.show(getSupportFragmentManager(), R.id.content);
        } else {
            showPdf(pdfFile);
        }
    }

    @UiThread
    @Override
    public void onLoadFailure(IOException exception) {
        Log.wtf(TAG, exception);
    }

    @UiThread
    @Override
    public void onLoadSuccess(Response response) {
        showPdf(pdfFile);
    }

    @UiThread
    void showPdf(File pdfFile) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, PdfViewerFragment.newInstance(pdfFile))
                .commit();
    }
}
