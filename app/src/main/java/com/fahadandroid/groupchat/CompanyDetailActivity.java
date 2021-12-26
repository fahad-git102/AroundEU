package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fahadandroid.groupchat.models.ComapnyTimeScheduledModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CompanyDetailActivity extends AppCompatActivity implements View.OnClickListener{

    TextView tvFullLegalName, tvSize, tvLegalAddress, tvPostalCode, tvCity, tvCountry, tvTelephone, tvEmail,
            tvPiva, tvLegalRep, tvLegalRepID, tvWebsite, tvCompnayDesc, tvCompanyRespon, tvTaskOfStudents, tvContactPerson;
    CompanyModel companyModel;
    ImageButton goBack, btnSettings;
    DatabaseReference companyTimeScheduledRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_detail);
        companyModel = getIntent().getParcelableExtra("company");
        companyTimeScheduledRef = FirebaseDatabase.getInstance().getReference("companyTimeScheduled");
        String id = getIntent().getStringExtra("companyID");
        if (companyModel.getKey()==null){
            companyModel.setKey(id);
        }
        tvFullLegalName = findViewById(R.id.tvFullLegalName);
        tvSize = findViewById(R.id.tvSize);
        tvContactPerson = findViewById(R.id.tvContactPerson);
        tvLegalAddress = findViewById(R.id.tvLegalAddress);
        tvPostalCode = findViewById(R.id.tvPostalCode);
        tvCity = findViewById(R.id.tvCity);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        btnSettings = findViewById(R.id.btnSettings);
        btnSettings.setOnClickListener(this);
        tvCountry = findViewById(R.id.tvCountry);
        tvTelephone = findViewById(R.id.tvTelephone);
        tvEmail = findViewById(R.id.tvEmail);
        tvPiva = findViewById(R.id.tvPiva);
        tvLegalRep = findViewById(R.id.tvLegalRepresentative);
        tvLegalRepID = findViewById(R.id.tvLegalRepresentativeID);
        tvWebsite = findViewById(R.id.tvWebsite);
        tvCompnayDesc = findViewById(R.id.tvCompanyDescription);
        tvCompanyRespon = findViewById(R.id.tvCompanyResponsibility);
        tvTaskOfStudents = findViewById(R.id.tvStudentsTask);
        if (companyModel!=null){
            if (companyModel.getFullLegalName()!=null){
                tvFullLegalName.setText(companyModel.getFullLegalName());
            }
            if (companyModel.getSize()!=null){
                tvSize.setText(companyModel.getSize());
            }
            if (companyModel.getLegalAddress()!=null){
                tvLegalAddress.setText(companyModel.getLegalAddress());
            }
            if (companyModel.getPoastalCode()!=null){
                tvPostalCode.setText(companyModel.getPoastalCode());
            }
            if (companyModel.getCity()!=null){
                tvCity.setText(companyModel.getCity());
            }
            if (companyModel.getCountry()!=null){
                tvCountry.setText(companyModel.getCountry());
            }
            if (companyModel.getLegalRepresentative()!=null){
                tvLegalRep.setText(companyModel.getLegalRepresentative());
            }
            if (companyModel.getContactPerson()!=null){
                tvContactPerson.setText(companyModel.getContactPerson());
            }
            if (companyModel.getIdLegalRepresent()!=null){
                tvLegalRepID.setText(companyModel.getIdLegalRepresent());
            }
            if (companyModel.getEmail()!=null){
                tvEmail.setText(companyModel.getEmail());
            }
            if (companyModel.getWebsite()!=null){
                tvWebsite.setText(companyModel.getWebsite());
            }
            if (companyModel.getCompanyDescription()!=null){
                tvCompnayDesc.setText(companyModel.getCompanyDescription());
            }
            if (companyModel.getCompanyResponsibility()!=null){
                tvCompanyRespon.setText(companyModel.getCompanyResponsibility());
            }
            if (companyModel.getTaskOfStudents()!=null){
                tvTaskOfStudents.setText(companyModel.getTaskOfStudents());
            }
            if (companyModel.getPiva()!=null){
                tvPiva.setText(companyModel.getPiva());
            }
            if (companyModel.getTelephone()!=null){
                tvTelephone.setText(companyModel.getTelephone());
            }
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            companyTimeScheduledRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        try{
                            ComapnyTimeScheduledModel comapnyTimeScheduledModel = dataSnapshot.getValue(ComapnyTimeScheduledModel.class);
                            comapnyTimeScheduledModel.setKey(dataSnapshot.getKey());
                            if (comapnyTimeScheduledModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                                if (comapnyTimeScheduledModel.getCompanyId().equals(companyModel.getKey())){
                                    btnSettings.setImageDrawable(getResources().getDrawable(R.drawable.ic_pen));
                                }
                            }

                        }catch (Exception e){}
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.goBack){
            finish();
        }else if (view.getId()==R.id.btnSettings){
            Intent intent = new Intent(CompanyDetailActivity.this, CompanySettingsActivity.class);
            intent.putExtra("company", companyModel);
            intent.putExtra("companyID", companyModel.getKey());
            startActivity(intent);
        }
    }
}