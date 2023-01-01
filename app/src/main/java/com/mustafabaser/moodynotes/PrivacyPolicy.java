package com.mustafabaser.moodynotes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.mustafabaser.moodynotes.authentication.Register;

public class PrivacyPolicy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.Privacy_Policy);
        String url = "https://docs.mustafabaser.net/moody-notes";

        WebView webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("http:") || url.startsWith("https:")) {
                    return false;
                }

                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
                return true;
            }
        });
        webView.loadUrl(url);
    }

    //Back-button
    public boolean onSupportNavigateUp(){
        Intent intent = new Intent(PrivacyPolicy.this, Register.class);
        startActivity(intent);
        this.finish();
        return true;
    }

    public void onBackPressed() {
        WebView webView = (WebView) findViewById(R.id.webView);
        if (webView.canGoBack()) { // If previous page exists it will go to previous page.
            webView.goBack();
        } else { // If the previous page does not exist, it will close the application or return to the previous activity.
            super.onBackPressed();
        }
    }
}