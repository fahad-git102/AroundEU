package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.fahadandroid.groupchat.adminFragments.AddCategoriesFragment;
import com.fahadandroid.groupchat.adminFragments.AddCompanyFragment;
import com.fahadandroid.groupchat.adminFragments.AddCountryFragment;
import com.fahadandroid.groupchat.adminFragments.AddGroupsFragment;
import com.fahadandroid.groupchat.adminFragments.AddNewsFragment;
import com.fahadandroid.groupchat.adminFragments.ManageBusinessListFragment;
import com.fahadandroid.groupchat.adminFragments.ManagePlacesFragment;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.MyFirebaseMessagingService;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminHomeActivity extends AppCompatActivity implements View.OnClickListener{


    private DrawerLayout dl;
    private ActionBarDrawerToggle t;
    private NavigationView nv;
    DatabaseReference usersRef;
    FirebaseAuth mAuth;
    public static final int REQUEST_WRITE_STORAGE_REQUEST_CODE = 1111;
    ImageButton openDrawer;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        if(savedInstanceState == null) {
            getSupportFragmentManager().
                    beginTransaction().replace(R.id.container,new ManageBusinessListFragment()).commit();
        }
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        dl = findViewById(R.id.activity_main);
        openDrawer = findViewById(R.id.openDrawer);
        openDrawer.setOnClickListener(this);
        t = new ActionBarDrawerToggle(this, dl,R.string.Open, R.string.Close);
        dl.addDrawerListener(t);
        t.syncState();

        mAuth = FirebaseAuth.getInstance();
        nv = findViewById(R.id.nv);
        requestAppPermissions();
        View headerView = nv.getHeaderView(0);
        ImageView image= headerView.findViewById(R.id.navlogo);
        addDeviceToken();
        Picasso.get().load(R.drawable.logo_around).centerCrop()
                .resize(150, 150).error(R.drawable.logo_around).into(image);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){
                    case R.id.manage_business_lists:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ManageBusinessListFragment()).commit();
                        dl.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.add_country:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AddCountryFragment()).commit();
                        dl.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.add_news:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AddNewsFragment()).commit();
                        dl.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.manage_places:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new ManagePlacesFragment()).commit();
                        dl.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.add_categories:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AddCategoriesFragment()).commit();
                        dl.closeDrawer(Gravity.LEFT, true);
                        break;
                    case R.id.add_groups:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AddGroupsFragment()).commit();
                        dl.closeDrawer(Gravity.LEFT, true);
                        break;

                    case R.id.add_companies:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new AddCompanyFragment()).commit();
                        dl.closeDrawer(Gravity.LEFT, true);
                        break;

                    case R.id.add_coordinators:
                        Intent intent = new Intent(AdminHomeActivity.this, AddCoordinatorActivity.class);
                        startActivity(intent);
                        break;

                    case R.id.add_emergency:
                        Intent intent65 = new Intent(AdminHomeActivity.this, AddEmergencyActivity.class);
                        startActivity(intent65);
                        break;

                    case R.id.signout:
                        AlertDialog.Builder builder = new AlertDialog.Builder(AdminHomeActivity.this);
                        builder.setTitle("Log Out");
                        builder.setMessage("Are you sure you want to Log out ?");
                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mAuth.signOut();
                                deleteDeviceToken();
                                startActivity(new Intent(AdminHomeActivity.this, LoginActivity.class));
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
        if (view.getId()==R.id.openDrawer){
                dl.openDrawer(Gravity.LEFT, true);
        }
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

}