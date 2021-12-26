package com.fahadandroid.groupchat;

import androidx.appcompat.app.AppCompatActivity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.github.barteksc.pdfviewer.PDFView;

public class WebViewActivity extends AppCompatActivity {

    ImageButton goBack;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        String uriString = getIntent().getStringExtra("url");
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pdfView = findViewById(R.id.webView);
        pdfView.fromUri(Uri.parse(uriString)).load();
    }
}