 package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.fahadandroid.groupchat.R;
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
    TextView tvVersion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        tvVersion = findViewById(R.id.tvVersion);
        try {
            PackageInfo pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            tvVersion.setText("v"+pinfo.versionName);
            tvVersion.setVisibility(View.VISIBLE);
        } catch (PackageManager.NameNotFoundException e) {
            tvVersion.setVisibility(View.GONE);
            e.printStackTrace();
        }
        Intent intent = getIntent();
        usersRef = firebaseDatabase.getReference("Users");
        groupsRef = firebaseDatabase.getReference("groups");
        countriesRef = firebaseDatabase.getReference("countries");

        if (mAuth.getCurrentUser()!=null){
            usersRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        UserModel userModel = snapshot.getValue(UserModel.class);
                        if (intent.hasExtra("dataUid")||intent.hasExtra("chatId")){
                            String dataUid = null;
                            if (intent.hasExtra("dataUid")){
                                dataUid = intent.getStringExtra("dataUid");
                            }else if (intent.hasExtra("chatId")){
                                dataUid = intent.getStringExtra("chatId");
                            }
                            if (dataUid!=null){
                                Intent intent = new Intent(SplashActivity.this, ChatActivity.class);
                                intent.putExtra("group", dataUid);
                                startActivity(intent);
                                finish();
                            }
                        }else {
                            if (userModel.isAdmin()){
                                Intent intent = new Intent(SplashActivity.this, AdminHomeActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();

                            }
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