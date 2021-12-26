package com.fahadandroid.groupchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.fahadandroid.groupchat.adapters.StringSelectAdapter;

import java.util.ArrayList;
import java.util.List;

public class MyDocumentsActivity extends AppCompatActivity {

    ImageButton goBack;
    RecyclerView recyclerDocs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_documents);
        goBack = findViewById(R.id.goBack);
        recyclerDocs = findViewById(R.id.recycler_my_docs);
        recyclerDocs.setLayoutManager(new LinearLayoutManager(this));
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        List<String> stringList = new ArrayList<>();
        stringList.add("Privacy Policy");
        stringList.add("Terms of service");
        stringList.add("Study materials");
        StringSelectAdapter selectAdapter = new StringSelectAdapter(stringList, this);
        recyclerDocs.setAdapter(selectAdapter);
    }
}