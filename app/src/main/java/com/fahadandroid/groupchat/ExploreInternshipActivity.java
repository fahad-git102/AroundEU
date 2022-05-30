package com.fahadandroid.groupchat;

import static com.fahadandroid.groupchat.helpers.EUGroupChat.allCompanyList;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.CompanyAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExploreInternshipActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner spinner;
    SearchView searchView;
    RecyclerView recycler_companies;
    String selectedCountry;
    boolean fromAdmin = false;
    ImageButton goBack;
    List<CompanyModel> selectedCompanyModelList;
    CompanyAdapter adapter;

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter!=null){
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_internship);
        fromAdmin = getIntent().getBooleanExtra("fromAdmin", false);
        spinner = findViewById(R.id.spinner);
        searchView = findViewById(R.id.search);
        selectedCompanyModelList = new ArrayList<>();
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });
        searchView.clearFocus();
        if (searchView != null){
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    search(newText);
                    return true;
                }
            });
        }
        recycler_companies = findViewById(R.id.recycler_companies);
        recycler_companies.setLayoutManager(new LinearLayoutManager(this));
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        selectedCountry = EUGroupChat.countryNamesList.get(0);
        String[] items = new String[EUGroupChat.countryNamesList.size()];
        EUGroupChat.countryNamesList.toArray(items);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCountry = items[i];

                selectedCompanyModelList = new ArrayList<>();
                selectedCompanyModelList.clear();

                for (int a = 0; a<allCompanyList.size(); a++){
                    if (allCompanyList.get(a).getSelectedCountry().equals(selectedCountry)||allCompanyList.get(a).getSelectedCountry().contains(selectedCountry)){
                        selectedCompanyModelList.add(allCompanyList.get(a));
                    }
                }
                recycler_companies.setAdapter(null);
                adapter = new CompanyAdapter(selectedCompanyModelList, ExploreInternshipActivity.this, fromAdmin);
                recycler_companies.setAdapter(adapter);
                RecycleClick.addTo(recycler_companies).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                        Intent intent = new Intent(ExploreInternshipActivity.this, CompanyDetailActivity.class);
                        intent.putExtra("company", selectedCompanyModelList.get(i));
                        intent.putExtra("companyID", selectedCompanyModelList.get(i).getKey());
                        intent.putExtra("fromAdmin", fromAdmin);
                        startActivity(intent);
                    }
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.goBack){
            finish();
        }
    }

    private void search(String text){

        List<CompanyModel> companyModelList = new ArrayList<>();
        for (CompanyModel companyModel: selectedCompanyModelList){
            boolean nameMatch = false;
            if (companyModel.getFullLegalName()!=null){
            }
            if (companyModel.getFullLegalName().toLowerCase().contains(text.toLowerCase())){
                nameMatch=true;
            }
            if (nameMatch){
                companyModelList.add(companyModel);
            }
            recycler_companies.setAdapter(null);
            adapter = new CompanyAdapter(companyModelList, ExploreInternshipActivity.this, fromAdmin);
            recycler_companies.setAdapter(adapter);
            RecycleClick.addTo(recycler_companies).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                    Intent intent = new Intent(ExploreInternshipActivity.this, CompanyDetailActivity.class);
                    intent.putExtra("company", companyModelList.get(i));
                    intent.putExtra("companyID", companyModelList.get(i).getKey());
                    intent.putExtra("fromAdmin", fromAdmin);
                    startActivity(intent);
                }
            });

        }


//        if (selectedCountry.equals("Barcellona P.G")){
//            List<CompanyModel> companyModelList = new ArrayList<>();
//            for (CompanyModel companyModel: EUGroupChat.barcelonaCompanyList){
//                boolean nameMatch = false;
//                if (companyModel.getFullLegalName()!=null){
//               }
//                if (companyModel.getFullLegalName().toLowerCase().contains(text.toLowerCase())){
//                    nameMatch=true;
//                }
//                if (nameMatch){
//                    companyModelList.add(companyModel);
//                }
//                recycler_companies.setAdapter(null);
//                adapter = new CompanyAdapter(companyModelList, ExploreInternshipActivity.this, fromAdmin);
//                recycler_companies.setAdapter(adapter);
//                RecycleClick.addTo(recycler_companies).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(RecyclerView recyclerView, int i, View view) {
//                        Intent intent = new Intent(ExploreInternshipActivity.this, CompanyDetailActivity.class);
//                        intent.putExtra("company", companyModelList.get(i));
//                        intent.putExtra("companyID", companyModelList.get(i).getKey());
//                        intent.putExtra("fromAdmin", fromAdmin);
//                        startActivity(intent);
//                    }
//                });
//
//            }
//        }else if (selectedCountry.equals("Catania")){
//            List<CompanyModel> companyModelList = new ArrayList<>();
//            for (CompanyModel companyModel: EUGroupChat.cataniaCompanyList){
//                boolean nameMatch = false;
//                boolean fullLegalNameMatch = false;
//                if (companyModel.getLegalRepresentative()!=null){
//                    if (companyModel.getLegalRepresentative().toLowerCase().contains(text.toLowerCase())){
//                        nameMatch=true;
//                    }
//                }
//                if (companyModel.getFullLegalName()!=null){
//                    if (companyModel.getFullLegalName().toLowerCase().contains(text.toLowerCase())){
//                        fullLegalNameMatch=true;
//                    }
//                }
//                if (nameMatch||fullLegalNameMatch){
//                    companyModelList.add(companyModel);
//                    nameMatch = false;
//                    fullLegalNameMatch = false;
//                }
//                recycler_companies.setAdapter(null);
//                adapter = new CompanyAdapter(companyModelList, ExploreInternshipActivity.this, fromAdmin);
//                recycler_companies.setAdapter(adapter);
//                RecycleClick.addTo(recycler_companies).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
//                    @Override
//                    public void onItemClicked(RecyclerView recyclerView, int i, View view) {
//                        Intent intent = new Intent(ExploreInternshipActivity.this, CompanyDetailActivity.class);
//                        intent.putExtra("company", companyModelList.get(i));
//                        intent.putExtra("companyID", companyModelList.get(i).getKey());
//                        intent.putExtra("fromAdmin", fromAdmin);
//                        startActivity(intent);
//                    }
//                });
//            }
//        }
    }

}