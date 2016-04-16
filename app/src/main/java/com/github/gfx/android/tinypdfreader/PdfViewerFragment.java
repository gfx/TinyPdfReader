package com.github.gfx.android.tinypdfreader;

import com.github.gfx.android.tinypdfreader.databinding.FragmentPdfViewerBinding;

import android.content.res.Configuration;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;

import hugo.weaving.DebugLog;
import uk.co.senab.photoview.PhotoViewAttacher;


public class PdfViewerFragment extends Fragment {

    private static final String kPdfFile = "pdf_file";

    private static final String kPageIndex = "page_index";

    private boolean reversed = false;

    private File pdfFile;

    private int position;

    private PdfRenderer pdfRenderer;

    private FragmentPdfViewerBinding binding;

    public PdfViewerFragment() {
        // Required empty public constructor
    }

    public static PdfViewerFragment newInstance(File pdfFile) {
        PdfViewerFragment fragment = new PdfViewerFragment();
        Bundle args = new Bundle();
        args.putSerializable(kPdfFile, pdfFile);
        fragment.setArguments(args);
        return fragment;
    }

    @DebugLog
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pdfRenderer = createPdfRenderer(savedInstanceState != null ? savedInstanceState : getArguments());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PdfPagerAdapter adapter = new PdfPagerAdapter(getContext(), pdfRenderer);
        adapter.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
            @Override
            public void onViewTap(View view, float x, float y) {
                handleTap(x, y);
            }
        });

        binding = FragmentPdfViewerBinding.inflate(inflater, container, false);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setReversed(reversed);

        if (savedInstanceState != null) {
            position = pageIndexToPosition(savedInstanceState.getInt(kPageIndex));
        }

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        pdfRenderer.close();
        super.onDestroyView();
    }

    @DebugLog
    @Override
    public void onResume() {
        super.onResume();
        hideSystemUI();
        binding.viewPager.setCurrentItem(position);
    }

    @DebugLog
    @Override
    public void onPause() {
        showSystemUI();
        super.onPause();
    }

    @DebugLog
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(kPdfFile, pdfFile);
        outState.putInt(kPageIndex, positionToPageIndex(binding.viewPager.getCurrentItem()));
    }

    PdfRenderer createPdfRenderer(Bundle bundle) {
        pdfFile = (File) bundle.getSerializable(kPdfFile);
        try {
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            return new PdfRenderer(fd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    boolean isPortrait() {
        return getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }

    @DebugLog
    int positionToPageIndex(int position) {
        if (!isPortrait()) {
            return position;
        } else {
            if (position == 0) {
                return 0;
            } else {
                return position * 2 - 1;
            }
        }
    }

    @DebugLog
    int pageIndexToPosition(int pageIndex) {
        if (isPortrait()) {
            return pageIndex;
        } else {
            if (pageIndex == 0) {
                return 0;
            } else {
                return (pageIndex + 1) / 2;
            }
        }
    }

    @DebugLog
    boolean handleTap(float x, float y) {
        float thirdX = binding.viewPager.getWidth() / 3.0f;

        if (x < thirdX) {
            return binding.viewPager.arrowScroll(View.FOCUS_LEFT);
        } else if (x > 2 * thirdX) {
            return binding.viewPager.arrowScroll(View.FOCUS_RIGHT);
        }
        toggleSystemUi();
        return true;
    }

    /**
     * To hide system UI and dive into IMMERSIVE mode.
     *
     * <blockquote>
     * If you're building a book reader, news reader, or a magazine, use the IMMERSIVE flag in conjunction with
     * SYSTEM_UI_FLAG_FULLSCREEN and SYSTEM_UI_FLAG_HIDE_NAVIGATION. Because users may want to access the action bar and other
     * UI controls somewhat frequently, but not be bothered with any UI elements while flipping through content, IMMERSIVE is
     * a
     * good option for this use case.
     * </blockquote>
     *
     * @see <a href="http://developer.android.com/intl/ja/training/system-ui/immersive.html">Using Immersive Full-Screen
     * Mode</a>
     */
    private void hideSystemUI() {
        binding.getRoot().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    private void showSystemUI() {
        binding.getRoot().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    private void toggleSystemUi() {
        if (checkFrag(binding.getRoot().getSystemUiVisibility(), View.SYSTEM_UI_FLAG_IMMERSIVE)) {
            showSystemUI();
        } else {
            hideSystemUI();
        }
    }

    private boolean checkFrag(int vec, int flag) {
        return (vec & flag) == flag;
    }
}
