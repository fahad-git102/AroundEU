package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.CategoryPdfsAdapter;
import com.fahadandroid.groupchat.adapters.CountrySmallDialogAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.models.CategoryPdfsModel;
import com.fahadandroid.groupchat.models.CountryModel;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PdfsListActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recycler_pdfs;
    FloatingActionButton fab;
    String type;
    Uri fileUri;
    StorageReference storageReference;
    List<CategoryPdfsModel> list;
    ImageButton goBack;
    List<String> keys;
    DatabaseReference categoriesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfs_list);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        type = getIntent().getExtras().getString("type");
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        categoriesRef = FirebaseDatabase.getInstance().getReference("categories");
        recycler_pdfs = findViewById(R.id.recycler_pdfs);
        recycler_pdfs.setLayoutManager(new LinearLayoutManager(this));
        fab = findViewById(R.id.btnAdd);
        fab.setOnClickListener(this);
        list = new ArrayList<>();
        keys = new ArrayList<>();
        if (EUGroupChat.currentUser!=null){
            if (!EUGroupChat.currentUser.isAdmin()){
                fab.setVisibility(View.GONE);
            }
        }
        getPdfs();
    }

    @Override
    public void onClick(View vi) {
        if (vi.getId()==R.id.goBack){
            finish();
        }else if (vi.getId()==R.id.btnAdd){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View v = LayoutInflater.from(this).inflate(R.layout.new_category_dialog, null);
            TextView tvCountry = v.findViewById(R.id.tvCountry);
            EditText etName = v.findViewById(R.id.etName);
            LinearLayout linear_file = v.findViewById(R.id.linear_file);
            Button btnSave = v.findViewById(R.id.btnSave);
            builder.setView(v);
            AlertDialog alertDialog = builder.create();
            tvCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PdfsListActivity.this);
                    View view = LayoutInflater.from(PdfsListActivity.this).inflate(R.layout.country_select_dialog, null);
                    RecyclerView recyclerCountries = view.findViewById(R.id.recycler_countries);
                    recyclerCountries.setLayoutManager(new LinearLayoutManager(PdfsListActivity.this));
                    List<CountryModel> countriesList = new ArrayList<>();
                    builder.setView(view);
                    AlertDialog alertDia = builder.create();
                    DatabaseReference countriesRef = FirebaseDatabase.getInstance().getReference("countries");
                    countriesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                                try{
                                    CountryModel countryModel = dataSnapshot.getValue(CountryModel.class);
                                    countryModel.setKey(dataSnapshot.getKey());
                                    if (!countryModel.isDeleted()){
                                        countriesList.add(countryModel);
                                    }
                                }catch (Exception e){}
                            }
                            CountrySmallDialogAdapter adapter = new CountrySmallDialogAdapter(countriesList, PdfsListActivity.this);
                            recyclerCountries.setAdapter(adapter);
                            RecycleClick.addTo(recyclerCountries).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                                @Override
                                public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                                    tvCountry.setText(countriesList.get(i).getCountryName());
                                    alertDia.dismiss();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    alertDia.show();
                }
            });
            linear_file.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, 1234);
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String country = tvCountry.getText().toString();
                    String name = etName.getText().toString();
                    if (name.isEmpty()||country.isEmpty()||fileUri==null){
                        Toast.makeText(PdfsListActivity.this, "Every field required", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveFile(country, name);
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }


   private void saveFile(String country, String name){
       final ProgressDialog progressDialog = new ProgressDialog(PdfsListActivity.this);
       progressDialog.setMessage("Please wait....");
       progressDialog.setCancelable(false);
       progressDialog.setCanceledOnTouchOutside(false);
       progressDialog.show();
       File file = new File(fileUri.toString());
       storageReference.child(file.getName()).putFile(fileUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
           @Override
           public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
               storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                   @Override
                   public void onSuccess(Uri uri) {
                       progressDialog.dismiss();
                       String url = uri.toString();
                       CategoryPdfsModel model = new CategoryPdfsModel();
                       model.setCategory(type);
                       model.setFileUrl(url);
                       model.setTimeStamp(System.currentTimeMillis());
                       model.setCountry(country);
                       model.setName(name);
                       String key = categoriesRef.push().getKey();
                       model.setCategoryId(key);
                       categoriesRef.child(key).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                           @Override
                           public void onComplete(@NonNull Task<Void> task) {
                               progressDialog.dismiss();
                           }
                       });
                   }
               });
           }
       });
   }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1234){
            try {
                fileUri = data.getData();
            }catch (Exception e){}
        }
    }

    private void getPdfs(){
        CategoryPdfsAdapter adapter = new CategoryPdfsAdapter(list, this);
        recycler_pdfs.setAdapter(adapter);

        RecycleClick.addTo(recycler_pdfs).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(PdfsListActivity.this, LoadPdfActivity.class);
                intent.putExtra("url", list.get(i).getFileUrl());
                startActivity(intent);
            }
        });

        Query query = categoriesRef.orderByChild("category").equalTo(type);
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CategoryPdfsModel model = snapshot.getValue(CategoryPdfsModel.class);
                    if (model.getCategoryId()==null){
                        model.setCategoryId(snapshot.getKey());
                    }
                    if (EUGroupChat.currentUser.getSelectedCountry()!=null) {
                        if (model.getCountry()!=null){
                            if (model.getCountry().equals(EUGroupChat.currentUser.getSelectedCountry())) {
                                list.add(0, model);
                                keys.add(0, model.getCategoryId());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    CategoryPdfsModel model = snapshot.getValue(CategoryPdfsModel.class);
                    if (model.getCategoryId()==null){
                        model.setCategoryId(snapshot.getKey());
                    }
                    if (EUGroupChat.currentUser.getSelectedCountry()!=null) {
                        if (model.getCountry() != null) {
                            if (model.getCountry().equals(EUGroupChat.currentUser.getSelectedCountry())) {
                                int index = keys.indexOf(model.getCategoryId());
                                list.set(index, model);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    int index = keys.indexOf(snapshot.getKey());
                    list.remove(index);
                    adapter.notifyDataSetChanged();
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