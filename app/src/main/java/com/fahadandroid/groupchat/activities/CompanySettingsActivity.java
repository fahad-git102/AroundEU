package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.ComapnyTimeScheduledModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import ca.antonious.materialdaypicker.MaterialDayPicker;

public class CompanySettingsActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton goBack;
    MaterialDayPicker materialDayPicker;
    TextView tvMorningStart, tvMorningTo, tvNoonStart, tvNoonTo, tvCompanyName;
    EditText etDescription;
    CompanyModel companyModel;
    RelativeLayout progress;
    String keyExisted ;
    FirebaseAuth mAuth;
    DatabaseReference companyTimeScheduledRef;
    Button btnSave;
    boolean alreadyExist= false;
    String morningFrom, morningTo, noonFrom, noonTo, description;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company_settings);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        goBack = findViewById(R.id.goBack);
        mAuth = FirebaseAuth.getInstance();
        goBack.setOnClickListener(this);
        progress = findViewById(R.id.progress);
        String id = getIntent().getStringExtra("companyID");
        companyTimeScheduledRef = FirebaseDatabase.getInstance().getReference("companyTimeScheduled");
        companyModel = getIntent().getParcelableExtra("company");
        if (companyModel.getKey()==null){
            companyModel.setKey(id);
        }
        materialDayPicker = findViewById(R.id.day_picker);
        tvMorningStart = findViewById(R.id.tvMorningStartTime);
        tvCompanyName = findViewById(R.id.tvCompanyName);
        tvMorningTo = findViewById(R.id.tvMorningToTime);
        tvNoonStart = findViewById(R.id.tvNoonStart);
        tvNoonTo = findViewById(R.id.tvNoonTo);
        tvMorningStart.setOnClickListener(this);
        tvMorningTo.setOnClickListener(this);
        tvNoonStart.setOnClickListener(this);
        tvNoonTo.setOnClickListener(this);
        etDescription = findViewById(R.id.etDescription);
        btnSave = findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        if (companyModel!=null){
            if (companyModel.getFullLegalName()!=null){
                tvCompanyName.setText(companyModel.getFullLegalName());
            }else if (companyModel.getLegalRepresentative()!=null){
                tvCompanyName.setText(companyModel.getLegalRepresentative());
            }
        }
        companyTimeScheduledRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    ComapnyTimeScheduledModel comapnyTimeScheduledModel = snapshot.getValue(ComapnyTimeScheduledModel.class);
                    comapnyTimeScheduledModel.setKey(snapshot.getKey());
                    if (comapnyTimeScheduledModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                        if (comapnyTimeScheduledModel.getCompanyId().equals(companyModel.getKey())){
                            alreadyExist = true;
                            keyExisted = comapnyTimeScheduledModel.getKey();
                            materialDayPicker.setSelectedDays(comapnyTimeScheduledModel.getSelectedDays());
                            tvMorningStart.setText(comapnyTimeScheduledModel.getMorningFrom());
                            tvMorningTo.setText(comapnyTimeScheduledModel.getMorningTo());
                            tvNoonStart.setText(comapnyTimeScheduledModel.getNoonFrom());
                            tvNoonTo.setText(comapnyTimeScheduledModel.getNoonTo());
                            etDescription.setText(comapnyTimeScheduledModel.getDescription());
                            btnSave.setText("Update");
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    ComapnyTimeScheduledModel comapnyTimeScheduledModel = snapshot.getValue(ComapnyTimeScheduledModel.class);
                    comapnyTimeScheduledModel.setKey(snapshot.getKey());
                    if (comapnyTimeScheduledModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                        if (comapnyTimeScheduledModel.getCompanyId().equals(companyModel.getKey())){
                            alreadyExist = true;
                            keyExisted = comapnyTimeScheduledModel.getKey();
                            materialDayPicker.setSelectedDays(comapnyTimeScheduledModel.getSelectedDays());
                            tvMorningStart.setText(comapnyTimeScheduledModel.getMorningFrom());
                            tvMorningTo.setText(comapnyTimeScheduledModel.getMorningTo());
                            tvNoonStart.setText(comapnyTimeScheduledModel.getNoonFrom());
                            tvNoonTo.setText(comapnyTimeScheduledModel.getNoonTo());
                            etDescription.setText(comapnyTimeScheduledModel.getDescription());
                            btnSave.setText("Update");
                        }
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {

                }catch (Exception e){}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.goBack){
            finish();
        }else if (view.getId()==R.id.btnSave){
            List<MaterialDayPicker.Weekday> selectedDays = materialDayPicker.getSelectedDays();
            morningFrom = tvMorningStart.getText().toString();
            morningTo = tvMorningTo.getText().toString();
            noonFrom = tvNoonStart.getText().toString();
            noonTo = tvNoonTo.getText().toString();
            description = etDescription.getText().toString();
            if (selectedDays.size()==0||morningFrom.isEmpty()||morningTo.isEmpty()||noonFrom.isEmpty()||noonTo.isEmpty()||description.isEmpty()){
                Toast.makeText(this, "Please fill all the data !", Toast.LENGTH_SHORT).show();
                return;
            }
            progress.setVisibility(View.VISIBLE);
            ComapnyTimeScheduledModel comapnyTimeScheduledModel = new ComapnyTimeScheduledModel();
            comapnyTimeScheduledModel.setCompanyId(companyModel.getKey());
            comapnyTimeScheduledModel.setDescription(description);
            comapnyTimeScheduledModel.setMorningFrom(morningFrom);
            comapnyTimeScheduledModel.setMorningTo(morningTo);
            comapnyTimeScheduledModel.setNoonFrom(noonFrom);
            comapnyTimeScheduledModel.setNoonTo(noonTo);
            comapnyTimeScheduledModel.setUid(mAuth.getCurrentUser().getUid());
            comapnyTimeScheduledModel.setSelectedDays(selectedDays);
            if (alreadyExist){
                companyTimeScheduledRef.child(keyExisted).setValue(comapnyTimeScheduledModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progress.setVisibility(View.GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(CompanySettingsActivity.this);
                        builder.setMessage("Time schedule updated successfully !");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                });
            }else {
                companyTimeScheduledRef.push().setValue(comapnyTimeScheduledModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        progress.setVisibility(View.GONE);
                        AlertDialog.Builder builder = new AlertDialog.Builder(CompanySettingsActivity.this);
                        builder.setMessage("Time schedule set successfully !");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.setCancelable(false);
                        alertDialog.setCanceledOnTouchOutside(false);
                        alertDialog.show();
                    }
                });
            }
        }else if (view.getId()==R.id.tvMorningStartTime){
            pickTimeDialog(tvMorningStart);
        }else if (view.getId()==R.id.tvMorningToTime){
            pickTimeDialog(tvMorningTo);
        }else if (view.getId()==R.id.tvNoonStart){
            pickTimeDialog(tvNoonStart);
        }else if (view.getId()==R.id.tvNoonTo){
            pickTimeDialog(tvNoonTo);
        }
    }

    private void pickTimeDialog(TextView textView){
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(CompanySettingsActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                String time = selectedHour+":"+selectedMinute;
                try {
                    final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
                    final Date dateObj = sdf.parse(time);
                    String outputTime = new SimpleDateFormat("K:mm a").format(dateObj);
                    textView.setText(outputTime);
                } catch (final ParseException e) {
                    e.printStackTrace();
                }
            }
        }, hour, minute, false);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

}