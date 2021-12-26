package com.fahadandroid.groupchat.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.fahadandroid.groupchat.R;
import com.fahadandroid.groupchat.models.UserModel;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MentionUserAdapter extends RecyclerView.Adapter<MentionUserAdapter.MHolder> {

    List<UserModel> userModelList;
    Context context;

    public MentionUserAdapter(List<UserModel> userModelList, Context context) {
        this.userModelList = userModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public MHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.users_to_mention_item, parent, false);
        return new MHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MHolder holder, int position) {
        if (userModelList.get(position).getProfileUrl()!=null){
            Glide.with(context).load(userModelList.get(position).getProfileUrl()).placeholder(R.drawable.default_image).fitCenter().into(holder.profilePic);
        }
        holder.tvUserName.setText(userModelList.get(position).getFirstName()+" "+userModelList.get(position).getSurName());
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class MHolder extends RecyclerView.ViewHolder {

        CircleImageView profilePic;
        TextView tvUserName;

        public MHolder(@NonNull View itemView) {
            super(itemView);
            profilePic = itemView.findViewById(R.id.profile_pic);
            tvUserName = itemView.findViewById(R.id.tvUserName);
        }
    }
}
