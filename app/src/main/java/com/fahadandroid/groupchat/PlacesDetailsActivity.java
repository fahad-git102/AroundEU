package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.PlacesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class PlacesDetailsActivity extends AppCompatActivity {

    ImageButton goBack;
    ImageView imageView;
    TextView tvText, tvTitle, tvCategory;
    PlacesModel placesModel;
    Button btnApprove, btnDisapprove, btnDelete;
    DatabaseReference placesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places_details);
        goBack = findViewById(R.id.goBack);
        imageView = findViewById(R.id.imageView);
        tvText = findViewById(R.id.text);
        btnDelete = findViewById(R.id.btnDelete);
        btnApprove = findViewById(R.id.btnApprove);
        placesRef = FirebaseDatabase.getInstance().getReference("places");
        btnDisapprove = findViewById(R.id.btnDisapprove);
        tvTitle = findViewById(R.id.title);
        tvCategory = findViewById(R.id.tvCategory);
        placesModel = getIntent().getParcelableExtra("place");
        boolean isAdmin = getIntent().getBooleanExtra("isAdmin", false);
        boolean isDelete = getIntent().getBooleanExtra("delete", false);
        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        if (placesModel!=null){
            if (placesModel.getImageUrl()!=null){
                Glide.with(this).load(placesModel.getImageUrl()).placeholder(R.drawable.default_image).into(imageView);
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
            if (isAdmin){
                btnApprove.setVisibility(View.VISIBLE);
                btnDisapprove.setVisibility(View.VISIBLE);
                btnApprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Map<String, Object> map = new HashMap<>();
                        map.put("status", "approved");
                        placesRef.child(placesModel.getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                finish();
                                Toast.makeText(PlacesDetailsActivity.this, "Approved !", Toast.LENGTH_SHORT).show();
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
        }
    }
}