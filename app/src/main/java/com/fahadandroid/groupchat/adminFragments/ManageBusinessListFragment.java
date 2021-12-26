package com.fahadandroid.groupchat.adminFragments;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.GroupsActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.BusinessListAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.BusinessList;
import com.fahadandroid.groupchat.models.CountryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageBusinessListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageBusinessListFragment extends Fragment implements View.OnClickListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    FloatingActionButton btnAdd;
    FirebaseAuth mAuth;
    CountryModel selectedCountry;
    FirebaseDatabase firebaseDatabase;
    List<CountryModel> countryModelList;
    RecyclerView recyclerBusinessList;
    List<BusinessList> businessListList;
    List<String> businesKeys;
    DatabaseReference businessListRef, countriesRef;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManageBusinessListFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManageBusinessListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManageBusinessListFragment newInstance(String param1, String param2) {
        ManageBusinessListFragment fragment = new ManageBusinessListFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_manage_business_list, container, false);
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        countryModelList = new ArrayList<>();
        businessListRef = firebaseDatabase.getReference("businessList");
        countriesRef = firebaseDatabase.getReference("countries");
        businessListList = new ArrayList<>();
        recyclerBusinessList = view.findViewById(R.id.recycler_businessLists);
        recyclerBusinessList.setLayoutManager(new LinearLayoutManager(getActivity()));
        businesKeys = new ArrayList<>();
        getBusinessLists();
        getCountries();
        return view;
    }

    private void getBusinessLists(){
        BusinessListAdapter adapter = new BusinessListAdapter(businessListList, getActivity(), true);
        recyclerBusinessList.setAdapter(adapter);
        RecycleClick.addTo(recyclerBusinessList).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(getActivity(), GroupsActivity.class);
                intent.putExtra("businessList", businessListList.get(i));
                startActivity(intent);
            }
        });
        businessListRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    BusinessList businessList = snapshot.getValue(BusinessList.class);
                    if (!businessList.isDeleted()){
                        if (businessList.getKey()==null){
                            businessList.setKey(snapshot.getKey());
                        }
                        if (EUGroupChat.countryModelList!=null){
                            if (businessList.getCountryId()!=null){
                                for (int i = 0; i< EUGroupChat.countryModelList.size(); i++){
                                    if (EUGroupChat.countryModelList.get(i).getKey().equals(businessList.getCountryId())){
                                        if (!EUGroupChat.countryModelList.get(i).isDeleted()){
                                            businessListList.add(0, businessList);
                                            businesKeys.add(0, businessList.getKey());
                                            adapter.notifyDataSetChanged();
                                        }
                                    }
                                }
                            }
                        }
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

    private void getCountries(){
        countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    try{
                        CountryModel countryModel = dataSnapshot.getValue(CountryModel.class);
                        if (!countryModel.isDeleted()){
                            countryModel.setKey(dataSnapshot.getKey());
                            countryModelList.add(countryModel);
                        }

                    }catch (Exception e){}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnAdd){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View view1 = LayoutInflater.from(getActivity()).inflate(R.layout.add_string_data_dialog, null);
            EditText etName = view1.findViewById(R.id.etName);
            Button btnSave = view1.findViewById(R.id.btnSave);
            Spinner spinner = view1.findViewById(R.id.spinner);
            spinner.setVisibility(View.VISIBLE);
            if (countryModelList.size()>0){
                selectedCountry = countryModelList.get(0);
                ArrayAdapter<CountryModel> adapter =
                        new ArrayAdapter<>(getActivity().getApplicationContext(),  android.R.layout.simple_spinner_dropdown_item, countryModelList);
                adapter.setDropDownViewResource( android.R.layout.simple_spinner_dropdown_item);

                spinner.setAdapter(adapter);
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                        selectedCountry = countryModelList.get(i);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });
            }
            builder.setView(view1);
            AlertDialog alertDialog = builder.create();
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = etName.getText().toString();
                    if (!name.isEmpty()&&selectedCountry!=null){
                        BusinessList businessList = new BusinessList();
                        businessList.setName(name);
                        businessList.setCountryId(selectedCountry.getKey());
                        businessList.setTimeStamp(System.currentTimeMillis());
                        String key = businessListRef.push().getKey();
                        businessList.setKey(key);
                        businessListRef.child(key).setValue(businessList).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                alertDialog.dismiss();
                            }
                        });
                    }else {
                        if (TextUtils.isEmpty(name)){
                            etName.setError("Name Required");
                        }
                        if (selectedCountry==null){
                            Toast.makeText(getActivity(), "Please select a country", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            alertDialog.show();
        }
    }
}