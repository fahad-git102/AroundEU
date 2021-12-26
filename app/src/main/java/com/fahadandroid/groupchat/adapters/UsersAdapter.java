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

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UsersHolder> {

    List<UserModel> userModelList;
    Context context;

    public UsersAdapter(List<UserModel> userModelList, Context context) {
        this.userModelList = userModelList;
        this.context = context;
    }

    @NonNull
    @Override
    public UsersHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new UsersHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.users_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull UsersHolder holder, int position) {
        holder.tvUsername.setText(userModelList.get(position).getFirstName()+ " "+ userModelList.get(position).getSurName());
        holder.tvcountry.setText(userModelList.get(position).getCountry());
        Glide.with(context).load(userModelList.get(position).getProfileUrl()).placeholder(R.drawable.default_image).fitCenter().into(holder.profilePic);
    }

    @Override
    public int getItemCount() {
        return userModelList.size();
    }

    public class UsersHolder extends RecyclerView.ViewHolder{

        CircleImageView profilePic;
        TextView tvUsername, tvcountry;

        public UsersHolder(@NonNull View itemView) {
            super(itemView);
            tvUsername = itemView.findViewById(R.id.tv_user_name);
            tvcountry = itemView.findViewById(R.id.tv_country);
            profilePic = itemView.findViewById(R.id.imageView);
        }
    }
}
