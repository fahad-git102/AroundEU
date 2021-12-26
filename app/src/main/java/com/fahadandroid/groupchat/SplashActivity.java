 package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.fahadandroid.groupchat.models.GroupsModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

 public class SplashActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, groupsRef, countriesRef;
    boolean groupMatched = false;
    GroupsModel matchedGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        groupsRef = firebaseDatabase.getReference("groups");
        countriesRef = firebaseDatabase.getReference("countries");

        if (mAuth.getCurrentUser()!=null){
            usersRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (userModel.isAdmin()){
                            Intent intent = new Intent(SplashActivity.this, AdminHomeActivity.class);
                            startActivity(intent);
                            finish();
                        }else {
                            Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();

//                            if (userModel.getUserType()!=null){
//                                if (userModel.getUserType().toLowerCase().equals("student")){
//                                    if (userModel.isJoined()){
//                                            Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                    }else {
//                                        AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
//                                        View view = LayoutInflater.from(SplashActivity.this).inflate(R.layout.pincode_dialog_layout, null);
//                                        TextView tvText = view.findViewById(R.id.tvText);
//                                        tvText.setVisibility(View.VISIBLE);
//                                        EditText etPin = view.findViewById(R.id.etPincode);
//                                        Button btnJoin = view.findViewById(R.id.btnJoin);
//                                        ProgressBar progressBar = view.findViewById(R.id.progress);
//                                        builder.setView(view);
//                                        AlertDialog alertDialog = builder.create();
//                                        btnJoin.setOnClickListener(new View.OnClickListener() {
//                                            @Override
//                                            public void onClick(View view) {
//                                                String pin = etPin.getText().toString();
//                                                if (TextUtils.isEmpty(pin)){
//                                                    etPin.setError("Pin Required");
//                                                    return;
//                                                }
//                                                progressBar.setVisibility(View.VISIBLE);
//                                                groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                        progressBar.setVisibility(View.GONE);
//                                                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                                                            try {
//                                                                GroupsModel groupsModel = dataSnapshot.getValue(GroupsModel.class);
//                                                                groupsModel.setKey(dataSnapshot.getKey());
//                                                                if (groupsModel.getPincode()!=null){
//                                                                    if (groupsModel.getPincode().equals(pin)){
//                                                                        matchedGroup = groupsModel;
//                                                                        groupMatched = true;
//                                                                    }
//                                                                }
//                                                            }catch (Exception e){}
//                                                        }
//                                                        if (groupMatched){
//                                                            Toast.makeText(SplashActivity.this, "Pin matched", Toast.LENGTH_SHORT).show();
//                                                            List<String> approvedMembers = new ArrayList<>();
////
//                                                            if (matchedGroup.getApprovedMembers()!=null){
//                                                                approvedMembers = matchedGroup.getApprovedMembers();
//                                                                if (!approvedMembers.contains(mAuth.getCurrentUser().getUid())){
//                                                                    approvedMembers.add(mAuth.getCurrentUser().getUid());
//                                                                }
//                                                            }else {
//                                                                approvedMembers.add(mAuth.getCurrentUser().getUid());
//                                                            }
//                                                            Map<String, Object> map = new HashMap<>();
//                                                            map.put("approvedMembers", approvedMembers);
//                                                            groupsRef.child(matchedGroup.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                @Override
//                                                                public void onComplete(@NonNull Task<Void> task) {
//                                                                    Map<String, Object> map = new HashMap<>();
//                                                                    map.put("joined", true);
//                                                                    usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//                                                                            if (task.isSuccessful()){
//                                                                                alertDialog.dismiss();
//                                                                                progressBar.setVisibility(View.GONE);
//                                                                                Toast.makeText(SplashActivity.this, "Group Joined !", Toast.LENGTH_SHORT).show();
//                                                                                Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
//                                                                                startActivity(intent);
//                                                                                finish();
//                                                                            }else {
//                                                                                alertDialog.dismiss();
//                                                                                progressBar.setVisibility(View.GONE);
//                                                                                Toast.makeText(SplashActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                                            }
//                                                                        }
//                                                                    });
//                                                                }
//                                                            });
//                                                        }else {
//                                                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
//                                                            builder.setTitle("Error");
//                                                            builder.setMessage("Pincode not matched with any group.");
//                                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                                @Override
//                                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                                    dialogInterface.dismiss();
//                                                                }
//                                                            });
//                                                            AlertDialog alertDialog1 = builder.create();
//                                                            alertDialog1.show();
//                                                        }
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });
//                                            }
//                                        });
//                                        alertDialog.setCancelable(false);
//                                        alertDialog.setCanceledOnTouchOutside(false);
//                                        alertDialog.show();
//                                    }
//                                }else if (userModel.getUserType().toLowerCase().equals("teacher")){
//                                    if (!userModel.isJoined()){
//                                        if (userModel.getSelectedCountry()!=null){
//                                            AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
//                                            View view = LayoutInflater.from(SplashActivity.this).inflate(R.layout.pincode_dialog_layout, null);
//                                            TextView tvText = view.findViewById(R.id.tvText);
//                                            tvText.setVisibility(View.VISIBLE);
//                                            tvText.setText("Enter the pincode of your Country");
//                                            EditText etPin = view.findViewById(R.id.etPincode);
//                                            Button btnJoin = view.findViewById(R.id.btnJoin);
//                                            ProgressBar progressBar = view.findViewById(R.id.progress);
//                                            builder.setView(view);
//                                            AlertDialog alertDialog = builder.create();
//                                            btnJoin.setOnClickListener(new View.OnClickListener() {
//                                                @Override
//                                                public void onClick(View view) {
//                                                    String pin = etPin.getText().toString();
//                                                    if (TextUtils.isEmpty(pin)){
//                                                        etPin.setError("Pin Required");
//                                                        return;
//                                                    }
//                                                    progressBar.setVisibility(View.VISIBLE);
//                                                    countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                                                try {
//                                                                    CountryModel countryModel = dataSnapshot.getValue(CountryModel.class);
//                                                                    countryModel.setKey(dataSnapshot.getKey());
//                                                                    if (userModel.getSelectedCountry()!=null){
//                                                                        if (countryModel.getCountryName().equals(userModel.getSelectedCountry())){
//                                                                            if (pin.equals(countryModel.getPincode())){
//                                                                                progressBar.setVisibility(View.GONE);
//                                                                                Intent intent = new Intent(SplashActivity.this, SelectBusinessListActivity.class);
//                                                                                intent.putExtra("country", countryModel.getKey());
//                                                                                startActivity(intent);
//                                                                                finish();
//                                                                            }else {
//                                                                                progressBar.setVisibility(View.GONE);
//                                                                                AlertDialog.Builder builder = new AlertDialog.Builder(SplashActivity.this);
//                                                                                builder.setTitle("Error");
//                                                                                builder.setMessage("Pincode not matched with your country.");
//                                                                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                                                    @Override
//                                                                                    public void onClick(DialogInterface dialogInterface, int i) {
//                                                                                        dialogInterface.dismiss();
//                                                                                    }
//                                                                                });
//                                                                                AlertDialog alertDialog1 = builder.create();
//                                                                                alertDialog1.show();
//                                                                            }
//                                                                        }
//                                                                    }else {
//                                                                        Intent intent = new Intent(SplashActivity.this, SelectCountryActivity.class);
//                                                                        startActivity(intent);
//                                                                    }
//
//                                                                }catch (Exception e){}
//                                                            }
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                                        }
//                                                    });
//                                                }
//                                            });
//                                            alertDialog.setCanceledOnTouchOutside(false);
//                                            alertDialog.setCancelable(false);
//                                            alertDialog.show();
//                                        }else {
//                                            Intent intent = new Intent(SplashActivity.this, SelectCountryActivity.class);
//                                            startActivity(intent);
//                                        }
//                                    }else {
//                                        Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                }else {
//                                    if (userModel.getSelectedCountry()!=null){
//                                        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }else {
//                                        Intent intent = new Intent(SplashActivity.this, SelectCountryActivity.class);
//                                        startActivity(intent);
//                                        finish();
//                                    }
//                                }
//                            }else {
//                                if (userModel.getSelectedCountry()!=null){
//                                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }else {
//                                    Intent intent = new Intent(SplashActivity.this, SelectCountryActivity.class);
//                                    startActivity(intent);
//                                    finish();
//                                }
//                            }
                        }
                    }catch (Exception e){
                        Toast.makeText(SplashActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(SplashActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();

                }
            });
        }else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                }
            }, 1500);
        }
    }
}