package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.GroupsAdapter;
import com.fahadandroid.groupchat.adapters.UriSmallAdapter;
import com.fahadandroid.groupchat.models.BusinessList;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class GroupsActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton goBack;
    FloatingActionButton btnAdd;
    RecyclerView recyclerGroups;
    Uri fileUri;
    BusinessList businessList;
    String selectedCategory ;
    FirebaseAuth mAuth;
    UriSmallAdapter uriAdapter;
    TextView tvBusinessName;
    List<Uri> uriList;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    List<GroupsModel> groupsModelList;
    List<String> groupKeys;
    DatabaseReference groupsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        groupsModelList = new ArrayList<>();
        groupKeys = new ArrayList<>();
        uriList = new ArrayList<>();
        tvBusinessName = findViewById(R.id.tvBusinessName);
        groupsRef = firebaseDatabase.getReference("groups");
        businessList = getIntent().getParcelableExtra("businessList");
        tvBusinessName.setText(businessList.getName());
        recyclerGroups = findViewById(R.id.recycler_groups);
        recyclerGroups.setLayoutManager(new LinearLayoutManager(this));
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        getGroups();
    }

    private void getGroups(){
        GroupsAdapter adapter = new GroupsAdapter(groupsModelList, this, false);
        recyclerGroups.setAdapter(adapter);
        RecycleClick.addTo(recyclerGroups).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(GroupsActivity.this, ManageRequestsActivity.class);
                intent.putExtra("groupId", groupsModelList.get(i).getKey());
                startActivity(intent);
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
                        if (groupsModel.getBusinessKey().equals(businessList.getKey())){
                            groupsModelList.add(groupsModel);
                            groupKeys.add(groupsModel.getKey());
                            adapter.notifyDataSetChanged();
                        }
                    }

                }catch (Exception e){
                    Toast.makeText(GroupsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
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
        }else if (view.getId()==R.id.btnAdd){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view1 = LayoutInflater.from(this).inflate(R.layout.new_group_dialog, null);
            TextView tvHeading = view1.findViewById(R.id.heading);
            tvHeading.setText("Add new Group");
            EditText etName = view1.findViewById(R.id.etName);
            EditText etPincode = view1.findViewById(R.id.etPincode);
            CardView btnPdf = view1.findViewById(R.id.btnPdf);
            btnPdf.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, 1234);
                }
            });
            RecyclerView recyclerItems = view1.findViewById(R.id.recycler_items);
            recyclerItems.setLayoutManager(new LinearLayoutManager(GroupsActivity.this, LinearLayoutManager.HORIZONTAL, false));
            uriAdapter = new UriSmallAdapter(uriList, GroupsActivity.this);
            recyclerItems.setAdapter(uriAdapter);
            List<String> selectedItems = new ArrayList<>();
            TextView tvSelectCategories = view1.findViewById(R.id.selectedCategories);
            tvSelectCategories.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedItems.clear();
                    openSelectionDialog(selectedItems, tvSelectCategories);
                }
            });
            Button btnSave = view1.findViewById(R.id.btnSave);
            builder.setView(view1);
            AlertDialog alertDialog = builder.create();
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = etName.getText().toString();
                    String pincodeSting = etPincode.getText().toString();
                    saveNewGroup(name, pincodeSting, selectedItems, alertDialog);
                }
            });
            alertDialog.show();
        }
    }

    private void openSelectionDialog(List<String> selectedItems, TextView tvSelectCategories){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View v = LayoutInflater.from(this).inflate(R.layout.selection_dialog, null);
        Button btnDone = v.findViewById(R.id.btnDone);
        CheckBox checkFood = v.findViewById(R.id.checkBox_food);
        CheckBox checkAccomodation = v.findViewById(R.id.checkBox_accomodation);
        CheckBox checkOthers = v.findViewById(R.id.checkBox_others);
        CheckBox checkClasses = v.findViewById(R.id.checkBox_classes);
        checkFood.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    selectedItems.add("Food");
                }else {
                    selectedItems.remove("Food");
                }
            }
        });
        checkAccomodation.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    selectedItems.add("Accommodation");
                }else {
                    selectedItems.remove("Accommodation");
                }
            }
        });
        checkClasses.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    selectedItems.add("Classes");
                }else {
                    selectedItems.remove("Classes");
                }
            }
        });
        checkOthers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    selectedItems.add("Others");
                }else {
                    selectedItems.remove("Others");
                }
            }
        });
        builder.setView(v);
        AlertDialog alertDialog = builder.create();
        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectedCategory = selectedItems.toString();
                alertDialog.dismiss();
                tvSelectCategories.setText(selectedCategory);
            }
        });
        alertDialog.show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234){
            try {
                fileUri = data.getData();
                uriList.add(fileUri);
                uriAdapter.notifyDataSetChanged();
            }catch (Exception e){}
        }
    }

    private void saveNewGroup(String name, String pincodeSting, List<String> selectedItems, AlertDialog alertDialog){
        if (!name.isEmpty()&&!pincodeSting.isEmpty()&&selectedItems.size()>0&&uriList!=null){

            if (selectedItems.size()!=uriList.size()){
                Toast.makeText(GroupsActivity.this, "You must attach a file for each category", Toast.LENGTH_SHORT).show();
                return;
            }

            final ProgressDialog progressDialog = new ProgressDialog(GroupsActivity.this);
            progressDialog.setMessage("Please wait....");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            List<String> urls = new ArrayList<>();
            for (int i = 0; i<uriList.size(); i++){
                File file = new File(fileUri.toString());
                storageReference.child(file.getName()).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();
                                urls.add(url);
                                if (urls.size()>=uriList.size()){
                                    int pincode = Integer.parseInt(pincodeSting);
                                    GroupsModel groupsModel = new GroupsModel(System.currentTimeMillis(), businessList.getKey(),
                                            name, mAuth.getCurrentUser().getUid(), String.valueOf(pincode), selectedItems);
                                    groupsModel.setFileUrls(urls);
                                    groupsModel.setCategory(selectedCategory);
                                    String key = groupsRef.push().getKey();
                                    groupsModel.setKey(key);
                                    groupsRef.child(key).setValue(groupsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss();
                                            alertDialog.dismiss();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        }else {
            Toast.makeText(GroupsActivity.this, "Full Data required", Toast.LENGTH_SHORT).show();
        }
    }

}