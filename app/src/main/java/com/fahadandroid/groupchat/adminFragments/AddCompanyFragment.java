package com.fahadandroid.groupchat.adminFragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.fahadandroid.groupchat.ExploreInternshipActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddCompanyFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddCompanyFragment extends Fragment implements View.OnClickListener{

    Spinner spinner;
    String selectedCountry;
    ImageButton btnViewAll;
    EditText etFullName, etLegalAddress, etPostalCode, etCity, etCountry, etTelephone, etEmail, etWebsite,
            etCompanyDescription, etCompanyResponsibility, etStudentTasks, etContactPerson;
    Button btnSave;
    RelativeLayout progress;
    DatabaseReference companyRef;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddCompanyFragment() {
        // Required empty public constructor
    }

    public static AddCompanyFragment newInstance(String param1, String param2) {
        AddCompanyFragment fragment = new AddCompanyFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_company, container, false);
        spinner = view.findViewById(R.id.spinner);
        companyRef = FirebaseDatabase.getInstance().getReference("companies");
        selectedCountry = EUGroupChat.countryNamesList.get(0);
        String[] items = new String[EUGroupChat.countryNamesList.size()];
        EUGroupChat.countryNamesList.toArray(items);
        btnViewAll = view.findViewById(R.id.btnViewAll);
        btnViewAll.setOnClickListener(this);
        etFullName = view.findViewById(R.id.etFullLegalName);
        etLegalAddress = view.findViewById(R.id.etLegalAddress);
        etPostalCode = view.findViewById(R.id.etPostalCode);
        progress = view.findViewById(R.id.progress);
        etCity = view.findViewById(R.id.etCity);
        etCountry = view.findViewById(R.id.etCountry);
        etTelephone = view.findViewById(R.id.etTelephone);
        etEmail = view.findViewById(R.id.etEmail);
        etContactPerson = view.findViewById(R.id.etContactPerson);
        etWebsite = view.findViewById(R.id.etwebsite);
        etCompanyDescription = view.findViewById(R.id.etCompanyDescription);
        etCompanyResponsibility = view.findViewById(R.id.etCompanyResponsibility);
        etStudentTasks = view.findViewById(R.id.etStudentsTasks);
        btnSave = view.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(this);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCountry = items[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayAdapter aa = new ArrayAdapter(getActivity(),android.R.layout.simple_spinner_item,items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        return view;
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnViewAll){
            Intent intent = new Intent(getActivity(), ExploreInternshipActivity.class);
            intent.putExtra("fromAdmin", true);
            startActivity(intent);
        }else if (view.getId() == R.id.btnSave){
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
            CompanyModel companyModel = new CompanyModel();
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
            companyRef.push().setValue(companyModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progress.setVisibility(View.GONE);
                    if (task.isSuccessful()){
                        Toast.makeText(getActivity(), "New company added !", Toast.LENGTH_SHORT).show();
                        etFullName.setText("");
                        etLegalAddress.setText("");
                        etCity.setText("");
                        etCountry.setText("");
                        etPostalCode.setText("");
                        etEmail.setText("");
                        etWebsite.setText("");
                        etCompanyDescription.setText("");
                        etContactPerson.setText("");
                        etCompanyResponsibility.setText("");
//                        etLegalRepresentative.setText("");
//                        etIdRepresentative.setText("");
//                        etPiva.setText("");
                        etTelephone.setText("");
                        etFullName.setText("");
                        etStudentTasks.setText("");
                    }
                }
            });
        }
    }
}