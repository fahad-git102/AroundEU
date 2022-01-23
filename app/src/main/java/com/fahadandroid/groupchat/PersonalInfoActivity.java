package com.fahadandroid.groupchat;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fahadandroid.groupchat.adapters.StringHorizontalAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.ComapnyTimeScheduledModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.fahadandroid.groupchat.ChatActivity.PICK_IMAGE;
import static com.fahadandroid.groupchat.ChatActivity.REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE;
import static com.fahadandroid.groupchat.ChatActivity.TAKE_PHOTO;

public class PersonalInfoActivity extends AppCompatActivity implements View.OnClickListener{

    LinearLayout linearEdit;
    ImageButton goBack;
    String picturepath;
    TextView tvUserName, tvDob, tvEmail, tvName, tvAbout, tvWorksAt, tvWorkTitle;
    CircleImageView profilePic, mainPic;
    StorageReference storageReference;
    FirebaseDatabase firebaseDatabase;
    TextView tvMorningStart, tvMorningTo, tvNoonStart, tvNoonTo;
    DatabaseReference usersRef, companyTimeScheduledRef;
    Uri contentUri;
    FirebaseAuth mAuth;
    RecyclerView recyclerWorkingDays;
    CompanyModel myCompany;
    LinearLayout linear_work;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mainPic = findViewById(R.id.mainPic);
        tvMorningStart = findViewById(R.id.tvMorningStartTime);
        tvMorningTo = findViewById(R.id.tvMorningToTime);
        tvNoonStart = findViewById(R.id.tvNoonStart);
        tvNoonTo = findViewById(R.id.tvNoonTo);
        linear_work = findViewById(R.id.linear_work);
        recyclerWorkingDays = findViewById(R.id.recycler_working_days);
        recyclerWorkingDays.setLayoutManager(new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
        tvWorksAt = findViewById(R.id.tvWorksAt);
        tvAbout = findViewById(R.id.tvAbout);
        tvWorkTitle = findViewById(R.id.tvWorkTitle);
        usersRef = firebaseDatabase.getReference("Users");
        companyTimeScheduledRef = FirebaseDatabase.getInstance().getReference("companyTimeScheduled");
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        linearEdit = findViewById(R.id.linearEdit);
        linearEdit.setOnClickListener(this);
        goBack = findViewById(R.id.goBack);
        goBack.setOnClickListener(this);
        tvUserName = findViewById(R.id.tvUserName);
        tvEmail = findViewById(R.id.tvEmail);
        tvName = findViewById(R.id.tvName);
        tvDob = findViewById(R.id.tvDob);
        tvWorksAt.setOnClickListener(this);
        getData();
        getCompany();
    }

    private void getData(){
        usersRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    UserModel userModel = snapshot.getValue(UserModel.class);
                    if (userModel.getProfileUrl()!=null){
                        Picasso.get().load(userModel.getProfileUrl()).placeholder(R.drawable.default_user_img).into(mainPic);
                    }else {
                        mainPic.setImageDrawable(getResources().getDrawable(R.drawable.default_user_img));
                    }
                    tvUserName.setText(userModel.getFirstName());
                    tvEmail.setText(userModel.getEmail());
                    tvDob.setText(userModel.getDob());
                    tvName.setText(userModel.getFirstName()+" "+userModel.getSurName());
                    if (userModel.getAbout()!=null){
                        tvAbout.setText(userModel.getAbout());
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view1) {
        if (view1.getId()==R.id.linearEdit){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view = LayoutInflater.from(PersonalInfoActivity.this).inflate(R.layout.edit_personal_info_dialog, null);
            profilePic = view.findViewById(R.id.profilePic);
            EditText etFirstName = view.findViewById(R.id.etFirstName);
            EditText etSecondName = view.findViewById(R.id.etSecondName);
            EditText etAboutMe = view.findViewById(R.id.etAboutMe);
            Button btnSave = view.findViewById(R.id.btnSave);
            if (EUGroupChat.currentUser!=null){
                if (EUGroupChat.currentUser.getProfileUrl()!=null){
                    Picasso.get().load(EUGroupChat.currentUser.getProfileUrl()).placeholder(R.drawable.default_user_img).into(profilePic);
                }else {
                    profilePic.setImageDrawable(getResources().getDrawable(R.drawable.default_user_img));
                }
                etFirstName.setText(EUGroupChat.currentUser.getFirstName());
                etSecondName.setText(EUGroupChat.currentUser.getSurName());
                if (EUGroupChat.currentUser.getAbout()!=null){
                    etAboutMe.setText(EUGroupChat.currentUser.getAbout());
                }
            }
            builder.setView(view);
            AlertDialog alertDialog = builder.create();
            profilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showImageDialog();
                }
            });
            btnSave.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String firstName = etFirstName.getText().toString();
                    String secondName = etSecondName.getText().toString();
                    String aboutMe = etAboutMe.getText().toString();
                    if (TextUtils.isEmpty(firstName)||TextUtils.isEmpty(secondName)){
                        if (TextUtils.isEmpty(firstName)){
                            etFirstName.setError("First name required !");
                        }
                        if (TextUtils.isEmpty(secondName)){
                            etSecondName.setError("Second name required !");
                        }
                        return;
                    }

                    saveData(firstName, secondName, aboutMe, alertDialog);
                }
            });
            alertDialog.show();
        }else if (view1.getId()==R.id.goBack){
            finish();
        }else if (view1.getId()==R.id.tvWorksAt){
            if (myCompany!=null){
                Intent intent = new Intent(PersonalInfoActivity.this, CompanyDetailActivity.class);
                intent.putExtra("company", myCompany);
                intent.putExtra("companyID", myCompany.getKey());
                startActivity(intent);
            }
        }
    }

    private void saveData(String firstname, String secondname, String about, AlertDialog alertDialog){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        if (contentUri!=null){
            File file = new File(contentUri.toString());
            storageReference.child(file.getName()).putFile(contentUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Map<String, Object> map = new HashMap<>();
                            map.put("profileUrl", url);
                            map.put("firstName", firstname);
                            map.put("surName", secondname);
                            map.put("about", about);
                            usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        alertDialog.dismiss();
                                        getData();
                                        Toast.makeText(PersonalInfoActivity.this, "Personal Information updated !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }else {
            Map<String, Object> map = new HashMap<>();
            map.put("firstName", firstname);
            map.put("surName", secondname);
            map.put("about", about);
            usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()){
                        alertDialog.dismiss();
                        getData();
                        Toast.makeText(PersonalInfoActivity.this, "Personal Information updated !", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getCompany(){
        companyTimeScheduledRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
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
                                    linear_work.setVisibility(View.VISIBLE);
                                    tvWorksAt.setVisibility(View.VISIBLE);
                                    tvWorkTitle.setVisibility(View.VISIBLE);
                                    try {
                                        if (!myCompany.getFullLegalName().isEmpty()){
                                            tvWorksAt.setText(myCompany.getFullLegalName());
                                        }else if (!myCompany.getLegalRepresentative().isEmpty()){
                                            tvWorksAt.setText(myCompany.getLegalRepresentative());
                                        }
                                        tvMorningStart.setText(companyTimeScheduledModel.getMorningFrom());
                                        tvMorningTo.setText(companyTimeScheduledModel.getMorningTo());
                                        tvNoonStart.setText(companyTimeScheduledModel.getNoonFrom());
                                        tvNoonTo.setText(companyTimeScheduledModel.getNoonTo());
                                        if (companyTimeScheduledModel.getSelectedDays()!=null){
                                            List<String> list = new ArrayList<>();
                                            for (int i = 0; i<companyTimeScheduledModel.getSelectedDays().size(); i++){
                                                list.add(companyTimeScheduledModel.getSelectedDays().get(i).name());
                                            }
                                            StringHorizontalAdapter adapter = new StringHorizontalAdapter(list,
                                                    PersonalInfoActivity.this);
                                            recyclerWorkingDays.setAdapter(adapter);
                                        }

                                    }catch (Exception e){
                                        tvWorksAt.setText("Company name not found !");
                                    }
                                }else {
                                    tvWorksAt.setVisibility(View.GONE);
                                    tvWorkTitle.setVisibility(View.GONE);
                                    linear_work.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                }catch (Exception e){}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showImageDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(PersonalInfoActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view1 = inflater.inflate(R.layout.select_camera_action_dialog, null);
        Button camera = view1.findViewById(R.id.camera);
        Button gallery = view1.findViewById(R.id.gallery);
        builder.setView(view1);
        final AlertDialog alertDialog = builder.create();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if ((ContextCompat.checkSelfPermission(PersonalInfoActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        && ContextCompat.checkSelfPermission(PersonalInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PersonalInfoActivity.this, new String[]{
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

                if ((ContextCompat.checkSelfPermission(PersonalInfoActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        && ContextCompat.checkSelfPermission(PersonalInfoActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PersonalInfoActivity.this, new String[]{
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
                Uri photoURI = FileProvider.getUriForFile(PersonalInfoActivity.this,
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
                if (profilePic!=null){
                    profilePic.setImageURI(contentUri);
                }
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
            Bitmap bmp = HelperClass.handleSamplingAndRotationBitmap(PersonalInfoActivity.this, uri);
            contentUri = HelperClass.getImageUri(PersonalInfoActivity.this, bmp);
            profilePic.setImageBitmap(bmp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}