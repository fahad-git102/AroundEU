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

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.PlacesAdapter;
import com.fahadandroid.groupchat.models.PlacesModel;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class ApprovedPlacesAdminActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton goBack;
    RecyclerView recyclerPlaces;
    List<PlacesModel> placesModelList;
    DatabaseReference placesRef;
    PlacesAdapter adapter;
    List<String> placesKeys;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approved_places_admin);
        goBack= findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        recyclerPlaces = findViewById(R.id.recyclerPlaces);
        recyclerPlaces.setLayoutManager(new LinearLayoutManager(this));
        placesKeys = new ArrayList<>();
        placesModelList = new ArrayList<>();
        placesRef = FirebaseDatabase.getInstance().getReference("places");
        getPlaces();
    }

    private void getPlaces(){
        adapter = new PlacesAdapter(placesModelList, this, true);
        recyclerPlaces.setAdapter(adapter);
        RecycleClick.addTo(recyclerPlaces).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(ApprovedPlacesAdminActivity.this, PlacesDetailsActivity.class);
                intent.putExtra("place", placesModelList.get(i).getKey());
                intent.putExtra("delete", true);
                startActivity(intent);
            }
        });
        placesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    PlacesModel placesModel = snapshot.getValue(PlacesModel.class);
                    placesModel.setKey(snapshot.getKey());
                    placesModelList.add(0,placesModel);
                    placesKeys.add(0,placesModel.getKey());
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    PlacesModel placesModel = snapshot.getValue(PlacesModel.class);
                    placesModel.setKey(snapshot.getKey());
                    int index = placesKeys.indexOf(placesModel.getKey());
                    placesModelList.set(index, placesModel);
                    adapter.notifyDataSetChanged();
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try{
                    int index = placesKeys.indexOf(snapshot.getKey());
                    placesModelList.remove(index);
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
        if (view.getId() == R.id.goBack){
            finish();
        }
    }
}