package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fahadandroid.groupchat.models.CoordinatorModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rilixtech.widget.countrycodepicker.CountryCodePicker;

public class AddCoordinatorActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton goBack;
    EditText etDetails;
    CountryCodePicker countryCodePicker;
    EditText etPhone;
    Button btnSave;
    String text, phone;
    TextView btnViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coordinator);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        btnViewAll = findViewById(R.id.btnViewAll);
        btnViewAll.setOnClickListener(this);
        etDetails = findViewById(R.id.etDetails);
        countryCodePicker = findViewById(R.id.countryCodePicker);
        etPhone = findViewById(R.id.phone_number_edt);
        countryCodePicker.registerPhoneNumberTextView(etPhone);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnSave){
            text = etDetails.getText().toString();
            phone = countryCodePicker.getFullNumberWithPlus();
            if (text.isEmpty()||phone.isEmpty()){
                if (TextUtils.isEmpty(text)){
                    etDetails.setError("Field Required");
                }
                if (TextUtils.isEmpty(phone)){
                    etPhone.setError("Phone Required");
                }
                return;
            }
            addCoordinate();
        }else if (view.getId()==R.id.goBack){
            finish();
        }else if (view.getId()==R.id.btnViewAll){
            Intent intent = new Intent(AddCoordinatorActivity.this, ContactsInfoActivity.class);
            intent.putExtra("fromAdmin", true);
            intent.putExtra("from", "coordinate");
            startActivity(intent);
        }
    }
    private void addCoordinate(){
        DatabaseReference coordinatorsRef = FirebaseDatabase.getInstance().getReference("coordinators");
        CoordinatorModel coordinatorModel = new CoordinatorModel();
        coordinatorModel.setPhone(phone);
        coordinatorModel.setText(text);
        coordinatorsRef.push().setValue(coordinatorModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    AlertDialog.Builder builder = new AlertDialog.Builder(AddCoordinatorActivity.this);
                    builder.setTitle("Success");
                    builder.setMessage("Coordinator's contact added successfully");
                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            finish();
                        }
                    });
                    AlertDialog alertDialog = builder.create();
                    alertDialog.setCancelable(false);
                    alertDialog.setCanceledOnTouchOutside(false);
                    alertDialog.show();
                }
            }
        });
    }
}