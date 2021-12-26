package com.fahadandroid.groupchat;

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

import java.util.ArrayList;
import java.util.List;

public class ExploreInternshipActivity extends AppCompatActivity implements View.OnClickListener{

    Spinner spinner;
    SearchView searchView;
    RecyclerView recycler_companies;
    String selectedCountry;
    ImageButton goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_explore_internship);
        spinner = findViewById(R.id.spinner);
        searchView = findViewById(R.id.search);
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
        selectedCountry = "Barcelona P.G";
        String[] items = new String[]{"Barcelona P.G", "Catania"};
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCountry = items[i];
                if (selectedCountry.equals("Barcelona P.G")){
                    recycler_companies.setAdapter(null);
                    CompanyAdapter adapter = new CompanyAdapter(EUGroupChat.barcelonaCompanyList, ExploreInternshipActivity.this);
                    recycler_companies.setAdapter(adapter);
                    RecycleClick.addTo(recycler_companies).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                            Intent intent = new Intent(ExploreInternshipActivity.this, CompanyDetailActivity.class);
                            intent.putExtra("company", EUGroupChat.barcelonaCompanyList.get(i));
                            intent.putExtra("companyID", EUGroupChat.barcelonaCompanyList.get(i).getKey());
                            startActivity(intent);
                        }
                    });
                }else if (selectedCountry.equals("Catania")){
                    recycler_companies.setAdapter(null);
                    CompanyAdapter adapter = new CompanyAdapter(EUGroupChat.cataniaCompanyList, ExploreInternshipActivity.this);
                    recycler_companies.setAdapter(adapter);
                    RecycleClick.addTo(recycler_companies).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                        @Override
                        public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                            Intent intent = new Intent(ExploreInternshipActivity.this, CompanyDetailActivity.class);
                            intent.putExtra("company", EUGroupChat.cataniaCompanyList.get(i));
                            intent.putExtra("companyID", EUGroupChat.cataniaCompanyList.get(i).getKey());
                            startActivity(intent);
                        }
                    });
                }
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
        if (selectedCountry.equals("Barcelona P.G")){
            List<CompanyModel> companyModelList = new ArrayList<>();
            for (CompanyModel companyModel: EUGroupChat.barcelonaCompanyList){
                boolean nameMatch = false;
                if (companyModel.getFullLegalName()!=null){
                    if (companyModel.getFullLegalName().toLowerCase().contains(text.toLowerCase())){
                        nameMatch=true;
                    }
                }
                if (nameMatch){
                    companyModelList.add(companyModel);
                }
                recycler_companies.setAdapter(null);
                CompanyAdapter adapter = new CompanyAdapter(companyModelList, ExploreInternshipActivity.this);
                recycler_companies.setAdapter(adapter);
                RecycleClick.addTo(recycler_companies).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                        Intent intent = new Intent(ExploreInternshipActivity.this, CompanyDetailActivity.class);
                        intent.putExtra("company", companyModelList.get(i));
                        intent.putExtra("companyID", companyModelList.get(i).getKey());
                        startActivity(intent);
                    }
                });

            }
        }else if (selectedCountry.equals("Catania")){
            List<CompanyModel> companyModelList = new ArrayList<>();
            for (CompanyModel companyModel: EUGroupChat.cataniaCompanyList){
                boolean nameMatch = false;
                if (companyModel.getLegalRepresentative()!=null){
                    if (companyModel.getLegalRepresentative().toLowerCase().contains(text.toLowerCase())){
                        nameMatch=true;
                    }
                }
                if (nameMatch){
                    companyModelList.add(companyModel);
                }
                recycler_companies.setAdapter(null);
                CompanyAdapter adapter = new CompanyAdapter(companyModelList, ExploreInternshipActivity.this);
                recycler_companies.setAdapter(adapter);
                RecycleClick.addTo(recycler_companies).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                        Intent intent = new Intent(ExploreInternshipActivity.this, CompanyDetailActivity.class);
                        intent.putExtra("company", companyModelList.get(i));
                        intent.putExtra("companyID", companyModelList.get(i).getKey());
                        startActivity(intent);
                    }
                });
            }
        }
    }

}