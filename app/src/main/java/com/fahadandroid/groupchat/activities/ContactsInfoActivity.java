package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.CoordinatorsAdapter;
import com.fahadandroid.groupchat.adapters.EmergencyContactAdapter;
import com.fahadandroid.groupchat.models.CoordinatorModel;
import com.fahadandroid.groupchat.models.EmergencyContact;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ContactsInfoActivity extends AppCompatActivity {

    ImageButton goback;
    TextView tvTitle;
    DatabaseReference coordinatorsRef;
    LinearLayout linear_emergency, linear_office_contacts;
    boolean fromAdmin;
    RecyclerView recyclerCoordinators;
    List<EmergencyContact> emergencyContactList;
    TextView tvPhoneNo/*, tvCarabinieri, tvStatePolice, tvFireDep, tvMedicalAid*/;
    List<CoordinatorModel> coordinatorsList;
    RecyclerView recyclerEmergencyContacts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contacts_info);
        goback = findViewById(R.id.goBack);
        tvTitle = findViewById(R.id.tvTitle);
        tvPhoneNo = findViewById(R.id.phone_no);
        recyclerEmergencyContacts = findViewById(R.id.recycler_contacts);
        recyclerEmergencyContacts.setLayoutManager(new LinearLayoutManager(this));
        emergencyContactList = new ArrayList<>();
        linear_office_contacts = findViewById(R.id.linear_office_contact);
        recyclerCoordinators = findViewById(R.id.recycler_contacts_coordinators);
        recyclerCoordinators.setLayoutManager(new LinearLayoutManager(this));
        linear_emergency = findViewById(R.id.linear_emergency);
        String from = getIntent().getExtras().getString("from");
        fromAdmin = getIntent().getExtras().getBoolean("fromAdmin", false);

        if (from.equals("coordinate")){
            linear_emergency.setVisibility(View.GONE);
            recyclerCoordinators.setVisibility(View.VISIBLE);
            linear_office_contacts.setVisibility(View.GONE);
            coordinatorsList = new ArrayList<>();
            coordinatorsRef = FirebaseDatabase.getInstance().getReference("coordinators");
            coordinatorsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            CoordinatorModel coordinatorModel = dataSnapshot.getValue(CoordinatorModel.class);
                            coordinatorModel.setKey(dataSnapshot.getKey());
                            coordinatorsList.add(coordinatorModel);
                        }
                        CoordinatorsAdapter adapter = new CoordinatorsAdapter(coordinatorsList, ContactsInfoActivity.this, fromAdmin);
                        recyclerCoordinators.setAdapter(adapter);
                    }catch (Exception e){}
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }else if (from.equals("emergency")){
            linear_emergency.setVisibility(View.VISIBLE);
            recyclerCoordinators.setVisibility(View.GONE);
            linear_office_contacts.setVisibility(View.GONE);
            getEmergencyContacts();
        }else if (from.equals("office")){
            linear_emergency.setVisibility(View.GONE);
            recyclerCoordinators.setVisibility(View.GONE);
            linear_office_contacts.setVisibility(View.VISIBLE);
            String text = "<u>+39 0902130696</u>";
            tvPhoneNo.setText(Html.fromHtml(text));
            tvTitle.setText("Office Contact");
            tvPhoneNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse("tel:+39 0902130696"));
                    startActivity(intent);
                }
            });
        }
        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void getEmergencyContacts(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("emergencyContacts");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    try {
                        EmergencyContact emergencyContact = dataSnapshot.getValue(EmergencyContact.class);
                        emergencyContactList.add(emergencyContact);
                    }catch (Exception e){
                        Toast.makeText(ContactsInfoActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                EmergencyContactAdapter adapter = new EmergencyContactAdapter(emergencyContactList, ContactsInfoActivity.this, fromAdmin);
                recyclerEmergencyContacts.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}