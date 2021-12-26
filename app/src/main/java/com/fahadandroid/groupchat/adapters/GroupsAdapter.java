package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.GroupsModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.GHolder> {

    List<GroupsModel> groupsModelList;
    Context context;
    boolean showButton ;
    DatabaseReference groupsRef, usersRef;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth mAuth;

    public GroupsAdapter(List<GroupsModel> groupsModelList, Context context, boolean showButton) {
        this.groupsModelList = groupsModelList;
        this.context = context;
        this.showButton = showButton;
    }

    @NonNull
    @Override
    public GHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.groups_item_layout, null);
        return new GHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GHolder holder, int position) {
        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        groupsRef = firebaseDatabase.getReference("groups");
        usersRef = firebaseDatabase.getReference("Users");
        holder.tvGroupName.setText(groupsModelList.get(position).getName());
        holder.tvLastMessage.setText("Last Message");
        try {
            String firstletter = String.valueOf(groupsModelList.get(position).getName().charAt(0));
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color = generator.getColor(position);
            TextDrawable drawable = TextDrawable.builder()
                    .buildRound(firstletter, color);
            holder.imageView.setImageDrawable(drawable);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        if (showButton){

            groupsRef.child(groupsModelList.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    try{
                        GroupsModel groupsModel = snapshot.getValue(GroupsModel.class);
                        boolean alreadyApplied = false;
                        boolean alreadyJoined = false;
                        if (groupsModel.getKey()==null){
                            groupsModel.setKey(snapshot.getKey());
                        }
                        List<String> pendingMembers = new ArrayList<>();
                        List<String> approvedMembers = new ArrayList<>();
                        if (groupsModel.getPendingMembers()!=null){
                            pendingMembers = groupsModel.getPendingMembers();
                            if (pendingMembers.contains(mAuth.getCurrentUser().getUid())){
                                alreadyApplied = true;
                            }else {
                                alreadyApplied = false;
                            }
                        }
                        if (groupsModel.getApprovedMembers()!=null){
                            approvedMembers = groupsModel.getApprovedMembers();
                            if (approvedMembers.contains(mAuth.getCurrentUser().getUid())){
                                alreadyJoined = true;
                            }else {
                                alreadyJoined = false;
                            }
                        }
                        if (alreadyApplied&&!alreadyApplied){
                            holder.btnJoin.setVisibility(View.GONE);
                        }else if (alreadyApplied||alreadyJoined){
                            holder.btnJoin.setVisibility(View.GONE);
                        }else {
                            holder.btnJoin.setVisibility(View.VISIBLE);
                        }
                    }catch (Exception e){
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });



            holder.btnJoin.setVisibility(View.VISIBLE);
            holder.btnJoin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    View view1 = LayoutInflater.from(context).inflate(R.layout.pincode_dialog_layout, null);
                    EditText etPincode = view1.findViewById(R.id.etPincode);
                    Button btnJoin = view1.findViewById(R.id.btnJoin);
                    builder.setView(view1);
                    AlertDialog alertDialog = builder.create();
                    btnJoin.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            String pincode = etPincode.getText().toString();
                            if (pincode.isEmpty()){
                                etPincode.setError("Pincode Required");
                                return;
                            }
                            int pin = Integer.parseInt(pincode);
                            if (groupsModelList.get(position).getPincode().equals(pincode)){
                                Toast.makeText(context, "Pin matched", Toast.LENGTH_SHORT).show();
                                groupsRef.child(groupsModelList.get(position).getKey()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        try{
                                            GroupsModel groupsModel = snapshot.getValue(GroupsModel.class);
                                            if (groupsModel.getKey()==null){
                                                groupsModel.setKey(snapshot.getKey());
                                            }
                                            List<String> approvedMembers = new ArrayList<>();
//                                            List<String> pendingMembers = new ArrayList<>();
//                                            if (groupsModel.getPendingMembers()!=null){
//                                                pendingMembers = groupsModel.getPendingMembers();
//                                                if (!pendingMembers.contains(mAuth.getCurrentUser().getUid())){
//                                                    pendingMembers.add(mAuth.getCurrentUser().getUid());
//                                                }
//                                            }else {
//                                                pendingMembers.add(mAuth.getCurrentUser().getUid());
//                                            }
//                                            Map<String, Object> map = new HashMap<>();
//                                            map.put("pendingMembers", pendingMembers);
//                                            groupsRef.child(groupsModelList.get(position).getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<Void> task) {
//                                                    alertDialog.dismiss();
//                                                    Toast.makeText(context, "Request Sent !", Toast.LENGTH_SHORT).show();
//                                                }
//                                            });
                                            if (groupsModel.getApprovedMembers()!=null){
                                                approvedMembers = groupsModel.getApprovedMembers();
                                                if (!approvedMembers.contains(mAuth.getCurrentUser().getUid())){
                                                    approvedMembers.add(mAuth.getCurrentUser().getUid());
                                                }
                                            }else {
                                                approvedMembers.add(mAuth.getCurrentUser().getUid());
                                            }
                                            Map<String, Object> map = new HashMap<>();
                                            map.put("approvedMembers", approvedMembers);
                                            groupsRef.child(groupsModelList.get(position).getKey()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Map<String, Object> map = new HashMap<>();
                                                    map.put("joined", true);
                                                    usersRef.child(mAuth.getCurrentUser().getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            alertDialog.dismiss();
                                                            Toast.makeText(context, "Group Joined !", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });
                                                    alertDialog.dismiss();
                                                    Toast.makeText(context, "Group Joined !", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }catch (Exception e){
                                            alertDialog.dismiss();
                                            Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(context, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }else {
                                Toast.makeText(context, "Wrong Pincode", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    alertDialog.show();
                }
            });
        }else {
            holder.btnJoin.setVisibility(View.GONE);
        }

        if (groupsModelList.get(position).getJoined()!=null){
            if (groupsModelList.get(position).getJoined().equals("pending")){
                holder.pending.setVisibility(View.VISIBLE);
            }else {
                holder.pending.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return groupsModelList.size();
    }

    public class GHolder extends RecyclerView.ViewHolder{

        ImageView imageView, pending;
        TextView tvGroupName, tvLastMessage;
        Button btnJoin;

        public GHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            tvGroupName = itemView.findViewById(R.id.tv_group_name);
            tvLastMessage = itemView.findViewById(R.id.tv_last_message);
            btnJoin = itemView.findViewById(R.id.btnJoin);
            pending = itemView.findViewById(R.id.pending);
        }
    }
}
