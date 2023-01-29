package com.fahadandroid.groupchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.StringSelectAdapter;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    ImageButton goBack;
    RecyclerView recyclerSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        goBack = findViewById(R.id.goBack);
        recyclerSettings = findViewById(R.id.recycler_my_docs);
        recyclerSettings.setLayoutManager(new LinearLayoutManager(this));
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        List<String> stringList = new ArrayList<>();
        stringList.add("Notifications");
        stringList.add("Rate us");
        stringList.add("Language Preference");
        stringList.add("Sign out");
        StringSelectAdapter selectAdapter = new StringSelectAdapter(stringList, this);
        recyclerSettings.setAdapter(selectAdapter);
    }
}