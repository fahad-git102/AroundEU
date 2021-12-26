package com.fahadandroid.groupchat.adminFragments;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.CountriesAdapter;
import com.fahadandroid.groupchat.models.CountryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class AddCountryFragment extends Fragment implements View.OnClickListener{

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    RecyclerView recyclerCountries;
    List<CountryModel> countryModelList;
    List<String> countryKeys;
    FloatingActionButton btnAdd;
    DatabaseReference countriesRef;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddCountryFragment() {
        // Required empty public constructor
    }

    public static AddCountryFragment newInstance(String param1, String param2) {
        AddCountryFragment fragment = new AddCountryFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_country, container, false);
        recyclerCountries = view.findViewById(R.id.recycler_countries);
        countriesRef = FirebaseDatabase.getInstance().getReference("countries");
        recyclerCountries.setLayoutManager(new LinearLayoutManager(getActivity()));
        btnAdd = view.findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        countryKeys = new ArrayList<>();
        countryModelList = new ArrayList<>();
        getCountryList();
        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnAdd){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            View v = LayoutInflater.from(getActivity()).inflate(R.layout.add_country_dialog, null);
            EditText etCountryName = v.findViewById(R.id.etCountryName);
            EditText etPincode = v.findViewById(R.id.etPincode);
            ProgressBar progress = v.findViewById(R.id.progress);
            Button btnSave = v.findViewById(R.id.btnJoin);
            builder.setView(v);
            AlertDialog alertDialog = builder.create();
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String name = etCountryName.getText().toString();
                    String pincode = etPincode.getText().toString();
                    if (name.isEmpty()||pincode.isEmpty()){
                        Toast.makeText(getActivity(), "Full data required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    progress.setVisibility(View.VISIBLE);
                    CountryModel countryModel = new CountryModel(name, pincode);
                    countriesRef.push().setValue(countryModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progress.setVisibility(View.GONE);
                            alertDialog.dismiss();
                            Toast.makeText(getActivity(), "Country Added !", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            alertDialog.show();
        }
    }
    private void getCountryList(){
        CountriesAdapter adapter = new CountriesAdapter(countryModelList, getActivity(), true);
        recyclerCountries.setAdapter(adapter);
        countriesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CountryModel countryModel = snapshot.getValue(CountryModel.class);
                    countryModel.setKey(snapshot.getKey());
                    if (!countryModel.isDeleted()){
                        countryModelList.add(countryModel);
                        countryKeys.add(countryModel.getKey());
                    }
                    adapter.notifyDataSetChanged();
                }catch (Exception e){
                    Toast.makeText(getActivity(), ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CountryModel countryModel = snapshot.getValue(CountryModel.class);
                    countryModel.setKey(snapshot.getKey());
                    int index = countryKeys.indexOf(countryModel.getKey());
                    if (!countryModel.isDeleted()){
                        countryModelList.set(index, countryModel);
                    }else {
                        countryModelList.remove(index);
                    }
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    int index = countryKeys.indexOf(snapshot.getKey());
                    countryModelList.remove(index);
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