package com.fahadandroid.groupchat.helpers;

import android.app.Application;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fahadandroid.groupchat.ApiCall.RetrofitAPIClient;
import com.fahadandroid.groupchat.models.CompanyApiModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.fahadandroid.groupchat.models.CountryModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EUGroupChat extends Application {
    public static List<UserModel> userModelList;
    List<String> userKeys, countryKeys, barcelonaKeys, cataniaKeys;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, countriesRef, companiesRef;
    public static List<CompanyModel> barcelonaCompanyList, cataniaCompanyList;
    public static UserModel currentUser;
    public static List<CountryModel> countryModelList;
    public static List<CompanyApiModel> companiesList;
    FirebaseAuth mAuth;
    @Override
    public void onCreate() {
        super.onCreate();
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        countriesRef = firebaseDatabase.getReference("countries");
        companiesRef = firebaseDatabase.getReference("companies");
        barcelonaCompanyList = new ArrayList<>();
        cataniaCompanyList = new ArrayList<>();
        getUsers();
        getCountryList();
        populateCompaniesLists();
    }
    private void getUsers(){
        userModelList = new ArrayList<>();
        userKeys = new ArrayList<>();
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUid()!=null){
                        userModelList.add(userModel);
                        userKeys.add(userModel.getUid());
                        if (mAuth.getCurrentUser()!=null){
                            if (userModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                                currentUser = userModel;
                            }
                        }
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUid()!=null){
                        int index = userKeys.indexOf(userModel.getUid());
                        userModelList.set(index, userModel);
                        if (mAuth.getCurrentUser()!=null){
                            if (userModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                                currentUser = userModel;
                            }
                        }
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try{
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getUid()!=null) {
                        int index = userKeys.indexOf(userModel.getUid());
                        userModelList.remove(index);
                        userKeys.remove(userModel.getUid());
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
        usersRef.addChildEventListener(childEventListener);
    }
    private void getCountryList(){
        countryModelList = new ArrayList<>();
        countryKeys = new ArrayList<>();
        countriesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CountryModel countryModel = snapshot.getValue(CountryModel.class);
                    countryModel.setKey(snapshot.getKey());
                    countryModelList.add(countryModel);
                    countryKeys.add(countryModel.getKey());
                }catch (Exception e){

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CountryModel countryModel = snapshot.getValue(CountryModel.class);
                    countryModel.setKey(snapshot.getKey());
                    int index = countryKeys.indexOf(countryModel.getKey());
                    countryModelList.set(index, countryModel);
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    int index = countryKeys.indexOf(snapshot.getKey());
                    countryModelList.remove(index);
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
    private void populateCompaniesLists(){
        barcelonaKeys = new ArrayList<>();
        cataniaKeys = new ArrayList<>();
        companiesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CompanyModel companyModel = snapshot.getValue(CompanyModel.class);
                    companyModel.setKey(snapshot.getKey());
                    if (companyModel.getSelectedCountry()!=null){
                        if (companyModel.getSelectedCountry().equals("Barcellona P.G")){
                            barcelonaCompanyList.add(companyModel);
                            barcelonaKeys.add(companyModel.getKey());
                        }else if (companyModel.getSelectedCountry().equals("Catania")){
                            cataniaCompanyList.add(companyModel);
                            cataniaKeys.add(companyModel.getKey());
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CompanyModel companyModel = snapshot.getValue(CompanyModel.class);
                    companyModel.setKey(snapshot.getKey());
                    if (companyModel.getSelectedCountry()!=null){
                        if (companyModel.getSelectedCountry().equals("Barcellona P.G")){
                            int index = barcelonaKeys.indexOf(companyModel.getKey());
                            barcelonaCompanyList.set(index, companyModel);
                        }else if (companyModel.getSelectedCountry().equals("Catania")){
                            int index = cataniaKeys.indexOf(companyModel.getKey());
                            cataniaCompanyList.set(index, companyModel);
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    CompanyModel companyModel = snapshot.getValue(CompanyModel.class);
                    companyModel.setKey(snapshot.getKey());
                    if (companyModel.getSelectedCountry()!=null){
                        if (companyModel.getSelectedCountry().equals("Barcellona P.G")){
                            int index = barcelonaKeys.indexOf(companyModel.getKey());
                            barcelonaCompanyList.remove(index);
                        }else if (companyModel.getSelectedCountry().equals("Catania")){
                            int index = cataniaKeys.indexOf(companyModel.getKey());
                            cataniaCompanyList.remove(index);
                        }
                    }
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

}
