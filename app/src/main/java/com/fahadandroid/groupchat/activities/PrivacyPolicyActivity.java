package com.fahadandroid.groupchat.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.fahadandroid.groupchat.R;
import com.github.barteksc.pdfviewer.PDFView;

public class PrivacyPolicyActivity extends AppCompatActivity {

    PDFView pdfView;
    ImageButton goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy_policy);
        pdfView = findViewById(R.id.pdfView);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        pdfView.fromAsset("privacy_notice.pdf")
                .enableSwipe(true) /* allows to block changing pages using swipe*/
                .swipeHorizontal(false)
                .enableDoubletap(true)
                .load();
    }
}