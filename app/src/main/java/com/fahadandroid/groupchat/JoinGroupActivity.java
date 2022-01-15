package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.GroupsAdapter;
import com.fahadandroid.groupchat.models.BusinessList;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class JoinGroupActivity extends AppCompatActivity implements View.OnClickListener{
    ImageButton goBack;
    RecyclerView recyclerGroups;
    BusinessList businessList;
    FirebaseAuth mAuth;
    TextView tvBusinessName;
    FirebaseDatabase firebaseDatabase;
    List<GroupsModel> groupsModelList;
    List<String> groupKeys;
    DatabaseReference groupsRef;
    boolean isCoountryCordinator = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_group);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupsModelList = new ArrayList<>();
        groupKeys = new ArrayList<>();
        tvBusinessName = findViewById(R.id.tvBusinessName);
        groupsRef = firebaseDatabase.getReference("groups");
        businessList = getIntent().getParcelableExtra("businessList");
        isCoountryCordinator = getIntent().getBooleanExtra("isCoountryCordinator", false);
        tvBusinessName.setText(businessList.getName());
        recyclerGroups = findViewById(R.id.recycler_groups);
        recyclerGroups.setLayoutManager(new LinearLayoutManager(this));
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        getGroups();
    }
    private void getGroups(){
        GroupsAdapter adapter = new GroupsAdapter(groupsModelList, this, true);
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
                            Intent intent = new Intent(JoinGroupActivity.this, ChatActivity.class);
                            intent.putExtra("group",groupsModelList.get(i).getKey());
                            startActivity(intent);
                        }
                    }
                }

            }
        });
        groupsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
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
                        if (groupsModel.getBusinessKey().equals(businessList.getKey())){
                            groupsModelList.add(groupsModel);
                            groupKeys.add(groupsModel.getKey());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    GroupsModel groupsModel = snapshot.getValue(GroupsModel.class);
                    if (groupsModel.getKey()==null){
                        groupsModel.setKey(snapshot.getKey());
                    }
                    if (!groupsModel.isDeleted()){
                        if (groupsModel.getBusinessKey().equals(businessList.getKey())){
                            int index = groupKeys.indexOf(groupsModel.getKey());
                            groupsModelList.set(index, groupsModel);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try{
                    String key = snapshot.getKey();
                    int index = groupKeys.indexOf(key);
                    groupsModelList.remove(index);
                    groupKeys.remove(key);
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
    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.goBack){
            finish();
        }
    }
}