package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.GroupsAdapter;
import com.fahadandroid.groupchat.models.BusinessList;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class JoinGroupActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton goBack;
    RecyclerView recyclerGroups;
    BusinessList businessList;
    FirebaseAuth mAuth;
    TextView tvBusinessName, tvNoData;
    FirebaseDatabase firebaseDatabase;
    List<GroupsModel> groupsModelList;
    List<String> groupKeys;
    DatabaseReference groupsRef;
    ProgressBar progressBar;
    boolean isCoountryCordinator = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupKeys = new ArrayList<>();
        tvNoData = findViewById(R.id.tvNoData);
        progressBar = findViewById(R.id.progress);
        tvBusinessName = findViewById(R.id.tvBusinessName);
        groupsRef = firebaseDatabase.getReference("groups");
        businessList = getIntent().getParcelableExtra("businessList");
        isCoountryCordinator = getIntent().getBooleanExtra("isCoountryCordinator", false);
        tvBusinessName.setText(businessList.getName());
        recyclerGroups = findViewById(R.id.recycler_groups);
        recyclerGroups.setLayoutManager(new LinearLayoutManager(this));
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        getGroupsList();
    }

    private void getGroupsList(){
        groupsModelList = new ArrayList<>();
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        Query query = groupsRef.orderByChild("businessKey").equalTo(businessList.getKey());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        GroupsModel groupsModel = snapshot.getValue(GroupsModel.class);
                        if (groupsModel.getKey()==null){
                            groupsModel.setKey(snapshot.getKey());
                        }
                        if (!groupsModel.isDeleted()){
                            boolean alreadyApplied = false;
                            boolean alreadyJoined = false;
                            if (groupsModel.getKey()==null){
                                groupsModel.setKey(snapshot.getKey());
                            }
                            List<String> pendingMembers = new ArrayList<>();
                            List<String> approvedMembers = new ArrayList<>();
                            if (groupsModel.getPendingMembers()!=null){
                                pendingMembers = groupsModel.getPendingMembers();
                                if (pendingMembers.contains(mAuth.getCurrentUser().getUid())){
                                    alreadyApplied = true;
                                }else {
                                    alreadyApplied = false;
                                }
                            }
                            if (groupsModel.getApprovedMembers()!=null){
                                approvedMembers = groupsModel.getApprovedMembers();
                                if (approvedMembers.contains(mAuth.getCurrentUser().getUid())){
                                    alreadyJoined = true;
                                }else {
                                    alreadyJoined = false;
                                }
                            }
                            if (alreadyJoined){
                                groupsModel.setJoined("yes");
                            }else if (alreadyApplied&&!alreadyJoined){
                                groupsModel.setJoined("pending");
                            }else {
                                groupsModel.setJoined("no");
                            }
                            groupsModelList.add(groupsModel);
                        }
                    }
                    if (groupsModelList.size()>0){
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        tvNoData.setVisibility(View.GONE);
                        progressBar.setVisibility(View.GONE);
                        GroupsAdapter adapter = new GroupsAdapter(groupsModelList,
                                JoinGroupActivity.this, true);
                        recyclerGroups.setAdapter(adapter);
                        RecycleClick.addTo(recyclerGroups).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                                if (isCoountryCordinator){
                                    Intent intent = new Intent(JoinGroupActivity.this, ChatActivity.class);
                                    intent.putExtra("group",groupsModelList.get(i).getKey());
                                    startActivity(intent);
                                }else {
                                    if (groupsModelList.get(i).getJoined()!=null){
                                        if (groupsModelList.get(i).getJoined().equals("yes")){
                                            Intent intent = new Intent(JoinGroupActivity.this,
                                                    ChatActivity.class);
                                            intent.putExtra("group",groupsModelList.get(i).getKey());
                                            startActivity(intent);
                                        }
                                    }
                                }

                            }
                        });
                    }else {
                        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        tvNoData.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                }catch (Exception e){
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                tvNoData.setVisibility(View.VISIBLE);
                tvNoData.setText(error.getMessage());
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.goBack){
            finish();
        }
    }
}