package com.fahadandroid.groupchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.github.barteksc.pdfviewer.PDFView;

public class PdfViewerActivity extends AppCompatActivity {

    PDFView pdfView;
    ImageButton goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_viewer);
        goBack = findViewById(R.id.goBack);
        pdfView = findViewById(R.id.webView);
        String uriString = getIntent().getStringExtra("type");
        if (uriString.equals("cultural")){
            pdfView.fromAsset("cultural_activities.pdf").load();
        }else if (uriString.equals("general")){
            pdfView.fromAsset("general_info.pdf").load();
        }else if (uriString.equals("excursion")){
            pdfView.fromAsset("excursion_merged.pdf").load();
        }
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}