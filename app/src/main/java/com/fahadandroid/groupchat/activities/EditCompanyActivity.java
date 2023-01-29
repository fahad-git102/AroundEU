package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditCompanyActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner spinner;
    String selectedCountry;
    EditText etFullName, etLegalAddress, etPostalCode, etCity, etCountry, etTelephone, etEmail, etWebsite,
            etCompanyDescription, etCompanyResponsibility, etStudentTasks, etContactPerson;
    Button btnSave;
    ImageButton goBack;
    RelativeLayout progress;
    DatabaseReference companyRef;
    CompanyModel companyModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_company);
        companyModel = getIntent().getParcelableExtra("company");
        spinner = findViewById(R.id.spinner);
        companyRef = FirebaseDatabase.getInstance().getReference("companies");
        selectedCountry = EUGroupChat.countryNamesList.get(0);
        String[] items = new String[EUGroupChat.countryNamesList.size()];
        EUGroupChat.countryNamesList.toArray(items);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        etFullName = findViewById(R.id.etFullLegalName);
        etLegalAddress = findViewById(R.id.etLegalAddress);
        etPostalCode = findViewById(R.id.etPostalCode);
        progress = findViewById(R.id.progress);
        etCity = findViewById(R.id.etCity);
        etCountry = findViewById(R.id.etCountry);
        etTelephone = findViewById(R.id.etTelephone);
        etEmail = findViewById(R.id.etEmail);
        etContactPerson = findViewById(R.id.etContactPerson);
        etWebsite = findViewById(R.id.etwebsite);
        etCompanyDescription = findViewById(R.id.etCompanyDescription);
        etCompanyResponsibility = findViewById(R.id.etCompanyResponsibility);
        etStudentTasks = findViewById(R.id.etStudentsTasks);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCountry = items[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayAdapter aa = new ArrayAdapter(this, android.R.layout.simple_spinner_item,items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        if (companyModel!=null){
            if (companyModel.getSelectedCountry()!=null){
                int index = -1;
                for (int i = 0; i<items.length; i++){
                    if (items[i].equals(companyModel.getSelectedCountry())){
                        index = i;
                    }
                }
                spinner.setSelection(index);
            }
            if (companyModel.getFullLegalName()!=null){
                etFullName.setText(companyModel.getFullLegalName());
            }
            if (companyModel.getLegalAddress()!=null){
                etLegalAddress.setText(companyModel.getLegalAddress());
            }
            if (companyModel.getPoastalCode()!=null){
                etPostalCode.setText(companyModel.getPoastalCode());
            }
            if (companyModel.getCity()!=null){
                etCity.setText(companyModel.getCity());
            }
            if (companyModel.getCountry()!=null){
                etCountry.setText(companyModel.getCountry());
            }
            if (companyModel.getTelephone()!=null){
                etTelephone.setText(companyModel.getTelephone());
            }
            if (companyModel.getEmail()!=null){
                etEmail.setText(companyModel.getEmail());
            }
            if (companyModel.getContactPerson()!=null){
                etContactPerson.setText(companyModel.getContactPerson());
            }
            if (companyModel.getWebsite()!=null){
                etWebsite.setText(companyModel.getWebsite());
            }
            if (companyModel.getCompanyDescription()!=null){
                etCompanyDescription.setText(companyModel.getCompanyDescription());
            }
            if (companyModel.getCompanyResponsibility()!=null){
                etCompanyResponsibility.setText(companyModel.getCompanyResponsibility());
            }
            if(companyModel.getTaskOfStudents()!=null){
                etStudentTasks.setText(companyModel.getTaskOfStudents());
            }
        }

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnSave){

            String fullLegalName = etFullName.getText().toString();
            String legalAddress = etLegalAddress.getText().toString();
            String city = etCity.getText().toString();
            String country = etCountry.getText().toString();
            String email = etEmail.getText().toString();
            String postalCode = etPostalCode.getText().toString();
            String telephone = etTelephone.getText().toString();
            String contactPerson = etContactPerson.getText().toString();
            String website = etWebsite.getText().toString();
            String companyDescription = etCompanyDescription.getText().toString();
            String companyResponsibility = etCompanyResponsibility.getText().toString();
            String tasksOfStudents = etStudentTasks.getText().toString();
            companyModel.setFullLegalName(fullLegalName);
            companyModel.setLegalAddress(legalAddress);
            companyModel.setCity(city);
            companyModel.setContactPerson(contactPerson);
            companyModel.setCountry(country);
            companyModel.setEmail(email);
            companyModel.setPoastalCode(postalCode);
            companyModel.setTelephone(telephone);
            companyModel.setWebsite(website);
            companyModel.setCompanyDescription(companyDescription);
            companyModel.setCompanyResponsibility(companyResponsibility);
            companyModel.setTaskOfStudents(tasksOfStudents);
            companyModel.setSelectedCountry(selectedCountry);
            progress.setVisibility(View.VISIBLE);
            companyRef.child(companyModel.getKey()).setValue(companyModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(EditCompanyActivity.this, "Updated", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });

        }else if (view.getId()==R.id.goBack){
            finish();
        }
    }
}