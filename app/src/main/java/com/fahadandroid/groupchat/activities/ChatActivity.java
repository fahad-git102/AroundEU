package com.fahadandroid.groupchat.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Database;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.media.MediaMetadataRetriever;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chootdev.recycleclick.RecycleClick;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.adapters.ChatRecyclerAdapter;
import com.fahadandroid.groupchat.adapters.MentionUserAdapter;
import com.fahadandroid.groupchat.adapters.StringHorizontalAdapter;
import com.fahadandroid.groupchat.adapters.StringPdfsAdapter;
import com.fahadandroid.groupchat.adapters.UsersAdapter;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.EmojiUtils;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.helpers.MyScrollToBottomObserver;
import com.fahadandroid.groupchat.models.ComapnyTimeScheduledModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.fahadandroid.groupchat.models.MessagesModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rygelouv.audiosensei.player.AudioSenseiListObserver;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener{

    public final static int REQUEST_CAMERA_WRITE_EXTERNAL_STORAGE = 1919;
    public static final int PICK_IMAGE = 1001;
    public static final int TAKE_PHOTO = 1000;
    private List<UserModel> membersList;
    ImageButton goBack, btnRecord;
    GroupsModel thisGroupsModel;
    ItemTouchHelper itemTouchHelper;
    String key;
    UserModel mentionedUser;
    HashMap<String, Long> messagesCountMap;
    MediaRecorder recorder;
    String fileName;
    ProgressBar progressBar;
    public static final int GALLERY = 31, CAMERA = 32;
    DatabaseReference usersRef;
    String replyId;
    RecyclerView recyclerUsersToMention;
    TextView tvGroupName;
    FirebaseFunctions mFunctions;
    List<String> messagesKeys;
    DatabaseReference companyTimeModelRef;
    ComapnyTimeScheduledModel myCompanySchedule ;
    List<MessagesModel> messagesModelList;
    StorageReference storageReference;
    FirebaseAuth mAuth;
    String picturepath;
    ChatRecyclerAdapter adapter;
    EditText etTypeHere;
    RecyclerView recycler_chat;
    ImageButton btnSend, btnAdd, btnInfo;
    LinearLayoutManager linearLayoutManager;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference groupsRef, chatRef;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    CardView cardReply;
    TextView tvReplyUsername, tvReplyMessageType;
    ImageView replyImage, btnCloseReply;
     private boolean permissionToRecordAccepted = false;
    private String [] permissions = {Manifest.permission.RECORD_AUDIO};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        AudioSenseiListObserver.getInstance().registerLifecycle(getLifecycle());
        messagesModelList = new ArrayList<>();
        goBack = findViewById(R.id.goBack);
        btnInfo = findViewById(R.id.btnInfo);
        messagesCountMap = new HashMap<>();
        membersList = new ArrayList<>();
        btnInfo.setOnClickListener(this);
        recycler_chat = findViewById(R.id.recycler_chat);
        etTypeHere = findViewById(R.id.etTypeHere);
        progressBar = findViewById(R.id.progressBar);
        btnCloseReply = findViewById(R.id.btnCloseReply);
        btnCloseReply.setOnClickListener(this);
        cardReply = findViewById(R.id.card_reply);
        tvReplyUsername = findViewById(R.id.tvReplyUserName);
        tvReplyMessageType = findViewById(R.id.tvReplyMessageType);
        replyImage = findViewById(R.id.replyImage);
        ActivityCompat.requestPermissions(this, permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        storageReference = FirebaseStorage.getInstance().getReference().child("media");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");
        companyTimeModelRef = FirebaseDatabase.getInstance().getReference("companyTimeScheduled");
        thisGroupsModel = getIntent().getParcelableExtra("groupModel");
        getMembers();
        linearLayoutManager = new LinearLayoutManager(this);
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

        setupRecyclerView();

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

        if (key == null) {
            progressBar.setVisibility(View.VISIBLE);
            if (thisGroupsModel !=null){
                boolean isFound = false;
                if (thisGroupsModel.getApprovedMembers()!=null){
                    if (thisGroupsModel.getApprovedMembers().contains(mAuth.getCurrentUser().getUid())){

                        if (thisGroupsModel.isDeleted()){
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
                            key = thisGroupsModel.getKey();
                            chatRef = firebaseDatabase.getReference("groups").child(key).child("messages");
                            getChat();
                            getMessages();
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
                }
            }
        }else {
            getMessagesCount();
            readAllMessages();
        }
    }

    private void getAllMembers(String str){
        List<UserModel> approvedMembers = new ArrayList<>();
        List<UserModel> refinedUsers = new ArrayList<>();

        for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
            for (int i = 0; i< thisGroupsModel.getApprovedMembers().size(); i++){
                if (thisGroupsModel.getApprovedMembers().get(i).equals(EUGroupChat.userModelList.get(a).getUid())){
                    approvedMembers.add(EUGroupChat.userModelList.get(a));
                }
            }
        }

        for (int i = 0; i<approvedMembers.size(); i++){
            if (approvedMembers.get(i).getFirstName().toLowerCase().contains(str.toLowerCase())
                    ||approvedMembers.get(i).getSurName().toLowerCase().contains(str.toLowerCase())){
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
                    String text = "<b>@" + refinedUsers.get(i).getFirstName()+"</b>";
                    etTypeHere.setText(Html.fromHtml(text));
                    mentionedUser = refinedUsers.get(i);
                    refinedUsers.clear();
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
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
        }
        if (!permissionToRecordAccepted ) finish();

    }

    private void getMessages(){
        DatabaseReference messagesRef = groupsRef.child(key).child("messages");
        FirebaseRecyclerOptions<MessagesModel> options = new FirebaseRecyclerOptions.Builder<MessagesModel>()
                .setQuery(messagesRef, MessagesModel.class)
                .build();
        adapter = new ChatRecyclerAdapter(options, ChatActivity.this, key);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setStackFromEnd(true);
        recycler_chat.setLayoutManager(manager);
        recycler_chat.setAdapter(adapter);
        adapter.registerAdapterDataObserver(new MyScrollToBottomObserver(recycler_chat, adapter, manager));

        RecycleClick.addTo(recycler_chat).setOnItemLongClickListener(new RecycleClick.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClicked(RecyclerView recyclerView, int i, View view) {
                deleteMessage(adapter.getItem(i));
                return false;
            }
        });
    }

    private void deleteMessage(MessagesModel messagesModel){
        if (messagesModel.getUid().equals(mAuth.getCurrentUser().getUid())){
            AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
            builder.setTitle("Delete Message ?");
            builder.setMessage("Are you sure you want to delete this message ?");
            builder.setIcon(getResources().getDrawable(R.drawable.delete));
            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int iq) {
                    if (messagesModel.getKey()==null){
                        Toast.makeText(ChatActivity.this, "You cannot delete this message",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    try {
                        groupsRef.child(key).child("messages").child(messagesModel.getKey()).
                                removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        Toast.makeText(ChatActivity.this, "Deleted !", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                });
                    }catch (Exception e){
                        Toast.makeText(ChatActivity.this, "You cannot delete this message",
                                Toast.LENGTH_SHORT).show();
                    }
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
    }

    @Override
    protected void onPause() {
        adapter.stopListening();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (key!=null){
            chatRef = firebaseDatabase.getReference("groups").child(key).child("messages");
            getChat();
            getMessages();
        }
        adapter.startListening();
    }

    private void getChat(){
        if (thisGroupsModel ==null){
            Query query = groupsRef.orderByChild("key").equalTo(key);
            query.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                    try{
                        GroupsModel groupsModel = snapshot.getValue(GroupsModel.class);
                        if(groupsModel.getKey()==null){
                            groupsModel.setKey(snapshot.getKey());
                        }
                        thisGroupsModel = groupsModel;
                        tvGroupName.setText(groupsModel.getName());
                        getMembers();
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
                        thisGroupsModel = groupsModel;
                        tvGroupName.setText(groupsModel.getName());
                        getMembers();
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
        }else {
            tvGroupName.setText(thisGroupsModel.getName());
        }

    }

    private void getMembers(){
        membersList = new ArrayList<>();
        if (thisGroupsModel !=null){
            if (thisGroupsModel.getApprovedMembers()!=null){
                for (UserModel item : EUGroupChat.userModelList){
                    if (thisGroupsModel.getApprovedMembers().contains(item.getUid())){
                        membersList.add(item);
                    }
                }
            }
        }
    }

    @Override
    public void onClick(View view) {

        if (view.getId()==R.id.btnCloseReply){

            if (replyId!=null){
                cardReply.setVisibility(View.GONE);
                replyId = null;
            }else {
                cardReply.setVisibility(View.GONE);
            }

        }else if (view.getId()==R.id.btnInfo){

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View view1 = LayoutInflater.from(this).inflate(R.layout.group_info_dialog, null);
            TextView tvName = view1.findViewById(R.id.etName);
            RecyclerView recyclerPdfs = view1.findViewById(R.id.recycler_items);
            recyclerPdfs.setLayoutManager(new LinearLayoutManager(ChatActivity.this, RecyclerView.HORIZONTAL, false));
            LinearLayout btnGroupMembers = view1.findViewById(R.id.btnGroupMembers);
            TextView tvAccommodation = view1.findViewById(R.id.tvAccomodation);
            TextView tvMembersCount = view1.findViewById(R.id.tvMembersCount);
            if (membersList!=null)
                tvMembersCount.setText(membersList.size()+"");

            btnGroupMembers.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (thisGroupsModel !=null){
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
                        View v = LayoutInflater.from(ChatActivity.this).inflate(R.layout.users_list_dialog, null);
                        RecyclerView recyclerUsers = v.findViewById(R.id.recycler_users);
                        TextView tvMembersCount = v.findViewById(R.id.tv_members_count);
                        TextView tvNoData = v.findViewById(R.id.tvNoData);
                        recyclerUsers.setLayoutManager(new LinearLayoutManager(ChatActivity.this));
                        if (membersList.size()>0){
                            tvNoData.setVisibility(View.GONE);
                            tvMembersCount.setVisibility(View.VISIBLE);
                            tvMembersCount.setText(membersList.size()+"");
                            UsersAdapter adapter = new UsersAdapter(membersList, ChatActivity.this);
                            recyclerUsers.setAdapter(adapter);
                            RecycleClick.addTo(recyclerUsers).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                                @Override
                                public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                                    showUsersData(membersList.get(i).getUid());
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
                                tvAccommodation.setText(groupsModel.getCategory());
                            }
                            if (groupsModel.getFileUrls()!=null){

                                StringPdfsAdapter adapter = new StringPdfsAdapter(groupsModel.getFileUrls(), ChatActivity.this);
                                recyclerPdfs.setAdapter(adapter);
                                RecycleClick.addTo(recyclerPdfs).setOnItemClickListener(new RecycleClick.OnItemClickListener() {
                                    @Override
                                    public void onItemClicked(RecyclerView recyclerView, int i, View view) {
                                        Intent intent = new Intent(ChatActivity.this, LoadPdfActivity.class);
                                        intent.putExtra("url", groupsModel.getFileUrls().get(i));
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
            if (message.isEmpty()) return;
            String mes = "";

            if (!EmojiUtils.containsEmoji(message)){
                Pattern pattern = Pattern.compile("@\\w+");

                Matcher matcher = pattern.matcher(message);

                while (matcher.find())
                {
                    mes = matcher.group();
                }

                message = message.replace(mes, "<b>"+mes+"</b>");
            }

            if (!message.isEmpty()){
                String mkey = chatRef.push().getKey();
                final Map<String, Object> map = new HashMap<>();
                map.put("timeStamp", ServerValue.TIMESTAMP);
                map.put("uid", mAuth.getCurrentUser().getUid());
                map.put("message", message);
                map.put("key", mkey);
                if (replyId!=null){
                    map.put("replyId", replyId);
                }
                etTypeHere.setText("");
                chatRef.child(mkey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        cardReply.setVisibility(View.GONE);
                        replyId = null;
                        if (task.isSuccessful()){
                            manageNotifications();
                            manageMessagesCounts();
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
            View view1 = inflater.inflate(R.layout.image_video_location_dialog, null);
            Button video = view1.findViewById(R.id.video);
            Button image = view1.findViewById(R.id.image);
            Button documents = view1.findViewById(R.id.documents);
            Button location = view1.findViewById(R.id.location);
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
            location.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(ChatActivity.this);
                    builder1.setTitle("Current Location");
                    builder1.setMessage("Do you want to share your current location ?");
                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            sendLocation();
                            dialogInterface.dismiss();
                        }
                    });
                    builder1.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog alertDialog1 = builder1.create();
                    alertDialog1.show();

                    alertDialog.dismiss();
                }
            });
            alertDialog.show();
        }
    }

    private void readAllMessages(){
        DatabaseReference countsRef= FirebaseDatabase.getInstance().getReference("groups").
                child(key).child("unReadCounts");
        HashMap<String, Object> map = new HashMap<>();
        map.put(mAuth.getCurrentUser().getUid(), 0L);
        countsRef.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                try {
                    if (key != null){
                        FirebaseDatabase.getInstance().getReference("unReadMessages")
                                .child(mAuth.getCurrentUser().getUid()).child(key).setValue(false);
                    }else if (thisGroupsModel!=null&&thisGroupsModel.getKey()!=null){
                        FirebaseDatabase.getInstance().getReference("unReadMessages")
                                .child(mAuth.getCurrentUser().getUid()).child(thisGroupsModel.getKey()).setValue(false);
                    }
                }catch (Exception e){}
            }
        });
    }

    private void getMessagesCount(){
        DatabaseReference countsRef= FirebaseDatabase.getInstance().getReference("groups").
                child(key).child("unReadCounts");
        countsRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String key = snapshot.getKey();
                    long value = snapshot.getValue(Long.class);
                    messagesCountMap.put(key, value);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                try {
                    String key = snapshot.getKey();
                    long value = snapshot.getValue(Long.class);
                    messagesCountMap.put(key, value);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                try {
                    String key = snapshot.getKey();
                    messagesCountMap.put(key, 0L);
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

    private void manageMessagesCounts(){
        List<String> idsList = new ArrayList<>();
        List<UserModel> membersList = new ArrayList<>();
        if (thisGroupsModel !=null){
            if (thisGroupsModel.getApprovedMembers()!=null){
                for(int i = 0; i< thisGroupsModel.getApprovedMembers().size(); i++){
                    for (int a = 0; a<EUGroupChat.userModelList.size(); a++){
                        if (thisGroupsModel.getApprovedMembers().get(i).
                                equals(EUGroupChat.userModelList.get(a).getUid())){
                            membersList.add(EUGroupChat.userModelList.get(a));
                        }
                    }
                }
                if (membersList.size()>0){
                    for (int a = 0; a<membersList.size(); a++){
                        if (!membersList.get(a).getUid().equals(EUGroupChat.currentUser.getUid())){
                            if (membersList.get(a).getUserType()!=null){
                                if (!membersList.get(a).getUserType().toLowerCase().equals("student")){
                                    idsList.add(membersList.get(a).getUid());
                                }
                            }
                        }

                    }

                    Set<String> keySet = messagesCountMap.keySet();
                    ArrayList<String> listOfKeys
                            = new ArrayList<String>(keySet);

                    if (messagesCountMap.size()>0){
                        Iterator myVeryOwnIterator = messagesCountMap.keySet().iterator();
                        while(myVeryOwnIterator.hasNext()) {
                            String key=(String)myVeryOwnIterator.next();
                            long value=messagesCountMap.get(key);
                            messagesCountMap.put(key, value+1);
                            if (!listOfKeys.contains(key)){
                                listOfKeys.add(key);
                            }
                        }
                    }
                    for (int i = 0; i < idsList.size(); i++){
                        if (!listOfKeys.contains(idsList.get(i))){
                            messagesCountMap.put(idsList.get(i), 1L);
                            listOfKeys.add(idsList.get(i));
                        }
                    }
                    if (EUGroupChat.admin!=null){
                        if (!listOfKeys.contains(EUGroupChat.admin.getUid())){
                            messagesCountMap.put(EUGroupChat.admin.getUid(), 1L);
                            listOfKeys.add(EUGroupChat.admin.getUid());
                        }
                    }

                    if (listOfKeys.contains(mAuth.getCurrentUser().getUid())){
                        messagesCountMap.remove(mAuth.getCurrentUser().getUid());
                        listOfKeys.remove(mAuth.getCurrentUser().getUid());
                    }

                    for (String item : listOfKeys){
                        FirebaseDatabase.getInstance().getReference("unReadMessages")
                                .child(item).child(thisGroupsModel.getKey()).setValue(true);
                    }

                    DatabaseReference countsRef= FirebaseDatabase.getInstance().getReference("groups").
                            child(key).child("unReadCounts");
                    countsRef.setValue(messagesCountMap);
                }
            }
        }
    }

    private void manageNotifications() {
        try {
            List<String> list = new ArrayList<>();

            for (int i = 0; i<EUGroupChat.userModelList.size(); i++){
                if (EUGroupChat.userModelList.get(i).getUid()!=null){
                    if (!EUGroupChat.userModelList.get(i).getUid().equals(mAuth.getCurrentUser().getUid())){
                        if(EUGroupChat.userModelList.get(i).isAdmin()){
                            if (EUGroupChat.userModelList.get(i).getDeviceTokens()!=null)
                                list.addAll(EUGroupChat.userModelList.get(i).getDeviceTokens());
                        }else if (EUGroupChat.userModelList.get(i).getUserType()!=null){
                            if (EUGroupChat.userModelList.get(i).getUserType().equals("Cordinator")){
                                if (EUGroupChat.userModelList.get(i).getDeviceTokens()!=null)
                                    list.addAll(EUGroupChat.userModelList.get(i).getDeviceTokens());
                            }
                        }
                    }
                }
            }

            if (membersList!=null){
                for (UserModel item : membersList){
                    if (item.getUid()!=null){
                        if (!item.getUid().equals(mAuth.getCurrentUser().getUid())){
                            if (!item.isAdmin()){
                                if (item.getUserType()!=null){
                                    if (item.getDeviceTokens()!=null){
                                        list.addAll(item.getDeviceTokens());
                                    }
                                }else {
                                    if (!item.getUserType().equals("Cordinator")){
                                        list.addAll(item.getDeviceTokens());
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (list!=null){
                sendNotification(list, key);
            }
            if (mentionedUser!=null){
                sendMentionNotification();
            }
        }catch (Exception e){
            e.printStackTrace();
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
            try {
                Uri uri = data.getData();
                sendMessageWithDoc(uri);
            }catch (Exception e){}
        }
    }

    private File createImageFile() throws IOException {
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
                        String mkey = chatRef.push().getKey();
                        map.put("timeStamp", ServerValue.TIMESTAMP);
                        map.put("uid", mAuth.getCurrentUser().getUid());
                        map.put("image", url);
                        map.put("key", mkey);
                        if (replyId!=null){
                            map.put("replyId", replyId);
                        }
                        chatRef.child(mkey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                cardReply.setVisibility(View.GONE);
                                replyId = null;
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    etTypeHere.setText("");
                                    manageNotifications();
                                    manageMessagesCounts();
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
                        String mkey = chatRef.push().getKey();
                        map.put("timeStamp", ServerValue.TIMESTAMP);
                        map.put("uid", mAuth.getCurrentUser().getUid());
                        map.put("document", url);
                        map.put("key", mkey);
                        if (replyId!=null){
                            map.put("replyId", replyId);
                        }
                        chatRef.child(mkey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                cardReply.setVisibility(View.GONE);
                                replyId = null;
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    etTypeHere.setText("");
                                    manageNotifications();
                                    manageMessagesCounts();
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
                        String mKey = chatRef.push().getKey();
                        map.put("timeStamp", ServerValue.TIMESTAMP);
                        map.put("uid", mAuth.getCurrentUser().getUid());
                        map.put("video", url);
                        map.put("key", mKey);
                        if (replyId!=null){
                            map.put("replyId", replyId);
                        }
                        chatRef.child(mKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                cardReply.setVisibility(View.GONE);
                                replyId = null;
                                progressDialog.dismiss();
                                if (task.isSuccessful()){
                                    etTypeHere.setText("");
                                    manageNotifications();
                                    manageMessagesCounts();
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
    private void sendNotification(List<String> stringList, String chatId){

        for (int i = 0; i<EUGroupChat.userModelList.size(); i++){
            if (!EUGroupChat.userModelList.get(i).getUid().equals(mAuth.getCurrentUser().getUid())){
                if (EUGroupChat.userModelList.get(i).isAdmin()){
                    if (EUGroupChat.userModelList.get(i).getDeviceTokens()!=null){
                        stringList.addAll(EUGroupChat.userModelList.get(i).getDeviceTokens());
                    }
                }
            }
        }
        HashSet<String> hashSet = new HashSet<>();
        hashSet.addAll(stringList);
        stringList.clear();
        stringList.addAll(hashSet);

        Map<String, Object> map = new HashMap<>();

        String title = thisGroupsModel.getName();
        String message = EUGroupChat.currentUser.getFirstName()+" sent a message in your group";
        map.put("title", title);
        map.put("message", message);

        Map<String, Object> smallMap = new HashMap<>();
        smallMap.put("title", title);
        smallMap.put("message", message);
        smallMap.put("dataUid", chatId);

        map.put("data", smallMap);
        map.put("tokens", stringList);

        cloudMessageNotification(map);
    }

    private Task<String> cloudMessageNotification(Map<String, Object> data){
        return mFunctions
                .getHttpsCallable("sendNotificationToList")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void sendMentionNotification(){
        Map<String, Object> map = new HashMap<>();
        String title = thisGroupsModel.getName();
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
        TextView tvMorningStart = view.findViewById(R.id.tvMorningStartTime);
        TextView tvMorningTo = view.findViewById(R.id.tvMorningToTime);
        TextView tvNoonStart = view.findViewById(R.id.tvNoonStart);
        TextView tvNoonTo = view.findViewById(R.id.tvNoonTo);
        RecyclerView recyclerWorkingDays = view.findViewById(R.id.recycler_working_days);
        recyclerWorkingDays.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));

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
                                                    context);
                                            recyclerWorkingDays.setAdapter(adapter);
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
                    ".m4a",
                    storageDir
            );

            fileName = image.getAbsolutePath();

            recorder = new MediaRecorder();


            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.setAudioSamplingRate(16000);
            recorder.setAudioChannels(1);
            recorder.setOutputFile(fileName);

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

    public String getAudioFileLength(Uri uri, boolean stringFormat) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            MediaMetadataRetriever mmr = new MediaMetadataRetriever();
            mmr.setDataSource(ChatActivity.this, uri);
            String duration = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            int millSecond = Integer.parseInt(duration);
            if (millSecond < 0) return String.valueOf(0); // if some error then we say duration is zero
            if (!stringFormat) return String.valueOf(millSecond);
            int hours, minutes, seconds = millSecond / 1000;
            hours = (seconds / 3600);
            minutes = (seconds / 60) % 60;
            seconds = seconds % 60;
            if (hours > 0 && hours < 10) stringBuilder.append("0").append(hours).append(":");
            else if (hours > 0) stringBuilder.append(hours).append(":");
            if (minutes < 10) stringBuilder.append("0").append(minutes).append(":");
            else stringBuilder.append(minutes).append(":");
            if (seconds < 10) stringBuilder.append("0").append(seconds);
            else stringBuilder.append(seconds);
        }catch (Exception e){
            e.printStackTrace();
        }
        return stringBuilder.toString();
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
            String audioLength = getAudioFileLength(uri, false);
            long seconds = TimeUnit.MILLISECONDS.toSeconds(Long.parseLong(audioLength));
            storageReference.child(file.getName()).putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    storageReference.child(file.getName()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String url = uri.toString();
                            Map<String, Object> map = new HashMap<>();
                            String mKey = chatRef.push().getKey();
                            map.put("timeStamp", ServerValue.TIMESTAMP);
                            map.put("uid", mAuth.getCurrentUser().getUid());
                            map.put("audio", url);
                            map.put("audioTime", seconds);
                            map.put("key", mKey);
                            if (replyId!=null){
                                map.put("replyId", replyId);
                            }
                            chatRef.child(mKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    cardReply.setVisibility(View.GONE);
                                    replyId = null;
                                    progressDialog.dismiss();
                                    if (task.isSuccessful()) {
                                        etTypeHere.setText("");
                                        manageNotifications();
                                        manageMessagesCounts();
                                    }
                                }
                            });
                        }
                    });
                }
            });

        }

    }

    private void sendLocation(){
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        @SuppressLint("MissingPermission") android.location.Location lastKnownLocation = locationManager.getLastKnownLocation(locationProvider);

        if (lastKnownLocation!=null){
            double userLat = lastKnownLocation.getLatitude();
            double userLong = lastKnownLocation.getLongitude();

            final Map<String, Object> map = new HashMap<>();
            String mKey = chatRef.push().getKey();
            map.put("timeStamp", ServerValue.TIMESTAMP);
            map.put("uid", mAuth.getCurrentUser().getUid());
            map.put("longitude", userLong);
            map.put("latitude", userLat);
            map.put("key", mKey);
            if (replyId!=null){
                map.put("replyId", replyId);
            }

            chatRef.child(mKey).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()){
                        cardReply.setVisibility(View.GONE);
                        replyId = null;
                        etTypeHere.setText("");
                        manageNotifications();
                        manageMessagesCounts();
                    }
                }
            });
        }

    }


    private void setupRecyclerView(){

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN,  ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                //awesome code when user grabs recycler card to reorder
                return false;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                super.clearView(recyclerView, viewHolder);
                //awesome code to run when user drops card and completes reorder
            }


            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                if (direction == ItemTouchHelper.RIGHT) {
                    MessagesModel messagesModel = adapter.getItem(viewHolder.getAdapterPosition());
                    replyId = messagesModel.getKey();
                    cardReply.setVisibility(View.VISIBLE);
                    for (int i = 0; i< EUGroupChat.userModelList.size(); i++){
                        if (EUGroupChat.userModelList.get(i).getUid().equals(messagesModel.getUid())){
                            tvReplyUsername.setText(EUGroupChat.userModelList.get(i).getFirstName());
                        }
                    }
                    if (messagesModel.getMessage()!=null&&!messagesModel.getMessage().isEmpty()){
                        tvReplyMessageType.setText(Html.fromHtml(messagesModel.getMessage()));
                        replyImage.setImageBitmap(null);
                    }else if (messagesModel.getAudio()!=null){
                        tvReplyMessageType.setText("Audio");
                        Glide.with(ChatActivity.this).load(getResources().getDrawable(R.drawable.mic)).override(100,100).into(replyImage);
                    }else if (messagesModel.getLatitude()>0&&messagesModel.getLongitude()>0){
                        tvReplyMessageType.setText("Location");
                        Glide.with(ChatActivity.this).load(getResources().getDrawable(R.drawable.google_maps)).into(replyImage);
                    }else if (messagesModel.getImage()!=null){
                        tvReplyMessageType.setText("Image");
                        Glide.with(ChatActivity.this).load(messagesModel.getImage()).fitCenter().placeholder(R.drawable.default_image).into(replyImage);
                    }else if (messagesModel.getDocument()!=null){
                        tvReplyMessageType.setText("Document");
                        Glide.with(ChatActivity.this).load(getResources().getDrawable(R.drawable.doc)).into(replyImage);
                    }else if (messagesModel.getVideo()!=null){
                        tvReplyMessageType.setText("Video");
                        Glide.with(ChatActivity.this).load(getResources().getDrawable(android.R.drawable.presence_video_online)).into(replyImage);
                    }
                }
                adapter.notifyItemChanged(viewHolder.getAdapterPosition());
            }
        };
        itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recycler_chat);
    }

}