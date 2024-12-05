package com.example.eventapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.eventapp.R;

public class ConfirmedRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        WebView webView = findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // Ovde možete implementirati logiku za praćenje klika na link
                if (url.contains("verify")) {
                    // Link vodi ka verifikaciji, tada možete preduzeti odgovarajuće akcije
                    // Na primer, označiti korisnika kao verifikovanog
                    return true; // Vraćanje true za sprečavanje otvaranja linka u eksternom browseru
                } else {
                    return super.shouldOverrideUrlLoading(view, url);
                }
            }
        });


       // webView.loadUrl(verificationLink);
    }
}