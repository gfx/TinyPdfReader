package com.github.gfx.android.tinypdfreader;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.UiThread;
import android.support.v7.app.AppCompatActivity;

import java.io.File;
import java.io.IOException;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements UrlLoaderFragment.ResultListener {

    File pdfFile;

    UrlLoaderFragment urlLoaderFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Uri uri = Uri.parse("http://www.jssec.org/dl/android_securecoding.pdf");
        //String url = "https://github.com/googlesamples/android-PdfRendererBasic/raw/master/Application/src/main/assets/sample.pdf";
        pdfFile = new File(getCacheDir(), "file.pdf");

        if (!pdfFile.exists()) {
            urlLoaderFragment = UrlLoaderFragment.newInstance(uri, pdfFile);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.content, urlLoaderFragment)
                    .commit();

        } else {
            showPdf(pdfFile);
        }
    }

    void dismissUrlLoaderFragment() {
        getSupportFragmentManager().beginTransaction()
                .remove(urlLoaderFragment)
                .commit();
    }

    @UiThread
    @Override
    public void onLoadFailure(IOException exception) {
        dismissUrlLoaderFragment();
    }

    @UiThread
    @Override
    public void onLoadSuccess(Response response) {
        dismissUrlLoaderFragment();
        showPdf(pdfFile);
    }


    @UiThread
    void showPdf(File pdfFile) {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.content, PdfViewerFragment.newInstance(pdfFile))
                .commit();
    }
}
