package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.CountriesAdapter;
import com.fahadandroid.groupchat.models.CountryModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectCountryActivity extends AppCompatActivity {

    List<CountryModel> countriesList;
    ImageButton goBack;
    RecyclerView recycler_countries;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, countriesRef;
    boolean fromSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_country);
        goBack = findViewById(R.id.goBack);
        countriesList = new ArrayList<>();
        mAuth = FirebaseAuth.getInstance();
        fromSignUp = getIntent().getBooleanExtra("fromSignUp", false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        countriesRef = firebaseDatabase.getReference("countries");
        recycler_countries = findViewById(R.id.recycler_countries);
        recycler_countries.setLayoutManager(new LinearLayoutManager(this));
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        countriesList.add(new CountryModel("Barcelona P.G", "11111"));
//        countriesList.add(new CountryModel("Catania", "22222"));
        getCountries();
    }

    private void getCountries(){
        countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    try{
                        CountryModel countryModel = dataSnapshot.getValue(CountryModel.class);
                        countryModel.setKey(dataSnapshot.getKey());
                        if (!countryModel.isDeleted()){
                            countriesList.add(countryModel);
                        }
                    }catch (Exception e){}
                }
                CountriesAdapter adapter = new CountriesAdapter(countriesList, SelectCountryActivity.this, false);
                recycler_countries.setAdapter(adapter);
                RecycleClick.addTo(recycler_countries).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                        if (mAuth.getCurrentUser()!=null){
                            AlertDialog.Builder builder = new AlertDialog.Builder(SelectCountryActivity.this);
                            View v = LayoutInflater.from(SelectCountryActivity.this).inflate(R.layout.pincode_dialog_layout, null);
                            EditText etPincode = v.findViewById(R.id.etPincode);
                            Button btnSave = v.findViewById(R.id.btnJoin);
                            btnSave.setText("Save");
                            builder.setView(v);
                            AlertDialog alertDialog = builder.create();
                            btnSave.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    String pincode = etPincode.getText().toString();
                                    if (TextUtils.isEmpty(pincode)){
                                        Toast.makeText(SelectCountryActivity.this, "Pincode Required !", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    if (pincode.equals(countriesList.get(i).getPincode())){
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("selectedCountry", countriesList.get(i).getCountryName());
                                        usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()){
                                                    if (fromSignUp){
//                                                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectCountryActivity.this);
//                                                        builder.setMessage("User created successfully ! Now login to your account to continue.");
//                                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                                mAuth.signOut();
//                                                                Intent intent = new Intent(SelectCountryActivity.this, LoginActivity.class);
//                                                                startActivity(intent);
//                                                                finish();
//                                                            }
//                                                        });
//                                                        AlertDialog alertDialog = builder.create();
//                                                        alertDialog.setCancelable(false);
//                                                        alertDialog.setCanceledOnTouchOutside(false);
//                                                        alertDialog.show();
                                                        Intent intent = new Intent(SelectCountryActivity.this, SelectBusinessListActivity.class);
                                                        intent.putExtra("country", countriesList.get(i).getKey());
                                                        startActivity(intent);

                                                    }else {
                                                        Intent intent = new Intent(SelectCountryActivity.this, SelectBusinessListActivity.class);
                                                        intent.putExtra("country", countriesList.get(i).getKey());
                                                        startActivity(intent);
//                                                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectCountryActivity.this);
//                                                        builder.setTitle("Success");
//                                                        builder.setMessage("User registeration completed successfully. Now login again to continue.");
//                                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                                dialogInterface.dismiss();
//                                                                mAuth.signOut();
//                                                                Intent intent = new Intent(SelectCountryActivity.this, LoginActivity.class);
//                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                                                                startActivity(intent);
//                                                                finish();
//                                                            }
//                                                        });
//                                                        AlertDialog alertDialog1 = builder.create();
//                                                        alertDialog1.show();

                                                    }

                                                }else {
                                                    Toast.makeText(SelectCountryActivity.this, "Something went wrong !", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        });
                                    }else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(SelectCountryActivity.this);
                                        builder.setTitle("Error");
                                        builder.setMessage(" Your pincode not matched with country's pincode.");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        AlertDialog alertDialog1 = builder.create();
                                        alertDialog1.show();
                                    }
                                }
                            });
                            alertDialog.show();
                        }

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}