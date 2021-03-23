package com.vnrvjiet.tsraksha;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;


public class GoLayout extends Fragment {
    private String url, title;
    private TextView pdfTitle;
    private ImageButton pdfClose;
    private WebView pdfWebView;
    private LoadingBar loadingBar;

    public GoLayout(String url, String title) {
        this.url = url;
        this.title = title;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_go_layout, container, false);
        Context context = getContext();
        Application.getInstance().initAppLanguage(context);
        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        initializeFields(view);
        setWebView();
        pdfClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFragmentManager().popBackStackImmediate();
            }
        });
        return view;
    }

    private void setWebView() {
        pdfTitle.setText(title);
        pdfWebView.loadUrl(url);
        pdfWebView.getSettings().setJavaScriptEnabled(true);
        pdfWebView.getSettings().setBuiltInZoomControls(true);
        pdfWebView.getSettings().setDisplayZoomControls(false);
        pdfWebView.setWebChromeClient(new WebChromeClient());
        loadingBar.dismissLoadingBar();
        pdfWebView.setWebViewClient(new WebViewClient() {
                                        @Override
                                        public void onPageStarted(WebView view, String url, Bitmap favicon) {
                                            super.onPageStarted(view, url, favicon);
                                            loadingBar.showLoadingBar(1);
                                        }

                                        @Override
                                        public void onPageFinished(WebView view, String url) {
                                            super.onPageFinished(view, url);
                                            pdfWebView.loadUrl("javascript:(function() { " +
                                                    "document.querySelector('[role=\"toolbar\"]').remove();})()");
                                            loadingBar.dismissLoadingBar();
                                        }
                                    }
        );
    }

    private void initializeFields(View view) {
        pdfTitle = view.findViewById(R.id.pdf_title);
        pdfClose = view.findViewById(R.id.pdf_close_button);
        pdfWebView = view.findViewById(R.id.pdf_web_view);
        loadingBar = new LoadingBar(getActivity());
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ((AppCompatActivity) getActivity()).getSupportActionBar().show();
    }
}
