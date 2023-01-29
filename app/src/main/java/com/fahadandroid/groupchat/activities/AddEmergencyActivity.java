package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.EmergencyContact;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddEmergencyActivity extends AppCompatActivity implements View.OnClickListener{

    EditText etName, etNumber;
    Button btnSave;
    ImageButton goBack;
    String name, number;
    TextView tvViewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_emergency);
        etName = findViewById(R.id.etName);
        etNumber = findViewById(R.id.etNumber);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        tvViewAll = findViewById(R.id.btnViewAll);
        tvViewAll.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnSave){
            name = etName.getText().toString();
            number = etNumber.getText().toString();
            if (name.isEmpty()||number.isEmpty()){
                if (TextUtils.isEmpty(name)){
                    etName.setError("Name required");
                }
                if (TextUtils.isEmpty(number)){
                    etNumber.setError("Number required");
                }
                return;
            }

            saveContact();

        }else if (view.getId()==R.id.goBack){
            finish();
        }else if (view.getId()==R.id.btnViewAll){
            Intent intent = new Intent(AddEmergencyActivity.this, ContactsInfoActivity.class);
            intent.putExtra("fromAdmin", true);
            intent.putExtra("from", "emergency");
            startActivity(intent);
        }
    }

    private void saveContact(){
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("emergencyContacts");
        EmergencyContact emergencyContact = new EmergencyContact();
        String key = ref.push().getKey();
        emergencyContact.setKey(key);
        emergencyContact.setName(name);
        emergencyContact.setNumber(number);
        ref.child(key).setValue(emergencyContact).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                etName.setText("");
                etNumber.setText("");
                Toast.makeText(AddEmergencyActivity.this, "Emergency contact added !", Toast.LENGTH_SHORT).show();
            }
        });
    }

}