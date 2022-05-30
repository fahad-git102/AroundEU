package com.fahadandroid.groupchat.adminFragments;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fahadandroid.groupchat.HomeActivity;
import com.fahadandroid.groupchat.NewsActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.NewsModel;
import com.fahadandroid.groupchat.models.NotificationsModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.yalantis.ucrop.UCrop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.app.Activity.RESULT_OK;
import static com.fahadandroid.groupchat.ChatActivity.PICK_IMAGE;
import static com.fahadandroid.groupchat.ChatActivity.REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE;
import static com.fahadandroid.groupchat.ChatActivity.TAKE_PHOTO;

public class AddNewsFragment extends Fragment implements View.OnClickListener{

    ImageView btnAddImage;
    StorageReference storageReference;
    EditText etText, etTitle;
    FirebaseFunctions mFunctions;
    Spinner etCountry;
    Uri contentUri;
    FirebaseAuth mAuth;
    String selectedCountry;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference newsRef, notificationsRef;
    String picturepath;
    Button btnSubmit, btnAllNews;

    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddNewsFragment() {
        // Required empty public constructor
    }

    public static AddNewsFragment newInstance(String param1, String param2) {
        AddNewsFragment fragment = new AddNewsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_news, container, false);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        etCountry = view.findViewById(R.id.etCountry);
        btnAllNews = view.findViewById(R.id.btnAllNews);
        btnAllNews.setOnClickListener(this);
        newsRef = firebaseDatabase.getReference("news");
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        notificationsRef = firebaseDatabase.getReference("notifications");
        etText = view.findViewById(R.id.etText);
        etTitle = view.findViewById(R.id.etTitle);
        btnAddImage = view.findViewById(R.id.add_image);
        btnAddImage.setOnClickListener(this);
        btnSubmit = view.findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
        selectedCountry = EUGroupChat.countryNamesList.get(0);
        String[] items = new String[EUGroupChat.countryNamesList.size()];
        EUGroupChat.countryNamesList.toArray(items);
        etCountry.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectedCountry = items[i];
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) { }
        });
        ArrayAdapter aa = new ArrayAdapter(getActivity(), android.R.layout.simple_spinner_item,items);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        etCountry.setAdapter(aa);
        return view;
    }

    @Override
    public void onClick(View view1) {
        if (view1.getId()==R.id.btnAllNews){
            Intent intent1 = new Intent(requireContext(), NewsActivity.class);
            startActivity(intent1);
        }else if (view1.getId()==R.id.add_image){
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getLayoutInflater();
            View view = inflater.inflate(R.layout.select_camera_action_dialog, null);
            Button camera = view.findViewById(R.id.camera);
            Button gallery = view.findViewById(R.id.gallery);
            builder.setView(view);
            final AlertDialog alertDialog = builder.create();
            camera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ((ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{
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

                    if ((ContextCompat.checkSelfPermission(getActivity(), WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                            && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{
                                        WRITE_EXTERNAL_STORAGE,
                                        Manifest.permission.CAMERA},
                                REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE);
                    } else{
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto , PICK_IMAGE);
                    }
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }else if (view1.getId()==R.id.btnSubmit){
            String description = etText.getText().toString();
            String title = etTitle.getText().toString();
            if (description.isEmpty()){
                etText.setError("Description required");
                return;
            }
            if (title.isEmpty()){
                etTitle.setError("Title required");
                return;
            }
            if (contentUri==null){
                Toast.makeText(getActivity(), "Image Required", Toast.LENGTH_SHORT).show();
                return;
            }
            uploadNews(description, title);
        }
    }

    private void uploadNews(String description, String title){
        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
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
                        NewsModel newsModel = new NewsModel(description, url, mAuth.getCurrentUser().getUid(), System.currentTimeMillis());
                        newsModel.setTitle(title);
                        newsModel.setCountry(selectedCountry);
                        String pushKey = newsRef.push().getKey();
                        if (pushKey!=null){
                            newsRef.child(pushKey).setValue(newsModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    sendNotification(pushKey);
                                    progressDialog.dismiss();
                                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                                    builder.setTitle("Success");
                                    builder.setMessage("Post uploaded successfully !");
                                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                            etText.setText("");
                                            contentUri = null;
                                            btnAddImage.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.ic_photo));
                                        }
                                    });
                                    AlertDialog alertDialog = builder.create();
                                    alertDialog.setCanceledOnTouchOutside(false);
                                    alertDialog.setCancelable(false);
                                    alertDialog.show();
                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void cropImage(@NonNull Uri uri){
        String destinationFileName = "MatesRates";
        destinationFileName += ".jpg";
        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getActivity().getCacheDir(), destinationFileName)));
        uCrop.withAspectRatio(1,1);
        uCrop.withMaxResultSize(450,450);
        uCrop.withOptions(getCropOptions());
        uCrop.start(requireContext(), AddNewsFragment.this);
    }

    private UCrop.Options getCropOptions(){
        UCrop.Options options = new UCrop.Options();
        options.setCompressionQuality(100);
        options.setHideBottomControls(false);
        options.setFreeStyleCropEnabled(true);
        options.setStatusBarColor(getResources().getColor(R.color.colorBlueLight));
        options.setToolbarColor(getResources().getColor(R.color.colorSilver));
        options.setToolbarTitle("Crop Image");
        return options;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
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
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        picturepath = image.getAbsolutePath();
        return image;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO){
            if (resultCode==RESULT_OK){
                galleryAddPic();
                setPic();
            }
        }else if (requestCode == PICK_IMAGE){
            if (resultCode == RESULT_OK){
                try{
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturepath = cursor.getString(columnIndex);
                    cursor.close();
                    if (selectedImage!=null) {
                        cropImage(selectedImage);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        }else if (requestCode == UCrop.REQUEST_CROP && resultCode==RESULT_OK&&data!=null) {

            contentUri = UCrop.getOutput(data);
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), contentUri);
                File file = new File(picturepath);
                OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                os.close();
                btnAddImage.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(picturepath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        getActivity().sendBroadcast(mediaScanIntent);
    }

    private void setPic() {
        int targetW = 250;
        int targetH = 250;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bmp = BitmapFactory.decodeFile(picturepath, bmOptions);
        Uri uri = HelperClass.getImageUri(getActivity(), bmp);
        cropImage(uri);
    }


    private void sendNotification(String newsId){
        String title = "News Added";
        String message = "Admin add a new news";

        NotificationsModel notificationsModel = new NotificationsModel();
        notificationsModel.setMessage(message);
        notificationsModel.setTitle(title);
        notificationsModel.setDataUid(newsId);
        notificationsModel.setTimeStamp(System.currentTimeMillis());

        notificationsRef.push().setValue(notificationsModel);

    }

}