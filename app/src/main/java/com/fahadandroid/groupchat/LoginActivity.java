package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.CountriesAdapter;
import com.fahadandroid.groupchat.adapters.CountrySmallDialogAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.CountryModel;
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

import java.util.ArrayList;
import java.util.List;

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
                                        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                        finish();
//                                        if (userModel.getUserType()!=null){
//                                            if (userModel.getUserType().equals("Cordinator")){
//                                                AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
//                                                View view = LayoutInflater.from(LoginActivity.this).inflate(R.layout.country_select_dialog, null);
//                                                RecyclerView recyclerCountries = view.findViewById(R.id.recycler_countries);
//                                                recyclerCountries.setLayoutManager(new LinearLayoutManager(LoginActivity.this));
//                                                List<CountryModel> countriesList = new ArrayList<>();
//                                                DatabaseReference countriesRef = FirebaseDatabase.getInstance().getReference("countries");
//                                                countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                                                    @Override
//                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                        for (DataSnapshot dataSnapshot: snapshot.getChildren()){
//                                                            try{
//                                                                CountryModel countryModel = dataSnapshot.getValue(CountryModel.class);
//                                                                countryModel.setKey(dataSnapshot.getKey());
//                                                                if (!countryModel.isDeleted()){
//                                                                    countriesList.add(countryModel);
//                                                                }
//                                                            }catch (Exception e){}
//                                                        }
//                                                        CountrySmallDialogAdapter adapter = new CountrySmallDialogAdapter(countriesList, LoginActivity.this);
//                                                        recyclerCountries.setAdapter(adapter);
//                                                        RecycleClick.addTo(recyclerCountries).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
//                                                            @Override
//                                                            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
//                                                                SharedPreferences preferences = getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE);
//                                                                SharedPreferences.Editor editor = preferences.edit();
//                                                                editor.putString("country",countriesList.get(i).getCountryName());
//                                                                editor.putString("countryKey",countriesList.get(i).getKey());
//                                                                editor.apply();
//                                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                                                startActivity(intent);
//                                                                finish();
//                                                            }
//                                                        });
//                                                    }
//
//                                                    @Override
//                                                    public void onCancelled(@NonNull DatabaseError error) {
//
//                                                    }
//                                                });
//                                                builder.setView(view);
//                                                AlertDialog alertDialog = builder.create();
//                                                alertDialog.setCancelable(false);
//                                                alertDialog.setCanceledOnTouchOutside(false);
//                                                alertDialog.show();
//                                            }else {
//                                                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                                startActivity(intent);
//                                                finish();
//                                            }
//                                        }else {
//                                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
//                                            startActivity(intent);
//                                            finish();
//                                        }
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