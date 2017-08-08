package com.example.aasir.reddit.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.aasir.reddit.R;

/**
 * Created by Aasir on 8/6/2017.
 */

public class WebViewActivity  extends AppCompatActivity{

    private static final String TAG = "WebViewActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);

        WebView webView = (WebView) findViewById(R.id.webView);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.webViewProgressBar);
        TextView loadingText = (TextView) findViewById(R.id.loadingText);
        progressBar.setVisibility(View.VISIBLE);

        Intent incomingIntent = getIntent();
        String url = incomingIntent.getStringExtra("url");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);

        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progressBar.setVisibility(View.GONE);
                loadingText.setText("");
            }
        });

    }
}
