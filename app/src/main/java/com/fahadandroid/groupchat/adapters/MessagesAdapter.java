package com.fahadandroid.groupchat.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.activities.LoadPdfActivity;
import com.fahadandroid.groupchat.activities.OpenAttachmentActivity;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.helpers.EUGroupChat;
import com.fahadandroid.groupchat.helpers.HelperClass;
import com.fahadandroid.groupchat.helpers.HyperlinkTextView;
import com.fahadandroid.groupchat.models.ComapnyTimeScheduledModel;
import com.fahadandroid.groupchat.models.CompanyModel;
import com.fahadandroid.groupchat.models.MessagesModel;
import com.fahadandroid.groupchat.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rygelouv.audiosensei.player.AudioSenseiPlayerView;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MHolder> {

    List<MessagesModel> messageModelList;
    Context context;
    List<MediaPlayer> mpList;
    DatabaseReference companyTimeModelRef;
    ComapnyTimeScheduledModel myCompanySchedule ;
    FirebaseAuth mAuth;

    public MessagesAdapter(List<MessagesModel> messageModelList, Context context) {
        this.messageModelList = messageModelList;
        this.context = context;
        mpList = new ArrayList<>();
        companyTimeModelRef = FirebaseDatabase.getInstance().getReference("companyTimeScheduled");
    }

    @NonNull
    @Override
    public MHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_item, parent, false);
        return new MHolder(view);
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
    @Override
    public void onBindViewHolder(@NonNull MHolder holder, int pos) {
        mAuth = FirebaseAuth.getInstance();
        if (messageModelList.get(holder.getAdapterPosition()).getUid().equals(mAuth.getCurrentUser().getUid())){

            holder.tvMyMessage.setMovementMethod(LinkMovementMethod.getInstance());
            Linkify.addLinks(holder.tvMyMessage, Linkify.WEB_URLS);

            holder.linearUser.setVisibility(View.GONE);
            holder.linearMe.setVisibility(View.VISIBLE);
            holder.profilePic.setVisibility(View.GONE);
            holder.tvMyTime.setText(HelperClass.getFormattedDateTime(messageModelList.get(holder.getAdapterPosition()).getTimeStamp(), "MMM dd, yyyy hh:mm a"));

            for (int i = 0; i< EUGroupChat.userModelList.size(); i++){
                if (EUGroupChat.userModelList.get(i).getUid().equals(messageModelList.get(holder.getAdapterPosition()).getUid())){
                    holder.tvMyName.setText(EUGroupChat.userModelList.get(i).getFirstName() +" "+EUGroupChat.userModelList.get(i).getSurName());
                }
            }

            if (messageModelList.get(holder.getAdapterPosition()).getReplyId()!=null){
                holder.cardMyReply.setVisibility(View.VISIBLE);
                for (int a = 0 ; a<messageModelList.size(); a++){
                    if (messageModelList.get(a).getKey().equals(messageModelList.get(holder.getAdapterPosition()).getReplyId())){
                        for (int i = 0; i< EUGroupChat.userModelList.size(); i++){
                            if (EUGroupChat.userModelList.get(i).getUid().equals(messageModelList.get(a).getUid())){
                                holder.tvMyReplyUserName.setText(EUGroupChat.userModelList.get(i).getFirstName());
                            }
                        }
                        if (messageModelList.get(a).getMessage()!=null&&!messageModelList.get(a).getMessage().isEmpty()){
                            holder.tvMyReplyMessageType.setText(Html.fromHtml(messageModelList.get(a).getMessage()));
                        }else if (messageModelList.get(a).getAudio()!=null){
                            holder.tvMyReplyMessageType.setText("Audio");
                            Glide.with(context).load(context.getResources().getDrawable(R.drawable.mic)).override(100,100).into(holder.myReplyImage);
                        }else if (messageModelList.get(a).getLatitude()>0&&messageModelList.get(a).getLongitude()>0){
                            holder.tvMyReplyMessageType.setText("Location");
                            Glide.with(context).load(context.getResources().getDrawable(R.drawable.google_maps)).into(holder.myReplyImage);
                        }else if (messageModelList.get(a).getImage()!=null){
                            holder.tvMyReplyMessageType.setText("Image");
                            Glide.with(context).load(messageModelList.get(a).getImage()).fitCenter().placeholder(R.drawable.default_image).into(holder.myReplyImage);
                        }else if (messageModelList.get(a).getDocument()!=null){
                            holder.tvMyReplyMessageType.setText("Document");
                            Glide.with(context).load(context.getResources().getDrawable(R.drawable.doc)).into(holder.myReplyImage);
                        }else if (messageModelList.get(a).getVideo()!=null){
                            holder.tvMyReplyMessageType.setText("Video");
                            Glide.with(context).load(context.getResources().getDrawable(android.R.drawable.presence_video_online)).into(holder.myReplyImage);
                        }
                    }
                }
            }else {
                holder.cardMyReply.setVisibility(View.GONE);
            }

            if (messageModelList.get(holder.getAdapterPosition()).getMessage()!=null){
                holder.tvMyMessage.setVisibility(View.VISIBLE);
                holder.audioPlayerMy.setVisibility(View.GONE);
                holder.tvMyMessage.setText(Html.fromHtml(messageModelList.get(holder.getAdapterPosition()).getMessage()));
                holder.tvMyMessage.setMovementMethod(LinkMovementMethod.getInstance());
                Linkify.addLinks(holder.tvMyMessage, Linkify.WEB_URLS);
            }else if (messageModelList.get(holder.getAdapterPosition()).getImage()!=null){
                holder.myImageView.setVisibility(View.VISIBLE);
                holder.tvMyMessage.setVisibility(View.GONE);
                holder.audioPlayerMy.setVisibility(View.GONE);
                holder.btnPlayMe.setVisibility(View.GONE);
                Glide.with(context).load(messageModelList.get(holder.getAdapterPosition()).getImage()).override(150,150).placeholder(R.drawable.default_image).into(holder.myImageView);
                holder.myImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, OpenAttachmentActivity.class);
                        intent.putExtra("url", messageModelList.get(holder.getAdapterPosition()).getImage());
                        context.startActivity(intent);
                    }
                });
            }else if (messageModelList.get(holder.getAdapterPosition()).getVideo()!=null){
                holder.myImageView.setVisibility(View.VISIBLE);
                holder.tvMyMessage.setVisibility(View.GONE);
                holder.btnPlayMe.setVisibility(View.VISIBLE);
                holder.audioPlayerMy.setVisibility(View.GONE);
                Glide.with(context).load(messageModelList.get(holder.getAdapterPosition()).getVideo()).override(150,150).placeholder(R.drawable.default_image).into(holder.myImageView);
                holder.btnPlayMe.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, OpenAttachmentActivity.class);
                        intent.putExtra("isVideo", true);
                        intent.putExtra("url", messageModelList.get(holder.getAdapterPosition()).getVideo());
                        context.startActivity(intent);
                    }
                });
            }else if (messageModelList.get(holder.getAdapterPosition()).getDocument()!=null){
                holder.linear_documents_Me.setVisibility(View.VISIBLE);
                holder.btnPlayMe.setVisibility(View.GONE);
                holder.myImageView.setVisibility(View.GONE);
                holder.tvMyMessage.setVisibility(View.GONE);
                holder.audioPlayerMy.setVisibility(View.GONE);
                holder.linear_documents_Me.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, LoadPdfActivity.class);
                        intent.putExtra("url", messageModelList.get(holder.getAdapterPosition()).getDocument());
                        context.startActivity(intent);
                    }
                });
            }else if (messageModelList.get(holder.getAdapterPosition()).getAudio()!=null){
                holder.linear_documents_Me.setVisibility(View.GONE);
                holder.btnPlayMe.setVisibility(View.GONE);
                holder.myImageView.setVisibility(View.GONE);
                holder.tvMyMessage.setVisibility(View.GONE);
                holder.audioPlayerMy.setVisibility(View.VISIBLE);
                holder.audioPlayerMy.setAudioTarget(messageModelList.get(holder.getAdapterPosition()).getAudio());

            }else if (messageModelList.get(holder.getAdapterPosition()).getLatitude()>0&&messageModelList.get(holder.getAdapterPosition()).getLongitude()>0){
                holder.myImageView.setVisibility(View.VISIBLE);
                holder.tvMyMessage.setVisibility(View.GONE);
                holder.btnPlayMe.setVisibility(View.GONE);
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.google_maps)).placeholder(R.drawable.default_image).into(holder.myImageView);
                holder.myImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?q=loc:" + messageModelList.get(holder.getAdapterPosition()).getLatitude()
                                + "," + messageModelList.get(holder.getAdapterPosition()).getLongitude();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        context.startActivity(intent);
                    }
                });
            }else {
                holder.btnPlayMe.setVisibility(View.GONE);
                holder.myImageView.setVisibility(View.GONE);
                holder.tvMyMessage.setVisibility(View.VISIBLE);
                holder.audioPlayerMy.setVisibility(View.GONE);
            }

        }else {

            holder.linearMe.setVisibility(View.GONE);
            holder.linearUser.setVisibility(View.VISIBLE);
            holder.profilePic.setVisibility(View.VISIBLE);
            holder.tvUserTime.setText(HelperClass.getFormattedDateTime(messageModelList.get(holder.getAdapterPosition()).getTimeStamp(), "MMM dd, yyyy hh:mm a"));
            for (int i = 0; i< EUGroupChat.userModelList.size(); i++){
                if (EUGroupChat.userModelList.get(i).getUid().equals(messageModelList.get(holder.getAdapterPosition()).getUid())){
                    holder.tvUserName.setText(EUGroupChat.userModelList.get(i).getFirstName()+" "+EUGroupChat.userModelList.get(i).getSurName());
                    if (EUGroupChat.userModelList.get(i).isAdmin()){
                        Glide.with(context).load(R.drawable.project_consult).placeholder(R.drawable.default_user_img).override(150, 150).into(holder.profilePic);
                    }else {
                        if (EUGroupChat.userModelList.get(i).getProfileUrl()!=null){
                            Glide.with(context).load(EUGroupChat.userModelList.get(i).getProfileUrl()).placeholder(R.drawable.default_user_img).override(150, 150).into(holder.profilePic);
                        }
                    }
                }
            }

            if (messageModelList.get(holder.getAdapterPosition()).getReplyId()!=null){
                holder.cardOtherReply.setVisibility(View.VISIBLE);
                for (int a = 0 ; a<messageModelList.size(); a++){
                    if (messageModelList.get(a).getKey().equals(messageModelList.get(holder.getAdapterPosition()).getReplyId())){
                        for (int i = 0; i< EUGroupChat.userModelList.size(); i++){
                            if (EUGroupChat.userModelList.get(i).getUid().equals(messageModelList.get(a).getUid())){
                                holder.tvOtherReplyUserName.setText(EUGroupChat.userModelList.get(i).getFirstName());
                            }
                        }
                        if (messageModelList.get(a).getMessage()!=null&&!messageModelList.get(a).getMessage().isEmpty()){
                            holder.tvOtherReplyMessageType.setText(Html.fromHtml(messageModelList.get(a).getMessage()));
                        }else if (messageModelList.get(a).getAudio()!=null){
                            holder.tvMyReplyMessageType.setText("Audio");
                            Glide.with(context).load(context.getResources().getDrawable(R.drawable.mic)).override(100,100).into(holder.otherReplyImage);
                        }else if (messageModelList.get(a).getLatitude()>0&&messageModelList.get(a).getLongitude()>0){
                            holder.tvOtherReplyMessageType.setText("Location");
                            Glide.with(context).load(context.getResources().getDrawable(R.drawable.google_maps)).into(holder.otherReplyImage);
                        }else if (messageModelList.get(a).getImage()!=null){
                            holder.tvOtherReplyMessageType.setText("Image");
                            Glide.with(context).load(messageModelList.get(a).getImage()).fitCenter().placeholder(R.drawable.default_image).into(holder.otherReplyImage);
                        }else if (messageModelList.get(a).getDocument()!=null){
                            holder.tvOtherReplyMessageType.setText("Document");
                            Glide.with(context).load(context.getResources().getDrawable(R.drawable.doc)).into(holder.otherReplyImage);
                        }else if (messageModelList.get(a).getVideo()!=null){
                            holder.tvOtherReplyMessageType.setText("Video");
                            Glide.with(context).load(context.getResources().getDrawable(android.R.drawable.presence_video_online)).into(holder.otherReplyImage);
                        }
                    }
                }
            }else {
                holder.cardOtherReply.setVisibility(View.GONE);
            }
            if (messageModelList.get(holder.getAdapterPosition()).getMessage()!=null){
                holder.tvUserMessage.setVisibility(View.VISIBLE);
                holder.audioPlayerUser.setVisibility(View.GONE);
                holder.tvUserMessage.setText(Html.fromHtml(messageModelList.get(holder.getAdapterPosition()).getMessage()));
                holder.tvUserMessage.setMovementMethod(LinkMovementMethod.getInstance());
                Linkify.addLinks(holder.tvUserMessage, Linkify.WEB_URLS);
            }else if (messageModelList.get(holder.getAdapterPosition()).getImage()!=null){
                holder.usersImageView.setVisibility(View.VISIBLE);
                holder.tvUserMessage.setVisibility(View.GONE);
                holder.btnPlayUsers.setVisibility(View.GONE);
                holder.audioPlayerUser.setVisibility(View.GONE);
                Glide.with(context).load(messageModelList.get(holder.getAdapterPosition()).getImage()).placeholder(R.drawable.default_image).override(150,150).into(holder.usersImageView);
                holder.usersImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, OpenAttachmentActivity.class);
                        intent.putExtra("url", messageModelList.get(holder.getAdapterPosition()).getImage());
                        context.startActivity(intent);
                    }
                });
            }else if (messageModelList.get(holder.getAdapterPosition()).getVideo()!=null){
                holder.usersImageView.setVisibility(View.VISIBLE);
                holder.tvUserMessage.setVisibility(View.GONE);
                holder.audioPlayerUser.setVisibility(View.GONE);
                holder.btnPlayUsers.setVisibility(View.VISIBLE);
                Glide.with(context).load(messageModelList.get(holder.getAdapterPosition()).getVideo()).override(150,150).placeholder(R.drawable.default_image).into(holder.usersImageView);
                holder.btnPlayUsers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(context, OpenAttachmentActivity.class);
                        intent.putExtra("isVideo", true);
                        intent.putExtra("url", messageModelList.get(holder.getAdapterPosition()).getVideo());
                        context.startActivity(intent);
                    }
                });

            }else if (messageModelList.get(holder.getAdapterPosition()).getDocument()!=null){
                holder.linear_documents_other.setVisibility(View.VISIBLE);
                holder.btnPlayUsers.setVisibility(View.GONE);
                holder.usersImageView.setVisibility(View.GONE);
                holder.tvUserMessage.setVisibility(View.GONE);
                holder.audioPlayerUser.setVisibility(View.GONE);
                holder.linear_documents_other.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
//                        downloadPdfFromUrl(messageModelList.get(position).getDocument());
                        Intent intent = new Intent(context, LoadPdfActivity.class);
                        intent.putExtra("url", messageModelList.get(holder.getAdapterPosition()).getDocument());
                        context.startActivity(intent);
                    }
                });
            }else if (messageModelList.get(holder.getAdapterPosition()).getAudio()!=null){
                holder.linear_documents_other.setVisibility(View.GONE);
                holder.btnPlayUsers.setVisibility(View.GONE);
                holder.usersImageView.setVisibility(View.GONE);
                holder.tvUserMessage.setVisibility(View.GONE);
                holder.audioPlayerUser.setVisibility(View.VISIBLE);
                holder.audioPlayerUser.setAudioTarget(messageModelList.get(holder.getAdapterPosition()).getAudio());

            }else if (messageModelList.get(holder.getAdapterPosition()).getLatitude()>0&&messageModelList.get(holder.getAdapterPosition()).getLongitude()>0){
                holder.usersImageView.setVisibility(View.VISIBLE);
                holder.tvUserMessage.setVisibility(View.GONE);
                holder.btnPlayUsers.setVisibility(View.GONE);
                Glide.with(context).load(context.getResources().getDrawable(R.drawable.google_maps)).
                        placeholder(R.drawable.default_image).override(150,150).into(holder.usersImageView);
                holder.usersImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String uri = "http://maps.google.com/maps?q=loc:" + messageModelList.get(holder.getAdapterPosition()).getLatitude()
                                + "," + messageModelList.get(holder.getAdapterPosition()).getLongitude();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        intent.setPackage("com.google.android.apps.maps");
                        context.startActivity(intent);
                    }
                });
            } else {
                holder.tvUserMessage.setVisibility(View.VISIBLE);
                holder.usersImageView.setVisibility(View.GONE);
                holder.btnPlayUsers.setVisibility(View.GONE);
                holder.audioPlayerUser.setVisibility(View.GONE);
            }

        }

        if (holder.tvMyName!=null){
            holder.tvMyName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showUsersData(messageModelList.get(holder.getAdapterPosition()).getUid());
                }
            });
        }
        if (holder.tvUserName!=null){
            holder.tvUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showUsersData(messageModelList.get(holder.getAdapterPosition()).getUid());
                }
            });
        }

    }

    public void stopPlayers(){
        for (int i = 0; i<mpList.size(); i++){
            if (mpList.get(i)!=null){
                mpList.get(i).stop();
                mpList.get(i).release();
            }
        }
    }

    private void showUsersData(String uid){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.users_info_dialog, null);
        CircleImageView profilePic = view.findViewById(R.id.profilePic);
        TextView tvFirstName = view.findViewById(R.id.etFirstName);
        TextView tvWorksAt = view.findViewById(R.id.tvWorksAt);
        TextView tvWorkTitle = view.findViewById(R.id.tvWorkTitle);
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
                UserModel userModel = EUGroupChat.userModelList.get(i);
                profilePic.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (userModel.getProfileUrl()!=null){
                            Intent intent = new Intent(context, OpenAttachmentActivity.class);
                            intent.putExtra("url", userModel.getProfileUrl());
                            intent.putExtra("profile_pic", true);
                            context.startActivity(intent);
                        }

                    }
                });

                if (EUGroupChat.userModelList.get(i).isAdmin()){

                    Glide.with(context).load(R.drawable.project_consult).fitCenter().into(profilePic);
                    tvFirstName.setText("Admin");
                    String text = "A <b><i>Rocca</i></b> association";
                    tvAbout.setText(Html.fromHtml(text));

                }else {
                    if (EUGroupChat.userModelList.get(i).getProfileUrl()!=null){
                        Glide.with(context).load(EUGroupChat.userModelList.get(i).getProfileUrl()).fitCenter().placeholder(R.drawable.default_image).into(profilePic);
                    }
                    tvFirstName.setText(EUGroupChat.userModelList.get(i).getFirstName()+" "+EUGroupChat.userModelList.get(i).getSurName());
                    if (EUGroupChat.userModelList.get(i).getAbout()!=null){
                        tvAbout.setText(EUGroupChat.userModelList.get(i).getAbout());
                    }
                }
                if (EUGroupChat.userModelList.get(i).getPhone()!=null){
                    tvPhone.setText(EUGroupChat.userModelList.get(i).getPhone());
                }else {
                    tvPhone.setText("---");
                }

                if (EUGroupChat.userModelList.get(i).getEmail()!=null){
                    tvEmail.setText(EUGroupChat.userModelList.get(i).getEmail());
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
            }
        }

        if (EUGroupChat.currentUser.isAdmin()){
            List<String> days = new ArrayList<>();
            days.add("Mon");
            days.add("Tue");
            days.add("Wed");
            days.add("Thu");
            days.add("Fri");
            tvMorningStart.setText("9:00");
            tvMorningTo.setText("13:00");
            tvNoonStart.setText("15:30");
            tvNoonTo.setText("18:30");
            StringHorizontalAdapter adapter = new StringHorizontalAdapter(days,
                    context);
            recyclerWorkingDays.setAdapter(adapter);
            linearWork.setVisibility(View.GONE);
            tvWorkTitle.setVisibility(View.GONE);

        }else {
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
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
                        AlertDialog alertDialog = builder1.create();
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
        }


        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    public int getItemCount() {
        return messageModelList.size();
    }

    public class MHolder extends RecyclerView.ViewHolder{

        LinearLayout linearUser, linearMe;
        HyperlinkTextView tvUserMessage, tvMyMessage;
        TextView tvMyName, tvUserName;
        TextView tvUserTime, tvMyTime, tvMyReplyUserName, tvOtherReplyUserName, tvMyReplyMessageType, tvOtherReplyMessageType;
        CircleImageView profilePic;
        LinearLayout linear_documents_Me, linear_documents_other;
        ImageView usersImageView, myImageView;
        ImageView btnPlayMe, btnPlayUsers, myReplyImage, otherReplyImage;
        CardView cardMyReply, cardOtherReply;
        AudioSenseiPlayerView audioPlayerMy, audioPlayerUser;

        public MHolder(@NonNull View itemView) {
            super(itemView);
            linearUser = itemView.findViewById(R.id.linear_user);
            cardOtherReply = itemView.findViewById(R.id.card_other_reply);
            cardMyReply = itemView.findViewById(R.id.card_my_reply);
            myReplyImage = itemView.findViewById(R.id.myReplyImage);
            otherReplyImage = itemView.findViewById(R.id.otherReplyImage);
            tvMyReplyUserName = itemView.findViewById(R.id.tvMyReplyUserName);
            tvOtherReplyUserName = itemView.findViewById(R.id.tvOtherReplyUserName);
            tvMyReplyMessageType = itemView.findViewById(R.id.tvMyReplyMessageType);
            tvOtherReplyMessageType = itemView.findViewById(R.id.tvOtherReplyMessageType);
            linearMe = itemView.findViewById(R.id.linear_me);
            tvUserMessage = itemView.findViewById(R.id.users_message);
            tvUserName = itemView.findViewById(R.id.username);
            tvMyName = itemView.findViewById(R.id.myname);
            tvMyMessage = itemView.findViewById(R.id.my_message);
            tvUserTime = itemView.findViewById(R.id.users_time);
            linear_documents_Me = itemView.findViewById(R.id.linear_documents_me);
            linear_documents_other = itemView.findViewById(R.id.linear_documents_others);
            tvMyTime = itemView.findViewById(R.id.my_time);
            profilePic = itemView.findViewById(R.id.profile_pic);
            usersImageView = itemView.findViewById(R.id.users_imageView);
            myImageView = itemView.findViewById(R.id.my_imageView);
            btnPlayMe = itemView.findViewById(R.id.btnPlayMy);
            btnPlayUsers = itemView.findViewById(R.id.btnPlayUser);
            audioPlayerMy = itemView.findViewById(R.id.audioPlayerMy);
            audioPlayerUser = itemView.findViewById(R.id.audioPlayerUser);

        }
    }

}
