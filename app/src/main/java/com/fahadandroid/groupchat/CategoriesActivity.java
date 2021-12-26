package com.fahadandroid.groupchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.StringSelectAdapter;

import java.util.ArrayList;
import java.util.List;

public class CategoriesActivity extends AppCompatActivity {

    ImageButton goBack;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerView= findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> stringList = new ArrayList<>();
        stringList.add("General Information");
        stringList.add("Excursions");
        stringList.add("Cultural Activities");
        StringSelectAdapter stringSelectAdapter = new StringSelectAdapter(stringList, this);
        recyclerView.setAdapter(stringSelectAdapter);
        RecycleClick.addTo(recyclerView).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                switch (i){
                    case 0:
                        Intent intent = new Intent(CategoriesActivity.this, PdfViewerActivity.class);
                        intent.putExtra("type", "general");
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent2 = new Intent(CategoriesActivity.this, PdfViewerActivity.class);
                        intent2.putExtra("type", "excursion");
                        startActivity(intent2);
                        break;
                    case 2:
                        Intent intent1 = new Intent(CategoriesActivity.this, PdfViewerActivity.class);
                        intent1.putExtra("type", "cultural");
                        startActivity(intent1);
                        break;
                }
            }
        });

    }
}