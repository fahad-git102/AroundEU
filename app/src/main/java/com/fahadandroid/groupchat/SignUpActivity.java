package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnSignUp;
    TextView tvGoToLogin;
    RadioGroup radioGroup;
    FirebaseAuth mAuth;
    GroupsModel matchedGroup;
    boolean groupMatched = false;
    RelativeLayout progress;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, groupsRef;
    EditText etDob;
    CountryCodePicker countryCodePicker;
    String firstName, surName, email, phone, country, password, confirmPass, dob, userType;
    EditText etFirstName, etSurName, etEmail, etCountry, etPassword, etRetypePassword, edtPhoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        countryCodePicker = findViewById(R.id.countryCodePicker);
        edtPhoneNumber = findViewById(R.id.phone_number_edt);
        progress = findViewById(R.id.progress);
        usersRef = firebaseDatabase.getReference("Users");
        groupsRef = firebaseDatabase.getReference("groups");
        btnSignUp = findViewById(R.id.btnSignUp);
        btnSignUp.setOnClickListener(this);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvGoToLogin.setOnClickListener(this);
        etDob = findViewById(R.id.etDob);
        etDob.setFocusableInTouchMode(false);
        etDob.setFocusable(false);
        etDob.setOnClickListener(this);
        etFirstName = findViewById(R.id.etFirstName);
        etSurName = findViewById(R.id.etSurName);
        etEmail = findViewById(R.id.etEmail);
        etCountry = findViewById(R.id.etCountry);
        etPassword = findViewById(R.id.etPassword);
        etRetypePassword = findViewById(R.id.etConfirmPassword);
        countryCodePicker.registerPhoneNumberTextView(edtPhoneNumber);
        radioGroup = findViewById(R.id.radioGroup);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.tvGoToLogin){
            startActivity(new Intent(this, LoginActivity.class));
        }else if (view.getId()==R.id.btnSignUp){
            firstName = etFirstName.getText().toString();
            surName = etSurName.getText().toString();
            email = etEmail.getText().toString();
            phone = countryCodePicker.getFullNumberWithPlus();
            country = etCountry.getText().toString();
            password = etPassword.getText().toString();
            confirmPass = etRetypePassword.getText().toString();
            dob = etDob.getText().toString();
            int selectedId=radioGroup.getCheckedRadioButtonId();
            RadioButton radioButton =(RadioButton)findViewById(selectedId);
            userType = radioButton.getText().toString();
            if (firstName.isEmpty()||surName.isEmpty()||email.isEmpty()||phone.isEmpty()||
                    country.isEmpty()||password.isEmpty()||confirmPass.isEmpty()||dob.isEmpty()||userType.isEmpty()){
                if (TextUtils.isEmpty(firstName)){
                    etFirstName.setError("First Name Required");
                }

                if (TextUtils.isEmpty(userType)){
                    Toast.makeText(this, "Select user type", Toast.LENGTH_SHORT).show();
                }

                if (TextUtils.isEmpty(surName)){
                    etSurName.setError("Surname Required");
                }
                if (TextUtils.isEmpty(email)){
                    etEmail.setError("Email Required");
                }
                if (TextUtils.isEmpty(phone)){
                    edtPhoneNumber.setError("Phone Required");
                }
                if (TextUtils.isEmpty(country)){
                    etCountry.setError("Country Required");
                }
                if (TextUtils.isEmpty(password)){
                    etPassword.setError("Password Required");
                }
                if (TextUtils.isEmpty(confirmPass)){
                    etRetypePassword.setError("Retype your password");
                }
                if (TextUtils.isEmpty(dob)){
                    etDob.setError("Date of Birth Required");
                }
                return;
            }
            signUp();

        }else if (view.getId()==R.id.etDob){
            HelperClass.datePicker(etDob, this);
//            datePickerDialog.show();
        }
    }

    private void signUp(){
        progress.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    UserModel userModel = new UserModel(firebaseUser.getUid(), firstName, surName, email, country,
                            dob, phone);
                    userModel.setUserType(userType);
                    userModel.setJoinedOn(System.currentTimeMillis());
                    usersRef.child(firebaseUser.getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progress.setVisibility(View.GONE);
                            if (task.isSuccessful()){
                                mAuth.signOut();
                                Toast.makeText(SignUpActivity.this, "User created successfully !", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }else {
                                progress.setVisibility(View.GONE);
                                Toast.makeText(SignUpActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(SignUpActivity.this, ""+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}