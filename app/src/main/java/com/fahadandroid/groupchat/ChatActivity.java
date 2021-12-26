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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.adapters.MentionUserAdapter;
import com.fahadandroid.groupchat.adapters.MessagesAdapter;
import com.fahadandroid.groupchat.adapters.UsersAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.models.ComapnyTimeScheduledModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.fahadandroid.groupchat.models.MessagesModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{

    ImageButton goBack, btnRecord;
    GroupsModel thisgroupsModel;
    String key;
    UserModel mentionedUser;
    MediaRecorder recorder;
    String fileName;
    public static final int GALLERY = 31, CAMERA = 32;
    DatabaseReference usersRef;
    RecyclerView recyclerUsersToMention;
    TextView tvGroupName;
    FirebaseFunctions mFunctions;
    public final static int REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE = 1919;
    public static final int PICK_IMAGE = 1001;
    public static final int TAKE_PHOTO = 1000;
    List<String> messagesKeys;
    DatabaseReference companyTimeModelRef;
    ComapnyTimeScheduledModel myCompanySchedule ;
    List<MessagesModel> messagesModelList;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    String picturepath;
    MessagesAdapter adapter;
    EditText etTypeHere;
    RecyclerView recycler_chat;
    ImageButton btnSend, btnAdd, btnInfo;
    LinearLayoutManager linearLayoutManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference groupsRef, chatRef;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    // Requesting permission to RECORD_AUDIO
    private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        messagesModelList = new ArrayList<>();
        goBack = findViewById(R.id.goBack);
        btnInfo = findViewById(R.id.btnInfo);
        btnInfo.setOnClickListener(this);
        recycler_chat = findViewById(R.id.recycler_chat);
        etTypeHere = findViewById(R.id.etTypeHere);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        companyTimeModelRef = FirebaseDatabase.getInstance().getReference("companyTimeScheduled");
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recycler_chat.setLayoutManager(linearLayoutManager);
        mAuth = FirebaseAuth.getInstance();
        messagesKeys = new ArrayList<>();
        recyclerUsersToMention = findViewById(R.id.recycler_users_to_mention);
        recyclerUsersToMention.setLayoutManager(new LinearLayoutManager(this));
        tvGroupName = findViewById(R.id.tvGroupName);
        firebaseDatabase = FirebaseDatabase.getInstance();
        mFunctions = FirebaseFunctions.getInstance();
        groupsRef = firebaseDatabase.getReference("groups");
        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(this);
        btnAdd = findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);
        goBack.setOnClickListener(this);
        btnRecord = findViewById(R.id.btnRecord);
        key = getIntent().getStringExtra("group");

        btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                    startRecording();
                    etTypeHere.setTextColor(getResources().getColor(R.color.colorGreen));
                    etTypeHere.setText("Recording...");
                }else if (motionEvent.getAction() == MotionEvent.ACTION_UP){
                    stopRecording();
                    etTypeHere.setTextColor(getResources().getColor(R.color.colorDarkGrey));
                    etTypeHere.setText("");
                }

                return false;
            }
        });

        etTypeHere.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                if (text.length()>0){
                    Pattern p = Pattern.compile("[@][a-zA-Z0-9-.]+");
                    Matcher m = p.matcher(text);
                    int cursorPosition = etTypeHere.getSelectionStart();
                    while(m.find())
                    {
                        if (cursorPosition >= m.start() && cursorPosition <= m.end())
                        {
                            final int s = m.start() + 1; // add 1 to ommit the "@" tag
                            final int e = m.end();
                            getAllMembers(editable.toString().substring(1));
                            break;
                        }
                    }
                }else {
                    recyclerUsersToMention.setVisibility(View.GONE);
                }

            }
        });

        if (key!=null){
            chatRef = firebaseDatabase.getReference("groups").child(key).child("messages");
            getChat();
            getMessages();
        }else {
            groupsRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    boolean isFound = false;

                    for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                        try {
                            GroupsModel groupsModel = dataSnapshot.getValue(GroupsModel.class);
                            groupsModel.setKey(dataSnapshot.getKey());

                            if (groupsModel.getApprovedMembers()!=null){
                                if (groupsModel.getApprovedMembers().contains(mAuth.getCurrentUser().getUid())){

                                    if (groupsModel.isDeleted()){
                                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                                        builder.setTitle("Chat Unavailable");
                                        builder.setMessage("This group is unavailable.");
                                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                Map<String, Object> map = new HashMap<>();
                                                map.put("joined", false);
                                                usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        dialogInterface.dismiss();
                                                        finish();
                                                    }
                                                });
                                            }
                                        });
                                        AlertDialog alertDialog = builder.create();
                                        alertDialog.show();
                                    }else {
                                        isFound = true;
                                        key = groupsModel.getKey();
                                        chatRef = firebaseDatabase.getReference("groups").child(key).child("messages");
                                        getChat();
                                        getMessages();
                                    }


                                }
                            }

                        }catch (Exception e){}
                    }

                    if (!isFound){
                        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
                        builder.setTitle("Chat Unavailable");
                        builder.setMessage("This group is unavailable.");
                        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Map<String, Object> map = new HashMap<>();
                                map.put("joined", false);
                                usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                });
                            }
                        });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(ChatActivity.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void getAllMembers(String str){
        List<UserModel> approvedMembers = new ArrayList<>();
        List<UserModel> refinedUsers = new ArrayList<>();

        for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
            for (int i = 0; i<thisgroupsModel.getApprovedMembers().size(); i++){
                if (thisgroupsModel.getApprovedMembers().get(i).equals(EUGroupChat.userModelList.get(a).getUid())){
                    approvedMembers.add(EUGroupChat.userModelList.get(a));
                }
            }
        }

        for (int i = 0; i<approvedMembers.size(); i++){
            if (approvedMembers.get(i).getFirstName().contains(str)||approvedMembers.get(i).getSurName().contains(str)){
                refinedUsers.add(approvedMembers.get(i));
            }
        }

        if (approvedMembers.size()>0){
            recyclerUsersToMention.setVisibility(View.VISIBLE);
            MentionUserAdapter adapter = new MentionUserAdapter(refinedUsers, this);
            recyclerUsersToMention.setAdapter(adapter);
            RecycleClick.addTo(recyclerUsersToMention).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                @Override
                public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                    String text = "<b>" + refinedUsers.get(i).getFirstName()+" "+refinedUsers.get(i).getSurName() + "</b>";
                    etTypeHere.setText(Html.fromHtml(text));
                    refinedUsers.clear();
                    mentionedUser = refinedUsers.get(i);
                    recyclerUsersToMention.setVisibility(View.GONE);
                }
            });
        }else {
            recyclerUsersToMention.setVisibility(View.GONE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted  = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void getMessages(){
        adapter = new MessagesAdapter(messagesModelList, this);
        recycler_chat.setAdapter(adapter);
        groupsRef.child(key).child("messages").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    MessagesModel messagesModel = snapshot.getValue(MessagesModel.class);
                    messagesModel.setKey(snapshot.getKey());
                    messagesModelList.add(messagesModel);
                    messagesKeys.add(snapshot.getKey());
                    adapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPosition(messagesModelList.size()-1);
                }catch (Exception e){
                    Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    MessagesModel messagesModel = snapshot.getValue(MessagesModel.class);
                    messagesModel.setKey(snapshot.getKey());
                    int index = messagesKeys.indexOf(messagesModel.getKey());
                    messagesModelList.set(index,messagesModel);
                    adapter.notifyDataSetChanged();
                    linearLayoutManager.scrollToPosition(messagesModelList.size()-1);
                }catch (Exception e){
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                String key = snapshot.getKey();
                int index = messagesKeys.indexOf(key);
                try{
                    messagesModelList.remove(index);
                    adapter.notifyDataSetChanged();

                }catch (Exception e){
                    getChat();
                }
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getChat(){
        groupsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    GroupsModel groupsModel = snapshot.getValue(GroupsModel.class);
                    if(groupsModel.getKey()==null){
                        groupsModel.setKey(snapshot.getKey());
                    }
                    if (groupsModel.getKey().equals(key)){
                        thisgroupsModel = groupsModel;
                        tvGroupName.setText(groupsModel.getName());
                    }
                }catch (Exception e){
                    Toast.makeText(ChatActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try{
                    GroupsModel groupsModel = snapshot.getValue(GroupsModel.class);
                    if(groupsModel.getKey()==null){
                        groupsModel.setKey(snapshot.getKey());
                    }
                    if (groupsModel.getKey().equals(key)){
                        thisgroupsModel = groupsModel;
                        tvGroupName.setText(groupsModel.getName());
                    }
                }catch (Exception e){

                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.btnInfo){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view1 = LayoutInflater.from(this).inflate(R.layout.group_info_dialog, null);
            TextView tvName = view1.findViewById(R.id.etName);
            LinearLayout linear_file = view1.findViewById(R.id.linear_file);
            ImageButton btnGroupMembers = view1.findViewById(R.id.btnGroupMembers);
            TextView tvAccomodation = view1.findViewById(R.id.tvAccomodation);

            btnGroupMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (thisgroupsModel!=null){
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
                        View v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.users_list_dialog, null);
                        RecyclerView recyclerUsers = v.findViewById(R.id.recycler_users);
                        TextView tvNoData = v.findViewById(R.id.tvNoData);
                        recyclerUsers.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                        List<UserModel> usersList = new ArrayList<>();
                        for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
                            if (thisgroupsModel.getApprovedMembers()!=null){
                                for (int i = 0; i<thisgroupsModel.getApprovedMembers().size(); i++){
                                    if (EUGroupChat.userModelList.get(a).getUid().equals(thisgroupsModel.getApprovedMembers().get(i))){
                                        usersList.add(EUGroupChat.userModelList.get(a));
                                    }
                                }
                            }
                        }
                        if (usersList.size()>0){
                            tvNoData.setVisibility(View.GONE);
                            UsersAdapter adapter = new UsersAdapter(usersList, ChatActivity.this);
                            recyclerUsers.setAdapter(adapter);
                            RecycleClick.addTo(recyclerUsers).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                                @Override
                                public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                                    showUsersData(usersList.get(i).getUid());
                                }
                            });
                        }else {
                            tvNoData.setVisibility(View.VISIBLE);
                        }
                        builder1.setView(v);
                        AlertDialog alertDialog = builder1.create();
                        alertDialog.show();
                    }
                }
            });

            if (key!=null){
                groupsRef.child(key).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        try {
                            GroupsModel groupsModel = snapshot.getValue(GroupsModel.class);
                            if (groupsModel.getName()!=null){
                                tvName.setText(groupsModel.getName());
                            }
                            if (groupsModel.getCategory()!=null){
                                tvAccomodation.setText(groupsModel.getCategory());
                            }
                            if (groupsModel.getFileUrl()!=null){
                                linear_file.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Intent intent = new Intent(ChatActivity.this, LoadPdfActivity.class);
                                        intent.putExtra("url", groupsModel.getFileUrl());
                                        startActivity(intent);
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
            Button btnOK = view1.findViewById(R.id.btnOk);
            builder.setView(view1);
            AlertDialog alertDialog = builder.create();
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();

        }else if (view.getId()==R.id.btnSend){

            String message = etTypeHere.getText().toString();
            if (!message.isEmpty()){
                final Map<String, Object> map = new HashMap<>();
                map.put("timeStamp", ServerValue.TIMESTAMP);
                map.put("uid", mAuth.getCurrentUser().getUid());
                map.put("message", message);

                chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            etTypeHere.setText("");
                            if (thisgroupsModel.getApprovedMembers()!=null){
                                List<String> list = new ArrayList<>();
                                for (int i = 0; i<thisgroupsModel.getApprovedMembers().size(); i++){
                                    if (!thisgroupsModel.getApprovedMembers().get(i).equals(mAuth.getCurrentUser().getUid())){
                                        for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
                                            if (EUGroupChat.userModelList.get(a).getUid().equals(thisgroupsModel.getApprovedMembers().get(i))){
                                                if (EUGroupChat.userModelList.get(a).getDeviceTokens()!=null){
                                                    list.addAll(EUGroupChat.userModelList.get(a).getDeviceTokens());
                                                }
                                            }
                                        }
                                    }
                                }
                                sendNotification(list);
                                if (mentionedUser!=null){
                                    sendMentionNotification();
                                }
                            }
                        }
                    }
                });
            }



        }else if (view.getId()==R.id.goBack){
            if(adapter!=null)
                adapter.stopPlayers();
            finish();
        }else if (view.getId()==R.id.btnAdd){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            LayoutInflater inflater = getLayoutInflater();
            View view1 = inflater.inflate(R.layout.image_video_dialog, null);
            Button video = view1.findViewById(R.id.video);
            Button image = view1.findViewById(R.id.image);
            Button documents = view1.findViewById(R.id.documents);
            builder.setView(view1);
            final AlertDialog alertDialog = builder.create();
            documents.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    intent.setType("application/pdf");
                    startActivityForResult(intent, 1234);
                    alertDialog.dismiss();
                }
            });
            video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getVideosMedia();
                    alertDialog.dismiss();
                }
            });
            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getMedia();
                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(adapter!=null)
            adapter.stopPlayers();
    }

    private void getVideosMedia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.select_camera_action_dialog, null);
        Button camera = view.findViewById(R.id.camera);
        Button gallery = view.findViewById(R.id.gallery);
        camera.setText("Record Video");
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeVideoFromCamera();
                alertDialog.dismiss();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseVideoFromGallary();
                alertDialog.dismiss();
            }
        });
        alertDialog.show();
    }

    private void getMedia(){
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.select_camera_action_dialog, null);
        Button camera = view.findViewById(R.id.camera);
        Button gallery = view.findViewById(R.id.gallery);
        builder.setView(view);
        final AlertDialog alertDialog = builder.create();
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ((ContextCompat.checkSelfPermission(ChatActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        && ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{
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

                if ((ContextCompat.checkSelfPermission(ChatActivity.this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                        && ContextCompat.checkSelfPermission(ChatActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ChatActivity.this, new String[]{
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
                Uri photoURI = FileProvider.getUriForFile(ChatActivity.this,
                        "com.fahad.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == TAKE_PHOTO){
            if (resultCode==RESULT_OK){
                galleryAddPic();
            }
        }else if (requestCode == PICK_IMAGE && resultCode == RESULT_OK  && data != null){
            if(data.getClipData() != null){

                int count = data.getClipData().getItemCount();
                for (int i=0; i<count; i++){

                    Uri imageUri = data.getClipData().getItemAt(i).getUri();
                    sendMessageWithAttachment(imageUri);
                }
            }
            else if(data.getData() != null){

                Uri imgUri = data.getData();
                sendMessageWithAttachment(imgUri);
            }

        }else if (requestCode == GALLERY){
            if (data != null) {
                Uri contentURI = data.getData();
                String selectedVideoPath = HelperClass.getPath(contentURI, ChatActivity.this);
                Log.d("path",selectedVideoPath);
                File file1 = new File(selectedVideoPath);
                sendMessageWithVideo(contentURI, file1);
            }
        }else if (requestCode==CAMERA){
            Uri contentURI = data.getData();
            final String recordedVideoPath = HelperClass.getPath(contentURI, this);
            File file = new File(recordedVideoPath);
            sendMessageWithVideo(contentURI, file);
        }else if (requestCode == 1234){
            Uri uri = data.getData();
            sendMessageWithDoc(uri);
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(picturepath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);

        try {
            Bitmap bmp = HelperClass.handleSamplingAndRotationBitmap(ChatActivity.this, contentUri);
            Uri uri = HelperClass.getImageUri(ChatActivity.this, bmp);
            sendMessageWithAttachment(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void sendMessageWithAttachment(Uri uri){
        final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        File file = new File(uri.toString());
        storageReference.child(file.getName()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Map<String, Object> map = new HashMap<>();
                        map.put("timeStamp", ServerValue.TIMESTAMP);
                        map.put("uid", mAuth.getCurrentUser().getUid());
                        map.put("image", url);
                        chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    etTypeHere.setText("");
                                    if (thisgroupsModel.getApprovedMembers()!=null){
                                        List<String> list = new ArrayList<>();
                                        for (int i = 0; i<thisgroupsModel.getApprovedMembers().size(); i++){
                                            if (!thisgroupsModel.getApprovedMembers().get(i).equals(mAuth.getCurrentUser().getUid())){
                                                for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
                                                    if (EUGroupChat.userModelList.get(a).getUid().equals(thisgroupsModel.getApprovedMembers().get(i))){
                                                        if (EUGroupChat.userModelList.get(a).getDeviceTokens()!=null){
                                                            list.addAll(EUGroupChat.userModelList.get(a).getDeviceTokens());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        sendNotification(list);
                                        if (mentionedUser!=null){
                                            sendMentionNotification();
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void sendMessageWithDoc(Uri uri){
        final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        File file = new File(uri.toString());
        storageReference.child(file.getName()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Map<String, Object> map = new HashMap<>();
                        map.put("timeStamp", ServerValue.TIMESTAMP);
                        map.put("uid", mAuth.getCurrentUser().getUid());
                        map.put("document", url);
                        chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    etTypeHere.setText("");
                                    if (thisgroupsModel.getApprovedMembers()!=null){
                                        List<String> list = new ArrayList<>();
                                        for (int i = 0; i<thisgroupsModel.getApprovedMembers().size(); i++){
                                            if (!thisgroupsModel.getApprovedMembers().get(i).equals(mAuth.getCurrentUser().getUid())){
                                                for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
                                                    if (EUGroupChat.userModelList.get(a).getUid().equals(thisgroupsModel.getApprovedMembers().get(i))){
                                                        if (EUGroupChat.userModelList.get(a).getDeviceTokens()!=null){
                                                            list.addAll(EUGroupChat.userModelList.get(a).getDeviceTokens());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        sendNotification(list);
                                        if (mentionedUser!=null){
                                            sendMentionNotification();
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void sendMessageWithVideo(Uri uri, File file){
        final ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
        progressDialog.setMessage("Please wait....");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        storageReference.child(file.getName()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String url = uri.toString();
                        Map<String, Object> map = new HashMap<>();
                        map.put("timeStamp", ServerValue.TIMESTAMP);
                        map.put("uid", mAuth.getCurrentUser().getUid());
                        map.put("video", url);
                        chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    etTypeHere.setText("");
                                    if (thisgroupsModel.getApprovedMembers()!=null){
                                        List<String> list = new ArrayList<>();
                                        for (int i = 0; i<thisgroupsModel.getApprovedMembers().size(); i++){
                                            if (!thisgroupsModel.getApprovedMembers().get(i).equals(mAuth.getCurrentUser().getUid())){
                                                for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
                                                    if (EUGroupChat.userModelList.get(a).getUid().equals(thisgroupsModel.getApprovedMembers().get(i))){
                                                        if (EUGroupChat.userModelList.get(a).getDeviceTokens()!=null){
                                                            list.addAll(EUGroupChat.userModelList.get(a).getDeviceTokens());
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                        sendNotification(list);
                                        if (mentionedUser!=null){
                                            sendMentionNotification();
                                        }
                                    }
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    public void chooseVideoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takeVideoFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    private Task<String> cloudNotification(Map<String, Object> data) {

        return mFunctions
                .getHttpsCallable("sendNotification")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void sendNotification(List<String> stringList){
        Map<String, Object> map = new HashMap<>();
        String title = thisgroupsModel.getName();
        String message = EUGroupChat.currentUser.getFirstName()+" sent a message in your group";
        map.put("title", title);
        map.put("message", message);
        map.put("deviceToken", stringList);

        cloudNotification(map);
    }

    private void sendMentionNotification(){
        Map<String, Object> map = new HashMap<>();
        String title = thisgroupsModel.getName();
        String message = mentionedUser.getFirstName()+ " "+ mentionedUser.getSurName() + " mentioned you in a message.";
        map.put("title", title);
        map.put("message", message);
        map.put("deviceToken", mentionedUser.getDeviceTokens());
        cloudNotification(map);
        mentionedUser = null;
    }

    private void showUsersData(String uid){
        Context context = ChatActivity.this;
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.users_info_dialog, null);
        CircleImageView profilePic = view.findViewById(R.id.profilePic);
        TextView tvFirstName = view.findViewById(R.id.etFirstName);
        TextView tvWorksAt = view.findViewById(R.id.tvWorksAt);
        TextView tvAbout = view.findViewById(R.id.etAboutMe);
        TextView tvEmail = view.findViewById(R.id.tvEmail);
        TextView tvPhone = view.findViewById(R.id.tvPhoneNumber);

        LinearLayout linearWork = view.findViewById(R.id.linearWork);
        ImageView btnDetails = view.findViewById(R.id.btnDetails);
        for (int i = 0 ; i<EUGroupChat.userModelList.size(); i++){
            if (EUGroupChat.userModelList.get(i).getUid().equals(uid)){
                if (EUGroupChat.userModelList.get(i).getProfileUrl()!=null){
                    Glide.with(context).load(EUGroupChat.userModelList.get(i).getProfileUrl()).placeholder(R.drawable.default_image).into(profilePic);
                }
                tvFirstName.setText(EUGroupChat.userModelList.get(i).getFirstName()+" "+EUGroupChat.userModelList.get(i).getSurName());
                if (EUGroupChat.userModelList.get(i).getAbout()!=null){
                    tvAbout.setText(EUGroupChat.userModelList.get(i).getAbout());
                }
                if (EUGroupChat.userModelList.get(i).getPhone()!=null){
                    tvPhone.setText(EUGroupChat.userModelList.get(i).getPhone());
                }else {
                    tvPhone.setText("---");
                }

                int finalI = i;
                tvPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (EUGroupChat.userModelList.get(finalI).getPhone()!=null){
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:"+EUGroupChat.userModelList.get(finalI).getPhone()));
                            context.startActivity(intent);
                        }
                    }
                });

                if (EUGroupChat.userModelList.get(i).getEmail()!=null){
                    tvEmail.setText(EUGroupChat.userModelList.get(i).getEmail());
                }
            }
        }
        final CompanyModel[] myCompany = {null};
        companyTimeModelRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try{
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                        ComapnyTimeScheduledModel companyTimeScheduledModel = snapshot.getValue(ComapnyTimeScheduledModel.class);
                        if (companyTimeScheduledModel.getUid()!=null){
                            if (companyTimeScheduledModel.getUid().equals(uid)){
                                String id = companyTimeScheduledModel.getCompanyId();
                                myCompanySchedule = companyTimeScheduledModel;
                                boolean barcelonaMatch = false;
                                for (int i = 0; i<EUGroupChat.barcelonaCompanyList.size(); i++){
                                    if (EUGroupChat.barcelonaCompanyList.get(i).getKey().equals(id)){
                                        barcelonaMatch = true;
                                        myCompany[0] = EUGroupChat.barcelonaCompanyList.get(i);
                                    }
                                }
                                if (!barcelonaMatch){
                                    for (int i = 0; i<EUGroupChat.cataniaCompanyList.size(); i++){
                                        if (EUGroupChat.cataniaCompanyList.get(i).getKey().equals(id)){
                                            myCompany[0] = EUGroupChat.cataniaCompanyList.get(i);
                                        }
                                    }
                                }
                                if (myCompany[0] !=null){
                                    linearWork.setVisibility(View.VISIBLE);
                                    try {
                                        if (!myCompany[0].getFullLegalName().isEmpty()){
                                            tvWorksAt.setText(myCompany[0].getFullLegalName());
                                        }else if (!myCompany[0].getLegalRepresentative().isEmpty()){
                                            tvWorksAt.setText(myCompany[0].getLegalRepresentative());
                                        }
                                    }catch (Exception e){
                                        tvWorksAt.setText("Company name not found !");
                                    }
                                    if (myCompanySchedule!=null){
                                        btnDetails.setVisibility(View.VISIBLE);
                                    }else {
                                        btnDetails.setVisibility(View.GONE);
                                    }
                                }else {
                                    linearWork.setVisibility(View.GONE);
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

        btnDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (myCompany[0]!=null&&myCompanySchedule!=null){
                    android.app.AlertDialog.Builder builder1 = new android.app.AlertDialog.Builder(context);
                    View view1 = LayoutInflater.from(context).inflate(R.layout.company_schedule_dialog, null);
                    TextView tvCompanyName = view1.findViewById(R.id.tvCompanyName);
                    TextView tvDays = view1.findViewById(R.id.tvDays);
                    TextView tvMorningStartTime = view1.findViewById(R.id.tvMorningStartTime);
                    TextView tvMorningToTime = view1.findViewById(R.id.tvMorningToTime);
                    TextView tvNoonStart = view1.findViewById(R.id.tvNoonStart);
                    TextView tvNoonTo = view1.findViewById(R.id.tvNoonTo);
                    TextView tvDescription = view1.findViewById(R.id.tvDescription);
                    ImageButton btnCancel = view1.findViewById(R.id.btnCancel);
                    try{
                        if (!myCompany[0].getFullLegalName().isEmpty()){
                            tvCompanyName.setText(myCompany[0].getFullLegalName());
                        }else if (!myCompany[0].getLegalRepresentative().isEmpty()){
                            tvCompanyName.setText(myCompany[0].getLegalRepresentative());
                        }
                        if (myCompanySchedule.getSelectedDays()!=null){
                            String idList = myCompanySchedule.getSelectedDays().toString();
                            String csv = idList.substring(1, idList.length() - 1).replace(", ", ",");
                            tvDays.setText(csv);
                        }
                        if (myCompanySchedule.getMorningFrom()!=null){
                            tvMorningStartTime.setText(myCompanySchedule.getMorningFrom());
                        }
                        if (myCompanySchedule.getMorningTo()!=null){
                            tvMorningToTime.setText(myCompanySchedule.getMorningTo());
                        }
                        if (myCompanySchedule.getNoonFrom()!=null){
                            tvNoonStart.setText(myCompanySchedule.getNoonFrom());
                        }
                        if (myCompanySchedule.getNoonTo()!=null){
                            tvNoonTo.setText(myCompanySchedule.getNoonTo());
                        }
                        if (myCompanySchedule.getDescription()!=null){
                            tvDescription.setText(myCompanySchedule.getDescription());
                        }
                    }catch (Exception e){}
                    builder1.setView(view1);
                    android.app.AlertDialog alertDialog = builder1.create();
                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });
                    alertDialog.show();
                }
            }
        });
        builder.setView(view);
        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void startRecording() {
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "AroundEU_media_" + timeStamp + "_";
            File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(
                    imageFileName,
                    ".3gp",
                    storageDir
            );

            fileName = image.getAbsolutePath();

            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e("Recording_Tag", "prepare() failed");
            }

            recorder.start();
        }catch (Exception e){
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        try {
            if (recorder!=null){
                recorder.stop();
                recorder.release();
                recorder = null;

                uploadAudio();

            }
        }catch (Exception e){
            Toast.makeText(this, "Tap and hold to record !", Toast.LENGTH_SHORT).show();
        }

    }

    private void uploadAudio(){

        if (fileName!=null){

            ProgressDialog progressDialog = new ProgressDialog(ChatActivity.this);
            progressDialog.setMessage("Please wait....");
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();

            File file = new File(fileName);
            Uri uri = Uri.fromFile(file);
            storageReference.child(file.getName()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Map<String, Object> map = new HashMap<>();
                            map.put("timeStamp", ServerValue.TIMESTAMP);
                            map.put("uid", mAuth.getCurrentUser().getUid());
                            map.put("audio", url);
                            chatRef.push().setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()){
                                        etTypeHere.setText("");
                                        if (thisgroupsModel.getApprovedMembers()!=null){
                                            List<String> list = new ArrayList<>();
                                            for (int i = 0; i<thisgroupsModel.getApprovedMembers().size(); i++){
                                                if (!thisgroupsModel.getApprovedMembers().get(i).equals(mAuth.getCurrentUser().getUid())){
                                                    for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
                                                        if (EUGroupChat.userModelList.get(a).getUid().equals(thisgroupsModel.getApprovedMembers().get(i))){
                                                            if (EUGroupChat.userModelList.get(a).getDeviceTokens()!=null){
                                                                list.addAll(EUGroupChat.userModelList.get(a).getDeviceTokens());
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                            sendNotification(list);
                                            if (mentionedUser!=null){
                                                sendMentionNotification();
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    });
                }
            });

        }

    }

}