package com.fahadandroid.groupchat.adminFragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidbuts.multispinnerfilter.KeyPairBoolData;
import com.androidbuts.multispinnerfilter.MultiSpinnerListener;
import com.androidbuts.multispinnerfilter.MultiSpinnerSearch;
import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.activities.GroupsActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.BusinessListAdapter;
import com.fahadandroid.groupchat.adapters.UriSmallAdapter;
import com.fahadandroid.groupchat.models.BusinessList;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.Random;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddGroupsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddGroupsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    FirebaseAuth mAuth;
    String selectedCategory;
    Uri fileUri;
    UriSmallAdapter uriAdapter;
    List<Uri> uriList;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    RecyclerView recyclerBusinessList;
    List<BusinessList> businessListList;
    List<String> businesKeys;
    DatabaseReference businessListRef, groupsRef;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddGroupsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddGroupsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddGroupsFragment newInstance(String param1, String param2) {
        AddGroupsFragment fragment = new AddGroupsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private void getBusinessLists(){
        BusinessListAdapter adapter = new BusinessListAdapter(businessListList, getActivity(), false);
        recyclerBusinessList.setAdapter(adapter);
        RecycleClick.addTo(recyclerBusinessList).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.new_group_dialog, null);
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
                recyclerItems.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                uriAdapter = new UriSmallAdapter(uriList, requireContext());
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
                        saveNewGroup(businessListList.get(i), name, pincodeSting, selectedItems, alertDialog);
//                        if (!name.isEmpty()&&!pincodeSting.isEmpty()&&selectedItems.size()>0&&fileUri!=null){
//                            final ProgressDialog progressDialog = new ProgressDialog(getActivity());
//                            progressDialog.setMessage("Please wait....");
//                            progressDialog.setCancelable(false);
//                            progressDialog.setCanceledOnTouchOutside(false);
//                            progressDialog.show();
//                            File file = new File(fileUri.toString());
//                            storageReference.child(file.getName()).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                                @Override
//                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                                    storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                        @Override
//                                        public void onSuccess(Uri uri) {
//                                            progressDialog.dismiss();
//                                            String url = uri.toString();
//                                            GroupsModel groupsModel = new GroupsModel(System.currentTimeMillis(), businessListList.get(i).getKey(),
//                                                    name, mAuth.getCurrentUser().getUid(), pincodeSting, selectedItems);
//                                            groupsModel.setFileUrl(url);
//                                            groupsModel.setCategory(selectedCategory);
//                                            String key = groupsRef.push().getKey();
//                                            groupsModel.setKey(key);
//                                            groupsRef.child(key).setValue(groupsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    alertDialog.dismiss();
//                                                    Intent intent = new Intent(getActivity(), GroupsActivity.class);
//                                                    intent.putExtra("businessList", businessListList.get(i));
//                                                    startActivity(intent);
//                                                }
//                                            });
//                                        }
//                                    });
//                                }
//                            });
//
//                        }else {
//                            Toast.makeText(getActivity(), "Please provide full data", Toast.LENGTH_SHORT).show();
//                        }
                    }
                });
                alertDialog.show();
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
                        businessListList.add(businessList);
                        businesKeys.add(businessList.getKey());
                        adapter.notifyDataSetChanged();
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    BusinessList businessList = snapshot.getValue(BusinessList.class);
                    if (!businessList.isDeleted()){
                        if (businessList.getKey()==null){
                            businessList.setKey(snapshot.getKey());
                        }
                        int index = businesKeys.indexOf(businessList.getKey());
                        businessListList.set(index, businessList);
                        adapter.notifyDataSetChanged();
                    }else {
                        if (businessList.getKey()==null){
                            businessList.setKey(snapshot.getKey());
                        }
                        int index = businesKeys.indexOf(businessList.getKey());
                        businessListList.remove(index);
                        adapter.notifyDataSetChanged();
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
    }

    private int getRandomNumber(int min,int max) {
        return (new Random()).nextInt((max - min) + 1) + min;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_groups, container, false);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupsRef = firebaseDatabase.getReference("groups");
        businessListRef = firebaseDatabase.getReference("businessList");
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        businessListList = new ArrayList<>();
        uriList = new ArrayList<>();
        recyclerBusinessList = view.findViewById(R.id.recycler_businessLists);
        recyclerBusinessList.setLayoutManager(new LinearLayoutManager(getActivity()));
        businesKeys = new ArrayList<>();
        getBusinessLists();
        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234){
            fileUri = data.getData();
            uriList.add(fileUri);
            uriAdapter.notifyDataSetChanged();
        }
    }

    private void openSelectionDialog(List<String> selectedItems, TextView tvSelectCategories){
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View v = LayoutInflater.from(requireContext()).inflate(R.layout.selection_dialog, null);
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


    private void saveNewGroup(BusinessList businessList, String name, String pincodeSting, List<String> selectedItems, AlertDialog alertDialog){
        if (!name.isEmpty()&&!pincodeSting.isEmpty()&&selectedItems.size()>0&&uriList!=null){

            if (selectedItems.size()!=uriList.size()){
                Toast.makeText(requireContext(), "You must attach a file for each category", Toast.LENGTH_SHORT).show();
                return;
            }

            final ProgressDialog progressDialog = new ProgressDialog(requireContext());
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
                                            Intent intent = new Intent(getActivity(), GroupsActivity.class);
                                            intent.putExtra("businessList", businessList);
                                            startActivity(intent);
                                        }
                                    });
                                }
                            }
                        });
                    }
                });
            }
        }else {
            Toast.makeText(requireContext(), "Full Data required", Toast.LENGTH_SHORT).show();
        }
    }

}