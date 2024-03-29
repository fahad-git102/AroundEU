package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.LocationModel;
import com.fahadandroid.groupchat.models.NotificationsModel;
import com.fahadandroid.groupchat.models.PlacesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class PlacesDetailsActivity extends AppCompatActivity {

    ImageButton goBack;
    ImageView imageView;
    TextView tvText, tvTitle, tvCategory, tvLocation;
    Button btnApprove, btnDisapprove, btnDelete;
    RelativeLayout progress;
    DatabaseReference placesRef;
    LinearLayout linearLocation;
    LocationModel locationModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_details);
        goBack = findViewById(R.id.goBack);
        imageView = findViewById(R.id.imageView);
        progress = findViewById(R.id.progress);
        linearLocation = findViewById(R.id.linear_location);
        tvLocation = findViewById(R.id.tvLocation);
        tvText = findViewById(R.id.text);
        btnDelete = findViewById(R.id.btnDelete);
        btnApprove = findViewById(R.id.btnApprove);
        placesRef = FirebaseDatabase.getInstance().getReference("places");
        btnDisapprove = findViewById(R.id.btnDisapprove);
        tvTitle = findViewById(R.id.title);
        tvCategory = findViewById(R.id.tvCategory);
        String key = getIntent().getStringExtra("place");
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        boolean isDelete = getIntent().getBooleanExtra("delete", false);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvText.setMovementMethod(LinkMovementMethod.getInstance());
        Linkify.addLinks(tvText, Linkify.WEB_URLS);
        tvLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationModel!=null){
                    try {
                        String geoUri = "http://maps.google.com/maps?q=loc:" + locationModel.getLatitude()
                                + "," + locationModel.getLongitude() + " (" + locationModel.getAddress() + ")?z=12";
                        Intent mapIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                        startActivity(mapIntent);
                    }catch (Exception e){
                        Toast.makeText(PlacesDetailsActivity.this, "Please install Google Maps", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });
        placesRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try {
                    PlacesModel placesModel = snapshot.getValue(PlacesModel.class);
                    placesModel.setKey(snapshot.getKey());
                    if (placesModel.getImageUrl()!=null){
                        Glide.with(PlacesDetailsActivity.this).load(placesModel.getImageUrl()).fitCenter().placeholder(R.drawable.default_image).into(imageView);
                    }
                    if (placesModel.getDescription()!=null){
                        tvText.setText(placesModel.getDescription());
                    }
                    if (placesModel.getCategory()!=null){
                        tvCategory.setText(placesModel.getCategory());
                    }
                    for (int i = 0; i< EUGroupChat.userModelList.size(); i++){
                        if (EUGroupChat.userModelList.get(i).getUid()!=null){
                            if (EUGroupChat.userModelList.get(i).getUid().equals(placesModel.getUid())){
                                tvTitle.setText(EUGroupChat.userModelList.get(i).getFirstName()+" "+EUGroupChat.userModelList.get(i).getSurName());
                            }
                        }
                    }
                    if (placesModel.getLocation()!=null){
                        linearLocation.setVisibility(View.VISIBLE);
                        String loc = "<u>"+placesModel.getLocation().getAddress()+"</u>";
                        tvLocation.setText(Html.fromHtml(loc));
                        locationModel = placesModel.getLocation();
                    }else {
                        linearLocation.setVisibility(View.GONE);
                    }
                    if (isAdmin){
                        btnApprove.setVisibility(View.VISIBLE);
                        btnDisapprove.setVisibility(View.VISIBLE);
                        btnApprove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                progress.setVisibility(View.VISIBLE);
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", "approved");
                                placesRef.child(placesModel.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        DatabaseReference notificationsRef = FirebaseDatabase.getInstance().getReference("notifications");
                                        NotificationsModel notificationsModel = new NotificationsModel();
                                        notificationsModel.setMessage("Place Approved by admin");
                                        notificationsModel.setTitle("Place Added");
                                        notificationsModel.setDataUid(placesModel.getKey());
                                        notificationsModel.setTimeStamp(System.currentTimeMillis());

                                        notificationsRef.push().setValue(notificationsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                progress.setVisibility(View.GONE);
                                                Toast.makeText(PlacesDetailsActivity.this, "Approved !", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                        });
                                    }
                                });
                            }
                        });
                        btnDisapprove.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("status", "disapproved");
                                placesRef.child(placesModel.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        finish();
                                        Toast.makeText(PlacesDetailsActivity.this, "Disapproved !", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        });
                    }
                    if (isDelete){
                        btnDelete.setVisibility(View.VISIBLE);
                        btnDelete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PlacesDetailsActivity.this);
                                builder.setTitle("Delete ?");
                                builder.setMessage("Are you sure you want to delete this place ?");
                                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        placesRef.child(placesModel.getKey()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                finish();
                                                Toast.makeText(PlacesDetailsActivity.this, "Deleted successfully !", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                });
                                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog alertDialog = builder.create();
                                alertDialog.show();
                            }
                        });
                    }
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}