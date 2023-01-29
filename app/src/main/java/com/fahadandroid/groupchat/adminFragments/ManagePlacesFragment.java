package com.fahadandroid.groupchat.adminFragments;

import android.content.Intent;
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

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.activities.ApprovedPlacesAdminActivity;
import com.fahadandroid.groupchat.activities.PlacesDetailsActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.PlacesAdapter;
import com.fahadandroid.groupchat.models.PlacesModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManagePlacesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManagePlacesFragment extends Fragment {

    RecyclerView recyclerPlaces;
    DatabaseReference placesRef;
    List<String> placesKeys;
    Button btnApprovedPlaces;
    List<PlacesModel> placesModelList;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ManagePlacesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ManagePlacesFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ManagePlacesFragment newInstance(String param1, String param2) {
        ManagePlacesFragment fragment = new ManagePlacesFragment();
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
        View view = inflater.inflate(R.layout.fragment_manage_places, container, false);
        placesRef = FirebaseDatabase.getInstance().getReference("places");
        recyclerPlaces = view.findViewById(R.id.recycler_places);
        recyclerPlaces.setLayoutManager(new LinearLayoutManager(getActivity()));
        placesModelList = new ArrayList<>();
        placesKeys = new ArrayList<>();
        getPendingPlaces();
        btnApprovedPlaces = view.findViewById(R.id.btnApprovedPlaces);
        btnApprovedPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), ApprovedPlacesAdminActivity.class));
            }
        });
        return view;
    }

    private void getPendingPlaces(){
        PlacesAdapter adapter = new PlacesAdapter(placesModelList, getActivity(), false);
        recyclerPlaces.setAdapter(adapter);
        RecycleClick.addTo(recyclerPlaces).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(getActivity(), PlacesDetailsActivity.class);
                intent.putExtra("place", placesModelList.get(i).getKey());
                intent.putExtra("isAdmin", true);
                startActivity(intent);
            }
        });
        placesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    PlacesModel placesModel = snapshot.getValue(PlacesModel.class);
                    placesModel.setKey(snapshot.getKey());
                    if (placesModel.getStatus()!=null){
                        if (placesModel.getStatus().equals("pending")){
                            placesModelList.add(0, placesModel);
                            placesKeys.add(0, placesModel.getKey());
                            adapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    PlacesModel placesModel = snapshot.getValue(PlacesModel.class);
                    placesModel.setKey(snapshot.getKey());
                    if (placesModel.getStatus()!=null){
                        if (placesModel.getStatus().equals("pending")){
                            int index = placesKeys.indexOf(placesModel.getKey());
                            placesModelList.set(index, placesModel);
                            adapter.notifyDataSetChanged();
                        }else {
                            int index = placesKeys.indexOf(placesModel.getKey());
                            placesModelList.remove(index);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

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