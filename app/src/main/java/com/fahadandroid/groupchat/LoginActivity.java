package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnLogin;
    TextView tvGoToSignUp;
    FirebaseAuth mAuth;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, groupsRef, countriesRef;
    boolean groupMatched = false;
    GroupsModel matchedGroup;
    EditText etEmail, etPassword;
    RelativeLayout progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        groupsRef = firebaseDatabase.getReference("groups");
        countriesRef = firebaseDatabase.getReference("countries");
        etEmail = findViewById(R.id.etEmail);
        progress = findViewById(R.id.progress);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(this);
        tvGoToSignUp = findViewById(R.id.tvGoToSignup);
        tvGoToSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.tvGoToSignup){
            startActivity(new Intent(this, SignUpActivity.class));
        }else if (view.getId()==R.id.btnLogin){
            String email = etEmail.getText().toString();
            String password = etPassword.getText().toString();
            if (email.isEmpty()||password.isEmpty()){
                if (TextUtils.isEmpty(email)){
                    etEmail.setError("Email Required");
                }
                if (TextUtils.isEmpty(password)){
                    etPassword.setError("Password Required");
                }
                return;
            }
            progress.setVisibility(View.VISIBLE);
            mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progress.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        usersRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                try{
                                    UserModel userModel = snapshot.getValue(UserModel.class);
                                    EUGroupChat.currentUser = userModel;
                                    if (userModel.isAdmin()){
                                        Intent intent = new Intent(LoginActivity.this, AdminHomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }else {
//                                        if (userModel.getUserType()!=null){
//                                            if (userModel.getUserType().toLowerCase().equals("student")){
//                                                if (userModel.isJoined()){
//                                                        Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
//                                                        startActivity(intent);
//                                                        finish();
//                                                }else {
//                                                    AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                                    View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.pincode_dialog_layout, null);
//                                                    TextView tvText = view.findViewById(R.id.tvText);
//                                                    tvText.setVisibility(View.VISIBLE);
//                                                    EditText etPin = view.findViewById(R.id.etPincode);
//                                                    Button btnJoin = view.findViewById(R.id.btnJoin);
//                                                    ProgressBar progressBar = view.findViewById(R.id.progress);
//                                                    builder.setView(view);
//                                                    AlertDialog alertDialog = builder.create();
//                                                    btnJoin.setOnClickListener(new View.OnClickListener() {
//                                                        @Override
//                                                        public void onClick(View view) {
//                                                            String pin = etPin.getText().toString();
//                                                            if (TextUtils.isEmpty(pin)){
//                                                                etPin.setError("Pin Required");
//                                                                return;
//                                                            }
//                                                            progressBar.setVisibility(View.VISIBLE);
//                                                            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                @Override
//                                                                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                    progressBar.setVisibility(View.GONE);
//                                                                    progress.setVisibility(View.GONE);
//                                                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                                                                        try {
//                                                                            GroupsModel groupsModel = dataSnapshot.getValue(GroupsModel.class);
//                                                                            groupsModel.setKey(dataSnapshot.getKey());
//                                                                            if (groupsModel.getPincode()!=null){
//                                                                                if (groupsModel.getPincode().equals(pin)){
//                                                                                    matchedGroup = groupsModel;
//                                                                                    groupMatched = true;
//                                                                                }
//                                                                            }
//                                                                        }catch (Exception e){}
//                                                                    }
//                                                                    if (groupMatched){
//                                                                        Toast.makeText(LoginActivity.this, "Pin matched", Toast.LENGTH_SHORT).show();
//                                                                        List<String> approvedMembers = new ArrayList<>();
////
//                                                                        if (matchedGroup.getApprovedMembers()!=null){
//                                                                            approvedMembers = matchedGroup.getApprovedMembers();
//                                                                            if (!approvedMembers.contains(mAuth.getCurrentUser().getUid())){
//                                                                                approvedMembers.add(mAuth.getCurrentUser().getUid());
//                                                                            }
//                                                                        }else {
//                                                                            approvedMembers.add(mAuth.getCurrentUser().getUid());
//                                                                        }
//                                                                        Map<String, Object> map = new HashMap<>();
//                                                                        map.put("approvedMembers", approvedMembers);
//                                                                        groupsRef.child(matchedGroup.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                            @Override
//                                                                            public void onComplete(@NonNull Task<Void> task) {
//                                                                                Map<String, Object> map = new HashMap<>();
//                                                                                map.put("joined", true);
//                                                                                usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                                    @Override
//                                                                                    public void onComplete(@NonNull Task<Void> task) {
//                                                                                        if (task.isSuccessful()){
//                                                                                            alertDialog.dismiss();
//                                                                                            progressBar.setVisibility(View.GONE);
//                                                                                            Toast.makeText(LoginActivity.this, "Group Joined !", Toast.LENGTH_SHORT).show();
//                                                                                            Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
//                                                                                            startActivity(intent);
//                                                                                            finish();
//                                                                                        }else {
//                                                                                            alertDialog.dismiss();
//                                                                                            progressBar.setVisibility(View.GONE);
//                                                                                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                                                                        }
//                                                                                    }
//                                                                                });
//                                                                            }
//                                                                        });
//                                                                    }else {
//                                                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                                                        builder.setTitle("Error");
//                                                                        builder.setMessage("Pincode not matched with any group.");
//                                                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                                            @Override
//                                                                            public void onClick(DialogInterface dialogInterface, int i) {
//                                                                                dialogInterface.dismiss();
//                                                                            }
//                                                                        });
//                                                                        AlertDialog alertDialog1 = builder.create();
//                                                                        alertDialog1.show();
//                                                                    }
//                                                                }
//
//                                                                @Override
//                                                                public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                }
//                                                            });
//                                                        }
//                                                    });
//                                                    alertDialog.setCancelable(false);
//                                                    alertDialog.setCanceledOnTouchOutside(false);
//                                                    alertDialog.show();
//                                                }
//                                            }else if (userModel.getUserType().toLowerCase().equals("teacher")){
//                                                if (!userModel.isJoined()){
//                                                    if (userModel.getSelectedCountry()!=null){
//                                                        AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                                        View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.pincode_dialog_layout, null);
//                                                        TextView tvText = view.findViewById(R.id.tvText);
//                                                        tvText.setVisibility(View.VISIBLE);
//                                                        tvText.setText("Enter the pincode of your Country");
//                                                        EditText etPin = view.findViewById(R.id.etPincode);
//                                                        Button btnJoin = view.findViewById(R.id.btnJoin);
//                                                        ProgressBar progressBar = view.findViewById(R.id.progress);
//                                                        builder.setView(view);
//                                                        AlertDialog alertDialog = builder.create();
//                                                        btnJoin.setOnClickListener(new View.OnClickListener() {
//                                                            @Override
//                                                            public void onClick(View view) {
//                                                                String pin = etPin.getText().toString();
//                                                                if (TextUtils.isEmpty(pin)){
//                                                                    etPin.setError("Pin Required");
//                                                                    return;
//                                                                }
//                                                                progressBar.setVisibility(View.VISIBLE);
//                                                                countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                                    @Override
//                                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                                                            try {
//                                                                                CountryModel countryModel = dataSnapshot.getValue(CountryModel.class);
//                                                                                countryModel.setKey(dataSnapshot.getKey());
//                                                                                if (userModel.getSelectedCountry()!=null){
//                                                                                    if (countryModel.getCountryName().equals(userModel.getSelectedCountry())){
//                                                                                        if (pin.equals(countryModel.getPincode())){
//                                                                                            progressBar.setVisibility(View.GONE);
//                                                                                            Intent intent = new Intent(LoginActivity.this, SelectBusinessListActivity.class);
//                                                                                            intent.putExtra("country", countryModel.getKey());
//                                                                                            startActivity(intent);
//                                                                                            finish();
//                                                                                        }else {
//                                                                                            progressBar.setVisibility(View.GONE);
//                                                                                            AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                                                                            builder.setTitle("Error");
//                                                                                            builder.setMessage("Pincode not matched with your country.");
//                                                                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                                                                                                @Override
//                                                                                                public void onClick(DialogInterface dialogInterface, int i) {
//                                                                                                    dialogInterface.dismiss();
//                                                                                                }
//                                                                                            });
//                                                                                            AlertDialog alertDialog1 = builder.create();
//                                                                                            alertDialog1.show();
//                                                                                        }
//                                                                                    }
//                                                                                }else {
//                                                                                    Intent intent = new Intent(LoginActivity.this, SelectCountryActivity.class);
//                                                                                    startActivity(intent);
//                                                                                }
//
//                                                                            }catch (Exception e){}
//                                                                        }
//                                                                    }
//
//                                                                    @Override
//                                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                                    }
//                                                                });
//                                                            }
//                                                        });
//                                                        alertDialog.setCanceledOnTouchOutside(false);
//                                                        alertDialog.setCancelable(false);
//                                                        alertDialog.show();
//                                                    }else {
//                                                        Intent intent = new Intent(LoginActivity.this, SelectCountryActivity.class);
//                                                        startActivity(intent);
//                                                    }
//                                                }else {
//                                                    Intent intent = new Intent(LoginActivity.this, ChatActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//
//                                            }else {
//                                                if (userModel.getSelectedCountry()!=null){
//                                                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }else {
//                                                    Intent intent = new Intent(LoginActivity.this, SelectCountryActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//                                            }
//                                        }else {
//                                            if (userModel.getSelectedCountry()!=null){
//                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                                startActivity(intent);
//                                                finish();
//                                            }else {
//                                                Intent intent = new Intent(LoginActivity.this, SelectCountryActivity.class);
//                                                startActivity(intent);
//                                                finish();
//                                            }
//                                        }
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }catch (Exception e){

                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

                    }else {
                        Toast.makeText(LoginActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }
}