package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.UriSmallAdapter;
import com.fahadandroid.groupchat.adapters.UsersAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.http.Url;

public class ManageRequestsActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvGroupName;
    ImageButton goBack, btnMore;
    RecyclerView recyclerUsers;
    PopupMenu dropDownMenu;
    List<String> usersList;
    Uri fileUri;
    UriSmallAdapter uriAdapter;
    List<Uri> uriList;
    StorageReference storageReference;
    GroupsModel groupsModel, myGroup;
    List<UserModel> userModels ;
    Button btnGoToChat;
    DatabaseReference groupsRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_requests);
        tvGroupName = findViewById(R.id.tv_group_name);
        goBack = findViewById(R.id.goBack);
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        groupsRef = FirebaseDatabase.getInstance().getReference("groups");
        userModels = new ArrayList<>();
        btnMore = findViewById(R.id.btnMore);
        btnGoToChat = findViewById(R.id.btnGoToChat);
        btnGoToChat.setOnClickListener(this);
        btnMore.setOnClickListener(this);
        uriList = new ArrayList<>();
        usersList = new ArrayList<>();
        goBack.setOnClickListener(this);
        recyclerUsers = findViewById(R.id.recycler_users);
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        String groupId = getIntent().getExtras().getString("groupId");
        getGroupData(groupId);

        dropDownMenu = new PopupMenu(this, btnMore);
        final Menu menu = dropDownMenu.getMenu();
        menu.add(0, 0, 0, "Delete Group");
        menu.add(0, 1, 0, "Edit Group");
        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case 0:
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageRequestsActivity.this);
                        builder.setTitle("Delete Group ?");
                        builder.setMessage("Are you sure you want to delete this group ?");
                        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if (groupId!=null){
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("deleted", true);
                                    groupsRef.child(myGroup.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(ManageRequestsActivity.this);
                                            builder.setTitle("Group deleted");
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                    finish();
                                                }
                                            });
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }
                                    });
                                }
                            }
                        });
                        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        return true;

                    case 1:
                        final String[] selectedCategory = {""};
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ManageRequestsActivity.this);
                        View view = LayoutInflater.from(ManageRequestsActivity.this).inflate(R.layout.new_group_dialog, null);
                        TextView tvHeading = view.findViewById(R.id.heading);
                        tvHeading.setText("Update Group");
                        EditText etName = view.findViewById(R.id.etName);
                        EditText etPincode = view.findViewById(R.id.etPincode);
                        CardView btnPdf = view.findViewById(R.id.btnPdf);
                        TextView tvSelectedCategories = view.findViewById(R.id.selectedCategories);
                        tvSelectedCategories.setVisibility(View.VISIBLE);
                        btnPdf.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                intent.setType("application/pdf");
                                startActivityForResult(intent, 1234);
                            }
                        });
                        RecyclerView recyclerItems = view.findViewById(R.id.recycler_items);
                        recyclerItems.setLayoutManager(new LinearLayoutManager(ManageRequestsActivity.this,
                                LinearLayoutManager.HORIZONTAL, false));
                        uriAdapter = new UriSmallAdapter(uriList, ManageRequestsActivity.this);
                        recyclerItems.setAdapter(uriAdapter);

                        MultiSpinnerSearch singleSpinnerSearch = view.findViewById(R.id.singleItemSelectionSpinner);
                        List<String> selectedItems = new ArrayList<>();
                        List<KeyPairBoolData> keyPairBoolDataList = new ArrayList<>();
                        keyPairBoolDataList.add(new KeyPairBoolData("Accomodation", false));
                        keyPairBoolDataList.add(new KeyPairBoolData("Food", false));
                        keyPairBoolDataList.add(new KeyPairBoolData("Classes", false));
                        keyPairBoolDataList.add(new KeyPairBoolData("Others", false));
                        if (myGroup!=null){
                            etName.setText(myGroup.getName());
                            etPincode.setText(myGroup.getPincode()+"");
                            if (myGroup.getFileUrls()!=null){
                                String cats = "Selected Categories= "+myGroup.getCategoryList().toString().replace(", ", ",").replaceAll("[\\[.\\]]", "");
                                tvSelectedCategories.setText(cats);
                                for (int a = 0; a<myGroup.getFileUrls().size(); a++){
                                    try {
                                        URL url = new URL(myGroup.getFileUrls().get(a));
                                        Uri uri = Uri.parse( url.toURI().toString() );
                                        uriList.add(uri);
                                    } catch (MalformedURLException e1) {
                                        e1.printStackTrace();
                                    } catch (URISyntaxException e) {
                                        e.printStackTrace();
                                    }
                                }
                                uriAdapter.notifyDataSetChanged();
                            }
                        }
                        singleSpinnerSearch.setItems(keyPairBoolDataList, new MultiSpinnerListener() {
                            @Override
                            public void onItemsSelected(List<KeyPairBoolData> items) {
                                for (int i = 0; i < items.size(); i++) {
                                    if (items.get(i).isSelected()) {
                                        selectedItems.add(items.get(i).getName());
                                    }
                                }
                                selectedCategory[0] = selectedItems.toString();
                                String cats = "Selected Categories= "+selectedItems.toString().replace(", ", ",").replaceAll("[\\[.\\]]", "");
                                tvSelectedCategories.setText(cats);
                            }
                        });

                        Button btnSave = view.findViewById(R.id.btnSave);
                        builder1.setView(view);
                        AlertDialog alertDialog1 = builder1.create();
                        btnSave.setText("Update");
                        btnSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String name = etName.getText().toString();
                                String pincodeSting = etPincode.getText().toString();
                                if (fileUri!=null){
                                    updateGroup(name, pincodeSting, selectedItems, alertDialog1);
                                }else {
//                                    int pincode = Integer.parseInt(pincodeSting);
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("name", name);
                                    map.put("pincode", pincodeSting);
                                    if (selectedItems.size()>0){
                                        map.put("categoryList", selectedItems);
                                    }
                                    groupsRef.child(myGroup.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            alertDialog1.dismiss();
                                            getGroupData(myGroup.getKey());
                                            Toast.makeText(ManageRequestsActivity.this, "Group date updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                        alertDialog1.show();

                        return true;

                }
                return false;
            }
        });
        btnMore.setOnTouchListener(dropDownMenu.getDragToOpenListener());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234){
            try {
                fileUri = data.getData();
            }catch (Exception e){}
        }
    }

    private void getGroupData(String groupId){
        userModels.clear();
        usersList.clear();
        DatabaseReference groupsRef = FirebaseDatabase.getInstance().getReference("groups");
        groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                    groupsModel = snapshot.getValue(GroupsModel.class);
                    groupsModel.setKey(snapshot.getKey());
                    if (groupsModel.getKey().equals(groupId)){
                        myGroup = groupsModel;
                        if (groupsModel.getPendingMembers()!=null){
                            usersList.addAll(groupsModel.getPendingMembers());
                        }
                    }
                }
                for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
                    if (EUGroupChat.userModelList.get(a).getUid()!=null){
                        for (int i = 0; i<usersList.size(); i++){
                            if (EUGroupChat.userModelList.get(a).getUid().equals(usersList.get(i))){
                                userModels.add(EUGroupChat.userModelList.get(a));
                            }
                        }
                    }
                }
                tvGroupName.setText(myGroup.getName());
                UsersAdapter adapter = new UsersAdapter(userModels, ManageRequestsActivity.this);
                recyclerUsers.setAdapter(adapter);
                RecycleClick.addTo(recyclerUsers).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int a, View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(ManageRequestsActivity.this);
                        builder.setTitle(userModels.get(a).getFirstName()+ " "+userModels.get(a).getSurName());
                        builder.setMessage("Are you sure you want " + userModels.get(a).getFirstName() + " to join this group ?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                try {
                                    List<String> pendingMembers = new ArrayList<>();
                                    List<String> approvedMembers = new ArrayList<>();
                                    if (groupsModel.getPendingMembers()!=null){
                                        pendingMembers = groupsModel.getPendingMembers();
                                        if (pendingMembers.contains(userModels.get(a).getUid())){
                                            pendingMembers.remove(userModels.get(a).getUid());
                                        }
                                    }
                                    if (groupsModel.getApprovedMembers()!=null){
                                        approvedMembers = groupsModel.getApprovedMembers();
                                        if (!approvedMembers.contains(userModels.get(a).getUid())){
                                            approvedMembers.add(userModels.get(a).getUid());
                                        }
                                    }else {
                                        approvedMembers.add(userModels.get(a).getUid());
                                    }
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("approvedMembers", approvedMembers);
                                    map.put("pendingMembers", pendingMembers);
                                    groupsRef.child(groupId).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            getGroupData(groupId);
                                        }
                                    });
                                }catch (Exception e){}

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
        }else if (view.getId()==R.id.btnMore){
            dropDownMenu.show();
        }else if (view.getId()==R.id.btnGoToChat){
            if (myGroup!=null){
                Intent intent = new Intent(ManageRequestsActivity.this, ChatActivity.class);
                intent.putExtra("group", myGroup.getKey());
                startActivity(intent);
            }
        }
    }
    private void updateGroup(String name, String pincodeSting, List<String> selectedItems, AlertDialog alertDialog){
        if (!name.isEmpty()&&!pincodeSting.isEmpty()&&selectedItems.size()>0&&uriList!=null){

            if (selectedItems.size()!=uriList.size()){
                Toast.makeText(ManageRequestsActivity.this, "You must attach a file for each category", Toast.LENGTH_SHORT).show();
                return;
            }

            final ProgressDialog progressDialog = new ProgressDialog(ManageRequestsActivity.this);
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
                                    Map<String, Object> map = new HashMap<>();
                                    map.put("name", name);
                                    map.put("pincode", pincodeSting);
                                    map.put("fileUrls", urls);
                                    if (selectedItems.size()>0){
                                        map.put("categoryList", selectedItems);
                                    }
                                    groupsRef.child(myGroup.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            alertDialog.dismiss();
                                            getGroupData(myGroup.getKey());
                                            Toast.makeText(ManageRequestsActivity.this, "Group date updated", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        }else {
            Toast.makeText(ManageRequestsActivity.this, "Full Data required", Toast.LENGTH_SHORT).show();
        }
    }
}