package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.MyFirebaseMessagingService;
import com.fahadandroid.groupchat.models.ComapnyTimeScheduledModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.fahadandroid.groupchat.models.CountryModel;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout btnProfile, btnJoinGroup, btnNews, btnPlaces, btnCategories, btnSearch;
    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    FirebaseAuth mAuth;
    TextView tvCordinatorsCountry;
    CardView progress;
    CompanyModel myCompany;
    String cordinatorCountry;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference usersRef, groupsRef, countriesRef, companyTimeScheduledRef;
    boolean groupMatched = false;
    GroupsModel matchedGroup;
    ImageButton openDrawer, btnNotifications;
    public static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 1111;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        dl = findViewById(R.id.activity_home_drawer);
        openDrawer = findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(this);
        btnCategories = findViewById(R.id.btnCategories);
        btnCategories.setOnClickListener(this);
        progress = findViewById(R.id.progress);
        btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(this);
        btnNotifications = findViewById(R.id.notifications);
        btnNotifications.setOnClickListener(this);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        AutoRequestAllPermissions();
        dl.addDrawerListener(t);
        t.syncState();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        usersRef = firebaseDatabase.getReference("Users");
        groupsRef = firebaseDatabase.getReference("groups");
        countriesRef = firebaseDatabase.getReference("countries");
        companyTimeScheduledRef = firebaseDatabase.getReference("companyTimeScheduled");
        nv = findViewById(R.id.nv);
        View headerView = nv.getHeaderView(0);
        ImageView image= headerView.findViewById(R.id.navlogo);
        addDeviceToken();
        Picasso.get().load(R.drawable.logo_around).centerCrop()
                .resize(150, 150).error(R.drawable.logo_around).into(image);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.website:
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://eprojectconsult.com/"));
                        startActivity(browserIntent);
                        break;
                    case R.id.facebook:
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("fb://facewebmodal/f?href=https://www.facebook.com/eprojectconsult"))); //12345 is Facebook page number
                        } catch (Exception e) {
                            //open link in browser
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                    .parse("https://www.facebook.com/eprojectconsult/")));
                        }
                        break;
                    case R.id.instagram:
                        Intent instagram = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/eprojectconsult/"));
                        startActivity(instagram);
                        break;
                    case R.id.linkedIn:
                        Intent linkedin = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/company/eprojectconsult-office"));
                        startActivity(linkedin);
                        break;
                    case R.id.twitter:
                        Intent twitter = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/EprojectConsult"));
                        startActivity(twitter);
                        break;
                    case R.id.youtube:
                        Intent youtube = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/user/eprojectconsult/videos"));
                        startActivity(youtube);
                        break;
                    case R.id.coordinators:
                        Intent intentl = new Intent(HomeActivity.this, ContactsInfoActivity.class);
                        intentl.putExtra("from", "coordinate");
                        startActivity(intentl);
                        break;
                    case R.id.emergency:
                        Intent intentl1 = new Intent(HomeActivity.this, ContactsInfoActivity.class);
                        intentl1.putExtra("from", "emergency");
                        startActivity(intentl1);
                        break;
                    case R.id.office_contact:
                        Intent intentl13 = new Intent(HomeActivity.this, ContactsInfoActivity.class);
                        intentl13.putExtra("from", "office");
                        startActivity(intentl13);
                        break;
                    case R.id.signout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        builder.setTitle("Log Out");
                        builder.setMessage("Are you sure you want to Log out ?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE).edit().clear().apply();
                                mAuth.signOut();
                                deleteDeviceToken();
                                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                                finish();
                            }
                        });
                        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                        break;
                }
                return false;
            }
        });
        openDrawer = findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(this);
        btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(this);
        btnJoinGroup = findViewById(R.id.btnJoinGroup);
        btnJoinGroup.setOnClickListener(this);
        btnPlaces = findViewById(R.id.btnPlaces);
        btnPlaces.setOnClickListener(this);
        btnNews = findViewById(R.id.btnNews);
        btnNews.setOnClickListener(this);
        tvCordinatorsCountry = findViewById(R.id.tvCordinatorsCountry);

        if (EUGroupChat.currentUser!=null){
            if (EUGroupChat.currentUser.getUserType().equals("Cordinator")){
                SharedPreferences prfs = getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE);
                cordinatorCountry = prfs.getString("country", "");
                if (cordinatorCountry!=null){
                    if (cordinatorCountry.equals("")){
                        getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE).edit().clear().apply();
                        mAuth.signOut();
                        deleteDeviceToken();
                        startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                        finish();
                    }else {
                        tvCordinatorsCountry.setVisibility(View.VISIBLE);
                        String text = "<u>Cordinator's Country: <b>"+cordinatorCountry+"</b></u>";
                        tvCordinatorsCountry.setText(Html.fromHtml(text));
                    }
                }else {
                    getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE).edit().clear().apply();
                    mAuth.signOut();
                    deleteDeviceToken();
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }

        requestAppPermissions();
    }

    private void deleteDeviceToken(){
        List<String> deviceTokens = new ArrayList<>();
        if (EUGroupChat.currentUser!=null){
            if (EUGroupChat.currentUser.getDeviceTokens()!=null){
                deviceTokens = EUGroupChat.currentUser.getDeviceTokens();
            }
            String myToken = MyFirebaseMessagingService.getToken(this);
            if (myToken!=null){
                if (deviceTokens.contains(myToken)){
                    deviceTokens.remove(myToken);
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("deviceTokens", deviceTokens);
            usersRef.child(EUGroupChat.currentUser.getUid()).updateChildren(map);
        }
    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(HomeActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(HomeActivity.this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1289);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1289:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can use local drive .");
                } else {
                    Log.e("value", "Permission Denied, You cannot use local drive .");
                }
                break;
        }
    }

    void AutoRequestAllPermissions(){
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){return;}
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), PackageManager.GET_PERMISSIONS);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        if(info==null){return;}
        String[] permissions = info.requestedPermissions;
        boolean remained = false;
        for (String permission : permissions) {
            if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                remained = true;
            }
        }
        if(remained) {
            requestPermissions(permissions, 0);
        }
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit App");
        builder.setMessage("Are you sure you want to exit app ?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    private void requestAppPermissions() {
        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        if (hasReadPermissions() && hasWritePermissions() && hasCameraPermissions()) {
            return;
        }

        ActivityCompat.requestPermissions(this,
                new String[] {
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                }, REQUEST_WRITE_STORAGE_REQUEST_CODE); // your request code
    }

    private boolean hasReadPermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasWritePermissions() {
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }

    private boolean hasCameraPermissions(){
        return (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED);
    }

    private void addDeviceToken(){
        List<String> deviceTokens = new ArrayList<>();
        if (EUGroupChat.currentUser!=null){
            if (EUGroupChat.currentUser.getDeviceTokens()!=null){
                deviceTokens = EUGroupChat.currentUser.getDeviceTokens();
            }
            String myToken = MyFirebaseMessagingService.getToken(this);
            if (myToken!=null){
                if (!deviceTokens.contains(myToken)){
                    deviceTokens.add(myToken);
                }
            }
            Map<String, Object> map = new HashMap<>();
            map.put("deviceTokens", deviceTokens);
            usersRef.child(EUGroupChat.currentUser.getUid()).updateChildren(map);
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId()==R.id.btnProfile){
            Intent intent = new Intent(HomeActivity.this, ProfileActivity.class);
            startActivity(intent);
        }else if (view.getId()==R.id.btnJoinGroup){
            if (EUGroupChat.currentUser!=null){
                manageGroupChats();
            }
        }else if (view.getId()==R.id.btnNews){
            Intent intent1 = new Intent(HomeActivity.this, NewsActivity.class);
            startActivity(intent1);
        }else if (view.getId()==R.id.btnPlaces){
            Intent intent1 = new Intent(HomeActivity.this, PlacesActivity.class);
            startActivity(intent1);
        }else if (view.getId()==R.id.openDrawer){
            dl.openDrawer(Gravity.LEFT, true);
        }else if (view.getId()==R.id.notifications){
            Intent intent13 = new Intent(HomeActivity.this, NotificationsActivity.class);
            startActivity(intent13);
        }else if (view.getId()==R.id.btnCategories){
            Intent intent14 = new Intent(HomeActivity.this, CategoriesActivity.class);
            startActivity(intent14);
        }else if (view.getId()==R.id.btnSearch){
            progress.setVisibility(View.VISIBLE);
            companyTimeScheduledRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try{
                        boolean companyFound = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                            ComapnyTimeScheduledModel companyTimeScheduledModel = snapshot.getValue(ComapnyTimeScheduledModel.class);
                            if (companyTimeScheduledModel.getUid()!=null){
                                if (companyTimeScheduledModel.getUid().equals(mAuth.getCurrentUser().getUid())){
                                    String id = companyTimeScheduledModel.getCompanyId();
                                    boolean barcelonaMatch = false;
                                    for (int i = 0; i<EUGroupChat.barcelonaCompanyList.size(); i++){
                                        if (EUGroupChat.barcelonaCompanyList.get(i).getKey().equals(id)){
                                            barcelonaMatch = true;
                                            myCompany = EUGroupChat.barcelonaCompanyList.get(i);
                                        }
                                    }
                                    if (!barcelonaMatch){
                                        for (int i = 0; i<EUGroupChat.cataniaCompanyList.size(); i++){
                                            if (EUGroupChat.cataniaCompanyList.get(i).getKey().equals(id)){
                                                myCompany = EUGroupChat.cataniaCompanyList.get(i);
                                            }
                                        }
                                    }
                                    if (myCompany!=null){
                                       companyFound = true;
                                    }
                                }
                            }
                        }

                        if (companyFound){
                            progress.setVisibility(View.GONE);
                            Intent intent = new Intent(HomeActivity.this, CompanyDetailActivity.class);
                            intent.putExtra("company", myCompany);
                            intent.putExtra("companyID", myCompany.getKey());
                            startActivity(intent);
                        }else {
                            progress.setVisibility(View.GONE);
                            Intent i = new Intent(HomeActivity.this, ExploreInternshipActivity.class);
                            startActivity(i);
                        }

                    }catch (Exception e){
                        progress.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    progress.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void manageGroupChats(){
        if (EUGroupChat.currentUser.getUserType()!=null){
            if (EUGroupChat.currentUser.getUserType().toLowerCase().equals("student")){
                if (EUGroupChat.currentUser.isJoined()){
                    Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                    startActivity(intent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                    View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.pincode_dialog_layout, null);
                    TextView tvText = view.findViewById(R.id.tvText);
                    tvText.setVisibility(View.VISIBLE);
                    EditText etPin = view.findViewById(R.id.etPincode);
                    Button btnJoin = view.findViewById(R.id.btnJoin);
                    ProgressBar progressBar = view.findViewById(R.id.progress);
                    builder.setView(view);
                    AlertDialog alertDialog = builder.create();
                    btnJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String pin = etPin.getText().toString();
                            if (TextUtils.isEmpty(pin)){
                                etPin.setError("Pin Required");
                                return;
                            }
                            progressBar.setVisibility(View.VISIBLE);
                            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    progressBar.setVisibility(View.GONE);
                                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                        try {
                                            GroupsModel groupsModel = dataSnapshot.getValue(GroupsModel.class);
                                            groupsModel.setKey(dataSnapshot.getKey());
                                            if (groupsModel.getPincode()!=null){
                                                if (groupsModel.getPincode().equals(pin)){
                                                        matchedGroup = groupsModel;
                                                        groupMatched = true;

                                                }
                                            }
                                        }catch (Exception e){}
                                    }
                                    if (groupMatched){
                                        if (!matchedGroup.isDeleted()){
                                            Toast.makeText(HomeActivity.this, "Pin matched", Toast.LENGTH_SHORT).show();
                                            List<String> approvedMembers = new ArrayList<>();
//
                                            if (matchedGroup.getApprovedMembers()!=null){
                                                approvedMembers = matchedGroup.getApprovedMembers();
                                                if (!approvedMembers.contains(mAuth.getCurrentUser().getUid())){
                                                    approvedMembers.add(mAuth.getCurrentUser().getUid());
                                                }
                                            }else {
                                                approvedMembers.add(mAuth.getCurrentUser().getUid());
                                            }
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("approvedMembers", approvedMembers);
                                            groupsRef.child(matchedGroup.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("joined", true);
                                                    usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()){
                                                                alertDialog.dismiss();
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(HomeActivity.this, "Group Joined !", Toast.LENGTH_SHORT).show();
                                                                Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
                                                                intent.putExtra("group", matchedGroup.getKey());
                                                                startActivity(intent);
                                                            }else {
                                                                alertDialog.dismiss();
                                                                progressBar.setVisibility(View.GONE);
                                                                Toast.makeText(HomeActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            }
                                                        }
                                                    });
                                                }
                                            });
                                        }else {
                                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                            builder.setTitle("Chat Unavailable");
                                            builder.setMessage("This group is deleted by admin.");
                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.dismiss();
                                                }
                                            });
                                            AlertDialog alertDialog = builder.create();
                                            alertDialog.show();
                                        }
                                    }else {
                                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                        builder.setTitle("Error");
                                        builder.setMessage("Pincode not matched with any group.");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                dialogInterface.dismiss();
                                            }
                                        });
                                        AlertDialog alertDialog1 = builder.create();
                                        alertDialog1.show();
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
            }else if (EUGroupChat.currentUser.getUserType().toLowerCase().equals("teacher")){
                if (!EUGroupChat.currentUser.isJoined()){
                    if (EUGroupChat.currentUser.getSelectedCountry()!=null){
                        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                        View view = LayoutInflater.from(HomeActivity.this).inflate(R.layout.pincode_dialog_layout, null);
                        TextView tvText = view.findViewById(R.id.tvText);
                        tvText.setVisibility(View.VISIBLE);
                        tvText.setText("Enter the pincode of your Country");
                        EditText etPin = view.findViewById(R.id.etPincode);
                        Button btnJoin = view.findViewById(R.id.btnJoin);
                        ProgressBar progressBar = view.findViewById(R.id.progress);
                        builder.setView(view);
                        AlertDialog alertDialog = builder.create();
                        btnJoin.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                String pin = etPin.getText().toString();
                                if (TextUtils.isEmpty(pin)){
                                    etPin.setError("Pin Required");
                                    return;
                                }
                                progressBar.setVisibility(View.VISIBLE);
                                countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                                            try {
                                                CountryModel countryModel = dataSnapshot.getValue(CountryModel.class);
                                                countryModel.setKey(dataSnapshot.getKey());
                                                if (EUGroupChat.currentUser.getSelectedCountry()!=null){
                                                    if (countryModel.getCountryName().equals(EUGroupChat.currentUser.getSelectedCountry())){
                                                        if (pin.equals(countryModel.getPincode())){
                                                            progressBar.setVisibility(View.GONE);
                                                            Intent intent = new Intent(HomeActivity.this, SelectBusinessListActivity.class);
                                                            intent.putExtra("country", countryModel.getKey());
                                                            startActivity(intent);
                                                            alertDialog.dismiss();
                                                        }else {
                                                            progressBar.setVisibility(View.GONE);
                                                            AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
                                                            builder.setTitle("Error");
                                                            builder.setMessage("Pincode not matched with your country.");
                                                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    dialogInterface.dismiss();
                                                                }
                                                            });
                                                            AlertDialog alertDialog1 = builder.create();
                                                            alertDialog1.show();
                                                        }
                                                    }
                                                }else {
                                                    Intent intent = new Intent(HomeActivity.this, SelectCountryActivity.class);
                                                    startActivity(intent);
                                                }

                                            }catch (Exception e){}
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            }
                        });
                        alertDialog.show();
                    }else {
                        Intent intent = new Intent(HomeActivity.this, SelectCountryActivity.class);
                        startActivity(intent);
                    }
                }else {
                    Intent intent = new Intent(HomeActivity.this, ChatActivity.class);
//                    intent.putExtra("group", matchedGroup.getKey());
                    startActivity(intent);
                }

            }else if (EUGroupChat.currentUser.getUserType().toLowerCase().equals("cordinator")){

                if (cordinatorCountry!=null){
                    Intent intent = new Intent(HomeActivity.this, SelectBusinessListActivity.class);
                    intent.putExtra("cordinatorCountry", true);
                    startActivity(intent);
                }
//                if (EUGroupChat.currentUser.getSelectedCountry()!=null){
//                    countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//                            for (DataSnapshot dataSnapshot : snapshot.getChildren()){
//                                try {
//                                    CountryModel countryModel = dataSnapshot.getValue(CountryModel.class);
//                                    countryModel.setKey(dataSnapshot.getKey());
//                                    if (EUGroupChat.currentUser.getSelectedCountry()!=null){
//                                        if (countryModel.getCountryName().equals(EUGroupChat.currentUser.getSelectedCountry())){
//                                            Intent intent = new Intent(HomeActivity.this, SelectBusinessListActivity.class);
//                                            intent.putExtra("country", countryModel.getKey());
//                                            startActivity(intent);
//                                        }
//                                    }else {
//                                        Intent intent = new Intent(HomeActivity.this, SelectCountryActivity.class);
//                                        startActivity(intent);
//                                    }
//
//                                }catch (Exception e){}
//                            }
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//
//                        }
//                    });
//                }
            }else {
                getSharedPreferences("Cordinator_Country", Context.MODE_PRIVATE).edit().clear().apply();
                mAuth.signOut();
                deleteDeviceToken();
                startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                finish();
            }
        }
    }


}