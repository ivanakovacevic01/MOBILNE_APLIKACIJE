package com.example.eventapp.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ActivationHandlerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        Uri data = intent.getData();
        Log.i("EventApp","uslo");
        if (data != null) {
            String activationCode = data.getQueryParameter("code");
            if (activationCode != null && !activationCode.isEmpty()) {
                // Ovde implementirajte kod za proveru i aktivaciju naloga korisnika na osnovu aktivacionog koda
            }
        }

        // Zatim možete završiti ovu aktivnost ako je obrada završena
        finish();
    }
}