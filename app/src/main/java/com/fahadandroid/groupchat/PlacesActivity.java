package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.PlacesAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.PlacesModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.fahadandroid.groupchat.ChatActivity.PICK_IMAGE;
import static com.fahadandroid.groupchat.ChatActivity.REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE;
import static com.fahadandroid.groupchat.ChatActivity.TAKE_PHOTO;

public class PlacesActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton goBack;
    RecyclerView recyclerPlaces;
    StorageReference storageReference;
    DatabaseReference placesRef;
    ImageView add_image;
    PlacesAdapter adapter;
    Spinner spinner;
    List<String> placesKeys;
    String selectedCategory;
    List<PlacesModel> placesModelList;
    ImageButton btnAdd;
    Uri contentUri;
    String picturepath, selected, selectedCountry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_places);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        placesKeys = new ArrayList<>();
        spinner = findViewById(R.id.spinner);
        selected = "All";
        String[] items = new String[]{"All", "Bars & Restaurants", "Sightseeing places", "Experience"};
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selected = items[i];
                List<PlacesModel> places = new ArrayList<>();
                if (selected.equals("All")){
                    recyclerPlaces.setAdapter(null);
                    adapter = new PlacesAdapter(placesModelList, PlacesActivity.this, false);
                    recyclerPlaces.setAdapter(adapter);
                }else {
                    if (placesModelList.size()>0){
                        for (int a = 0; a<placesModelList.size(); a++){
                            if(placesModelList.get(a).getCategory()!=null){
                                if (placesModelList.get(a).getCategory().equals(selected)){
                                    places.add(placesModelList.get(a));
                                }
                            }
                        }
                        recyclerPlaces.setAdapter(null);
                        adapter = new PlacesAdapter(places, PlacesActivity.this, false);
                        recyclerPlaces.setAdapter(adapter);
                        RecycleClick.addTo(recyclerPlaces).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                            @Override
                            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                                Intent intent = new Intent(PlacesActivity.this, PlacesDetailsActivity.class);
                                intent.putExtra("place", places.get(i).getKey());
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(aa);

        placesModelList = new ArrayList<>();
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        recyclerPlaces = findViewById(R.id.recyclerPlaces);
        recyclerPlaces.setLayoutManager(new LinearLayoutManager(this));
        placesRef = FirebaseDatabase.getInstance().getReference("places");
        getPlaces();
    }

    @Override
    public void onClick(View view1) {
        if (view1.getId()==R.id.goBack){
            finish();
        }else if (view1.getId()==R.id.btnAdd){
            AlertDialog.Builder builder = new AlertDialog.Builder(PlacesActivity.this);
            View view = LayoutInflater.from(this).inflate(R.layout.new_places_dialog, null);
            LinearLayout linearImage = view.findViewById(R.id.linear);
            add_image = view.findViewById(R.id.add_image);
            Spinner spinner = view.findViewById(R.id.spinner);
            selectedCategory = "Bars & Restaurants";
            Spinner etCountry = view.findViewById(R.id.etCountry);
            String[] items = new String[]{"Bars & Restaurants", "Sightseeing places", "Experience"};
            spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedCategory = items[i];
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });
            ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,items);
            aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner.setAdapter(aa);
            selectedCountry = EUGroupChat.countryNamesList.get(0);
            String[] itemsCountry = new String[EUGroupChat.countryNamesList.size()];
            EUGroupChat.countryNamesList.toArray(itemsCountry);
            etCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                    selectedCountry = itemsCountry[i];
                }
                @Override
                public void onNothingSelected(AdapterView<?> adapterView) { }
            });
            ArrayAdapter aa1 = new ArrayAdapter(this, android.R.layout.simple_spinner_item,itemsCountry);
            aa1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            etCountry.setAdapter(aa1);
            builder.setView(view);
            AlertDialog alertDialog = builder.create();
            EditText etText = view.findViewById(R.id.etText);
            Button btnSave = view.findViewById(R.id.btnSave);
            linearImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(PlacesActivity.this);
                    LayoutInflater inflater = getLayoutInflater();
                    View view1 = inflater.inflate(R.layout.select_camera_action_dialog, null);
                    Button camera = view1.findViewById(R.id.camera);
                    Button gallery = view1.findViewById(R.id.gallery);
                    builder.setView(view1);
                    final AlertDialog alertDialog = builder.create();
                    camera.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if ((ContextCompat.checkSelfPermission(PlacesActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    && ContextCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PlacesActivity.this, new String[]{
                                                WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA},
                                        1919);
                            } else{
                                dispatchTakePictureIntent();
                            }
                            alertDialog.dismiss();
                        }
                    });
                    gallery.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if ((ContextCompat.checkSelfPermission(PlacesActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                                    && ContextCompat.checkSelfPermission(PlacesActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PlacesActivity.this, new String[]{
                                                WRITE_EXTERNAL_STORAGE,
                                                Manifest.permission.CAMERA},
                                        REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE);
                            } else{
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent,"Select Picture"), PICK_IMAGE);
                            }
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String description = etText.getText().toString();
                    if (description!=null&&contentUri!=null){
                        savePlaces(description);
                        alertDialog.dismiss();
                    }else {
                        Toast.makeText(PlacesActivity.this, "Please provide full data", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDialog.show();
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(PlacesActivity.this,
                        "com.fahad.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        picturepath = image.getAbsolutePath();
        return image;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO){
            if (resultCode==RESULT_OK){
                galleryAddPic();
//                setPic();
            }
        }else if (requestCode == PICK_IMAGE){
            if (resultCode == RESULT_OK){
                contentUri = data.getData();
                add_image.setImageURI(contentUri);
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(picturepath);
        Uri uri = Uri.fromFile(f);
        mediaScanIntent.setData(uri);
        sendBroadcast(mediaScanIntent);
        try {
            Bitmap bmp = HelperClass.handleSamplingAndRotationBitmap(PlacesActivity.this, uri);
            contentUri = HelperClass.getImageUri(PlacesActivity.this, bmp);
            add_image.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getPlaces(){
        adapter = new PlacesAdapter(placesModelList, this, false);
        recyclerPlaces.setAdapter(adapter);
        RecycleClick.addTo(recyclerPlaces).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                Intent intent = new Intent(PlacesActivity.this, PlacesDetailsActivity.class);
                intent.putExtra("place", placesModelList.get(i).getKey());
                startActivity(intent);
            }
        });
        placesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    PlacesModel placesModel = snapshot.getValue(PlacesModel.class);
                    placesModel.setKey(snapshot.getKey());
                    if (placesModel.getStatus()!=null){
                        if (placesModel.getStatus().equals("approved")){

                            if (EUGroupChat.currentUser.getSelectedCountry()!=null) {
                                if (placesModel.getCountry() != null && placesModel.getCountry().equals(EUGroupChat.currentUser.getSelectedCountry())) {
                                    placesModelList.add(0,placesModel);
                                    placesKeys.add(0,placesModel.getKey());
                                    adapter.notifyDataSetChanged();
                                }
                            }else {
                                placesModelList.add(0,placesModel);
                                placesKeys.add(0,placesModel.getKey());
                                adapter.notifyDataSetChanged();
                            }
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    PlacesModel placesModel = snapshot.getValue(PlacesModel.class);
                    placesModel.setKey(snapshot.getKey());
                    if (placesModel.getStatus()!=null) {
                        if (placesModel.getStatus().equals("approved")) {
                            if (EUGroupChat.currentUser.getSelectedCountry()!=null) {
                                if (placesModel.getCountry() != null && placesModel.getCountry().equals(EUGroupChat.currentUser.getSelectedCountry())) {
                                    int index = placesKeys.indexOf(placesModel.getKey());
                                    placesModelList.set(index, placesModel);
                                    adapter.notifyDataSetChanged();
                                }
                            }else {
                                int index = placesKeys.indexOf(placesModel.getKey());
                                placesModelList.set(index, placesModel);
                                adapter.notifyDataSetChanged();
                            }

                        }else {
                            int index = placesKeys.indexOf(placesModel.getKey());
                            placesModelList.remove(index);
                            adapter.notifyDataSetChanged();
                        }
                    }

                }catch (Exception e){}
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try{
                    int index = placesKeys.indexOf(snapshot.getKey());
                    placesModelList.remove(index);
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

    private void savePlaces(String description){
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        File file = new File(contentUri.toString());
        storageReference.child(file.getName()).putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        PlacesModel placesModel = new PlacesModel();
                        placesModel.setDescription(description);
                        placesModel.setImageUrl(url);
                        if (selectedCategory!=null){
                            placesModel.setCountry(selectedCountry);
                        }
                        placesModel.setStatus("pending");
                        placesModel.setCategory(selectedCategory);
                        placesModel.setUid(mAuth.getCurrentUser().getUid());
                        placesModel.setTimeStamp(System.currentTimeMillis());
                        placesRef.push().setValue(placesModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    AlertDialog.Builder builder = new AlertDialog.Builder(PlacesActivity.this);
                                    builder.setMessage("Place sent for approval successfully !");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.setCancelable(false);
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.show();
                                }
                            }
                        });
                    }
                });
            }
        });

    }

}