package com.fahadandroid.groupchat.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.StringSelectAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.HelperClass;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    ImageButton goBack;
    RecyclerView recyclerProfileOptions;
    TextView tvJoiningDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        goBack = findViewById(R.id.goBack);
        tvJoiningDate = findViewById(R.id.tvJoiningDate);
        if (EUGroupChat.currentUser!=null){
            tvJoiningDate.setText(HelperClass.getFormattedDateTime(EUGroupChat.currentUser.getJoinedOn(),
                    "dd MMM, yyyy"));
        }
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        recyclerProfileOptions = findViewById(R.id.recycler_profile_options);
        recyclerProfileOptions.setLayoutManager(new LinearLayoutManager(this));
        List<String> stringList = new ArrayList<>();
        stringList.add("Personal Information");
        stringList.add("My Documents");
        stringList.add("My Classes");
        stringList.add("Settings");
        StringSelectAdapter adapter = new StringSelectAdapter(stringList, this);
        recyclerProfileOptions.setAdapter(adapter);
        RecycleClick.addTo(recyclerProfileOptions).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                switch (i){
                    case 0:
                        Intent intent = new Intent(ProfileActivity.this, PersonalInfoActivity.class);
                        startActivity(intent);
                        break;
                    case 1:
                        Intent intent1 = new Intent(ProfileActivity.this, MyDocumentsActivity.class);
                        startActivity(intent1);
                        break;
                    case 2:

                        break;
                    case 3:
                        Intent intent2 = new Intent(ProfileActivity.this, SettingsActivity.class);
                        startActivity(intent2);
                        break;
                }
            }
        });
    }
}