package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.NotificationsAdapter;
import com.fahadandroid.groupchat.models.NotificationsModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity {

    RecyclerView recyclerNotifications;
    NotificationsAdapter adapter;
    ImageButton goBack;
    List<NotificationsModel> notificationsModelList;
    List<String> notKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);
        goBack = findViewById(R.id.goBack);
        notificationsModelList = new ArrayList<>();
        notKeys = new ArrayList<>();
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerNotifications = findViewById(R.id.recycler_notifications);
        recyclerNotifications.setLayoutManager(new LinearLayoutManager(this));
        getNotifications();
    }

    private void getNotifications(){
        adapter = new NotificationsAdapter(notificationsModelList, this);
        recyclerNotifications.setAdapter(adapter);
        RecycleClick.addTo(recyclerNotifications).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                if (notificationsModelList.get(i).getTitle().equals("News Added")){
                    Intent intent = new Intent(NotificationsActivity.this, NewsDetailActivity.class);
                    intent.putExtra("news", notificationsModelList.get(i).getDataUid());
                    startActivity(intent);
                }else if (notificationsModelList.get(i).getTitle().equals("Place Added")) {
                    Intent intent = new Intent(NotificationsActivity.this, PlacesDetailsActivity.class);
                    intent.putExtra("place", notificationsModelList.get(i).getDataUid());
                    startActivity(intent);
                }
            }
        });
        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
        notificationsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    NotificationsModel notificationsModel = snapshot.getValue(NotificationsModel.class);
                    notificationsModel.setKey(snapshot.getKey());
                    notificationsModelList.add(0, notificationsModel);
                    notKeys.add(0, snapshot.getKey());
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    NotificationsModel notificationsModel = snapshot.getValue(NotificationsModel.class);
                    notificationsModel.setKey(snapshot.getKey());
                    int index = notKeys.indexOf(snapshot.getKey());
                    notificationsModelList.set(index, notificationsModel);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    int index = notKeys.indexOf(snapshot.getKey());
                    notificationsModelList.remove(index);
                    notKeys.remove(index);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}