package com.rrgroup.dailysamachar;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_view);

        // Get the URL to load from the Intent
        String url = getIntent().getStringExtra("url");

        // Find the WebView and load the URL
        webView = findViewById(R.id.webView);
        webView.loadUrl(url);

        // Enable JavaScript in the WebView
        webView.getSettings().setJavaScriptEnabled(true);

        // Set a WebViewClient to handle page navigation
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}

