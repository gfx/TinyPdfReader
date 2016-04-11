package com.github.gfx.android.tinypdfreader;

import com.github.gfx.android.tinypdfreader.databinding.FragmentPdfViewerBinding;

import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.io.File;
import java.io.IOException;


public class PdfViewerFragment extends Fragment {

    private static final String kPdfFile = "pdf_file";

    private boolean reversed = false;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        File pdfFile = (File) getArguments().getSerializable(kPdfFile);

        try {
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(fd);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        binding = FragmentPdfViewerBinding.inflate(inflater, container, false);
        PagerAdapter adapter = new PdfPagerAdapter(getContext(), pdfRenderer);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setReversed(reversed);
        return binding.getRoot();
    }


    @Override
    public void onDestroyView() {
        pdfRenderer.close();

        super.onDestroyView();
    }
}
