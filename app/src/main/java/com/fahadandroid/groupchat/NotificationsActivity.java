package com.fahadandroid.groupchat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.fahadandroid.groupchat.adapters.NotificationsAdapter;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView recyclerNotifications;
    NotificationsAdapter adapter;
    ImageButton goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerNotifications = findViewById(R.id.recycler_notifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NotificationsAdapter();
        recyclerNotifications.setAdapter(adapter);
    }
}