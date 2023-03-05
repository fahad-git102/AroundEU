package com.fahadandroid.groupchat.activities;

import static com.fahadandroid.groupchat.helpers.Constants.unreadGroupsList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.BusinessListAdapter;
import com.fahadandroid.groupchat.models.BusinessList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SelectBusinessListActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerBusinessList;
    List<BusinessList> businessListList ;
    List<String> businesKeys;
    ImageButton goBack;
    boolean isCordinatorCountry;
    private List<String> unReadBusinessList;
    BusinessListAdapter adapter;
    TextView tvLogout;
    DatabaseReference businessListRef, groupsRef;
    String countryOfCordinator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_business_list);
        mAuth = FirebaseAuth.getInstance();
        goBack = findViewById(R.id.goBack);
        unReadBusinessList = new ArrayList<>();
        isCordinatorCountry = getIntent().getBooleanExtra("cordinatorCountry", false);
        if (isCordinatorCountry){
            SharedPreferences prfs = getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE);
            countryOfCordinator = prfs.getString("country", "");
        }
        tvLogout = findViewById(R.id.tvLogout);
        tvLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(SelectBusinessListActivity.this);
                builder.setTitle("Log Out");
                builder.setMessage("Are you sure you want to Log out ?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE).edit().clear().apply();
                        mAuth.signOut();
                        startActivity(new Intent(SelectBusinessListActivity.this, LoginActivity.class));
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupsRef = firebaseDatabase.getReference("groups");
        businessListRef = firebaseDatabase.getReference("businessList");
        String countryKey = getIntent().getExtras().getString("country");
        businessListList = new ArrayList<>();
        recyclerBusinessList = findViewById(R.id.recycler_businessLists);
        recyclerBusinessList.setLayoutManager(new LinearLayoutManager(this));
        businesKeys = new ArrayList<>();

        if (isCordinatorCountry){
            SharedPreferences prfs = getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE);
            countryKey = prfs.getString("countryKey", "");
        }

        manageUnreadMessages(countryKey);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null){
            updateUnRead();
        }
    }

    private void updateUnRead(){
        try {
            unReadBusinessList = new ArrayList<>();
            if (unreadGroupsList!=null&&unreadGroupsList.size()>0){
                for(String item : unreadGroupsList){
                    FirebaseDatabase.getInstance().getReference("groups").child(item).child("businessKey")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    try {
                                        String value = snapshot.getValue(String.class);
                                        unReadBusinessList.add(value);
                                        if (unReadBusinessList.size()==unreadGroupsList.size()){
                                            adapter.updateUnReadList(unReadBusinessList);
                                        }
                                    }catch (Exception e){

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }else {
                adapter.updateUnReadList(unReadBusinessList);
            }
        }catch (Exception e){
            if (adapter!=null&&unReadBusinessList!=null){
                adapter.updateUnReadList(unReadBusinessList);
            }
        }
    }

    private void manageUnreadMessages(String countryKey){
        try {
            unReadBusinessList = new ArrayList<>();
            if (unreadGroupsList!=null&&unreadGroupsList.size()>0){
                for(String item : unreadGroupsList){
                    FirebaseDatabase.getInstance().getReference("groups").child(item).child("businessKey")
                            .addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    try {
                                        String value = snapshot.getValue(String.class);
                                        unReadBusinessList.add(value);
                                        if (unReadBusinessList.size()==unreadGroupsList.size()){
                                            getBusinessLists(countryKey);
                                        }
                                    }catch (Exception e){

                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
            }else {
                getBusinessLists(countryKey);
            }
        }catch (Exception e){
            Toast.makeText(this, "Please check your network.", Toast.LENGTH_SHORT).show();
        }
    }

    private void getBusinessLists(String countryKey){
        try {
            adapter = new BusinessListAdapter(businessListList, this, false, unReadBusinessList);
            recyclerBusinessList.setAdapter(adapter);
            RecycleClick.addTo(recyclerBusinessList).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                    Intent intent = new Intent(SelectBusinessListActivity.this, JoinGroupActivity.class);
                    intent.putExtra("businessList", businessListList.get(i));
                    intent.putExtra("isCoountryCordinator", isCordinatorCountry);
                    startActivity(intent);
                }
            });
            businessListRef.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try{
                        BusinessList businessList = snapshot.getValue(BusinessList.class);
                        if (businessList.getKey()==null){
                            businessList.setKey(snapshot.getKey());
                        }
                        if (!businessList.isDeleted()){
                            if (businessList.getCountryId()!=null){
                                if (businessList.getCountryId().equals(countryKey)){
                                    businessListList.add(businessList);
                                    businesKeys.add(businessList.getKey());
                                    adapter.notifyDataSetChanged();
                                }

                            }
                        }

                    }catch (Exception e){}
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try {
                        BusinessList businessList = snapshot.getValue(BusinessList.class);
                        if (businessList.getKey()==null){
                            businessList.setKey(snapshot.getKey());
                        }
                        if (businessList.getCountryId()!=null) {
                            if (businessList.getCountryId().equals(countryKey)) {
                                if (!businessList.isDeleted()){
                                    int index = businesKeys.indexOf(businessList.getKey());
                                    businessListList.set(index, businessList);
                                    adapter.notifyDataSetChanged();
                                }else {
                                    int index = businesKeys.indexOf(businessList.getKey());
                                    businessListList.remove(index);
                                    adapter.notifyDataSetChanged();
                                }
                            }
                        }

                    }catch (Exception e){}
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                    try{
                        String key = snapshot.getKey();
                        int index = businesKeys.indexOf(key);
                        businessListList.remove(index);
                        businesKeys.remove(key);
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
        }catch (Exception e){}
    }

}